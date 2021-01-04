package com.busanit.airbnb;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.busanit.airbnb.component.TestUtil;
import com.busanit.airbnb.enums.UserStatus;
import com.busanit.airbnb.shared.ApiError;
import com.busanit.airbnb.user.User;
import com.busanit.airbnb.user.UserRepository;
import com.busanit.airbnb.user.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
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
	
	@Test @DisplayName("[로그인] 인증 없이, 401 받음")
	void postLogin_whenRequestWithoutAuth_receiveUnauthorized() {
		ResponseEntity<Object> response = login(Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}
	@Test @DisplayName("[로그인] 인증 실패, 401 받음")
	void postLogin_whenCredentialIsNotValid_receiveUnauthorized() {
		authenticate();
		ResponseEntity<Object> response = login(Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}
	@Test @DisplayName("[로그인] 인증 없이, ApiError 받음")
	void postLogin_whenRequestWithoutAuth_receiveApiError() {
		ResponseEntity<ApiError> response = login(ApiError.class);
		
		assertThat(response.getBody().getUrl()).contains(API_1_0_LOGIN);
	}
	@Test @DisplayName("[로그인] 인증 없이, validationErrors 받지 않음")
	void postLogin_whenRequestWithoutAuth_receiveValidationErrors() {
		ResponseEntity<String> response = login(String.class);
		
		assertThat(response.getBody().contains("validationErrors")).isFalse();
	}
	@Test @DisplayName("[로그인] 인증 실패, WWW-인증헤더 받지 않음")
	void postLogin_whenCredentialIsNotValid_notReceiveWWWAuthenticate() {
		authenticate();
		ResponseEntity<Object> response = login(Object.class);
		
		assertThat(response.getHeaders().containsKey("WWW-Authenticate")).isFalse();
	}
	@Test @DisplayName("[로그인] 인증 성공, 200 받음")
	void postLogin_whenCredentialIsValid_receiveOk() {
		userservice.save(TestUtil.createValidUser());
		authenticate();
		
		ResponseEntity<Object> response = login(Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	@Test @DisplayName("[로그인] 인증 성공, 회원 이름 받음")
	void postLogin_whenCredentialIsValid_receiveUserName() {
		User user = TestUtil.createValidUser();
		userservice.save(user);
		authenticate();
		
		ResponseEntity<User> response = login(User.class);
		
		assertThat(response.getBody().getName()).isEqualTo(user.getName());
	}
	@Test @DisplayName("[로그인] 인증 성공, 회원 이메일 받음")
	void postLogin_whenCredentialIsValid_receiveUserEmail() {
		User user = TestUtil.createValidUser();
		userservice.save(user);
		authenticate();
		
		ResponseEntity<User> response = login(User.class);
		
		assertThat(response.getBody().getEmail()).isEqualTo(user.getEmail());
	}
	@Test @DisplayName("[로그인] 인증 성공, 회원  ID 받음")
	void postLogin_whenCredentialIsValid_receiveUserId() {
		User user = TestUtil.createValidUser();
		userservice.save(user);
		authenticate();
		
		ResponseEntity<User> response = login(User.class);
		
		assertThat(response.getBody().getId()).isEqualTo(user.getId());
	}
	@Test @DisplayName("[로그인] 인증 성공, 회원  상태 받음")
	void postLogin_whenCredentialIsValid_receiveUserStatus() {
		User user = TestUtil.createValidUser();
		user.setStatus(UserStatus.HOST);
		userservice.save(user);
		authenticate();
		
		ResponseEntity<User> response = login(User.class);
		
		assertThat(response.getBody().getStatus()).isEqualTo(user.getStatus());
	}
	@Test @DisplayName("[로그인] 인증 성공, 회원  프로필 사진 받음")
	void postLogin_whenCredentialIsValid_receiveUserProfile() {
		User user = TestUtil.createValidUser();
		userservice.save(user);
		authenticate();
		
		ResponseEntity<User> response = login(User.class);
		
		assertThat(response.getBody().getProfile()).isEqualTo(user.getProfile());
	}
	@Test @DisplayName("[로그인] 인증 성공, 회원  비밀번호 받지 않음")
	void postLogin_whenCredentialIsValid_notReceiveUserPassword() {
		User user = TestUtil.createValidUser();
		userservice.save(user);
		authenticate();
		
		ResponseEntity<String> response = login(String.class);
		
		assertThat(response.getBody().contains("password")).isFalse();
	}

	
	private void authenticate() {
		testRestTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor("test@naver.com", "P4ssword"));
	}
	private <T> ResponseEntity<T> login(Class<T> responseType) {
		return testRestTemplate.postForEntity(API_1_0_LOGIN, null, responseType);
	}
}
