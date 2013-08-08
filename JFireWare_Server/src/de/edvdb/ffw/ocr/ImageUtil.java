package de.edvdb.ffw.ocr;

import java.io.File;

import org.apache.log4j.Logger;

import de.edvdb.ffw.system.Config;
import de.edvdb.ffw.system.ServerConfig;
import de.edvdb.ffw.util.Utils;

public class ImageUtil {
	private static Logger log = Logger.getLogger(ImageUtil.class);

	public static File stitch(File image) {
		if(!ServerConfig.STITCHENABLE) {
			return image;
		}
		File stitchedImage = new File(Config.TEMPDIR + Utils.getCurrentDatetime()
				+ ".tif");
		try {
			log.trace("Converting file '" + image.getAbsolutePath() + "'");
			Runtime rt = Runtime.getRuntime();
			// Zusammenflicken von einzelnen TIFF-Seiten
			if ((image.getName().endsWith(".tif")) || (image.getName().endsWith(".TIF")) ||
				(image.getName().endsWith(".tiff")) || (image.getName().endsWith(".TIFF"))) {
				String params = ServerConfig.STITCHPARAM;
				params = params.replace("%INPUT%", image.getAbsolutePath());
				params = params.replace("%OUTPUT%", stitchedImage.getAbsolutePath());
				String command = ServerConfig.STITCHPATH + " " + params;
				log.trace("Command [" + command + "]");
				Process pr = rt.exec(command);
				pr.waitFor();
			}
		} catch (Exception e) {
			log.error("Converting failed", e);
			cleanUp(image, ServerConfig.ARCHIVEDIR);
		} 
		return stitchedImage;
	}
	
	public static File convert(File image) {
		if(!ServerConfig.IMGENABLE) {
			return image;
		}
		File bmpImage = new File(Config.TEMPDIR + Utils.getCurrentDatetime()
				+ ".bmp");
		try {
			log.trace("Converting file '" + image.getAbsolutePath() + "'");
			Runtime rt = Runtime.getRuntime();
			// Konvertieren falls nicht .bmp
			if (!image.getName().endsWith(".bmp")) {
				String params = ServerConfig.IMGPARAM;
				params = params.replace("%INPUT%", image.getAbsolutePath());
				params = params.replace("%OUTPUT%", bmpImage.getAbsolutePath());
				String command = ServerConfig.IMGPATH + " " + params;
				log.trace("Command [" + command + "]");
				Process pr = rt.exec(command);
				pr.waitFor();
			}
		} catch (Exception e) {
			log.error("Converting failed", e);
			cleanUp(image, ServerConfig.ARCHIVEDIR);
		} 
		return bmpImage;
	}
	
	public static void cleanUp(File image, String targetDirectory) {
		File targetdir = new File(targetDirectory);
		File targetFile = new File(targetdir, image.getName());
		if (((!targetFile.exists()) && image.renameTo(targetFile))
				|| (targetFile.exists() && targetFile.delete() && image
						.renameTo(targetFile))) {
			log.debug("Moved Imagedfile '" + image.getName() + "'");
		} else {
			log.info("Deleted Imagedfile '" + image.getName()
					+ "' due to moving-error.");
		}
	}

	public static File[] getImagefiles() {
		File dir = new File(ServerConfig.INPUTDIR);
		return dir.listFiles(new PatternFilter());
	}
}
