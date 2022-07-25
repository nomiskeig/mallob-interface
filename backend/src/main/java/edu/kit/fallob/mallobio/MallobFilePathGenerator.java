package edu.kit.fallob.mallobio;

import java.io.File;

public class MallobFilePathGenerator {
	
	
	public static final String LOG_FILE_PATH_BEFROE_ID = File.separator + "jobs.";
	
	public static final String LOG_FILE_EXTENSION = ".txt";
	
	
	

	
	/**
	 * Generate the output-log file for a process with a given process-id, 
	 * when given the mallob, base-directory (basePath) and a process id.
	 * @param processID
	 * @param basePath
	 * 
	 * @return The generated path as described above
	 */
	public static String generateLogFilePath(int processID, String basePath) {
		return generateLogDirectoryPath(processID, basePath) + File.separator + generateLogName(processID) + File.separator + LOG_FILE_EXTENSION;
	}
	
	
	/**
	 * Create the path to the out-directory (the directory in which the log-file lies) of the process with processID
	 * @return
	 */
	public static String generateLogDirectoryPath(int processID, String basePath) {
		return basePath + LOG_FILE_PATH_BEFROE_ID + Integer.toString(processID) + ".out";
	}
	
	/**
	 * 
	 * @param processID
	 * @return
	 */
	public static String generateLogName(int processID) {
		return Integer.toString(processID);
	}
	
	
	/**
	 * Generates the path to a certain client process' in-directory
	 * @param clientProcessID
	 * @param basePath
	 * @return
	 */
	public static String generatePathToMallobSubmitDirectory(String basePath, int clientProcessID) {
		return basePath += ".api"+File.separator+"jobs." + clientProcessID + File.separator + "in" + File.separator;
	}
	
	/**
	 * Generates the path to a certain client process' in-directory
	 * @param clientProcessID
	 * @param basePath 
	 * @return
	 */
	public static String generatePathToMallobAbortDirectory(String basePath, int clientProcessID) {
		return basePath += ".api"+File.separator+"jobs." + clientProcessID + File.separator + "in" + File.separator;
	}
}
