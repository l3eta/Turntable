package org.l3eta.tt.manager.tabs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicButtonUI;

import org.l3eta.tt.Enums.Status;
import org.l3eta.tt.User;
import org.l3eta.tt.images.JQueryTheme;
import org.l3eta.tt.images.JQueryTheme.Images;
import org.l3eta.tt.images.JQueryTheme.Theme;
import org.l3eta.tt.images.Texture;
import org.l3eta.tt.images.Texture.TextureType;

public class PrivateChatTabTitle extends JPanel {

	private String pmImagesURL = "https://s3.amazonaws.com/static.turntable.fm/images/pm/status_indicators_flat.png";
	private static final long serialVersionUID = 766189015161232857L;
	private JLabel name, status;
	private User user;

	public PrivateChatTabTitle() {
		this(getDefault());
	}

	private static User getDefault() {
		User user = new User();
		user.setName("I wonder where this goes");
		user.setStatus(Status.AVAILABLE);
		return user;
	}

	public PrivateChatTabTitle(User user) {
		status = new JLabel(Texture.get(pmImagesURL, TextureType.URL).getSubIcon(user.getStatus().getLocation()));

		name = new JLabel(user.getName());
		name.setForeground(new Color(0x282828));
		name.setFont(new Font("Arial", Font.BOLD, 13));

		JButton settings = new JButton(JQueryTheme.get(Theme.DEFAULT).getIcon(Images.GEAR));
		settings.setUI(new JBUI());
		settings.setFocusable(false);
		settings.setBorderPainted(false);
		settings.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		settings.setContentAreaFilled(false);
		settings.addMouseListener(bml);

		JButton close = new JButton(JQueryTheme.get(Theme.DEFAULT).getIcon(Images.CLOSE));
		close.setUI(new JBUI());
		close.setFocusable(false);
		close.setBorderPainted(false);
		close.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		close.setContentAreaFilled(false);
		close.addMouseListener(bml);

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(status, GroupLayout.PREFERRED_SIZE, 9, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(name).addGap(3)
						.addComponent(settings, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE).addGap(1)
						.addComponent(close, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
										groupLayout.createParallelGroup(Alignment.LEADING, false)
												.addComponent(name, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(status, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
												.addComponent(settings, 0, 0, Short.MAX_VALUE)
												.addComponent(close, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)))
						.addGap(5)));
		setLayout(groupLayout);
	}

	public User getUser() {
		return user;
	}

	private final MouseListener bml = new MouseAdapter() {
		public void mouseEntered(MouseEvent e) {
			Component component = e.getComponent();
			if (component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(true);
			}
		}

		public void mouseExited(MouseEvent e) {
			Component component = e.getComponent();
			if (component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(false);
			}
		}
	};

	private class JBUI extends BasicButtonUI {
		protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect) {
			AbstractButton b = (AbstractButton) c;
			ButtonModel model = b.getModel();
			Icon icon = b.getIcon();
			Icon tmpIcon = null;

			if (icon == null) {
				return;
			}

			Icon selectedIcon = null;

			/* the fallback icon should be based on the selected state */
			if (model.isSelected()) {
				selectedIcon = (Icon) b.getSelectedIcon();
				if (selectedIcon != null) {
					icon = selectedIcon;
				}
			}

			if (!model.isEnabled()) {
				if (model.isSelected()) {
					tmpIcon = (Icon) b.getDisabledSelectedIcon();
					if (tmpIcon == null) {
						tmpIcon = selectedIcon;
					}
				}

				if (tmpIcon == null) {
					tmpIcon = (Icon) b.getDisabledIcon();
				}
			} else if (model.isPressed() && model.isArmed()) {
				tmpIcon = (Icon) b.getPressedIcon();
				if (tmpIcon != null) {
					// revert back to 0 offset
					clearTextShiftOffset();
				}
			} else if (b.isRolloverEnabled() && model.isRollover()) {
				if (model.isSelected()) {
					tmpIcon = (Icon) b.getRolloverSelectedIcon();
					if (tmpIcon == null) {
						tmpIcon = selectedIcon;
					}
				}

				if (tmpIcon == null) {
					tmpIcon = (Icon) b.getRolloverIcon();
				}
			}

			if (tmpIcon != null) {
				icon = tmpIcon;
			}
			Insets in = c.getInsets();
			icon.paintIcon(c, g, -(in.left / 2), -(in.top / 2));

		}
	}
}
