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
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JProgressBar;

/**
 * @author Ian Kenny
 * 
 */
public class ImportGasSpectrometaryData extends Thread
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
	} // /:~
	private static FileHandler fh;
	private static FileWriter fSQL;
	private static final String LINESEPARATOR = java.lang.System
			.getProperty("line.separator");
	private static final String FILESEPARATOR = java.lang.System
			.getProperty("file.separator");

	private static Logger logger = Logger
			.getLogger("org.mltestbed.data.rawimport.ImportGasSpectrometaryData");
	private static final String S_GAS_DATA = "s:\\Gas Spectrometry";
	private static final String SEMG_IMPORT_PROPERTIES = "ImportGasSpectrometaryData.properties";
	private boolean bFinished = false;
	private Connection con = null;
	private String connectString;
	private String curFilename;
	private String dataSQLString = "Select * from GasSpectrometaryDataRaw";
	private String driver;
	private String filter;
	private String folder = S_GAS_DATA;
	private Vector<String> message = new Vector<String>();
	private JProgressBar progress;
	private Properties prop;
	private String pwd;
	private ResultSet rsData;
	private ResultSetMetaData rsmdData;
	private String uid;

	private String url = "jdbc:odbc:";
	/**
	 * 
	 */
	public ImportGasSpectrometaryData(JProgressBar progress, Properties prop)
	{
		super();
		try
		{

			fh = new FileHandler("SEMGlog.?.xml".replaceFirst("\\?", Long.valueOf(
					getId()).toString()));
			fSQL = new FileWriter("SEMGdata.?.sql".replaceFirst("\\?",
					 Long.valueOf(getId()).toString()));
		} catch (SecurityException e)
		{
			logger.log(Level.SEVERE, e.getMessage());
			// e.printStackTrace();
		} catch (IOException e)
		{
			logger.log(Level.SEVERE, e.getMessage());;
			// e.printStackTrace();
		}
		// Request that every detail gets logged.

		logger.setLevel(Level.ALL);

		// Send logger output to our FileHandler.
		logger.addHandler(fh);
		// Initialise properties
		this.prop = prop;
		this.progress = progress;
		initProps();
		saveProps();
	}
	@SuppressWarnings("unused")
	private void deleteExisting(String id, long trial, long sample)
	{
		try
		{
			Connection con = getConnection();
			Statement stmt = con.createStatement();
			String sql = "Delete "
					+ dataSQLString.substring(dataSQLString.toUpperCase()
							.indexOf("FROM"));
			sql += " where [id] = '" + id + "' And trial = " + trial
					+ " And [sample] = " + sample;
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
			logger.log(Level.SEVERE, "Sorry, I cannot find the FROM clause "
					+ e.getMessage());
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
			String sql = "DELETE FROM GasSpectrometaryDataRaw WHERE filename = '"
					+ curFilename + "'";
			try
			{
				Connection con = getConnection();

				Statement stmt = con.createStatement();
				// logSQL(sql);
				stmt.execute(sql);
				sql = "Update rawfiles Set isprocessed = 0 where name = '"
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
//		super.finalize();
	}
	private Connection getConnection() throws ClassNotFoundException,
			SQLException
	{
		Class.forName(driver);
		if (con == null)
			con = DriverManager.getConnection(url + connectString);
		return con;
	}
	/**
	 * @return the curFilename
	 */
	public String getCurFilename()
	{
		return curFilename;
	}
	public synchronized Vector<String> getMessage()
	{
		Vector<String> msg = new Vector<String>();
		msg.addAll(message);
		message.clear();
		return msg;

	}

	private synchronized String getNextFile(String tableName)
	{
		String sql = "Select name from toProcess";
		String name = null;
		try
		{
			Connection con = getConnection();
			// sql = sql.replaceFirst("\\?", "'"+files[i].getName()+"'");
			Statement stmt = con
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			// logSQL(sql);
			ResultSet rs = stmt.executeQuery(sql);

			if (!rs.next())
				putFiles(tableName);
			rs.close();
			// logSQL(sql);
			rs = stmt.executeQuery(sql);
			if (rs.next())
				name = rs.getString(1);
			stmt.close();
			updateProcessed(name);
		} catch (ClassNotFoundException e)
		{
			logger.log(Level.SEVERE, e.getMessage());;

		} catch (SQLException e)
		{
			logger.log(Level.SEVERE, e.getMessage());;

		}

		return name;
	}
	public void importData(String tableName)
	{
		// openDB();
		// deleteExisting();
		// File fileImport = new File(folder);
		// File files[] = fileImport.listFiles();
		// File files[] = fileImport.listFiles(new DirFilter(filter));

		// if (fileImport.isDirectory())'

		while ((curFilename = getNextFile(tableName)) != null)
		{

			// for (int i = 0; i < files.length; i++)
			// {

			FileInputStream file;
			try
			{

				File rawFile = new File(folder, curFilename);
				logger.log(Level.INFO, "Starting " + curFilename);

				file = new FileInputStream(rawFile);
				BufferedReader buf = new BufferedReader(new InputStreamReader(
						file));
				if (progress != null)
					progress.firePropertyChange("value", 0, 0);
				readData(buf, tableName, curFilename);
				buf.close();
				buf = null;
				message = null;
				// putMessage(new Integer(i).toString());
				// putMessage(new Integer(files.length).toString());
				putMessage(curFilename);

			} catch (FileNotFoundException e)
			{

				logger.log(Level.SEVERE, e.getMessage());
			} catch (IOException e)
			{

				logger.log(Level.SEVERE, e.getMessage());
			}

			logger.log(Level.INFO, "Finished: File " + curFilename);
		}
		logger.log(Level.INFO, "Processing finished");
		bFinished = true;
	}

	private void initProps()
	{
		if (prop == null)
			prop = new Properties();
		prop.put(
				"dataSQLString",
				dataSQLString = prop
						.getProperty("dataSQLString", dataSQLString));
		prop.put(
				"sourceConnectString",
				connectString = prop.getProperty("sourceConnectString",
						"GasData"));
		prop.put("userid", uid = prop.getProperty("userid", ""));
		prop.put("password", pwd = prop.getProperty("password", ""));
		prop.put(
				"driver",
				driver = prop.getProperty("driver",
						"sun.jdbc.odbc.JdbcOdbcDriver"));
		prop.put("url", url = prop.getProperty("url", url));
		prop.put("folder", folder = prop.getProperty("folder", folder));
		filter = prop.getProperty("fileFilter", "*.txt");
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
			this.prop.load(new FileInputStream(SEMG_IMPORT_PROPERTIES));
		} catch (FileNotFoundException e)
		{

			logger.log(Level.SEVERE, e.getMessage());
		} catch (IOException e)
		{
			logger.log(Level.SEVERE, e.getMessage());
		}
	}
	private String logSQL(String sql)
	{

		// try
		// {
		// fSQL.write("/*** "+DateFormat.getInstance().format(new Date())
		// +" "+ curFilename+ " ***/"+LINESEPARATOR);
		// fSQL.write(sql +LINESEPARATOR);
		// } catch (IOException e)
		// {
		// logger.log(Level.SEVERE,e.getMessage());;
		// logger.log(Level.SEVERE,e.getMessage());
		// }

		// Log.getLogger().info("SQL: "+ sql);
		return sql;
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
			rsData = stmt.executeQuery(dataSQLString);
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
	private synchronized void putFiles(String tableName)
	{
		File fileImport;
		if (tableName != null)
			fileImport = new File(folder + FILESEPARATOR + tableName);
		else
			fileImport = new File(folder);
		File files[] = fileImport.listFiles();

		for (int i = 0; i < files.length; i++)
		{
			String sql = "INSERT INTO rawFiles ( Name, isprocessed) VALUES ( ?, 0 )";
			try
			{
				Connection con = getConnection();
				if (tableName != null)
					sql = sql.replaceFirst("\\?", "'" + tableName + "\\"
							+ FILESEPARATOR + files[i].getName() + "'");
				else
					sql = sql.replaceFirst("\\?", "'" + files[i].getName()
							+ "'");
				//sql = sql.replace("\\", "\\\\");
				Statement stmt = con.createStatement();
				// logSQL(sql);
				stmt.execute(sql);
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
	public synchronized void putMessage(String str)
	{
		if (message == null)
			message = new Vector<String>();
		message.addElement(str);

	}
	private void readData(BufferedReader buf, String tableName, String fileName)
	{
		// read the 4 header lines;
		try
		{
			logger.log(Level.INFO, "Processing... " + fileName);
			// String key = fileName.split("\\.")[0];
			// String header = "";
			String line;
			// do
			// {
			// line = buf.readLine();
			// line = line.replaceAll("\'", "\'\'");
			// header += (line + LINESEPARATOR);
			// } while (!line.trim().equals(""));
			// writeHeader(fileName, key, header);
			//
			// logger.log(Level.INFO, "Processing... " + fileName + " body");
			int i = 0;
			while ((line = buf.readLine()) != null)
			{
				if (line.charAt(0) != '#')
					writeBody(tableName, fileName, i, line);
				i++;
			}
			logger.info(i + " lines processed for " + fileName);
		} catch (IOException e)
		{
			logger.log(Level.SEVERE, e.getMessage());
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
			String list[] = f.list();
			for (int i = 0; i < list.length; i++)
			{
				File f2 = new File(f.getAbsolutePath() + FILESEPARATOR
						+ list[i]);
				if (f2.isDirectory())
					importData(list[i]);
			}
		} else
			logger.log(Level.SEVERE, "Source folder not found");
		super.run();
	}
	private void saveProps()
	{
		try
		{
			this.prop.store(new FileOutputStream(SEMG_IMPORT_PROPERTIES),
					"Gas Data Input");
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
		String sql = "Update rawfiles Set isprocessed = 1 where name = '"
				+ name + "'";
		try
		{
			Connection con = getConnection();
			// sql = sql.replaceFirst("\\?", "'"+files[i].getName()+"'");
			Statement stmt = con.createStatement();
			// logSQL(sql);
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
	private void writeBody(String tablename, String fileName, int index,
			String line)
	{

		String sql = "INSERT INTO #table# ( filename, [index], ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, ch9, ch10, ch11) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		sql = sql.replaceFirst("#table#", tablename);
		String values[] = line.trim().split(",");
		if (values.length > 4)
		{
			try
			{

				Connection con = getConnection();

				Statement stmt = con.createStatement();
				// rsHeader.moveToInsertRow();
				// rsHeader.updateString(1, key);
				// rsHeader.updateInt(2,trial);
				// rsHeader.updateString(3, sensor );
				// rsHeader.updateInt(4, sample);
				// rsHeader.updateDouble(5, value);
				// rsHeader.insertRow();
				sql = sql.replaceFirst("\\?", "'" + fileName + "'");
				sql = sql.replace("\\", "\\\\");
				sql = sql.replaceFirst("\\?", String.valueOf(index));
				for (int i = 0; i < values.length; i++)
					sql = sql.replaceFirst("\\?", values[i]);
				logSQL(sql);
				stmt.execute(sql);
				stmt.close();
			} catch (SQLException e)
			{
				logger.log(Level.SEVERE, e.getMessage() + " " + sql);

			} catch (ClassNotFoundException e)
			{
				logger.log(Level.SEVERE, e.getMessage());

			} catch (ArrayIndexOutOfBoundsException e)
			{
				logger.log(Level.SEVERE, e.getMessage()
						+ " There's a problem with " + fileName + ":" + line);
			}

		} else
			logger.log(Level.SEVERE, "There's a problem with " + fileName + ":"
					+ line);
	}
	@SuppressWarnings("unused")
	private void writeHeader(String fileName, String key, String header)
	{
		String sql = "INSERT INTO SEMGfileHeader ( [filename], [header]) VALUES ( ?, ?)";
		try
		{
			Connection con = getConnection();

			sql = sql.replaceFirst("\\?", "'" + fileName + "'");
			sql = sql.replaceFirst("\\?", "'" + header + "'");
			Statement stmt = con.createStatement();
			logSQL(sql);
			stmt.execute(sql);
			// rsData.moveToInsertRow();
			// rsData.updateString(1, key );
			// rsData.updateString(2, fileName);
			// rsData.updateString(3, header);
			// Clob clob =rsData.getClob(3);
			//
			// clob.setString(0, header);
			// rsData.updateClob(3, clob);
			// rsData.insertRow();
			stmt.close();
			logger.info("Header for " + fileName + " processed");
		} catch (SQLException e)
		{
			logger.log(Level.SEVERE, e.getMessage() + " " + sql);
		} catch (ClassNotFoundException e)
		{
			logger.log(Level.SEVERE, e.getMessage());
		}

	}

}
