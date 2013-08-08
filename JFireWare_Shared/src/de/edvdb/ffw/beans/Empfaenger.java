package de.edvdb.ffw.beans;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;

import proguard.annotation.Keep;
import proguard.annotation.KeepPublicGettersSetters;

import de.edvdb.ffw.db.Database;

@Keep
@KeepPublicGettersSetters
@Entity
@Table(name = "Empfaenger")
public class Empfaenger {
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(Empfaenger.class);

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private long id;

	@Column(name = "vorname")
	private String vorname = "";

	@Column(name = "nachname")
	private String nachname = "";

	@Column(name = "email")
	private String email = "";

	@Column(name = "handynummer")
	private String handynummer = "";

	@Column(name = "emailaktiv")
	private boolean emailEnable = false;

	@Column(name = "smsaktiv")
	private boolean smsEnable = false;

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHandynummer() {
		return handynummer;
	}

	public void setHandynummer(String handynummer) {
		this.handynummer = handynummer;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setEmailEnable(boolean emailEnable) {
		this.emailEnable = emailEnable;
	}

	public boolean isEmailEnable() {
		return emailEnable;
	}

	public void setSmsEnable(boolean smsEnable) {
		this.smsEnable = smsEnable;
	}

	public boolean isSmsEnable() {
		return smsEnable;
	}

	public Empfaenger() {
		super();
	}

	public Empfaenger(String vorname, String nachname, String email,
			String handynummer, boolean emailEnable, boolean smsEnable) {
		super();
		this.vorname = vorname;
		this.nachname = nachname;
		this.email = email;
		this.handynummer = handynummer;
		this.emailEnable = emailEnable;
		this.smsEnable = smsEnable;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Empfaenger> getAll() {
		return Database.getSession().createCriteria(Empfaenger.class).list();
	}
	
	@Override
	public String toString() {
		return vorname + " " + nachname;
	}
	
}
