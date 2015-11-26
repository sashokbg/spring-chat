package bg.alexander.company.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {
	private final Logger log = LogManager.getLogger(MessageServiceImpl.class);
	private List<UserConnectionImpl> userConnections;

	@Value("${user.connection.keepalive.retries}")
	private String test;
	
	public MessageServiceImpl() {
		userConnections = new ArrayList<>();
	}
	
	@Override
	public boolean subscribe(String userId, String userName){
		UserConnectionImpl userCon = new UserConnectionImpl();
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
	public void broadcastMessage(String message){
		userConnections.stream().forEach(
			(u) -> u.sendMessage(message)
		);
	}
	
	@Override
	@Async
	public void keepAlive(String userId){
		log.info("Keeping alive user ["+userId+"]");
		UserConnectionImpl userCon = userConnections.stream().filter((u)-> u.getUserId().equals(userId)).findFirst().get();
		userCon.keepAlive();
		if(!userCon.isActive()){
			log.info("User ["+userId+"] timeout. Disconnecting");
			userConnections.remove(userCon);
		}
	}
	
	@Override
	@Async
	public void postMessage(String message, String userName){
		userConnections.stream().filter(
			(u)-> u.getUserName().equals(userName)).findFirst().get().sendMessage(message);
	}
	
	@Override
	public String readMessage(String userId) {
		String message = userConnections.stream().filter((u)-> u.getUserId().equals(userId)).findFirst().get().readMessage();
		log.info("Consuming a message ["+message+"] by user ["+userId+"]");
		return message;
	}

	@Override
	public boolean isUserSubscribed(String userId) {
		if(userConnections.stream().filter((u)-> u.getUserId().equals(userId)).count()<1)
			return false;
		return true;
	}
}
