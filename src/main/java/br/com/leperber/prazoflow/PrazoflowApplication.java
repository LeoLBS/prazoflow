package br.com.leperber.prazoflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PrazoflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrazoflowApplication.class, args);
	}

}
