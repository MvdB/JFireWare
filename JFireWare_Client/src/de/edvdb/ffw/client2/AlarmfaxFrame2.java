package de.edvdb.ffw.client2;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import de.edvdb.ffw.client.IAlarmfaxPanel;
import de.edvdb.ffw.client.PausePanel;

public class AlarmfaxFrame2 extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1581522281868122608L;
	private IAlarmfaxPanel alarmpanel;
	private PausePanel pausePanel;
	private MapPanel2 mapPanel;

	public AlarmfaxFrame2(String title) {
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		LayoutManager overlay = new OverlayLayout(getContentPane());
		setLayout(overlay);
		prepareView();
		hideCursor();
		
		// Force foreground
		setAlwaysOnTop(true);
		toFront();
	}

	private void hideCursor() {
		BufferedImage blankImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(blankImage, new Point(0, 0), "blank cursor");
		setCursor(blankCursor);
	}

	public void prepareView() {
//		alarmpanel = new AlarmfaxPanel();
		alarmpanel = new AlarmfaxPanel2();
		((JPanel) alarmpanel).setOpaque(false);
		setGlassPane((Component) alarmpanel);
		pausePanel = new PausePanel();
		add(pausePanel);
//		mapPanel = new MapPanel2();
//		add(mapPanel);
		
	}

	public IAlarmfaxPanel getAlarmPanel() {
		return alarmpanel;
	}

	public PausePanel getPausePanel() {
		return pausePanel;
	}

	public MapPanel2 getMapPanel() {
		return mapPanel;
	}
}
