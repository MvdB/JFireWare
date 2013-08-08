package de.edvdb.ffw.gui;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import de.edvdb.ffw.beans.Empfaenger;
import de.edvdb.ffw.db.Database;

public class EmpfaengerTableModel extends AbstractTableModel {
	private static Logger log = Logger.getLogger(EmpfaengerTableModel.class);
	private static final long serialVersionUID = 6346138757524649446L;

	@SuppressWarnings("unchecked")
	List<Empfaenger> empfaengerListe = Database.getSession()
			.createCriteria(Empfaenger.class).list();

	public EmpfaengerTableModel() {

	}

	@Override
	public int getRowCount() {
		return empfaengerListe != null ? empfaengerListe.size() : 0;
	}

	@Override
	public int getColumnCount() {
		return 7;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return empfaengerListe.get(rowIndex).getId();
		case 1:
			return empfaengerListe.get(rowIndex).getNachname();
		case 2:
			return empfaengerListe.get(rowIndex).getVorname();
		case 3:
			return empfaengerListe.get(rowIndex).getEmail();
		case 4:
			return empfaengerListe.get(rowIndex).isEmailEnable();
		case 5:
			return empfaengerListe.get(rowIndex).getHandynummer();
		case 6:
			return empfaengerListe.get(rowIndex).isSmsEnable();
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 1:
			return true;
		case 2:
			return true;
		case 3:
			return true;
		case 4:
			return true;
		case 5:
			return true;
		case 6:
			return true;
		default:
			return false;
		}
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Id";
		case 1:
			return "Nachname";
		case 2:
			return "Vorname";
		case 3:
			return "EMail-Adresse";
		case 4:
			return "EMail aktiv";
		case 5:
			return "Handynummer";
		case 6:
			return "SMS aktiv";
		default:
			return super.getColumnName(column);
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return Integer.class;
		case 1:
			return String.class;
		case 2:
			return String.class;
		case 3:
			return String.class;
		case 4:
			return Boolean.class;
		case 5:
			return String.class;
		case 6:
			return Boolean.class;
		default:
			return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		log.debug("setValueAt(Object " + aValue + ", int " + rowIndex
				+ ", int " + columnIndex + ")");
		switch (columnIndex) {
		case 1:
			empfaengerListe.get(rowIndex).setNachname((String) aValue);
			break;
		case 2:
			empfaengerListe.get(rowIndex).setVorname((String) aValue);
			break;
		case 3:
			empfaengerListe.get(rowIndex).setEmail((String) aValue);
			break;
		case 4:
			empfaengerListe.get(rowIndex).setEmailEnable((Boolean) aValue);
			break;
		case 5:
			empfaengerListe.get(rowIndex).setHandynummer((String) aValue);
			break;
		case 6:
			empfaengerListe.get(rowIndex).setSmsEnable((Boolean) aValue);
			break;
		default:
			super.setValueAt(aValue, rowIndex, columnIndex);
			break;
		}
		fireTableDataChanged();
	}

	public void saveChanges() {
		for (Empfaenger empfaenger : empfaengerListe) {
			if ("".equalsIgnoreCase(empfaenger.getNachname()) || "".equalsIgnoreCase(empfaenger.getVorname())
			|| ("".equalsIgnoreCase(empfaenger.getHandynummer()) && "".equalsIgnoreCase(empfaenger.getEmail()))) {
				JOptionPane.showMessageDialog(null, "Bei mindestens einem Datensatz wurden nicht alle nötigen Felder gefüllt. Bis zur Korrektur dieses Fehlers wird KEINE Änderung gespeichert. (Mindestens benötigt: Nachname, Vorname zzgl. EMail und/oder SMS)", "Datenfehler", JOptionPane.ERROR_MESSAGE);
			} else {
				log.info("Saving '" + empfaenger.toString() + "'");
				if (empfaenger.getId() != 0) {
					Database.merge(empfaenger);
				} else {
					Database.persist(empfaenger);
				}
			}
		}
		fireTableDataChanged();
	}

	public void addRow(Empfaenger e) {
		empfaengerListe.add(e);
		fireTableDataChanged();
	}

	public void deleteRow(int selectedRow) {
		Empfaenger empfaenger = empfaengerListe.get(selectedRow);
		log.info("Deleting '" + empfaenger.toString() + "'");
		if (empfaenger.getId() != 0) {
			Database.delete(empfaenger);
		}
		empfaengerListe.remove(selectedRow);
		fireTableDataChanged();
	}
}
