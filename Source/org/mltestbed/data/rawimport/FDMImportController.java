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
import java.util.logging.MemoryHandler;

import javax.swing.JProgressBar;

import org.mltestbed.util.ReadTar;


/**
 * @author Ian Kenny
 * 
 */
public class FDMImportController extends Thread
{
	private static final String FDM_IMPORT_PROPERTIES = "FDMImport.properties";
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

	private static final String TMPDIR = java.lang.System
			.getProperty("java.io.tmpdir")
			+ "FDMData";

	private String connectString;
	private String curFilename;
	private boolean bFinished = false;
	private static final String FDM_DATA = "E:\\FDM\\";
	private String folder = FDM_DATA;
	private static final String LINESEPARATOR = java.lang.System
			.getProperty("line.separator");
	private static final String FILESEPARATOR = java.lang.System
			.getProperty("file.separator");
	private static Logger logger = Logger
			.getLogger("org.mltestbed.data.rawimport.ImportFDMData");
	private String dataSQLString = "Select * from FDMdata";
	private String driver;
	private String filter = ".tar";
	private String headerSQLString = "Select * from FDMheader";
	private Vector<String> message = new Vector<String>();
	private Properties prop;
	private String pwd;
	private String uid;

	private String url = "jdbc:odbc:";

	private boolean isLogging = true;

	/**
	 * 
	 */
	public FDMImportController()
	{
		super();
		initProps();
	}

	private void initProps()
	{
		if (prop == null)
			prop = new Properties();
		prop.put("headerSQLString", headerSQLString = prop.getProperty(
				"headerSQLString", headerSQLString));
		prop.put("dataSQLString", dataSQLString = prop.getProperty(
				"dataSQLString", dataSQLString));
		prop.put("sourceConnectString", connectString = prop.getProperty(
				"sourceConnectString", "FDMData"));
		prop.put("userid", uid = prop.getProperty("userid", ""));
		prop.put("password", pwd = prop.getProperty("password", ""));
		prop.put("driver", driver = prop.getProperty("driver",
				"sun.jdbc.odbc.JdbcOdbcDriver"));
		prop.put("url", url = prop.getProperty("url", url));
		prop.put("folder", folder = prop.getProperty("folder", folder));
		filter = prop.getProperty("fileFilter", ".gz");
		prop.put("fileFilter", filter);
		loadProps();

	}
	private void loadProps()
	{
		try
		{
			this.prop.load(new FileInputStream(FDM_IMPORT_PROPERTIES));
		} catch (FileNotFoundException e)
		{

			log(Level.SEVERE, e.getMessage());
		} catch (IOException e)
		{
			log(Level.SEVERE, e.getMessage());
		}
	}
	private String getDetail(File file)
	{
		String pat = file.getName().substring(0, 6) + ".txt";
		String buf = "";
		try
		{

			String line;
			FileInputStream fis = new FileInputStream(new File(FDM_DATA
					+ FILESEPARATOR + pat));
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			while ((line = br.readLine()) != null)
				buf += LINESEPARATOR + line;
			br.close();
		} catch (FileNotFoundException e)
		{
			log(Level.SEVERE, e.getMessage());
		} catch (IOException e)
		{
			log(Level.SEVERE, e.getMessage());
		}

		return buf;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public synchronized void run()
	{
		importData();
		super.run();
	}
	public void importData()
	{
		// openDB();
		// deleteExisting();
		// File fileImport = new File(folder);
		// File files[] = fileImport.listFiles();
		// File files[] = fileImport.listFiles(new DirFilter(filter));

		// if (fileImport.isDirectory())
		do
		{
			curFilename = null;

			for (int i = 0; i < 10 && curFilename == null; i++)
				curFilename = getNextFile();
			if (curFilename != null && curFilename != "")
			{
				processTar(new File(curFilename));
				updateProcessed(curFilename);
				log(Level.INFO, "Finished: File " + curFilename);
			}
		} while (curFilename != null);
		log(Level.INFO, "Processing finished");
		bFinished = true;
	}
	private void log(Level level, String message)
	{
		if (isLogging)
			logger.log(level, message);
		System.out.println(message);
	}
	/**
	 * @return the bFinished
	 */
	public boolean isFinished()
	{
		return bFinished;
	}
	private void updateProcessed(String name)
	{
		String sql = "Update rawfiles Set isprocessed = 2 where name = '"
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
			log(Level.SEVERE, e.getMessage());;
			// log(Level.SEVERE,e.getMessage());
		} catch (SQLException e)
		{
			log(Level.SEVERE, e.getMessage());;
			// log(Level.SEVERE,e.getMessage());
		}

	}
	private long findStartPosition(BufferedReader buf, String name)
	{
		String sql = "Select Max(sample) From FDMData where name = '"
				+ name + "'";
		long value =0;
		try
		{
			Connection con = getConnection();
			// sql = sql.replaceFirst("\\?", "'"+files[i].getName()+"'");
			Statement stmt = con.createStatement();
			// logSQL(sql);
			ResultSet rs = stmt.executeQuery(sql);
			value = rs.getLong(1);
			stmt.close();
			// Cant use NumberedLineReader because the line numbers are limited to 32k
			for (long i = 0; i < value && buf.readLine()!=null; i++);
		} catch (ClassNotFoundException e)
		{
			log(Level.SEVERE, e.getMessage());;
			// log(Level.SEVERE,e.getMessage());
		} catch (SQLException e)
		{
			log(Level.SEVERE, e.getMessage());;
			// log(Level.SEVERE,e.getMessage());
		} catch (IOException e)
		{
			log(Level.SEVERE, e.getMessage());
		}
		
		return value;

	}

