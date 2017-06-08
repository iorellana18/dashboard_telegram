package cl.citiaps.dashboard.topology;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

public class Topology {
	private static final String TOPOLOGY_NAME = "dashboard";

	public static void main(String[] args) {

		Config config = new Config();
		config.setDebug(true);
		config.setMessageTimeoutSecs(120);
		config.setNumWorkers(1);

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("LogsSpout", new KafkaSpout<>(KafkaSpoutConfig.builder("127.0.0.1:9092", "ayni").build()),
				1);

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
