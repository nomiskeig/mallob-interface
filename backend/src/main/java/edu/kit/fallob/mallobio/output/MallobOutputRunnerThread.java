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
	
	/**
	 * Given an array of Threads, it starts every thread in the array
	 * @param threadpool
	 */
	public static void startThreadPoolExecution(Thread[] threadpool) {
		for (Thread t : threadpool) {
			t.start();
		}
	}
	
	/**
	 * Given an array of threads and MallobOutputRunnerThreads - which *have!* to be same length
	 * It first calls stopRunning() on every MallobOUtputRunnerThread
	 * and after that tries to join to threads
	 * 
	 * @param threadpool
	 * @param runnerThreads
	 * @throws IllegalArgumentException if threadpool and runnerthreads are not the same length
	 * @throws InterruptedException if joining of thread fails
	 */
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
	private boolean runCheckers = false;
	
	public MallobOutputRunnerThread(int checkActionInterval) {
		this.checkActionInterval = checkActionInterval;
		this.checkers = new ArrayList<>();
	}
	
	@Override
	public void run() {
		this.runCheckers = true;
		
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
	
	
	/*Stops execution of the run-method */
	public void stopRunning() {
		this.runCheckers = false;
	}
	
	/*Possibly restarts execution of the run-method- method has to be called again */
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
	
	/**
	 * Adds an action checker to this threads checkers.
	 * If the thread has been started and did not hold any action checkers, this method would also call notify() - 
	 * this is because the thread sets itself to wait, if not checkers are to execute .
	 * 
	 * If the thread hasn't been started, it wouldn't call notfiy().
	 * @param checker
	 */
	public void addActionChecker(MallobOutputActionChecker checker) {
		this.checkers.add(checker);
		
		
		if (this.runCheckers && this.checkers.size() == 1) {
			this.notify();
		}
	}
	
	public void removeActionChecker(MallobOutputActionChecker checker) {
		this.checkers.remove(checker);
	}

}
