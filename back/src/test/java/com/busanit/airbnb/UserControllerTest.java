package com.busanit.airbnb;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.busanit.airbnb.component.TestUtil;
import com.busanit.airbnb.enums.UserStatus;
import com.busanit.airbnb.shared.ApiError;
import com.busanit.airbnb.shared.GeneralResponse;
import com.busanit.airbnb.user.User;
import com.busanit.airbnb.user.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class UserControllerTest {
	private static final String API_1_0_USERS = "/api/1.0/users";
	
	@Autowired
	TestRestTemplate testRestTemplate;
	
	@Autowired
	UserRepository userRepository;
	
	@BeforeEach
	void cleanup() {
		userRepository.deleteAll();
	}

	@Test @DisplayName("[회원가입] 유효한 회원의 경우, 200 받음")
	void postUser_whenUserIsValid_receiveOk() {
		User user = TestUtil.createValidUser();
		
		ResponseEntity<Object> response = postUser(user, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	@Test @DisplayName("[회원가입] 유효한 회원의 경우, GeneralResponse 받음")
	void postUser_whenUserIsValid_receiveGeneralResponse() {
		User user = TestUtil.createValidUser();
		
		ResponseEntity<GeneralResponse> response = postUser(user, GeneralResponse.class);
		
		assertThat(response.getBody().getMessage()).isEqualTo("회원 가입 성공");
	}
	@Test @DisplayName("[회원가입] 유효한 회원의 경우, DB에 저장됨")
	void postUser_whenUserIsValid_savedUserInDB() {
		User user = TestUtil.createValidUser();
		
		postUser(user, Object.class);
		
		assertThat(userRepository.count()).isEqualTo(1);
	}
	@Test @DisplayName("[회원가입] 유효한 회원의 경우, 비밀번호 암호화 후 저장")
	void postUser_whenUserIsValid_savedPasswordHashedUserInDB() {
		User user = TestUtil.createValidUser();
		
		postUser(user, Object.class);
		
		User inDB = userRepository.findAll().get(0);
		assertThat(inDB.getPassword()).isNotEqualTo(user.getPassword());
	}
	@Test @DisplayName("[회원가입] 이메일 없이, 400 받음")
	void postUser_whenSendsRequestWithoutEmail_receiveBadRequest() {
		User user = TestUtil.createValidUser();
		
		user.setEmail(null);
		ResponseEntity<Object> response = postUser(user, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원가입] 이름 없이, 400 받음")
	void postUser_whenSendsRequestWithoutUsername_receiveBadRequest() {
		User user = TestUtil.createValidUser();
		
		user.setName(null);
		ResponseEntity<Object> response = postUser(user, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원가입] 비밀번호 없이, 400 받음")
	void postUser_whenSendsRequestWithoutPassword_receiveBadRequest() {
		User user = TestUtil.createValidUser();
		
		user.setPassword(null);
		ResponseEntity<Object> response = postUser(user, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원가입] 유효한 회원, 기본 게스트 상태")
	void postUser_whenSendsRequestWithoutPassword_userStatusIsGuest() {
		User user = TestUtil.createValidUser();
		
		postUser(user, Object.class);
		User inDB = userRepository.findAll().get(0);
		
		assertThat(inDB.getStatus()).isEqualTo(UserStatus.GUEST);
	}
	@Test @DisplayName("[회원가입] 맞지 않은 이메일 형식, 400 받음")
	void postUser_whenEmailIsNotValid_receiveBadRequest() {
		User user = TestUtil.createValidUser();
		
		user.setEmail("goo@goog");
		ResponseEntity<Object> response = postUser(user, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원가입] name 2글자 미만, 400 받음")
	void postUser_whenUsernameLessThanRequired_receiveBadRequest() {
		User user = TestUtil.createValidUser();
		
		user.setName("a");
		ResponseEntity<Object> response = postUser(user, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원가입] password 8글자 미만, 400 받음")
	void postUser_whenPasswordLessThanRequired_receiveBadRequest() {
		User user = TestUtil.createValidUser();
		
		user.setPassword("abc");
		ResponseEntity<Object> response = postUser(user, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원가입] username 20글자 초과, 400 받음")
	void postUser_whenUsernameMoreThanRequired_receiveBadRequest() {
		User user = TestUtil.createValidUser();
		
		String moreThan20 = IntStream.rangeClosed(1, 21).mapToObj(i -> "a").collect(Collectors.joining());
		user.setName(moreThan20);
		ResponseEntity<Object> response = postUser(user, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원가입] password 255글자 초과, 400 받음")
	void postUser_whenPasswordMoreThanRequired_receiveBadRequest() {
		User user = TestUtil.createValidUser();
		
		String moreThan255 = IntStream.rangeClosed(1, 256).mapToObj(i -> "a").collect(Collectors.joining());
		user.setPassword("P4ssword" + moreThan255);
		ResponseEntity<Object> response = postUser(user, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원가입] password 소문자만, 400 받음")
	void postUser_whenPasswordOnlyLowercase_receiveBadRequest() {
		User user = TestUtil.createValidUser();
		
		user.setPassword("password");
		ResponseEntity<Object> response = postUser(user, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원가입] password 대문자만, 400 받음")
	void postUser_whenPasswordOnlyUppercase_receiveBadRequest() {
		User user = TestUtil.createValidUser();
		
		user.setPassword("PASSWORD");
		ResponseEntity<Object> response = postUser(user, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원가입] password 숫자만, 400 받음")
	void postUser_whenPasswordOnlyDemical_receiveBadRequest() {
		User user = TestUtil.createValidUser();
		
		user.setPassword("12345678");
		ResponseEntity<Object> response = postUser(user, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원가입] password 숫자만, APIError 받음")
	void postUser_whenPasswordOnlyDemical_receiveApiError() {
		User user = TestUtil.createValidUser();
		
		user.setPassword("12345678");
		ResponseEntity<ApiError> response = postUser(user, ApiError.class);
		
		assertThat(response.getBody().getUrl()).contains(API_1_0_USERS);
	}
	@Test @DisplayName("[회원가입] 오류 여러개일 때, ValidationErrors으로 받음")
	void postUser_whenHavingManyErrors_receiveApiErrorWithValidationErrors() {
		User user = new User();
		
		ResponseEntity<ApiError> response = postUser(user, ApiError.class);
		
		assertThat(response.getBody().getValidationErrors().size()).isEqualTo(3);
	}
	@Test @DisplayName("[회원가입] name 빈값일 때, message 받음")
	void postUser_whenUsernameIsNull_receiveMessageForUsername() {
		User user = TestUtil.createValidUser();
		user.setName(null);
		
		ResponseEntity<ApiError> response = postUser(user, ApiError.class);
		
		assertThat(response.getBody().getValidationErrors().get("username")).isEqualTo("필수 입력 값입니다");
	}
	@Test @DisplayName("[회원가입] 올바르지 않은 이메일 형식, message 받음")
	void postUser_whenEmailIsNotValid_receiveMessageForEmail() {
		User user = TestUtil.createValidUser();
		user.setEmail("goo@gle");
		
		ResponseEntity<ApiError> response = postUser(user, ApiError.class);
		
		assertThat(response.getBody().getValidationErrors().get("email")).isEqualTo("잘못된 이메일 형식입니다");
	}
	@Test @DisplayName("[회원가입] 비밀번호 패턴 부적합, message 받음")
	void postUser_whenPasswordPatternNotMeet_receiveMessageForPassword() {
		User user = TestUtil.createValidUser();
		user.setPassword("12345678");
		
		ResponseEntity<ApiError> response = postUser(user, ApiError.class);
		
		assertThat(response.getBody().getValidationErrors().get("password")).isEqualTo("최소 하나 이상의 소문자, 대문자, 숫자의 조합이어야 합니다");
	}
	@Test @DisplayName("[회원가입] 비밀번호 8자 미만, message 받음")
	void postUser_whenPasswordLessThanRequired_receiveMessageForPassword() {
		User user = TestUtil.createValidUser();
		user.setPassword("P4ss");
		
		ResponseEntity<ApiError> response = postUser(user, ApiError.class);
		
		assertThat(response.getBody().getValidationErrors().get("password")).isEqualTo("최소 8자 이상, 최대 255자 이하여야 합니다");
	}
	@Test @DisplayName("[회원가입] 중복된 이메일, 400 받음")
	void postUser_whenEmailDuplicated_receiveBadRequest() {
		userRepository.save(TestUtil.createValidUser());
		User user = TestUtil.createValidUser();
		
		ResponseEntity<Object> response = postUser(user, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	
	
	
	private <T> ResponseEntity<T> postUser(User user, Class<T> responseType) {
		return testRestTemplate.postForEntity(API_1_0_USERS, user, responseType);
	}
	
	

}
