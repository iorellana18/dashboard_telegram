package cl.citiaps.dashboard.eda;

import java.util.List;

import org.apache.storm.tuple.Values;

import com.google.gson.Gson;

public class Count {
	private String tipo;
	private String date;
	private Long count;

	public Count() {
		this.tipo = null;
		this.date = null;
		this.setCount(Long.valueOf(0));

	}

	public Count(String tipo, String date, Long count) {
		this.setTipo(tipo);
		this.date = date;
		this.setCount(count);
	}

	public List<Object> factoryCount() {
		return new Values(this);
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
