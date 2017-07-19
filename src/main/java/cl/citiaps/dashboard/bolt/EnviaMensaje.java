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
import cl.citiaps.dashboard.utils.Configuration;

/*****
 * Bolt que envía post a aplicación de chat y envía mensaje a elasticsearch
 ******/

public class EnviaMensaje implements IRichBolt {

	private static final long serialVersionUID = 6101916216609388178L;

	private static final Logger logger = LoggerFactory.getLogger(EnviaMensaje.class);

	private OutputCollector outputCollector;
	private Map mapConf;

	private Configuration configuration;

	private ClienteHTTP cliente;

	@Override
	public void cleanup() {
		logger.info("Close " + this.getClass().getSimpleName());
	}

	@Override
	public void execute(Tuple tuple) {
		Log log = (Log) tuple.getValueByField("log");
		Message message = log.getMessage();

		/**
		 * Envío del log al Backend de Go, para que este mensaje sea visualizado
		 * en el chat
		 */
		this.cliente.sendPost(message);

		/**
		 * Además de enviarlo a ElasticSearch para que éste realice una nube de
		 * términos respecto a los datos que se consideren necesarios
		 */
		if (comprueba(log.getText())) {
			this.outputCollector.emit(message.factoryLog());
		}

	}

	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.mapConf = map;
		this.outputCollector = outputCollector;

		this.configuration = new Configuration("config/config.properties");
		this.configuration.Setup();

		this.cliente = new ClienteHTTP(this.configuration.backend.host, this.configuration.backend.port);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("message"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return mapConf;
	}

	public Boolean comprueba(String texto) {
		if (texto.equals("/sys_enviar_mision") || texto.equals("/enviar_mision") || texto.equals("/sys_terminar_mision")
				|| texto.equals("/terminar_tarea") || texto.equals("/terminar_tarea@RimayBot")
				|| texto.equals("/listar_tareas@RimayBot") || texto.equals("/listar_tareas")
				|| texto.equals("/ayuda@RimayBot") || texto.equals("/ayuda") || texto.equals("/admin_user@RimayBot")
				|| texto.equals("/admin_user") || texto.equals("/crear_tarea@RimayBot") || texto.equals("/crear_tarea")
				|| texto.equals("/listar_usuarios") || texto.equals("/listar_usuarios@RimayBot")
				|| texto.equals("/cancelar@RimayBot") || texto.equals("/cancelar") || texto.equals("code_0")) {
			return false;
		}
		return true;
	}

}
