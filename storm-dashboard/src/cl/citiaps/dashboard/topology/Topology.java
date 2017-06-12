package cl.citiaps.dashboard.topology;

import java.util.UUID;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

import cl.citiaps.dashboard.bolt.ElasticSearch;
import cl.citiaps.dashboard.bolt.ParseLog;

public class Topology {
	private static final String TOPOLOGY_NAME = "dashboard";

	public static void main(String[] args) {
		String topicName = "ayni";
		BrokerHosts hosts = new ZkHosts("localhost:2181");
		SpoutConfig spoutConfig = new SpoutConfig(hosts, topicName, "/" + topicName, UUID.randomUUID().toString());
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);

		Config config = new Config();
		config.setDebug(false);
		config.setMessageTimeoutSecs(120);
		config.setNumWorkers(1);

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("LogsSpout", kafkaSpout, 1);
		builder.setBolt("ParseLog", new ParseLog(), 1).shuffleGrouping("LogsSpout");
		builder.setBolt("ElasticSearch", new ElasticSearch("localhost", 9200, "cluster")).shuffleGrouping("ParseLog");

		if (args != null && args.length > 0) {
			try {
				StormSubmitter.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());
			Utils.sleep(200000);
			cluster.shutdown();
		}

	}
}
