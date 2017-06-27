package cl.citiaps.dashboard.bolt;

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

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

public class MisionesEstado implements IRichBolt {

	private static final long serialVersionUID = 7784329420249780555L;

	private static Logger logger = LoggerFactory.getLogger(MisionesEstado.class);

	private OutputCollector outputCollector;
	private Map mapConf;

	private Timer emitTask;
	private long timeDelay;
	private long emitTimeframe;

	private Long countInit;
	private AtomicLong rateMisionInit;

	private Long countFinish;
	private AtomicLong rateMisionFinish;

	private Long timestampCurrent;

	public MisionesEstado(long timeDelay, long emitTimeframe) {
		this.timeDelay = timeDelay;
		this.emitTimeframe = emitTimeframe;
	}

	@Override
	public void prepare(Map mapConf, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.mapConf = mapConf;
		this.outputCollector = outputCollector;

		this.countInit = Long.valueOf(0);
		this.rateMisionInit = new AtomicLong(0);

		this.countFinish = Long.valueOf(0);
		this.rateMisionFinish = new AtomicLong(0);

		this.timestampCurrent = new Date().getTime();

		this.emitTask = new Timer();
		this.emitTask.scheduleAtFixedRate(new EmitTask(this.outputCollector), timeDelay * 1000, emitTimeframe * 1000);
	}

	@Override
	public void execute(Tuple tuple) {
		Log log = (Log) tuple.getValueByField("log");

		if (log.getAccion().equals("INIT_MISSION") && log.getTipoUsuario().equals("COORDINATOR")) {
			rateMisionInit.getAndIncrement();
			timestampCurrent = log.getTimestamp();
		} else if (log.getAccion().equals("FINISH_MISSION") && log.getTipoUsuario().equals("COORDINATOR")) {
			rateMisionInit.getAndDecrement();
			rateMisionFinish.getAndIncrement();
			timestampCurrent = log.getTimestamp();
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
		outputFieldsDeclarer.declare(new Fields("count"));
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
		private final OutputCollector outputCollector;

		private long previousSnapshotInit;
		private long previousSnapshotFinish;

		private long rateInit;
		private long rateFinish;

		public EmitTask(OutputCollector outputCollector) {
			this.outputCollector = outputCollector;

			this.previousSnapshotInit = 0;
			this.previousSnapshotFinish = 0;

			this.rateInit = 0;
			this.rateFinish = 0;
		}

		/**
		 * Ejecución periódica cada cierta ventana de tiempo, la cual emitirá
		 * los datos
		 */
		@Override
		public void run() {
			/**
			 * Cantidad de misiones inicializadas
			 */
			long snapshotInit = rateMisionInit.get();
			long snapshotFinish = rateMisionFinish.get();

			this.rateInit = snapshotInit - this.previousSnapshotInit;
			this.previousSnapshotInit = snapshotInit;

			this.rateFinish = snapshotFinish - this.previousSnapshotFinish;
			this.previousSnapshotFinish = snapshotFinish;

			countInit = snapshotInit;

			Count acumInit = new Count("misionesIniciadasCount", ParseDate.parse(timestampCurrent), countInit);
			Count rateInit = new Count("misionesIniciadasRate", ParseDate.parse(timestampCurrent), this.rateInit);
			this.outputCollector.emit(acumInit.factoryCount());
			this.outputCollector.emit(rateInit.factoryCount());

			/**
			 * Cantidad de misiones finalizadas
			 */

			countFinish = snapshotFinish;

			Count acumFinish = new Count("misionesTerminadasCount", ParseDate.parse(timestampCurrent), countFinish);
			Count rateFinish = new Count("misionesTerminadasRate", ParseDate.parse(timestampCurrent), this.rateFinish);
			this.outputCollector.emit(acumFinish.factoryCount());
			this.outputCollector.emit(rateFinish.factoryCount());
		}

	}

}
