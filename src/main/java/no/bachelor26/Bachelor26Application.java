package no.bachelor26;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Bachelor26Application {

	private static final Logger logger = LoggerFactory.getLogger(Bachelor26Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Bachelor26Application.class, args);
		logger.info("BACKEND BE RUNNIN");
	}

}
