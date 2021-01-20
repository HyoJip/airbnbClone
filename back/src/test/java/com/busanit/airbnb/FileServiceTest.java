package com.busanit.airbnb;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.aspectj.util.FileUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.busanit.airbnb.configuration.AppConfiguration;
import com.busanit.airbnb.file.FileService;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class FileServiceTest {

	FileService fileService;
	
	AppConfiguration appConfiguration;
	
	@BeforeAll
	void setup() {
		appConfiguration = new AppConfiguration();
		appConfiguration.setUploadPath("uploads-test");
		
		fileService = new FileService(appConfiguration);
		
		new File(appConfiguration.getUploadPath()).mkdir();
		new File(appConfiguration.getFullProfileImageFolder()).mkdir();
		new File(appConfiguration.getFullRoomImageFolder()).mkdir();
	}
	
	@Test @DisplayName("[파일 형식 검사] PNG 파일일 경우, image/png")
	void detectType_whenPngFileProvided_returnsImagePng() throws IOException {
		File resourceFile = new ClassPathResource("test-png.png").getFile();
		byte[] fileArr = FileUtil.readAsByteArray(resourceFile);
		
		String fileType = fileService.detectType(fileArr);
		
		assertThat(fileType).isEqualTo("image/png");
	}	
	@Test @DisplayName("[파일 형식 검사] JPG 파일일 경우, image/jpeg")
	void detectType_whenJpgFileProvided_returnsImageJpg() throws IOException {
		File resourceFile = new ClassPathResource("test-jpg.jpg").getFile();
		byte[] fileArr = FileUtil.readAsByteArray(resourceFile);
		
		String fileType = fileService.detectType(fileArr);
		
		assertThat(fileType).isEqualTo("image/jpeg");
	}
	
	@AfterEach
	void cleanup() throws IOException {
		FileUtils.cleanDirectory(new File(appConfiguration.getFullProfileImageFolder()));
		FileUtils.cleanDirectory(new File(appConfiguration.getFullRoomImageFolder()));
		
	}
}
