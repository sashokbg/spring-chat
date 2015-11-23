package bg.alexander.company.service;

public interface MessageService {
	public String readMessage(String userId);
	public void postMessage(String message, String userId);
	public void subscribe(String userId, String userName);
	public void broadcastMessage(String message);
	public void keepAlive(String userId);
}
