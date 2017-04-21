package com.zx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.zx","com.geccocrawler.gecco.spring"})
@SpringBootApplication
public class Spider1Application {

	public static void main(String[] args) {
		SpringApplication.run(Spider1Application.class, args);
	}
}
