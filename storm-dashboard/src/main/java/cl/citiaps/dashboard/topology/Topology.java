package cl.citiaps.dashboard.topology;

import java.util.UUID;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;

import cl.citiaps.dashboard.bolt.EnviaMensaje;
import cl.citiaps.dashboard.bolt.EnviaMision;
import cl.citiaps.dashboard.bolt.MisionesTotales;
import cl.citiaps.dashboard.bolt.ParseLog;

public class Topology {
	public static void main(String[] args) {
		String topicName = args[1];
		BrokerHosts hosts = new ZkHosts("158.170.140.101:2181");
		SpoutConfig spoutConfig = new SpoutConfig(hosts, topicName, "/" + topicName, UUID.randomUUID().toString());
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);

		Config config = new Config();
		config.setDebug(false);
		config.setMessageTimeoutSecs(3600);
		config.setNumWorkers(1);

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("LogsSpout", kafkaSpout, 1);

		builder.setBolt("ParseLog", new ParseLog(), 1).shuffleGrouping("LogsSpout");
		builder.setBolt("MisionesTotales", new MisionesTotales(5, 5), 1).shuffleGrouping("ParseLog");
		builder.setBolt("EnviaMensaje", new EnviaMensaje(5, 5), 1).shuffleGrouping("ParseLog");
		builder.setBolt("EnviaMision", new EnviaMision(5, 5), 1).shuffleGrouping("ParseLog");

		// builder.setBolt("ElasticSearch", new ElasticSearch("158.170.140.158",
		// 9300, "cluster", "telegram", args[2]))
		// .shuffleGrouping("MisionesTotales")
		// .shuffleGrouping("EnviaMision");

		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology(args[0], config, builder.createTopology());
	}
}
