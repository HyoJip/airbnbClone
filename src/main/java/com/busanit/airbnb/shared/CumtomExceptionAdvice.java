package com.busanit.airbnb.shared;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CumtomExceptionAdvice {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleError(HttpServletRequest request, MethodArgumentNotValidException exception) {
		
		String url =  request.getServletPath();
		ApiError apiError = new ApiError(400, "유효하지 않은 데이터", url);
		Map<String, String> validationErrors = new HashMap<>();
		
		List<FieldError> errors = exception.getFieldErrors();
		for (FieldError error : errors) {
			validationErrors.put(error.getField(), error.getDefaultMessage());
		}
		apiError.setValidationErrors(validationErrors);
		
		return apiError;
	}
}
