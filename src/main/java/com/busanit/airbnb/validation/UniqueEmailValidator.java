package com.busanit.airbnb.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.busanit.airbnb.user.User;
import com.busanit.airbnb.user.UserRepository;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String>{
	
	@Autowired
	UserRepository userRepository;

	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			return true;
		}
		return false;
	}

}
