package org.l3eta.tt.images;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.l3eta.tt.images.Texture.Location;
import org.l3eta.tt.images.Texture.TextureType;

public class ImageLoader {
	private static HashMap<String, Image> cache;
	static {
		init();
	}

	public static void init() {
		cache = new HashMap<String, Image>();
		Texture texture = Texture.get(loadImage("icons.png"), TextureType.URL);
		for (LoaderImage img : LoaderImage.values()) {
			cache.put(img.getName(), texture.getSubImage(img.getLocation()));
		}
		cache.put("icon", getImage("icon.png"));
	}

	private static String loadImage(String name) {
		return ImageLoader.class.getResource(name).toExternalForm();

	}

	public static Icon getIcon(String name) {
		if (cache.containsKey(name))
			return new ImageIcon(cache.get(name));
		return new ImageIcon(getImage(name));
	}

	public static Image getImage(String name) {
		if (cache.containsKey(name))
			return cache.get(name);
		return Texture.get(loadImage(name), TextureType.URL).getImage();
	}

	public enum LoaderImage {
		TAG_ADD("addTag", new Location(0, 0, 16, 16)), CURRENT_SONG("currentSong", new Location(16, 0, 16, 16)),
		FOLDER("folder", new Location(32, 0, 16, 16)), TOP("top", new Location(48, 0, 16, 16)), BOTTOM("bottom",
				new Location(0, 16, 16, 16)), DELETE("del", new Location(16, 16, 16, 16)), SAVE("save", new Location(
				32, 16, 16, 16)), SCRIPT("script", new Location(48, 16, 16, 16)), ADD_FAV("addFav", new Location(0, 32,
				16, 16)), NEW("new", new Location(16, 32, 16, 16)), DEL_FAV("delFav", new Location(32, 32, 16, 16));

		private String name;
		private Location location;

		private LoaderImage(String name, Location loc) {
			this.name = name;
			this.location = loc;
		}

		private String getName() {
			return name;
		}

		private Location getLocation() {
			return location;
		}
	}
}
