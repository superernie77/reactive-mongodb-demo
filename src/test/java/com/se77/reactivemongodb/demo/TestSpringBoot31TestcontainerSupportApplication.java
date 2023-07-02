package com.se77.reactivemongodb.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestSpringBoot31TestcontainerSupportApplication {

	@Bean
	@ServiceConnection
	MongoDBContainer mongoDbContainer() {
		return new MongoDBContainer("mongo:latest").withReuse(true);
	}
	
	public static void main(String[] args) {
		SpringApplication.from(Application::main).with(TestSpringBoot31TestcontainerSupportApplication.class).run(args);
	}

}