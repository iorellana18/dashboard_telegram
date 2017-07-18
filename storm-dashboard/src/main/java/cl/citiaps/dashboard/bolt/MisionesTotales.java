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

/*****
 * Bolt que muestra datos cuantitativos del estado las misiones Datos que envía:
 * * Cantidad de misiones creadas * Cantidad de misiones iniciadas * Cantidad de
 * misiones finalizadas
 ******/

public class MisionesTotales implements IRichBolt {

	private static final long serialVersionUID = 6101916216609388178L;
	private static final Logger logger = LoggerFactory.getLogger(ParseLog.class);
	private OutputCollector outputCollector;
	private Map mapConf;

	private Timer emitTask;
	private long timeDelay;
	private long emitTimeframe;

	private AtomicLong misionesCreadas;
	private AtomicLong misionesFinalizadas;

	private Long timeStampCurrent;

	public MisionesTotales(long timeDelay, long emitTimeframe) {
		this.timeDelay = timeDelay;
		this.emitTimeframe = emitTimeframe;
	}

	@Override
	public void cleanup() {
		logger.info("CleanUp ParseLog");
		System.runFinalization();
		System.gc();
	}

	@Override
	public void execute(Tuple tuple) {
		Log log = (Log) tuple.getValueByField("log");
		if (log.getText().equals("/sys_enviar_mision")) {
			this.misionesCreadas.getAndIncrement();
			timeStampCurrent = log.getTimeStamp();
		} else if (log.getText().equals("/sys_terminar_mision")) {
			this.misionesFinalizadas.getAndIncrement();
			timeStampCurrent = log.getTimeStamp();
		}
	}

	@Override
	public void prepare(Map mapConf, TopologyContext topologyContext, OutputCollector outputCollector) {
		logger.info("Prepare ParseLog");
		this.mapConf = mapConf;
		this.outputCollector = outputCollector;

		this.misionesCreadas = new AtomicLong(0);
		this.misionesFinalizadas = new AtomicLong(0);
		this.timeStampCurrent = new Date().getTime();

		this.emitTask = new Timer();
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

		private long previousCreadas;
		private long previousFinalizadas;
		private long creadasRate;
		private long finalizadasRate;

		public EmitTask(OutputCollector outputCollector) {
			this.outputCollector = outputCollector;
			this.previousCreadas = 0;
			this.previousFinalizadas = 0;
			this.creadasRate = 0;
			this.finalizadasRate = 0;
		}

		/**
		 * Ejecución periódica cada cierta ventana de tiempo, la cual emitirá
		 * los datos
		 */
		@Override
		public void run() {
			long creadasSnapshot = misionesCreadas.get();
			long finalizadasSnapshot = misionesFinalizadas.get();

			this.creadasRate = creadasSnapshot - this.previousCreadas;
			this.finalizadasRate = finalizadasSnapshot - this.previousFinalizadas;

			this.previousCreadas = creadasSnapshot;
			this.previousFinalizadas = finalizadasSnapshot;

			logger.info("Misiones creadas: {}", this.creadasRate);
			logger.info("Misiones finalizadas: {}", this.finalizadasRate);

			Count creadas = new Count("misionesCreadasRate", ParseDate.parse(timeStampCurrent), this.creadasRate);
			Count finalizadas = new Count("misionesFinalizadasRate", ParseDate.parse(timeStampCurrent),
					this.finalizadasRate);

			this.outputCollector.emit(creadas.factoryCount());
			this.outputCollector.emit(finalizadas.factoryCount());

		}

	}

}
