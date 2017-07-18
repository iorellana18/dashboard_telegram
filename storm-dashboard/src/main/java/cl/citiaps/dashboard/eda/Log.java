package cl.citiaps.dashboard.eda;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.storm.tuple.Values;

public class Log {
	private long timeStamp;
	private String messageId;
	private String text;
	private String firstName;
	private String lastName;
	private String userName;
	private String userId;
	private String missionId;
	private String mission;
	private String encargado;
	private long latitud;
	private long longitud;

	public Log(String text) {
		String[] parser = text.split("\\s*\",\"\\s*");
		setTimeStamp(Long.valueOf(parser[0].replace("\"", "")));
		setMessageId(parser[1]);
		setText(parser[2]);
		setFirstName(parser[3]);
		setLastName(parser[4]);
		setUserName(parser[5]);
		setUserId(parser[6]);
		setMissionId(parser[7]);
		setMission(parser[8]);
		setEncargado(parser[9]);
		setLatitud(Long.valueOf(parser[11]));
		setLongitud(Long.valueOf(parser[12]));
	}

	public Message getMessage() {
		Message message = new Message(this.userName, this.timeStamp, this.text);
		return message;
	}

	public Mision getMision() {
		Mision mision = new Mision();
		mision.setTipo(text);
		mision.setMision(mission);
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		dateFormat.setTimeZone(timeZone);
		mision.setDate(dateFormat.format(timeStamp));
		mision.setEncargado(encargado);
		mision.setLocation(latitud + "," + longitud);
		return mision;
	}

	public List<Object> factoryLog() {
		return new Values(this);
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMissionId() {
		return missionId;
	}

	public void setMissionId(String missionId) {
		this.missionId = missionId;
	}

	public String getEncargado() {
		return encargado;
	}

	public void setEncargado(String encargado) {
		this.encargado = encargado;
	}

	public String getMission() {
		return mission;
	}

	public void setMission(String mission) {
		this.mission = mission;
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
