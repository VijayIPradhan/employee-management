package com.employee.management;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.math.BigDecimal;


@SpringBootApplication
@EnableScheduling
public class ManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(ManagementApplication.class, args);
		Double a=10.0;
		System.out.println(a*100/100);
	}
}
