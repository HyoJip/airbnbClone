package com.busanit.airbnb.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = ProfileImageValidator.class)
public @interface ProfileImage {
	String message() default "{airbnb.constraints.profile.ProfileImage.message}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}