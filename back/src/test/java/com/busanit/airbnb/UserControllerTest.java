package com.busanit.airbnb;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.busanit.airbnb.component.TestUtil;
import com.busanit.airbnb.configuration.AppConfiguration;
import com.busanit.airbnb.shared.ApiError;
import com.busanit.airbnb.shared.GeneralResponse;
import com.busanit.airbnb.user.User;
import com.busanit.airbnb.user.UserRepository;
import com.busanit.airbnb.user.UserService;
import com.busanit.airbnb.user.UserStatus;
import com.busanit.airbnb.user.vm.UserUpdateVM;
import com.busanit.airbnb.user.vm.UserVM;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class UserControllerTest {
	private static final String API_1_0_USERS = "/api/1.0/users";
	
	@Autowired
	TestRestTemplate testRestTemplate;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserService userService;
	
	@Autowired
	AppConfiguration appConfiguration;
	
	@BeforeEach
	void cleanup() {
		userRepository.deleteAll();
		testRestTemplate.getRestTemplate().getInterceptors().clear();
	}
	
	@AfterEach
	void cleanupDirectory() throws IOException {
		FileUtils.cleanDirectory(new File(appConfiguration.getFullProfileImageFolder()));
		FileUtils.cleanDirectory(new File(appConfiguration.getFullRoomImageFolder()));
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
		
		assertThat(response.getBody().getValidationErrors().get("name")).isEqualTo("필수 입력 값입니다");
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
	
	
	@Test @DisplayName("[회원조회] 존재하는 회원, 200 받음")
	void getUserById_whenUserIsExist_receiveOk() {
		User user = userService.save(TestUtil.createValidUser());
		ResponseEntity<Object> response = getUser(user.getId(), Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	@Test @DisplayName("[회원조회] 존재하는 회원, 비밀번호 미포함")
	void getUserById_whenUserIsExist_receiveUserWithoutPassword() {
		User user = userService.save(TestUtil.createValidUser());
		ResponseEntity<String> response = getUser(user.getId(), String.class);
		
		assertThat(response.getBody().contains("password")).isFalse();
	}
	@Test @DisplayName("[회원조회] 존재하지 않는 회원, 404 받음")
	void getUserById_whenUserIsNotExist_receiveNotFound() {
		userService.save(TestUtil.createValidUser());
		ResponseEntity<Object> response = getUser(0, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
	@Test @DisplayName("[회원조회] 존재하지 않는 회원, ApiError 받음")
	void getUserById_whenUserIsNotExist_receiveApiError() {
		userService.save(TestUtil.createValidUser());
		ResponseEntity<ApiError> response = getUser(0, ApiError.class);
		
		assertThat(response.getBody().getMessage()).contains("일치하는 회원이 없습니다");
	}

	
	@Test @DisplayName("[회원수정] 인증되지 않은 회원, 401 받음")
	void putUserById_whenUserIsNotExist_receiveUnAuthrized() {
		ResponseEntity<Object> response = putUser(0, null, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}
	@Test @DisplayName("[회원수정] 다른 회원 수정하려할 경우, 403 받음")
	void putUserById_whenUserTryUpdateAnotherUser_receiveForbidden() {
		userService.save(TestUtil.createValidUser());
		authenticate();
		ResponseEntity<Object> response = putUser(0, null, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}
	@Test @DisplayName("[회원수정] 인증되지 않은 회원, ApiError 받음")
	void putUserById_whenUserIsNotExist_receiveApiError() {
		ResponseEntity<ApiError> response = putUser(0, null, ApiError.class);
		
		assertThat(response.getBody().getUrl()).contains(API_1_0_USERS + "/0");
	}
	@Test @DisplayName("[회원수정] 다른 회원 수정하려할 경우, ApiError 받음")
	void putUserById_whenUserTryUpdateAnotherUser_receiveApiError() {
		userService.save(TestUtil.createValidUser());
		authenticate();
		
		ResponseEntity<ApiError> response = putUser(0, null, ApiError.class);
		
		assertThat(response.getBody().getUrl()).contains(API_1_0_USERS + "/0");
	}
	@Test @DisplayName("[회원수정] 인증된 회원의 본인 수정 요청, 200 받음")
	void putUserById_whenAuthorizedUserSendValidRequest_receiveOk() {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		ResponseEntity<Object> response = putUser(user.getId(), request, Object.class);		
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	@Test @DisplayName("[회원수정] 인증된 회원의 본인 수정 요청, DB에 이름 수정됨")
	void putUserById_whenAuthorizedUserSendValidRequest_nameIsUpdatedInDB() {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		putUser(user.getId(), request, Object.class);
		User inDB = userService.findById(user.getId());
		
		assertThat(inDB.getName()).isEqualTo(userUpdateVM.getName());
	}
	@Test @DisplayName("[회원수정] 인증된 회원의 본인 수정 요청, DB에 비밀번호 수정됨")
	void putUserById_whenAuthorizedUserSendValidRequest_passwordIsUpdatedInDB() {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		String prevPassword = userService.findById(user.getId()).getPassword();
		
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		putUser(user.getId(), request, Object.class);
		User inDB = userService.findById(user.getId());
		
		assertThat(prevPassword).isNotEqualTo(inDB.getPassword());
	}
	@Test @DisplayName("[회원수정] 인증된 회원의 본인 수정 요청, 업데이트 된 회원 정보 받음")
	void putUserById_whenAuthorizedUserSendValidRequest_receivceUserWithUpdatedName() {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		ResponseEntity<UserVM> response = putUser(user.getId(), request, UserVM.class);
		
		assertThat(response.getBody().getName()).isEqualTo(userUpdateVM.getName());
	}
	@Test @DisplayName("[회원수정] 인증된 회원의 지원하는 타입의 프로필 사진 수정 요청, 무작위 생성된 이름으로 저장됨")
	void putUserById_whenAuthorizedUserSendValidRequest_savedWithRandomName() throws IOException {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		
		String imageString = readFileToBase64("profile.png");
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		userUpdateVM.setProfile(imageString);
		
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		ResponseEntity<UserVM> response = putUser(user.getId(), request, UserVM.class);
		assertThat(response.getBody().getProfile()).isNotEqualTo("profile-image.png");
	}
	@Test @DisplayName("[회원수정] 인증된 회원의 지원하는 타입의 프로필 사진 수정 요청, 프로필 촐더에 이미지 파일 저장됨")
	void putUserById_whenAuthorizedUserSendValidRequest_receivce() throws IOException {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		
		String imageString = readFileToBase64("profile.png");
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		userUpdateVM.setProfile(imageString);
		
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		ResponseEntity<UserVM> response = putUser(user.getId(), request, UserVM.class);
		
		String imageName = response.getBody().getProfile();
		File savedImage = new File(appConfiguration.getFullProfileImageFolder() + "/" + imageName);
		assertThat(savedImage.exists()).isTrue();
	}
	@Test @DisplayName("[회원수정] 인증된 회원의 이름 null, 400 받음")
	void putUserById_whenAuthorizedUserSendInvalidRequestWithoutName_receiveBadRequest() {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		userUpdateVM.setName(null);
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		ResponseEntity<Object> response = putUser(user.getId(), request, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원수정] 인증된 회원의 이름 2글자 미만 , 400 받음")
	void putUserById_whenAuthorizedUserSendInvalidRequestWithNameLessThanRequired_receiveBadRequest() {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		userUpdateVM.setName("a");
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		ResponseEntity<Object> response = putUser(user.getId(), request, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원수정] 인증된 회원의 이름 20글자 초과 , 400 받음")
	void putUserById_whenAuthorizedUserSendInvalidRequestWithNameMoreThanRequired_receiveBadRequest() {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		String str21 = IntStream.rangeClosed(1, 21).mapToObj(i -> "a").toString();
		userUpdateVM.setName(str21);
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		ResponseEntity<Object> response = putUser(user.getId(), request, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원수정] 인증된 회원의 password 8글자 미만, 400 받음")
	void putUserById_whenAuthorizedUserSendInvalidRequestWithPasswordLessThanRequired_receiveBadRequest() {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		userUpdateVM.setPassword("abc");
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		ResponseEntity<Object> response = putUser(user.getId(), request, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원수정] 인증된 회원의 password 255글자 초과, 400 받음")
	void putUserById_whenAuthorizedUserSendInvalidRequestWithPasswordMoreThanRequired_receiveBadRequest() {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		String moreThan255 = IntStream.rangeClosed(1, 256).mapToObj(i -> "a").collect(Collectors.joining());
		userUpdateVM.setPassword("P4ssword" + moreThan255);
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		ResponseEntity<Object> response = putUser(user.getId(), request, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원수정] 인증된 회원의 password 소문자만, 400 받음")
	void putUserById_whenAuthorizedUserSendInvalidRequestWithPasswordOnlyLowercase_receiveBadRequest() {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		userUpdateVM.setPassword("password");
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		ResponseEntity<Object> response = putUser(user.getId(), request, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원수정] 인증된 회원의 password 대문자만, 400 받음")
	void putUserById_whenAuthorizedUserSendInvalidRequestWithPasswordOnlyUppercase_receiveBadRequest() {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		userUpdateVM.setPassword("PASSWORD");
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		ResponseEntity<Object> response = putUser(user.getId(), request, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원수정] 인증된 회원의 password 숫자만, 400 받음")
	void putUserById_whenAuthorizedUserSendInvalidRequestWithPasswordOnlyDemical_receiveBadRequest() {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		userUpdateVM.setPassword("12345678");
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		ResponseEntity<Object> response = putUser(user.getId(), request, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원수정] 프로필 이미지가 JPG일 경우, 200 받음")
	void putUserById_whenAuthorizedUserSendValidRequestWithJpgFile_receiveOk() throws IOException {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		String imageString = readFileToBase64("test-jpg.jpg");
		userUpdateVM.setProfile(imageString);
		
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		ResponseEntity<Object> response = putUser(user.getId(), request, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	@Test @DisplayName("[회원수정] 프로필 이미지가 PNG일 경우, 200 받음")
	void putUserById_whenAuthorizedUserSendValidRequestWithPngFile_receiveOk() throws IOException {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		String imageString = readFileToBase64("test-png.png");
		userUpdateVM.setProfile(imageString);
		
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		ResponseEntity<Object> response = putUser(user.getId(), request, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	@Test @DisplayName("[회원수정] 프로필 이미지가 GIF일 경우, 400 받음")
	void putUserById_whenAuthorizedUserSendValidRequestWithGifFile_receiveBadRequest() throws IOException {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		String imageString = readFileToBase64("test-gif.gif");
		userUpdateVM.setProfile(imageString);
		
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		ResponseEntity<Object> response = putUser(user.getId(), request, Object.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	@Test @DisplayName("[회원수정] 프로필 이미지가 이미 존재할 경우, 이전 이미지 파일 삭제")
	void putUserById_whenAuthorizedUserwhoHasProfileImageChangeImage_removesOldFileFromStroage() throws IOException {
		User user = userService.save(TestUtil.createValidUser());
		authenticate();
		UserUpdateVM userUpdateVM = TestUtil.createValidUserUpdateVM();
		String imageString = readFileToBase64("test-png.png");
		userUpdateVM.setProfile(imageString);
		HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
		
		ResponseEntity<UserVM> response = putUser(user.getId(), request, UserVM.class);
		putUser(user.getId(), request, Object.class);
		
		String oldImageName = response.getBody().getProfile();
		File oldImage = new File(appConfiguration.getFullProfileImageFolder() + "/" + oldImageName);
		assertThat(oldImage.exists()).isFalse();
	}
	
	
	
	private String readFileToBase64(String fileName) throws IOException {
		ClassPathResource imageResource = new ClassPathResource(fileName);
		byte[] imageArr = FileUtils.readFileToByteArray(imageResource.getFile());
		String imageString = Base64.getEncoder().encodeToString(imageArr);
		return imageString;
	}
	private void authenticate() {
		testRestTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor("test@naver.com", "P4ssword"));
	}
	private <T> ResponseEntity<T> putUser(long id, HttpEntity<?> request, Class<T> responseType) {
		String path = API_1_0_USERS + "/" + id;
		return testRestTemplate.exchange(path, HttpMethod.PUT, request, responseType);
	}
	private <T> ResponseEntity<T> getUser(long id, Class<T> responseType) {
		return testRestTemplate.getForEntity(API_1_0_USERS + "/" + id, responseType);
	}
	private <T> ResponseEntity<T> postUser(User user, Class<T> responseType) {
		return testRestTemplate.postForEntity(API_1_0_USERS, user, responseType);
	}
}
