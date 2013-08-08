package de.edvdb.ffw.client2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.edvdb.ffw.beans.Alarmfax;
import de.edvdb.ffw.client.IAlarmfaxPanel;
import de.edvdb.ffw.system.ClientConfig;

public class MapPanel2 extends JPanel implements IAlarmfaxPanel {
	private static final long serialVersionUID = -3794079034306241409L;
	
	private Alarmfax fax;
	private JLabel lMap;

	public MapPanel2() {
		super();
		initComponents();
		updateComponents();
	}

	public void initComponents() {
		this.removeAll();
		this.setBackground(Color.WHITE);
		Dimension window = new Dimension(ClientConfig.RESOLUTION_X, ClientConfig.RESOLUTION_Y);
		lMap = new JLabel();
		lMap.setSize(window.width, window.height);
		lMap.setLocation(0, 0);
		lMap.setSize(ClientConfig.RESOLUTION_X, ClientConfig.RESOLUTION_Y);
		lMap.setAlignmentX(Component.CENTER_ALIGNMENT);
		lMap.setAlignmentY(Component.TOP_ALIGNMENT);
		this.add(lMap);
	}

	public void updateComponents() {
		if (getFax() != null) {
			getFax().prepareMaps();
			lMap.setIcon(getImageIconByFilename(getFax().getOverviewMap().getAbsolutePath()));
			this.repaint();
		}
	}

	public void setFax(Alarmfax fax) {
		this.fax = fax;
	}

	public Alarmfax getFax() {
		return fax;
	}

	private ImageIcon getImageIconByFilename(String filename) {
		ImageIcon icon = new ImageIcon(filename);
		Image image = icon.getImage();
		Dimension window = new Dimension(ClientConfig.RESOLUTION_X, ClientConfig.RESOLUTION_Y);
		image = image.getScaledInstance(window.width, window.height, java.awt.Image.SCALE_SMOOTH);
		return new ImageIcon(image);
	}
}
