package edu.kit.fallob.mallobio.input;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MallobInputTests {
	
	public static final String TEST_DIRECTORY_PATH = System.getProperty("user.dir") + "\\src\\main\\resources\\inputTests";
	
	
	public static final int[] CLIENT_PROCESSES = {0};
	
	private static MallobInput mInput;
	
	@Test
	public void testJSONCreation() {
		
	}
	
	@BeforeEach
	public void setupBeforeEach() {
		mInput = new MallobInputImplementation(TEST_DIRECTORY_PATH, CLIENT_PROCESSES);
	}
	
	
	@BeforeAll
	public static void beforeAll() {
		new File(TEST_DIRECTORY_PATH).mkdirs();
	}
	
	@AfterAll
	public static void afterAll() {
		
	}
}
