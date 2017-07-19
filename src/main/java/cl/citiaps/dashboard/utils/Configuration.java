package cl.citiaps.dashboard.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	private String path;

	public ZK zk;
	public Kafka kafka;
	public ElasticSearch elasticSearch;
	public Backend backend;

	public Configuration(String path) {
		this.path = path;

		this.zk = null;
		this.kafka = null;
		this.elasticSearch = null;
	}

	public boolean Setup() {
		boolean status = true;

		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(path);
			prop.load(input);

			this.zk = new ZK(prop.getProperty("zookeeper.host"), Integer.parseInt(prop.getProperty("zookeeper.port")));
			this.kafka = new Kafka(prop.getProperty("kafka.topic"));
			this.elasticSearch = new ElasticSearch(prop.getProperty("elastic.host"),
					Integer.parseInt(prop.getProperty("elastic.port")), prop.getProperty("elastic.cluster.name"),
					prop.getProperty("elastic.index"), prop.getProperty("elastic.type"));
			this.backend = new Backend(prop.getProperty("backend.host"),
					Integer.parseInt(prop.getProperty("backend.port")));
		} catch (IOException ex) {
			ex.printStackTrace();
			status = false;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
					status = false;
				}
			}
		}

		return status;
	}

	public class ZK {
		public String host;
		public int port;

		public ZK(String host, int port) {
			this.host = host;
			this.port = port;
		}
	}

	public class Kafka {
		public String topic;

		public Kafka(String topic) {
			this.topic = topic;
		}
	}

	public class ElasticSearch {
		public String host;
		public int port;
		public String clusterName;
		public String index;
		public String type;

		public ElasticSearch(String host, int port, String clusterName, String index, String type) {
			this.host = host;
			this.port = port;
			this.clusterName = clusterName;
			this.index = index;
			this.type = type;
		}
	}

	public class Backend {
		public String host;
		public int port;

		public Backend(String host, int port) {
			this.host = host;
			this.port = port;
		}
	}
}
