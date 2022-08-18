package edu.kit.fallob.mallobio.output;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class MallobOutputReader implements Runnable {

	
	
	
	private String pathToDirectory;
	private String logFileName;
	private String absoluteLogFilePath;
	
	private boolean stopWatchingLogFile;
	
	private List<OutputProcessor> processors;
	
	private int lastReadLine;
	

	
	/**
	 * 
	 * @param pathToDirectory path, in which the log-file is going to be
	 * @param logFileName name of the file
	 */
	public MallobOutputReader(String pathToDirectory, String logFileName) {
		this.pathToDirectory = pathToDirectory;
		this.logFileName = logFileName;
		this.stopWatchingLogFile = false;
		this.absoluteLogFilePath = pathToDirectory + File.separator + logFileName;
		this.processors = new ArrayList<>();
	}
	
	
	/**
	 * Read the next Line(s) of the file, which path is sepcified in pathToMallobOutputLog
	 */
	public void readNextLine() {
		String line = null;
		try (Stream<String> lines = Files.lines(Paths.get(absoluteLogFilePath))){
			line = lines.skip(lastReadLine).findFirst().get();
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
		this.giveLineToProcessors(line);
		lastReadLine++;
	}
	
	/**
	 * @throws InterruptedException 
	 * @throws IOException 
	 * 
	 */
	public void listenToLogFile() throws InterruptedException, IOException {
		
		WatchService watcher = getWatcher();

		//retreive the result from the directory 
		while(!stopWatchingLogFile) {
			WatchKey nextKey = watcher.take();
			
			
			for (WatchEvent<?> event : nextKey.pollEvents()) {
				
				if (event.kind() != StandardWatchEventKinds.ENTRY_CREATE) {
					continue;
				}
								
				WatchEvent<Path> ev = (WatchEvent<Path>)event;
		        Path filename = ev.context();
		        		        
				if (logFileWasChanged(filename.toString())){
					readNextLine();
				}
			}
		}
	}
	
	
	/**
	 * Checks, if the a given string equals logFileName
	 * @param string
	 * @return 
	 */
	private boolean logFileWasChanged(String fileName) {
		return logFileName.equals(fileName);
	}

	/**
	 * Creates a watcher for the directory-path stored in pathToMallobDirectory.
	 * Also registers modify-events in this directory.
	 * 
	 * @return the watcher for the specified directory
	 * @throws IOException
	 */
	private WatchService getWatcher() throws IOException {
		//setup watcher 
		Path dir = Paths.get(pathToDirectory);
		WatchService watcher = FileSystems.getDefault().newWatchService();
		try {
			dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
		} catch(IOException s) {
			s.printStackTrace();
		}
		return watcher;
	}
	
	/**
	 * 
	 * @param line
	 */
	private void giveLineToProcessors(String line) {
		if (line == null) {return;}
		for(OutputProcessor p : processors) {
			p.processLogLine(line);
		}
	}
	
	
	public void addProcessor(OutputProcessor p) {
		processors.add(p);
	}
	
	/**
	 * Tells the reader to stop watching after the log file. Essentially kills the thread after the next 
	 * modification of the log-file
	 */
	public void stopWatchingLogFile() {
		this.stopWatchingLogFile = true;
	}

	@Override
	public void run() {
		try {
			listenToLogFile();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
}
