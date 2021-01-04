package com.busanit.airbnb.component;

import com.busanit.airbnb.user.User;

public class TestUtil {
	
	public static User createValidUser() {
		User user = new User();
		user.setEmail("test@naver.com");
		user.setName("test-user");
		user.setPassword("P4ssword");
		user.setProfile("profile.png");
		return user;
	}
}
