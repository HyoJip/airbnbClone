package com.busanit.airbnb.shared;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(value = Include.NON_EMPTY)
public class ApiError {
	
	private int status;
	private String message;
	private String url;
	private Map<String, String> validationErrors;
	
	public ApiError(int status, String message, String url) {
		this.status = status;
		this.message = message;
		this.url = url;
	}
}
