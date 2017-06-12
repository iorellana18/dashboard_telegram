package cl.citiaps.dashboard.bolt;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.citiaps.dashboard.eda.Log;

public class Print implements IRichBolt {

	private static final long serialVersionUID = 2826677710066604080L;

	private static final Logger logger = LoggerFactory.getLogger(Print.class);

	private OutputCollector outputCollector;
	private Map mapConf;

	private boolean view;
	private Long init = new Long("1397328001");

	@Override
	public void cleanup() {
		logger.info("CleanUp Print");

		System.runFinalization();
		System.gc();
	}

	@Override
	public void execute(Tuple tuple) {
		Log log = (Log) tuple.getValueByField("log");
		if (log.getTimestamp() != init) {
			view = true;
			init += 1;
		}

		if (log.getTimestamp() == init && view) {
			logger.info(log.toString());
			view = false;
		}

	}

	@Override
	public void prepare(Map mapConf, TopologyContext topologyContext, OutputCollector outputCollector) {
		logger.info("Prepare Print");

		this.mapConf = mapConf;
		this.outputCollector = outputCollector;

		this.view = true;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		// outputFieldsDeclarer.declare(new Fields("texto"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return mapConf;
	}

}
