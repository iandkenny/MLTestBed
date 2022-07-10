/**
 * 
 */
package org.mltestbed.data.rawimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JProgressBar;

import org.mltestbed.ui.ImportMinasGUI;
import org.mltestbed.util.Util;

/**
 * @author Ian Kenny
 *
 */
public class ImportMinasPassageData extends Thread
{
	class DirFilter implements FilenameFilter
	{
		String afn;
		DirFilter(String afn)
		{
			this.afn = afn;
		}
		public boolean accept(File dir, String name)
		{
			// Strip path information:
			String f = new File(name).getName();
			return f.indexOf(afn) != -1;
		}
	} /// :~
	private static final String AIR_TEMP = "AirTemp";
	private static final String BAROMETRIC = "Barometric";
	private static FileHandler fh;
	private static FileWriter fSQL;
	private static final String HUMIDITY = "Humidity";
	private static final String LINESEPARATOR = java.lang.System
			.getProperty("line.separator");
	private static Logger logger = Logger
			.getLogger("org.mltestbed.data.rawimport.ImportMinasPassageData");
	private static final String MINASPASSAGE_IMPORT_PROPERTIES = "MinasPassageImport.properties";
	private static final String PRECIPITATION = "Precipitation";
	private static final String S_MinasPassage_DATA = "S:\\MinasPassage\\";
	private String airTempSQLString = "Select * from AirTemp";
	private boolean bFinished = false;
	private Connection con = null;
	private String connectString;
	private String curFilename;
	private String driver;
	private String filter;
	private String folder = S_MinasPassage_DATA;
	private String humiditySQLString = "Select * from Humidity";
	private String message = "";
	private JProgressBar progress;
	private ImportMinasGUI dialog;
	private Properties prop;
	private String pwd;
	private ResultSet rsData;
	private ResultSet rsHeader;
	private ResultSetMetaData rsmdData;
	private ResultSetMetaData rsmdHeader;
	private String uid;

	private String url = "jdbc:";
	private int filestoprocess;
	/**
	 * 
	 */
	public ImportMinasPassageData(ImportMinasGUI dialog, Properties prop)
	{
		super();
//		try
//		{
//
//			//fh = new FileHandler("MinasPassagelog.?.xml".replaceFirst("\\?",
//			//		Long.valueOf(getId()).toString()));
//			// fSQL = new
//			// FileWriter("MinasPassagedata.?.sql".replaceFirst("\\?",
//			// Long.valueOf(getId()).toString()));
//		} catch (SecurityException e)
//		{
//			logger.log(Level.SEVERE, e.getMessage());
////			e.printStackTrace();
//		} catch (IOException e)
//		{
//			logger.log(Level.SEVERE, e.getMessage());;
////			e.printStackTrace();
//		}
		// Request that every detail gets logged.

		logger.setLevel(Level.ALL);

		// Send logger output to our FileHandler.
		if (fh != null)
			logger.addHandler(fh);
		// Initialise properties
		this.prop = prop;
		this.dialog = dialog;
		this.progress = dialog.getProgressBar();
		saveProps();
		initProps();
	}
	private void airTempReadData(BufferedReader buf, String fileName)
	{
		try
		{
			String line;
			logger.log(Level.INFO, "Processing... " + fileName + " body");
			long i = 0;
			while ((line = buf.readLine()) != null)
			{
				if (line.charAt(0) != '#')
					airTempWriteBody(fileName, line);
				i++;
			}
			logger.info(i + " lines processed for " + fileName);
		} catch (IOException e)
		{
			logger.log(Level.SEVERE, e.getMessage());
		}

	}

