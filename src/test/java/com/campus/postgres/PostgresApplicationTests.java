package com.campus.postgres;

import com.campus.postgres.user.entity.UserEntity;
import com.campus.postgres.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PostgresApplicationTests {
	@Autowired
	private UserRepository userRepository;
	@Test
	void repositoryTest(){
		UserEntity user = UserEntity.builder()
				.firstName("test")
				.lastName("test")
				.build();

		user = userRepository.save(user);
		UserEntity check = userRepository.findById(user.getId()).get();
		assert check.getId().equals(user.getId());
	}

	@Test
	void contextLoads() {
	}

}
