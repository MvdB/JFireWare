package de.edvdb.ffw.client;

import de.edvdb.ffw.beans.Alarmfax;

public interface IAlarmfaxPanel  {
	public void setFax(Alarmfax fax);
	public Alarmfax getFax();
	public void updateComponents();
	public void setVisible(boolean enable);
	public void initComponents();
}
