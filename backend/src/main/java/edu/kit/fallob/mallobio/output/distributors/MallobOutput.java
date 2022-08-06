package edu.kit.fallob.mallobio.output.distributors;

import edu.kit.fallob.mallobio.listeners.outputloglisteners.OutputLogLineListener;
import edu.kit.fallob.mallobio.listeners.resultlisteners.ResultObjectListener;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class MallobOutput {

	private static MallobOutput mallobOutput = null;

	
	private ResultObjectDistributor resultObjectDistributor;
	
	private OutputLogLineDistributor outputLogLineDistributor;
	
	
	private MallobOutput() {
		this.resultObjectDistributor = new ResultObjectDistributor();
		this.outputLogLineDistributor = new OutputLogLineDistributor();
	}

	public static MallobOutput getInstance() {
		if (mallobOutput == null) {
			mallobOutput = new MallobOutput();
		}

		return mallobOutput;
	}
	
	
	public void addResultObjectListener(ResultObjectListener listener) {
		this.resultObjectDistributor.addListener(listener);
	}
	
	public void removeResultObjectListener(ResultObjectListener listener) {
		this.resultObjectDistributor.removeListener(listener);
	}
	
	public void addOutputLogLineListener(OutputLogLineListener listener) {
		this.outputLogLineDistributor.addListener(listener);
	}
	
	public void removeOutputLogLineListener(OutputLogLineListener listener) {
		this.outputLogLineDistributor.removeListener(listener);
	}

	public ResultObjectDistributor getResultObjectDistributor() {
		return resultObjectDistributor;
	}

	public OutputLogLineDistributor getOutputLogLineDistributor() {
		return outputLogLineDistributor;
	}
}
