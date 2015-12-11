package bg.alexander.chat.model;

public class SystemMessage<T> extends Message{
	private T payload;
	private MessageType type;
	public enum MessageType{
		USER_LOGIN("USR_LOG");
		private String message;
		
		private MessageType(String message) {
			this.message = message;
		}
		
		public String getMessage(){
			return this.message;
		}
	}
	
	public SystemMessage(MessageType type, T payload) {
		super.setMessage(type.getMessage());
		this.setType(type);
		this.payload = payload;
	}

	public T getPayload() {
		return payload;
	}

	public void setPayload(T payload) {
		this.payload = payload;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}
}
