package com.example.Blink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BlinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlinkApplication.class, args);
		System.out.println("App Running Successfully");
		System.out.println("App Running Successfullysssssssssss");
	}
}
