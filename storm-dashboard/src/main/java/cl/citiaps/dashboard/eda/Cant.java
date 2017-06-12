package cl.citiaps.dashboard.eda;

import com.google.gson.Gson;

public class Cant {
	private String tipo;
	private String date;
	private int cant;
	private String mision;
	private String location;
	private String tipoEmergencia;

	public Cant() {
		this.date = null;
		this.cant = 0;
		this.mision = null;
		this.location = null;
		this.tipoEmergencia = null;
	}

	public Cant(String tipo, String date, int cant, String mision, String location, String tipoEmergencia) {
		this.tipo = tipo;
		this.date = date;
		this.cant = cant;
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

	public int getCant() {
		return cant;
	}

	public void setCant(int cant) {
		this.cant = cant;
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
