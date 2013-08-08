package de.edvdb.ffw.db;

import java.io.File;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import de.edvdb.ffw.system.Config;

public class Database {

	private static SessionFactory sessions = null;
	private static Session session = null;
	
	public static void initDatabase() {
		if (sessions == null) {
			File confFile = new File(Config.HIBERNATECONF);
			Configuration cfg = new Configuration();
			sessions = cfg.configure(confFile).buildSessionFactory();
			sessions.getStatistics().setStatisticsEnabled(true);
		}
	}
	
	public static Session getSession() {
		if((session == null) || (!session.isOpen())) {
			initDatabase();
			session = sessions.openSession();
		}
		return session;
	}

	public static Object merge(Object toMerge) {
		initDatabase();
		Session session = getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.merge(toMerge);
            tx.commit();
        }
        catch (HibernateException he) {
        	if (tx!=null) tx.rollback();
            throw he;
        }
        finally {
        	session.close();
        }
        return toMerge;
	}
	
	public static Object persist(Object toPersist) {
		initDatabase();
		Session session = getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.persist(toPersist);
            tx.commit();
        }
        catch (HibernateException he) {
        	if (tx!=null) tx.rollback();
            throw he;
        }
        finally {
        	session.close();
        }
        return toPersist;
	}
	
	public static Object delete(Object toDelete) {
		initDatabase();
		Session session = getSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(toDelete);
            tx.commit();
        }
        catch (HibernateException he) {
        	if (tx!=null) tx.rollback();
            throw he;
        }
        finally {
        	session.close();
        }
        return toDelete;
	}
}
