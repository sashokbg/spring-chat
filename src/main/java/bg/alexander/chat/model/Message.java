package bg.alexander.chat.model;

import java.time.LocalTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import bg.alexander.chat.controllers.formatters.JsonDateSerializer;

/**
 * Bean representing a message sent through a user connection 
 * 
 * @see UserConnection
 * @author Kirilov
 *
 */
public class Message {
	public static Message EMPTY = new Message();
	public static Message CLOSE = new Message("CONNECTION CLOSE");;
	private User fromUser;
	private String message;
	@JsonSerialize(using=JsonDateSerializer.class)
	private LocalTime timeStamp;

	public Message() {
		message = "";
		fromUser= new User();
		timeStamp = LocalTime.now();
	}
	
	public Message(String message) {
		timeStamp = LocalTime.now();
		this.message = message;
	}
	
	public Message(User fromUser, String message) {
		timeStamp = LocalTime.now();
		this.fromUser = fromUser;
		this.message = message;
	}
	
	public User getFromUser() {
		return fromUser;
	}
	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public LocalTime getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(LocalTime timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	@Override
	public String toString(){
		return "["+timeStamp+"]["+fromUser+"] "+message;
	}
}
