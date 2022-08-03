package com.se77.reactivemongodb.demo;

import java.time.Duration;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@SpringBootApplication
public class Application {

	/**
	 * Resets Data in MongoDB.
	 * @param movieRepo
	 * @return
	 */
	@Bean
	CommandLineRunner demo(MovieRepository movieRepo) {

		return args -> {

			movieRepo.deleteAll().subscribe(null, null,
					() -> Stream.of("Aeon Flux", "Enter the Mono<Void>", "The Fluxinator", "Silence of the Lambdas")
							.map(name -> new Movie(UUID.randomUUID().toString(), name, randomGenre()))
							.forEach(movie -> movieRepo.save(movie).subscribe(System.out::println)));
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Generates a random Genre-String
	 * @return the genre
	 */
	private String randomGenre() {
		String[] genres = "horror, romance,drama".split(",");

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

@Component
class MovieService {

	@Autowired
	private MovieRepository movieRepo;

	/**
	 * Generates a Random Username
	 * @return
	 */
	public String generateUserName() {
		String[] users = "Josh, Ernie, Bob".split(",");
		return users[new Random().nextInt(users.length)];
	}

	/**
	 * Generates a Flux of Movie-Events for a Movie 
	 * @param movie Movie to generate Events for
	 * @return Flux of Events
	 */
	public Flux<MovieEvent> getEvents(Movie movie){
		Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
		
		Flux<MovieEvent> events = Flux.fromStream(Stream.generate( () -> new MovieEvent(movie, new Date(), generateUserName())));
		
		return Flux.zip(interval, events).map(Tuple2::getT2);
	}

	/**
	 * Returns all Movies in the DB
	 * @return Flux of all Movies
	 */
	public Flux<Movie> getAll() {
		return movieRepo.findAll();
	}

	/**
	 * Returns a Movie by Id
	 * @param id id of the movie
	 * @return
	 */
	public Mono<Movie> getById(String id) {
		return movieRepo.findById(id);
	}
}

@RestController
@RequestMapping("/movies")
class MovieRestController {
	
	@Autowired
	private MovieService service;
	
	@GetMapping(value = "/{id}/events" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<MovieEvent> getEvents(@PathVariable String id) {
		return service.getById(id).flatMapMany(service::getEvents);
	}
	
	@GetMapping
	public Flux<Movie> getAll(){
		return service.getAll();
	}
	
	@GetMapping("/{id}")
	public Mono<Movie> getById(@PathVariable String id) {
		return service.getById(id);
	}
	
}

interface MovieRepository extends ReactiveMongoRepository<Movie, String> {

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