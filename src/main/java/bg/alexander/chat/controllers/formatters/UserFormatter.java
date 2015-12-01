package bg.alexander.chat.controllers.formatters;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import bg.alexander.chat.model.User;
import bg.alexander.chat.service.MessageService;

@Component
public class UserFormatter implements Formatter<User>{

	@Autowired
	private MessageService messageService;
	
	@Override
	public String print(User user, Locale locale) {
		return user.toString();
	}

	@Override
	public User parse(String userNameOrId, Locale locale) throws ParseException {
		User user = messageService.getSubscribedUser(userNameOrId);
		if(user!=null){
			return user;
		}
		user = messageService.getSubscribedUserByName(userNameOrId);
		
		return user;
	}

}
