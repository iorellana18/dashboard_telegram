package cl.citiaps.dashboard.bolt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.citiaps.dashboard.eda.Log;

/*****
 * Bolt que muestra datos sobre misiones finalizadas ("FINISH_MISSION")
 * Datos que envía:
 * * Cantidad de misiones finalizadas
 * * Tiempo promedio de finalización de las misiones (desde que inician INIT_MISSION)
 * * Tiempo máximo de finalización
 * * Tiempo mńimo de finalización
******/

public class CuentaFinalizados implements IRichBolt {

	private static final long serialVersionUID = 7784329420249780555L;

	private static Logger logger = LoggerFactory.getLogger(VoluntariosRechazan.class);

	private OutputCollector outputCollector;
	private Map mapConf;
	private Timer emitTask;
	private long timeDelay;
	private long emitTimeframe;
	private Long timestampCurrent;
	private Map<String, Long> timeMisions;
	private SummaryStatistics stats;
	private ArrayList<String> idMisiones;
	private Map<String, Long> misiones;

	public CuentaFinalizados(long timeDelay, long emitTimeframe) {
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
		this.timestampCurrent = log.getTimestamp();
		if (log.getAccion().equals("INIT_MISSION")) {
			// System.out.println("Mision " + log.getMision() + " iniciada");
			misiones.put(log.getMision(), log.getTimestamp());
		} else if (log.getAccion().equals("FINISH_MISSION") && log.getTipoUsuario().equals("VOLUNTEER")) {
			// System.out.println("Mision " + log.getMision() + " finalizada");
			long tiempoInicio = misiones.get(log.getMision());
			long tiempoFin = log.getTimestamp();
			stats.addValue(tiempoFin - tiempoInicio);
		}

	}

	@Override
	public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
		this.mapConf = mapConf;
		this.outputCollector = outputCollector;
		this.emitTask = new Timer();
		this.stats = new SummaryStatistics();
		this.idMisiones = new ArrayList<String>();
		misiones = new HashMap<String, Long>();
		this.emitTask.scheduleAtFixedRate(new EmitTask(this.outputCollector), timeDelay * 1000, emitTimeframe * 1000);

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("count"));

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return mapConf;
	}

	private class EmitTask extends TimerTask {
		private final OutputCollector outputCollector;

		public EmitTask(OutputCollector outputCollector) {
			this.outputCollector = outputCollector;
		}

		/**
		 * Ejecución periódica cada cierta ventana de tiempo, la cual emitirá
		 * los datos
		 */
		@Override
		public void run() {
			System.out.println("Misiones finalizadas : " + stats.getN());
			System.out.println("Promedio en finalizar : " + stats.getMean());
			System.out.println("Máximo : " + stats.getMax());
			System.out.println("Mínimo : " + stats.getMin());

		}

	}

}
