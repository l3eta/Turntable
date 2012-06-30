package org.l3eta.tt;

import static org.l3eta.tt.Util.random;

public final class Emoji {

	public static String getRandomHeart() {
		return HeartColor.get(random(HeartColor.colors())).toString();
	}

	public static String getHeart(HeartColor color) {
		return color.toString();
	}

	public enum HeartColor {
		RED("heart"), BLUE("blue_heart"), PURPLE("purple_heart"), YELLOW("yellow_heart"), GREEN("green_heart");

		private String ico;

		private HeartColor(String ico) {
			this.ico = ico;
		}

		private static HeartColor get(int index) {
			return values()[index];
		}

		private static int colors() {
			return values().length;
		}

		public String toString() {
			return ":" + ico + ":";
		}
	}
}
