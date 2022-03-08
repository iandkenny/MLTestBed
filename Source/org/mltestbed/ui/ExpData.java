/**
 * 
 */
package org.mltestbed.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.mltestbed.data.Experiment;
import org.mltestbed.heuristics.PSO.BaseSwarm;
import org.mltestbed.heuristics.PSO.ClassicPSO;
import org.mltestbed.heuristics.PSO.Heirarchy.Heirarchy;
import org.mltestbed.testFunctions.HeirarchyTestBase;
import org.mltestbed.testFunctions.TestBase;
import org.mltestbed.topologies.Topology;
import org.mltestbed.util.Log;

/**
 * @author Ian Kenny
 * 
 */
public class ExpData
{
	private static final String NO_RESULTS_ARE_AVAILABLE = "No results are available";

	private final String SQLString = "select * from notebook";

	private DefaultMutableTreeNode topNode;

	private String connectString = "SwarmExperiments";

	private Properties runparams;

	private String driver;

	private String url = "jdbc:odbc:";

	private String uid;

	private String pwd;

	private JTree tree;

	/**
	 * @throws Exception
	 * 
	 */
	public ExpData(Properties prop, JTree tree) throws Exception
	{
		if (tree == null || topNode == null)
		{
			if (this.topNode == null)
				if (tree != null)
					this.topNode = (DefaultMutableTreeNode) tree.getModel()
							.getRoot();
				else
					this.topNode = new DefaultMutableTreeNode(
							NO_RESULTS_ARE_AVAILABLE);
			tree = new JTree(topNode);
		} else
			topNode.removeAllChildren();
		this.tree = tree;
		runparams = prop;
		if (runparams == null)
		{
			runparams = new Properties();
			throw new Exception("runparams cannot be null");
		}
		initProps();
	}
	public ExpData(Properties runparams)
	{
		this.runparams = runparams;
		initProps();
	}
	protected void initProps()
	{
		// SQLString = prop.getProperty("SQLString",SQLString);
		if (runparams == null)
			runparams = new Properties();
		connectString = runparams.getProperty("connection", connectString);
		uid = runparams.getProperty("userid", uid);
		pwd = runparams.getProperty("password", pwd);
		driver = runparams.getProperty("driver",
				"sun.jdbc.odbc.JdbcOdbcDriver");
		url = runparams.getProperty("url", url);

	}
	/**
	 * @return
	 */
	private Statement getStatement()
	{
		Statement stmt = null;
		try
		{
			Driver d = (Driver) Class.forName(driver).getDeclaredConstructor().newInstance();
			DriverManager.registerDriver(d);
			Connection con;
			if (!uid.equals(""))
				con = DriverManager.getConnection(url + connectString, uid,
						pwd);
			else
				con = DriverManager.getConnection(url + connectString);
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | SQLException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		return stmt;
	}

	/**
	 * 
	 */
	private JTree getExperiments()
	{
		try
		{
			if (topNode != null && tree != null)
			{
				Statement stmt = getStatement();

				if (stmt != null)
				{
					ResultSet rs = stmt.executeQuery(SQLString);
					if (rs != null)
					{
						topNode.removeAllChildren();
						tree.setRootVisible(true);
						while (rs.next())
						{
							addNode(rs.getLong(1), rs.getString(2));
						}
						expandAPath(tree.getPathForRow(0));
						if (topNode.getChildCount() != 0 && tree != null)
							tree.setRootVisible(false);
						
						tree.updateUI();
					}
				}
			}
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}

		return tree;
	}
	/**
	 * @param expno
	 * @param description
	 */
	private void addNode(long expno, String description)
	{

		DefaultMutableTreeNode node1 = new DefaultMutableTreeNode(
				"Experiment " + expno);
		topNode.add(node1);

		DefaultMutableTreeNode node2 = new DefaultMutableTreeNode(description);
		node1.add(node2);

	}
	/**
	 * Refreshes the experiment tree
	 */
	public JTree refresh()
	{
		return getExperiments();
	}
	/**
	 * @param expNo
	 */
	public Experiment load(Long expNo, long runs, boolean rerun)
	{
		Experiment newExp = null;
		try
		{
			Statement stmt = getStatement();
			String sqlWhere;
			if (expNo == null)
				sqlWhere = " where Expnum IN (Select Max(Expnum) From notebook)";
			else
				sqlWhere = " where Expnum = " + expNo.toString().trim();
			ResultSet rs = stmt.executeQuery(SQLString + sqlWhere);

			String buf;
			if (rs.first())
			{
				try
				{
					Clob clob = rs.getClob(3);
					buf = new String(clob.getSubString(1, (int) clob.length()));
				} catch (UnsupportedOperationException e)
				{
					buf = rs.getString(3);
					// e.printStackTrace();
				}
				// backwards compatibility 
				buf = new String(buf.replaceAll("com.swarmtestbed", "org.mltestbed"));
				//
				newExp = load(buf);
				if (newExp != null)
				{
					newExp.setRuns(runs);

					newExp.setNotes(
							"*** Automatic rerun of Experiment " + expNo);
				}
				rs.close();
				rs = null;

			}
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		return newExp;

	}
	public Experiment load(String xml)
	{
		BaseSwarm swarm = null;
		Topology neighbourhood = null;
		TestBase objective = null;
		Heirarchy heir = null;
		Experiment newExp = null;
		try
		{
			ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
			Properties params = new Properties();
			params.loadFromXML(in);

			runparams = loadRunProps(params, runparams);
			String buf = params.getProperty("swarmclass");
			if (buf != null && buf != "")
			{
				Class<?> class1 = (Class<?>) Class.forName(buf);

				swarm = (BaseSwarm) class1.getDeclaredConstructor().newInstance();
				swarm.setRunParams(runparams);
				swarm.load(params);

			}

			buf = params.getProperty("topologyclass");
			if (buf != null && buf != "")
			{

				Class<?> class1 = (Class<?>) Class.forName(buf);
				Constructor<?> constructor = (Constructor<?>) class1
						.getConstructor(new Class[]
				{BaseSwarm.class});
				neighbourhood = (Topology) constructor.newInstance(new Object[]
				{new ClassicPSO()});
				neighbourhood.load(params);
				swarm.setNeighbourhood(neighbourhood);
			}

			buf = params.getProperty("objectiveclass");
			if (buf != null && buf != "")
			{
				Class<?> class1 = (Class<?>) Class.forName(buf);

				objective = (TestBase) class1.getDeclaredConstructor().newInstance();
				objective.load(params);
				swarm.setTestFunction(objective);
			}

			buf = params.getProperty("heirarchyproperties");
			if (buf != null && buf != "")
			{
				buf = buf.substring(1, buf.length() - 2);
				Properties heirProp = new Properties();
				heirProp.loadFromXML(new ByteArrayInputStream(buf.getBytes()));
				buf = params.getProperty("heirarchyclass");
				BaseSwarm master = (BaseSwarm) Class.forName(buf).getDeclaredConstructor().newInstance();
				master.load(heirProp);
				buf = params.getProperty("heirarchyobjective");
				HeirarchyTestBase testFunct = (HeirarchyTestBase) Class
						.forName(buf).getDeclaredConstructor().newInstance();
				testFunct.load(params);
				master.setTestFunction(testFunct);
				boolean usefips = Boolean.parseBoolean(
						heirProp.getProperty("heirarchyusefips", "true"));
				if (usefips)
				{
					buf = heirProp.getProperty("heirarchytopology", "");
					if (buf != "")
					{
						Topology t = (Topology) Class.forName(buf)
								.getDeclaredConstructor().newInstance();
						master.setNeighbourhood(t);
					}
				} else
					master.setNeighbourhood(neighbourhood);
				heir = new Heirarchy(master, swarm, params, runparams);
			}

			newExp = new Experiment();
			newExp.setSwarm(swarm);
			newExp.setNeighbourhood(neighbourhood);
			newExp.setHeir(heir);
			newExp.setRunParams(runparams);
			newExp.setRuns(Long.parseLong(runparams.getProperty("runs", "1")));
			newExp.setNotes("*** Distributed run of Experiment ");

		} catch (ClassNotFoundException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		} catch (InstantiationException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		} catch (InvalidPropertiesFormatException e)
		{
			// Dont report this as the params field format has changed
			// Log.log(Level.SEVERE, new Exception(e.getMessage());
			// e.printStackTrace();
		} catch (IOException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		} catch (SecurityException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		return newExp;

	}
	private Properties loadRunProps(Properties props, Properties runparams)
	{
		Properties params = new Properties();

		if (props != null && runparams != null)
		{
			Enumeration<Object> keys = runparams.keys();
			while (keys.hasMoreElements())
			{
				String key = (String) keys.nextElement();
				if (props.containsKey(key))
					params.setProperty((String) key,
							props.getProperty((String) key));
			}
		}
		return params;
	}
	private void expandAPath(TreePath p)
	{

		tree.expandPath(p);
		if (p != null)
		{
			TreeNode currNode = (TreeNode) p.getLastPathComponent();
			int numChildren = currNode.getChildCount();
			for (int i = 0; i < numChildren; ++i)
			{

				TreePath newPath = p.pathByAddingChild(currNode.getChildAt(i));
				expandAPath(newPath);
			}
		}
	}

}
