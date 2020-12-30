package com.busanit.airbnb.user;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.busanit.airbnb.converter.UserStatusConverter;
import com.busanit.airbnb.enums.UserStatus;
import com.busanit.airbnb.validation.UniqueEmail;
import com.busanit.airbnb.validation.ValidEmail;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class User {
	
	@Id @GeneratedValue
	private long id;
	
	@NotNull
	@ValidEmail
	@UniqueEmail
	private String email;
	
	@NotNull
	@Size(min = 4, max = 20)
	private String username;
	
	@NotNull
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*", message = "{airbnb.constraints.password.Pattern.message}")
	@Size(min = 8, max = 255)
	private String password;
	
	@Convert(converter = UserStatusConverter.class)
	private UserStatus status;
	
	private String profile;
}
