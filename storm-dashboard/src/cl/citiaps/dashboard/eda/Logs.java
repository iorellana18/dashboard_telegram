package cl.citiaps.dashboard.eda;

import java.util.Date;
import java.util.List;

import org.apache.storm.tuple.Values;

public class Logs {
	private Date timestamp;
	private String texto;
	private String sentimiento;

	public Logs() {
		this.setTimestamp(null);
		this.setTexto(null);
		this.setSentimiento(null);
	}

	public Logs(Date timestamp, String texto, String sentimiento) {
		super();
		this.setTimestamp(timestamp);
		this.setTexto(texto);
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

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getSentimiento() {
		return sentimiento;
	}

	public void setSentimiento(String sentimiento) {
		this.sentimiento = sentimiento;
	}

	@Override
	public String toString() {
		return "{Texto=" + getTexto() + "},{Timestamp=" + getTimestamp().toString() + "}";
	}

}
