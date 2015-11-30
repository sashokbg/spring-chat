package bg.alexander.company.model;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

/**
 * A class representing a user connection between the server and the client </br>
 * Each user tracks it's own message queue. Uses a blocking queue to make it wait</br>
 * 
 * 
 * @author Kirilov
 *
 */
public class UserConnection {
	private ArrayBlockingQueue<Message> messages;
	private String userName;
	private String userId;
	private boolean isWaiting;
	private boolean isActive;
	private final Logger log = LogManager.getLogger(UserConnection.class);
	
	@Value("${user.connection.keepalive.retries}")
	private String test;
	
	private int keepAliveRetries; //the number of consecutive times a user can timeout before disc
	
	public UserConnection() {
		//TODO externalize application properties https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html
		messages = new ArrayBlockingQueue<>(30);
		isActive = true;
		keepAliveRetries=3;
	}
	
	/**
	 * This method is called by the client, when he wants to check his messages </br>
	 * Essentially does messages.take() - blocking queue
	 * 
	 * @return the first message of the queue. Waits until one is available
	 */
	public Message readMessage(){
		if(isWaiting){
			log.error("Someone else is waiting already for a message on this connection. Attemting to liberate connection");
			try {
				messages.put(Message.EMPTY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {
			isWaiting = true;
			Message message = messages.take();
			log.debug("Message taken ["+message+"]");
			isWaiting = false;
			return message;
		} catch (InterruptedException e) {
			e.printStackTrace();
			isWaiting = false;
		}
		return null;
	}
	
	/**
	 * This method is called by the server, when someone wants to send a message to the current user
	 * @param message
	 * @return
	 */
	public boolean sendMessage(Message message){
		try {
			this.messages.put(message);
			keepAliveRetries = 3;
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void keepAlive(){
		keepAliveRetries--;
		try {
			this.messages.put(Message.EMPTY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(keepAliveRetries < 0){
			this.setActive(false);
		}
	}
	
	public ArrayBlockingQueue<Message> getMessages() {
		return messages;
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
	public void setUserId(String usedId) {
		this.userId = usedId;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isWaiting() {
		return isWaiting;
	}

	public void setWaiting(boolean isWaiting) {
		this.isWaiting = isWaiting;
	}
}
