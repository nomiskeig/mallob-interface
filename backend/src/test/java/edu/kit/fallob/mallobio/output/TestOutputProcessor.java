package edu.kit.fallob.mallobio.output;

import java.util.List;
import java.util.ArrayList;


public class TestOutputProcessor implements OutputProcessor {
	
	public List<String> lines = new ArrayList<>();

	
	public TestOutputProcessor() {
		
	}
	
	@Override
	public void processLogLine(String logLine) {
		lines.add(logLine);
	}
	
	public void getSize() {
		System.out.println(lines.size());
	}

}
