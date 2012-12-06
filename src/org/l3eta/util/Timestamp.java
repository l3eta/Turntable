package org.l3eta.util;

import java.util.Calendar;

public class Timestamp {
	private static boolean h24 = false;
	private int month, day, hour, min;
	private boolean am;

	private Timestamp(long l) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(l);
		month = c.get(Calendar.MONTH) + 1;
		day = c.get(Calendar.DAY_OF_MONTH);
		hour = (h24 ? c.get(Calendar.HOUR_OF_DAY) : c.get(Calendar.HOUR));
		if (!h24 && hour == 0) {
			hour = 12;
		}
		min = c.get(Calendar.MINUTE);
		am = c.get(Calendar.AM_PM) == Calendar.AM;

	}

	private String fixInt(int i) {
		return (i < 10) ? "0" + i : i + "";
	}

	public static void set24Hour(boolean on) {
		h24 = on;
	}

	public static Timestamp now() {
		return new Timestamp(System.currentTimeMillis());
	}

	public String toString() {
		return String.format("[%s:%s %s %s/%s]", fixInt(hour), fixInt(min),
				(am ? "AM" : "PM"), fixInt(month), fixInt(day));
	}

}
