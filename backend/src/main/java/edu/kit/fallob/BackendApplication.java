package edu.kit.fallob;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.kit.fallob.configuration.FallobConfigReader;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		
		/*
		 * -----------------------Production code.Ddo not use until integration-tests begin--------------------------
		String pathToFallobConfigFile = args[0];
		FallobConfigReader reader;
		try {
			 reader = new FallobConfigReader(pathToFallobConfigFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		try {
			reader.setupFallobConfig();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		*/
		
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
