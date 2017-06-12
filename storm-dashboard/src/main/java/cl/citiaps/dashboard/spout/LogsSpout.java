package cl.citiaps.dashboard.spout;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import cl.citiaps.dashboard.utils.Palabras;

public class LogsSpout implements IRichSpout {

	private static final long serialVersionUID = 3932220468408803184L;
	Stack<String> stack = new Stack<String>();
	private SpoutOutputCollector collector;
	private Map conf;
	@Override
	public void ack(Object arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void fail(Object arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nextTuple() {
		if(!stack.isEmpty()){
			Values line = new Values(stack.pop());
			//System.out.println("imprime :" +line);
			this.collector.emit(line);
		}

	}

	@Override
	public void open(Map arg0, TopologyContext arg1, SpoutOutputCollector arg2) {
		Palabras palabras = new Palabras();
		stack = palabras.leerDiccionario("simulator-logs/ayni.log");
		
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("cadena"));

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return this.conf;
	}

}
