package org.l3eta.tt.task;

public class Task extends Thread {
	public static final long DEFAULT_WAIT = 1000;
	private long wait;
	private boolean running;
	private Runnable task;

	public Task(Runnable task) {
		this(task, DEFAULT_WAIT);
	}

	public Task(Runnable task, long wait) {
		this.task = task;
		this.wait = wait;
	}

	public Runnable getTask() {
		return task;
	}

	public boolean isRunning() {
		return running;
	}
	
	public void quit() {
		running = false;
	}
	
	public void start() {
		if(!running) {
			running = true;
			super.start();
		}		
	}

	public long getWait() {
		return wait;
	}

	public void sleep() {
		try {
			Thread.sleep(wait);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
