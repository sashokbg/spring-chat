package bg.alexander.company.service;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {
	private final Logger log = LogManager.getLogger(TaskServiceImpl.class);
	private ArrayBlockingQueue<String> queue;

	public TaskServiceImpl() {
		queue = new ArrayBlockingQueue<>(30);
	}
	
	@Override
	@Async
	public void postMessage(String message){
		try {
			queue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
//	@Override
//	public String executeNoBlock() {
//		log.info("Timeout - Consuming a message");
//		try {
//			if(queue.isEmpty())
//				return "e";
//			return queue.take();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	@Override
	public String execute() {
		try {
			String message = queue.take();
			log.info("Consuming a message ["+message+"]");
			return message;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
