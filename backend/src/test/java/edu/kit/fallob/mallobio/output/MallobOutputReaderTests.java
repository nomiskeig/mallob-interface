package edu.kit.fallob.mallobio.output;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class MallobOutputReaderTests {
	
	/**
	 * Amount of lines the test-file is going to have
	 */
	public static final int TEST_FILE_LINES = 100;
	
	public static final String DIRECTORY_PATH = System.getProperty("user.dir");
	
	public static final String FILE_NAME = "testFile.txt";
	
	//for test on windows 
	public static final String FILE_PATH = DIRECTORY_PATH + File.separator + FILE_NAME;
		
	private static File testFile;
	
	private static MallobOutputReader reader;
	private static TestOutputProcessor lineProcessor;
	
	

	@Test
	public void readFirstLineTest() {	
		
		reader.readNextLine();
		assertTrue(lineProcessor.lines.size() == 1);
		assertTrue(lineProcessor.lines.get(0).equals(getFileLine(0)));
		
	}
	
	
	@Test
	public void readMultipleLines() {
		for (int i = 0; i < TEST_FILE_LINES; i++) {
			reader.readNextLine();
			assertTrue(lineProcessor.lines.size() == (i + 1));
			assertTrue(lineProcessor.lines.get(i).equals(getFileLine(i)));
		}
	}
	
	
	@BeforeEach
	public void setupReader() {
		reader = new MallobOutputReader(DIRECTORY_PATH, FILE_NAME);
		lineProcessor = new TestOutputProcessor();
		reader.addProcessor(lineProcessor);
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
