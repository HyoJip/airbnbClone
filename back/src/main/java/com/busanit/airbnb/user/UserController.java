package com.busanit.airbnb.user;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.busanit.airbnb.shared.GeneralResponse;
import com.busanit.airbnb.user.vm.UserUpdateVM;
import com.busanit.airbnb.user.vm.UserVM;

@RestController
@RequestMapping("/api/1.0")
public class UserController {
	
	@Autowired
	UserService userService;

	@PostMapping("/users")
	public GeneralResponse signup(@Valid @RequestBody User user) {
		userService.save(user);
		return new GeneralResponse("회원 가입 성공");
	}
	
	@GetMapping("/users/{id:[\\d]+}")
	public UserVM lookup(@PathVariable long id) {
		User user = userService.findById(id);
		return new UserVM(user);
	}
	
	@PutMapping("/users/{id:[\\d]+}")
	@PreAuthorize("#id == principal.id")
	public UserVM update(@PathVariable long id, @RequestBody(required = false) UserUpdateVM userUpdate) {
		User user = userService.update(id, userUpdate);
		return new UserVM(user);
	}
}
