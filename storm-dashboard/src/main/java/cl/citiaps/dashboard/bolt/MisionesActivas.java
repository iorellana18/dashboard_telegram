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

public class MisionesActivas implements IRichBolt {

	private static final long serialVersionUID = 7784329420249780555L;

	private static Logger logger = LoggerFactory.getLogger(MisionesActivas.class);

	private OutputCollector outputCollector;
	private Map mapConf;

	private Timer emitTask;
	private long timeDelay;
	private long emitTimeframe;
	private Long count;
	private Long rateMision;
	private Long timestampCurrent;

	public MisionesActivas(long timeDelay, long emitTimeframe) {
		this.timeDelay = timeDelay;
		this.emitTimeframe = emitTimeframe;
	}

	@Override
	public void prepare(Map mapConf, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.mapConf = mapConf;
		this.outputCollector = outputCollector;

		this.count = Long.valueOf(0);

		this.rateMision = Long.valueOf(0);
		this.timestampCurrent = Long.valueOf(1397328001);

		this.emitTask = new Timer();
		this.emitTask.scheduleAtFixedRate(new EmitTask(this.outputCollector), timeDelay * 1000, emitTimeframe * 1000);
	}

	@Override
	public void execute(Tuple tuple) {
		Log log = (Log) tuple.getValueByField("log");

		if (log.getAccion().equals("CREATE_MISSION")) {
			rateMision++;
			timestampCurrent = log.getTimestamp();
		} else if (log.getAccion().equals("FINISH_MISSION") && log.getTipoUsuario().equals("COORDINATOR")) {
			rateMision--;
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

		public EmitTask(OutputCollector outputCollector) {
			this.outputCollector = outputCollector;
		}

		/**
		 * Ejecución periódica cada cierta ventana de tiempo, la cual emitirá
		 * los datos
		 */
		@Override
		public void run() {

			count += rateMision;

			Count acum = new Count("misionesCount", ParseDate.parse(timestampCurrent), count);
			Count rate = new Count("misionesRate", ParseDate.parse(timestampCurrent), rateMision);
			this.outputCollector.emit(acum.factoryCount());
			this.outputCollector.emit(rate.factoryCount());

			rateMision = Long.valueOf(0);

		}

	}

}
