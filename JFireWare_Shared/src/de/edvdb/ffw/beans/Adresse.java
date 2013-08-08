package de.edvdb.ffw.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;

import proguard.annotation.Keep;
import proguard.annotation.KeepPublicGettersSetters;

import de.edvdb.ffw.enums.MarkerColor;
import de.edvdb.ffw.util.Utils;

@Keep
@KeepPublicGettersSetters
@Entity
@Table(name = "Adressen")
public class Adresse {
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(Adresse.class);

	@Column(name = "strasse")
	private String strasse = "";

	@Column(name = "hausnummer")
	private String hausnummer = "";

	@Column(name = "plz")
	private String plz = "";

	@Column(name = "ort")
	private String ort = "";
	
	@Column(name = "latitude")
	private String lat = "";
	
	@Column(name = "longitude")
	private String lng = "";
	
	@Column(name = "exactMatch")
	private Boolean exact = false;

	@SuppressWarnings("unused")
	private static final String BLANK = "";

	@Transient
	private MarkerColor color = MarkerColor.red;

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private long id;

	public MarkerColor getColor() {
		return color;
	}

	public void setColor(MarkerColor color) {
		this.color = color;
	}

	public Adresse(String strasse, String hausnummer, String plz, String ort) {
		super();
		this.strasse = strasse;
		this.hausnummer = hausnummer;
		this.plz = plz;
		this.ort = ort;
	}
	
	public Adresse(String strasse, String hausnummer, String plz, String ort, String lat, String lng, Boolean exact) {
		super();
		this.strasse = strasse;
		this.hausnummer = hausnummer;
		this.plz = plz;
		this.ort = ort;
		this.lat = lat;
		this.lng = lng;
		this.exact = exact;
	}

	public Adresse() {
		super();
	}

	@Override
	public String toString() {
		if (isEmpty()) {
			return "";
		} else {
			return strasse + " " + hausnummer + ", " + plz + " " + ort;
		}
	}

	public String getURLMaskedString() {
		String tmp = strasse + " " + hausnummer + "," + plz + " " + ort;
		tmp = Utils.replaceSpecialChars(tmp);
		return Utils.encodeURL(tmp);
	}

	public boolean isEmpty() {
		boolean empty = true;
		empty &= "".equalsIgnoreCase(strasse);
		empty &= "".equalsIgnoreCase(hausnummer);
		empty &= "".equalsIgnoreCase(plz);
		empty &= "".equalsIgnoreCase(ort);
		return empty;
	}

	public void validate() {
		// Falls die Strasse numerische Inhalte hat, handelt es sich meist um
		// eine Autobahn oder Bundesstrasse, weitere Detailierung erfolgt nur
		// per PLZ & Ort
		String valStrasse = strasse;
		if (Utils.containsNumerics(valStrasse)) {
			String[] strasseArray = valStrasse.split(" ");
			for (int j = 0; j < strasseArray.length; j++) {
				if (Utils.containsNumerics(strasseArray[j])) {
					valStrasse = strasseArray[j];
					break;
				}
			}

		}
		strasse = valStrasse;


		// Zerlegt mehrteilige Orte in Einzelne Elemente "München München" -->
		// "München"
		String[] currentOrt = ort.split(" ");
		String valOrt = currentOrt[0];

		if (currentOrt.length == 4) {
			valOrt += " " + currentOrt[1];
		}
		ort = valOrt;

	}

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getHausnummer() {
		return hausnummer;
	}

	public void setHausnummer(String hausnummer) {
		this.hausnummer = hausnummer;
	}

	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Adresse) {
			// Check
			Adresse toCheck = (Adresse) obj;
			boolean equals = true;
			equals &= toCheck.getStrasse().equalsIgnoreCase(strasse);
			equals &= toCheck.getHausnummer().equalsIgnoreCase(hausnummer);
			equals &= toCheck.getPlz().equalsIgnoreCase(plz);
			equals &= toCheck.getOrt().equalsIgnoreCase(ort);
			return equals;
		} else {
			return false;
		}
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public Boolean getExact() {
		return exact;
	}

	public void setExact(Boolean exact) {
		this.exact = exact;
	}
	
	public boolean hasGeoData() {
		return !("".equalsIgnoreCase(lat) || "".equalsIgnoreCase(lng));
	}
}
