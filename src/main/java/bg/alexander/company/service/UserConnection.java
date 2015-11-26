package bg.alexander.company.service;

public interface UserConnection {
	public String readMessage();
	public boolean sendMessage(String message);
	public void keepAlive();
}
