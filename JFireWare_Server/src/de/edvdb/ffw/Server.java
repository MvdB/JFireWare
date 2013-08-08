package de.edvdb.ffw;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import proguard.annotation.KeepApplication;

import de.edvdb.ffw.beans.Alarmfax;
import de.edvdb.ffw.db.Database;
import de.edvdb.ffw.enums.EventType;
import de.edvdb.ffw.mail.Mailer;
import de.edvdb.ffw.ocr.ImageUtil;
import de.edvdb.ffw.ocr.OCRUtil;
import de.edvdb.ffw.ocr.ParseFile;
import de.edvdb.ffw.pdf.PDFCreator;
import de.edvdb.ffw.system.Config;
import de.edvdb.ffw.system.ServerConfig;
import de.edvdb.ffw.util.ServerUtils;
import de.edvdb.ffw.web.SMS;
import de.edvdb.ffw.web.util.HTTPAccess;

@KeepApplication
public class Server implements Runnable {
	private static Logger log = Logger.getLogger(Server.class);
	private static boolean interrupted = false;
	private static boolean running = false;

	private File image;
	/**
	 * @param args
	 */

	public static void main(String[] args) {

		try {
			// loading own config
			DOMConfigurator.configure("../conf/log4j.xml");
			if (log.isTraceEnabled()) {
				printSysEnv();
			}
			log.info("AlarmfaxParser started..");
			ServerConfig sc = new ServerConfig();
			sc.initConfig();
			// prepare database
			Database.initDatabase();
			log.info("Initialized database..");
			log.info("Waiting for files..");
			Runtime.getRuntime().addShutdownHook(new Thread() { public void run() { HTTPAccess.log(Server.class.getName(), EventType.SHUTDOWN); }});
			HTTPAccess.log(Server.class.getName(), EventType.STARTUP);
			while (!interrupted) {
				running = true;
				File[] images = ImageUtil.getImagefiles();
				for (File image : images) {
					try {
						new Server(image).run();
					} catch (Exception e) {
						log.error("Failed to process file '" + image.getName() + "'");
					}
				}
				Thread.sleep(Config.SLEEPTIME);

				if (sc.checkConfig()) {
					log.info("Using new configuration");
				}
			}
		} catch (Exception e) {
			log.fatal("Application has crashed!", e);
			Mailer.sendMail(e.getLocalizedMessage(),
					"AlarmfaxParser has crashed!", null, true);
			HTTPAccess.log(Server.class.getName(), EventType.ERROR);
		} finally {
			running = false;
			log.info("AlarmfaxParser stopped..");
			HTTPAccess.log(Server.class.getName(), EventType.SHUTDOWN);
		}
	}
	
	private static void printSysEnv() {
		Map<String, String> env = System.getenv();
		for (String key : env.keySet()) {
			log.trace(key + " : " + env.get(key));
		}

		Properties prop = System.getProperties();
		for (Object key : prop.keySet()) {
			log.trace(key + " : " + prop.get(key));
		}
	}

	public Server(File image) {
		this.image = image;
	}
	
	@Override
	public void run() {
		File tifImage = ImageUtil.stitch(image);
		File bmpImage = ImageUtil.convert(tifImage);
		File txtFile = OCRUtil.analyze(bmpImage);
		Alarmfax fax = ParseFile.analyzeFile(txtFile);
		if ((fax != null) && (!fax.isEmpty())) {
			ImageUtil.cleanUp(image, ServerConfig.ARCHIVEDIR);
			boolean isFollowup = ServerUtils.isFollowup(fax);
			log.info("Fax parsed: " + fax.toString());
			if (ServerConfig.SMSENABLE
					&& (!isFollowup || ServerConfig.SMSFOLLOWUP)) {
				SMS sms = new SMS(fax);
				sms.run();
			}
			fax.prepareMaps();
			PDFCreator pdf = new PDFCreator(fax, bmpImage);
			File pdfFile = pdf.createPDF();
			if (ServerConfig.MAILENABLE
					&& (!isFollowup || ServerConfig.MAILFOLLOWUP)) {
				Mailer.sendMail(pdfFile, fax);
			}
			if (ServerConfig.PRINTENABLE
					&& (!isFollowup || ServerConfig.PRINTFOLLOWUP)) {
				boolean printingDone = false;
				if(ServerConfig.PRINTCOUNT > 0) {
					log.info("Printing " + ServerConfig.PRINTCOUNT + " times (hard-coded)");
					for(int i= 0; i < ServerConfig.PRINTCOUNT; i++) {
						ServerUtils.print(pdfFile);
						printingDone = true;
					}
				}
				if(ServerConfig.PRINTPERVEHICLE) {
					for(String fahrzeug : fax.getFahrzeuge()) {
						log.info("Printing copy for '" + fahrzeug + "'");
						ServerUtils.print(pdfFile);
						printingDone = true;
					}
				}
				if(!printingDone) {
					log.warn("Printing enabled, but not properly configured. Printing 1 copy as default.");
					ServerUtils.print(pdfFile);
					printingDone = true;
				}
			}
			Database.persist(fax);
			log.info("Fax written to database [ID: " + fax.getId() + "]");
			fax.cleanUp();
			HTTPAccess.log(fax, EventType.WORKLOAD);
		} else {
			ImageUtil.cleanUp(image, ServerConfig.SPAMDIR);
			log.info("File '" + image.getName() + "' seems to be invalid");
		}
		if (!(log.isDebugEnabled() || log.isTraceEnabled())) {
			if (tifImage.delete()) {
				log.debug("Deleted TIF '" + tifImage.getName()
						+ "'");
			}
			if (bmpImage.delete()) {
				log.debug("Deleted BMP '" + bmpImage.getName()
						+ "'");
			}
			if (txtFile.delete()) {
				log.debug("Deleted TXT '" + txtFile.getName() + "'");
			}
		}
	}

	public static void setInterrupted(boolean interrupted) {
		Server.interrupted = interrupted;
	}

	public static boolean isInterrupted() {
		return interrupted;
	}

	public static boolean isRunning() {
		return running;
	}
}
