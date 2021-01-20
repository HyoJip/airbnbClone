package com.busanit.airbnb.configuration;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	AppConfiguration appConfiguration;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/**")
			.addResourceLocations("file:"+appConfiguration.getUploadPath() + "/")
			.setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
	}
	
	@Bean
	public CommandLineRunner createUploadFolder() {
		return (String... args) -> {
			createFolderIfNotExist(appConfiguration.getUploadPath());
			createFolderIfNotExist(appConfiguration.getFullProfileImageFolder());
			createFolderIfNotExist(appConfiguration.getFullRoomImageFolder());
		};

	}

	private void createFolderIfNotExist(String folderPath) {
		File folder = new File(folderPath);
		boolean folderExist = folder.exists() && folder.isDirectory();
		if (!folderExist) {
			folder.mkdir();
		}
	}
}
