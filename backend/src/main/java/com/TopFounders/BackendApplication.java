package com.TopFounders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class BackendApplication {

	@GetMapping("/helloworld")
	public String sayHello(){
		return "Hello World!";
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
