package com.busanit.airbnb;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.busanit.airbnb.user.UserRepository;
import com.busanit.airbnb.user.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class LoginControllerTest {

	private static final String API_1_0_LOGIN = "/api/1.0/login";

	@Autowired
	TestRestTemplate testRestTemplate;
	
	@Autowired
	UserService userservice;
	
	@Autowired
	UserRepository userRepository;
	
	@BeforeEach
	void cleanup() {
		testRestTemplate.getRestTemplate().getInterceptors().clear();
		userRepository.deleteAll();
	}
	
	@Test @DisplayName("[로그인] 인증없이, 401 받음")
	void postLogin_whenRequestWithoutAuth_receiveUnauthorized() {
		ResponseEntity<Object> response = login(Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	private <T> ResponseEntity<T> login(Class<T> responseType) {
		return testRestTemplate.postForEntity(API_1_0_LOGIN, null, responseType);
	}
}
