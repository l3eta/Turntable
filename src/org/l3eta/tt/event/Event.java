package org.l3eta.tt.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.l3eta.tt.Enums.ListenerPriority;
import org.l3eta.tt.util.Timestamp;

public class Event {
	private boolean cancelled;
	private Timestamp created;

	public Event() {
		created = Timestamp.now();
	}

	public Timestamp getCreated() {
		return created; 
	}
	
	public boolean isCancelled() {
		return cancelled;
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface EventMethod {
	}
	
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Listener {
		public ListenerPriority priority() default ListenerPriority.NORMAL;
	}
}
