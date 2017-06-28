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

import cl.citiaps.dashboard.bolt.ElasticSearch;
import cl.citiaps.dashboard.bolt.InicializarMision;
import cl.citiaps.dashboard.bolt.MisionesEspera;
import cl.citiaps.dashboard.bolt.MisionesEstado;
import cl.citiaps.dashboard.bolt.ParseLog;
import cl.citiaps.dashboard.bolt.ResponderMision;
import cl.citiaps.dashboard.bolt.VoluntariosActivos;
import cl.citiaps.dashboard.bolt.VoluntariosMisiones;
import cl.citiaps.dashboard.bolt.VoluntariosRechazan;

public class Topology {
	public static void main(String[] args) {
		String topicName = args[1];
		BrokerHosts hosts = new ZkHosts("158.170.169.205:2181");
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

		// builder.setBolt("ResponderMision", new ResponderMision(5,
		// 5)).shuffleGrouping("ParseLog");
		// builder.setBolt("InicializarMision", new InicializarMision(5,
		// 5)).shuffleGrouping("ParseLog");
		builder.setBolt("MisionesEstado", new MisionesEstado(5, 5)).shuffleGrouping("ParseLog");
		builder.setBolt("MisionesEspera", new MisionesEspera(5, 5)).shuffleGrouping("ParseLog");
		builder.setBolt("VoluntariosActivos", new VoluntariosActivos(5, 5)).shuffleGrouping("ParseLog");
		builder.setBolt("VoluntariosRechazan", new VoluntariosRechazan(5, 5)).shuffleGrouping("ParseLog");
		builder.setBolt("VoluntariosMisiones", new VoluntariosMisiones(5, 5)).shuffleGrouping("ParseLog");

		builder.setBolt("ElasticSearch", new ElasticSearch("158.170.140.158", 9300, "cluster", args[2]))
				// .shuffleGrouping("ResponderMision").shuffleGrouping("InicializarMision")
				.shuffleGrouping("MisionesEstado").shuffleGrouping("MisionesEspera")
				.shuffleGrouping("VoluntariosActivos").shuffleGrouping("VoluntariosMisiones")
				.shuffleGrouping("VoluntariosRechazan");

		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology(args[0], config, builder.createTopology());
	}
}
