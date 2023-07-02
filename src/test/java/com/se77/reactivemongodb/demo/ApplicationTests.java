package com.se77.reactivemongodb.demo;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.mongodb.assertions.Assertions;

@SpringBootTest
@Testcontainers
public class ApplicationTests {

	@ServiceConnection
	@Container
	static MongoDBContainer container = new MongoDBContainer("mongo:latest");

	@Test
	public void contextLoads() {
		RestTemplate template = new RestTemplate();

		List<Movie> result = template.getForObject("http://localhost:8080/movies", List.class);

		Assertions.assertTrue(result.size()==4);
	}

}
