package cl.citiaps.dashboard.eda;

import java.util.Date;
import java.util.List;

import org.apache.storm.tuple.Values;

import com.google.gson.Gson;

import cl.citiaps.dashboard.utils.ParseDate;

public class Message {
	private String User;
	private String Date;
	private String Text;

	public Message(String user, long date, String text) {
		setUser(user);
		setText(text);
		setDate(ParseDate.parse(date));
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
		return User;
	}

	public void setUser(String user) {
		this.User = user;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String string) {
		this.Date = string;
	}

	public String getText() {
		return Text;
	}

	public void setText(String text) {
		this.Text = text;
	}
}
