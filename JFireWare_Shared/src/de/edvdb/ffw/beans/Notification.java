package de.edvdb.ffw.beans;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import proguard.annotation.Keep;
import proguard.annotation.KeepPublicGettersSetters;

@Keep
@KeepPublicGettersSetters
@Entity
@Table(name = "Benachrichtigung")
public class Notification {
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Empfaenger getEmpfaenger() {
		return empfaenger;
	}

	public void setEmpfaenger(Empfaenger empfaenger) {
		this.empfaenger = empfaenger;
	}

	public Alarmfax getAlarmfax() {
		return alarmfax;
	}

	public void setAlarmfax(Alarmfax alarmfax) {
		this.alarmfax = alarmfax;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public Notification(Empfaenger empfaenger, Alarmfax alarmfax,
			NotificationType type, String returnCode, Date timestmp) {
		super();
		this.empfaenger = empfaenger;
		this.alarmfax = alarmfax;
		this.type = type;
		this.returnCode = returnCode;
		this.timestamp = timestmp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@Type(type = "de.edvdb.ffw.beans.Empfaenger")
	private Empfaenger empfaenger;
	
	@OneToOne(cascade = CascadeType.ALL)
	@Type(type = "de.edvdb.ffw.beans.Alarmfax")
	private Alarmfax alarmfax;
	
	@Enumerated(EnumType.STRING)
	private NotificationType type;
	
	@Column(name = "returnCode")
	private String returnCode;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "timestmp")
	private Date timestamp = new Date();
}
