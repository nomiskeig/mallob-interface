package edu.kit.fallob;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.DatabaseGarbageCollector;
import edu.kit.fallob.database.EventDao;

import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.kit.fallob.configuration.FallobConfigReader;
import edu.kit.fallob.configuration.FallobConfiguration;
import edu.kit.fallob.mallobio.MallobFilePathGenerator;
import edu.kit.fallob.mallobio.MallobReaderStarter;
import edu.kit.fallob.mallobio.input.MallobInputImplementation;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.CentralOutputLogListener;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.PeriodicBufferChecker;
import edu.kit.fallob.mallobio.output.distributors.MallobOutput;
import edu.kit.fallob.springConfig.FallobException;

@SpringBootApplication
public class BackendApplication {


	public static void main(String[] args) throws FallobException {
		
		 //-----------------------Production code.Ddo not use until integration-tests begin--------------------------
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
		} catch (IOException  | JSONException e) {
			System.out.println("Missing arguments in Fallob-Configuration file. Please check for correct spelling of arguments and completeness.");
			e.printStackTrace();
        }

		
		FallobConfiguration config = FallobConfiguration.getInstance();


		//set the right start time in the configuration
		EventDao eventDao = new DaoFactory().getEventDao();
		LocalDateTime firstEventTime = eventDao.getTimeOfFirstEvent();

		if (firstEventTime != null) {
			config.setStartTime(firstEventTime);
		}

		//start the database garbage collector
		Runnable garbageCollector = new DatabaseGarbageCollector(config.getGarbageCollectorInterval());
		Thread garbageCollectorThread = new Thread(garbageCollector);
		garbageCollectorThread.start();
		
		//start PeriodicBufferChecker
		Thread pbc = new Thread(PeriodicBufferChecker.getInstance());
		pbc.start();
		

		//initialize mallobio
		int amountReaderThreads = config.getAmountReaderThreads();
		int readingIntervalPerReadingThread = config.getReadingIntervalPerReadingThread(); 
		

		
		MallobReaderStarter mallobio = new MallobReaderStarter(config.getMallobBasePath());	
		mallobio.initOutput(config.getAmountProcesses(), amountReaderThreads, readingIntervalPerReadingThread);
		mallobio.initInput(config.getAmountProcesses(), config.getClientProcesses());
		
		//add all listeners to mallobio
		mallobio.addStaticListeners();
		
		

		
		//-----------------------add additional file-readers here 
		//mallobio.addIrregularReaders(<yourfilepath>);
		//file-reader for 
		mallobio.addIrregularReaders(MallobFilePathGenerator.generatePathToJobMappingsLogFile(config.getMallobBasePath(), config.getAmountProcesses() - 1));
		
		mallobio.startMallobio();
		
		
		manageFallobParameters(args);
		
		SpringApplication.run(BackendApplication.class, args);
	}
	
	
	private static void manageFallobParameters(String[] args) {
		
		for (String s : args) {
			if (s.equals("printMallobLogsToConsole")) {
				CentralOutputLogListener consolePrinter = new CentralOutputLogListener();
				MallobOutput.getInstance().addOutputLogLineListener(consolePrinter);
				MallobOutput.getInstance().addResultObjectListener(consolePrinter);
			}
			
			if (s.equals("t")) {
				MallobInputImplementation.getInstance().useTilde();
			}
		}
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
