package cl.citiaps.dashboard.eda;

import java.util.List;

import org.apache.storm.tuple.Values;

import com.google.gson.Gson;

public class Mision {
	private String tipo;
	private String date;
	private Long count;
	private String mision;
	private String emergencia;
	private String encargado;
	private long latitud;
	private long longitud;

	public Mision() {
		this.tipo = null;
		this.date = null;
		this.setCount(Long.valueOf(0));
		this.mision = null;
		this.emergencia = null;
		this.encargado = null;
	}

	public Mision(String tipo, String date, String mision, String emergencia,String encargado) {
		this.setTipo(tipo);
		this.date = date;
		this.mision = mision;
		this.emergencia = emergencia;
		this.encargado = encargado;

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

	public String getMision() {
		return mision;
	}

	public void setMision(String mision) {
		this.mision = mision;
	}

	public String getEmergencia() {
		return emergencia;
	}

	public void setEmergencia(String emergencia) {
		this.emergencia = emergencia;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getEncargado() {
		return encargado;
	}

	public void setEncargado(String encargado) {
		this.encargado = encargado;
	}

	public long getLatitud() {
		return latitud;
	}

	public void setLatitud(long latitud) {
		this.latitud = latitud;
	}

	public long getLongitud() {
		return longitud;
	}

	public void setLongitud(long longitud) {
		this.longitud = longitud;
	}

}
