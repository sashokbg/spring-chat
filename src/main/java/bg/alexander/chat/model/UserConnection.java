package bg.alexander.chat.model;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>
 * A class representing a user connection between the server and the client </br>
 * Each user tracks it's own message queue. Uses a blocking queue to make it wait
 * </p>
 * 
 * <p>
 * 	{@link Message.EMPTY} represents a keep alive message
 * </p>
 * 
 * @author Kirilov
 *
 */
public class UserConnection {
	private ArrayBlockingQueue<Message> messages;
	private User user;
	private boolean isWaiting;
	private boolean isActive;
	private final Logger log = LogManager.getLogger(UserConnection.class);
	
	private int keepAliveRetries; //the number of consecutive times a user can timeout before disc
	
	public UserConnection() {
		//TODO externalize application properties https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html
		messages = new ArrayBlockingQueue<>(30);
		isActive = true;
		keepAliveRetries=3;
	}
	
	/**
	 * <p>
	 * 	This method is called by the client, when he wants to check his messages </br>
	 * 	Essentially does messages.take() - blocking queue
	 * </p>
	 * <p>
	 * 	Reactivates the connection if it has not consumed all of it's timeouts
	 * </p>
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
			//reactivate the connection since there is traffic on it
			if(keepAliveRetries>0){
				isActive = true;
			}
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
	
	/**
	 * Keep the current connection alive by sending an empty keep alive message <br />
	 * When keep alive is initiated the connection is set to inactive, until the user actually consumes it
	 * @see #readMessage(Message)
	 */
	public void keepAlive(){
		keepAliveRetries--;
		try {
			isActive = false; // on each timeout we deactivate
			messages.put(Message.EMPTY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(keepAliveRetries <= 0){
			this.setActive(false);
		}
	}
	
	public ArrayBlockingQueue<Message> getMessages() {
		return messages;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public boolean isActive() {
		return isActive;
	}
	
	/**
	 * Are there any timeout retries left ?
	 * @return <b>true</b> if keepAliveRetries = 0
	 */
	public boolean isTimeOuted() {
		return keepAliveRetries > 0 ? false : true;
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

	@Override
	public String toString(){
		return "User connection {\n"
				+ "user: "+user.getUserName()+"\n"
				+ "isActive: "+isActive+"\n"
				+ "isTimeOut: "+isTimeOuted()+"\n"
				+ "isWaiting: "+isWaiting+"}";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result + (isWaiting ? 1231 : 1237);
		result = prime * result + keepAliveRetries;
		result = prime * result + ((messages == null) ? 0 : messages.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserConnection other = (UserConnection) obj;
		if (isActive != other.isActive)
			return false;
		if (isWaiting != other.isWaiting)
			return false;
		if (keepAliveRetries != other.keepAliveRetries)
			return false;
		if (messages == null) {
			if (other.messages != null)
				return false;
		} else if (!messages.equals(other.messages))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
}
