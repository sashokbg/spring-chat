package bg.alexander.company.controllers;

import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import bg.alexander.company.service.MessageService;

@Controller
public class MessageController {
	private final Logger log = LogManager.getLogger(MessageController.class);
	
	@Autowired
	private MessageService messageService;
	
	@RequestMapping("/broadcast-message")
	public String broadcastMessage(String message){
		log.info("Broadcasting a message : "+message);
		messageService.broadcastMessage(message);
		return "redirect:messages-post";
	}
	
	@RequestMapping(name="post-message", method=RequestMethod.POST)
	public String postMessage(
			String message,
			String userId) {
		log.info("Posting a message : "+message+" to ["+userId+"]");
		messageService.postMessage(message, userId);
		
		return "redirect:messages-post";
	}
	
	@RequestMapping(name="post-message", method=RequestMethod.GET)
	public String postMessage() {
		log.info("Opened message post board");
		
		return "messages-post";
	}
	
	@RequestMapping("/subscribe")
	public @ResponseBody String subscribe(String userId){
		log.info("Subscribing user "+userId);
		messageService.subscribe(userId);
		
		return "OK";
	}
	
	@RequestMapping("/read-messages")
	public @ResponseBody DeferredResult<String> readMessages(String userId) {
		CompletableFuture<String> future = CompletableFuture
		.supplyAsync(()->messageService.readMessage(userId));
		
		log.info("Reading messages for user "+userId);
		DeferredResult<String> deferredResult = new DeferredResult<>(45000L);
		deferredResult.onTimeout(()-> {
			log.info("request expired, sending keep alive");
			deferredResult.setResult("");
			messageService.keepAlive(userId);
			future.cancel(true);
		});
		
		future.whenCompleteAsync((result, throwable) -> {
			deferredResult.setResult(result);
		});
		
		return deferredResult;
	}
	
	@RequestMapping("/messages")
	public String messages() {
		log.info("Messages page");
		
		return "messages";
	}
	
	@RequestMapping("/messages-post")
	public String subscribe() {
		log.info("Post Messages page");
		
		return "messages-post";
	}
	
}
