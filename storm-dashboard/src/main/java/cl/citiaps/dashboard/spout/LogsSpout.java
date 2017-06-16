package cl.citiaps.dashboard.spout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.citiaps.dashboard.eda.Log;
import cl.citiaps.dashboard.utils.Palabras;

public class LogsSpout implements IRichSpout {

	private static final long serialVersionUID = 3932220468408803184L;

	private static final Logger logger = LoggerFactory.getLogger(LogsSpout.class);

	private Stack<String> stack = new Stack<String>();
	private Map<Long, List<Log>> timestamp;
	private SpoutOutputCollector collector;
	private Map conf;

	private static final int delay = 10;

	private Long init = new Long("1397328001");

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void nextTuple() {
		if (timestamp.containsKey(init)) {
			List<Log> logsCurrent = timestamp.get(init);
			for (Log log : logsCurrent) {
				this.collector.emit(log.factoryLog());
			}

			init += 1;             
			Utils.sleep(delay);
		} else {
			init += 1;
			Utils.sleep(delay);
		}
	}

	private Map<Long, List<Log>> parseMap(Stack<String> stack) {
		Map<Long, List<Log>> timestamp = new HashMap<Long, List<Log>>();

		for (String raw : stack) {
			Log log = new Log(raw);
			if (timestamp.containsKey(log.getTimestamp())) {
				timestamp.get(log.getTimestamp()).add(log);
			} else {
				List<Log> logs = new ArrayList<Log>();
				logs.add(log);
				timestamp.put(log.getTimestamp(), logs);
			}
		}

		return timestamp;
	}

	@Override
	public void open(Map arg0, TopologyContext arg1, SpoutOutputCollector collector) {
		Palabras palabras = new Palabras();
		logger.info("Init");
		stack = palabras.leerDiccionario("logs/ayni.log");
		timestamp = parseMap(stack);
		logger.info("Finish");
		this.collector = collector;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("log"));

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return this.conf;
	}

	@Override
	public void ack(Object arg0) {
	}

	@Override
	public void activate() {
	}

	@Override
	public void deactivate() {
	}

	@Override
	public void fail(Object arg0) {
	}

}
