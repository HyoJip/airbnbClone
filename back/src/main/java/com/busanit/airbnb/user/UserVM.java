package com.busanit.airbnb.user;

import com.busanit.airbnb.enums.UserStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserVM {
	private long id;
	private String email;
	private String name;
	private UserStatus status;
	private String profile;
	
	public UserVM(User user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.name = user.getName();
		this.status = user.getStatus();
		this.profile = user.getProfile();
	}
}
