package cl.citiaps.dashboard.bolt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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

public class cuentaRechazos implements IRichBolt  {

	private static final long serialVersionUID = 7784329420249780555L;

	private static Logger logger = LoggerFactory.getLogger(VoluntariosRechazan.class);

	private OutputCollector outputCollector;
	private Map mapConf;
	private Timer emitTask;
	private long timeDelay;
	private long emitTimeframe;
	private ArrayList<String> idAceptados;
	private Long timestampCurrent;
	private long cuentaRechazos;
	
	public cuentaRechazos(long timeDelay, long emitTimeframe) {
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
		if(log.getAccion().equals("ACCEPT_MISSION")){
			idAceptados.add(log.getIdUsuario());
		}
		if(log.getAccion().equals("REJECT_MISSION")){
			// Se comprueba que los que rechacen la misión no hayan aceptado una antes
			if(!idAceptados.contains(log.getIdUsuario())){
				//System.out.println("Usuario +"+log.getIdUsuario()+" con mision "+log.getAccion());
				cuentaRechazos++;
				this.timestampCurrent = log.getTimestamp();
			}
		}
		
	}

	@Override
	public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
		this.mapConf = mapConf;
		this.outputCollector = outputCollector;
		this.emitTask = new Timer();
		idAceptados = new ArrayList<String>();
		cuentaRechazos = 0;
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
			
			
			System.out.println("Cantidad de rechazos : "+cuentaRechazos);
			// Ya que hay que contar igual a los que aceptaron se podría obtener otros números
			System.out.println("Total misioneros : "+(cuentaRechazos+idAceptados.size()));
			System.out.println("Total porcentaje rechazo : "+((cuentaRechazos*100)/(cuentaRechazos+idAceptados.size())));
			
			//Probar con elasticSearch
			/*Count contador = new Count("Rechazados", ParseDate.parse(timestampCurrent),
					cuentaRechazos);
			this.outputCollector.emit(contador.factoryCount());
			*/

		}

	}
}
