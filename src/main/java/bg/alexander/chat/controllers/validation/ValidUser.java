package bg.alexander.chat.controllers.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=UserValidator.class)
public @interface ValidUser {
	String message() default "{com.mycompany.constraints.checkcase}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