	private void ASCIIFileProcessed(String tar, String asc)
	{
		String sql = "INSERT INTO asciifiles ( tar,asciifile) VALUES ( ?, ?)";
		try
		{
			Connection con = getConnection();

			sql = sql.replaceFirst("\\?", "'" + tar + "'");
			sql = sql.replaceFirst("\\?", "'" + asc + "'");
			
			Statement stmt = con.createStatement();
			stmt.setQueryTimeout(0);
			logSQL(sql);
			stmt.execute(sql);
			stmt.close();
			con.close();
//			log(Level.INFO, "Header for " + fileName + " processed");
		} catch (SQLException e)
		{
			log(Level.SEVERE, e.getMessage() + " " + sql);
		} catch (ClassNotFoundException e)
		{
			log(Level.SEVERE, e.getMessage());
		}
	}
	private synchronized String getNextFile()
	{
		String sql = "SELECT name from rawFiles";
	
		String name = "";
		try
		{
			// There's a problem with Java synchronisation on startup
			// while(inWait)
			// wait(500);
			Connection con = getConnection();

			// sql = sql.replaceFirst("\\?", "'"+files[i].getName()+"'");
			Statement stmt = con
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			// logSQL(sql);
			stmt.setQueryTimeout(0);
			ResultSet rs = stmt.executeQuery(sql);

			if (!rs.first())
				putFiles();
			rs.close();
			// stmt.close();
			sql = "SELECT name from toprocess";
			// stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			// ResultSet.CONCUR_READ_ONLY);
			// logSQL(sql);
			// stmt.setQueryTimeout(0);
			con.setAutoCommit(false);
			rs = stmt.executeQuery(sql);
			if (rs.first())
			{
				rs.refreshRow();
				name = rs.getString(1);
				rs.close();
				String updatesql = "Update rawfiles Set isprocessed = 1 where name = '"
						+ name + "'";
				stmt.execute(updatesql);
			} else
				name = null;
			con.commit();

			stmt.close();

			con.close();
			// inWait=false;
			// notify();

		} catch (ClassNotFoundException e)
		{
			log(Level.SEVERE, e.getMessage());

		} catch (SQLException e)
		{
			if (e.getSQLState().equals("40001"))
				name = "";
			else
				log(Level.SEVERE, e.getMessage());

		}
		System.out.println(name);
		return name;
	}

