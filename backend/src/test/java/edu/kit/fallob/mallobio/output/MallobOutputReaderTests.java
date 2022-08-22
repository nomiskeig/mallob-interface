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
	
	private static int linesInFile = 0;
	private static String currentFileContent = "";
	
	
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
		assertTrue(lineProcessor.lines.get(0).equals(getFileLine(0)));	
	}

	
	@Test
	public void testReadingSingleIncrement() throws IOException {
		//test, if reading is correct if reader is called multiple times
		reader.readNextLine();
		
		
		int increment = 1;
		addToFile(increment);
		reader.readNextLine();
		
		//check that last logline looks like expected 
		assertTrue(lineProcessor.lines.get(lineProcessor.lines.size() - 1).equals(getFileLine(linesInFile - 1)));
	}
	
	@Test
	public void testReadingMultipleIncrement() throws IOException {
		reader.readNextLine();
		
		int beforeIncrement = linesInFile;
		assertTrue(lineProcessor.lines.size() == beforeIncrement);
		
		int increment = 4;
		addToFile(increment);
		reader.readNextLine();
		
		assertTrue(beforeIncrement + increment == linesInFile);
		assertTrue(lineProcessor.lines.size() == linesInFile);
	}
	

	@Test
	public void testNoNewLine() {
		reader.readNextLine();
		assertTrue(lineProcessor.lines.size() == linesInFile);
		reader.readNextLine();
		assertTrue(lineProcessor.lines.size() == linesInFile);
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
		addToFile(TEST_FILE_LINES);
	}
	
	
	public static void addToFile(int amountLines) throws IOException {
		
		for (int i = 0; i < amountLines; i++) {
			currentFileContent += getFileLine(linesInFile) + System.lineSeparator();
			linesInFile++;
		}
		
		//write to file
		FileWriter writer = new FileWriter(testFile.getAbsolutePath());
		writer.write(currentFileContent);
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