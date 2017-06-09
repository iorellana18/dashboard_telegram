package cl.citiaps.dashboard.eda;

import java.util.Date;
import java.util.List;

import org.apache.storm.tuple.Values;

public class Logs {
	private Date timestamp;
	private String idUser;
	private String sentimiento;

	public Logs() {
		this.setTimestamp(null);
		this.setSentimiento(null);
	}

	public Logs(Date timestamp, String sentimiento) {
		super();
		this.setTimestamp(timestamp);
		this.setSentimiento(sentimiento);
	}

	public List<Object> factoryTexto() {
		return new Values(this);
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getSentimiento() {
		return sentimiento;
	}

	public void setSentimiento(String sentimiento) {
		this.sentimiento = sentimiento;
	}

	// @Override
	// public String toString() {
	// return "{Texto=" + getTexto() + "},{Timestamp=" +
	// getTimestamp().toString() + "}";
	// }

}
