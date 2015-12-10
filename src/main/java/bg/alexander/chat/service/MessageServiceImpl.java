package bg.alexander.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
		if(!userCon.isActive()){
			log.info("User ["+userId+"] timeout. Disconnecting");
			//send one last close message in order to end last reading thread
			userCon.sendMessage(Message.CLOSE);
			userConnections.remove(userCon);
		}
		else{
			userCon.keepAlive();
			//Wait some time and if user is still inactive, it means that the next keep alive was not consumed - closed browser
			Thread t = new Thread(
				()-> {
					log.debug("Running keep alive timeout wait");
					try {
						Thread.sleep(5000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					//if the connection is timed out, it should be closed elsewhere
					log.debug(userCon);
					if(!userCon.isActive() && !userCon.isTimeOuted()){
						log.debug("Disconnecting "+userCon+". Keep alive not consumed withing time period");
						userConnections.remove(userCon);
					}
					else{
						log.debug("Keep alive was consumed");
					}
				});
			t.start();
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
		UserConnection connection = null;
		connection = userConnections.stream().filter((u)-> u.getUser().getUserId().equals(userId)).findFirst().orElse(null);
		
		if(connection == null)
			return null;
		
		Message message = connection.readMessage();
		log.debug("Consuming a message ["+message+"] by user ["+userId+"]");
		return message;
	}

	@Override
	public boolean isUserSubscribed(String userId) {
		if(userConnections.stream().filter((u)-> {
				return u.getUser().getUserId().equals(userId) && !u.isTimeOuted();
			}).count()<1){
			
			return false;
		}
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
	public List<User> getUserConnections() {
		List<User> connectedUsers = new ArrayList<>();
		userConnections.stream().filter((u)->u.isActive()).collect(Collectors.toList()).stream().
			forEach((e)->connectedUsers.add(e.getUser()));
		return connectedUsers;
	}
}
