package org.l3eta.tt;

import java.util.HashMap;
import java.util.Map;

import org.l3eta.tt.Enums.StickerCategory;
import org.l3eta.tt.Enums.StickerState;
import org.l3eta.tt.images.Texture;
import org.l3eta.tt.images.Texture.TextureType;
import org.l3eta.tt.util.Message;

public class Sticker {
	private static final Map<String, Sticker> stickerMap = new HashMap<String, Sticker>();
	private final String prefix = "http://static.turntable.fm/roommanager_assets/stickers/";
	private double price;
	private String id, description, name, path;
	private Texture texture, small;
	private StickerCategory category;
	private StickerState state;

	public Sticker(Message message) {
		category = StickerCategory.get(message.getString("category"));
		state = StickerState.get(message.getString("state"));
		path = message.getString("path");
		name = message.getString("name");
		price = message.getDouble("price");
		id = message.getString("_id");
		description = message.getString("description");
		texture = Texture.get(prefix + path + ".png", TextureType.URL);
		small = Texture.get(prefix + path + "_small.png", TextureType.URL);
		if(!stickerMap.containsKey(id)) 
			stickerMap.put(id, this);
	}

	public StickerState getState() {
		return state;
	}

	public StickerCategory getCategory() {
		return category;
	}

	public Texture getSmallTexture() {
		return small;
	}

	public Texture getTexture() {
		return texture;
	}

	public double getPrice() {
		return price;
	}

	public String getID() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}
	
	public static Sticker get(String id) {
		if(stickerMap.containsKey(id))
			return stickerMap.get(id);
		return null;
	}
}
