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

import cl.citiaps.dashboard.bolt.ElasticSearch;
import cl.citiaps.dashboard.bolt.EnviaMensaje;
import cl.citiaps.dashboard.bolt.EnviaMision;
import cl.citiaps.dashboard.bolt.MisionesPorPersona;
import cl.citiaps.dashboard.bolt.MisionesTotales;
import cl.citiaps.dashboard.bolt.ParseLog;
import cl.citiaps.dashboard.utils.Configuration;

public class Topology {
	public static void main(String[] args) {
		Configuration configuration = new Configuration("config/config.properties");
		configuration.Setup();

		BrokerHosts hosts = new ZkHosts(configuration.zk.host + ":" + configuration.zk.port);
		SpoutConfig spoutConfig = new SpoutConfig(hosts, configuration.kafka.topic, "/" + configuration.kafka.topic,
				UUID.randomUUID().toString());
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);

		Config config = new Config();
		config.setDebug(false);
		config.setMessageTimeoutSecs(3600);
		config.setNumWorkers(1);

		TopologyBuilder builder = new TopologyBuilder();

		/**
		 * Consumidor de Kafka
		 */
		builder.setSpout("LogsSpout", kafkaSpout, 1);

		/**
		 * Parseo de los datos que se obtienen de Kafka en la clase Log
		 */
		builder.setBolt("ParseLog", new ParseLog(), 1).shuffleGrouping("LogsSpout");

		/**
		 * Envío de los mensajes
		 */
		builder.setBolt("EnviaMensaje", new EnviaMensaje(), 1).shuffleGrouping("ParseLog");

		/**
		 * Obtención de las estadísticas respecto a la creación y cantidad de
		 * misiones que se encuentren realizadas en el sistema
		 */
		builder.setBolt("EnviaMision", new EnviaMision(), 1).shuffleGrouping("ParseLog");
		builder.setBolt("MisionesTotales", new MisionesTotales(5, 5), 1).shuffleGrouping("ParseLog");
		builder.setBolt("MisionesPorPersona", new MisionesPorPersona(5, 5), 1).shuffleGrouping("ParseLog");

		/**
		 * Envío de las estadísticas obtenidas anteriormente
		 */
		builder.setBolt("ElasticSearch",
				new ElasticSearch(configuration.elasticSearch.host, configuration.elasticSearch.port,
						configuration.elasticSearch.clusterName, configuration.elasticSearch.index,
						configuration.elasticSearch.type))
				.shuffleGrouping("EnviaMensaje").shuffleGrouping("EnviaMision").shuffleGrouping("MisionesPorPersona")
				.shuffleGrouping("MisionesTotales");

		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology(args[0], config, builder.createTopology());
	}
}
