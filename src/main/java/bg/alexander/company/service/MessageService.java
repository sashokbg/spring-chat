package bg.alexander.company.service;

public interface MessageService {
	public String readMessage(String userId);
	public void postMessage(String message);
	public void subscribe(String userId);
}
