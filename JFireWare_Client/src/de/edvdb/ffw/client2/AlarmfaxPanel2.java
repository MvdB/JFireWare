package de.edvdb.ffw.client2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import de.edvdb.ffw.beans.Alarmfax;
import de.edvdb.ffw.client.IAlarmfaxPanel;
import de.edvdb.ffw.system.ClientConfig;
import de.edvdb.ffw.system.Config;
import de.edvdb.ffw.util.Messages;
import de.edvdb.ffw.util.Utils;

public class AlarmfaxPanel2 extends JPanel implements IAlarmfaxPanel {
	private static final long serialVersionUID = -3794079034306241409L;
	private static Color FONTCOLOR = Color.BLACK;
	private static final Font BIG_FONT = new Font("Arial", Font.BOLD, 34);
	private static final Font MEDIUM_FONT = new Font("Arial", Font.BOLD, 28);
	private static final int ROWCOUNT = 13;
	private static final int COLCOUNT = 10;
	private static final Font TITLEFONT = new Font("Arial", Font.BOLD, 16);;

	
	private Alarmfax fax;
	private JLabel lSchlagwort, lTimestamp, lAdresse, lBemerkung, lFahrzeuge, lZustaendigkeit, lGeraete, lOverview;
	
	public AlarmfaxPanel2() {
		super();
		initComponents();
		updateComponents();
	}

	public void initComponents() {
		this.removeAll();
		setLayout(null);
		setOpaque(false);
		
		lAdresse = initComponent(Messages.getString("Adresse"), 0, 0, 10, 1, BIG_FONT, FONTCOLOR);
		lSchlagwort = initComponent(Messages.getString("Schlagwort"), 0, 1, 10, 1, BIG_FONT, FONTCOLOR);
		lTimestamp = initComponent(Messages.getString("Alarmierungszeitpunkt"), 7, 2, 3, 1, BIG_FONT, FONTCOLOR);
		lZustaendigkeit = initComponent(Messages.getString("Zustaendigkeit"), 0, 2, 3, 2, BIG_FONT, FONTCOLOR);
		lFahrzeuge = initComponent(Messages.getString("Fahrzeuge"), 0, 4, 1, 7, BIG_FONT, FONTCOLOR);
		lBemerkung = initComponent(Messages.getString("Bemerkung"), 0, 11, 7, 2, MEDIUM_FONT, FONTCOLOR);
		lGeraete = initComponent(Messages.getString("Einsatzmittel"), 7, 11, 3, 2, MEDIUM_FONT, FONTCOLOR);
		lOverview = initComponent(null, 1, 2, 9, 9, BIG_FONT, FONTCOLOR);
	}

	public void updateComponents() {
		if (getFax() != null) {
			Dimension mapSize = lOverview.getMaximumSize();
			ClientConfig.MAP_SIZE = mapSize.width + "x" + mapSize.height;
			
			getFax().prepareMaps();
			
			String timeSinceAlert = Utils.getTimeDiff(getFax().getTimestamp());
			lSchlagwort.setText(Utils.getSetAsMultilineHTMLString(getFax()
					.getSchlagwort()));
			lTimestamp.setText("<html><b>"
					+ Utils.getTimeFromDate(getFax().getTimestamp())
					+ "</b> ( " + timeSinceAlert + " )</html>");
			lAdresse.setText(getFax().getAdresse().toString());
			lBemerkung.setText(Utils.getListAsMultilineHTMLString(getFax()
					.getBemerkung()));
			List<String> zustaendigkeiten = new ArrayList<String>(fax.getZustaendigkeit());
			String textZustaendigkeit = "<html>";
			for(String zustaendigkeit : zustaendigkeiten) {
				if(zustaendigkeit.contains(Config.FFWNAME) || Config.FFWNAME.contains(textZustaendigkeit)) {
					textZustaendigkeit += "<font color=\"red\"><b>" + zustaendigkeit + "</b></font><br/>";
				} else {
					textZustaendigkeit += "<font color=\"black\">" + zustaendigkeit + "</font><br/>";
				}
				
			}
			textZustaendigkeit += "</html>";
			lZustaendigkeit.setText(textZustaendigkeit);
			lFahrzeuge.setText(Utils.getListAsMultilineHTMLString(getFax()
					.getFahrzeugeShort()));
			lOverview.setIcon(getImageIconByFilename(getFax().getOverviewMap()
					.getAbsolutePath(), lOverview.getMaximumSize()));
			lGeraete.setText(Utils.getSetAsMultilineHTMLString(getFax()
					.getEinsatzmittel()));
		}
		this.repaint();
	}

	public void setFax(Alarmfax fax) {
		this.fax = fax;
	}

	public Alarmfax getFax() {
		return fax;
	}
	
	private JLabel initComponent(String initialText, int x, int y, int width, int height, Font f, Color c) {
		Dimension window = new Dimension(ClientConfig.RESOLUTION_X, ClientConfig.RESOLUTION_Y);
		int cWidth = window.width / COLCOUNT;
		int rHeight = window.height / ROWCOUNT;
		
		Dimension maxSize = new Dimension((width * cWidth), (height * rHeight));
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		panel.setSize(maxSize);
		panel.setLocation((x * cWidth), (y * rHeight));
//		panel.setBackground(Color.PINK);
		if(initialText != null) {
			panel.setBorder(getTitleborder(initialText));
		}
//		panel.setOpaque(false);
		this.add(panel);
		
		JLabel label = new JLabel("", SwingConstants.LEFT);
//		label.setLocation((x * cWidth), (y * rHeight));
		label.setSize(maxSize);
		label.setMinimumSize(maxSize);
		label.setMaximumSize(maxSize);
		label.setPreferredSize(maxSize);
		label.setFont(f);
		label.setForeground(c);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		label.setAlignmentY(Component.TOP_ALIGNMENT);
		label.setVerticalAlignment(SwingConstants.TOP);
		panel.add(label);
		
		return label;
	}
	
	private ImageIcon getImageIconByFilename(String filename, Dimension size) {
		ImageIcon icon = new ImageIcon(filename);
		Image image = icon.getImage();
		if(size == null) {
			size = new Dimension(ClientConfig.RESOLUTION_X, ClientConfig.RESOLUTION_Y);
		}
		image = image.getScaledInstance(size.width, size.height, java.awt.Image.SCALE_SMOOTH);
		return new ImageIcon(image);
	}
	
	public Border getTitleborder(String title) {
		Border baseBorder = BorderFactory.createLineBorder(Color.DARK_GRAY, 2);
		TitledBorder titleBorder = BorderFactory
				.createTitledBorder(baseBorder, title, TitledBorder.CENTER,
						TitledBorder.DEFAULT_POSITION, TITLEFONT);
		return titleBorder;
	}
}
