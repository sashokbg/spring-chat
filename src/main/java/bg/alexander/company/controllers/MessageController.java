package bg.alexander.company.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

@Controller
public class MessageController {
	private final Logger log = LogManager.getLogger(MessageController.class);
	@RequestMapping("/response-body")
	public @ResponseBody DeferredResult<String> callable() {
		log.info("Called async method");
		DeferredResult<String> deferredResult = new DeferredResult<>();
		
//		CompletableFuture.supplyAsync(null).whenComplete(action);
		
		return deferredResult;
	}
}
