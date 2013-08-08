package de.edvdb.ffw.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.Random;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import de.edvdb.ffw.system.ClientConfig;
import de.edvdb.ffw.util.Utils;

public class PausePanel extends JPanel {
	private static final long serialVersionUID = 1781924043962979998L;
	private JLabel lbl, lDateTime;
	private Timer timer;
	private Dimension iconSize, dateSize;
	private static final Font BIG_FONT = new Font("Arial", Font.BOLD, 50);
	protected static final int LBL_HEIGHT = 50;
	
	@SuppressWarnings("unused")
	public PausePanel() {
		super();
		this.setBackground(Color.BLACK);
		
		lbl = new JLabel("test");
//		ImageIcon icon = new ImageIcon(ClientConfig.IDLEFILE);
		ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("screensaver.png"));
		iconSize = new Dimension(icon.getIconWidth(), icon.getIconHeight());
		lbl.setSize(iconSize);
		lbl.setPreferredSize(iconSize);
		lbl.setIcon(icon);
		
		lDateTime = new JLabel("dd.MM.yyyy HH:mm:ss");
		dateSize = new Dimension(icon.getIconWidth(), LBL_HEIGHT);
		lDateTime.setSize(dateSize);
		lDateTime.setPreferredSize(dateSize);
		lDateTime.setVerticalTextPosition(SwingConstants.CENTER);
		lDateTime.setHorizontalTextPosition(SwingConstants.CENTER);
		lDateTime.setAlignmentX(Component.CENTER_ALIGNMENT);
		lDateTime.setAlignmentY(Component.CENTER_ALIGNMENT);
		lDateTime.setVerticalAlignment(SwingConstants.CENTER);
		lDateTime.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		lDateTime.setFont(BIG_FONT);
		lDateTime.setForeground(Color.WHITE);
		this.setLayout(null);
		this.add(lDateTime);
		this.add(lbl);

		JPanel tmp = this;

		Action updateCursorAction = new AbstractAction() {
			private static final long serialVersionUID = 8122692013835742640L;
			boolean shouldDraw = false;

			public void actionPerformed(ActionEvent e) {
				Random r = new Random();
				Dimension screen = getSize();
				int x = r.nextInt(screen.width - iconSize.width);
				int y = r.nextInt(screen.height - (iconSize.height + LBL_HEIGHT));
				lDateTime.setLocation(x, y);
				lbl.setLocation(x, y + LBL_HEIGHT);
				lDateTime.setText(Utils.readableFormat.format(new Date()));
				repaint();
			}
		};

		timer = new Timer(5000, updateCursorAction);
	}

	public void setTimerEnable(boolean enable) {
		if (enable && (!timer.isRunning())) {
			timer.start();
		} else if ((!enable) && (timer.isRunning())) {
			timer.stop();
		}
	}
}
