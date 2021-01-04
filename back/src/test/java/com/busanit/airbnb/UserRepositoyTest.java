package com.busanit.airbnb;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.busanit.airbnb.component.TestUtil;
import com.busanit.airbnb.user.User;
import com.busanit.airbnb.user.UserRepository;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoyTest {
	
	@Autowired
	TestEntityManager testEntityManager;
	
	@Autowired
	UserRepository userRepository;
	
	@Test @DisplayName("[findByEmail] 회원이 존재할 경우, 회원 찾음")
	void findByEmail_whenUserExists_returnsUser() {
		User user = testEntityManager.persist(TestUtil.createValidUser());
		
		User inDB = userRepository.findByEmail(user.getEmail());
		
		assertThat(inDB).isNotNull();
	}
	@Test @DisplayName("[findByEmail] 회원이 존재하지 않을 경우, Null 반환")
	void findByEmail_whenUserDoesNotExists_returnsNull() {
		User inDB = userRepository.findByEmail("anonymous@gmail.com");
		
		assertThat(inDB).isNull();
	}
}
