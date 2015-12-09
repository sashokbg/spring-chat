package bg.alexander.chat.controllers;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import bg.alexander.chat.model.Message;
import bg.alexander.chat.model.User;
import bg.alexander.chat.model.UserConnection;
import bg.alexander.chat.service.MessageService;

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
	
	@Autowired
	private HttpServletResponse response;
	
	@Autowired
	private HttpServletRequest request;
	
	@RequestMapping(path="broadcast-message", method=RequestMethod.POST)
	public @ResponseBody String broadcastMessage(String message){
		String userId = request.getSession().getId();
		User fromUser = messageService.getSubscribedUser(userId);
		
		log.info("Broadcasting a message : "+message+" from user ["+fromUser+"]");
		messageService.broadcastMessage(fromUser, message);
		return "OK";
	}
	
	@RequestMapping(path="post-message", method=RequestMethod.POST)
	public @ResponseBody String postMessage(String message, @ModelAttribute("toUser") User toUser) {
		String userId = request.getSession().getId();
		User fromUser = messageService.getSubscribedUser(userId);
		
		if(fromUser!=null){
			log.info("Posting a message : "+message+" to ["+toUser+"]");
			messageService.postMessage(fromUser, toUser, message);
		}
		else{
			set403();
			return "KO";
		}
		
		return "OK";
	}
	
	@RequestMapping("connected-users")
	public @ResponseBody List<User> getConnectedUsers(){
		log.debug("Getting connected users");
		return messageService.getUserConnections();
	}
	
	@RequestMapping("subscribe")
	public @ResponseBody String subscribe(@Valid User user, BindingResult bs){
		if(bs.hasErrors()){
			set403();
			log.error("Subscribing failed - Cannot convert user name");
			return "KO";
		}
		String userId = request.getSession().getId();
		user.setUserId(userId);
		log.info("Subscribing user "+user.getUserName()+" with id "+userId);
		boolean result = false;
		result = messageService.subscribe(user);
		if(!result){
			log.error("Subscribing failed - User exists");
			set403();
		}
		
		return result ? "OK" : "KO";
	}

	@RequestMapping("read-messages")
	public @ResponseBody DeferredResult<Message> readMessages() {
		String userId = request.getSession().getId();
		if(!messageService.isUserSubscribed(userId)){
			try {
				response.sendError(408);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		CompletableFuture<Message> future = CompletableFuture
		.supplyAsync(()->messageService.readMessage(userId));
		
		log.info("Reading messages for user "+userId);
		DeferredResult<Message> deferredResult = new DeferredResult<>(7000L);
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
	
	@RequestMapping("messages")
	public String messages() {
		log.info("Messages page");
		
		return "messages";
	}
	
	private void set403() {
		try {
			response.sendError(403);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
