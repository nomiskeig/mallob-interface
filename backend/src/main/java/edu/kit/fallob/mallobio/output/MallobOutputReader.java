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
import java.util.Optional;
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
	
	private long lastReadLine;
	


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
	 * 
	 * @param pathToDirectory path, in which the log-file is going to be
	 * @param logFileName name of the file
	 * @param processor Initial output-processor for this reader
	 */
	public MallobOutputReader(String pathToDirectory, String logFileName, OutputProcessor processor) {
		this.pathToDirectory = pathToDirectory;
		this.logFileName = logFileName;
		this.stopWatchingLogFile = false;
		this.absoluteLogFilePath = pathToDirectory + File.separator + logFileName;
		this.processors = new ArrayList<>();
		this.processors.add(processor);
	}
	
	
	/**
<<<<<<< HEAD
	 * Read the next Line(s) of the file, which path is sepcified in pathToMallobOutputLog
	 */
	public void readNextLine() {
		List<String> newLines = null;
		try (Stream<String> lines = Files.lines(Paths.get(absoluteLogFilePath))){
			newLines = lines.skip(lastReadLine).toList();
			
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
		
		if (newLines == null || newLines.size() == 0) {
			return;
		} else {
			this.giveLineToProcessors(newLines);
			lastReadLine += newLines.size();
		}
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
	 * @param list
	 */
	private void giveLineToProcessors(List<String> list) {
		if (list == null) {return;}
		for(OutputProcessor p : processors) {
			for (String s : list) {
				p.processLogLine(s);
			}
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