	/**
	 * 
	 */
	private synchronized void putFiles()
	{
		File fileImport = new File(folder);
		File files[] = fileImport.listFiles();

		for (int i = 0; i < files.length; i++)
		{
			String sql = "INSERT INTO rawFiles ( Name, isprocessed ) VALUES ( ?, 0 )";
			try
			{
				Connection con = getConnection();
				sql = sql.replaceFirst("\\?", "'" + files[i].getName() + "'");
				Statement stmt = con.createStatement();
				// logSQL(sql);
				stmt.setQueryTimeout(0);
				stmt.execute(sql);
				stmt.close();
				con.close();
			} catch (ClassNotFoundException e)
			{

				log(Level.SEVERE, e.getMessage());
			} catch (SQLException e)
			{

				log(Level.SEVERE, e.getMessage());
			}
		}
	}
	/**
	 * @param fileName
	 */
	private void processTar(File file)
	{

		String[] dir;
		String fname = file.getName();
		String path = TMPDIR + FILESEPARATOR
				+ fname.substring(0, fname.indexOf("."));

		String detail = getDetail(file);
		boolean ital = (fname.toLowerCase().indexOf("interiktal") != -1
				? false
				: true);
		try
		{
			File tmpFile = new File(path);
			tmpFile.mkdirs();
			String pat = fname.substring(0, 6);
			writeHeader(fname, pat, detail);

			dir = tmpFile.list();
			ImportFDMData threads[] = new ImportFDMData[dir.length];
			for (int i = 0; i < dir.length; i++)
			{
				new File(path, dir[i]).delete();
				deleteExisting(fname);
			}
			log(Level.SEVERE,fname+ " is being processed by " + currentThread().getName());
			ReadTar.readTar(ReadTar.getInputStream(FDM_DATA + fname), path);
			tmpFile = new File(path);
			dir = tmpFile.list();
			for (int i = 0; i < dir.length; i++)
			{
				threads[i]= new ImportFDMData(prop);
				threads[i].setParams(path,dir[i],pat, ital);
				threads[i].start();
				Thread.sleep(5000);
			}
			boolean bFlag;
			do
			{
				bFlag = true;
				for (int i = 0; i < threads.length; i++)
				{
					threads[i].join();
					bFlag = bFlag && (threads[i].getState()!=Thread.State.TERMINATED);
//					Thread.sleep(5000);
				}
			} while (!bFlag);
		
			for (int i = 0; i < dir.length; i++)
			{
				ASCIIFileProcessed(fname,dir[i]);
				new File(path, dir[i]).delete();
				threads[i]=null;
			}

		} catch (Exception e)
		{
			log(Level.SEVERE, e.getMessage());
		}

	}
	private Connection getConnection() throws ClassNotFoundException,
			SQLException
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(url + connectString);
		return con;
	}
	private void deleteExisting(String fname)
	{
		try
		{
			Connection con = getConnection();
			Statement stmt = con.createStatement();
			// String sql ="Delete "+ headerSQLString.substring(
			// headerSQLString.toUpperCase().indexOf("FROM"));
			// logSQL(sql);
			// stmt.execute(sql);
			String sql = "Delete "
					+ dataSQLString.substring(dataSQLString.toUpperCase()
							.indexOf("FROM")) + " WHERE filename='" + fname
					+ "'";
			logSQL(sql);
			stmt.setQueryTimeout(0);
			stmt.execute(sql);
			stmt.close();
		} catch (ClassNotFoundException e)
		{
			log(Level.SEVERE, e.getMessage());

		} catch (SQLException e)
		{
			log(Level.SEVERE, e.getMessage());

		} catch (StringIndexOutOfBoundsException e)
		{
			log(Level.SEVERE, "Sorry, I cannot find the FROM clause "
					+ e.getMessage());
		}

	}

	private void logSQL(String sql)
	{
		// TODO Auto-generated method stub
		
	}
	private void writeHeader(String fileName, String key, String header)
	{
		String sql = "INSERT INTO FDMHeader ( id,[filename], header) VALUES ( ?, ?, ?)";
		try
		{
			Connection con = getConnection();

			sql = sql.replaceFirst("\\?", "'" + key + "'");
			sql = sql.replaceFirst("\\?", "'" + fileName + "'");
			sql = sql.replaceFirst("\\?", "'" + header + "'");
			Statement stmt = con.createStatement();
			stmt.setQueryTimeout(0);
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
			con.close();
			log(Level.INFO, "Header for " + fileName + " processed");
		} catch (SQLException e)
		{
			log(Level.SEVERE, e.getMessage() + " " + sql);
		} catch (ClassNotFoundException e)
		{
			log(Level.SEVERE, e.getMessage());
		}

	}



class ImportFDMData extends Thread
{
	private static final String FDM_IMPORT_PROPERTIES = "FDMImport.properties";
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
	private   FileHandler fh;
	private  FileWriter fSQL;
	private final String LINESEPARATOR = java.lang.System
			.getProperty("line.separator");
	private final String FILESEPARATOR = java.lang.System
			.getProperty("file.separator");
	private Logger logger = Logger
			.getLogger("org.mltestbed.data.rawimport.ImportFDMData");

