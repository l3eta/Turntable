package org.l3eta.tt.task;

public class DelayedTask extends Task {
	public DelayedTask(Runnable task) {
		super(task, DEFAULT_WAIT);
	}

	public DelayedTask(Runnable task, long wait) {
		super(task, wait);
	}

	public void run() {
		sleep();
		getTask().run();		
	}
}
