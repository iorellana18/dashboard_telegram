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

import cl.citiaps.dashboard.eda.Count;
import cl.citiaps.dashboard.eda.Log;
import cl.citiaps.dashboard.utils.ParseDate;

public class InicializarMision implements IRichBolt {

	private static final long serialVersionUID = 7784329420249780555L;
	private static Logger logger = LoggerFactory.getLogger(MisionesEspera.class);
	private OutputCollector outputCollector;
	private Map mapConf;

	private long timeDelay;
	private long emitTimeframe;
	private Timer emitTask;

	/**
	 * Ídem problema de la clase ResponderMision
	 */
	private Map<String, Long> timeMisions;
	private SummaryStatistics stats;
	private Long timestampCurrent;

	public InicializarMision(long timeDelay, long emitTimeframe) {
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
		if (log.getAccion().equals("CREATE_MISSION")) {
			this.timestampCurrent = log.getTimestamp();
			this.timeMisions.put(log.getMision(), log.getTimestamp());
		}

		if (log.getAccion().equals("INIT_MISSION")) {
			this.timestampCurrent = log.getTimestamp();

			long timeMision = this.timeMisions.get(log.getMision());
			// if (timeMision != null) {
			this.stats.addValue(log.getTimestamp() - this.timeMisions.get(timeMision));
			this.timeMisions.remove(log.getMision());
			// } else {
			// logger.error("El archivo Log posee un error...");
			// }
		}

	}

	@Override
	public void prepare(Map mapConf, TopologyContext arg1, OutputCollector outputCollector) {
		this.mapConf = mapConf;
		this.outputCollector = outputCollector;

		this.emitTask = new Timer();
		this.emitTask.scheduleAtFixedRate(new EmitTask(this.outputCollector), timeDelay * 1000, emitTimeframe * 1000);

		this.timeMisions = new HashMap<String, Long>();
		this.timestampCurrent = Long.valueOf(1397328001);
		this.stats = new SummaryStatistics();
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
			Count promedio = new Count("avgTimeIniciarMision", ParseDate.parse(timestampCurrent),
					(long) stats.getMean());
			Count max = new Count("maxTimeIniciarMision", ParseDate.parse(timestampCurrent), (long) stats.getMax());
			Count min = new Count("minTimeIniciarMision", ParseDate.parse(timestampCurrent), (long) stats.getMin());
			Count desviacion = new Count("devTimeIniciarMision", ParseDate.parse(timestampCurrent),
					(long) stats.getStandardDeviation());

			this.outputCollector.emit(promedio.factoryCount());
			this.outputCollector.emit(max.factoryCount());
			this.outputCollector.emit(min.factoryCount());
			this.outputCollector.emit(desviacion.factoryCount());
		}

	}
}
