package fr.istic.tp.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

// @SpringBootApplication(exclude ={MongoAutoConfiguration.class})
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration.class, MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
