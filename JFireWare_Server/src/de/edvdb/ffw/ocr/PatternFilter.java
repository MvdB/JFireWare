package de.edvdb.ffw.ocr;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.edvdb.ffw.system.ServerConfig;

public class PatternFilter implements FileFilter {
	private static Logger log = Logger.getLogger(PatternFilter.class);

	@Override
	public boolean accept(File file) {
		Pattern p = Pattern.compile(ServerConfig.IMGPATTERN);
		Matcher m = p.matcher(file.getName());
		log.trace("Parsing '" + file.getName() + "' with Pattern '"
				+ ServerConfig.IMGPATTERN + "'");
		return m.matches();
	}

}
