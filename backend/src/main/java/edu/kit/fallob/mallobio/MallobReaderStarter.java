package edu.kit.fallob.mallobio;

import java.util.ArrayList;
import java.util.List;


import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.mallobio.input.MallobInputImplementation;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.EventListener;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.JobStatusListener;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.MallobTimeListener;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.WarningListener;
import edu.kit.fallob.mallobio.listeners.resultlisteners.JobResultListener;
import edu.kit.fallob.mallobio.output.MallobOutputReader;
import edu.kit.fallob.mallobio.output.MallobOutputWatcherManager;
import edu.kit.fallob.mallobio.output.distributors.MallobOutput;
import edu.kit.fallob.mallobio.output.distributors.OutputLogLineDistributor;
import edu.kit.fallob.mallobio.output.distributors.ResultObjectDistributor;
import edu.kit.fallob.springConfig.FallobException;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 * 
 * Initializes the module 
 *
 */
public class MallobReaderStarter {
	
	protected String pathToMallobDirectory;
	
	private Thread[] readerThreadPool;
	private MallobOutputReader[] readers;

	
	private MallobOutputWatcherManager watcherManager;
	
	private MallobOutput mallobOutput;
	private OutputLogLineDistributor logDistributor;
	private ResultObjectDistributor resultDistributor;
	
	private List<MallobOutputReader> irregularFilesReaders;
	
	
	public MallobReaderStarter(String pathToMallobDirectory) {
		this.pathToMallobDirectory = pathToMallobDirectory;
	}
	
	/**
	 * Initialize the input-module to communicate with mallob 
	 * 
	 * @param amountProcesses
	 * @param clientProcesses
	 */
	public void initInput(int amountProcesses, int[] clientProcesses) {
		MallobInputImplementation mii = MallobInputImplementation.getInstance();
		if (clientProcesses == null || clientProcesses.length == amountProcesses) {
			mii.setupInputAllProcesses(pathToMallobDirectory, amountProcesses);
		} else {
			mii.setupInput(pathToMallobDirectory, clientProcesses);
		}
	}
	
	/**
	 * 
	 * 0. Initialize MallobOuptut and all required distributor-classes
	 * 1. Sets result-distributor for watcher-manager
	 * 2. Initializes readers for log-lines
	 * 
	 * @param amountProcesses
	 * @param amountReaderThreads Amount of threads that each hold MallobOutputReader
	 * @param readingIntervalPerReadingThread Inteval between read of every MallobOutputReader
	 */
	public void initOutput(int amountProcesses) throws IllegalArgumentException
	{
		
		initializeMallobOuptut();
		
		watcherManager = MallobOutputWatcherManager.getInstance();
		this.watcherManager.setResultDistributor(resultDistributor);
		
		
		initializeReaders(amountProcesses);
	}
	
	
	
	/**
	 * Initialize mallobOuptut, logDistributor and resultDistributor
	 * mallobOutput is going to hold a reference of both the log and resultDistributor
	 * set log-distributor and result-distributor
	 */
	private void initializeMallobOuptut() {
		this.mallobOutput = MallobOutput.getInstance();
		this.logDistributor = this.mallobOutput.getOutputLogLineDistributor();
		this.resultDistributor = this.mallobOutput.getResultObjectDistributor();
	}
	
	/**
	 * Add file-readers, to watch specific files. Files that are being watched, are the regular log-files of each process.
	 * 
	 * All added Readers are also being started, when startMallobIO() is called
	 * 
	 * @param directoryPath of the file which is supposed to be watched 
	 * @param fileName name of the file which is supposed to be watched
	 */
	public void addIrregularReaders(String directoryPath, String fileName) {
		if (logDistributor == null) {
			throw new NullPointerException("Log-Distributor is null. Please call initOutput() first.");
		}
		if (irregularFilesReaders == null) {
			irregularFilesReaders = new ArrayList<>();
		}
		irregularFilesReaders.add(new MallobOutputReader(directoryPath, fileName, logDistributor));	
	}
	
	

	/**
	 * Initializes an array of readers (length === amountProcesses)
	 * Then, it inintializes a new MallobOutputReader for every index of the array
	 * Every Reader gets the same distributor
	 * 
	 * @param amountProcesses
	 */
	private void initializeReaders(int amountProcesses) 
	{
		this.readers = new MallobOutputReader[amountProcesses];
		for (int i = 0; i < amountProcesses; i++) {
			this.readers[i] = new MallobOutputReader(
					MallobFilePathGenerator.generateLogDirectoryPath(pathToMallobDirectory, i), 
					MallobFilePathGenerator.generateLogName(i),
					logDistributor);
		}
	}
	
	
	/**
	 * Adds all listeners the mallob-output 
	 * @throws FallobException, if connection to database could not be established (eventListener, ...)
	 */
	public void addStaticListeners() throws FallobException {
		
		DaoFactory dao = new DaoFactory();
		
		this.mallobOutput.addResultObjectListener(new JobResultListener(dao.getJobDao()));

		this.mallobOutput.addOutputLogLineListener(new EventListener(dao.getEventDao()));
		this.mallobOutput.addOutputLogLineListener(new JobStatusListener());
		this.mallobOutput.addOutputLogLineListener(MallobTimeListener.getInstance());
		this.mallobOutput.addOutputLogLineListener(new WarningListener(dao.getWarningDao()));
	}
	
	
	
	//----------------------------------start/stop mallobio--------------------
	
	
	/**
	 * Starts the reading of the log-files and the watching of the output directories 
	 */
	public void startMallobio() {		
		
		//collect all irregular readers into one array
		if (irregularFilesReaders != null) {
			MallobOutputReader[] allReaders = new MallobOutputReader[irregularFilesReaders.size() + readers.length];
			
			//copy regular readers
			for (int i = 0; i < readers.length; i++) {
				allReaders[i] = readers[i];
			}
			
			for (int i = 0; i < irregularFilesReaders.size(); i++) {
				allReaders[readers.length + i] = irregularFilesReaders.get(i);
			}
			readers = allReaders;
		} 
		
		
		readerThreadPool = new Thread[readers.length];
		for (int i = 0; i < readers.length; i++) {
			readerThreadPool[i] = new Thread(readers[i]);
			readerThreadPool[i].start();
		}
	}


	/**
	 * Stops all created Threads (joins them) After that no output of mallob is being read.
	 * 
	 * @throws InterruptedException
	 */
	public void stopMallobio() throws InterruptedException {
		for (MallobOutputReader r : readers) {
			r.stopWatchingLogFile();
		}
		for (Thread t : readerThreadPool) {
			t.join();
		}
	}
	
	public MallobOutputReader[] getReaders() {
		return this.readers;
	}
	

}
