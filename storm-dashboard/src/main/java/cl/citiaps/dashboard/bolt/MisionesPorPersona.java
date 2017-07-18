package cl.citiaps.dashboard.bolt;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cl.citiaps.dashboard.eda.Log;
import cl.citiaps.dashboard.eda.Mision;
import cl.citiaps.dashboard.eda.User;

/*****
 * Bolt que obtiene cantidad de misiones de cada encargado por ventana de tiempo
 * 
 ******/

public class MisionesPorPersona implements IRichBolt {

	private static final long serialVersionUID = 7784329420249780555L;

	private static Logger logger = LoggerFactory.getLogger(MisionesPorPersona.class);

	private OutputCollector outputCollector;
	private Map mapConf;

	private Timer emitTask;
	private long timeDelay;
	private long emitTimeframe;

	private Map<String, Long> countMisiones;

	public MisionesPorPersona(long timeDelay, long emitTimeframe) {
		this.timeDelay = timeDelay;
		this.emitTimeframe = emitTimeframe;
	}

	@Override
	public void cleanup() {
		logger.info("Close " + this.getClass().getSimpleName());

		this.emitTask.cancel();
		this.emitTask.purge();
	}

	@Override
	public void execute(Tuple tuple) {
		Log log = (Log) tuple.getValueByField("log");
		if (log.getText().equals("/sys_enviar_mision")) {
			if (this.countMisiones.containsKey(log.getEncargado())) {
				this.countMisiones.put(log.getEncargado(), (this.countMisiones.get(log.getEncargado()) + 1));
			} else {
				this.countMisiones.put(log.getEncargado(), Long.valueOf(1));
			}
		} else if (log.getText().equals("/sys_terminar_mision")) {
			if (this.countMisiones.containsKey(log.getEncargado())) {
				this.countMisiones.put(log.getEncargado(), (this.countMisiones.get(log.getEncargado()) - 1));
			} else {
				this.countMisiones.put(log.getEncargado(), Long.valueOf(-1));
			}
		}
	}

	@Override
	public void prepare(Map mapConf, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.mapConf = mapConf;
		this.outputCollector = outputCollector;

		this.countMisiones = Collections.synchronizedMap(new HashMap<String, Long>());

		this.emitTask = new Timer();
		this.emitTask.scheduleAtFixedRate(new EmitTask(this.countMisiones, this.outputCollector), timeDelay * 1000,
				emitTimeframe * 1000);

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("usuario"));

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return mapConf;
	}

	private class EmitTask extends TimerTask {
		private final Map<String, Long> countMisiones;
		private final OutputCollector outputCollector;

		public EmitTask(Map<String, Long> countMisiones, OutputCollector outputCollector) {
			this.countMisiones = countMisiones;
			this.outputCollector = outputCollector;
		}

		/**
		 * Ejecución periódica cada cierta ventana de tiempo, la cual emitirá
		 * los datos
		 */
		@Override
		public void run() {

			Map<String, Long> snapshotCountMisiones;
			synchronized (this.countMisiones) {
				snapshotCountMisiones = new HashMap<String, Long>(this.countMisiones);
				this.countMisiones.clear();
			}

			for (String username : snapshotCountMisiones.keySet()) {
				User user = new User(username, snapshotCountMisiones.get(username));
				this.outputCollector.emit(new Values(user));
			}
		}
	}
}
