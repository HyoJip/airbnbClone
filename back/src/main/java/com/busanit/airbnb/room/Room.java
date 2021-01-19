package com.busanit.airbnb.room;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Room {
	private long id;
	private String name;
	private LocalDate updated;
	private LocalDate created;
	private String description;
	private int price;
	private String address;
	private int beds;
	private int bedRooms;
	private int baths;
	private String checkIn;
	private String checkOut;
	private int capacity;
}
