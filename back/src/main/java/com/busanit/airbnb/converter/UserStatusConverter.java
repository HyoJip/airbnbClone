package com.busanit.airbnb.converter;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;

import com.busanit.airbnb.enums.UserStatus;

public class UserStatusConverter implements AttributeConverter<UserStatus, String>{

	@Override
	public String convertToDatabaseColumn(UserStatus status) {
		if (status == null) {
			return UserStatus.GUEST.getCode();
		}
		
		return status.getCode();
	}

	@Override
	public UserStatus convertToEntityAttribute(String code) {
		return Stream.of(UserStatus.values())
				.filter(i -> i.getCode().equals(code))
				.findFirst().orElseThrow();
	}

}
