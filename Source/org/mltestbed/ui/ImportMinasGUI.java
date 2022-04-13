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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.mltestbed.data.ReadData;
import org.mltestbed.data.rawimport.ImportMinasPassageData;

public class ImportMinasGUI extends JDialog
		implements
			ActionListener,
			PropertyChangeListener
{
	private static final String DATA_INPUT_COMPLETE = "Data Input Complete";
	private static final String DATA_SUBSET = "Data Subset:";
	private static final String IMPORT_MINAS_DATA = "Import Minas Passage Data";
	private static final String JDBC_CONNECT = "JDBC Connect:";
	private static Logger logger = Logger
			.getLogger("org.mltestbed.data.rawimport.ImportMinasPassageData");
	private static final int MAX_THREADS = 5;
	private static final String MINASPASSAGE_IMPORT_PROPERTIES = "MinasPassageImport.properties";
	private static final String S_MINAS_DATA = "S:\\Minas Passage\\";
	private static final long serialVersionUID = 1L;
	private static final String SPECIFY_SOURCE_FOLDER = "Please Specify source folder: ";
	private JButton cancel;
	private String connectString;
	private String folder = S_MINAS_DATA;
	private ImportMinasPassageData[] ing;
	private JFormattedTextField jdbcEdit;
	private JButton ok;
	private JProgressBar progressbar;
	private Properties prop;
	private JFormattedTextField sourceEdit;
	private JComboBox<String> subsetBox;
	private String url = "jdbc:";
	private JLabel progressLabel;
	public ImportMinasGUI(Properties prop)
	{
		super();
		this.prop = prop;
		if (this.prop == null)
			this.prop = new Properties();
		initProps();
		// saveProps();
		connectString = this.prop.getProperty("sourceConnectString",
				"mysql://dbs1:3306/MinasPassage?user=root&password=calvin");
		createGUI();
		// openDB();

	}
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if (source == cancel)
		{
			if (ing != null)
				for (int i = 0; i < ing.length; i++)
					ing[i] = null;
			setVisible(false);
		} else if (source == ok)
		{
			folder = sourceEdit.getText();
			url = jdbcEdit.getText();
			int i = url.indexOf(':', 1) + 1;
			connectString = url.substring(i);
			url = url.substring(0, i);
			prop.put("import", subsetBox.getSelectedItem());
			prop.put("folder", folder);
			prop.put("url", url);
			prop.put("sourceConnectString", connectString);
			ing = new ImportMinasPassageData[MAX_THREADS];
			ok.setEnabled(!ok.isEnabled());
			progressbar.setIndeterminate(false);
			for (i = 0; i < MAX_THREADS; i++)
			{
				ing[i] = new ImportMinasPassageData(this, prop);
				ing[i].start();
//				i = MAX_THREADS;
			}
//			progressbar.setString(ing[0].getCurFilename());
//			progressbar.updateUI();
			// importData();
		}
	}
	// Method to create a button
	private JButton createButton(String label)
	{
		JButton button = new JButton(label); // Create the button
		button.setPreferredSize(new Dimension(80, 20)); // Set the size
		button.addActionListener(this); // Listener is the dialog
		return button; // Return the button
	}
	private void createGUI()
	{
		setTitle(IMPORT_MINAS_DATA);
		JLabel subsetLabel = new JLabel(DATA_SUBSET);
		subsetBox = new JComboBox<String>();
		subsetBox.addItem("All");
		subsetBox.addItem("Air Temp");
		subsetBox.addItem("Humidity");
		subsetBox.addItem("Precipitation");
		subsetBox.addItem("Barometric Pressure");

		JLabel jdbcLabel = new JLabel(JDBC_CONNECT);
		jdbcEdit = new JFormattedTextField(url + connectString);
		JLabel sourceFolder = new JLabel(SPECIFY_SOURCE_FOLDER);
		sourceEdit = new JFormattedTextField(folder);
		sourceEdit.addActionListener(this);
		setContentPane(new JPanel(new BorderLayout()));
		// Create the dialog button panel
		JPanel buttonPane = new JPanel(new GridLayout()); // Create a panel to
															// hold buttons

		buttonPane.add(progressbar = new JProgressBar(0, 100));
		progressbar.setStringPainted(true);
		progressbar.setString("");
		progressbar.addPropertyChangeListener("value", this);
		
		// Create and add the buttons to the buttonPane
		buttonPane.add(progressLabel = new JLabel(""));
		buttonPane.add(ok = createButton("OK")); // Add the OK button
		buttonPane.add(cancel = createButton("Cancel")); // Add the Cancel button
		progressLabel.addPropertyChangeListener("value",this);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);// Add pane to
																// content pane

		// Code to create the org.mltestbed.data input panel
		JPanel dataPane = new JPanel(); // Create the data
										// entry panel
		dataPane.setBorder(BorderFactory.createCompoundBorder(
				// Create pane
				// border
				BorderFactory.createLineBorder(Color.BLACK),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		GridBagLayout gbLayout = new GridBagLayout(); // Create the layout
		dataPane.setLayout(gbLayout); // Set the pane layout
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayout.setConstraints(subsetLabel, constraints);
		dataPane.add(subsetLabel);
		gbLayout.setConstraints(subsetBox, constraints);
		dataPane.add(subsetBox);
		gbLayout.setConstraints(jdbcLabel, constraints);
		dataPane.add(jdbcLabel);
		gbLayout.setConstraints(jdbcEdit, constraints);
		dataPane.add(jdbcEdit);
		gbLayout.setConstraints(sourceFolder, constraints);
		dataPane.add(sourceFolder);
		gbLayout.setConstraints(sourceEdit, constraints);
		dataPane.add(sourceEdit);
		dataPane.setMinimumSize(new Dimension(200, 200));
		getContentPane().add(dataPane, BorderLayout.CENTER);
		setResizable(true);
		pack();
		setVisible(false);
		setMinimumSize(new Dimension(500, 200));
	}
	private void initProps()
	{
		if (prop == null)
			prop = new Properties();
		loadProps();
		prop.put("airTempSQLString", prop.getProperty("airTempSQLString", ""));
		prop.put("humiditySQLString",
				prop.getProperty("humiditySQLString", ""));
		prop.put("sourceConnectString", connectString = prop.getProperty(
				"sourceConnectString",
				"mysql://dbs1:3306/MinasPassage?user=root&password=calvin"));
		prop.put("userid", prop.getProperty("userid", ""));
		prop.put("password", prop.getProperty("password", ""));
		prop.put("driver",
				prop.getProperty("driver", "com.mysql.cj.jdbc.Driver"));
		prop.put("url", url = prop.getProperty("url", url));
		prop.put("folder", folder = prop.getProperty("folder", folder));
		prop.put("fileFilter", prop.getProperty("fileFilter", "*.rd.???"));

	}

	private void loadProps()
	{
		try
		{
			this.prop.load(new FileInputStream(MINASPASSAGE_IMPORT_PROPERTIES));
		} catch (FileNotFoundException e)
		{

			logger.log(Level.SEVERE, e.getMessage());
		} catch (IOException e)
		{
			logger.log(Level.SEVERE, e.getMessage());
		}
	}
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getSource() == progressbar)
		{
			eventprocess();
		}
		else if (evt.getSource() == progressLabel)
			eventprocess();

	}

	/**
	 * 
	 */
	public void eventprocess()
	{
		boolean flag = true;
		for (int i = 0; i < MAX_THREADS; i++)
		{
			flag &= ing[i].isFinished();
			if (i == 0)
			{
				progressLabel.setText(ing[i].getMessage());
				progressLabel.updateUI();
				progressbar.setString(ing[i].getMessage());
			}
		}
		progressbar.setValue(progressbar.getValue()+1);
		if (flag)
		{
			progressbar.setIndeterminate(false);
			progressbar.updateUI();
			ok.setEnabled(true);
			JOptionPane.showMessageDialog(this, DATA_INPUT_COMPLETE);

		}
	}
	public JProgressBar getProgressBar()
	{
		return progressbar;
	}

}
