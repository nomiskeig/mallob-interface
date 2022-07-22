package edu.kit.fallob.mallobio;

import edu.kit.fallob.mallobio.output.MallobClientOutputWatcher;
import edu.kit.fallob.mallobio.output.MallobFilePathGenerator;
import edu.kit.fallob.mallobio.output.MallobOutputReader;
import edu.kit.fallob.mallobio.output.MallobOutputRunnerThread;
import edu.kit.fallob.mallobio.output.MallobOutputWatcherManager;
import edu.kit.fallob.mallobio.output.distributors.MallobOutput;
import edu.kit.fallob.mallobio.output.distributors.OutputLogLineDistributor;
import edu.kit.fallob.mallobio.output.distributors.ResultObjectDistributor;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 * 
 * Initializes the module 
 *
 */
public class MallobReaderStarter {
	



	
	protected String pathToMallobLogDirectory;
	
	
	private Thread[] readerThreadPool;
	private MallobOutputRunnerThread[] readerRunners;
	private MallobOutputReader[] readers;

	
	private MallobOutputWatcherManager watcherManager;
	
	private MallobOutput mallobOutput;
	private OutputLogLineDistributor logDistributor;
	private ResultObjectDistributor resultDistributor;
	
	
	
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
	 * @param mallbLogDirectory
	 * @param amountProcesses
	 * @param amountReaderThreads Amount of threads that each hold MallobOutputReader
	 * @param readingIntervalPerReadingThread Inteval between read of every MallobOutputReader
	 */
	public void initParsingModule(String mallbLogDirectory, 
			int amountProcesses,
			int amountWatcherThreads,
			int watchingIntervalPerWatcherThread,
			int amountReaderThreads,
			int readingIntervalPerReadingThread) throws IllegalArgumentException
	{
		
		
		if (amountReaderThreads > amountProcesses) {
			throw new IllegalArgumentException("Cant have more threads than watchers/readers");
		}
		
		initializeMallobOuptut();
		
		
		initializeReaders(mallbLogDirectory, 
				 amountProcesses,
				 amountReaderThreads,
				 readingIntervalPerReadingThread);
		
		initializeWatchers(mallbLogDirectory,
				amountWatcherThreads,
				watchingIntervalPerWatcherThread);
		//after this mallobio can be started 
			
	}
	
	
	private void initializeWatchers(String mallobBaseDirectory, 
			int amountWatcherThreads,
			int watchingIntervalPerWatcherThread) 
	{
		
		watcherManager = MallobOutputWatcherManager.getInstance();
		watcherManager.setup(mallobBaseDirectory, amountWatcherThreads, watchingIntervalPerWatcherThread);
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
		
		readerRunners = new MallobOutputRunnerThread[amountReaderThreads];
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
			if (roundRobinCounter >= readerRunners.length) {
				roundRobinCounter = 0;
			}
		}
	}
	
	
	/**
	 * Initialize mallobOuptut, logDistributor and resultDistributor
	 * mallobOutput is going to hold a reference of both the log and resultDistributor
	 */
	private void initializeMallobOuptut() {
		this.logDistributor = new OutputLogLineDistributor();
		this.resultDistributor = new ResultObjectDistributor();
		this.watcherManager.setResultDistributor(resultDistributor);
		this.mallobOutput = new MallobOutput(resultDistributor, logDistributor);
	}
	
	
	
	
	//----------------------------------start/stop mallobio--------------------
	
	
	/**
	 * Starts the reading of the log-files and the watching of the output directories 
	 */
	public void startMallobio() {

		MallobOutputRunnerThread.startThreadPoolExecution(readerThreadPool);
		this.watcherManager.startThreads();
	}


	/**
	 * Stops all created Threads (joins them) After that no output of mallob is being read.
	 * 
	 * @throws InterruptedException
	 */
	public void stopMallobio() throws InterruptedException {
		MallobOutputRunnerThread.stopThreadPoolExecution(readerThreadPool, readerRunners);
		this.watcherManager.stopThreads();
	}
	
	public MallobOutput getMallobOutput() throws NullPointerException {
		if (mallobOutput == null) {
			throw new NullPointerException("Not yet initialized. Please initialize Module first.");
		}
		return mallobOutput;
	}
}
