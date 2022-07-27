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

	
	private ResultObjectDistributor resultObjectDistributor;
	
	private OutputLogLineDistributor outputLogLineDistributor;
	
	
	public MallobOutput(ResultObjectDistributor resultObjectDistributor,
			OutputLogLineDistributor outputLogLineDistributor) {
		this.resultObjectDistributor = resultObjectDistributor;
		this.outputLogLineDistributor = outputLogLineDistributor;
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
	
	
}
