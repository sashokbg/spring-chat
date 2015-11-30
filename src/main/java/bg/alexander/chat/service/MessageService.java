package bg.alexander.chat.service;

import bg.alexander.chat.model.Message;

/**
 * 
 * @author Kirilov
 *
 */
public interface MessageService {
	public Message readMessage(String userId);
	public boolean subscribe(String userId, String userName);
	public void broadcastMessage(String userId,String message);
	public void keepAlive(String userId);
	public boolean isUserSubscribed(String userId);
	public void postMessage(String fromUser, String toUser, String message);
	public String getSubscribedUser(String userId);
}
