package de.edvdb.ffw.client;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import de.edvdb.ffw.beans.Alarmfax;
import de.edvdb.ffw.db.Database;
import de.edvdb.ffw.system.ClientConfig;
import de.edvdb.ffw.system.Config;
import de.edvdb.ffw.util.Utils;

public class AlarmfaxModel {
	private static Logger log = Logger.getLogger(AlarmfaxModel.class);
	private Alarmfax alarmfax = null;
	private AlarmfaxFrame frame = null;
	private IAlarmfaxPanel alarmfaxPanel = null;
	private PausePanel pausePanel = null;
	private AudioClip alert = null;
	private long oldTimeSinceAlert = 0;

	@SuppressWarnings("deprecation")
	public AlarmfaxModel(AlarmfaxFrame alarmfaxFrame) {
		this.frame = alarmfaxFrame;
		this.alarmfaxPanel = frame.getAlarmPanel();
		this.pausePanel = frame.getPausePanel();
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
		alarmfaxPanel.setVisible(enable);
		pausePanel.setVisible(!enable);
		pausePanel.setTimerEnable(!enable);
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
				Boolean newFax = refreshData();
				if ((alarmfaxPanel != null) && (newFax != null) && newFax) {
					alarmfaxPanel.setFax(alarmfax);
					alarmfaxPanel.updateComponents();
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
}
