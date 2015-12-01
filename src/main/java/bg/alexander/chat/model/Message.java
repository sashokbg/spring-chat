package bg.alexander.chat.model;

/**
 * Bean representing a message sent through a user connection 
 * 
 * @see UserConnection
 * @author Kirilov
 *
 */
public class Message {
	public static Message EMPTY = new Message();
	private User fromUser;
	private String message;
	
	public Message() {
		message = "";
		fromUser= new User();
	}
	
	public Message(User fromUser, String message) {
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
	
	@Override
	public String toString(){
		return "["+fromUser+"] "+message;
	}
}
