package cl.citiaps.dashboard.eda;

import java.util.Date;
import java.util.List;

import org.apache.storm.tuple.Values;

public class Log {
	private String texto;
	private Date timestamp;
	private String idUser;
	private String telefono;
	private String tipo;
	private String mision;
	private String latitud;
	private String longitud;
	private String tipoEmergencia;
	
	public Log(String text){
		String[] parser = text.split(" ");
		if(parser.length==11){ //Hay sólo una línea con 10 datos
			Date timeStamp = new Date(Long.valueOf(parser[0]));
			setTimestamp(timeStamp);
			setIdUser(parser[1]);
			setTelefono(parser[2]);
			setTipo(parser[3]);
			setMision(parser[4]);
			setLatitud(parser[6]);
			setLongitud(parser[8]);
			setTipoEmergencia(parser[10]);
		}
	}
	
	public void printLog(){
		System.out.println(timestamp+":"+idUser+":"+telefono+":"+tipo+":"+mision+":"+latitud+":"+longitud+":"+tipoEmergencia);
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

}
