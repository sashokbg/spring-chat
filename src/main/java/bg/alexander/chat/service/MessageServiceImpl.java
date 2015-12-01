package bg.alexander.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import bg.alexander.chat.model.Message;
import bg.alexander.chat.model.User;
import bg.alexander.chat.model.UserConnection;

@Service
public class MessageServiceImpl implements MessageService {
	private final Logger log = LogManager.getLogger(MessageServiceImpl.class);
	private List<UserConnection> userConnections;
	
	public MessageServiceImpl() {
		userConnections = new ArrayList<>();
	}
	
	@Override
	public boolean subscribe(User user){
		UserConnection userCon = new UserConnection();
		if(userConnections.stream().filter((u)-> u.getUser().equals(user)).count() > 0){
			return false;
		}
		
		userCon.setUser(user);
		userConnections.add(userCon);
		return true;
	}
	
	@Override
	@Async
	public void broadcastMessage(User fromUser, String message){
		Message broadCastMessage = new Message(fromUser,message);
		userConnections.stream().forEach(
			(u) -> u.sendMessage(broadCastMessage)
		);
	}
	
	@Override
	@Async
	public void keepAlive(String userId){
		log.debug("Keeping alive user ["+userId+"]");
		UserConnection userCon = userConnections.stream().filter((u)-> u.getUser().getUserId().equals(userId)).findFirst().get();
		userCon.keepAlive();
		if(!userCon.isActive()){
			log.info("User ["+userId+"] timeout. Disconnecting");
			userConnections.remove(userCon);
		}
	}
	
	@Override
	@Async
	public void postMessage(User fromUser, User toUser, String message){
		Message messageToSend = new Message(fromUser, message);
		userConnections.stream().filter(
			(u)-> u.getUser().equals(toUser)).findFirst().get().sendMessage(messageToSend);
	}
	
	@Override
	public Message readMessage(String userId) {
		Message message = userConnections.stream().filter((u)-> u.getUser().getUserId().equals(userId)).findFirst().get().readMessage();
		log.debug("Consuming a message ["+message+"] by user ["+userId+"]");
		return message;
	}

	@Override
	public boolean isUserSubscribed(String userId) {
		if(userConnections.stream().filter((u)-> u.getUser().getUserId().equals(userId)).count()<1)
			return false;
		return true;
	}
	
	@Override
	public User getSubscribedUserByName(String userName) {
		User user = null;
		try{
			user = userConnections.stream().filter((u)-> u.getUser().getUserName().equals(userName)).findFirst().get().getUser();
		}catch(NoSuchElementException e){
			log.debug("No user registered for name ["+userName+"]");
		}
		return user;
	}
	
	@Override
	public User getSubscribedUser(String userId) {
		User user= null;;
		try{
			user = userConnections.stream().filter((u)-> u.getUser().getUserId().equals(userId)).findFirst().get().getUser();
		}catch(NoSuchElementException e){
			log.debug("No user registered for id "+userId);
		}
		return user;
	}

	@Override
	public List<UserConnection> getUserConnections() {
		// TODO finish this
		return null;
	}
}
