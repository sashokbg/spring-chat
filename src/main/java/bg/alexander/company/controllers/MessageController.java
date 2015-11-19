package bg.alexander.company.controllers;

import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import bg.alexander.company.service.TaskService;

@Controller
public class MessageController {
	private final Logger log = LogManager.getLogger(MessageController.class);
	
	@Autowired
	private TaskService taskService;
	
	@RequestMapping("/post-message")
	public @ResponseBody String postMessage(@RequestParam(name="message") String message) {
		log.info("Posting a message : "+message);
		taskService.postMessage(message);
		
		return "posted "+message;
	}
	
	@RequestMapping("/read-messages")
	public @ResponseBody DeferredResult<String> readMessages() {
		log.info("Reading messages");
		DeferredResult<String> deferredResult = new DeferredResult<>();
		
		CompletableFuture
		.supplyAsync(taskService::execute)
		.whenCompleteAsync((result, throwable) -> deferredResult.setResult(result))
		;
		
		return deferredResult;
	}
	
	@RequestMapping("/subscribe")
	public String subscribe() {
		log.info("Subscribing");
		
		return "subscribe";
	}
	
}