	private void airTempWriteBody(String fileName, String line)
	{

		String sql = "INSERT INTO AirTemp ( timeindex, airtemp, AirTemperatureQCFlag) VALUES (  ?, ?, ?)";
		String values[] = line.trim().split(",");
		if (values.length >= 2)
		{
			String time = values[0].split("Z")[0];
			String temp = (Util.isNumeric(values[1])) ? values[1] : "null";
			String qc = values[2];

			try
			{
				Connection con = getConnection();

				Statement stmt = con.createStatement();
				sql = sql.replaceFirst("\\?", "'" + time + "'");
				sql = sql.replaceFirst("\\?", temp);
				sql = sql.replaceFirst("\\?", qc);
				logSQL(sql);
				stmt.execute(sql);
				stmt.close();
			} catch (SQLException e)
			{
				logger.log(Level.SEVERE, e.getMessage() + " " + sql);

			} catch (ClassNotFoundException e)
			{
				logger.log(Level.SEVERE, e.getMessage());
			}

		} else
			logger.log(Level.SEVERE,
					"There's a problem with " + fileName + ":" + line);
	}
	private void barometricReadData(BufferedReader buf, String fileName)
	{
		try
		{
			String line;
			logger.log(Level.INFO, "Processing... " + fileName + " body");
			long i = 0;
			while ((line = buf.readLine()) != null)
			{
				if (line.charAt(0) != '#')
					barometricWriteBody(fileName, line);
				i++;
			}
			logger.info(i + " lines processed for " + fileName);
		} catch (IOException e)
		{
			logger.log(Level.SEVERE, e.getMessage());
		}

	}
	private void barometricWriteBody(String fileName, String line)
	{

		String sql = "INSERT INTO  Barometric ( timeindex, barometricPressure, barometricPressureQCFlag) VALUES (  ?, ?, ?)";
		String values[] = line.trim().split(",");
		if (values.length >= 2)
		{
			String time = values[0].split("Z")[0];
			String barometric = (Util.isNumeric(values[1]))
					? values[1]
					: "null";
			String qc = values[2];

			try
			{
				Connection con = getConnection();

				Statement stmt = con.createStatement();
				sql = sql.replaceFirst("\\?", "'" + time + "'");
				sql = sql.replaceFirst("\\?", barometric);
				sql = sql.replaceFirst("\\?", qc);
				logSQL(sql);
				stmt.execute(sql);
				stmt.close();
			} catch (SQLException e)
			{
				logger.log(Level.SEVERE, e.getMessage() + " " + sql);

			} catch (ClassNotFoundException e)
			{
				logger.log(Level.SEVERE, e.getMessage());
			}

		} else
			logger.log(Level.SEVERE,
					"There's a problem with " + fileName + ":" + line);
	}
	@SuppressWarnings("unused")
	private void deleteExistingAirTemp()
	{
		try
		{
			Connection con = getConnection();
			Statement stmt = con.createStatement();
			String sql = "Delete " + airTempSQLString
					.substring(airTempSQLString.toUpperCase().indexOf("FROM"));
			logSQL(sql);
			stmt.execute(sql);
			stmt.close();
		} catch (ClassNotFoundException e)
		{
			logger.log(Level.SEVERE, e.getMessage());

		} catch (SQLException e)
		{
			logger.log(Level.SEVERE, e.getMessage());

		} catch (StringIndexOutOfBoundsException e)
		{
			logger.log(Level.SEVERE,
					"Sorry, I cannot find the FROM clause " + e.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable
	{
		if (!bFinished && curFilename != null)
		{
			String sql = "";
			try
			{
				Connection con = getConnection();

				Statement stmt = con.createStatement();

				sql = "Update rawFiles Set isprocessed = 0 where name = '"
						+ curFilename + "'";
				stmt.close();
			} catch (SQLException e)
			{
				logger.log(Level.WARNING, e.getMessage() + " " + sql);
			} catch (ClassNotFoundException e)
			{
				logger.log(Level.WARNING, e.getMessage());
			}

		}
		saveProps();
//			super.finalize();
	}
	private Connection getConnection()
			throws ClassNotFoundException, SQLException
	{
		Class.forName(driver);
		if (con == null || con.isClosed())
			con = DriverManager.getConnection(url + connectString);
		return con;
	}
	private Connection getNewConnection()
			throws ClassNotFoundException, SQLException
	{
		Connection con = null;
		Class.forName(driver);
		int i = 0;
		while (con ==null && i<5)
		{
			con = DriverManager.getConnection(url + connectString);
			i++;
		}
		return con;
	}
	/**
	 * @return the curFilename
	 */
	public String getCurFilename()
	{
		return curFilename;
	}
	public synchronized String getMessage()
	{
		return message;

	}

	private synchronized String getNextFile(String subfolder)
	{
		String sql = "SELECT name from rawFiles where subfolder = '" + subfolder
				+ "'";
		String name = null;
		try
		{
			Connection con = getConnection();

			Statement stmt = con.createStatement(
					ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			// logSQL(sql);
			ResultSet rs = stmt.executeQuery(sql);

			if (!rs.next())
				putFiles(subfolder);
			rs.close();
			stmt.close();
			sql = "Select name from toProcess";
			stmt = con.createStatement();
			// logSQL(sql);
			rs = stmt.executeQuery(sql);
			if (rs.next())
				name = rs.getString(1);
			stmt.close();

		} catch (ClassNotFoundException e)
		{
			logger.log(Level.SEVERE, e.getMessage());;

		} catch (SQLException e)
		{
			logger.log(Level.SEVERE, e.getMessage());;

		}

		return name;
	}
	private void humidityReadData(BufferedReader buf, String fileName)
	{
		try
		{
			String line;
			logger.log(Level.INFO, "Processing... " + fileName + " body");
			long i = 0;
			while ((line = buf.readLine()) != null)
			{
				if (line.charAt(0) != '#')
					humidityWriteBody(fileName, line);
				i++;
			}
			logger.info(i + " lines processed for " + fileName);
		} catch (IOException e)
		{
			logger.log(Level.SEVERE, e.getMessage());
		}

	}
	private void humidityWriteBody(String fileName, String line)
	{

		String sql = "INSERT INTO Humidity ( timeindex, humidity, relativeHumidityQCFlag) VALUES (  ?, ?, ?)";
		String values[] = line.trim().split(",");
		if (values.length >= 2)
		{
			String time = values[0].split("Z")[0];
			String humidity = (Util.isNumeric(values[1])) ? values[1] : "null";
			String qc = values[2];

			try
			{
				Connection con = getConnection();

				Statement stmt = con.createStatement();
				sql = sql.replaceFirst("\\?", "'" + time + "'");
				sql = sql.replaceFirst("\\?", humidity);
				sql = sql.replaceFirst("\\?", qc);
				logSQL(sql);
				stmt.execute(sql);
				stmt.close();
			} catch (SQLException e)
			{
				logger.log(Level.SEVERE, e.getMessage() + " " + sql);

			} catch (ClassNotFoundException e)
			{
				logger.log(Level.SEVERE, e.getMessage());
			}

		} else
			logger.log(Level.SEVERE,
					"There's a problem with " + fileName + ":" + line);
	}
	public void importData(String subfolder)
	{
		curFilename = "";
		filestoprocess = 0;
		while (curFilename != null)
		{
			curFilename = getNextFile(subfolder);
			if (curFilename != null && !curFilename.isEmpty())
			{
				updateProcessed(curFilename);

				processfile(curFilename,subfolder);

				logger.log(Level.INFO, "Finished: File " + curFilename);
				curFilename = "";
			}
		}
		logger.log(Level.INFO, "Processing finished");
		bFinished = true;
		if(dialog != null)
			dialog.fireEvent();
	}
	/**
	 * @param subfolder
	 */
	public void processfile(String curFilename,String subfolder)
	{
		FileInputStream file;
		try
		{
			File rawFile = new File(folder + File.separator + subfolder,
					curFilename);
			logger.log(Level.INFO, "Starting " + curFilename);
			putMessage("Starting " + curFilename);
			file = new FileInputStream(rawFile);
			BufferedReader buf = new BufferedReader(
					new InputStreamReader(file));
			if (subfolder.equals(AIR_TEMP))
				airTempReadData(buf, curFilename);
			else if (subfolder.equals(HUMIDITY))
				humidityReadData(buf, curFilename);
			else if (subfolder.equals(PRECIPITATION))
				precipitationReadData(buf, curFilename);
			else if (subfolder.equals(BAROMETRIC))
				barometricReadData(buf, curFilename);

			buf.close();
			putMessage("Finished " + curFilename);
		} catch (FileNotFoundException e)
		{

			logger.log(Level.SEVERE, e.getMessage());
		} catch (IOException e)
		{

			logger.log(Level.SEVERE, e.getMessage());
		}
	}
	private void initProps()
	{
		if (prop == null)
			prop = new Properties();
		prop.put("airTempSQLString", airTempSQLString = prop
				.getProperty("airTempSQLString", airTempSQLString));
		prop.put("humiditySQLString", humiditySQLString = prop
				.getProperty("humiditySQLString", humiditySQLString));
		prop.put("sourceConnectString", connectString = prop.getProperty(
				"sourceConnectString",
				"mysql://dbs1:3306/MinasPassage?user=root&password=calvin"));
		prop.put("userid", uid = prop.getProperty("userid", ""));
		prop.put("password", pwd = prop.getProperty("password", ""));
		prop.put("driver", driver = prop.getProperty("driver",
				"com.mysql.cj.jdbc.Driver"));
		prop.put("url", url = prop.getProperty("url", url));
		prop.put("folder", folder = prop.getProperty("folder", folder));
		filter = prop.getProperty("fileFilter", "*.rd.???");
		prop.put("fileFilter", filter);
		loadProps();

	}
	/**
	 * @return the bFinished
	 */
	public boolean isFinished()
	{
		return bFinished;
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
	private void logSQL(String sql)
	{

//	        try
//			{
//	        	fSQL.write("/*** "+DateFormat.getInstance().format(new Date())
//	        			+" "+ curFilename+ " ***/"+LINESEPARATOR);
//				fSQL.write(sql +LINESEPARATOR);
//			} catch (IOException e)
//			{
//				logger.log(Level.SEVERE,e.getMessage());
//			}

		logger.log(Level.INFO,"SQL: "+ sql);
	}
	@SuppressWarnings("unused")
	private void openDB()
	{
		Statement stmt;
		try
		{
			Connection con = getConnection();
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			rsHeader = stmt.executeQuery(airTempSQLString);
			rsmdHeader = rsHeader.getMetaData();
			rsData = stmt.executeQuery(humiditySQLString);
			rsmdData = rsData.getMetaData();
		} catch (ClassNotFoundException e)
		{
			System.out.println(e.getMessage());
			// logger.log(Level.SEVERE,e.getMessage());
		} catch (SQLException e)
		{
			System.out.println(e.getMessage());
			// logger.log(Level.SEVERE,e.getMessage());
		}

	}

	private void precipitationReadData(BufferedReader buf, String fileName)
	{
		try
		{
			String line;
			logger.log(Level.INFO, "Processing... " + fileName + " body");
			long i = 0;
			while ((line = buf.readLine()) != null)
			{
				if (line.charAt(0) != '#')
					precipitationWriteBody(fileName, line);
				i++;
			}
			logger.info(i + " lines processed for " + fileName);
		} catch (IOException e)
		{
			logger.log(Level.SEVERE, e.getMessage());
		}

	}

	private void precipitationWriteBody(String fileName, String line)
	{

		String sql = "INSERT INTO Precipitation ( timeindex, rainfallRate, rainfallRateQCFlag) VALUES (  ?, ?, ?)";
		String values[] = line.trim().split(",");
		if (values.length >= 2)
		{
			String time = values[0].split("Z")[0];
			String rainfall = (Util.isNumeric(values[1])) ? values[1] : "null";
			String qc = values[2];

			try
			{
				Connection con = getConnection();

				Statement stmt = con.createStatement();
				sql = sql.replaceFirst("\\?", "'" + time + "'");
				sql = sql.replaceFirst("\\?", rainfall);
				sql = sql.replaceFirst("\\?", qc);
				logSQL(sql);
				stmt.execute(sql);
				stmt.close();
			} catch (SQLException e)
			{
				logger.log(Level.SEVERE, e.getMessage() + " " + sql);

			} catch (ClassNotFoundException e)
			{
				logger.log(Level.SEVERE, e.getMessage());
			}

		} else
			logger.log(Level.SEVERE,
					"There's a problem with " + fileName + ":" + line);
	}
	private synchronized void putFiles(String subfolder)
	{
		File fileImport;
		if (subfolder != null)
			fileImport = new File(folder + File.separator + subfolder);
		else
			fileImport = new File(folder);
		if (fileImport.isDirectory())
		{
			try
			{
				Connection con = getConnection();
				Statement stmt = con.createStatement();
				File files[] = fileImport.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					if (!files[i].isDirectory())
					{
						String sql = "INSERT INTO rawFiles ( Name, subfolder) VALUES ( ?, ? )";
						sql = sql.replaceFirst("\\?",
								"'" + files[i].getName() + "'");
						sql = sql.replaceFirst("\\?", "'" + subfolder + "'");
//				logSQL(sql);
						stmt.execute(sql);
					}
				}
				stmt.close();
			} catch (ClassNotFoundException e)
			{

				logger.log(Level.SEVERE, e.getMessage());
			} catch (SQLException e)
			{

				logger.log(Level.SEVERE, e.getMessage());
			}

		}
	}

	/**
	 * @return the filestoprocess
	 */
	public int getFilestoprocess()
	{
		return filestoprocess;
	}
	public synchronized void putMessage(String str)
	{

		message = str;
		if (progress != null && dialog != null)
		{
			progress.firePropertyChange("value", 0, 0);
			dialog.fireEvent();	
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public synchronized void run()
	{
		File f = new File(folder);

		if (f.isDirectory())
		{
			String type = prop.getProperty("import").toLowerCase();
			if (type.equals("all") || type.equals("air temp"))
				importData(AIR_TEMP);
			if (type.equals("all") || type.equals("humidity"))
				importData(HUMIDITY);
			if (type.equals("all") || type.equals("precipitation"))
				importData(PRECIPITATION);
			if (type.equals("all") || type.equals("barometric pressure"))
				importData(BAROMETRIC);

		} else
			logger.log(Level.SEVERE, "Source folder not found");
		f = null;
		super.run();
	}
	private void saveProps()
	{
		try
		{
			if (prop != null)
				this.prop.store(
						new FileOutputStream(MINASPASSAGE_IMPORT_PROPERTIES),
						"Bay Of Fundy - Minas Passage Data Input");
		} catch (FileNotFoundException e)
		{
			logger.log(Level.SEVERE, e.getMessage());
		} catch (IOException e)
		{

			logger.log(Level.SEVERE, e.getMessage());
		}
	}
	private void updateProcessed(String name)
	{
		String sql = "Update rawFiles Set isprocessed = 1 where name = '" + name
				+ "'";
		try
		{
			Connection con = getConnection();
			// sql = sql.replaceFirst("\\?", "'"+files[i].getName()+"'");
			Statement stmt = con.createStatement();
//			logSQL(sql);
			stmt.execute(sql);

			stmt.close();
		} catch (ClassNotFoundException e)
		{
			logger.log(Level.SEVERE, e.getMessage());;
			// logger.log(Level.SEVERE,e.getMessage());
		} catch (SQLException e)
		{
			logger.log(Level.SEVERE, e.getMessage());;
			// logger.log(Level.SEVERE,e.getMessage());
		}

	}
}