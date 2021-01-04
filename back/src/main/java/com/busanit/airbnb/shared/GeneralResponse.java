package com.busanit.airbnb.shared;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GeneralResponse {
	
	private String message;
	
	public GeneralResponse(String message) {
		this.message = message;
	}
}
