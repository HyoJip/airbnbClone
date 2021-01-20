package com.busanit.airbnb.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.busanit.airbnb.file.FileService;
import com.busanit.airbnb.shared.NotFoundException;
import com.busanit.airbnb.user.vm.UserUpdateVM;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final FileService fileService;
	
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, FileService fileService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.fileService = fileService;
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
		if (userUpdate.getProfile() != null) {
			String randomFileName = fileService.saveProfileImage(userUpdate.getProfile());
			fileService.deleteProfileImage(randomFileName);
			user.setProfile(randomFileName);
		}
		return userRepository.save(user);
	}
}
