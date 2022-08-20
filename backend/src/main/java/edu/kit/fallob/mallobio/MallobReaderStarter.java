package edu.kit.fallob.mallobio;

import java.util.List;
import java.util.ArrayList;


import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.mallobio.input.MallobInputImplementation;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.EventListener;
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
			throw new IllegalArgumentException("Cannot have more threads than readers. Ensure, that you have at max. as many threads as processes.");
		}
		
		initializeMallobOuptut();
		
		watcherManager = MallobOutputWatcherManager.getInstance();
		watcherManager.setResultDistributor(resultDistributor);
		
		
		
		readerRunners = new MallobOutputRunnerThread[amountReaderThreads];
		readerThreadPool = MallobOutputRunnerThread.initializeThreadPool(readerRunners, readingIntervalPerReadingThread);
		initializeReaders(amountProcesses, readingIntervalPerReadingThread);
		

		//after this mallobio can be started 
	}
	
	
	
	

	/**
	 * See Description of initParsingMdule, This Method does step 1-2.5
	 * 
	 * 
	 * @param mallbLogDirectory
	 * @param amountProcesses
	 * @param readingIntervalPerReadingThread
	 */
	private void initializeReaders( 
			int amountProcesses,
			int readingIntervalPerReadingThread) 
	{
		if (logDistributor == null) {
			throw new NullPointerException("No log-distributor instance present at initialisation of readers.");
		}
		
				
		//create MallobReader for the log file of all processes 
		readers = new MallobOutputReader[amountProcesses];

		for (int i = 0; i < readers.length; i++) {
			//initialize MallobOutputreader and give them the correct log-distributor 

			readers[i] = new MallobOutputReader(MallobFilePathGenerator.generateLogFilePath(i, pathToMallobDirectory), logDistributor);
		}
		
		mapReaderToThread(readers, readerRunners);

	}
	
	/**
	 * Given an array of readers, it maps each reader onto a thread, such that all readers are evenly distributed amongst all threads
	 * @param readersToMap
	 * @param readerThreads
	 */
	private void mapReaderToThread(MallobOutputReader[] readersToMap, MallobOutputRunnerThread[] readerThreads) {
		if (readersToMap == null || readerThreads == null) {
			throw new NullPointerException("Cannot map onto null-object.");
		}
		
		int roundRobinCounter = 0;
		for (int i = 0; i < readersToMap.length; i++) {
			//add outputreader-to readerRunner
			readerThreads[roundRobinCounter].addActionChecker(readersToMap[i]);
			roundRobinCounter++;
			
			if (roundRobinCounter >= readerThreads.length) {

				roundRobinCounter = 0;
			}
		}
	}
	
	
	/**
	 * Add file-readers, to watch specific files. Files that are being watched, are the regular log-files of each process.
	 * 
	 * All added Readers are also being started, when startMallobIO() is called
	 * 
	 * @param directoryPath of the file which is supposed to be watched 
	 * @param fileName name of the file which is supposed to be watched
	 */
	public void addIrregularReaders(String filePath) {
		if (irregularFilesReaders == null) {
			irregularFilesReaders = new ArrayList<>();
		}
		MallobOutputReader r = new MallobOutputReader(filePath, logDistributor);
		irregularFilesReaders.add(r);	
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
		if (readerThreadPool == null || readerRunners == null || readers == null) {
			throw new NullPointerException("Cannot start mallobio. Module has not been initialized properly. Threadpool, Threads or Readeres missing.");
		}
		//add irregular readers to the current threads 
		if (irregularFilesReaders != null) {
            MallobOutputReader[] outputReaders = new MallobOutputReader[irregularFilesReaders.size()];
            irregularFilesReaders.toArray(outputReaders);
			this.mapReaderToThread(outputReaders, this.readerRunners);
		}
		
		//start reader-execution
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
	
	public MallobOutputReader[] getReaders() {
		return this.readers;
	}
	
	public MallobOutputRunnerThread[] getReaderThreads() {
		return this.readerRunners;
	}
}
