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


public class MisionesTotales implements IRichBolt{
	
	private static final long serialVersionUID = 6101916216609388178L;
	private static final Logger logger = LoggerFactory.getLogger(ParseLog.class);
	private OutputCollector outputCollector;
	private Map mapConf;
	
	private Timer emitTask;
	private long timeDelay;
	private long emitTimeframe;
	
	private AtomicLong misionesCreadas;
	private AtomicLong misionesIniciadas;
	private AtomicLong misionesFinalizadas;
	
	private Long timestampCurrent;
	
	
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
		if(log.getText().equals("\\sys_enviar_mision")){
			this.misionesCreadas.getAndIncrement();
		}else if(log.getText().equals("\\sys_iniciar_mision")){
			this.misionesIniciadas.getAndIncrement();
		}else if(log.getText().equals("\\sys_terminar_mision")){
			this.misionesFinalizadas.getAndIncrement();
		}
	}

	@Override
	public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
		logger.info("Prepare ParseLog");
		this.mapConf = mapConf;
		this.outputCollector = outputCollector;
		this.misionesCreadas = new AtomicLong(0);
		this.timestampCurrent = new Date().getTime();
		this.misionesFinalizadas = new AtomicLong(0);
		this.misionesIniciadas = new AtomicLong(0);
		
		this.emitTask = new Timer();
		this.emitTask.scheduleAtFixedRate(new EmitTask(this.outputCollector), timeDelay * 1000, emitTimeframe * 1000);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("log"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return mapConf;
	}
	
	private class EmitTask extends TimerTask {
		private final OutputCollector outputCollector;

		private long previousCreadas;
		private long previousIniciadas;
		private long previousFinalizadas;
		private long CreadasRate;
		private long IniciadasRate;
		private long FinalizadasRate;

		public EmitTask(OutputCollector outputCollector) {
			this.outputCollector = outputCollector;
			this.previousCreadas = 0;
			this.previousIniciadas = 0;
			this.previousFinalizadas = 0;
			this.CreadasRate = 0;
			this.IniciadasRate = 0;
			this.FinalizadasRate = 0;
		}

		/**
		 * Ejecución periódica cada cierta ventana de tiempo, la cual emitirá
		 * los datos
		 */
		@Override
		public void run() {
			long CreadasSnapshot = misionesCreadas.get();
			long IniciadasSnapshot = misionesIniciadas.get();
			long FinalizadasSnapshot = misionesFinalizadas.get();
			
			this.CreadasRate = CreadasSnapshot - this.previousCreadas;
			this.IniciadasRate = IniciadasSnapshot - this.previousIniciadas;
			this.FinalizadasRate = FinalizadasSnapshot - this.previousFinalizadas;
			
			this.previousCreadas = CreadasSnapshot;
			this.previousIniciadas = IniciadasSnapshot;
			this.previousFinalizadas = FinalizadasSnapshot;
			
			System.out.println("Misiones creadas: "+this.CreadasRate);
			System.out.println("Misiones iniciadas: "+this.IniciadasRate);
			System.out.println("Misiones finalizadas: "+this.FinalizadasRate);
			/*
			Count creadas = new Count("misionesCreadasRate", ParseDate.parse(timestampCurrent), this.CreadasRate);
			Count iniciadas = new Count("misionesIniciadasRate", ParseDate.parse(timestampCurrent), this.IniciadasRate);
			Count finalizadas = new Count("misionesFinalizadasCount", ParseDate.parse(timestampCurrent), this.FinalizadasRate);
			this.outputCollector.emit(creadas.factoryCount());
			this.outputCollector.emit(iniciadas.factoryCount());
			this.outputCollector.emit(finalizadas.factoryCount());
			*/
		}

	}


}
