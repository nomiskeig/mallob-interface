package edu.kit.fallob;


import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.kit.fallob.configuration.FallobConfigReader;
import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.mallobio.MallobReaderStarter;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		
		 //-----------------------Production code.Ddo not use until integration-tests begin--------------------------
		//initialize mallob-config
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

		//initialize mallobio
		int amountReaderThreads = 20;
		int readingIntervalPerReadingThread = 50; 
		
		FallobConfiguration config = FallobConfiguration.getInstance();

		
		MallobReaderStarter mallobio = new MallobReaderStarter(config.getMallobBasePath());	
		mallobio.initOutput(config.getAmountProcesses(), amountReaderThreads, readingIntervalPerReadingThread);
		mallobio.initInput(config.getAmountProcesses(), config.getClientProcesses());
		
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
