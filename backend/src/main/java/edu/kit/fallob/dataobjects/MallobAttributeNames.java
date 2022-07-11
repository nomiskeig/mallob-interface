package edu.kit.fallob.dataobjects;

/**
 * 
 *
 * @author Simon Wilhelm Schübel
 * @version 1.0
 * 
 * Holds the names of all currently known job-attribute in a JSON (When submitting Job to mallob).
 *
 */
public class MallobAttributeNames {
	
	
	
	public static final String MALLOB_USER = "user";
	public static final String MALLOB_JOB_NAME = "name";
	public static final String MALLOB_WALLCLOCK_LIMIT = "wallclock-limit";
	public static final String MALLOB_CPU_LIMIT = "cpu-limit";
	public static final String MALLOB_ARRIVAL = "arrival";
	public static final String MALLOB_MAX_DEMAND = "max-demand";
	public static final String MALLOB_DEPENDENCIES = "dependencies";
	public static final String MALLOB_CONTENT_MODE = "content-mode";
	public static final String MALLOB_INTERRUPT = "interrupt";
	public static final String MALLOB_INCREMENTAL = "incremental";
	public static final String MALLOB_LITERALS = "literals";
	public static final String MALLOB_ASSUMPTIONS = "assumptions";
	public static final String MALLOB_DONE = "done";
	
	public static final String[] ALL_ATTRIBUTES = {MALLOB_USER,
			MALLOB_JOB_NAME, MALLOB_WALLCLOCK_LIMIT, MALLOB_CPU_LIMIT, MALLOB_ARRIVAL,
			MALLOB_MAX_DEMAND, MALLOB_DEPENDENCIES, MALLOB_CONTENT_MODE, MALLOB_INTERRUPT,
			MALLOB_INCREMENTAL, MALLOB_LITERALS, MALLOB_ASSUMPTIONS, MALLOB_DONE};
}
