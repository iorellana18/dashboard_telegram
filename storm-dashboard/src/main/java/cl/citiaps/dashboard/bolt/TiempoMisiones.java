package cl.citiaps.dashboard.bolt;

import java.util.ArrayList;
import java.util.Date;
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

/*****
 * Bolt que indica el tiempo de duración de las misiones
 * Datos que envía:
 * * Tiempo promedio de las misiones desde que se crean hasta que finalizan
 * * Tiempo máximo de duración de misiones
 * * Tiempo mínimo de duración de misiones
******/

public class TiempoMisiones implements IRichBolt{

	private static final long serialVersionUID = 7784329420249780555L;

	private static Logger logger = LoggerFactory.getLogger(TiempoMisiones.class);

	private OutputCollector outputCollector;
	private Map mapConf;

	private Timer emitTask;
	private long timeDelay;
	private long emitTimeframe;
	private SummaryStatistics stats;
	private Map<String, Long> misiones;
	private Long timeStampCurrent;
	
	public TiempoMisiones(long timeDelay, long emitTimeframe) {
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
		if(log.getText().equals("\\sys_enviar_mision")){
			misiones.put(log.getMissionId(), log.getTimeStamp());
			timeStampCurrent = log.getTimeStamp();
		}else if(log.getText().equals("\\sys_terminar_mision")){
			long tiempoInicio = misiones.get(log.getMissionId());
			long tiempoFin = log.getTimeStamp();
			stats.addValue(tiempoFin-tiempoInicio);
			timeStampCurrent = log.getTimeStamp();
		}
		
	}

	@Override
	public void prepare(Map mapConf, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.mapConf = mapConf;
		this.outputCollector = outputCollector;

		this.timeStampCurrent = new Date().getTime();
		this.stats = new SummaryStatistics();
		this.misiones = new HashMap<String, Long>();
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

		@Override
		public void run() {
			System.out.println("Promedio de tiempo en finalizar : " + stats.getMean());
			System.out.println("Tiempo máximo : " + stats.getMax());
			System.out.println("Tiempo mínimo : " + stats.getMin());
			
			Count promedio = new Count("tiempoPromedio", ParseDate.parse(timeStampCurrent),stats.getMean());
			Count max = new Count("tiempoMaximo", ParseDate.parse(timeStampCurrent),stats.getMax());
			Count min = new Count("tiempoMinimo", ParseDate.parse(timeStampCurrent),stats.getMin());
			this.outputCollector.emit(promedio.factoryCount());
		}

	}

}
