package cl.citiaps.dashboard.topology;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;

import cl.citiaps.dashboard.bolt.VoluntariosActivos;
import cl.citiaps.dashboard.bolt.VoluntariosMisiones;
import cl.citiaps.dashboard.bolt.VoluntariosRechazan;
import cl.citiaps.dashboard.spout.LogsSpout;
import cl.citiaps.dashboard.bolt.ElasticSearch;
import cl.citiaps.dashboard.bolt.InicializarMision;
import cl.citiaps.dashboard.bolt.MisionesEspera;
import cl.citiaps.dashboard.bolt.MisionesEstado;
import cl.citiaps.dashboard.bolt.ResponderMision;

public class Topology {
	private static final String TOPOLOGY_NAME = "dashboard";

	public static void main(String[] args) {
		// String topicName = "ayni";
		// BrokerHosts hosts = new ZkHosts("localhost:2181");
		// SpoutConfig spoutConfig = new SpoutConfig(hosts, topicName, "/" +
		// topicName, UUID.randomUUID().toString());
		// spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		// KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);

		Config config = new Config();
		config.setDebug(false);
		config.setMessageTimeoutSecs(3600);
		config.setNumWorkers(1);

		TopologyBuilder builder = new TopologyBuilder();

		// builder.setSpout("LogsSpout", kafkaSpout, 1);
		builder.setSpout("LogsSpout", new LogsSpout(), 1);

		builder.setBolt("ResponderMision", new ResponderMision(5, 5)).shuffleGrouping("LogsSpout");
		builder.setBolt("InicializarMision", new InicializarMision(5, 5)).shuffleGrouping("LogsSpout");
		// builder.setBolt("MisionesEstado", new MisionesEstado(5,
		// 5)).shuffleGrouping("LogsSpout");
		// builder.setBolt("VoluntariosActivos", new VoluntariosActivos(5,
		// 5)).shuffleGrouping("LogsSpout");
		// builder.setBolt("VoluntariosMisiones", new VoluntariosMisiones(5,
		// 5)).shuffleGrouping("LogsSpout");
		// builder.setBolt("VoluntariosRechazan", new VoluntariosRechazan(5,
		// 5)).shuffleGrouping("LogsSpout");

		builder.setBolt("ElasticSearch", new ElasticSearch("158.170.140.158", 9300, "cluster"))
				.shuffleGrouping("ResponderMision").shuffleGrouping("InicializarMision");
		// .shuffleGrouping("MisionesActivas").shuffleGrouping("MisionesFinalizadas")
		// .shuffleGrouping("MisionesIniciadas").shuffleGrouping("VoluntariosActivos")
		// .shuffleGrouping("VoluntariosMisiones").shuffleGrouping("VoluntariosRechazan");

		if (args != null && args.length > 0) {
			try {
				StormSubmitter.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());
			// Utils.sleep(2000000000);
			// cluster.shutdown();
		}

	}
}
