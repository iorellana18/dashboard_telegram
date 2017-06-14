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
	private String date;
	private String tipoUsuario;
	private String idUsuario;
	private String accion;
	private String mision;
	private String location;
	private String emergencia;

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
			setTipoUsuario(parser[1]);
			setIdUsuario(parser[2]);
			setAccion(parser[3]);
			setMision(parser[4]);
			setLocation(parser[6] + "," + parser[8]);
			String[] parseEmergencia = parser[10].split("\\(");
			setEmergencia(parseEmergencia[0]);
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(String tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
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
