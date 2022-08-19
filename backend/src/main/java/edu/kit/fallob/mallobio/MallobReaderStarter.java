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
import edu.kit.fallob.mallobio.output.MallobOutputRunnerThread;
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
	private MallobOutputRunnerThread[] readerRunners;
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
	 * 0. Initialize MallobOuptut and all required distributor-classe
	 * 
	 * 
	 * 1. Calls initializeRunnerThreadPool() to initialize the MallobOutputReaderRunner.
	 * Each ReaderRunner is a thread. The method furthermore initializes a fiexed number of threads
	 * (amntReaderThreads).
	 * 
	 * 2. Initlaize MallobOutputReader - as many as there are processes - and map them to the 
	 * MallobOutputReaderRunner. A round-robin procedure is used, to ensure a uniform
	 * distribution of mallobOutputReader to MallbOutputReaderRunner
	 * 
	 * 2.5 give every MallobOutputReader the same LogLineDistributor (initialized in step 0)
	 * 
	 * 3.Initialize MallobOutputWatchers - works like starting the readers
	 * 
	 * @param amountProcesses
	 * @param amountReaderThreads Amount of threads that each hold MallobOutputReader
	 * @param readingIntervalPerReadingThread Inteval between read of every MallobOutputReader
	 */
	public void initOutput( 
			int amountProcesses,
			int amountReaderThreads,
			int readingIntervalPerReadingThread) throws IllegalArgumentException
	{
	
		if (amountReaderThreads > amountProcesses) {
			throw new IllegalArgumentException("Cant have more threads than readers");
		}
		
		watcherManager = MallobOutputWatcherManager.getInstance();
		
		
		initializeMallobOuptut();
		
		
		initializeReaders(pathToMallobDirectory, 
				 amountProcesses,
				 amountReaderThreads,
				 readingIntervalPerReadingThread);
		

		//after this mallobio can be started 
	}
	
	

	/**
	 * See Description of initParsingMdule, This Method does step 1-2.5
	 * 
	 * 
	 * @param mallbLogDirectory
	 * @param amountProcesses
	 * @param amountReaderThreads
	 * @param readingIntervalPerReadingThread
	 */
	private void initializeReaders(String mallbLogDirectory, 
			int amountProcesses,
			int amountReaderThreads,
			int readingIntervalPerReadingThread) 
	{
		
		readerRunners = new MallobOutputRunnerThread[amountReaderThreads + 1];
		readerThreadPool = MallobOutputRunnerThread.initializeThreadPool(readerRunners, readingIntervalPerReadingThread);
				
		//create MallobReader and map them to a readerThread
		int roundRobinCounter = 0;
		readers = new MallobOutputReader[amountProcesses];
		for (int i = 0; i < amountProcesses; i++) {
			
			//initialize MallobOutputreader
			readers[i] = new MallobOutputReader(MallobFilePathGenerator.generateLogFilePath(i, mallbLogDirectory));
			
			//give output-reader the correct distributor :
			readers[i].addProcessor(logDistributor);
			
			//add outputreader-to readerRunner
			readerRunners[roundRobinCounter].addActionChecker(readers[i]);
			roundRobinCounter++;
			if (roundRobinCounter >= readerRunners.length - 1) {
				roundRobinCounter = 0;
			}
		}
		MallobOutputReader jobMappingFileReader = new MallobOutputReader(MallobFilePathGenerator.generatePathToJobMappingsLogFile(mallbLogDirectory, amountProcesses - 1));
		jobMappingFileReader.addProcessor(logDistributor);
		readerRunners[amountReaderThreads].addActionChecker(jobMappingFileReader);
	}
	
	
	/**
	 * Initialize mallobOuptut, logDistributor and resultDistributor
	 * mallobOutput is going to hold a reference of both the log and resultDistributor
	 */
	private void initializeMallobOuptut() {
		this.mallobOutput = MallobOutput.getInstance();

		this.logDistributor = this.mallobOutput.getOutputLogLineDistributor();
		this.resultDistributor = this.mallobOutput.getResultObjectDistributor();
		
		this.watcherManager.setResultDistributor(resultDistributor);
		
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
		MallobOutputRunnerThread.startThreadPoolExecution(readerThreadPool);
	}


	/**
	 * Stops all created Threads (joins them) After that no output of mallob is being read.
	 * 
	 * @throws InterruptedException
	 */
	public void stopMallobio() throws InterruptedException {
		MallobOutputRunnerThread.stopThreadPoolExecution(readerThreadPool, readerRunners);
	}
	
	public MallobOutput getMallobOutput() throws NullPointerException {
		if (mallobOutput == null) {
			throw new NullPointerException("Not yet initialized. Please initialize Module first.");
		}
		return mallobOutput;
	}
}
