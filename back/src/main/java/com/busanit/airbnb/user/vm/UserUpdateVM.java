package com.busanit.airbnb.user.vm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.busanit.airbnb.validation.ProfileImage;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateVM {
	
	@NotNull
	@Size(min = 2, max = 20)
	private String name;
	
	@NotNull
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*", message = "{airbnb.constraints.password.Pattern.message}")
	@Size(min = 8, max = 255)
	private String password;
	
	@ProfileImage
	private String profile;
}
