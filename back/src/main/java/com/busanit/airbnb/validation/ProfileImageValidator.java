package com.busanit.airbnb.validation;

import java.util.Base64;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.busanit.airbnb.file.FileService;

public class ProfileImageValidator implements ConstraintValidator<ProfileImage, String> {

	@Autowired
	FileService fileService;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null)
			return true;

		byte[] decoded = Base64.getDecoder().decode(value);
		String type = fileService.detectType(decoded);

		if (type.equalsIgnoreCase("image/png") || type.equalsIgnoreCase("image/jpeg"))
			return true;
		
		return false;
	}

}
