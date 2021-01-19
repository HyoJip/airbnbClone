package com.busanit.airbnb.user.vm;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateVM {
	
	private String name;
	private String password;
	private String profile;
}
