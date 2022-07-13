package edu.kit.fallob.mallobio.output;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

public class MallobOutputReaderTests {
	
	/**
	 * Amount of lines the test-file is going to have
	 */
	public static final int TEST_FILE_LINES = 100;
	
	//for test on windows 
	public static final String FILE_PATH = System.getProperty("user.dir") + "\\testFile.txt";
	
	//for test on linux
	//public static final String FILE_PATH = "/testFile.txt";

	
	private static File testFile;
	
	
	/**
	 * 
	 */
	@Test
	public void readFirstLineTest() {
		MallobOutputReader reader = new MallobOutputReader(testFile.getAbsolutePath());
		TestOutputProcessor lineProcessor = new TestOutputProcessor();
		reader.addProcessor(lineProcessor);
		
		reader.readNextLine();
		assertTrue(lineProcessor.lines.size() == 1);
		assertTrue(lineProcessor.lines.get(0).equals(getFileLine(0)));
		
	}
	
	
	@Test
	public void readMultipleLines() {
		MallobOutputReader reader = new MallobOutputReader(testFile.getAbsolutePath());
		TestOutputProcessor lineProcessor = new TestOutputProcessor();
		reader.addProcessor(lineProcessor);
		
		
		for (int i = 0; i < TEST_FILE_LINES; i++) {
			reader.readNextLine();
			assertTrue(lineProcessor.lines.size() == (i + 1));
			assertTrue(lineProcessor.lines.get(i).equals(getFileLine(i)));
		}
	}
	
	@BeforeAll
	public static void setupFile() throws IOException {
		//createFile
		testFile = new File(FILE_PATH);
		
		//cerate File-content
		String fileContent = "";
		for (int i = 0; i < TEST_FILE_LINES; i++) {
			fileContent += getFileLine(i) + System.lineSeparator();
		}
		
		//write to file
		FileWriter writer = new FileWriter(testFile.getAbsolutePath());
		writer.write(fileContent);
		writer.close();
	}
	
	
	private static String getFileLine(int lineIndex) {
		return "Line" + lineIndex;
	}
	
	
	@AfterAll
	public static void deleteFile() {
		testFile.delete();
	}
	
}
