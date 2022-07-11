package edu.kit.fallob.mallobio;

import edu.kit.fallob.mallobio.output.MallobOutputReader;
import edu.kit.fallob.mallobio.output.MallobOutputReaderRunner;
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
	private MallobOutputReaderRunner[] readerRunners;
	private MallobOutputReader[] readers;
	
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
	 * 3.Initialize MallobOutputWatchers TODO
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
			int amountReaderThreads,
			int readingIntervalPerReadingThread) 
	{
		
		initializeMallobOuptut();
		
		initializeReaders(mallbLogDirectory, 
				 amountProcesses,
				 amountReaderThreads,
				 readingIntervalPerReadingThread);
		
		
		
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
		
		
		initializeRunnerThreadPool(amountReaderThreads, readingIntervalPerReadingThread);
				
		//create MallobReader and map them to a readerThread
		int roundRobinCounter = 0;
		readers = new MallobOutputReader[amountProcesses];
		for (int i = 0; i < amountProcesses; i++) {
			
			//initialize MallobOutputreader
			readers[i] = new MallobOutputReader(generateLogFilePath(i, mallbLogDirectory));
			
			//give output-reader the correct distributor :
			readers[i].addProcessor(logDistributor);
			
			//add outputreader-to readerRunner
			readerRunners[roundRobinCounter].addMallobOutputReader(readers[i]);
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
	 * Initializes the readerRunnerThreadPool
	 * 
	 * @param amountReaderThreads specifies the amout of threads the threadpool is going to have
	 * @param readingIntervalPerReadingThread
	 */
	private void initializeRunnerThreadPool(int amountReaderThreads, 
			int readingIntervalPerReadingThread) {
		readerThreadPool = new Thread[amountReaderThreads];
		readerRunners = new MallobOutputReaderRunner[amountReaderThreads];
		for (int i = 0; i < amountReaderThreads; i++) {
			readerRunners[i] = new MallobOutputReaderRunner(readingIntervalPerReadingThread);
			readerThreadPool[i] = new Thread(readerRunners[i]);
		}
	}
	
	
	/**
	 * Starts the reading of the log-files and the watching of the output directories 
	 */
	public void startMallobio() {
		//start log-reader thread-pool
		for (Thread t : readerThreadPool) {
			t.start();
		}
	}
	
	public MallobOutput getMallobOutput() throws NullPointerException {
		if (mallobOutput == null) {
			throw new NullPointerException("Not yet initialized. Please initialize Module first.");
		}
		return mallobOutput;
	}
}
