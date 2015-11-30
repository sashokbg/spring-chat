package bg.alexander.company.controllers;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import bg.alexander.company.model.Message;
import bg.alexander.company.service.MessageService;

/**
 * 
 * Spring controller assuring the message sending, subscribing and use connection </br >
 * A user must first subscribe to the service and then invoke readMessages method
 * 
 * @see Message
 * @see UserConnection
 * @see MessageService
 * 
 * @author Kirilov
 *
 */
@Controller
public class MessageController {
	private final Logger log = LogManager.getLogger(MessageController.class);
	
	@Autowired
	private MessageService messageService;
	
	@RequestMapping("/broadcast-message")
	public String broadcastMessage(String message, HttpServletRequest request){
		String userId = request.getSession().getId();
		String fromUser = messageService.getSubscribedUser(userId);
		
		log.info("Broadcasting a message : "+message);
		messageService.broadcastMessage(fromUser, message);
		return "redirect:messages-post";
	}
	
	@RequestMapping(name="post-message", method=RequestMethod.POST)
	public String postMessage(String message,String toUser, HttpServletRequest request) {
		String userId = request.getSession().getId();
		String fromUser = messageService.getSubscribedUser(userId);
		
		log.info("Posting a message : "+message+" to ["+toUser+"]");
		messageService.postMessage(fromUser, toUser, message);
		
		return "redirect:messages-post";
	}
	
	@RequestMapping(name="post-message", method=RequestMethod.GET)
	public String postMessage() {
		log.info("Opened message post board");
		
		return "messages-post";
	}
	
	@RequestMapping("/subscribe")
	public @ResponseBody String subscribe(String userName, HttpServletRequest request){
		String userId = request.getSession().getId();
		log.info("Subscribing user "+userName+" with id "+userId);
		boolean result = false;
		result = messageService.subscribe(userId, userName);
		
		return result ? "OK" : "NOK";
	}
	
	@RequestMapping("/read-messages")
	public @ResponseBody DeferredResult<Message> readMessages(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getSession().getId();
		if(!messageService.isUserSubscribed(userId)){
			try {
				response.sendError(403);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		CompletableFuture<Message> future = CompletableFuture
		.supplyAsync(()->messageService.readMessage(userId));
		
		log.info("Reading messages for user "+userId);
		DeferredResult<Message> deferredResult = new DeferredResult<>(45000L);
		deferredResult.onTimeout(()-> {
			log.info("request expired, sending keep alive");
			messageService.keepAlive(userId);
			deferredResult.setResult(null);
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
