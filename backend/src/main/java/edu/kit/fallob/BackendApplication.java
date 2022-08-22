package edu.kit.fallob;


import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.kit.fallob.configuration.FallobConfigReader;
import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.mallobio.MallobFilePathGenerator;
import edu.kit.fallob.mallobio.MallobReaderStarter;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.CentralOutputLogListener;
import edu.kit.fallob.mallobio.output.MallobOutputWatcherManager;
import edu.kit.fallob.mallobio.output.distributors.MallobOutput;
import edu.kit.fallob.springConfig.FallobException;

@SpringBootApplication
public class BackendApplication {


	public static void main(String[] args) throws FallobException {
		
		
		 //-----------------------Production code.Ddo not use until integration-tests begin--------------------------
		//initialize mallob-config
		args = new String[1]; args[0]="/home/siwi/pse_dev/fallob-configuration.json";
		String pathToFallobConfigFile = args[0];
		
		FallobConfigReader reader;
		try {
			 reader = new FallobConfigReader(pathToFallobConfigFile);
		} catch (FileNotFoundException e) {
			System.out.println("Fallob-Configuration file not found at specified location.");
			return;
		}
		
		try {
			reader.setupFallobConfig();
		} catch (IOException e) {
			System.out.println("Missing arguments in Fallob-Configuration file. Please check for correct spelling of arguments and completeness.");
			e.printStackTrace();
			return;
		}

		
		FallobConfiguration config = FallobConfiguration.getInstance();

		
		MallobReaderStarter mallobio = new MallobReaderStarter(config.getMallobBasePath());	
		mallobio.initOutput(config.getAmountProcesses());
		mallobio.initInput(config.getAmountProcesses(), config.getClientProcesses());
		
		
		//add all listeners to mallobio
		mallobio.addStaticListeners();
		
		//this listener prints all output-lines picked up by the readers
		MallobOutput.getInstance().addOutputLogLineListener(new CentralOutputLogListener());	

		
		//-----------------------add additional file-readers here 
		//mallobio.addIrregularReaders(<your directory, your filename>);
		int processID = config.getAmountProcesses() - 1;
		mallobio.addIrregularReaders(
				MallobFilePathGenerator.generateLogDirectoryPath(config.getMallobBasePath(), processID),
				MallobFilePathGenerator.generateLogMappingFileName(processID));
		mallobio.startMallobio();
		
		//-------------------testing watcher
		MallobOutputWatcherManager m = MallobOutputWatcherManager.getInstance();
		m.addNewWatcher("testuser", "testjob", 0);
		
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
