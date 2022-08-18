package edu.kit.fallob.mallobio;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.mallobio.input.MallobInputImplementation;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.EventListener;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.JobListener;
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
	 * 
	 * 1. Sets result-distributor for watcher-manager
	 * 2. Initializes readers for log-lines
	 * 
	 * @param amountProcesses
	 * @param amountReaderThreads Amount of threads that each hold MallobOutputReader
	 * @param readingIntervalPerReadingThread Inteval between read of every MallobOutputReader
	 */
	public void initOutput(int amountProcesses) throws IllegalArgumentException
	{
	
		watcherManager = MallobOutputWatcherManager.getInstance();
		
		this.mallobOutput = MallobOutput.getInstance();
		this.logDistributor = this.mallobOutput.getOutputLogLineDistributor();
		this.resultDistributor = this.mallobOutput.getResultObjectDistributor();
		this.watcherManager.setResultDistributor(resultDistributor);
		
		
		
		initializeReaders(amountProcesses);
		
	}
	
	

	/**
	 * Initializes an array of readers (length === amountProcesses)
	 * Then, it inintializes a new MallobOutputReader for every index of the array
	 * Every Reader gets the same 
	 * 
	 * @param amountProcesses
	 */
	private void initializeReaders(int amountProcesses) 
	{
		this.readers = new MallobOutputReader[amountProcesses];
		for (int i = 0; i < amountProcesses; i++) {
			this.readers[i] = new MallobOutputReader(MallobFilePathGenerator.generateOutDirectoryPath(amountProcesses, pathToMallobDirectory), 
					MallobFilePathGenerator.generateLogName(i));
			this.readers[i].addProcessor(logDistributor);
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
	
}
