package de.edvdb.ffw.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import de.edvdb.ffw.beans.Empfaenger;
import de.edvdb.ffw.system.Config;

public class ConfigGUI extends JFrame {
	private static Logger log = Logger.getLogger(ConfigGUI.class);
	private static final long serialVersionUID = -4913846706896983952L;



	public ConfigGUI() {
		setTitle("ConfigGUI - " + Config.VERSION);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(910, 628);
		setResizable(false);
		setLayout(null);
		final EmpfaengerTableModel model = new EmpfaengerTableModel();
		final JTable table = new JTable(model);
		table.setShowGrid(false);
		table.setShowHorizontalLines(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(0).setPreferredWidth(45);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.getColumnModel().getColumn(2).setPreferredWidth(200);
		table.getColumnModel().getColumn(3).setPreferredWidth(200);
		table.getColumnModel().getColumn(4).setPreferredWidth(50);
		table.getColumnModel().getColumn(5).setPreferredWidth(150);
		table.getColumnModel().getColumn(6).setPreferredWidth(50);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(0, 0, 900, 550);
		add(scrollPane);
		JButton newButton = new JButton("Neu");
		newButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 3713383927758476514L;

			@Override
			public void actionPerformed(ActionEvent e) {
				model.addRow(new Empfaenger());
			}
		});
		newButton.setBounds(0, 550, 300, 50);
		add(newButton);
		JButton saveButton = new JButton("Speichern");
		saveButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 4033767901952621814L;

			@Override
			public void actionPerformed(ActionEvent e) {
				model.saveChanges();
			}
		});
		saveButton.setBounds(300, 550, 300, 50);
		add(saveButton);
		JButton deleteButton = new JButton("Löschen");
		deleteButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = -6319470469293695413L;

			@Override
			public void actionPerformed(ActionEvent e) {
				model.deleteRow(table.getSelectedRow());
			}
		});
		deleteButton.setBounds(600, 550, 300, 50);
		add(deleteButton);
	}
	
	
	
	public static void main(String[] args) {
		DOMConfigurator.configure("..\\conf\\log4j.xml");
		ConfigGUI gui = new ConfigGUI();
		log.info("Starting GUI..");
		gui.setVisible(true);
	}
}
