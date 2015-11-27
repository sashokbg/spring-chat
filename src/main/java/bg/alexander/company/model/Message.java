package bg.alexander.company.model;

public class Message {
	public static Message KEEP_ALIVE_MESSAGE = new Message();;
	private String fromUser;
	private String message;
	
	public Message() {
		message = "";
	}
	
	public Message(String fromUser, String message) {
		this.fromUser = fromUser;
		this.message = message;
	}
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
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
