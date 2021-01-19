package com.busanit.airbnb;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StaticResourceTest {
	
	@Autowired
	TestRestTemplate testRestTemplate;
	

	@Test @DisplayName("[폴더확인] 앱 시작시 업로드 폴더 존재하지 않을 경우, 폴더 생성")
	void checkStaticFolder_whenAppIsInitialized_uploadFolderMustExist() {
		File uploadFolder = new File("upload-test");
		boolean uploadFolderExist = uploadFolder.exists() && uploadFolder.isDirectory();
		assertThat(uploadFolderExist).isTrue();
	}
}