	private boolean bFinished = false;
	private String connectString;
	private String curFilename;
	private String dataSQLString = "Select * from FDMdata";
	private String driver;
	private String filter = ".tar";
	private String headerSQLString = "Select * from FDMheader";
	private Vector<String> message = new Vector<String>();
	private JProgressBar progress;
	private Properties prop;
	private String pwd;
	private ResultSet rsData;
	private ResultSet rsHeader;
	private ResultSetMetaData rsmdData;
	private ResultSetMetaData rsmdHeader;
	private String uid;

	private String url = "jdbc:odbc:";

	private boolean isLogging = true;
	private String key;
	private String folder;
	private String fname;
	private String pat;
	private boolean ital;
	private String path;
	
	public ImportFDMData(Properties prop)
	{
		super();
		// Request that every detail gets logged.

		logger.setLevel(Level.ALL);
		try
		{
//			String pattern = "my%g.log";
			int limit = 1000000; // 1 Mb
//	        int numLogFiles = 3;

			fh = new FileHandler("FDMlog.xml",limit,1);
			fSQL = new FileWriter("FDMdata.sql");
		} catch (SecurityException e)
		{
			logger.log(Level.SEVERE, e.getMessage());
			// e.printStackTrace();
		} catch (IOException e)
		{
			logger.log(Level.SEVERE, e.getMessage());;
			// e.printStackTrace();
		}
		// Send logger output to our FileHandler.
		MemoryHandler memhandler = new MemoryHandler(fh, 500, Level.ALL);
		logger.addHandler(memhandler);
		// Initialise properties
		this.prop = prop;
		
		initProps();
		saveProps();
	}

	public void setParams(String path, String fname, String pat, boolean ital)
	{
		this.path = path;
		this.fname = fname;
		this.pat = pat;
		this.ital = ital;
		
	}

	private void processFile()
	{
		try
		{
			String[] fields;
			bFinished = false;
		
			log(Level.SEVERE, fname + " is being processed by "
					+ currentThread().getName());
				File rawFile = new File(path+FILESEPARATOR+fname);
//				log(Level.INFO, "Starting " + dir[i] + " file " + i + "/"
//						+ dir.length + " on thread "
//						+ currentThread().getName());
				fields = fname.split("_");
				key = fields[0];
				String block = fields[1];
				char sensor = fields[2].charAt(0);
				FileInputStream fis = new FileInputStream(rawFile);
				BufferedReader buf = new BufferedReader(new InputStreamReader(
						fis));
				if (progress != null)
					progress.firePropertyChange("value", 0, 0);
				readData(buf, fname, pat, key, ital, block, sensor);
				buf.close();
	
				message = null;
				
				// putMessage(new Integer(i).toString());
				// putMessage(new Integer(files.length).toString());
				putMessage(fname);
				bFinished = true;
		} catch (FileNotFoundException e)
		{
			logger.log(Level.SEVERE, e.getMessage());
		} catch (IOException e)
		{
			logger.log(Level.SEVERE, e.getMessage());
		} catch (Exception e)
		{
			logger.log(Level.SEVERE, e.getMessage());
		}
		
	}

