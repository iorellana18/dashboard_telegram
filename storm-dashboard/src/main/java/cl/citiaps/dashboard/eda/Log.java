package cl.citiaps.dashboard.eda;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.storm.tuple.Values;

import com.google.gson.Gson;

public class Log {
	private long timestamp;
	private String texto;
	private String date;
	private String idUser;
	private String telefono;
	private String tipo;
	private String mision;
	private String latitud;
	private String longitud;
	private String location;
	private String tipoEmergencia;

	public Log(String text) {
		String[] parser = text.split(" ");
		if (parser.length == 11) { // Hay sólo una línea con 10 datos
			Date timeStamp = new Date(Long.valueOf(parser[0]));
			setTimestamp(Long.valueOf(parser[0]) / 1000);
			TimeZone timeZone = TimeZone.getTimeZone("UTC");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
			dateFormat.setTimeZone(timeZone);
			setDate(dateFormat.format(timeStamp));
			// setTimestamp(parser[0]);
			setIdUser(parser[1]);
			setTelefono(parser[2]);
			setTipo(parser[3]);
			setMision(parser[4]);
			setLatitud(parser[6]);
			setLongitud(parser[8]);
			setLocation(getLatitud() + "," + getLongitud());
			String[] parseEmergencia = parser[10].split("\\(");
			setTipoEmergencia(parseEmergencia[0]);
		}
	}

	public List<Object> factoryLog() {
		return new Values(this);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getIdUser() {
		return idUser;
	}

	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getMision() {
		return mision;
	}

	public void setMision(String mision) {
		this.mision = mision;
	}

	public String getLatitud() {
		return latitud;
	}

	public void setLatitud(String latitud) {
		this.latitud = latitud;
	}

	public String getLongitud() {
		return longitud;
	}

	public void setLongitud(String longitud) {
		this.longitud = longitud;
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

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
