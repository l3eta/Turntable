package org.l3eta.tt;

import org.l3eta.tt.images.Texture.Location;

public class Enums {
	public enum Status {
		AVAILABLE(0, 0, 9, 7), AWAY(0, 7, 9, 7), OFFLINE(0, 14, 9, 7), UNAVAILABLE(0, 21, 9, 7), NO_PM(0, 28, 9, 14);
		private Location loc;

		private Status(int x, int y, int w, int h) {
			loc = new Location(x, y, w, h);
		}

		public Location getLocation() {
			return loc;
		}

		public String toString() {
			return name().toLowerCase();
		}
	}

	public enum TurntableCommand {
		KILLDASHNINE, SPEAK, PMMED, REM_MODERATOR, SNAGGED, ROOM_CHANGED, REGISTERED, UPDATE_ROOM, UPDATE_STICKER_PLACEMENTS, NOSONG, ADD_DJ, NEWSONG, BOOTED_USER, DEREGISTERED, UPDATE_USER, UPDATE_VOTES, OTHER, REM_DJ, NEW_MODERATOR;

		public static TurntableCommand get(String string) {
			return valueOf(string.replace('.', '_').toUpperCase());
		}
	}

	public enum Laptop {
		LINUX, PC, MAC, IPHONE, CHROME, ANDROID;

		public static Laptop get(String laptop) {
			return valueOf(laptop.toUpperCase());
		}
	}

	public enum Vote {
		UP, DOWN;

		public String toString() {
			return name().toLowerCase();
		}

		public static Vote get(Object o) {
			return valueOf(o.toString().toUpperCase());
		}
	}

	public enum LogLevel {
		INFO, WARN, ERROR, DEBUG;
	}

	public enum StickerState {
		ACTIVE; // TODO figure out what the unactive is

		public static StickerState get(String name) {
			return valueOf(name.toUpperCase());
		}
	}

	public enum StickerCategory {
		LAPTOP_STICKER;

		public static StickerCategory get(String name) {
			return valueOf(name.toUpperCase());
		}
	}

	public enum ListenerPriority {
		HIGHEST, HIGH, NORMAL, LOW, LOWEST;
	}

}
