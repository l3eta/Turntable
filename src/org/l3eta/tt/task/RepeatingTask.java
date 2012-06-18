package org.l3eta.tt.task;

public class RepeatingTask extends Task {

	public RepeatingTask(Runnable task) {
		super(task, DEFAULT_WAIT);
	}

	public RepeatingTask(Runnable task, long wait) {
		super(task, wait);
	}

	public void run() {
		while (isRunning()) {
			getTask().run();
			sleep();
		}
	}
}
