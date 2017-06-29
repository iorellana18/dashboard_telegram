package cl.citiaps.dashboard.eda;

import java.util.List;

import org.apache.storm.tuple.Values;

import com.google.gson.Gson;

public class Mision {
	private String tipo;
	private String date;
	private Long count;
	private Long cantVoluntarios;
	private String mision;
	private String location;
	private String emergencia;

	public Mision() {
		this.tipo = null;
		this.date = null;
		this.setCount(Long.valueOf(0));
		this.setCantVoluntarios(Long.valueOf(0));
		this.mision = null;
		this.location = null;
		this.emergencia = null;
	}

	public Mision(String tipo, String date, Long count, Long cantVoluntarios, String mision, String location,
			String emergencia) {
		this.setTipo(tipo);
		this.date = date;
		this.setCount(count);
		this.setCantVoluntarios(cantVoluntarios);
		this.mision = mision;
		this.location = location;
		this.emergencia = emergencia;

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

	public Long getCantVoluntarios() {
		return cantVoluntarios;
	}

	public void setCantVoluntarios(Long cantVoluntarios) {
		this.cantVoluntarios = cantVoluntarios;
	}

	public String getMision() {
		return mision;
	}

	public void setMision(String mision) {
		this.mision = mision;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

}
