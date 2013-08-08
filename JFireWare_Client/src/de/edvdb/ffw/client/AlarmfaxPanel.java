package de.edvdb.ffw.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import de.edvdb.ffw.beans.Alarmfax;
import de.edvdb.ffw.system.ClientConfig;
import de.edvdb.ffw.util.Utils;

public class AlarmfaxPanel extends JPanel implements IAlarmfaxPanel {
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(AlarmfaxPanel.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 3148469043555949818L;
	private static final int ROWCOUNT = 11;
	private static final int COLCOUNT = 6;
	
	private Alarmfax fax;
	private JLabel lSchlagwort, lTimestamp, lAdresse, lOverview, lDetail,
			lBemerkung, lFahrzeuge, lGeraete;

	private Font INFO = new Font("Arial", Font.PLAIN, 16);
	private Font HIGHLIGHT = null;
	private GridBagConstraints constraints;
	

	public AlarmfaxPanel() {
		super();
		HIGHLIGHT = new Font("Arial", Font.BOLD, ClientConfig.FONTSIZE);
		initComponents();
		updateComponents();
	}

	public Border getTitleborder(String title) {
		Border baseBorder = BorderFactory.createLineBorder(Color.DARK_GRAY, 2);
		TitledBorder titleBorder = BorderFactory
				.createTitledBorder(baseBorder, title, TitledBorder.CENTER,
						TitledBorder.DEFAULT_POSITION, INFO);
		return titleBorder;
	}

	public void initComponents() {
		this.removeAll();
		this.setBackground(Color.WHITE);
//		GridBagLayout gLayout = new GridBagLayout();
//		this.setLayout(gLayout);
		this.setLayout(null);
		constraints = new GridBagConstraints();
		constraints.ipadx = 0;
		constraints.ipady = 0;

		
		
		lTimestamp = initComponent("Alarmierungszeitpunkt", 6, 1, 0, 0, HIGHLIGHT);
		lSchlagwort = initComponent("Schlagwort", 5, 1, 0, 1, HIGHLIGHT, Color.RED);
		lAdresse = initComponent("Einsatzort", 5, 1, 0, 2, HIGHLIGHT, Color.RED);
		lFahrzeuge = initComponent("Fahrzeuge", 1, 2, 5, 1, HIGHLIGHT, Color.BLUE);
		lOverview = initComponent("Übersicht", 3, 6, 0, 3);
		lDetail = initComponent("Detailansicht", 3, 6, 3, 3);
		lBemerkung = initComponent("Bemerkung", 3, 2, 0, 9, HIGHLIGHT, Color.RED);
		lGeraete = initComponent("Geforderte Geräte", 3, 2, 3, 9, HIGHLIGHT, Color.BLUE);
		
	}

	public void updateComponents() {
		if (getFax() != null) {
			getFax().prepareMaps();
			String timeSinceAlert = Utils.getTimeDiff(getFax().getTimestamp());
			lSchlagwort.setText(Utils.getSetAsMultilineHTMLString(getFax()
					.getSchlagwort()));
			lTimestamp.setText("<html><b>"
					+ Utils.getDateFromDate(getFax().getTimestamp())
					+ " / "
					+ Utils.getTimeFromDate(getFax().getTimestamp())
					+ "</b> ( " + timeSinceAlert + " )</html>");
			lAdresse.setText(getFax().getAdresse().toString());
			lOverview.setIcon(getImageIconByFilename(getFax().getOverviewMap()
					.getAbsolutePath()));
			lDetail.setIcon(getImageIconByFilename(getFax().getDetailMap()
					.getAbsolutePath()));
			lBemerkung.setText(Utils.getListAsMultilineHTMLString(getFax()
					.getBemerkung()));
			lFahrzeuge.setText(Utils.getListAsMultilineHTMLString(getFax()
					.getFahrzeugeShort()));
			lGeraete.setText(Utils.getSetAsMultilineHTMLString(getFax()
					.getEinsatzmittel()));
		} else {
			lSchlagwort.setText("Schlagwort");
			lTimestamp.setText("Eingegangen am dd.mm.yyyy um hh.mm.ss Uhr.");
			lAdresse.setText("Strasse 123, 12345 Ort");
			lBemerkung.setText("Bemerkung, falls vorhanden.");
			lFahrzeuge.setText("Alarmierte Fahrzeuge");
		}
		this.repaint();
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
		int width = (int) (Toolkit.getDefaultToolkit().getScreenSize()
				.getHeight() * ClientConfig.MAPSCALE);
		// Bild ist quadratisch -> nur 1 Wert nötig
		image = image.getScaledInstance(width, width,
				java.awt.Image.SCALE_SMOOTH);
		return new ImageIcon(image);
	}

	private JLabel initComponent(String title, int width, int height, int x, int y) {
		return initComponent(title, width, height, x, y, HIGHLIGHT);

	}

	private JLabel initComponent(String title, int width, int height, int x, int y, Font f) {
		return initComponent(title, width, height, x, y, f, Color.BLACK);

	}

	private JLabel initComponent(String title, int width, int height, int x, int y, Font f,
			Color c) {
		Dimension window = new Dimension(ClientConfig.RESOLUTION_X, ClientConfig.RESOLUTION_Y);
		int cWidth = window.width / COLCOUNT;
		int rHeight = window.height / ROWCOUNT;
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		panel.setSize((width * cWidth), (height * rHeight));
		panel.setLocation((x * cWidth), (y * rHeight));
		if (title != null) {
			panel.setBorder(getTitleborder(title));
		}
		this.add(panel);
				
		JLabel label = new JLabel();
		label.setSize(panel.getSize());
		label.setFont(f);
		label.setForeground(c);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setAlignmentY(Component.TOP_ALIGNMENT);
		panel.add(label);
		
		return label;
	}
}
