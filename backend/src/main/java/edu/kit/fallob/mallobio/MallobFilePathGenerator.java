package edu.kit.fallob.mallobio;

public class MallobFilePathGenerator {

	public static final String LOG_FILE_PATH_BEFROE_ID = "/jobs.";

	public static final String LOG_FILE_EXTENSION = ".txt";

	public static String BASEPATH_MALLOB_API;

	/**
	 * Generate the output-log file for a process with a given process-id, when
	 * given the mallob, base-directory (basePath) and a process id.
	 * 
	 * @param processID
	 * @param basePath
	 * 
	 * @return The generated path as described above
	 */
	public static String generateLogFilePath(int processID) {
		return generateLogDirectoryPath(processID) + "/" + generateLogName(processID) + "/" + LOG_FILE_EXTENSION;
	}

	/**
	 * Create the path to the out-directory (the directory in which the log-file
	 * lies) of the process with processID
	 * 
	 * @return
	 */
	public static String generateLogDirectoryPath(int processID) {
		return BASEPATH_MALLOB_API + LOG_FILE_PATH_BEFROE_ID + Integer.toString(processID) + ".out";
	}

	/**
	 * Generate the path for a process' out-directory from mallob
	 * 
	 * @param processID
	 * @return
	 */
	public static String generatePathToMallobOutDirectory(int processID) {
		return BASEPATH_MALLOB_API + ".api/jobs." + Integer.toString(processID) + "/out/";
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
	 * 
	 * @param clientProcessID
	 * @param basePath
	 * @return
	 */
	public static String generatePathToMallobSubmitDirectory(int clientProcessID) {
		return BASEPATH_MALLOB_API + ".api/jobs." + clientProcessID + "/in/";
	}

	/**
	 * Generates the path to a certain client process' in-directory
	 * 
	 * @param clientProcessID
	 * @param basePath
	 * @return
	 */
	public static String generatePathToMallobAbortDirectory(int clientProcessID) {
		return BASEPATH_MALLOB_API + ".api/jobs." + clientProcessID + "/in/";
	}
}
