package de.edvdb.ffw;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import proguard.annotation.KeepApplication;

import de.edvdb.ffw.client2.AlarmfaxFrame2;
import de.edvdb.ffw.client2.AlarmfaxModel2;
import de.edvdb.ffw.db.Database;
import de.edvdb.ffw.enums.EventType;
import de.edvdb.ffw.system.ClientConfig;
import de.edvdb.ffw.web.util.HTTPAccess;

@KeepApplication
public class Client_v2 {
	private static Logger log = Logger.getLogger(Client_v2.class);

	public static void main(String[] args) {
		DOMConfigurator.configure("..\\conf\\log4j.xml");
		log.info("JFireWare_Client started..");
		ClientConfig cc = new ClientConfig();
		cc.initConfig();
		log.info("Initialized configuration..");
		Database.initDatabase();
		log.info("Initialized database..");
		// Verpacke den auszuf�hrenden Quellcode in ein eigenes
		// Runnable-Objekt, um diesen nachher im Event Dispatching
		// Thread ausf�hren zu k�nnen

		final AlarmfaxFrame2 client = new AlarmfaxFrame2("JFireWare - GUI 0.1");
		final AlarmfaxModel2 model = new AlarmfaxModel2(client);

		Runnable guiCreator = new Runnable() {
			public void run() {
				if(ClientConfig.FULLSCREEN) {
					client.setExtendedState(Frame.MAXIMIZED_BOTH);
				} else {
					client.setPreferredSize(new Dimension(ClientConfig.RESOLUTION_X, ClientConfig.RESOLUTION_Y));
					client.setSize(new Dimension(ClientConfig.RESOLUTION_X, ClientConfig.RESOLUTION_Y));
				}
				if(ClientConfig.UNDECORATED) {
					client.setUndecorated(true);
				}
			    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			    GraphicsDevice[] screens = ge.getScreenDevices();
			    int monitorNr = ClientConfig.MONITORNR;
			    if(screens.length > monitorNr) {
			    	GraphicsDevice screen = screens[monitorNr-1];
			    	screen.setFullScreenWindow(client);
			    }
				client.setVisible(true);
				HTTPAccess.log(Client_v2.class.getName(), EventType.STARTUP);
				Runtime.getRuntime().addShutdownHook(new Thread() { public void run() { HTTPAccess.log(Client_v2.class.getName(), EventType.SHUTDOWN); }});
			}
		};

		SwingUtilities.invokeLater(guiCreator);
		model.initAutoUpdate();
//		model.initConfigUpdate(cc);
	}
}
