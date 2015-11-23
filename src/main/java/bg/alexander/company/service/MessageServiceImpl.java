package bg.alexander.company.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {
	private final Logger log = LogManager.getLogger(MessageServiceImpl.class);
	private List<UserConnection> userConnections;

	public MessageServiceImpl() {
		userConnections = new ArrayList<>();
	}
	
	@Override
	public void subscribe(String userId, String userName){
		UserConnection userCon = new UserConnection();
		userCon.setUserId(userId);
		userCon.setUserName(userName);
		
		userConnections.add(userCon);
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
		userConnections.stream().filter((u)-> u.getUserId().equals(userId)).forEach(
			(u)-> u.sendMessage("")
		);
	}
	
	@Override
	@Async
	public void postMessage(String message, String userId){
		userConnections.stream().filter((u)-> u.getUserId().equals(userId)).forEach(
			(u)-> u.sendMessage(message)
		);
	}
	
	@Override
	public String readMessage(String userId) {
		String message = userConnections.stream().filter((u)-> u.getUserId().equals(userId)).findFirst().get().readMessage();
		log.info("Consuming a message ["+message+"] by user ["+userId+"]");
		return message;
	}
}
