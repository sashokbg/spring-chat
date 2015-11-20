package bg.alexander.company.service;

public interface MessageService {
	public String readMessage(String userId);
	public void postMessage(String message, String userId);
	public void subscribe(String userId);
	public void broadcastMessage(String message);
}
