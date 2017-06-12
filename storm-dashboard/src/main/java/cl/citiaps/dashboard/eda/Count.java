package cl.citiaps.dashboard.eda;

import com.google.gson.Gson;

public class Count {
	private String tipo;
	private String date;
	private int count;
	private String mision;
	private String location;
	private String tipoEmergencia;

	public Count() {
		this.date = null;
		this.setCount(0);
		this.mision = null;
		this.location = null;
		this.tipoEmergencia = null;
	}

	public Count(String tipo, String date, int count, String mision, String location, String tipoEmergencia) {
		this.tipo = tipo;
		this.date = date;
		this.setCount(count);
		this.mision = mision;
		this.location = location;
		this.tipoEmergencia = tipoEmergencia;

	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
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

	public String getTipoEmergencia() {
		return tipoEmergencia;
	}

	public void setTipoEmergencia(String tipoEmergencia) {
		this.tipoEmergencia = tipoEmergencia;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
