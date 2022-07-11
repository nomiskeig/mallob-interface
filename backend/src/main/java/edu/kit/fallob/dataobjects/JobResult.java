package dataobjects;

import java.io.File;

public class JobResult {
	
	private File result;
	
	public JobResult(File resultFile) {
		this.setResult(resultFile);
	}

	public File getResult() {
		return result;
	}

	public void setResult(File result) {
		this.result = result;
	}
	
	

}
