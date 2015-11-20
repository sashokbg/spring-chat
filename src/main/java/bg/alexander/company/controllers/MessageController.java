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

import bg.alexander.company.service.MessageService;

@Controller
public class MessageController {
	private final Logger log = LogManager.getLogger(MessageController.class);
	
	@Autowired
	private MessageService messageService;
	
	@RequestMapping("/post-message")
	public @ResponseBody String postMessage(@RequestParam(name="message") String message) {
		log.info("Posting a message : "+message);
		messageService.postMessage(message);
		
		return "posted "+message;
	}
	
	@RequestMapping("/subscribe")
	public @ResponseBody DeferredResult<String> readMessages(String userId) {
		messageService.subscribe(userId);
		
		CompletableFuture<String> future = CompletableFuture
		.supplyAsync(()->messageService.readMessage(userId));
		
		log.info("Reading messages for user "+userId);
		DeferredResult<String> deferredResult = new DeferredResult<>(45000L);
		deferredResult.onTimeout(()-> {
			log.info("request expired, sending keep alive");
			deferredResult.setResult("");
			messageService.postMessage("");
			future.cancel(true);
		});
		
		future.whenCompleteAsync((result, throwable) -> {
			deferredResult.setResult(result);
		});
		
		return deferredResult;
	}
	
	@RequestMapping("/messages")
	public String subscribe() {
		log.info("Messages page");
		
		return "messages";
	}
	
}
