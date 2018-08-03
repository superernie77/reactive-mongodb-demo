package com.se77.reactivemongodb.demo;

import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class Application {
	
	@Bean
	CommandLineRunner demo(MovieRepository movieRepo) {
		
		
		
		return args -> {
			
			movieRepo.deleteAll().subscribe(null, null, () -> Stream.of("Aeon Flux", "Enter the Mono<Void>", "The Fluxinator", "Silence of the Lambdas")
					.map(name -> new Movie(UUID.randomUUID().toString(), name , randomGenre() ))
					.forEach(movie -> movieRepo.save(movie).subscribe(System.out::println)));
		};
	}
	
	
	
	

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	private String randomGenre() {
		String [] genres =  "horror, romance,drama".split(",");
		
		return genres[new Random().nextInt(genres.length)];
	}
}


@Data
@AllArgsConstructor
@NoArgsConstructor
class MovieEvent {
	
	private Movie movie;
	
	private Date date;
	
	private String user;
}

class MovieService {
	
	@Autowired
	private MovieRepository movieRepo;
	
//	public Flux<MovieEvent> getEvents(){	
//	}
	
	public Flux<Movie> getAll(){
		return movieRepo.findAll();
	}
	
	public Mono<Movie> getById(String id){
		return movieRepo.findById(id);
	}
}

interface MovieRepository extends ReactiveMongoRepository<Movie, String>{
	
}

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
class Movie {
	
	@Id
	private String id;
	
	private String title;
	
	private String genre;
}