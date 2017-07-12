package cl.citiaps.dashboard.eda;

import java.util.Date;
import java.util.List;

import org.apache.storm.tuple.Values;

import com.google.gson.Gson;

public class Message {
	private User user;
	private long timeStamp;
	private Date date;
	private String text;

	public Message() {

	}

	public Message(User user, long date, String text) {
		setUser(user);
		setText(text);
		setDate(new Date(timeStamp));
	}

	public List<Object> factoryLog() {
		return new Values(this);
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
}
