package de.edvdb.ffw.system;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

public class ServerConfig extends Config {
	private static Logger log = Logger.getLogger(ServerConfig.class);

	private static XMLConfiguration config = new XMLConfiguration();
	private static LayoutConfig layoutConfig = new LayoutConfig();
	private static final String CONFIGFILE = "../conf/server.xml";
	private static Long CONFIGTIME;
	// Systemvariablen
	public static String OCRPATH;
	public static String IMGPATH;
	public static String PDFPATH;
	public static String OCRPARAM;
	public static Boolean IMGENABLE;
	public static String IMGPARAM;
	public static String PDFPARAM;
	public static Boolean STITCHENABLE;
	public static String STITCHPATH;
	public static String STITCHPARAM;
	public static String INPUTDIR;
	public static String OUTPUTDIR;
	public static String SPAMDIR;
	public static String ARCHIVEDIR;
	public static String IMGPATTERN;
	public static Boolean MAILENABLE;
	public static Boolean MAILFOLLOWUP;
	public static String MAILSERVER;
	public static String MAILPORT;
	public static Boolean MAILAUTH;
	public static Boolean USESSL;
	public static String MAILUSER;
	public static String MAILPASSWORD;
	public static String MAILFROM;
	public static String ADMINMAIL;
	public static Boolean PRINTENABLE;
	public static Boolean PRINTFOLLOWUP;
	public static Integer PRINTCOUNT = 1;
	public static Boolean PRINTPERVEHICLE;
	public static Boolean SMSENABLE;
	public static Boolean SMSFOLLOWUP;
	public static String SMSURL;
	public static Integer SMSMSGLENGTH;
	public static Integer FOLLOWUPHOURS;
	public static Integer FOLLOWUPMINUTES;
	public static String[][] POSSIBLEMISTAKES;
	public static List<String> WHITESPACES;
	public static String PLZPATTERN_FROM;
	public static String PLZPATTERN_TO;
	
	public static final int FROM = 0;
	public static final int TO = 1;

	
	public boolean checkConfig() {
		boolean reloaded = super.checkConfig();
		File configFile = new File(CONFIGFILE);
		Long lastModified = configFile.lastModified();
		if (CONFIGTIME.compareTo(lastModified) != 0) {
			CONFIGTIME = lastModified;

			initConfig();
			reloaded = true;
		} else {
			reloaded = false;
		}
		return reloaded;
	}
	
	public void initConfig() {
		super.initConfig();
		
		try {

			config = new XMLConfiguration();
			config.setValidating(false);
			config.setFileName(CONFIGFILE);
			config.load();
			if (CONFIGTIME == null) {
				CONFIGTIME = new File(CONFIGFILE).lastModified();
			}

			log.info("Configuration loaded..");

			OCRPATH = config.getString("system.ocr[@path]");
			OCRPARAM = config.getString("system.ocr[@param]");
			IMGENABLE = config.getBoolean("system.img[@enable]");
			IMGPATH = config.getString("system.img[@path]");
			IMGPARAM = config.getString("system.img[@param]");
			STITCHENABLE = config.getBoolean("system.stitch[@enable]");
			STITCHPATH = config.getString("system.stitch[@path]");
			STITCHPARAM = config.getString("system.stitch[@param]");
			PDFPATH = config.getString("system.pdfpath");
			PDFPARAM = config.getString("system.pdfparam");
			INPUTDIR = config.getString("system.inputdir");
			OUTPUTDIR = config.getString("system.outputdir");
			SPAMDIR = config.getString("system.spamdir");
			ARCHIVEDIR = config.getString("system.archivedir");
			IMGPATTERN = config.getString("system.imgfilepattern");
			MAILSERVER = config.getString("personal.mail[@server]");
			MAILPORT = config.getString("personal.mail[@port]");
			MAILAUTH = config.getBoolean("personal.mail[@auth]");
			USESSL = config.getBoolean("personal.mail[@usessl]");
			MAILENABLE = config.getBoolean("personal.mail[@enable]");
			MAILFOLLOWUP = config.getBoolean("personal.mail[@followup]");
			MAILUSER = config.getString("personal.mail[@user]");
			MAILPASSWORD = config.getString("personal.mail[@password]");
			ADMINMAIL = config.getString("system.adminmail");
			MAILFROM = config.getString("personal.mail[@from]");
			PRINTENABLE = config.getBoolean("personal.printer[@enable]");
			SMSENABLE = config.getBoolean("personal.sms[@enable]");
			PRINTFOLLOWUP = config.getBoolean("personal.printer[@followup]");
			PRINTCOUNT = config.getInt("personal.printer[@count]");
			PRINTPERVEHICLE = config.getBoolean("personal.printer[@perVehicle]");
			SMSFOLLOWUP = config.getBoolean("personal.sms[@followup]");
			SMSURL = config.getString("personal.sms[@url]");
			SMSMSGLENGTH = config.getInt("personal.sms[@msgLength]");
			FOLLOWUPHOURS = config.getInt("system.followupoffset[@hours]");
			FOLLOWUPMINUTES = config.getInt("system.followupoffset[@minutes]");
			PLZPATTERN_FROM = config.getString("personal.plzpattern[@from]");
			PLZPATTERN_TO = config.getString("personal.plzpattern[@to]");
			
			checkDirectories();
			prepareMaps();
			
			layoutConfig.checkConfig();
			
			log.info("Configuration parsed..");

		} catch (ConfigurationException e) {
			log.error("Failed to load Configuration!", e);
			System.exit(1);
		}
	}
	
	protected void checkDirectories() {
		super.checkDirectories();
		log.trace("Preparing server-directories..");
		File dir = new File(INPUTDIR);
		dir.mkdirs();
		dir = new File(OUTPUTDIR);
		dir.mkdirs();
		dir = new File(ARCHIVEDIR);
		dir.mkdirs();
		dir = new File(SPAMDIR);
		dir.mkdirs();
	}
	
	@SuppressWarnings("unchecked")
	private void prepareMaps() {
		List<String> from = config.getList("system.ocrmistake[@from]");
		List<String> to = config.getList("system.ocrmistake[@to]");
		POSSIBLEMISTAKES = new String[from.size()][2];
		for (int i = 0; i < from.size(); i++) {
			POSSIBLEMISTAKES[i][FROM] = from.get(i);
			POSSIBLEMISTAKES[i][TO] = to.get(i);
			if (log.isTraceEnabled()) {
				log.trace("New Mistakepattern loaded [" + from.get(i) + " -> "
						+ to.get(i) + "]");
			}
		}
		WHITESPACES = config.getList("system.ocrwhitespace[@toclear]");
	}
}
