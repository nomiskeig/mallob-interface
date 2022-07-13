package edu.kit.fallob.mallobio;

import edu.kit.fallob.mallobio.output.MallobClientOutputWatcher;
import edu.kit.fallob.mallobio.output.MallobOutputReader;
import edu.kit.fallob.mallobio.output.MallobOutputRunnerThread;
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

	
	private Thread[] directoryWatcherThreadPool;
	private MallobOutputRunnerThread[] directoryWatcherRunners;
	private MallobClientOutputWatcher[] watchers;
	
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
	 * @param clientProcessIDs
	 * @param amountProcesses
	 * @param amountReaderThreads Amount of threads that each hold MallobOutputReader
	 * @param readingIntervalPerReadingThread Inteval between read of every MallobOutputReader
	 */
	public void initParsingModule(String mallbLogDirectory, 
			int[] clientProcessIDs, 
			int amountProcesses,
			int amountWatcherThreads,
			int watchingIntervalPerWatcherThread,
			int amountReaderThreads,
			int readingIntervalPerReadingThread) throws IllegalArgumentException
	{
		
		
		if (amountWatcherThreads > clientProcessIDs.length || amountReaderThreads > amountProcesses) {
			throw new IllegalArgumentException("Cant have more threads than watchers/readers");
		}
		
		initializeMallobOuptut();
		
		initializeReaders(mallbLogDirectory, 
				 amountProcesses,
				 amountReaderThreads,
				 readingIntervalPerReadingThread);
		
		initializeWatchers(mallbLogDirectory,
				clientProcessIDs,
				amountWatcherThreads,
				watchingIntervalPerWatcherThread);
		
		//after this mallobio can be started 
				
		
		
		
	}
	
	private void initializeWatchers(String mallobBaseDirectory, 
			int[] clientProcessIDs, 
			int amountWatcherThreads,
			int watchingIntervalPerWatcherThread) 
	{

		directoryWatcherRunners = new MallobOutputRunnerThread[amountWatcherThreads];
		directoryWatcherThreadPool = initializeThreadPool(directoryWatcherRunners, 
				watchingIntervalPerWatcherThread);
		
		//create watchers and map them to a watcher-thread
		int amountWatchers = clientProcessIDs.length;
		watchers = new MallobClientOutputWatcher[amountWatchers];
		int roundRobinCounter = 0;
		for (int i = 0; i < amountWatchers; i++) {
			watchers[i] = new MallobClientOutputWatcher(mallobBaseDirectory, clientProcessIDs[i]);
			directoryWatcherRunners[roundRobinCounter].addActionChecker(watchers[i]);
			roundRobinCounter++;
			if (roundRobinCounter >= directoryWatcherRunners.length) {
				roundRobinCounter = 0;
			}
		}
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
		readerThreadPool = initializeThreadPool(readerRunners, readingIntervalPerReadingThread);
				
		//create MallobReader and map them to a readerThread
		int roundRobinCounter = 0;
		readers = new MallobOutputReader[amountProcesses];
		for (int i = 0; i < amountProcesses; i++) {
			
			//initialize MallobOutputreader
			readers[i] = new MallobOutputReader(generateLogFilePath(i, mallbLogDirectory));
			
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
		this.mallobOutput = new MallobOutput(resultDistributor, logDistributor);
	}
	
	/**
	 * Generate the output-log file for a process with a given process-id, 
	 * when given the mallob, base-directory (basePath) and a process id.
	 * @param processID
	 * @param basePath
	 * 
	 * @return The generated path as described above
	 */
	private String generateLogFilePath(int processID, String basePath) {
		return null;
	}
	
	

	
	/**
	 * Initialize a thread pool of runner-threads, 
	 * which each hold a time interval specified in interval
	 * @param runners
	 * @param interval
	 * @return The
	 */
	private Thread[] initializeThreadPool(MallobOutputRunnerThread[] runners, int interval) {
		Thread[] threadPool = new Thread[runners.length];
		for (int i = 0; i < runners.length; i++) {
			runners[i] = new MallobOutputRunnerThread(interval);
			threadPool[i] = new Thread(runners[i]);
		}
		return threadPool;
	}
	
	
	
	//----------------------------------start/stop mallobio--------------------
	
	
	/**
	 * Starts the reading of the log-files and the watching of the output directories 
	 */
	public void startMallobio() {
		//start log-reader thread-pool
		for (Thread t : readerThreadPool) {
			t.start();
		}
		
		for (Thread t : directoryWatcherThreadPool) {
			t.start();
		}
	}
	
	/**
	 * Stops all created Threads (joins them) After that no output of mallob is being read.
	 * 
	 * @throws InterruptedException
	 */
	public void stopMallobio() throws InterruptedException {
		//stop readers
		for (int i = 0; i < readerThreadPool.length; i++) {
			this.readerRunners[i].stopRunning();
			readerThreadPool[i].join();
		}
		
		//Stop watchers
		for (int i = 0; i < directoryWatcherThreadPool.length; i++) {
			this.directoryWatcherRunners[i].stopRunning();
			directoryWatcherThreadPool[i].join();
		}
	}
	
	public MallobOutput getMallobOutput() throws NullPointerException {
		if (mallobOutput == null) {
			throw new NullPointerException("Not yet initialized. Please initialize Module first.");
		}
		return mallobOutput;
	}
}
