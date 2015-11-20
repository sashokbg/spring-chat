package bg.alexander.company.service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {
	private final Logger log = LogManager.getLogger(MessageServiceImpl.class);
	private ConcurrentHashMap<String, ArrayBlockingQueue<String>> users;

	public MessageServiceImpl() {
		users = new ConcurrentHashMap<>();
	}
	
	@Override
	public void subscribe(String userId){
		users.put(userId, new ArrayBlockingQueue<>(30));
	}
	
	@Override
	@Async
	public void postMessage(String message){
		users.entrySet().stream().forEach(
			u -> { try{ u.getValue().put(message);}
			catch(Exception e){
				e.printStackTrace();
			}});
	}
	
	@Override
	public String readMessage(String userId) {
		try {
			String message = users.get(userId).take();
			log.info("Consuming a message ["+message+"] by user ["+userId+"]");
			return message;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
