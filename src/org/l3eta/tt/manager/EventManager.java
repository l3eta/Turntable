package org.l3eta.tt.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l3eta.tt.Enums.ListenerPriority;
import org.l3eta.tt.event.Event;
import org.l3eta.tt.event.Event.Listener;
import org.l3eta.tt.event.EventListener;

import org.l3eta.tt.util.Reflect;

public class EventManager {
	private Map<ListenerPriority, List<ListenerData>> ls;

	public EventManager() {
		ls = new HashMap<ListenerPriority, List<ListenerData>>();
		for (ListenerPriority e : ListenerPriority.values()) {
			ls.put(e, new ArrayList<ListenerData>());
		}
	}

	public void registerListener(EventListener el) {
		ListenerData data = new ListenerData(el);
		ls.get(data.getPriority()).add(data);
	}

	public void sendEvent(Event event) {
		for (ListenerPriority e : ListenerPriority.values()) {
			List<ListenerData> listeners = ls.get(e);
			for (ListenerData listener : listeners) {
				if (listener.hasEvent(event))
					listener.invoke(event);
			}
		}
	}

	class ListenerData {
		private List<Class<?>> events;
		private EventListener listener;
		private Method[] methods;

		public ListenerData(EventListener listener) {
			this.listener = listener;
			events = new ArrayList<Class<?>>();
			methods = Reflect.getEventMethods(listener.getClass());
			for (Method m : methods) {
				addEvent(m.getParameterTypes()[0]);
			}
		}

		public ListenerPriority getPriority() {
			return listener.getClass().getAnnotation(Listener.class).priority();
		}

		private void addEvent(Class<?> c) {
			if (!events.contains(c))
				events.add(c);
		}

		public boolean hasEvent(Event e) {
			return events.contains(e.getClass());
		}

		public Method[] getMethods() {
			return methods;
		}

		public void invoke(Event event) {
			try {
				for (Method m : methods) {
					if (m.getParameterTypes()[0].equals(event.getClass()))
						m.invoke(listener, event);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
