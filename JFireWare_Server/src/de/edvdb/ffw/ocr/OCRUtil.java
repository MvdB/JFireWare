package de.edvdb.ffw.ocr;

import java.io.File;

import org.apache.log4j.Logger;

import de.edvdb.ffw.system.Config;
import de.edvdb.ffw.system.ServerConfig;
import de.edvdb.ffw.util.Utils;

public class OCRUtil {
	private static Logger log = Logger.getLogger(OCRUtil.class);

	public static File analyze(File image) {
		try {
			log.trace("Analyzing file '" + image.getAbsolutePath() + "'");
			String filePath = Config.TEMPDIR + Utils.getCurrentDatetime() + ".txt";
			File txtFile = new File(filePath);
			String txtPath = txtFile.getCanonicalPath();
			String imgPath = image.getCanonicalPath();
			String params = ServerConfig.OCRPARAM;
			params = params.replace("%TEMP%", txtPath);
			params = params.replace("%IMAGE%", imgPath);
			if(Config.OCRNOEXTENSION) {
				params = params.replaceAll(".txt", "");
			}
			Runtime rt = Runtime.getRuntime();
			String command = ServerConfig.OCRPATH + " " + params;
			log.trace("Command [" + command + "]");
			Process pr = rt.exec(command);
			pr.waitFor();
			return txtFile;
		} catch (Exception e) {
			log.error("Analyzing failed", e);
			return null;
		}
	}

}
