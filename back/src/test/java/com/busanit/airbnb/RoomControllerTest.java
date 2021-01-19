package com.busanit.airbnb;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.busanit.airbnb.room.Room;
import com.busanit.airbnb.user.UserService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class RoomControllerTest {
	private static final String API_1_0_ROOMS = "/api/1.0/rooms";
	
	@Autowired
	TestRestTemplate testRestTemplate;
	
	@Autowired
	UserService userService;
	

	@Test @DisplayName("[숙소등록] 로그인 하지 않은 회원, 401 받음")
	void postRoom_whenRoomIsValidAndLoginDidNot_receiveOk() {
		Room room = TestUtil.createValidRoom();
		
		ResponseEntity<Object> response = postRoom(room, Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}
	@Test @DisplayName("[숙소등록] 로그인한 회원, 200 받음")
	void postRoom_whenRoomIsValidAndLoginDid_receiveOk() {
		userService.save(TestUtil.createValidUser());
		authenticate();
		Room room = TestUtil.createValidRoom();
		
		ResponseEntity<Object> response = postRoom(room, Object.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}


	private <T> ResponseEntity<T> postRoom(Room room, Class<T> responseType) {
		return testRestTemplate.postForEntity(API_1_0_ROOMS, room, responseType);
	}
	private void authenticate() {
		testRestTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor("test@naver.com", "P4ssword"));
	}

}
