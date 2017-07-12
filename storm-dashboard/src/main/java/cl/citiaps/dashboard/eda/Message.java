package cl.citiaps.dashboard.eda;

import java.util.Date;
import java.util.List;

import org.apache.storm.tuple.Values;

import com.google.gson.Gson;

public class Message {
	private String user;
	private Date date;
	private String text;

	public Message(String user, long date, String text) {
		setUser(user);
		setText(text);
		setDate(new Date(date));
	}

	public List<Object> factoryLog() {
		return new Values(this);
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
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
}
