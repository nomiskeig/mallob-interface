package edu.kit.fallob.mallobio.output;

import java.util.List;

public class MallobOutputReader {

	
	private String pathToMallobOutputLog;
	
	private List<OutputProcessor> processors;
	
	private int lastReadLine;
	
	
	public MallobOutputReader(String pathToMallobOutputLog) {
		this.pathToMallobOutputLog = pathToMallobOutputLog;
		this.lastReadLine = 0;
	}
	
	
	
	public void readNextLine() {
		
		
		lastReadLine++;
	}
	
	private void giveLineToProcessors(String line) {
		for(OutputProcessor p : processors) {
			p.processLogLine(line);
		}
	}
}
