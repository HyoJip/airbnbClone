package com.busanit.airbnb.shared;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
