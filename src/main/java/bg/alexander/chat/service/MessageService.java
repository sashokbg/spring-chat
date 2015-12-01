package bg.alexander.chat.service;

import java.util.List;

import bg.alexander.chat.model.Message;
import bg.alexander.chat.model.User;

/**
 * 
 * @author Kirilov
 *	
 */
public interface MessageService {
	public Message readMessage(String userId);
	public boolean subscribe(User user);
	public void broadcastMessage(User fromUser,String message);
	public void keepAlive(String userId);
	public boolean isUserSubscribed(String userId);
	void postMessage(User fromUser, User toUser, String message);
	public User getSubscribedUser(String userId);
	public User getSubscribedUserByName(String userName);
	public List<User> getUserConnections();
}
