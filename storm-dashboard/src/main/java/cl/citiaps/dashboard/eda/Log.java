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
	private String emergency;
	private long voluntarios;
	
	public Log(String text) {
		String[] parser = text.split("\",\"");
		setTimeStamp(Long.valueOf(parser[0]));
		setMessageId(parser[1]);
		setText(parser[2].substring(1,-1));
		setFirstName(parser[3].substring(1,-1));
		setLastName(parser[4].substring(1,-1));
		setUserName(parser[5].substring(1,-1));
		setUserId(parser[6].substring(1,-1));
		setMissionId(parser[7].substring(1,-1));
		setEmergency(parser[8].substring(1,-1));
		setVoluntarios(Long.valueOf(parser[9].substring(1,-1)));
	}
	
	public Message getMessage(){
		Message message = new Message();
		User user = new User();
		message.setTimeStamp(timeStamp);
		message.setText(text);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUserName(userName);
		user.setId(userId);
		message.setUser(user);
		return message;
	}
	
	public Mision getMision(){
		Mision mision = new Mision();
		mision.setTipo(text);
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		dateFormat.setTimeZone(timeZone);
		mision.setDate(dateFormat.format(timeStamp));
		mision.setCantVoluntarios(voluntarios);
		mision.setLocation(null);
		mision.setEmergencia(emergency);
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

	public String getEmergency() {
		return emergency;
	}

	public void setEmergency(String emergency) {
		this.emergency = emergency;
	}

	public long getVoluntarios() {
		return voluntarios;
	}

	public void setVoluntarios(long voluntarios) {
		this.voluntarios = voluntarios;
	}
	
	
	
	
}
