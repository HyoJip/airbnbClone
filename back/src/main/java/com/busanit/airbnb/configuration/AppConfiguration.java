package com.busanit.airbnb.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "airbnb")
@Data
public class AppConfiguration {

	private String uploadPath;
	
	private String profileImageFolder = "profile";
	
	private String roomImageFolder = "room";
	
	public String getFullProfileImageFolder() {
		return uploadPath + "/" + profileImageFolder;
	}
	
	public String getFullRoomImageFolder() {
		return uploadPath + "/" + roomImageFolder;
	}
}
