package de.edvdb.ffw.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import proguard.annotation.Keep;
import proguard.annotation.KeepPublicGettersSetters;

import de.edvdb.ffw.system.Config;
import de.edvdb.ffw.util.Utils;
import de.edvdb.ffw.web.util.HTTPAccess;

@Keep
@KeepPublicGettersSetters
@Entity
@Table(name = "Alarmfaxe")
public class Alarmfax {
	private static Logger log = Logger.getLogger(Alarmfax.class);
	private static Pattern fahrzeugPattern = Pattern.compile("[0-9]{2}/[0-9]{1}");

	public Alarmfax() {
		detailMap = new File(Config.TEMPDIR + "detail_"
				+ getFormattedTimestamp() + ".png");
		overviewMap = new File(Config.TEMPDIR + "overview_"
				+ getFormattedTimestamp() + ".png");
		log.trace("Prepared Files [" + detailMap.getName() + " / "
				+ overviewMap.getName() + "]");
	}

	public Adresse getAdresse() {
		return adresse;
	}

	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "timestmp")
	private Date timestamp = new Date();
	
	@ElementCollection
	@CollectionTable(name = "Zustaendigkeiten", joinColumns = @JoinColumn(name = "id"))
	@Column(name = "zustaendigkeit")
	private Set<String> zustaendigkeit = new HashSet<String>();

	@OneToOne(cascade = CascadeType.ALL)
	@Type(type = "de.edvdb.ffw.beans.Adresse")
	private Adresse adresse;

	@ElementCollection
	@CollectionTable(name = "Schlagworte", joinColumns = @JoinColumn(name = "id"))
	@Column(name = "schlagwort")
	private Set<String> schlagwort = new HashSet<String>();

	@ElementCollection
	@CollectionTable(name = "Bemerkungen", joinColumns = @JoinColumn(name = "id"))
	@Column(name = "bemerkung")
	private List<String> bemerkung = new ArrayList<String>();

	public Set<String> getSchlagwort() {
		return schlagwort;
	}

	public void setSchlagwort(Set<String> schlagwort) {
		this.schlagwort = schlagwort;
	}

	@ElementCollection
	@CollectionTable(name = "Einsatzmittel", joinColumns = @JoinColumn(name = "id"))
	@Column(name = "einsatzmittel")
	@org.hibernate.annotations.OrderBy(clause = "einsatzmittel")
	private Set<String> einsatzmittel = new HashSet<String>();

	@ElementCollection
	@CollectionTable(name = "Fahrzeuge", joinColumns = @JoinColumn(name = "id"))
	@Column(name = "fahrzeuge")
	@org.hibernate.annotations.OrderBy(clause = "fahrzeuge")
	private Set<String> fahrzeuge = new HashSet<String>();

	@Transient
	private File detailMap;

	public Set<String> getEinsatzmittel() {
		return einsatzmittel;
	}

	public void setEinsatzmittel(Set<String> einsatzmittel) {
		this.einsatzmittel = einsatzmittel;
	}

	public Set<String> getFahrzeuge() {
		return fahrzeuge;
	}
	
	public List<String> getFahrzeugeShort() {
		List<String> fahrzeugeShort = new ArrayList<String>();
		for(String f : fahrzeuge) {
			Matcher m = fahrzeugPattern.matcher(f);
			if(m.find()) {
				fahrzeugeShort.add(m.group());
			}
		}
		return fahrzeugeShort;
	}
	public void setFahrzeuge(Set<String> fahrzeuge) {
		this.fahrzeuge = fahrzeuge;
	}

	public File getDetailMap() {
		return detailMap;
	}

	public void setDetailMap(File detailMap) {
		this.detailMap = detailMap;
	}

	public File getOverviewMap() {
		return overviewMap;
	}

	public void setOverviewMap(File overviewMap) {
		this.overviewMap = overviewMap;
	}

	@Transient
	private File overviewMap;

	@Override
	public String toString() {
		return "ID: " + id + " # " + adresse.toString() + " [" + fahrzeuge
				+ " | " + einsatzmittel + " | " + schlagwort + " | "
				+ bemerkung + " | " + getZustaendigkeit() + "]";
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getFormattedTimestamp() {
		return Utils.dateFormat.format(timestamp);
	}
	
	public String getReadableTimestamp() {
		return Utils.readableFormat.format(timestamp);
	}

	public void cleanUp() {
		detailMap.delete();
		overviewMap.delete();
	}

	public boolean isEmpty() {
		if(log.isDebugEnabled()) {
			log.debug(bemerkung);
			log.debug(einsatzmittel);
			log.debug(fahrzeuge);
			log.debug(fahrzeuge);
			log.debug(adresse);
		}
		
		return adresse.isEmpty() || (einsatzmittel.isEmpty() && fahrzeuge.isEmpty() && schlagwort.isEmpty() && bemerkung.isEmpty());
	}

	public void setBemerkung(List<String> bemerkung) {
		this.bemerkung = bemerkung;
	}

	public List<String> getBemerkung() {
		return bemerkung;
	}

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



	public void prepareMaps() {
		HTTPAccess.getImageForAddress(getDetailMap(), getAdresse());
		HTTPAccess.getImageForAddress(getOverviewMap(), getAdresse(), Config.FFWADRESSE);
	}

	@Override
	public boolean equals(Object comp) {
		if (comp instanceof Alarmfax) {
			Alarmfax a = (Alarmfax) comp;
			return a.id == this.id;
		}
		return false;
	}

	public Set<String> getZustaendigkeit() {
		return zustaendigkeit;
	}

	public void setZustaendigkeit(Set<String> zustaendigkeit) {
		this.zustaendigkeit = zustaendigkeit;
	}
}
