package edu.kit.fallob.mallobio.output;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Simon Wilhelm Schübel
 * @version 1.0
 *
 */
public class MallobOutputRunnerThread implements Runnable {
	
	/**
	 * Creates an array of threads, which were given the runners. Each runner is getting its own thread.
	 * Each runner is initialized with the same interval
	 * @param runners
	 * @param interval
	 * @return created threadpool (array of threads for malloboutputrunnerthreads)
	 */
	public static Thread[] initializeThreadPool(MallobOutputRunnerThread[] runners, int interval) {
		Thread[] threadPool = new Thread[runners.length];
		for (int i = 0; i < runners.length; i++) {
			runners[i] = new MallobOutputRunnerThread(interval);
			threadPool[i] = new Thread(runners[i]);
		}
		return threadPool;
	}
	
	
	public static void startThreadPoolExecution(Thread[] threadpool) {
		for (Thread t : threadpool) {
			t.start();
		}
	}
	
	public static void stopThreadPoolExecution(Thread[] threadpool, MallobOutputRunnerThread[] runnerThreads) 
			throws IllegalArgumentException, InterruptedException 
	{
		if (threadpool.length != runnerThreads.length) {
			throw new IllegalArgumentException("Cannot stop thread if more threads than runners or vice versa");
		}
		for (int i = 0; i < threadpool.length; i++) {
			runnerThreads[i].stopRunning();
			threadpool[i].join();
		}
	}
	
	private List<MallobOutputActionChecker> checkers;
	private int checkActionInterval;
	private boolean runCheckers = true;
	
	public MallobOutputRunnerThread(int checkActionInterval) {
		this.checkActionInterval = checkActionInterval;
		this.checkers = new ArrayList<>();
	}
	
	@Override
	public void run() {
			
		while(runCheckers) {
			while(this.checkers.size() == 0) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			executeRunIteration();
		}
	}
	
	
	public void executeRunIteration() {
		List<MallobOutputActionChecker> checkersToRemove = new ArrayList<>();
		
		for (MallobOutputActionChecker checker : checkers) {
			checker.checkForAction();
			if (checker.isDone()) {
				checkersToRemove.add(checker);
			}
			try {
				Thread.sleep(checkActionInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		for (MallobOutputActionChecker checker : checkersToRemove) {
			this.checkers.remove(checker);
		}
	}
	
	
	
	public void stopRunning() {
		this.runCheckers = false;
	}
	
	public void restartRunning() {
		this.runCheckers = true;
	}
	
	/**
	 * 
	 * @return the amount of action checkers this thread currently manages (amount of action checkers that have been added, 
	 * and are not done or have not been removed yet)
	 */
	public int getAmountActionCheckers() {
		return this.checkers.size();
	}
	
	
	public void addActionChecker(MallobOutputActionChecker checker) {
		this.checkers.add(checker);
		
		//if the thread had zero checkers to execute - was effectively doing nothing but waiting,
		//the thread has to be notiyfied, so that it can continue runnig 
		if (this.checkers.size() == 1) {
			this.notify();
		}
	}
	
	public void removeActionChecker(MallobOutputActionChecker checker) {
		this.checkers.remove(checker);
	}

}
