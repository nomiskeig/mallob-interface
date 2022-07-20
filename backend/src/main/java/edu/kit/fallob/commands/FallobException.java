package edu.kit.fallob.commands;

public class FallobException extends Exception {
	
	private int errorcode;
	
	public FallobException(int errorcode) {
		this.errorcode = errorcode;
	}

	public int getErrorcode() {
		return errorcode;
	}
	
	

}
