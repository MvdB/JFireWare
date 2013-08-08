package de.edvdb.ffw.client2;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import de.edvdb.ffw.beans.Alarmfax;
import de.edvdb.ffw.client.AlarmfaxPanel;
import de.edvdb.ffw.client.IAlarmfaxPanel;
import de.edvdb.ffw.client.PausePanel;
import de.edvdb.ffw.db.Database;
import de.edvdb.ffw.system.ClientConfig;
import de.edvdb.ffw.system.Config;
import de.edvdb.ffw.util.HookScripts;
import de.edvdb.ffw.util.Utils;

public class AlarmfaxModel2 {
	private static Logger log = Logger.getLogger(AlarmfaxModel2.class);
	private Alarmfax alarmfax = null;
	private AlarmfaxFrame2 frame = null;
	private IAlarmfaxPanel alarmfaxPanel = null;
	private MapPanel2 mapPanel = null;
	private PausePanel pausePanel = null;
	private AudioClip alert = null;
	private Boolean currentlyEnabled = null;
	private long oldTimeSinceAlert = 0;

	@SuppressWarnings("deprecation")
	public AlarmfaxModel2(AlarmfaxFrame2 alarmfaxFrame) {
		this.frame = alarmfaxFrame;
		this.alarmfaxPanel = frame.getAlarmPanel();
		this.pausePanel = frame.getPausePanel();
		this.mapPanel = frame.getMapPanel();
		try {
			alert = Applet.newAudioClip(new File(ClientConfig.SOUNDFILE).toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public Boolean refreshData() {
		int hours = -1 * ClientConfig.TIMEOUTHOURS;
		int minutes = -1 * ClientConfig.TIMEOUTMINUTES;
		Date minDate = Utils.getDateByOffset(hours, minutes);
		Date maxDate = Utils.getDateByOffset(0, 5);
		Alarmfax old = alarmfax;
		List<Alarmfax> faxe = Database.getSession()
				.createCriteria(Alarmfax.class)
				.add(Restrictions.between("timestamp", minDate, maxDate))
				.addOrder(Order.desc("id")).list();
		alarmfax = ((faxe.size() > 0) ? faxe.get(0) : null);
		if ((alarmfax != null) && (!alarmfax.equals(old))) {
			log.info("New Fax loaded. # " + alarmfax.toString());
			setInfoscreenEnable(true);
			frame.requestFocus();
			if(alert != null && ClientConfig.SOUNDENABLE) {
				alert.play();
			}
			if (old != null) {
				old.cleanUp();
			}
			oldTimeSinceAlert = -1;
			return true;
		} else if ((alarmfax != null) && (alarmfax.equals(old))) {
			long newTimeSinceAlert = Utils.getTimeDiffAsLong(alarmfax.getTimestamp());
			if(newTimeSinceAlert > oldTimeSinceAlert) {
				oldTimeSinceAlert = newTimeSinceAlert;
				return true;
			}
			return false;
		} else {
			setInfoscreenEnable(false);
			return null;
		}
	}

	private void setInfoscreenEnable(boolean enable) {
		if((null != currentlyEnabled) && (enable == currentlyEnabled)) {
			return;
		} else {
			alarmfaxPanel.setVisible(enable);
	//		mapPanel.setVisible(enable);
			pausePanel.setVisible(!enable);
			pausePanel.setTimerEnable(!enable);
			if(enable) {
				HookScripts.on();
			} else {
				HookScripts.off();
			}
			currentlyEnabled = enable;
		}
	}

	public void setPanel(AlarmfaxPanel panel) {
		this.alarmfaxPanel = panel;
	}

	public IAlarmfaxPanel getPanel() {
		return alarmfaxPanel;
	}

	public void initAutoUpdate() {
		int delay = Config.SLEEPTIME;
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				boolean sizeChange = hasWindowsizeChanged();
				if(sizeChange) {
					log.debug("Windowsize has been changed. Please re-check layout.");
				}
				Boolean newFax = refreshData();
				if ((alarmfaxPanel != null) && /*(mapPanel != null) &&*/ (newFax != null) && (newFax || sizeChange)) {
					Dimension frameSize = frame.getSize();
					ClientConfig.MAP_SIZE = frameSize.width + "x" + frameSize.height;
					alarmfaxPanel.setFax(alarmfax);
					alarmfaxPanel.updateComponents();
//					mapPanel.setFax(alarmfax);
//					mapPanel.updateComponents();
				}
			}
		};
		Timer starter = new Timer(delay, taskPerformer);
		starter.setInitialDelay(0);
		starter.start();
	}
	
	public void initConfigUpdate(ClientConfig cc) {
		int delay = 60000; // 1 Min
		final ClientConfig config = cc; 
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				config.checkConfig();
				Config.MAP_SIZE = ClientConfig.RESOLUTION_X + "x" + ClientConfig.RESOLUTION_Y;
			}
		};
		Timer starter = new Timer(delay, taskPerformer);
		starter.setInitialDelay(0);
		starter.start();
	}	
	

	public void setPausePanel(PausePanel pausePanel) {
		this.pausePanel = pausePanel;
	}

	public PausePanel getPausePanel() {
		return pausePanel;
	}

	public JPanel getMapPanel() {
		return mapPanel;
	}

	public void setMapPanel(MapPanel2 mapPanel) {
		this.mapPanel = mapPanel;
	}
	
	public boolean hasWindowsizeChanged() {
		Dimension windowSize = frame.getSize();
		if((ClientConfig.RESOLUTION_X != windowSize.width) || (ClientConfig.RESOLUTION_Y != windowSize.height)) {
			ClientConfig.RESOLUTION_X = windowSize.width;
			ClientConfig.RESOLUTION_Y = windowSize.height;
			alarmfaxPanel.initComponents();
			return true;
		} else {
			return false;
		}
	}
}
