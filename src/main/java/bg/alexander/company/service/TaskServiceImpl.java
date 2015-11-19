package bg.alexander.company.service;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {
	private final Logger log = LogManager.getLogger(TaskServiceImpl.class);
	private ArrayBlockingQueue<String> queue;

	public TaskServiceImpl() {
		queue = new ArrayBlockingQueue<>(30);
	}
	
	@Override
	public void postMessage(String message){
		try {
			queue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String execute() {
		log.info("Consuming a message");
		try {
			return queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
