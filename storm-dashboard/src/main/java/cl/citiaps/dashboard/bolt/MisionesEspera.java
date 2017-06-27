package cl.citiaps.dashboard.bolt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

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
import cl.citiaps.dashboard.utils.ParseDate;

public class MisionesEspera implements IRichBolt {

	private static final long serialVersionUID = 7784329420249780555L;

	private static Logger logger = LoggerFactory.getLogger(MisionesEspera.class);

	private OutputCollector outputCollector;
	private Map mapConf;

	private Timer emitTask;
	private long timeDelay;
	private long emitTimeframe;

	private AtomicLong rateMision;
	private Long total;
	private Long timestampCurrent;

	public MisionesEspera(long timeDelay, long emitTimeframe) {
		this.timeDelay = timeDelay;
		this.emitTimeframe = emitTimeframe;
	}

	@Override
	public void prepare(Map mapConf, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.mapConf = mapConf;
		this.outputCollector = outputCollector;

		this.total = Long.valueOf(0);

		this.rateMision = new AtomicLong(0);
		this.timestampCurrent = new Date().getTime();

		this.emitTask = new Timer();
		this.emitTask.scheduleAtFixedRate(new EmitTask(this.outputCollector), timeDelay * 1000, emitTimeframe * 1000);
	}

	@Override
	public void execute(Tuple tuple) {
		Log log = (Log) tuple.getValueByField("log");

		if (log.getAccion().equals("CREATE_MISSION")) {
			total++;
			this.rateMision.getAndIncrement();
			timestampCurrent = log.getTimestamp();
		} else if (log.getAccion().equals("INIT_MISSION") && log.getTipoUsuario().equals("COORDINATOR")) {
			this.rateMision.getAndDecrement();
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

		private long previousSnapshot;
		private long rate;

		public EmitTask(OutputCollector outputCollector) {
			this.outputCollector = outputCollector;
			this.previousSnapshot = 0;
			this.rate = 0;
		}

		/**
		 * Ejecución periódica cada cierta ventana de tiempo, la cual emitirá
		 * los datos
		 */
		@Override
		public void run() {
			long snapshot = rateMision.get();
			this.rate = snapshot - this.previousSnapshot;
			this.previousSnapshot = snapshot;

			long count = snapshot;
			if (this.rate < 0) {
				this.rate = Long.valueOf(0);
			}

			Count acum = new Count("misionesEsperaCount", ParseDate.parse(timestampCurrent), count);
			Count rate = new Count("misionesEsperaRate", ParseDate.parse(timestampCurrent), this.rate);
			Count sum = new Count("misionesTotalesCount", ParseDate.parse(timestampCurrent), total);
			this.outputCollector.emit(acum.factoryCount());
			this.outputCollector.emit(rate.factoryCount());
			this.outputCollector.emit(sum.factoryCount());

			logger.info("{}", timestampCurrent);
		}

	}

}
