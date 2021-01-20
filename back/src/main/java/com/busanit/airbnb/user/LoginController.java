package com.busanit.airbnb.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.busanit.airbnb.user.vm.UserVM;

@RestController
@RequestMapping("/api/1.0")
public class LoginController {
	

	@PostMapping("/login")
	public UserVM login(@AuthenticationPrincipal User user) {
		return new UserVM(user);
	}
	
}
