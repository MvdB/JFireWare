package de.edvdb.ffw.util;

import org.apache.log4j.Logger;

import de.edvdb.ffw.system.ClientConfig;

public class HookScripts {
	private static Logger log = Logger.getLogger(HookScripts.class);

	public static int on() {
		if(ClientConfig.HOOKONENABLE) {
			try {
				log.trace("Executing on()-Hookscript");
				Runtime rt = Runtime.getRuntime();
				String params = ClientConfig.HOOKONPARAM;
				String command = ClientConfig.HOOKONPATH + " " + params;
				log.trace("Command [" + command + "]");
				Process pr = rt.exec(command);
				return pr.waitFor();
			} catch (Exception e) {
				log.error("on()-Hookscript failed", e);
			}
			return -1; 
		} else {
			return -99;
		}
	}

	public static int off() {
		if(ClientConfig.HOOKOFFENABLE) {
			try {
				log.trace("Executing off()-Hookscript");
				Runtime rt = Runtime.getRuntime();
				String params = ClientConfig.HOOKOFFPARAM;
				String command = ClientConfig.HOOKOFFPATH + " " + params;
				log.trace("Command [" + command + "]");
				Process pr = rt.exec(command);
				return pr.waitFor();
			} catch (Exception e) {
				log.error("off()-Hookscript failed", e);
			}
			return -1; 
		} else {
			return -99;
		}
	}
}
