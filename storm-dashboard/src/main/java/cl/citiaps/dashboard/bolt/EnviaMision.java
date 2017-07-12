package cl.citiaps.dashboard.bolt;

import java.util.Map;
import java.util.Timer;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.citiaps.dashboard.eda.Log;
import cl.citiaps.dashboard.eda.Mision;

public class EnviaMision implements IRichBolt{
	
	
	private static final long serialVersionUID = 6101916216609388178L;

	private static Logger logger = LoggerFactory.getLogger(EnviaMensaje.class);
	private OutputCollector outputCollector;
	private Map mapConf;
	private long timeDelay;
	private long emitTimeframe;
	private Timer emitTask;
	
	public EnviaMision(long timeDelay, long emitTimeframe) {
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
		if(log.getText().equals("\\sys_enviar_mision") || log.equals("\\sys_iniciar_mision") || 
				log.equals("\\sys_terminar_mision")){
			Mision mision = log.getMision();
			this.outputCollector.emit(mision.factoryCount());
		}
		
	}

	@Override
	public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("mision"));
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return mapConf;
	}

}
