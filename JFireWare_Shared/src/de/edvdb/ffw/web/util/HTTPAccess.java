package de.edvdb.ffw.web.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import de.edvdb.ffw.beans.Adresse;
import de.edvdb.ffw.enums.EventType;
import de.edvdb.ffw.system.Config;
import de.edvdb.ffw.util.Utils;

public class HTTPAccess {
	private static Logger log = Logger.getLogger(HTTPAccess.class);
	private static String LOCTYPE_EXACT = "ROOFTOP";
	private static String ZERO_RESULTS = "ZERO_RESULTS";

	public static void getImageForAddress(File outputfile, Adresse addrFax) {
		getImageForAddress(outputfile, addrFax, null);
	}
	
	public static void getImageForAddress(File outputfile, Adresse addrFax,
			Adresse addrFFW) {
		String url = "";
		if("GOOGLE".equalsIgnoreCase(Config.MAP_PROVIDER)) {
			if (addrFFW != null) {
				url = GMapUtils.getURLForAddress(addrFax, addrFFW, Config.MAPTYPE_OVERVIEW);
			} else {
				url = GMapUtils.getURLForAddress(addrFax, Config.MAPTYPE_DETAIL);
			}
		}else if("OSM".equalsIgnoreCase(Config.MAP_PROVIDER)) {
			if(!addrFax.hasGeoData()) {
				log.debug("Querying coordinates for address..");
				fillLocation(addrFax);
			}
			if (addrFFW != null) {
				url = OSMUtils.getURLForAddress(addrFax, addrFFW);
			} else {
				url = OSMUtils.getURLForAddress(addrFax, null);
			}
		}
		getImageFromURL(outputfile, url);
	}

	private static void fillLocation(Adresse addrFax) {
		try {
			String url = GMapUtils.getURLForLocation(addrFax);
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
			BufferedOutputStream bout = new BufferedOutputStream(baos,
					Config.BUFFERSIZE);
			Thread.sleep(1000);
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			httpget.addHeader("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				entity.getContentType();
				int l;
				byte[] tmp = new byte[Config.BUFFERSIZE];
				while ((l = instream.read(tmp)) != -1) {
					bout.write(tmp, 0, l);
					bout.flush();
				}
			}
			bout.close();
			baos.flush();
			baos.close();
			
			XPathFactory factory = XPathFactory.newInstance();
		    XPath xpath = factory.newXPath();
		    
		    InputSource statusSrc = new InputSource(new ByteArrayInputStream(baos.toByteArray()));
			Node status = (Node) xpath.evaluate("//status/text()", statusSrc, XPathConstants.NODE);
			log.debug(status.getTextContent());
			if(!ZERO_RESULTS.equalsIgnoreCase(status.getTextContent())) {
				InputSource lngSrc = new InputSource(new ByteArrayInputStream(baos.toByteArray()));
				Node lng = (Node) xpath.evaluate("//location/lng/text()", lngSrc, XPathConstants.NODE);
				InputSource latSrc = new InputSource(new ByteArrayInputStream(baos.toByteArray()));
				Node lat = (Node) xpath.evaluate("//location/lat/text()", latSrc, XPathConstants.NODE);
				InputSource locSrc = new InputSource(new ByteArrayInputStream(baos.toByteArray()));
				Node loc = (Node) xpath.evaluate("//location_type/text()", locSrc, XPathConstants.NODE);
				
				String locType = loc.getTextContent();
				Boolean exact = LOCTYPE_EXACT.equalsIgnoreCase(locType) ? true : false;
				
				addrFax.setLng(lng.getTextContent());
				addrFax.setLat(lat.getTextContent());
				addrFax.setExact(exact);
				log.trace("Latitude  : " + addrFax.getLat());
				log.trace("Longitude : " + addrFax.getLng());
				log.trace("ExactMatch: " + addrFax.getExact());
				log.debug("Analyzing Location succeeded");
			} else {
				log.warn("Analyzing Location failed - ZERO_RESULTS for given location");
				addrFax.setStrasse("keine gültige Adresse gefunden");
				addrFax.setHausnummer("");
				addrFax.setPlz("");
				addrFax.setOrt("");
				addrFax.setLng(Config.FFWADRESSE.getLng());
				addrFax.setLat(Config.FFWADRESSE.getLat());
				addrFax.setExact(false);
			}
		} catch (Exception e) {
			log.error("Analyzing Location failed", e);
		}
	}
	
	public static void getImageFromURL(File outputfile, String url) {
		try {
			FileOutputStream fos = new FileOutputStream(outputfile);
			BufferedOutputStream bout = new BufferedOutputStream(fos,
					Config.BUFFERSIZE);
			Thread.sleep(1000);
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			httpget.addHeader("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				entity.getContentType();
				int l;
				byte[] tmp = new byte[Config.BUFFERSIZE];
				while ((l = instream.read(tmp)) != -1) {
					bout.write(tmp, 0, l);
					bout.flush();
				}
			}

			bout.close();
			log.trace("Imagedownload succeeded");
		} catch (Exception e) {
			log.error("Imagedownload failed", e);
		}
	}
	
	@SuppressWarnings({ "finally", "unused" })
	public static String accessURL(String url) {
		String returncode = null;
		try {
			log.trace("Accessing URL '" + url + "'");
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			httpget.addHeader("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				entity.getContentType();
				int l;
				byte[] tmp = new byte[Config.BUFFERSIZE];
				if ((l = instream.read(tmp)) != -1) {
					returncode = Utils.extractNumerics(new String(tmp));
				}
			}
			log.trace("Finished accessing URL");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return returncode;
		}
	}
	
	@SuppressWarnings({ "finally", "unused" })
	public static String accessURL(String url, HashMap<String, String> getParameter) {
		String returncode = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			Set<String> keySet = getParameter.keySet();
			for(String key : keySet) {
				nameValuePairs.add(new BasicNameValuePair(key,getParameter.get(key)));
			}
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				entity.getContentType();
				int l;
				byte[] tmp = new byte[Config.BUFFERSIZE];
				if ((l = instream.read(tmp)) != -1) {
					returncode = Utils.extractNumerics(new String(tmp));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return returncode;
		}
	}
	
	public static void log(Object comp, EventType event) {
		log(comp.getClass().getName(), event);
	}
	
	public static void log(String comp, EventType event) {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("FF", Config.FFWNAME);
		parameters.put("Component", comp);
		parameters.put("Version", Config.VERSION);
		parameters.put("Event", event.toString());
		if(!(log.isDebugEnabled() || log.isTraceEnabled())) {
			HTTPAccess.accessURL("http://usage.jfireware.de/log.php", parameters);
		} else {
			HTTPAccess.accessURL("http://usage.jfireware.de/log_debug.php", parameters);
		}
	}
}
