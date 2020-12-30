package com.busanit.airbnb.enums;

public enum UserStatus {
	GUEST("G"),
	HOST("H");
	
	private String code;
	
	UserStatus(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
