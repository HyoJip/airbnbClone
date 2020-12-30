package com.busanit.airbnb.user;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.busanit.airbnb.shared.GeneralResponse;

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
	
}
