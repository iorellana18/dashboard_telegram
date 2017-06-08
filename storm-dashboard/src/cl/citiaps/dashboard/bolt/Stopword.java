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

public class Stopword implements IRichBolt {

	private static final long serialVersionUID = 2826677710066604080L;

	private static final Logger logger = LoggerFactory.getLogger(Stopword.class);
	private boolean showTuple = true;

	private OutputCollector outputCollector;
	private Map mapConf;

	private Palabras palabras;
	private List<String> stopwords;

	public Stopword(String[] palabras) {
		this.stopwords = new ArrayList<String>(Arrays.asList(palabras));
	}

	@Override
	public void cleanup() {
		logger.info("CleanUp Stopword Bolt");

		System.runFinalization();
		System.gc();
	}

	@Override
	public void execute(Tuple tuple) {
		Logs texto = (Logs) tuple.getValueByField("texto");
		if (showTuple)
			logger.info(texto.toString());

		texto.setTexto(this.palabras.eliminar(stopwords, texto.getTexto()));

		if (showTuple)
			logger.info(texto.toString());

		this.outputCollector.emit(tuple, texto.factoryTexto());
		this.outputCollector.ack(tuple);
	}

	@Override
	public void prepare(Map mapConf, TopologyContext topologyContext, OutputCollector outputCollector) {
		logger.info("Prepare Stopword Bolt");

		this.mapConf = mapConf;
		this.outputCollector = outputCollector;

		this.palabras = new Palabras();
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
