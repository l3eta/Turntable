package org.l3eta.turntable.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Line {
	private String line;

	public Line(String line) {
		this.line = line;
	}

	public Line complete(int start) {
		String[] args = line.split(" ");
		StringBuilder string = new StringBuilder();
		for (int i = start; i < args.length; i++) {
			string.append(args[i] + " ");
		}
		return new Line(string.toString().substring(0, string.length() - 1));
	}

	public boolean match(String regex) {
		Matcher match = Pattern.compile(regex).matcher(this.toString());
		return match.find();
	}

	public boolean successful() {
		if (line.endsWith("success: true}"))
			return true;
		return false;
	}

	public Line match(String regex, int index) {
		Matcher match = Pattern.compile(regex).matcher(this.toString());
		return new Line(match.group(index));
	}

	public Line[] getDjs() {
		String l = Pattern.compile("djs:\\{").split(line)[1];
		String data = l.substring(1, l.indexOf("}"));
		if (data.equals("")) {
			return new Line[] { new Line("null") };
		}
		String[] djs = Pattern.compile("[0-9]\\: ").split(data);
		Line[] array = new Line[djs.length];

		for (int i = 0; i < djs.length; i++) {
			array[i] = new Line(djs[i]).remove('"');
		}
		return array;
	}

	public Line remove(char c) {
		return remove(String.valueOf(c));
	}

	public Line remove(String data) {
		return new Line(line.replace(data, ""));
	}

	public boolean isNull() {
		return equals("null");
	}

	public Line[] split(String data) {
		String[] d = line.split(data);
		Line[] lines = new Line[d.length];
		for (int i = 0; i < d.length; i++) {
			lines[i] = new Line(d[i]);
		}
		return lines;
	}

	public Line[] split(Pattern p, Line data) {
		String[] d = p.split(data.toString());
		Line[] lines = new Line[d.length];
		for (int i = 0; i < d.length; i++) {
			lines[i] = new Line(d[i]);
		}
		return lines;
	}

	public Line[] split(Pattern p) {
		String[] d = p.split(this.toString());
		Line[] lines = new Line[d.length];
		for (int i = 0; i < d.length; i++) {
			lines[i] = new Line(d[i]);
		}
		return lines;
	}

	public String getString(String o) {
		String v = Pattern.compile(o + ": \"").split(line)[1];
		return v.substring(0, v.indexOf("\""));
	}

	public boolean equals(Object obj) {
		return this.toString().equalsIgnoreCase(String.valueOf(obj));
	}

	public int getInt(String o) {
		String i = ", ";
		if (o.equals("avatarid"))
			i = "}";
		String v = Pattern.compile(o + ": ").split(line)[1];
		return Integer.parseInt(v.substring(0, v.indexOf(i)));
	}

	public double getDouble(String o) {
		String v = Pattern.compile(o + ": ").split(line)[1];
		return Double.parseDouble(v.substring(0, v.indexOf(", ")));
	}

	public String toString() {
		return line;
	}

	public int indexOf(String string) {
		return line.indexOf(string);
	}

	public Line substring(int indexOf) {
		return new Line(line.substring(indexOf));
	}

	public Line substring(int indexOf, int end) {
		return new Line(line.substring(indexOf, end));
	}

	public boolean contains(String string) {
		return line.contains(string);
	}

	public boolean startsWith(String string) {
		return line.startsWith(string);
	}
}