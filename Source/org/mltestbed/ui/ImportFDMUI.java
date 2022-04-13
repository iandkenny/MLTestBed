package org.mltestbed.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.mltestbed.data.rawimport.ImportFDMData;



public class ImportFDMUI extends JDialog
		implements
			ActionListener,
			PropertyChangeListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int MAX_THREADS = 5;
	private static final String S_FDM_DATA = "S:\\FDM\\";
	private static final String IMPORT_FDM_DATA = "Import FDM Data";
	private static final String SPECIFY_SOURCE_FOLDER = "Please Specify source folder: ";
	private static final String JDBC_CONNECT = "JDBC Connect:";
	private JButton ok;
	private JButton cancel;
	private JProgressBar progressbar;
	private JFormattedTextField sourceEdit;
	private JFormattedTextField jdbcEdit;
	private Properties prop;
	private String folder = S_FDM_DATA;
	private String url = "jdbc:odbc:";
	private String connectString;
	private ImportFDMData ing[];

	public ImportFDMUI(Properties prop)
	{
		super();
		this.prop = prop;
		if (this.prop == null)
			this.prop = new Properties();
		// initProps();
		// saveProps();
		connectString = this.prop.getProperty("sourceConnectString", "FDMData");
		createGUI();
		// openDB();

	}
	private void createGUI()
	{
		setTitle(IMPORT_FDM_DATA);
		JLabel jdbcLabel = new JLabel(JDBC_CONNECT);
		jdbcEdit = new JFormattedTextField(url + connectString);
		JLabel sourceFolder = new JLabel(SPECIFY_SOURCE_FOLDER);
		sourceEdit = new JFormattedTextField(folder);
		sourceEdit.addActionListener(this);
		setContentPane(new JPanel(new BorderLayout()));
		// button
		// Create the dialog button panel
		JPanel buttonPane = new JPanel(new GridLayout()); // Create a panel to
															// hold buttons

		buttonPane.add(progressbar = new JProgressBar(0, 100));
		progressbar.setStringPainted(false);
		progressbar.setString("");
		progressbar.addPropertyChangeListener("value", this);
		// Create and add the buttons to the buttonPane
		buttonPane.add(ok = createButton("OK")); // Add the OK button
		buttonPane.add(cancel = createButton("Cancel")); // Add the Cancel
		getContentPane().add(buttonPane, BorderLayout.SOUTH);// Add pane to
																// content pane

		// Code to create the org.mltestbed.data input panel
		JPanel dataPane = new JPanel(); // Create the org.mltestbed.data entry panel
		dataPane.setBorder(BorderFactory.createCompoundBorder( // Create pane
																// border
				BorderFactory.createLineBorder(Color.BLACK), BorderFactory
						.createEmptyBorder(5, 5, 5, 5)));
		GridBagLayout gbLayout = new GridBagLayout(); // Create the layout
		dataPane.setLayout(gbLayout); // Set the pane layout
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayout.setConstraints(jdbcLabel, constraints);
		dataPane.add(jdbcLabel);
		gbLayout.setConstraints(jdbcEdit, constraints);
		dataPane.add(jdbcEdit);
		gbLayout.setConstraints(sourceFolder, constraints);
		dataPane.add(sourceFolder);
		gbLayout.setConstraints(sourceEdit, constraints);
		dataPane.add(sourceEdit);
		dataPane.setSize(200, 200);
		getContentPane().add(dataPane, BorderLayout.CENTER);
		pack();
		setVisible(false);

	}
	// Method to create a button
	private JButton createButton(String label)
	{
		JButton button = new JButton(label); // Create the button
		button.setPreferredSize(new Dimension(80, 20)); // Set the size
		button.addActionListener(this); // Listener is the dialog
		return button; // Return the button
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if (source == cancel)
		{
			for (int i = 0; i < MAX_THREADS; i++)
				ing[i] = null;
			setVisible(false);
		} else if (source == sourceEdit)
			folder = sourceEdit.getText();
		else if (source == jdbcEdit)
		{
			url = jdbcEdit.getText();
			int i = url.lastIndexOf(':', 1) + 1;
			connectString = url.substring(i);
			url = url.substring(1, i);
			prop.put("url", url);
			prop.put("sourceConnectString", connectString);

		} else if (source == ok)
		{
			ing = new ImportFDMData[MAX_THREADS];
			ok.setEnabled(!ok.isEnabled());
			progressbar.setIndeterminate(true);
			for (int i = 0; i < MAX_THREADS; i++)
			{
				ing[i] = new ImportFDMData(prop);
				ing[i].start();
			}
			progressbar.setString(ing[0].getCurFilename());
			progressbar.updateUI();
			// importData();
		}
	}
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getSource() == progressbar)
		{
			// progressbar.setValue( new Integer((String) evt.getNewValue()));
			// progressbar.updateUI();
			progressbar.setString(ing[0].getCurFilename());
			progressbar.updateUI();
			boolean flag = true;
			for (int i = 0; i < MAX_THREADS; i++)
				flag &= ing[i].isFinished();
			if (flag)
			{
				ok.setEnabled(true);
			}
		}

	}

}
