package com.busanit.airbnb.user;

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
