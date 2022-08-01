package edu.kit.fallob.dataobjects;

import java.io.File;
import java.util.List;

public class JobDescription {
	
	private List<File> descriptionFiles;
	private SubmitType submitType;
	
	
	
	public JobDescription(List<File> descriptionFiles, SubmitType submitType) {
		this.setDescriptionFiles(descriptionFiles);
		this.setSubmitType(submitType);
	}
	
	
	public SubmitType getSubmitType() {
		return submitType;
	}
	public void setSubmitType(SubmitType submitType) {
		this.submitType = submitType;
	}
	public List<File> getDescriptionFiles() {
		return descriptionFiles;
	}
	public void setDescriptionFiles(List<File> descriptionFiles) {
		this.descriptionFiles = descriptionFiles;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JobDescription) {
			return descriptionFiles.equals(((JobDescription) obj).getDescriptionFiles()) && submitType.equals(((JobDescription) obj).submitType);
		}
		return false;
	}
}
