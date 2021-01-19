package com.busanit.airbnb.component;

import com.busanit.airbnb.room.Room;
import com.busanit.airbnb.user.User;
import com.busanit.airbnb.user.vm.UserUpdateVM;

public class TestUtil {
	
	public static User createValidUser() {
		User user = new User();
		user.setEmail("test@naver.com");
		user.setName("test-user");
		user.setPassword("P4ssword");
		user.setProfile("profile-image.png");
		return user;
	}
	
	public static UserUpdateVM createValidUserUpdateVM() {
		UserUpdateVM userUpdateVM = new UserUpdateVM();
		userUpdateVM.setName("이몽룡");
		userUpdateVM.setPassword("P4ssw0rd");
		return userUpdateVM;
	}
	
	public static Room createValidRoom() {
		Room room = new Room();
		room.setName("테스트용 숙소");
		room.setDescription("광안리 근처");
		room.setPrice(100000);
		room.setAddress("부산광역시 남구 메카");
		room.setBeds(2);
		room.setBedRooms(1);
		room.setBaths(1);
		room.setCheckIn("15:00");
		room.setCheckOut("11:00");
		room.setCapacity(2);
		return room;
	}
}
