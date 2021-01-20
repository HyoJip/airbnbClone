package com.busanit.airbnb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.busanit.airbnb.configuration.AppConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class StaticResourceTest {
	
	@Autowired
	TestRestTemplate testRestTemplate;
	
	@Autowired
	AppConfiguration appConfiguration;
	
	@Autowired
	MockMvc mockMvc;
	
	@AfterEach
	void cleanup() throws IOException {
		FileUtils.cleanDirectory(new File(appConfiguration.getFullProfileImageFolder()));
		FileUtils.cleanDirectory(new File(appConfiguration.getFullRoomImageFolder()));
	}
	
	@Test @DisplayName("[폴더확인] 앱 시작시 업로드 폴더 존재하지 않을 경우, 폴더 생성")
	void checkStaticFolder_whenAppIsInitialized_uploadFolderMustExist() {
		File uploadFolder = new File(appConfiguration.getUploadPath());
		boolean uploadFolderExist = uploadFolder.exists() && uploadFolder.isDirectory();
		
		assertThat(uploadFolderExist).isTrue();
	}
	
	@Test @DisplayName("[폴더확인] 앱 시작시 업로드/프로필 폴더 존재하지 않을 경우, 폴더 생성")
	void checkStaticFolder_whenAppIsInitialized_profileFolderMustExist() {
		File profileImageFolder = new File(appConfiguration.getFullProfileImageFolder());
		boolean profileImageFolderExist = profileImageFolder.exists() && profileImageFolder.isDirectory();
		
		assertThat(profileImageFolderExist).isTrue();
	}
	
	@Test @DisplayName("[폴더확인] 앱 시작시 업로드/숙소 폴더 존재하지 않을 경우, 폴더 생성")
	void checkStaticFolder_whenAppIsInitialized_roomFolderMustExist() {
		File roomImageFolder = new File(appConfiguration.getFullRoomImageFolder());
		boolean roomImageFolderExist = roomImageFolder.exists() && roomImageFolder.isDirectory();
		
		assertThat(roomImageFolderExist).isTrue();
	}
	
	@Test @DisplayName("[프로필 사진 조회] 파일 존재시, 200 받음")
	void getStaticFile_whenImageExistsInProfileFolder_receiveOk() throws Exception {
		String fileName = "profile-picture.png";
		File source = new ClassPathResource("profile.png").getFile();
		File target = new File(appConfiguration.getFullProfileImageFolder() + "/" + fileName);
		
		FileUtils.copyFile(source, target);
		
		mockMvc.perform(get("/images/" + appConfiguration.getProfileImageFolder() + "/" + fileName)).andExpect(status().isOk());
	}
	
	@Test @DisplayName("[숙소 사진 조회] 파일 존재시, 200 받음")
	void getStaticFile_whenImageExistsInRoomFolder_receiveOk() throws Exception {
		String fileName = "profile-picture.png";
		File source = new ClassPathResource("profile.png").getFile();
		File target = new File(appConfiguration.getFullRoomImageFolder() + "/" + fileName);
		
		FileUtils.copyFile(source, target);
		
		mockMvc.perform(get("/images/" + appConfiguration.getRoomImageFolder() + "/" + fileName)).andExpect(status().isOk());
	}
	
	@Test @DisplayName("[숙소 사진 조회] 파일 존재하지 않을 경우, 404 받음")
	void getStaticFile_whenImageDoesNotExistsInFolder_receiveNotFound() throws Exception {
		mockMvc.perform(get("/images/" + appConfiguration.getRoomImageFolder() + "/no-exist-image.png"))
			.andExpect(status().isNotFound());
	}
	
	@Test @DisplayName("[숙소 사진 조회] 파일 존재시, 캐시 헤더 받음")
	void getStaticFile_whenImageExistsInRoomFolder_receiveWithCacheHeader() throws Exception {
		String fileName = "profile-picture.png";
		File source = new ClassPathResource("profile.png").getFile();
		File target = new File(appConfiguration.getFullRoomImageFolder() + "/" + fileName);
		
		FileUtils.copyFile(source, target);
		MvcResult result = mockMvc.perform(get("/images/" + appConfiguration.getRoomImageFolder() + "/" + fileName)).andReturn();
		
		String cacheControl = result.getResponse().getHeaderValue("Cache-Control").toString();
		assertThat(cacheControl).containsIgnoringCase("max-age=86400");
	}
}
