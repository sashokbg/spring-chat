package bg.alexander.chat.controllers.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;

import bg.alexander.chat.model.User;

public class UserValidator implements ConstraintValidator<ValidUser,User> {

	@Override
	public void initialize(ValidUser constraintAnnotation) {
		
	}

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context) {
		if(StringUtils.isEmpty(user.getUserName()))
			return false;
		return true;
	}

}
