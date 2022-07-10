/*
 * Created on 06-Oct-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mltestbed.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mltestbed.heuristics.PSO.BaseSwarm;
import org.mltestbed.testFunctions.HeirarchyTestBase;
import org.mltestbed.testFunctions.TestBase;
import org.mltestbed.util.Log;
import org.mltestbed.util.MemoryBufferedFile;
import org.mltestbed.util.Particle;
import org.mltestbed.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author Ian Kenny
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class OutputResults extends Thread implements Cloneable
{
	protected class ParticleResult
	{
		private HashMap<Particle, MemoryBufferedFile> funcSpecific;
		/**
		 * 
		 */
		private Particle gbest;
		private long iteration;
		private long run;
		private ArrayList<Particle> swarm;
		private long swarmNo;

		protected ParticleResult(long run, long iteration, Particle gbest,
				long swarmNo, ArrayList<Particle> swarm)
		{
			this.run = run;
			this.iteration = iteration;
			this.gbest = gbest;
			this.swarmNo = swarmNo;
			this.swarm = new ArrayList<Particle>(swarm);
			// This is needed when logging results because the file buffers are
			// reused
			this.funcSpecific = new HashMap<Particle, MemoryBufferedFile>();

			for (Iterator<Particle> iterator = swarm.iterator(); iterator
					.hasNext();)
			{
				Particle particle = (Particle) iterator.next();
				MemoryBufferedFile file = new MemoryBufferedFile(
						particle.retrieveBuffer());
				this.funcSpecific.put(particle, file);
			}
		}
		/**
		 * 
		 */
		private void destroy()
		{
			if (funcSpecific != null)
			{
				Collection<MemoryBufferedFile> keys = funcSpecific.values();
				for (Iterator<MemoryBufferedFile> iterator = keys
						.iterator(); iterator.hasNext();)
				{
					MemoryBufferedFile file = (MemoryBufferedFile) iterator
							.next();
					if (file != null)
					{
						file.destroy();
						file = null;
					}
				}
				funcSpecific.clear();
				funcSpecific = null;
			}
		}
		@Override
		protected void finalize() throws Throwable
		{
			destroy();
			// super.finalize();
		}

		/**
		 * @return the funcSpecific
		 */
		public HashMap<Particle, MemoryBufferedFile> getFuncSpecific()
		{
			return funcSpecific;
		}
		public BufferedReader getFuncSpecificByParticle(Particle particle)
		{
			BufferedReader br = null;
			br = new BufferedReader(
					new StringReader(funcSpecific.get(particle).read()));
			return br;
		}
		/**
		 * @return the gbest
		 */
		public Particle getGbest()
		{
			return gbest;
		}

		/**
		 * @return the iteration
		 */
		public long getIteration()
		{
			return iteration;
		}

		/**
		 * @return the run
		 */
		public long getRun()
		{
			return run;
		}

		/**
		 * @return the swarm
		 */
		public ArrayList<Particle> getSwarm()
		{
			return swarm;
		}

		public long getSwarmNo()
		{
			return swarmNo;
		}
	}
	private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";// "yyyy-MM-dd
	private static LinkedBlockingQueue<ParticleResult> firstResultsBuffer = new LinkedBlockingQueue<ParticleResult>();
	// HH:mm:ss:SSS";
	private static final String INSERT_INTO_NOTEBOOK = "INSERT INTO notebook ( expNum, Description, Parameters, Notes, StartTime ) VALUES ( ?, ?, ?, ?, ? )";
	private static final String INSERT_INTO_RESULTS = "INSERT INTO results(expnum,runnum,iteration,swarm,particle,bestscore,currentscore,isbest,position, bestposition, velocity, expspecific) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String INSERT_INTO_SWARMS = "INSERT INTO swarms(expnum,runnum,swarmno,params) VALUES(?,?,?,?)";

	private static final String LOGGING_RESULTS = "Logging Results, buffered SQL statements: ";
	private static final String OUTPUT_RESULTS = "OutputResults";
	private static final String RESULT_STORE = "Results";
	private static final String RUNTIME_PROPERTIES = "MLTestBedRuntime.properties";
	private static LinkedBlockingQueue<ParticleResult> secondResultsBuffer = new LinkedBlockingQueue<ParticleResult>();
	private static final String SELECT_RESULTS = "SELECT  expnum,runnum,iteration,swarm,particle,bestscore,currentscore,isbest,position, bestposition, velocity, expspecific FROM results";
	/**
	 * @return the datetimeFormat
	 */
	public static String getDatetimeFormat()
	{
		return DATETIME_FORMAT;
	}
	/**
	 * @return the insertIntoNotebook
	 */
	public static String getInsertIntoNotebook()
	{
		return INSERT_INTO_NOTEBOOK;
	}
	/**
	 * @return the insertIntoResults
	 */
	public static String getInsertIntoResults()
	{
		return INSERT_INTO_RESULTS;
	}
	/**
	 * @return the insertIntoSwarms
	 */
	public static String getInsertIntoSwarms()
	{
		return INSERT_INTO_SWARMS;
	}
	/**
	 * @return the loggingResults
	 */
	public static String getLoggingResults()
	{
		return LOGGING_RESULTS;
	}
	/**
	 * @return the outputResults
	 */
	public static String getOutputResults()
	{
		return OUTPUT_RESULTS;
	}
	/**
	 * @return the resultStore
	 */
	public static String getResultStore()
	{
		return RESULT_STORE;
	}
	/**
	 * @return the runtimeProperties
	 */
	public static String getRuntimeProperties()
	{
		return RUNTIME_PROPERTIES;
	}
	/**
	 * @return the selectResults
	 */
	public static String getSelectResults()
	{
		return SELECT_RESULTS;
	}
	public static Document readXmlFile(String filename)
	{
		Document doc = null;
		try
		{

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new File(filename));
		} catch (ParserConfigurationException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (SAXException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (IOException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

		return doc;
	}
	public static void retrieveLoggedSQL(String dir, String strCon,
			String element)
	{
		// TODO
	}
	// This method writes a DOM document to a file
	public static void writeXmlFile(Document doc, String filename)
	{
		try
		{
			// Prepare the DOM document for writing
			Source source = new DOMSource(doc);

			// Prepare the output file
			File file = new File(filename);
			Result result = new StreamResult(file);

			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance()
					.newTransformer();
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e)
		{
			Log.log(Level.SEVERE, e);
		} catch (TransformerException e)
		{
			Log.log(Level.SEVERE, e);
		}
	}

	private Connection con = null;
	private String connectString = "MLTestBedExperiments";
	private DateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
	private Document doc = null;
	private String driver;
	private Experiment exp;
	private long expNum = Long.MIN_VALUE;
	private File fileBuffer;
	private FileOutputStream fos;
	private Element HTMLRoot;
	private boolean log = false;
	private boolean logAll = true;
	private final String maxSQLString = "select Max(ExpNum) from notebook";
	private boolean offered;
	private ObjectOutputStream out;
	private boolean processSQL = false;
	private Properties prop;
	private String pwd = null;
	private boolean queue;
	private int resnum = 0;
	private final String resultSQL = "select * from results";
	private Node resultTableNode;
	private ResultSet rs;
	private ResultSetMetaData rsmd;
	private final String SQLString = "select * from notebook";
	private BaseSwarm swarm = null;
	private TestBase testFunc = null;

	private String uid = null;
	private String url = "jdbc:odbc:";

	/**
	 * 
	 */
	public OutputResults(Experiment exp)
	{
		super(OUTPUT_RESULTS);
		this.exp = exp;
		prop = new Properties();
		try
		{
			swarm = this.exp.getSwarm();
			prop.putAll(swarm.getRunParams());
			// if (swarm == null)
			// throw new Exception("Swarm == null");
			newRun(exp.getTextDescription(), exp.getXMLParameters());
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

	}
	public OutputResults(Experiment exp, Properties runparams)
	{
		super(OUTPUT_RESULTS);
		this.exp = exp;
		prop = runparams;
		try
		{
			newRun(exp.getTextDescription(), exp.getXMLParameters());
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

	}
	protected OutputResults(OutputResults o)
	{
		super();
		this.con = o.con;
		this.connectString = o.connectString;
		this.df = o.df;
		this.doc = o.doc;
		this.driver = o.driver;
		this.exp = o.exp;
		this.expNum = o.expNum;
		this.fileBuffer = o.fileBuffer;
//		OutputResults.firstResultsBuffer = new LinkedBlockingQueue<OutputResults.ParticleResult>(
//				o.firstResultsBuffer);
		this.fos = o.fos;
		this.HTMLRoot = o.HTMLRoot;
		this.log = o.log;
		this.logAll = o.logAll;
		this.out = o.out;
		this.processSQL = o.processSQL;
		this.prop = new Properties(o.prop);
		this.pwd = o.pwd;
		this.queue = o.queue;
		this.resnum = o.resnum;
		this.resultTableNode = o.resultTableNode;
		this.rs = o.rs;
		this.rsmd = o.rsmd;
//		OutputResults.secondResultsBuffer = new LinkedBlockingQueue<OutputResults.ParticleResult>(
//				o.secondResultsBuffer);
		this.swarm = o.swarm;
		this.testFunc = o.testFunc;
		this.uid = o.uid;
		this.url = o.url;
	}

	public OutputResults(Properties runparams)
	{
		super(OUTPUT_RESULTS);
		initProps();
		prop.putAll(runparams);
		createDoc();
	}
	@SuppressWarnings("unused")
	private Node addChild(Node node, Node child)
	{
		Node ret = null;
		if (node != null)
		{
			ret = node.appendChild(child);
		}
		return ret;
	}
	private Node addNode(Node node, String str)
	{
		Node ret = null;
		if (node != null)
		{
			ret = node.appendChild(doc.createElement(str));
		}
		return ret;
	}
	private Node addNodeWithText(Node node, String str, String text)
	{
		Node ret = null;
		if (node != null)
		{
			ret = node.appendChild(doc.createElement(str)
					.appendChild(doc.createTextNode(text)));
		}
		return ret;
	}
	/**
	* 
	*/
	public void clearQueue()
	{
		processResults();
	}

	public Object clone()
	{
		OutputResults clone = null;
		try
		{
			clone = new OutputResults(this);
		} catch (Exception e)
		{
			// This should never happen
			throw new InternalError(e.toString());
		}
		return clone;
	}

	/**
	 * 
	 */
	private void createDoc()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try
		{
			builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
			HTMLRoot = doc.createElement("HTML");
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		addNodeWithText(doc, "H1", testFunc.getDescription());
		addNodeWithText(doc, "H3", testFunc.getInfo());
		addNode(doc, "HR");
		addNodeWithText(doc, "H2", "results");
		resultTableNode = addNode(doc, "Table");
	}
	private void createNewRec(String text, String xml)
	{
		Statement stmt;
		try
		{
			con = getConnection();
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(maxSQLString);
			// rsmd = rs.getMetaData();
			rs.first();
			expNum = 1 + rs.getLong(1);
			String sql = "INSERT INTO notebook (ExpNum,description,parameters,notes,starttime) VALUES(";
			sql = sql + Long.toString(expNum) + ",";
			sql = sql + "'" + text + "',";
			sql = sql + "'" + xml + "',";
			sql = sql + "'" + exp.getNotes().replaceAll("\'", "\'\'") + "','"
					+ df.format(new Date()) + "')";
			stmt.execute(sql);
			stmt.close();
			if (exp.getSwarm().isHeirarchy())
				writeSwarmsData(exp.getSwarm().getRun());
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

	}
	/**
	 * @return the con
	 */
	public Connection getCon()
	{
		return con;
	}
	private Connection getConnection() throws SQLException
	{
		if (con == null || con.isClosed())
		{
			con = getNewConnection();
		}
		return con;
	}
	/**
	 * @return the connectString
	 */
	public String getConnectString()
	{
		return connectString;
	}
	/**
	 * @return the df
	 */
	public DateFormat getDf()
	{
		return df;
	}
	/**
	 * @return the doc
	 */
	public Document getDoc()
	{
		return doc;
	}
	/**
	 * @return the driver
	 */
	public String getDriver()
	{
		return driver;
	}
	/**
	 * @return the exp
	 */
	public Experiment getExp()
	{
		return exp;
	}
	/**
	 * @return the expNum
	 */
	public long getExpNum()
	{
		return expNum;
	}
	/**
	 * @return the fileBuffer
	 */
	public File getFileBuffer()
	{
		return fileBuffer;
	}
	/**
	 * @return the firstResultsBuffer
	 */
	public LinkedBlockingQueue<ParticleResult> getFirstResultsBuffer()
	{
		return firstResultsBuffer;
	}
	/**
	 * @return the fos
	 */
	public FileOutputStream getFos()
	{
		return fos;
	}
	/**
	 * @return the hTMLRoot
	 */
	public Element getHTMLRoot()
	{
		return HTMLRoot;
	}
	/**
	 * @return the maxSQLString
	 */
	public String getMaxSQLString()
	{
		return maxSQLString;
	}
	private Connection getNewConnection()
	{
		Connection connect = null;
		int tries = 5;
		do
		{
			try
			{
				Class.forName(driver);
				DriverManager.setLoginTimeout(0);
				if (uid.isEmpty())
					connect = DriverManager.getConnection(url + connectString);
				else
					connect = DriverManager.getConnection(url + connectString,
							uid, pwd);
				connect.setAutoCommit(true);
			} catch (ClassNotFoundException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			} catch (SQLException e)
			{
				Log.log(Level.INFO, new Exception(
						"Attempt to connect failed, retrying connection " + url
								+ connectString,
						e));
				e.printStackTrace();
				try
				{
					connect = null;
					sleep(3000 - (tries * 500));
				} catch (InterruptedException e1)
				{
					Log.log(Level.SEVERE, e);
					e1.printStackTrace();
				}
			}
			tries--;
		} while (connect == null && tries != 0);
		return connect;
	}
	private void getNewExpNum()
	{
		ResultSet rs;
		String sql = "Select Max(expNum) From notebook";

		Statement stmt;
		try
		{
			con = getConnection();
			stmt = con.createStatement();

			rs = stmt.executeQuery(sql);
			rs.first();
			expNum = rs.getLong(1) + 1;
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}

	}
	/**
	 * @return the out
	 */
	public ObjectOutputStream getOut()
	{
		return out;
	}
	/**
	 * @return Returns the params.
	 */
	public Properties getParams()
	{
		return prop;
	}
	/**
	 * @return the prop
	 */
	public Properties getProp()
	{
		return prop;
	}
	/**
	 * @return the pwd
	 */
	public String getPwd()
	{
		return pwd;
	}
	/**
	 * @return the resnum
	 */
	public int getResnum()
	{
		return resnum;
	}
	/**
	 * @return the resultSQL
	 */
	public String getResultSQL()
	{
		return resultSQL;
	}
	/**
	 * @return the resultTableNode
	 */
	public Node getResultTableNode()
	{
		return resultTableNode;
	}
	/**
	 * @return the rs
	 */
	public ResultSet getRs()
	{
		return rs;
	}
	/**
	 * @return the rsmd
	 */
	public ResultSetMetaData getRsmd()
	{
		return rsmd;
	}
	/**
	 * @return the secondResultsBuffer
	 */
	public LinkedBlockingQueue<ParticleResult> getSecondResultsBuffer()
	{
		return secondResultsBuffer;
	}
	/**
	 * @return the sQLString
	 */
	public String getSQLString()
	{
		return SQLString;
	}
	/**
	 * @return Returns the storeResults.
	 */
	public LinkedBlockingQueue<ParticleResult> getStoreResults()
	{
		return secondResultsBuffer;
	}
	public BaseSwarm getSwarm()
	{
		return swarm;
	}
	public ArrayList<String> getTableHeads()
	{
		ArrayList<String> list = new ArrayList<String>();
		try
		{
			con = getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(resultSQL);

			ResultSetMetaData rsmd = rs.getMetaData();
			int columns = rsmd.getColumnCount();
			for (int i = 0; i < columns; i++)
				list.add(rsmd.getColumnName(i));
			stmt.close();
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		return list;
	}
	/**
	 * @return Returns the testFunc.
	 */
	public TestBase getTestFunc()
	{
		return testFunc;
	}
	/**
	 * @return the uid
	 */
	public String getUid()
	{
		return uid;
	}
	/**
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}
	protected void initProps()
	{
		connectString = prop.getProperty("connection", connectString);
		uid = prop.getProperty("userid");
		pwd = prop.getProperty("password");
		driver = prop.getProperty("driver", "sun.jdbc.odbc.JdbcOdbcDriver");
		url = prop.getProperty("url", url);
		logAll = (prop.getProperty("logAll", Boolean.toString(logAll))
				.equalsIgnoreCase("true")) ? true : false;

	}
	/**
	 * @return the log
	 */
	public boolean isLog()
	{
		return log;
	}
	/**
	 * @return the logAll
	 */
	public boolean isLogAll()
	{
		return logAll;
	}
	/**
	 * @return the processSQL
	 */
	public boolean isProcessSQL()
	{
		return processSQL;
	}
	/**
	 * @return the queue
	 */
	public boolean isQueue()
	{
		return queue;
	}
	public void newRun(String text, String xml)
	{
		if (log)
		{
			try
			{
				// fileBuffer = File.Util.createTempFile("Data", null);
				// fos = new FileOutputStream(fileBuffer, true);
				// out = new ObjectOutputStream(fos);

				prop.load(new FileInputStream(RUNTIME_PROPERTIES));
			} catch (FileNotFoundException e)
			{
				Log.getLogger().info(e.getMessage());
				// e.printStackTrace();
			} catch (IOException e)
			{
				Log.log(Level.SEVERE, e);
				// e.printStackTrace();
			}
		}
		initProps();
		if (expNum == Long.MIN_VALUE)
			createNewRec(text, xml);
	}
	@SuppressWarnings("unused")
	private Document openDoc(File file)
	{
		Document doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try
		{
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			doc = builder.parse(file);
			doc.createElement("HTML");
		} catch (SAXException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return doc;
	}
	public Result outputToHTML()
	{
		Result result = null;

		TransformerFactory tFactory = TransformerFactory.newInstance();
		try
		{
			Transformer trans = tFactory.newTransformer();
			Source xmlSource = new DOMSource(doc);
			trans.transform(xmlSource, result);
		} catch (TransformerConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}
	public void processResults()
	{
		ParticleResult result = null;
		int no = secondResultsBuffer.size();
		Util.getSwarmui().updateLog(
				"storing " + (no != 0 ? no : "") + " queued results");
		while (!secondResultsBuffer.isEmpty())
		{
			try
			{
				result = (ParticleResult) secondResultsBuffer.poll();
				if (result != null)
				{
					// writeResultset(result);
					if (writePreparedStatement(result))
					{
						result.destroy();
						result = null;
					} else
						// something went wrong requeue result
						queueResult(result);
				}
			} catch (Exception e)
			{
				Log.log(Level.SEVERE, e);
				// e.printStackTrace();
			}
		}
		offered = false;
	}
	public void queueResult(ParticleResult result)
	{
		secondResultsBuffer.offer(result);
		offered = true;
		try
		{
			synchronized (secondResultsBuffer)
			{
				secondResultsBuffer.notifyAll();
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		Util.getSwarmui()
				.updateLog(secondResultsBuffer.size() + " results queued");
	}
	public void run()
	{
		if (log)
		{
			queue = true;
			while (queue)
			{

				try
				{
					synchronized (secondResultsBuffer)
					{
						while(!offered && queue)
							secondResultsBuffer.wait(10000);
					}
				} catch (Exception e)
				{
					Log.log(Level.SEVERE, e);
					// e.printStackTrace();
				}
				try
				{
						new Thread()
						{
							@Override
							public void run()
							{
								processResults();
								super.run();
							}

						}.start();
				} catch (Exception e)
				{
					Log.log(Level.SEVERE, e);
					// e.printStackTrace();
				}
			}
		}

		super.run();
	}
	/**
	 * @param con
	 *            the con to set
	 */
	public void setCon(Connection con)
	{
		this.con = con;
	}
	/**
	 * @param connectString
	 *            the connectString to set
	 */
	public void setConnectString(String connectString)
	{
		this.connectString = connectString;
	}
	/**
	 * @param df
	 *            the df to set
	 */
	public void setDf(DateFormat df)
	{
		this.df = df;
	}
	/**
	 * @param doc
	 *            the doc to set
	 */
	public void setDoc(Document doc)
	{
		this.doc = doc;
	}
	/**
	 * @param driver
	 *            the driver to set
	 */
	public void setDriver(String driver)
	{
		this.driver = driver;
	}

	public void setEndTime()
	{
		String sql = "UPDATE notebook SET Endtime = '" + df.format(new Date())
				+ "' WHERE expnum = " + expNum;
		storeData(sql);
	}
	/**
	 * @param exp
	 *            the exp to set
	 */
	public void setExp(Experiment exp)
	{
		this.exp = exp;
	}
	/**
	 * @param expNum
	 *            the expNum to set
	 */
	public void setExpNum(long expNum)
	{
		this.expNum = expNum;
	}
	/**
	 * @param fileBuffer
	 *            the fileBuffer to set
	 */
	public void setFileBuffer(File fileBuffer)
	{
		this.fileBuffer = fileBuffer;
	}
	/**
	 * @param firstResultsBuffer
	 *            the firstResultsBuffer to set
	 */
	public void setFirstResultsBuffer(
			LinkedBlockingQueue<ParticleResult> firstResultsBuffer)
	{
		OutputResults.firstResultsBuffer = firstResultsBuffer;
	}
	/**
	 * @param fos
	 *            the fos to set
	 */
	public void setFos(FileOutputStream fos)
	{
		this.fos = fos;
	}
	/**
	 * @param hTMLRoot
	 *            the hTMLRoot to set
	 */
	public void setHTMLRoot(Element hTMLRoot)
	{
		HTMLRoot = hTMLRoot;
	}
	/**
	 * @param log
	 *            the log to set
	 */
	public void setLog(boolean log)
	{
		this.log = queue = log;
	}
	/**
	 * @param logAll
	 *            the logAll to set
	 */
	public void setLogAll(boolean logAll)
	{
		this.logAll = logAll;
	}
	/**
	 * @param out
	 *            the out to set
	 */
	public void setOut(ObjectOutputStream out)
	{
		this.out = out;
	}
	/**
	 * @param params
	 *            The params to set.
	 */
	public void setParams(Properties params)
	{
		prop = params;
	}
	/**
	 * @param processSQL
	 *            the processSQL to set
	 */
	public void setProcessSQL(boolean processSQL)
	{
		this.processSQL = processSQL;
	}
	/**
	 * @param prop
	 *            the prop to set
	 */
	public void setProp(Properties prop)
	{
		this.prop = prop;
	}
	/**
	 * @param pwd
	 *            The pwd to set.
	 */
	public void setPwd(String pwd)
	{
		this.pwd = pwd;
	}
	/**
	 * @param queue
	 *            the queue to set
	 */
	public void setQueue(boolean queue)
	{
		this.queue = queue;
	}
	/**
	 * @param resnum
	 *            the resnum to set
	 */
	public void setResnum(int resnum)
	{
		this.resnum = resnum;
	}
	/**
	 * @param resultTableNode
	 *            the resultTableNode to set
	 */
	public void setResultTableNode(Node resultTableNode)
	{
		this.resultTableNode = resultTableNode;
	}

	/**
	 * @param rs
	 *            the rs to set
	 */
	public void setRs(ResultSet rs)
	{
		this.rs = rs;
	}
	/**
	 * @param rsmd
	 *            the rsmd to set
	 */
	public void setRsmd(ResultSetMetaData rsmd)
	{
		this.rsmd = rsmd;
	}

	/**
	 * @param secondResultsBuffer
	 *            the secondResultsBuffer to set
	 */
	public void setSecondResultsBuffer(
			LinkedBlockingQueue<ParticleResult> secondResultsBuffer)
	{
		OutputResults.secondResultsBuffer = secondResultsBuffer;
	}
	/**
	 * @param swarm
	 *            the swarm to set
	 */
	public void setSwarm(BaseSwarm swarm)
	{
		this.swarm = swarm;
	}

	/**
	 * @param testFunc
	 *            The testFunc to set.
	 */
	public void setTestFunc(TestBase testFunc)
	{
		this.testFunc = testFunc;
	}
	/**
	 * @param uid
	 *            The uid to set.
	 */
	public void setUid(String uid)
	{
		this.uid = uid;
	}
	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}
	public void stopQueue()
	{
		queue = false;
		synchronized (secondResultsBuffer)
		{
			secondResultsBuffer.notifyAll();
		}
		try
		{
			interrupt();
		} catch (Exception e)
		{

			// e.printStackTrace();
		}
		clearQueue();
	}
	private void storeData(String sql)
	{
		try
		{
			Connection con;
			con = getNewConnection();
			Statement stmt = con.createStatement();
			// System.out.println(sql);
			try
			{
				stmt.executeLargeUpdate(sql);
			} catch (UnsupportedOperationException e)
			{
				stmt.executeUpdate(sql);
			}
			stmt.close();
			con.close();
			stmt = null;
			con = null;
		} catch (SQLException e)
		{
			String exstr = e.getMessage() + "\n" + sql;
			SQLException nextException = null;
			do
			{
				nextException = e.getNextException();
				if (nextException != null)
					exstr += "\n" + nextException.getMessage();
			} while (nextException != null);

			Log.log(Level.SEVERE, new Exception(exstr, e));
			con = null;
			// e.printStackTrace();
		}
	}

	/**
	 * @param run
	 * @param iter
	 */
	public void storeResultsDB(long run, long iter)
	{

		if (swarm.isMaster() && swarm.isHeirarchy())
		{
			Properties extraHeirInfo = ((HeirarchyTestBase) swarm
					.getTestFunction()).extraHeirInfo(null);
			if (extraHeirInfo != null)
				swarm.getParams().putAll(extraHeirInfo);
		}

		writeIterResultData(run, iter);

	}
	/**
	 * @param rs
	 * @param field
	 * @param value
	 */
	private void update(ResultSet rs, int field, String value)
	{
		ResultSetMetaData rsmd;
		try
		{
			rsmd = rs.getMetaData();

			int type = rsmd.getColumnType(field);
			switch (type)
			{
				case Types.BIT :
				case Types.BINARY :
					rs.updateBoolean(field, Boolean.parseBoolean(value));
					break;
				case Types.LONGVARCHAR :
				case Types.LONGNVARCHAR :
					// reader = new StringReader(value);
					// rs.updateClob(field, reader);
					// Clob clob = rs.getClob(field);
					// clob.setString(0, value);
					// rs.updateClob(field, clob);
					// break;
				case Types.VARCHAR :
				case Types.CHAR :
					rs.updateString(field, value);
					break;
				case Types.BIGINT :
					rs.updateLong(field, Long.parseLong(value));
					break;
				case Types.SMALLINT :
				case Types.INTEGER :
				case Types.TINYINT :
					rs.updateInt(field, Integer.parseInt(value));
					break;
				case Types.DECIMAL :
				case Types.REAL :
				case Types.FLOAT :
				case Types.DOUBLE :
					rs.updateDouble(field, Double.parseDouble(value));
					break;
				case Types.DATE :
					rs.updateDate(field, java.sql.Date.valueOf(value));
					break;
				case Types.TIMESTAMP :
					Date date = DateFormat.getInstance().parse(value);
					Timestamp ts = new Timestamp(date.getTime());
					rs.updateTimestamp(field, ts);
					break;
				default :
					Log.getLogger()
							.info("Don't know how to store "
									+ rsmd.getColumnName(field) + " of type "
									+ rsmd.getColumnType(field)
									+ "Metadata ruturns type index " + type);
					break;

			}
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void updateNotes(String notes)
	{
		String sql = SQLString.replaceFirst("\\*", "notes") + " WHERE expnum = "
				+ expNum;
		String update = "UPDATE notebook SET notes = ? WHERE expnum = "
				+ expNum;

		try
		{
			String buf = update.replaceFirst("\\?",
					"CONCAT(notes,'" + System.getProperty("line.separator")
							+ "***" + notes + "***')");
			Connection con;
			con = getNewConnection();
			Statement stmt = con.createStatement();
			stmt.execute(buf);
			stmt.close();
			con.close();
			stmt = null;
			con = null;
		} catch (SQLException e1)
		{
			// fail this silently for now
			// e1.printStackTrace();
			// Log.log(Level.SEVERE, new Exception(e1.getMessage() + update,
			// e1));
			try
			{
				Connection con = getNewConnection();
				Statement stmt = con.createStatement(
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_UPDATABLE);
				ResultSet rs = stmt.executeQuery(sql);
				rs.first();
				String buf = rs.getString(1);
				rs.updateString(1, buf + System.getProperty("line.separator")
						+ "***" + notes + "***");
				rs.updateRow();
				rs.close();
				stmt.close();
				con.close();
			} catch (SQLException e2)
			{
				Log.log(Level.SEVERE, new Exception(e1.getMessage() + sql, e2));
			}
		}
	}
	/**
	 * @param description
	 * @param parameters
	 * @param notes
	 */
	public void writeExperimentData(String description, String parameters,
			String notes)
	{

		String sql = INSERT_INTO_NOTEBOOK;

		getNewExpNum();
		sql = sql.replaceFirst("\\?", String.valueOf(expNum));
		sql = sql.replaceFirst("\\?", "'" + description + "'");
		sql = sql.replaceFirst("\\?", "'" + parameters + "'");
		sql = sql.replaceFirst("\\?", "'" + notes + "'");
		storeData(sql);
	}

	public void writeIterResultData(long run, long iter)
	{
		try
		{
			long swarmNo = swarm.getSwarmNo();
			Particle bestParticle = swarm.getGBest();
			ParticleResult result = new ParticleResult(run, iter, bestParticle,
					swarmNo, swarm.getSwarmMembers());
			if (queue)
				queueResult(result);
			else
			{
				// writeResultset(result);
				writePreparedStatement(result);
				result.destroy();
				result = null;
			}

		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}
	}
	public void writeIterResultData(ParticleResult result)
	{

		double bestValue = Double.NaN;

		String sql = INSERT_INTO_RESULTS;
		long swarmNo = result.getSwarmNo();
		Particle bestParticle = result.getGbest();
		bestValue = bestParticle.getBestScore();
		sql = sql.replaceFirst("\\?", String.valueOf(expNum));
		sql = sql.replaceFirst("\\?", String.valueOf(result.getRun()));
		sql = sql.replaceFirst("\\?", String.valueOf(result.getIteration()));
		sql = sql.replaceFirst("\\?", String.valueOf(swarmNo));
		sql = sql.replaceFirst("\\?", "-1");
		sql = sql.replaceFirst("\\?",
				String.valueOf((Double) bestParticle.getBestScore()));
		sql = sql.replaceFirst("\\?",
				String.valueOf((Double) bestParticle.getCurrentScore()));
		sql = sql.replaceFirst("\\?",
				(bestValue == bestParticle.getBestScore()) ? "1" : "0");
		sql = sql.replaceFirst("\\?",
				"'" + bestParticle.getPosition().toString() + "'");
		sql = sql.replaceFirst("\\?",
				"'" + bestParticle.getPbest().toString() + "'");
		sql = sql.replaceFirst("\\?",
				"'" + bestParticle.getVelocity().toString() + "'");
		sql = sql.replaceFirst("\\?",
				"'" + bestParticle.getFuncSpecific().toString() + "'");
		storeData(sql);
		ArrayList<Particle> particles = result.getSwarm();
		for (int key = 0; key < particles.size(); key++)
		{
			Particle particle = particles.get(key);
			boolean isBest = bestValue == particle.getBestScore();
			if (isBest || logAll)
			{
				sql = INSERT_INTO_RESULTS;
				sql = sql.replaceFirst("\\?", String.valueOf(expNum));
				sql = sql.replaceFirst("\\?", String.valueOf(result.getRun()));
				sql = sql.replaceFirst("\\?",
						String.valueOf(result.getIteration()));
				sql = sql.replaceFirst("\\?", String.valueOf(swarmNo));
				sql = sql.replaceFirst("\\?", String.valueOf(key));
				sql = sql.replaceFirst("\\?",
						String.valueOf((Double) particle.getBestScore()));
				sql = sql.replaceFirst("\\?",
						String.valueOf((Double) particle.getCurrentScore()));
				sql = sql.replaceFirst("\\?", isBest ? "1" : "0");
				sql = sql.replaceFirst("\\?",
						"'" + particle.getPosition().toString() + "'");
				sql = sql.replaceFirst("\\?",
						"'" + particle.getPbest().toString() + "'");
				sql = sql.replaceFirst("\\?",
						"'" + particle.getVelocity().toString() + "'");
				sql = sql.replaceFirst("\\?",
						"'" + particle.getFuncSpecific().toString() + "'");

				storeData(sql);
			}
		}
	}

	/**
	 * @param result
	 * @param swarmNo
	 * @param key
	 * @param particle
	 * @param isBest
	 * @return success
	 */
	private boolean writePreparedStatement(ParticleResult result)
	{
		boolean success = false;
		try
		{
			Connection connection = getNewConnection();
			if (connection != null)
			{
				PreparedStatement pstmt = connection
						.prepareStatement(INSERT_INTO_RESULTS);
				Particle bestParticle = result.getGbest();
				double bestValue = bestParticle.getBestScore();
				pstmt.setLong(1, expNum);
				pstmt.setLong(2, result.getRun());
				pstmt.setLong(3, result.getIteration());
				pstmt.setLong(4, result.getSwarmNo());
				pstmt.setLong(5, -1);
				pstmt.setDouble(6, bestParticle.getBestScore());
				pstmt.setDouble(7, bestParticle.getCurrentScore());
				pstmt.setBoolean(8, true);
				StringReader stringReader = new StringReader(
						bestParticle.getPosition().toString());
				pstmt.setClob(9, stringReader);
				stringReader.close();
				stringReader = new StringReader(
						bestParticle.getPbest().toString());
				pstmt.setClob(10, stringReader);
				stringReader.close();
				stringReader = new StringReader(
						bestParticle.getVelocity().toString());
				pstmt.setClob(11, stringReader);
				stringReader.close();
				BufferedReader funcSpecificByid = result
						.getFuncSpecificByParticle(bestParticle);
				pstmt.setClob(12, funcSpecificByid);
				pstmt.executeUpdate();
				pstmt.close();
				funcSpecificByid.close();
				ArrayList<Particle> particles = result.getSwarm();
				for (int key = 0; key < particles.size(); key++)
				{
					Particle particle = particles.get(key);
					boolean isBest = bestValue == particle.getBestScore();
					if (isBest || logAll)
					{
						pstmt = connection
								.prepareStatement(INSERT_INTO_RESULTS);
						pstmt.setLong(1, expNum);
						pstmt.setLong(2, result.getRun());
						pstmt.setLong(3, result.getIteration());
						pstmt.setLong(4, result.getSwarmNo());
						pstmt.setLong(5, key);
						pstmt.setDouble(6, particle.getBestScore());
						pstmt.setDouble(7, particle.getCurrentScore());
						pstmt.setBoolean(8, isBest);
						stringReader = new StringReader(
								particle.getPosition().toString());
						pstmt.setClob(9, stringReader);
						stringReader.close();
						stringReader = new StringReader(
								particle.getPbest().toString());
						pstmt.setClob(10, stringReader);
						stringReader.close();
						stringReader = new StringReader(
								particle.getVelocity().toString());
						pstmt.setClob(11, stringReader);
						stringReader.close();
						funcSpecificByid = result
								.getFuncSpecificByParticle(particle);
						pstmt.setClob(12, funcSpecificByid);
						funcSpecificByid.close();
						pstmt.executeUpdate();
						pstmt.close();
					}
				}
				connection.close();
				connection = null;
				success = true;
			}
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (IOException e)
		{
			// set success because data has been lost and we are unable to
			// recover
			success = true;
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}
		return success;
	}
	@SuppressWarnings("unused")
	private void writeResultset(ParticleResult result)
	{
		// This was originally used to handle connection
		// issues due to available resources
		try
		{
			Connection connection = getNewConnection();
			Statement stmt = connection.createStatement(
					ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			Particle bestParticle = result.getGbest();
			double bestValue = bestParticle.getBestScore();
			ResultSet rs = stmt.executeQuery(SELECT_RESULTS + " WHERE expnum ="
					+ expNum + " AND RunNum =" + result.getRun()
					+ " AND iteration =" + result.getIteration()
					+ " AND particle = -1");
			rs.moveToInsertRow();
			update(rs, 1, Long.toString(expNum));
			update(rs, 2, Long.toString(result.getRun()));
			update(rs, 3, Long.toString(result.getIteration()));
			update(rs, 4, Long.toString(result.getSwarmNo()));
			update(rs, 5, "-1");
			update(rs, 6, Double.toString(bestParticle.getBestScore()));
			update(rs, 7, Double.toString(bestParticle.getCurrentScore()));
			update(rs, 8, "true");
			update(rs, 9, bestParticle.getPosition().toString());
			update(rs, 10, bestParticle.getPbest().toString());
			update(rs, 11, bestParticle.getVelocity().toString());
			BufferedReader br = new BufferedReader(new StringReader(
					result.getFuncSpecific().get(bestParticle).read()));
			String buf = "";
			if (br != null)
			{
				try
				{

					String line;
					while ((line = br.readLine()) != null)
						buf += line;
					br.close();
				} catch (IOException e)
				{
					Log.log(Level.SEVERE, e);
					e.printStackTrace();
				}

			}
			update(rs, 12, buf);
			rs.insertRow();
			rs.close();
			rs = null;
			ArrayList<Particle> particles = result.getSwarm();
			for (int key = 0; key < particles.size(); key++)
			{
				Particle particle = particles.get(key);
				boolean isBest = bestValue == particle.getBestScore();
				if (isBest || logAll)
				{
					rs = stmt.executeQuery(SELECT_RESULTS + " WHERE expnum ="
							+ expNum + " AND RunNum =" + result.getRun()
							+ " AND iteration =" + result.getIteration()
							+ " AND particle =" + key);
					rs.moveToInsertRow();
					update(rs, 1, Long.toString(expNum));
					update(rs, 2, Long.toString(result.getRun()));
					update(rs, 3, Long.toString(result.getIteration()));
					update(rs, 4, Long.toString(result.getSwarmNo()));
					update(rs, 5, Long.toString(key));
					update(rs, 6, Double.toString(particle.getBestScore()));
					update(rs, 7, Double.toString(particle.getCurrentScore()));
					update(rs, 8, Boolean.toString(isBest));
					update(rs, 9, particle.getPosition().toString());
					update(rs, 10, particle.getPbest().toString());
					update(rs, 11, particle.getVelocity().toString());
					br = new BufferedReader(new StringReader(
							result.getFuncSpecific().get(particle).read()));
					buf = "";
					if (br != null)
					{
						try
						{
							String line;
							while ((line = br.readLine()) != null)
								buf += line;
							br.close();
						} catch (IOException e)
						{
							Log.log(Level.SEVERE, e);
							e.printStackTrace();
						}

					}
					update(rs, 12, buf);
					// rs.updateCharacterStream(12,
					// particle.getFuncSpecificReader());
					rs.insertRow();
					rs.close();
				}
			}
			stmt.getConnection().close();
			stmt.close();
			rs = null;
			stmt = null;
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}
	}
	/**
	 * @param run
	 */
	public void writeSwarmsData(long run)
	{

		try
		{
			long swarmNo = swarm.getSwarmNo();
			String sql = INSERT_INTO_SWARMS;
			sql = sql.replaceFirst("\\?", String.valueOf(expNum));
			sql = sql.replaceFirst("\\?", String.valueOf(run));
			sql = sql.replaceFirst("\\?", String.valueOf(swarmNo));
			// sql = sql.replaceFirst("\\?", "-1");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			swarm.getParams().storeToXML(out,
					DateFormat.getDateInstance().format(new Date()));
			sql = sql.replaceFirst("\\?", "'" + out.toString() + "'");
			storeData(sql);
		} catch (IOException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}

	}
}
