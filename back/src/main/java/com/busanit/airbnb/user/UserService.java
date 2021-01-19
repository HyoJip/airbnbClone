package com.busanit.airbnb.user;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.busanit.airbnb.shared.NotFoundException;
import com.busanit.airbnb.user.vm.UserUpdateVM;

@Service
public class UserService {

	private final UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	public User save(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		return userRepository.save(user);
	}

	public User findById(long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("id:" + id + " 일치하는 회원이 없습니다"));
	}

	public User update(long id, UserUpdateVM userUpdate) {
		User user = userRepository.getOne(id);
		user.setName(userUpdate.getName());
		user.setPassword(userUpdate.getPassword());
		String randomFileName = UUID.randomUUID().toString().replaceAll("-", "");
		user.setProfile(randomFileName);
		return userRepository.save(user);
	}
}
