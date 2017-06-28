package cl.citiaps.dashboard.bolt;

import java.util.Collections;
import java.util.Date;
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

import cl.citiaps.dashboard.eda.Count;
import cl.citiaps.dashboard.eda.Log;
import cl.citiaps.dashboard.eda.Mision;

public class VoluntariosMisiones implements IRichBolt {

	private static final long serialVersionUID = 7784329420249780555L;

	private static Logger logger = LoggerFactory.getLogger(VoluntariosMisiones.class);

	private OutputCollector outputCollector;
	private Map mapConf;

	private Timer emitTask;
	private long timeDelay;
	private long emitTimeframe;

	private Map<String, Long> countVoluntario;
	private Map<String, Mision> classVoluntario;

	public VoluntariosMisiones(long timeDelay, long emitTimeframe) {
		this.timeDelay = timeDelay;
		this.emitTimeframe = emitTimeframe;
	}

	@Override
	public void prepare(Map mapConf, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.mapConf = mapConf;
		this.outputCollector = outputCollector;

		this.countVoluntario = Collections.synchronizedMap(new HashMap<String, Long>());
		this.classVoluntario = new HashMap<String, Mision>();

		this.emitTask = new Timer();
		this.emitTask.scheduleAtFixedRate(
				new EmitTask(this.countVoluntario, this.classVoluntario, this.outputCollector), timeDelay * 1000,
				emitTimeframe * 1000);
	}

	@Override
	public void execute(Tuple tuple) {
		Log log = (Log) tuple.getValueByField("log");
		if (log.getAccion().equals("ACCEPT_MISSION") && log.getTipoUsuario().equals("VOLUNTEER")) {
			if (this.countVoluntario.containsKey(log.getMision())) {
				this.countVoluntario.put(log.getMision(), (this.countVoluntario.get(log.getMision()) + 1));
			} else {
				this.countVoluntario.put(log.getMision(), Long.valueOf(1));
				Mision count = new Mision("voluntariosMisiones", log.getDate(), Long.valueOf(0), log.getMision(),
						log.getLocation(), log.getEmergencia());
				this.classVoluntario.put(log.getMision(), count);
			}
		} else if (log.getAccion().equals("FINISH_MISSION") && log.getTipoUsuario().equals("VOLUNTEER")) {
			this.countVoluntario.put(log.getMision(), (this.countVoluntario.get(log.getMision()) - 1));
		}

	}

	/**
	 * Método que se realiza cuando se cierra el Bolt
	 */
	@Override
	public void cleanup() {
		logger.info("Close " + this.getClass().getSimpleName());

		this.emitTask.cancel();
		this.emitTask.purge();
	}

	/**
	 * Método que declara los campos que posee la tupla enviada por este bolt
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("mision"));
	}

	/**
	 * Get de la configuración de la topología
	 * 
	 * @return configuración
	 */
	@Override
	public Map<String, Object> getComponentConfiguration() {
		return mapConf;
	}

	/**
	 * Clase para poder emitir cada cierto tiempo los eventos que fueron
	 * contados por el bolt
	 */
	private class EmitTask extends TimerTask {
		private final Map<String, Long> countNum;
		private final Map<String, Mision> classVoluntario;
		private final OutputCollector outputCollector;

		public EmitTask(Map<String, Long> countNum, Map<String, Mision> classVoluntario,
				OutputCollector outputCollector) {
			this.countNum = countNum;
			this.classVoluntario = classVoluntario;
			this.outputCollector = outputCollector;
		}

		/**
		 * Ejecución periódica cada cierta ventana de tiempo, la cual emitirá
		 * los datos
		 */
		@Override
		public void run() {

			Map<String, Long> snapshotCountNum;
			synchronized (this.countNum) {
				snapshotCountNum = new HashMap<String, Long>(this.countNum);
				/**
				 * Arreglar
				 */
				this.countNum.clear();
			}

			for (Mision mision : classVoluntario.values()) {
				mision.setCount(snapshotCountNum.get(mision.getMision()));
				this.outputCollector.emit(new Values(mision));
			}

		}

	}

}
