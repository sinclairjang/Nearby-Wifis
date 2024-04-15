package com.test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;

public class FormatTest {
	public static void main(String[] args) {
//		LocalDateTime dateTime = LocalDateTime.now();
//		System.out.println(dateTime);
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
//		String text = dateTime.format(formatter);
//		System.out.println(text);
//		LocalDateTime parsedDate = LocalDateTime.parse(text, formatter);
//		System.out.println(parsedDate);
//		
//		Timestamp ts = Timestamp.valueOf("2024-04-02 11:12:46.0");
//		System.out.println(ts);
//		
		Instant now = Instant.now();
//		
//		System.out.println(Timestamp.from(now));
		
//		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		String text = dateTime.format(formatter);
		Timestamp ts = Timestamp.from(now);
//		System.out.println(text);
//		System.out.println(ts.toLocalDateTime());
		
		LocalDateTime dateTime2 = ts.toLocalDateTime();
		String text2 = dateTime2.format(formatter);
		System.out.println(text2);
	}

}
