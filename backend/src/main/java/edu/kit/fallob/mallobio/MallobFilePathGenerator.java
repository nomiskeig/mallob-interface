package edu.kit.fallob.mallobio;

import java.io.File;

public class MallobFilePathGenerator {
	
	
	public static final String API_DIRECTORY = ".api";
	
	public static final String LOG_FILE_NAME = "log.";
	
	public static final String JSON_EXTENSION = ".json";
	
	public static final String JOB_INPUT_DIRECTORY = "jobs.";
	
	public static final String IN_DIRECTORY = "in";
	
	public static final String OUT_DIRECTORY = "out";
	
	
	
	
	

	
	/**
	 * Generate the output-log file for a process with a given process-id, 
	 * when given the mallob, base-directory (basePath) and a process id.
	 * @param processID
	 * @param basePath
	 * 
	 * @return The generated path as described above
	 */
	public static String generateLogFilePath(int processID, String basePath) {
		return basePath + API_DIRECTORY + File.separator + Integer.toString(processID) + File.separator + LOG_FILE_NAME + generateLogName(processID);
	}
	
	
	/**
	 * Create the path to the out-directory (the directory in which the solution lies) of the process with processID
	 * @return
	 */
	public static String generateOutDirectoryPath(int processID, String basePath) {
		return basePath + API_DIRECTORY + File.separator + JOB_INPUT_DIRECTORY + Integer.toString(processID) + File.separator + OUT_DIRECTORY + File.separator;
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
		return basePath += API_DIRECTORY + File.separator + JOB_INPUT_DIRECTORY + clientProcessID + File.separator + IN_DIRECTORY + File.separator;
	}
	
	/**
	 * Generates the path to a certain client process' in-directory
	 * @param clientProcessID
	 * @param basePath 
	 * @return
	 */
	public static String generatePathToMallobAbortDirectory(String basePath, int clientProcessID) {
		//return basePath += ".api"+File.separator+"jobs." + clientProcessID + File.separator + "in" + File.separator;
		return generatePathToMallobSubmitDirectory(basePath, clientProcessID);
	}
	
	/**
	 * generates the file-name of a result file depending on the job name (for the result) and the user, who submitted the result
	 * @param jobName
	 * @param username
	 * @return
	 */
	public static String generateResultName(String jobName, String username) {
		 return username + "." + jobName + JSON_EXTENSION;
	}
	
	
	public static String generatePathToJobMappingsLogFile(String basePath, int clientProcessID) {
		return generateLogFilePath(clientProcessID, basePath) + ".i";
	}
	
	public static String generatePathToReaderLogFile(String basePath, int clientProcessID) {
		return generateLogFilePath(clientProcessID, basePath) + ".reader";
	}

    public static String generatePathToJobFSLogFile(String basePath, int clientProcessID) {
        return generateLogFilePath(clientProcessID, basePath) + ".i-fs";
    }


}
