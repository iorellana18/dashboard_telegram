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
import cl.citiaps.dashboard.eda.Message;
import cl.citiaps.dashboard.utils.ClienteHTTP;

/*****
 * Bolt que envía post a aplicación de chat y envía mensaje a elasticsearch
******/


public class EnviaMensaje implements IRichBolt {

	private static final long serialVersionUID = 6101916216609388178L;

	private static Logger logger = LoggerFactory.getLogger(EnviaMensaje.class);
	private OutputCollector outputCollector;
	private Map mapConf;

	@Override
	public void cleanup() {
		logger.info("Close " + this.getClass().getSimpleName());
	}

	@Override
	public void execute(Tuple tuple) {
		Log log = (Log) tuple.getValueByField("log");
		Message message = log.getMessage();
		ClienteHTTP cliente = new ClienteHTTP();
		cliente.sendPost(message);
		if(comprueba(log.getText())){
			this.outputCollector.emit(message.factoryLog());
		}
		
	}

	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.mapConf = map;
		this.outputCollector = outputCollector;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("message"));

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return mapConf;
	}
	
	public Boolean comprueba(String texto){
		if(texto.equals("/sys_enviar_mision")||texto.equals("/enviar_mision")||texto.equals("/sys_terminar_mision")
				||texto.equals("/terminar_mision")||texto.equals("/ayuda@RimayBot")
				||texto.equals("/listar_misiones@RimayBot")||texto.equals("/crear_mision@RimayBot")
				||texto.equals("/terminar_mision@RimayBot")||texto.equals("/cancelar@RimayBot")
				||texto.equals("/admin_user@RimayBot")){
					return false;
				}
		return true;
	}

}
