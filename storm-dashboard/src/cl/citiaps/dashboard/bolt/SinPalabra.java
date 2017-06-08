package cl.citiaps.dashboard.bolt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.citiaps.dashboard.eda.Logs;
import cl.citiaps.dashboard.utils.Palabras;

public class SinPalabra implements IRichBolt {

	private static final long serialVersionUID = -3466248684452125481L;

	private static final Logger logger = LoggerFactory.getLogger(SinPalabra.class);
	private boolean showTuple = true;

	private OutputCollector outputCollector;
	private Map mapConf;

	private Palabras palabras;
	private List<String> listadoPalabras;

	public SinPalabra(String[] palabras) {
		this.listadoPalabras = new ArrayList<String>(Arrays.asList(palabras));
	}

	@Override
	public void cleanup() {
		logger.info("CleanUp Sentimientos Bolt");

		System.runFinalization();
		System.gc();
	}

	@Override
	public void execute(Tuple tuple) {
		Logs texto = (Logs) tuple.getValueByField("texto");
		if (showTuple)
			logger.info(texto.toString());

		if (!this.palabras.contiene(this.listadoPalabras, texto.getTexto())) {
			if (showTuple)
				logger.info(texto.toString());

			this.outputCollector.emit(tuple, texto.factoryTexto());
			this.outputCollector.ack(tuple);
		}
	}

	@Override
	public void prepare(Map mapConf, TopologyContext topologyContext, OutputCollector outputCollector) {
		logger.info("Prepare Sentimientos Bolt");

		this.mapConf = mapConf;
		this.outputCollector = outputCollector;

		this.palabras = new Palabras();
		logger.info(this.listadoPalabras.toString());
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("texto"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return mapConf;
	}

}
