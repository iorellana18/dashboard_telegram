package cl.citiaps.dashboard.bolt;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.citiaps.dashboard.eda.Count;
import cl.citiaps.dashboard.eda.Log;
import cl.citiaps.dashboard.eda.Mision;

public class ElasticSearch implements IRichBolt {

	private static final long serialVersionUID = 2826677710066604080L;

	private static final Logger logger = LoggerFactory.getLogger(ElasticSearch.class);

	private OutputCollector outputCollector;
	private Map mapConf;

	private TransportClient transportClient;

	private ObjectMapper oMapper;

	private String IP;
	private int port;
	private String clusterName;

	private String index;
	private String type;

	public ElasticSearch(String IP, int port, String clusterName, String index, String type) {
		this.IP = IP;
		this.port = port;
		this.clusterName = clusterName;
		this.index = index;
		this.type = type;
	}

	@Override
	public void cleanup() {
		logger.info("CleanUp ElasticSearch");
		this.transportClient.close();

		System.runFinalization();
		System.gc();
	}

	@Override
	public void execute(Tuple tuple) {
		if (tuple.contains("log")) {
			Log log = (Log) tuple.getValueByField("log");

			Map<String, Object> map = oMapper.convertValue(log, new TypeReference<Map<String, String>>() {
			});

			IndexResponse response = transportClient.prepareIndex(index, type).setSource(map).get();

		} else if (tuple.contains("count")) {
			Count count = (Count) tuple.getValueByField("count");

			Map<String, Object> map = oMapper.convertValue(count, new TypeReference<Map<String, Object>>() {
			});
			logger.info("{}", map);

			IndexResponse response = transportClient.prepareIndex(index, type).setSource(map).get();
			// logger.info("{}", response);
		} else if (tuple.contains("mision")) {
			Mision mision = (Mision) tuple.getValueByField("mision");

			Map<String, Object> map = oMapper.convertValue(mision, new TypeReference<Map<String, Object>>() {
			});
			logger.info("{}", map);

			IndexResponse response = transportClient.prepareIndex(index, type).setSource(map).get();
			// UpdateResponse response = transportClient.prepareUpdate("onemi",
			// "mision").setDoc(map).get();
			// logger.info("{}", response);
		}
	}

	@Override
	public void prepare(Map mapConf, TopologyContext topologyContext, OutputCollector outputCollector) {
		logger.info("Prepare ElasticSearch");

		this.mapConf = mapConf;
		this.outputCollector = outputCollector;

		Settings settings = Settings.builder().put("cluster.name", this.clusterName).build();
		try {
			this.transportClient = new PreBuiltTransportClient(settings);
			this.transportClient = this.transportClient
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(this.IP), this.port));
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
		}

		this.oMapper = new ObjectMapper();
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