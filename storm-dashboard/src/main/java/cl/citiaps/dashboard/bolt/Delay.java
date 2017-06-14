package cl.citiaps.dashboard.bolt;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

public class Delay implements IRichBolt {

	private static final long serialVersionUID = 7784329420249780555L;

	private static Logger logger = LoggerFactory.getLogger(Delay.class);

	private OutputCollector outputCollector;
	private Map mapConf;

	private Timer emitTask;
	private long timeDelay;
	private long emitTimeframe;

	private Long init = new Long("1397328001");

	private Map<Long, List<Log>> timestamp;

	public Delay(long timeDelay, long emitTimeframe) {
		this.timeDelay = timeDelay;
		this.emitTimeframe = emitTimeframe;
	}

	@Override
	public void prepare(Map mapConf, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.mapConf = mapConf;
		this.outputCollector = outputCollector;

		this.timestamp = new HashMap<Long, List<Log>>();

		this.emitTask = new Timer();
		this.emitTask.scheduleAtFixedRate(new EmitTask(this.timestamp, this.outputCollector), timeDelay, emitTimeframe);
	}

	@Override
	public void execute(Tuple tuple) {
		Log log = (Log) tuple.getValueByField("log");

		if (this.timestamp.containsKey(log.getTimestamp())) {
			this.timestamp.get(log.getTimestamp()).add(log);
		} else {
			List<Log> logs = new ArrayList<Log>();
			logs.add(log);
			this.timestamp.put(log.getTimestamp(), logs);
		}
	}

	/**
	 * Método que se realiza cuando se cierra el Bolt
	 */
	@Override
	public void cleanup() {
		logger.info("Close " + this.getClass().getSimpleName());

//		this.emitTask.shutdown();
	}

	/**
	 * Método que declara los campos que posee la tupla enviada por este bolt
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("log"));
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
		private final Map<Long, List<Log>> timestamp;
		private final OutputCollector outputCollector;

		public EmitTask(Map<Long, List<Log>> timestamp, OutputCollector outputCollector) {
			this.timestamp = timestamp;
			this.outputCollector = outputCollector;
		}

		/**
		 * Ejecución periódica cada cierta ventana de tiempo, la cual emitirá
		 * los datos
		 */
		@Override
		public void run() {

			/*
			 * Crear un snapshot del contador, para posteriormente enviar las
			 * estadísticas
			 */
			Map<Long, List<Log>> snapshotTimestamp;
			synchronized (this.timestamp) {
				snapshotTimestamp = new HashMap<Long, List<Log>>(this.timestamp);
			}

			if (snapshotTimestamp.containsKey(init)) {
				List<Log> logsCurrent;
				synchronized (snapshotTimestamp.get(init)) {
					logsCurrent = snapshotTimestamp.get(init);
				}

				for (Log log : logsCurrent) {
					// if (log.getAccion().equals("INIT_MISSION")) {
					// logger.info("INIT_MISSION");
					// }
					this.outputCollector.emit(new Values(log));
				}

				init += 1;
			} else {
				init += 1;
			}

		}

	}

}
