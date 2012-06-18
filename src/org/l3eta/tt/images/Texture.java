package org.l3eta.tt.images;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Texture {
	private static Map<String, Texture> textures;	
	private Map<Location, Image> subs;
	private Image image;

	
	public static Texture get(String loc, TextureType type) {
		if(textures == null)
			textures = new HashMap<String, Texture>();
		String name = loc + "-" + type.name();
		if(textures.containsKey(name)) 
			return textures.get(name);
		else {
			Texture texture = new Texture(loc, type);
			textures.put(name, texture);
			return texture;
		}
	}

	private Texture(String loc, TextureType type) {
		try {
			if (type == TextureType.FILE) {
				image = ImageIO.read(new File(loc));
			} else if (type == TextureType.URL) {
				image = ImageIO.read(new URL(loc));
			}
			subs = new HashMap<Location, Image>();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Image getSubImage(Location loc) {
		if(subs.containsKey(loc))
			return subs.get(loc);
		else {
			Image i = getSubImage(loc.getX(), loc.getY(), loc.getWidth(), loc.getHeight());
			subs.put(loc, i);
			return i;
		}
	}
	
	public Icon getSubIcon(Location loc) {
		return new ImageIcon(getSubImage(loc));
	}

	private Image getSubImage(int x, int y, int w, int h) {
		return ((BufferedImage) image).getSubimage(x, y, w, h);
	}
	
	public Image getImage() {
		return image;
	}

	public enum TextureType {
		URL, FILE;
	}

	public static class Location {
		private int x, y, w, h;

		public Location(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getWidth() {
			return w;
		}

		public int getHeight() {
			return h;
		}
	}	
}
