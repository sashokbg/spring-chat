package bg.alexander.company.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import bg.alexander.company.model.Message;
import bg.alexander.company.model.UserConnection;

@Service
public class MessageServiceImpl implements MessageService {
	private final Logger log = LogManager.getLogger(MessageServiceImpl.class);
	private List<UserConnection> userConnections;

	@Value("${user.connection.keepalive.retries}")
	private String test;
	
	public MessageServiceImpl() {
		userConnections = new ArrayList<>();
	}
	
	@Override
	public boolean subscribe(String userId, String userName){
		UserConnection userCon = new UserConnection();
		userCon.setUserId(userId);
		userCon.setUserName(userName);
		if(userConnections.stream().filter((u)-> u.getUserName().equals(userName)).count() > 0){
			return false;
		}
		
		userConnections.add(userCon);
		return true;
	}
	
	@Override
	@Async
	public void broadcastMessage(String fromUser, String message){
		Message broadCastMessage = new Message(fromUser,message);
		userConnections.stream().forEach(
			(u) -> u.sendMessage(broadCastMessage)
		);
	}
	
	@Override
	@Async
	public void keepAlive(String userId){
		log.debug("Keeping alive user ["+userId+"]");
		UserConnection userCon = userConnections.stream().filter((u)-> u.getUserId().equals(userId)).findFirst().get();
		userCon.keepAlive();
		if(!userCon.isActive()){
			log.info("User ["+userId+"] timeout. Disconnecting");
			userConnections.remove(userCon);
		}
	}
	
	@Override
	@Async
	public void postMessage(String fromUser, String toUser, String message){
		Message messageToSend = new Message(fromUser, message);
		userConnections.stream().filter(
			(u)-> u.getUserName().equals(toUser)).findFirst().get().sendMessage(messageToSend);
	}
	
	@Override
	public Message readMessage(String userId) {
		Message message = userConnections.stream().filter((u)-> u.getUserId().equals(userId)).findFirst().get().readMessage();
		log.debug("Consuming a message ["+message+"] by user ["+userId+"]");
		return message;
	}

	@Override
	public boolean isUserSubscribed(String userId) {
		if(userConnections.stream().filter((u)-> u.getUserId().equals(userId)).count()<1)
			return false;
		return true;
	}
	
	@Override
	public String getSubscribedUser(String userId) {
		String userName;
		try{
			userName = userConnections.stream().filter((u)-> u.getUserId().equals(userId)).findFirst().get().getUserName();
		}catch(NoSuchElementException e){
			log.error("No user registered for id "+userId);
			userName = null;
		}
		return userName;
	}
}
