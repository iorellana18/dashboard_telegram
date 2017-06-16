package cl.citiaps.dashboard.bolt;

import java.util.ArrayList;
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

public class InicializarMision implements IRichBolt{

	private static final long serialVersionUID = 7784329420249780555L;
	private static Logger logger = LoggerFactory.getLogger(MisionesActivas.class);
	private OutputCollector outputCollector;
	private Map mapConf;
	private long timeDelay;
	private long emitTimeframe;
	private Timer emitTask;
	private Long timestampCurrent;
	private ArrayList<String> idsMision;
	private ArrayList<Long> timeStampList;
	private SummaryStatistics stats;
	
	
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
		if(log.getAccion().equals("CREATE_MISSION")){
			idsMision.add(log.getMision());
			System.out.println("\nSe agrega nueva mision :"+log.getMision());
			timeStampList.add(log.getTimestamp());
			//System.out.println("Con timestamp :"+log.getTimestamp());
		}
		
		if(log.getAccion().equals("INIT_MISSION")){
			for(int i=0; i<idsMision.size();i++){
				if(idsMision.get(i).equals(log.getMision())){
					stats.addValue(log.getTimestamp()-timeStampList.get(i));
					System.out.println("\nSe inicializa la mision "+log.getMision());
					break;
				}
			}
		}
		
	}

	@Override
	public void prepare(Map mapConf, TopologyContext arg1, OutputCollector outputCollector) {
		this.mapConf = mapConf;
		this.outputCollector = outputCollector;
		this.emitTask = new Timer();
		timeStampList = new ArrayList<Long>();
		idsMision = new ArrayList<String>();
		this.timestampCurrent = Long.valueOf(1397328001);
		stats = new SummaryStatistics();
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
			Count promedio = new Count("PromedioInicioMision",ParseDate.parse(timestampCurrent),(long)stats.getMean());
			Count max = new Count("TiempoMaximoInicioMision",ParseDate.parse(timestampCurrent),(long)stats.getMax());
			Count min = new Count("TiempoMinimoInicioMision",ParseDate.parse(timestampCurrent),(long)stats.getMin());
			Count desviacion = new Count("DesviacionEstandarInicioMision",ParseDate.parse(timestampCurrent),(long)stats.getStandardDeviation());
			Count misiones = new Count("TotalMisiones",ParseDate.parse(timestampCurrent),(long)stats.getN());
			this.outputCollector.emit(promedio.factoryCount());
			this.outputCollector.emit(max.factoryCount());
			this.outputCollector.emit(min.factoryCount());
			this.outputCollector.emit(desviacion.factoryCount());
			this.outputCollector.emit(misiones.factoryCount());
		}

	}
}
