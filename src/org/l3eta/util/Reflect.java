package org.l3eta.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.l3eta.tt.event.Event;
import org.l3eta.tt.event.Event.EventMethod;
import org.l3eta.tt.event.EventListener;

public class Reflect {
	public static Method[] getMethods(Class<?> c, Class<?>... paramTypes) {
		List<Method> methods = new ArrayList<Method>();
		for (Method m : c.getDeclaredMethods()) {
			if (m.getParameterTypes().equals(paramTypes)) {
				methods.add(m);
			}
		}
		return methods.toArray(new Method[0]);
	}

	public static Method[] getEventMethods(Class<? extends EventListener> el) {
		List<Method> methods = new ArrayList<Method>();
		for (Method m : el.getDeclaredMethods()) {
			if (m.getAnnotation(EventMethod.class) != null) {
				if (m.getParameterTypes().length == 1) {
					Class<?> c = m.getParameterTypes()[0];
					if (Event.class.isAssignableFrom(c)
							&& !c.equals(Event.class)) {
						methods.add(m);
					}
				}
			}
		}
		return methods.toArray(new Method[0]);
	}
	
	public static Method[] getMethods(Class<?> clazz, Class<?> param) {
		ArrayList<Method> methods = new ArrayList<Method>();
		for(Method m : clazz.getDeclaredMethods()) {
			if(m.getParameterTypes().length == 1) {
				if(m.getParameterTypes()[0].equals(param)) {
					methods.add(m);
				}
			}
		}		
		return methods.toArray(new Method[0]);
	}

}
