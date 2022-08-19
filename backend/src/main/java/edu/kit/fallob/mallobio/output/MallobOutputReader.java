package edu.kit.fallob.mallobio.output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class MallobOutputReader implements MallobOutputActionChecker {

	
	private String pathToMallobOutputLog;
	
	private List<OutputProcessor> processors;
	
	private int lastReadLine;
	
	/**
	 * Constructor of MallobOutputReader 
	 * 
	 * 
	 * @param pathToMallobOutputLog log-file which this reader is reading
	 */
	public MallobOutputReader(String pathToMallobOutputLog) {
		this.pathToMallobOutputLog = pathToMallobOutputLog;
		this.lastReadLine = 0;
		processors = new ArrayList<>();
	}
	
	
	/**
	 * read all lines form the file which have not been read up until this point
	 * Give all lines to registered processors
	 */
	public void readNextLine() {		
		List<String> newLines = null;
		try (Stream<String> lines = Files.lines(Paths.get(pathToMallobOutputLog))){
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
	 * 
	 * @param newLines
	 */
	private void giveLineToProcessors(List<String> newLines) {
		if (newLines == null) {return;}
		for(OutputProcessor p : processors) {
			for (String line : newLines) {
				p.processLogLine(line);
			}
		}
	}
	
	
	public void addProcessor(OutputProcessor p) {
		processors.add(p);
	}


	@Override
	public void checkForAction() {
		readNextLine();
	}


	@Override
	public boolean isDone() {
		return false;
	}
}
