package de.edvdb.ffw.web.util;

import org.apache.log4j.Logger;

import de.edvdb.ffw.beans.Adresse;
import de.edvdb.ffw.system.Config;

public class OSMUtils {
	private static Logger log = Logger.getLogger(OSMUtils.class);
	private static String OSM_STATIC_BASEURL = "http://staticmap.openstreetmap.de/staticmap.php?";
//	private static String OSM_STATIC_BASEURL = "http://dev.nichtberechtigt.de/osm/staticmap.php?";
	private static int BASEZOOM = 16;
	
	public static String getURLForAddress(Adresse einsatzort, Adresse ffwhaus) {
		
		String mapURL = OSM_STATIC_BASEURL;
		if(ffwhaus == null) {
			mapURL += "center=" + einsatzort.getLat() + "," + einsatzort.getLng();
			mapURL += "&zoom=17";
		} else {
			mapURL += "center=" + interpolateCoords(ffwhaus.getLat(), einsatzort.getLat()) + "," + interpolateCoords(ffwhaus.getLng(), einsatzort.getLng());
			mapURL += "&zoom=" + calculateZoom(einsatzort, ffwhaus);
		}
		mapURL += "&size=" + Config.MAP_SIZE;
		mapURL += "&maptype=mapnik";
		if(einsatzort.getExact()) {
			mapURL += "&markers=" + einsatzort.getLat() + "," + einsatzort.getLng() + ",ol-marker";
		} else {
			mapURL += "&markers=" + einsatzort.getLat() + "," + einsatzort.getLng() + ",ol-marker-gold";
		}
		if(ffwhaus != null) {
			mapURL += "%7C" + ffwhaus.getLat() + "," + ffwhaus.getLng() + ",ol-marker-green"; // '%7C' entspricht dem URLEncode von '|'
		}
		log.trace("MapURL : '" + mapURL + "'");
		return mapURL;
	}
	
	private static String interpolateCoords(String koord1, String koord2) {
		double double1 = Double.parseDouble(koord1);
		double double2 = Double.parseDouble(koord2);
		return Double.toString((double1 + double2) / 2);
	}
	
	private static int calculateZoom(Adresse einsatzort, Adresse ffwhaus) {
		Double lat1 = Double.valueOf(einsatzort.getLat());
		Double lat2 = Double.valueOf(ffwhaus.getLat());
		Double lng1 = Double.valueOf(einsatzort.getLng());
		Double lng2 = Double.valueOf(ffwhaus.getLng());
		Double diffLat = Math.abs(lat1-lat2) * 100;
		Double diffLng = Math.abs(lng1-lng2) * 100;
		
		Double latFaktor = Math.sqrt(diffLat);
		Double lngFaktor = Math.sqrt(diffLng) / 2;
		
		Double diffFaktor = (latFaktor > lngFaktor) ? latFaktor : lngFaktor;
		Double zoom = BASEZOOM - diffFaktor;
		return zoom.intValue() > 0 ? zoom.intValue() : 0;
	}

}
