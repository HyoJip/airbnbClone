package com.busanit.airbnb.user;

import java.beans.Transient;
import java.util.Collection;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.busanit.airbnb.converter.UserStatusConverter;
import com.busanit.airbnb.enums.UserStatus;
import com.busanit.airbnb.validation.UniqueEmail;
import com.busanit.airbnb.validation.ValidEmail;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class User implements UserDetails{
	
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	private long id;
	
	@NotNull
	@ValidEmail
	@UniqueEmail
	private String email;
	
	@NotNull
	@Size(min = 2, max = 20)
	private String name;
	
	@NotNull
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*", message = "{airbnb.constraints.password.Pattern.message}")
	@Size(min = 8, max = 255)
	private String password;
	
	@Convert(converter = UserStatusConverter.class)
	private UserStatus status;
	
	private String profile;

	@Override
	@Transient
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.createAuthorityList("ROLE_USER");
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	@Transient
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	@Transient
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	@Transient
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	@Transient
	public boolean isEnabled() {
		return true;
	}
}
