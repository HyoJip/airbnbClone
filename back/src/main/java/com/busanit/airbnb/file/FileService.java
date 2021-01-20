package com.busanit.airbnb.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import com.busanit.airbnb.configuration.AppConfiguration;

@Service
public class FileService {

	private final AppConfiguration appConfiguration;
	
	private final Tika tika;

	public FileService(AppConfiguration appConfiguration) {
		this.appConfiguration = appConfiguration;
		this.tika = new Tika();
	}
	
	public String saveProfileImage(String base64Images) {
		String imageName = UUID.randomUUID().toString().replaceAll("-", "");
		
		byte[] decodedBytes = Base64.getDecoder().decode(base64Images);
		File target = new File(appConfiguration.getFullProfileImageFolder() + "/" + imageName);
		try {
			FileUtils.writeByteArrayToFile(target, decodedBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageName;
	}

	public String detectType(byte[] fileArr) {
		return tika.detect(fileArr);
	}

	public void deleteProfileImage(String profile) {
		try {
			Files.deleteIfExists(Paths.get(appConfiguration.getFullProfileImageFolder() + "/" + profile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
