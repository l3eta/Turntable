package org.l3eta.tt.images;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import org.l3eta.tt.images.Texture.Location;
import org.l3eta.tt.images.Texture.TextureType;

//TODO write a look and feel changer.
public class JQueryTheme {
	static {
		themes = new HashMap<Theme, JQueryTheme>();
	}

	private static Map<Theme, JQueryTheme> themes;
	private Texture texture;

	public static JQueryTheme get(Theme theme) {
		if (themes.containsKey(theme)) {
			return themes.get(theme);
		}
		JQueryTheme jqt = new JQueryTheme(theme);
		themes.put(theme, jqt);
		return jqt;
	}

	JQueryTheme(Theme theme) {
		texture = Texture.get(JQueryTheme.class.getResource(theme.getFile()).toExternalForm(), TextureType.URL);
	}

	public Icon getIcon(Images img) {
		return texture.getSubIcon(img.getLocation());
	}

	public Image getImage(Images img) {
		return texture.getSubImage(img.getLocation());
	}

	public Image getImage() {
		return texture.getImage();
	}

	public enum Theme {
		DEFAULT("ui-icons_222222.png");
		private String file;

		Theme(String file) {
			this.file = file;
		}

		String getFile() {
			return file;
		}
	}

	public enum Images {
		CARAT_1_N(new Location(0, 0, 16, 16)), CARAT_1_NE(new Location(16, 0, 16, 16)), CARAT_1_E(new Location(32, 0, 16, 16)), CARAT_1_SE(
				new Location(48, 0, 16, 16)), CARAT_1_S(new Location(64, 0, 16, 16)), CARAT_1_SW(new Location(80, 0, 16, 16)), CARAT_1_W(
				new Location(96, 0, 16, 16)), CARAT_1_NW(new Location(112, 0, 16, 16)), CARAT_2_N_S(new Location(128, 0, 16, 16)), CARAT_2_E_W(
				new Location(144, 0, 16, 16)), TRIANGLE_1_N(new Location(0, 16, 16, 16)), TRIANGLE_1_NE(new Location(16, 16, 16, 16)), TRIANGLE_1_E(
				new Location(32, 16, 16, 16)), TRIANGLE_1_SE(new Location(48, 16, 16, 16)), TRIANGLE_1_S(new Location(64, 16, 16, 16)), TRIANGLE_1_SW(
				new Location(80, 16, 16, 16)), TRIANGLE_1_W(new Location(96, 16, 16, 16)), TRIANGLE_1_NW(new Location(112, 16, 16, 16)), TRIANGLE_2_N_S(
				new Location(128, 16, 16, 16)), TRIANGLE_2_E_W(new Location(144, 16, 16, 16)), ARROW_1_N(new Location(0, 32, 16, 16)), ARROW_1_NE(
				new Location(16, 32, 16, 16)), ARROW_1_E(new Location(32, 32, 16, 16)), ARROW_1_SE(new Location(48, 32, 16, 16)), ARROW_1_S(
				new Location(64, 32, 16, 16)), ARROW_1_SW(new Location(80, 32, 16, 16)), ARROW_1_W(new Location(96, 32, 16, 16)), ARROW_1_NW(
				new Location(112, 32, 16, 16)), ARROW_2_N_S(new Location(128, 32, 16, 16)), ARROW_2_NE_SW(new Location(144, 32, 16, 16)), ARROW_2_E_W(
				new Location(160, 32, 16, 16)), ARROW_2_SE_NW(new Location(176, 32, 16, 16)), ARROWSTOP_1_N(new Location(192, 32, 16, 16)), ARROWSTOP_1_E(
				new Location(208, 32, 16, 16)), ARROWSTOP_1_S(new Location(224, 32, 16, 16)), ARROWSTOP_1_W(new Location(240, 32, 16, 16)), ARROWTHICK_1_N(
				new Location(0, 48, 16, 16)), ARROWTHICK_1_NE(new Location(16, 48, 16, 16)), ARROWTHICK_1_E(new Location(32, 48, 16, 16)), ARROWTHICK_1_SE(
				new Location(48, 48, 16, 16)), ARROWTHICK_1_S(new Location(64, 48, 16, 16)), ARROWTHICK_1_SW(new Location(80, 48, 16, 16)), ARROWTHICK_1_W(
				new Location(96, 48, 16, 16)), ARROWTHICK_1_NW(new Location(112, 48, 16, 16)), ARROWTHICK_2_N_S(new Location(128, 48, 16,
				16)), ARROWTHICK_2_NE_SW(new Location(144, 48, 16, 16)), ARROWTHICK_2_E_W(new Location(160, 48, 16, 16)), ARROWTHICK_2_SE_NW(
				new Location(176, 48, 16, 16)), ARROWTHICKSTOP_1_N(new Location(192, 48, 16, 16)), ARROWTHICKSTOP_1_E(new Location(208, 48,
				16, 16)), ARROWTHICKSTOP_1_S(new Location(224, 48, 16, 16)), ARROWTHICKSTOP_1_W(new Location(240, 48, 16, 16)), ARROWRETURNTHICK_1_W(
				new Location(0, 64, 16, 16)), ARROWRETURNTHICK_1_N(new Location(16, 64, 16, 16)), ARROWRETURNTHICK_1_E(new Location(32, 64,
				16, 16)), ARROWRETURNTHICK_1_S(new Location(48, 64, 16, 16)), ARROWRETURN_1_W(new Location(64, 64, 16, 16)), ARROWRETURN_1_N(
				new Location(80, 64, 16, 16)), ARROWRETURN_1_E(new Location(96, 64, 16, 16)), ARROWRETURN_1_S(new Location(112, 64, 16, 16)), ARROWREFRESH_1_W(
				new Location(128, 64, 16, 16)), ARROWREFRESH_1_N(new Location(144, 64, 16, 16)), ARROWREFRESH_1_E(new Location(160, 64, 16,
				16)), ARROWREFRESH_1_S(new Location(176, 64, 16, 16)), ARROW_4(new Location(0, 80, 16, 16)), ARROW_4_DIAG(new Location(16,
				80, 16, 16)), EXTLINK(new Location(32, 80, 16, 16)), NEWWIN(new Location(48, 80, 16, 16)), REFRESH(new Location(64, 80, 16,
				16)), SHUFFLE(new Location(80, 80, 16, 16)), TRANSFER_E_W(new Location(96, 80, 16, 16)), TRANSFERTHICK_E_W(new Location(
				112, 80, 16, 16)), FOLDER_COLLAPSED(new Location(0, 96, 16, 16)), FOLDER_OPEN(new Location(16, 96, 16, 16)), DOCUMENT(
				new Location(32, 96, 16, 16)), DOCUMENT_B(new Location(48, 96, 16, 16)), NOTE(new Location(64, 96, 16, 16)), MAIL_CLOSED(
				new Location(80, 96, 16, 16)), MAIL_OPEN(new Location(96, 96, 16, 16)), SUITCASE(new Location(112, 96, 16, 16)), COMMENT(
				new Location(128, 96, 16, 16)), PERSON(new Location(144, 96, 16, 16)), PRINT(new Location(160, 96, 16, 16)), TRASH(
				new Location(176, 96, 16, 16)), LOCKED(new Location(192, 96, 16, 16)), UNLOCKED(new Location(208, 96, 16, 16)), BOOKMARK(
				new Location(224, 96, 16, 16)), TAG(new Location(240, 96, 16, 16)), HOME(new Location(0, 112, 16, 16)), FLAG(new Location(
				16, 112, 16, 16)), CALENDAR(new Location(32, 112, 16, 16)), CART(new Location(48, 112, 16, 16)), PENCIL(new Location(64,
				112, 16, 16)), CLOCK(new Location(80, 112, 16, 16)), DISK(new Location(96, 112, 16, 16)), CALCULATOR(new Location(112, 112,
				16, 16)), ZOOMIN(new Location(128, 112, 16, 16)), ZOOMOUT(new Location(144, 112, 16, 16)), SEARCH(new Location(160, 112,
				16, 16)), WRENCH(new Location(176, 112, 16, 16)), GEAR(new Location(192, 112, 16, 16)), HEART(
				new Location(208, 112, 16, 16)), STAR(new Location(224, 112, 16, 16)), LINK(new Location(240, 112, 16, 16)), CANCEL(
				new Location(0, 128, 16, 16)), PLUS(new Location(16, 128, 16, 16)), PLUSTHICK(new Location(32, 128, 16, 16)), MINUS(
				new Location(48, 128, 16, 16)), MINUSTHICK(new Location(64, 128, 16, 16)), CLOSE(new Location(80, 128, 16, 16)), CLOSETHICK(
				new Location(96, 128, 16, 16)), KEY(new Location(112, 128, 16, 16)), LIGHTBULB(new Location(128, 128, 16, 16)), SCISSORS(
				new Location(144, 128, 16, 16)), CLIPBOARD(new Location(160, 128, 16, 16)), COPY(new Location(176, 128, 16, 16)), CONTACT(
				new Location(192, 128, 16, 16)), IMAGE(new Location(208, 128, 16, 16)), VIDEO(new Location(224, 128, 16, 16)), SCRIPT(
				new Location(240, 128, 16, 16)), ALERT(new Location(0, 144, 16, 16)), INFO(new Location(16, 144, 16, 16)), NOTICE(
				new Location(32, 144, 16, 16)), HELP(new Location(48, 144, 16, 16)), CHECK(new Location(64, 144, 16, 16)), BULLET(
				new Location(80, 144, 16, 16)), RADIO_OFF(new Location(96, 144, 16, 16)), RADIO_ON(new Location(112, 144, 16, 16)), PIN_W(
				new Location(128, 144, 16, 16)), PIN_S(new Location(144, 144, 16, 16)), PLAY(new Location(0, 160, 16, 16)), PAUSE(
				new Location(16, 160, 16, 16)), SEEK_NEXT(new Location(32, 160, 16, 16)), SEEK_PREV(new Location(48, 160, 16, 16)), SEEK_END(
				new Location(64, 160, 16, 16)), SEEK_START(new Location(80, 160, 16, 16)), SEEK_FIRST(new Location(80, 160, 16, 16)), STOP(
				new Location(96, 160, 16, 16)), EJECT(new Location(112, 160, 16, 16)), VOLUME_OFF(new Location(128, 160, 16, 16)), VOLUME_ON(
				new Location(144, 160, 16, 16)), POWER(new Location(0, 176, 16, 16)), SIGNAL_DIAG(new Location(16, 176, 16, 16)), SIGNAL(
				new Location(32, 176, 16, 16)), BATTERY_0(new Location(48, 176, 16, 16)), BATTERY_1(new Location(64, 176, 16, 16)), BATTERY_2(
				new Location(80, 176, 16, 16)), BATTERY_3(new Location(96, 176, 16, 16)), CIRCLE_PLUS(new Location(0, 192, 16, 16)), CIRCLE_MINUS(
				new Location(16, 192, 16, 16)), CIRCLE_CLOSE(new Location(32, 192, 16, 16)), CIRCLE_TRIANGLE_E(
				new Location(48, 192, 16, 16)), CIRCLE_TRIANGLE_S(new Location(64, 192, 16, 16)), CIRCLE_TRIANGLE_W(new Location(80, 192,
				16, 16)), CIRCLE_TRIANGLE_N(new Location(96, 192, 16, 16)), CIRCLE_ARROW_E(new Location(112, 192, 16, 16)), CIRCLE_ARROW_S(
				new Location(128, 192, 16, 16)), CIRCLE_ARROW_W(new Location(144, 192, 16, 16)), CIRCLE_ARROW_N(new Location(160, 192, 16,
				16)), CIRCLE_ZOOMIN(new Location(176, 192, 16, 16)), CIRCLE_ZOOMOUT(new Location(192, 192, 16, 16)), CIRCLE_CHECK(
				new Location(208, 192, 16, 16)), CIRCLESMALL_PLUS(new Location(0, 208, 16, 16)), CIRCLESMALL_MINUS(new Location(16, 208,
				16, 16)), CIRCLESMALL_CLOSE(new Location(32, 208, 16, 16)), SQUARESMALL_PLUS(new Location(48, 208, 16, 16)), SQUARESMALL_MINUS(
				new Location(64, 208, 16, 16)), SQUARESMALL_CLOSE(new Location(80, 208, 16, 16)), GRIP_DOTTED_VERTICAL(new Location(0, 224,
				16, 16)), GRIP_DOTTED_HORIZONTAL(new Location(16, 224, 16, 16)), GRIP_SOLID_VERTICAL(new Location(32, 224, 16, 16)), GRIP_SOLID_HORIZONTAL(
				new Location(48, 224, 16, 16)), GRIPSMALL_DIAGONAL_SE(new Location(64, 224, 16, 16)), GRIP_DIAGONAL_SE(new Location(80,
				224, 16, 16));
		private Location loc;

		private Images(Location loc) {
			this.loc = loc;
		}

		public Location getLocation() {
			return loc;
		}

	}
}
