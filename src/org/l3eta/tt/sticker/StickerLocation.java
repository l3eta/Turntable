package org.l3eta.tt.sticker;

import org.l3eta.tt.Sticker;
import org.l3eta.tt.util.Message;

public class StickerLocation {
	private Sticker sticker;
	private int left, top;
	private double angle;
	
	public StickerLocation(Message message) {
		top = message.getInt("top");
		angle = message.getDouble("angle");
		left = message.getInt("left");
		sticker = Sticker.get(message.getString("sticker_id"));
	}
	
	public Sticker getSticker() {
		return sticker;
	}
	
	public int getTop() {
		return top;
	}
	
	
	public int getLeft() {
		return left;
	}
	
	public double getAngle() {
		return angle;
	}
}
