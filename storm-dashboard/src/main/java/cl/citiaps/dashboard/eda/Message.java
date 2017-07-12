package cl.citiaps.dashboard.eda;

import java.util.List;

import org.apache.storm.tuple.Values;

public class Message {
	private String messageId;
	private User user;
	private long date;
	private String text;
	
	public Message(){
		
	}
	
	public Message(String messageId, User user, long date, String text){
		setMessageId(messageId);
		setUser(user);
		setDate(date);
		setText(text);
	}
	
	
	public List<Object> factoryLog() {
		return new Values(this);
	}
	
	

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}


	public String getMessageId() {
		return messageId;
	}


	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
}
