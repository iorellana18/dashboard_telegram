package cl.citiaps.dashboard.bolt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import cl.citiaps.dashboard.eda.Mision;
import cl.citiaps.dashboard.utils.ParseDate;

/*****
 * Bolt que cuenta la cantidad de voluntarios activos (entre ACCEPT_MISSION y FINISH_MISION)
 * Datos que envía:
 * * Cantidad de voluntarios activos 
 * * Cantidad de voluntarios que acepto una misión por unidad de tiempo
******/

/**
 * 
 * ARREGLAR SYNC THREAD !!!!
 * 
 * @author daniel
 *
 */

public class VoluntariosActivos implements IRichBolt {

	private static final long serialVersionUID = 7784329420249780555L;

	private static Logger logger = LoggerFactory.getLogger(VoluntariosActivos.class);

	private OutputCollector outputCollector;
	private Map mapConf;

	private Timer emitTask;
	private long timeDelay;
	private long emitTimeframe;

	private AtomicLong rateVoluntario;
	private AtomicLong voluntarioAcepta;
	private Long timestampCurrent;
	private Map<String, Long> misiones;

	public VoluntariosActivos(long timeDelay, long emitTimeframe) {
		this.timeDelay = timeDelay;
		this.emitTimeframe = emitTimeframe;
	}

	@Override
	public void prepare(Map mapConf, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.mapConf = mapConf;
		this.outputCollector = outputCollector;

		Long.valueOf(0);

		this.rateVoluntario = new AtomicLong(0);
		this.voluntarioAcepta = new AtomicLong(0);
		this.timestampCurrent = new Date().getTime();
		this.misiones = new HashMap<String, Long>();

		this.emitTask = new Timer();
		this.emitTask.scheduleAtFixedRate(new EmitTask(this.outputCollector), timeDelay * 1000, emitTimeframe * 1000);
	}

	@Override
	public void execute(Tuple tuple) {
		Log log = (Log) tuple.getValueByField("log");

		if (log.getAccion().equals("ACCEPT_MISSION")) {
			if(this.misiones.containsKey(log.getMision())){
				this.misiones.put(log.getMision(), this.misiones.get(log.getMision())+1);
			}else{
				this.misiones.put(log.getMision(),Long.valueOf(1));
			}
			rateVoluntario.getAndIncrement();
			voluntarioAcepta.getAndIncrement();
			timestampCurrent = log.getTimestamp();
		} else if (log.getAccion().equals("FINISH_MISSION") && log.getTipoUsuario().equals("VOLUNTEER")) {
			rateVoluntario.getAndSet(rateVoluntario.get()-this.misiones.get(log.getMision()));
			//rateVoluntario.getAndDecrement();
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
		private long acceptRate;
		private long previousAccept;

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

			long snapshot = rateVoluntario.get();
			this.rate = snapshot - this.previousSnapshot;
			this.previousSnapshot = snapshot;

			long aceptaSnapshot = voluntarioAcepta.get();
			this.acceptRate = aceptaSnapshot - this.previousAccept;
			this.previousAccept = aceptaSnapshot;

			long count = snapshot;
			long aceptadosTotales = aceptaSnapshot;

			Count acum = new Count("voluntariosActivosAcum", ParseDate.parse(timestampCurrent), count);
			Count rate = new Count("voluntariosActivosRate", ParseDate.parse(timestampCurrent), this.rate);
			this.outputCollector.emit(acum.factoryCount());
			this.outputCollector.emit(rate.factoryCount());
			
			Count totalAceptados = new Count ("totalAceptados",ParseDate.parse(timestampCurrent),aceptadosTotales);
			Count aceptadosxVentana = new Count ("aceptadosRate",ParseDate.parse(timestampCurrent),acceptRate);
			this.outputCollector.emit(totalAceptados.factoryCount());
			this.outputCollector.emit(aceptadosxVentana.factoryCount());
		}

	}

}