	private void log(Level level, String message)
	{
		if (isLogging || level == Level.SEVERE)
			logger.log(level, message);
		System.out.println(message);
	}
	private Connection getConnection() throws ClassNotFoundException,
			SQLException
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(url + connectString);
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
	private void initProps()
	{
		if (prop == null)
			prop = new Properties();
		prop.put("headerSQLString", headerSQLString = prop.getProperty(
				"headerSQLString", headerSQLString));
		prop.put("dataSQLString", dataSQLString = prop.getProperty(
				"dataSQLString", dataSQLString));
		prop.put("sourceConnectString", connectString = prop.getProperty(
				"sourceConnectString", "FDMData"));
		prop.put("userid", uid = prop.getProperty("userid", ""));
		prop.put("password", pwd = prop.getProperty("password", ""));
		prop.put("driver", driver = prop.getProperty("driver",
				"sun.jdbc.odbc.JdbcOdbcDriver"));
		prop.put("url", url = prop.getProperty("url", url));
		prop.put("folder", folder = prop.getProperty("folder", folder));
		filter = prop.getProperty("fileFilter", ".gz");
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
	private void logSQL(String sql)
	{

		// try
		// {
		// fSQL.write("/*** Thread:" + this.currentThread().getName()
		// + LINESEPARATOR
		// + DateFormat.getInstance().format(new Date()) + " "
		// + curFilename + " ***/" + LINESEPARATOR);
		// fSQL.write(sql + LINESEPARATOR);
		// } catch (IOException e)
		// {
		// log(Level.SEVERE, e.getMessage());;
		// }

		// System.out.println("SQL: "+ sql);
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
			rsHeader = stmt.executeQuery(headerSQLString);
			rsmdHeader = rsHeader.getMetaData();
			rsData = stmt.executeQuery(dataSQLString);
			rsmdData = rsData.getMetaData();
		} catch (ClassNotFoundException e)
		{
			System.out.println(e.getMessage());
			// log(Level.SEVERE,e.getMessage());
		} catch (SQLException e)
		{
			System.out.println(e.getMessage());
			// log(Level.SEVERE,e.getMessage());
		}

	}
	public void putMessage(String str)
	{
		if (message == null)
			message = new Vector<String>();
		message.addElement(str);

	}
	private void readData(BufferedReader buf, String fileName, String pat,
			String key, boolean ital, String block, char sensor)
	{
		try
		{
			log(Level.INFO, "Thread " + currentThread().getName()
					+ " processing... " + fileName);

			String line;
			// log(Level.INFO,"Processing... "+ fileName + " header");
			long sample = findStartPosition(buf, fileName );

			while ((line = buf.readLine()) != null)
			{
				line = line.trim();
				if (line.charAt(0) != '#' && !line.equals(""))
					writeBody(fileName, pat, key, ital, block, sensor, sample,
							line);
				System.out.println(currentThread().getName()
						+ " has just written sample " + sample + " for "
						+ fileName + " value " + line);
				sample++;

			}
			log(Level.INFO, sample + " lines processed for " + fileName);
		} catch (IOException e)
		{
			log(Level.SEVERE, e.getMessage());
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
		processFile();
		super.run();
	}
	private void saveProps()
	{
		try
		{
			this.prop.store(new FileOutputStream(FDM_IMPORT_PROPERTIES),
					"FDM Data Input");
		} catch (FileNotFoundException e)
		{
			log(Level.SEVERE, e.getMessage());
		} catch (IOException e)
		{

			log(Level.SEVERE, e.getMessage());
		}
	}
	private void loadProps()
	{
		try
		{
			this.prop.load(new FileInputStream(FDM_IMPORT_PROPERTIES));
		} catch (FileNotFoundException e)
		{

			log(Level.SEVERE, e.getMessage());
		} catch (IOException e)
		{
			log(Level.SEVERE, e.getMessage());
		}
	}

	private void writeBody(String fileName, String pat, String key,
			boolean ital, String block, char sensor, long sample, String value)
	{

		String sql = "INSERT INTO FDMData ( filename, [key], pat, ital, block, sensor, sample, sensorvalue) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)";
		// String values[] = line.trim().split(" ");
		// if (values.length > 3)
		// {
		// String trial = values[0];
		// String sensor = values[1];
		// String sample = values[2];
		// String value = values[3];

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
			sql = sql.replaceFirst("\\?", "'" + key + "'");
			sql = sql.replaceFirst("\\?", "'" + pat + "'");
			sql = sql.replaceFirst("\\?", (ital ? "1" : "0"));
			sql = sql.replaceFirst("\\?", "'" + block + "'");
			sql = sql.replaceFirst("\\?",  Character.valueOf(sensor).toString());
			sql = sql.replaceFirst("\\?",  Long.valueOf(sample).toString());
			sql = sql.replaceFirst("\\?", value);
			logSQL(sql);
			stmt.setQueryTimeout(0);
			stmt.execute(sql);
			stmt.close();
			// con.close();
		} catch (SQLException e)
		{
			log(Level.SEVERE, "Current Thread " + currentThread().getName()
					+ LINESEPARATOR + e.getMessage() + " " + sql);
//			e.printStackTrace();
			// logSQL("*** Failed Current Thread "+
			// currentThread().getName()+LINESEPARATOR + e.getMessage() + " " +
			// sql);

		} catch (ClassNotFoundException e)
		{
			log(Level.SEVERE, e.getMessage());
		}
		//
		// } else
		// log(Level.SEVERE, "There's a problem with " + fileName + ":"
		// + line);
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
			String sql = "DELETE FROM FDMData WHERE id = '" + key + "'";
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
				log(Level.WARNING, e.getMessage() + " " + sql);
			} catch (ClassNotFoundException e)
			{
				log(Level.WARNING, e.getMessage());
			}

		}
//		super.finalize();
	}

	}
}