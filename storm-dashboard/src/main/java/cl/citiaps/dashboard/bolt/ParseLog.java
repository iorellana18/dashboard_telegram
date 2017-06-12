package cl.citiaps.dashboard.bolt;

import java.util.List;
import java.util.Map;

import org.apache.storm.generated.GlobalStreamId;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.MessageId;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.citiaps.dashboard.eda.Log;

public class ParseLog implements IRichBolt {

	private static final long serialVersionUID = 6101916216609388178L;

	private static final Logger logger = LoggerFactory.getLogger(ParseLog.class);

	private OutputCollector outputCollector;
	private Map mapConf;

	@Override
	public void cleanup() {
		logger.info("CleanUp ParseLog");

		System.runFinalization();
		System.gc();
	}

	@Override
	public void execute(Tuple tuple) {
		String text = (String) tuple.getValueByField("str");
		Log log = new Log(text);
		Values values = new Values(log);
		this.outputCollector.emit(values);
	}

	@Override
	public void prepare(Map mapConf, TopologyContext topologyContext, OutputCollector outputCollector) {
		logger.info("Prepare ParseLog");

		this.mapConf = mapConf;
		this.outputCollector = outputCollector;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("log"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return mapConf;
	}

}
