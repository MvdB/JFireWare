package de.edvdb.ffw.web.util;

import org.apache.log4j.Logger;

import de.edvdb.ffw.beans.Adresse;
import de.edvdb.ffw.system.Config;

public class GMapUtils {
	private static Logger log = Logger.getLogger(GMapUtils.class);
	public static final String GOOGLEMAP_BASEURL = "http://maps.google.com/maps/api/staticmap?mobile=true&sensor=false&";
	public static final String GOOGLEAPIS_BASEURL = "http://maps.googleapis.com/maps/api/geocode/xml?sensor=false&address=";


	public static String getURLForAddress(Adresse einsatzort, String zoom,
			Adresse ffwhaus, String type) {
		String mapURL = GOOGLEMAP_BASEURL;
		mapURL += "&size=" + Config.MAP_SIZE;
		if (type != null) {
			mapURL += "&maptype=" + type;
		}
		if (zoom != null) {
			mapURL += "&zoom=" + zoom;
		}
		if (einsatzort != null) {
			mapURL += "&markers=size:mid%7Ccolor:"
					+ einsatzort.getColor().toString() + "%7C"
					+ einsatzort.getURLMaskedString() + "&";
		}
		if (ffwhaus != null) {
			mapURL += "&markers=size:mid%7Ccolor:"
					+ ffwhaus.getColor().toString() + "%7C"
					+ ffwhaus.getURLMaskedString() + "&";
		}
		log.trace("MapURL : '" + mapURL + "'");
		return mapURL;
	}

	public static String getURLForAddress(Adresse einsatzort, String zoom,
			String type) {
		return getURLForAddress(einsatzort, zoom, null, type);
	}

	public static String getURLForAddress(Adresse einsatzort, Adresse ffwhaus,
			String type) {
		return getURLForAddress(einsatzort, null, ffwhaus, type);
	}

	public static String getURLForAddress(Adresse einsatzort, String type) {
		return getURLForAddress(einsatzort, null, null, type);
	}
	
	public static String getURLForLocation(Adresse einsatzort) {
		String locationURL = GOOGLEAPIS_BASEURL;
		if (einsatzort != null) {
			locationURL += einsatzort.getURLMaskedString();
		}
		log.trace("locationURL : '" + locationURL + "'");
		return locationURL;
	}
}
