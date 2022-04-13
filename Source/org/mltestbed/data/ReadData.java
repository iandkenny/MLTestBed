/*
 * Created on 30-Dec-2004
 *
 * TODO To change the template for this generated file go to
 *
 */
package org.mltestbed.data;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.ui.MLUI;
import org.mltestbed.util.Log;
import org.mltestbed.util.Util;

/**
 * @author Ian Kenny
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
abstract public class ReadData
{
	protected static Connection con = null;
	protected static Vector<String> keys = null;
	private static final int MAX_ROWS_RETRIEVED_PER_FETCH = 1000;
	public static boolean isNumeric(String str)
	{
		try
		{
			Double.parseDouble(str);
		} catch (NumberFormatException e)
		{
			return false;
		}
		return true;
	}
	private ArrayList<String> columnNames;
	protected String connectString = "";
	protected Vector<Vector<Object>> dataCollection = null;
	protected String driver = "";
	private ResultSet masterRS = null;
	protected Properties prop = new Properties();
	protected String pwd = "";
	protected String SQLString = "";

	protected Statement stmt = null;
	protected String uid = "";
	protected String url = "jdbc:";

	/**
	 * 
	 */
	public ReadData()
	{
		super();
		initProps();
		dataCollection = null;
		try
		{
			MLUI swarmui = Util.getSwarmui();
			if (swarmui != null)
			{
				prop.setProperty("driver",
						swarmui.getRunparams().getProperty("driver", driver));
				prop.setProperty("url",
						swarmui.getRunparams().getProperty("url", url));
				prop.setProperty("userid",
						swarmui.getRunparams().getProperty("userid", uid));
				prop.setProperty("password",
						swarmui.getRunparams().getProperty("password", pwd));
			}

		} catch (Exception e)
		{
			Log.getLogger().log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
		}
		keys = null;
	}
	public String buildSQL(HashMap<String, String> key)
	{
		String bufSQL = SQLString;
		String table = SQLString
				.substring(SQLString.toUpperCase().indexOf("FROM") + 5).trim();
		int indexOf = table.indexOf("] ");
		if (indexOf != -1)
			table = table.substring(0, indexOf + 1).trim();
		else
		{
			indexOf = table.indexOf(' ');
			if (indexOf != -1)
				table = table.substring(0, indexOf).trim();

		}
		// table = table.replace("[", "").replace("]", "");
		Set<String> keyset = key.keySet();
		for (Iterator<String> iterator = keyset.iterator(); iterator.hasNext();)
		{
			String currKey = (String) iterator.next();
			String currValue = key.get(currKey);
			if (!currValue.equals(""))
			{
				try
				{
					currKey = currKey.replace("[", "").replace("]", "");
					DatabaseMetaData meta = getLocalConnection().getMetaData();
					ResultSet rsColumns = meta.getColumns(null, null, table,
							currKey);
					String buf;
					if (rsColumns.next())
					{
						buf = rsColumns.getString("TYPE_NAME");
						if (buf.toUpperCase().indexOf("CHAR") != -1
								|| buf.toUpperCase().indexOf("DATETIME") != -1)
							buf = "'" + currValue + "'";
						else
							buf = currValue;
					} else if (isNumeric(currValue))
						buf = currValue;
					else
						buf = "'" + currValue + "'";
					if (bufSQL.toUpperCase().indexOf("ORDER") == -1)
						bufSQL += " AND " + currKey + " = " + buf;
					else
						bufSQL = bufSQL.replaceFirst("(?i)ORDER",
								" AND " + currKey + " = " + buf + " ORDER");
					rsColumns.close();
				} catch (SQLException e)
				{
					Log.getLogger().log(Level.SEVERE, e.getMessage(), e);
					e.printStackTrace();
				}
			}

		}

		bufSQL = bufSQL.replaceFirst(" AND ", " WHERE ");

		return bufSQL;
	}
	protected String buildSQL(String bufSQL, String key)
	{

		boolean startsWith = key.toUpperCase().startsWith("NOT");
		if (key.indexOf(",") != -1 || startsWith)
		{
			String buf = key;
			if (startsWith)
				buf = " NOT IN (" + buf.substring(3);
			else
				buf = " IN (" + buf;
			bufSQL = bufSQL.replace(key, buf + ") ").replaceFirst("=", "");
		}
		return bufSQL;
	}
	public void closeMainStatement()
	{
		try
		{
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		} catch (SQLException e)
		{
			Log.getLogger().log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
		}

	}

	public void closeStatement(ResultSet rs)
	{
		try
		{
			if (rs != null)
				rs.getStatement().close();
		} catch (SQLException e)
		{
			Log.getLogger().log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
		}

	}

	public ArrayList<String> columnNames(ResultSet rs)
	{

		try
		{
			if (rs != null)
			{
				columnNames = new ArrayList<String>();
				ResultSetMetaData metaData = rs.getMetaData();
				for (int column = 1; column <= metaData
						.getColumnCount(); column++)
				{

					columnNames.add(metaData.getColumnName(column));
				}
			} else
				columnNames = new ArrayList<String>();
		} catch (SQLException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		} catch (Exception e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		}
		return columnNames;
	}
	public void deleteSelected()
	{
		try
		{
			Statement stmt = getStatement();
			String sql = "DELETE FROM selectedKeys";
			logSQL(sql);
			stmt.execute(sql);
			stmt.close();

		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}

	}

	public void destroy()
	{
		try
		{
			closeMainStatement();
			if (masterRS != null)
			{
				closeStatement(masterRS);
				masterRS.close();
			}
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

	}
	@Override
	protected void finalize() throws Throwable
	{
		destroy();
		super.finalize();
	}
	/**
	 * @return the columnNames
	 */
	public ArrayList<String> getColumnNames()
	{
		return columnNames;
	}

	/**
	 * @return Returns the ConnectString.
	 */
	public String getConnectString()
	{
		return connectString;
	}
	public ArrayList<Vector<Object>> getData()
	{
		ArrayList<Vector<Object>> data = new ArrayList<Vector<Object>>();
		Vector<Object> currRow = new Vector<Object>();
		try
		{
			ResultSet rs = populateRS(SQLString);
			columnNames(rs);
			do
			{
				currRow = getRow(rs);
				data.add(currRow);
			} while (!rs.next());
			rs.close();
		} catch (SQLException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		} catch (Exception e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		}

		return data;
	}
	/**
	 * @param key
	 * @return the selected data
	 */
	public synchronized ArrayList<ArrayList<Object>> getData(boolean reset)
	{
		ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> row;
		String bufSQL = null;
		try
		{
			if (masterRS == null || reset)
			{
				if (masterRS != null)
					closeStatement(masterRS);
				HashMap<String, String> hashkeys = new HashMap<String, String>();
				for (String key : keys)
				{
					hashkeys.put(key.split("=")[0], key.split("=")[1]);
				}
				bufSQL = buildSQL(hashkeys);
				masterRS = populateRS(bufSQL);
				columnNames(masterRS);
			}

			if (masterRS.next())
			{
				int cols = masterRS.getMetaData().getColumnCount();
				int k = 0;
				do
				{
					row = new ArrayList<Object>();
					for (int i = 1; i <= cols; i++)
					{
						String s = masterRS.getString(i);
						double d = 0;
						if (isNumeric(s))
							d = masterRS.getDouble(i);
						else
							d = 0; // not ideal but we need some way to handle
									// missing values numerically
						row.add(d);
					}
					data.add(row);
				} while (masterRS.next() && k++ < MAX_ROWS_RETRIEVED_PER_FETCH);
			} else
				Log.log(Level.SEVERE, new Exception(
						"There is no data for this query " + bufSQL));
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (NullPointerException e)
		{
			Log.log(Level.SEVERE, e);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

		return data.isEmpty() ? null : data;
	}

	/**
	 * @param key
	 * @return the selected data
	 */
	public synchronized LinkedList<ArrayList<Double>> getData(
			HashMap<String, String> key)
	{
		LinkedList<ArrayList<Double>> data = new LinkedList<ArrayList<Double>>();
		ArrayList<Double> row;
		String bufSQL;
		try
		{
			if (key != null)
				bufSQL = buildSQL(key);
			else
				bufSQL = SQLString;
			ResultSet rs = populateRS(bufSQL);
			int cols = rs.getMetaData().getColumnCount();
			columnNames(rs);
			if (rs.next())
			{
				do
				{
					row = new ArrayList<Double>();
					for (int i = 1; i <= cols; i++)
					{
						String s = rs.getString(i);
						double d = 0;
						if (isNumeric(s))
							d = rs.getDouble(i);
						else
							d = 0; // not ideal but we need some way to handle
									// missing values numerically
						row.add(d);
					}
					data.add(row);
					// keys.add(rs.getString(0));
				} while (rs.next());
				rs.getStatement().close();
				rs = null;
			} else
				Log.log(Level.SEVERE, new Exception(
						"There is no data for this query " + bufSQL));
			// rsmd = rs.getMetaData();
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (NullPointerException e)
		{
			Log.log(Level.SEVERE, e);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

		return data;
	}
	/**
	 * @return Returns the dataCollection.
	 */
	public Vector<Vector<Object>> getDataCollection()
	{
		return dataCollection;
	}

	public int getDim()
	{
		String buf[] = SQLString
				.substring(0, SQLString.toUpperCase().indexOf("FROM"))
				.split(",");
		return buf.length - 1;
	}

	protected abstract Vector<String> getKeys();
	public Connection getLocalConnection()
	{
		Connection con = null;
		try
		{
			Class.forName(driver);
			con = DriverManager.getConnection(url + connectString, uid, pwd);
		} catch (ClassNotFoundException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		} catch (SQLException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		}
		return con;

	}
	private Connection getNewConnection()
	{
		Connection connect = null;
		int tries = 5;
		do
		{
			try
			{
				Driver d = (Driver) Class.forName(driver)
						.getDeclaredConstructor().newInstance();
				DriverManager.registerDriver(d);
				DriverManager.setLoginTimeout(0);
				if (uid.equals(""))
					connect = DriverManager.getConnection(url + connectString);
				else
					connect = DriverManager.getConnection(url + connectString,
							uid, pwd);
			} catch (ClassNotFoundException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			} catch (SQLException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
				try
				{
					connect = null;
					wait(500 - (tries * 100));
				} catch (InterruptedException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (InstantiationException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			} catch (IllegalAccessException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			} catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tries--;
		} while (connect == null && tries != 0);
		return connect;
	}
	public LinkedList<ArrayList<Double>> getNext()
	{
		LinkedList<ArrayList<Double>> data = new LinkedList<ArrayList<Double>>();
		ArrayList<Double> row;

		try
		{
			if (masterRS != null)
			{
				int cols = masterRS.getMetaData().getColumnCount();
				if (masterRS.next())
				{
					int k = 0;
					do
					{
						row = new ArrayList<Double>();
						for (int i = 1; i <= cols; i++)
						{
							String s = masterRS.getString(i);
							double d = 0;
							if (isNumeric(s))
								d = masterRS.getDouble(i);
							else
								d = 0; // not ideal but we need some way to
										// handle
										// missing values numerically
							row.add(d);
						}
						data.add(row);
					} while (masterRS.next()
							&& k++ < MAX_ROWS_RETRIEVED_PER_FETCH);
				} else
					Log.log(Level.SEVERE,
							new Exception("There is no data for this query "));
			}
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (NullPointerException e)
		{
			Log.log(Level.SEVERE, e);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

		return data.isEmpty() ? null : data;
	}
	public LinkedList<ArrayList<Double>> getNext(ResultSet rs)
	{
		LinkedList<ArrayList<Double>> data = new LinkedList<ArrayList<Double>>();
		ArrayList<Double> row;

		try
		{
			if (rs != null)
			{
				int cols = rs.getMetaData().getColumnCount();
				if (rs.next())
				{
					int k = 0;
					do
					{
						row = new ArrayList<Double>();
						for (int i = 1; i <= cols; i++)
						{
							String s = rs.getString(i);
							double d = 0;
							if (isNumeric(s))
								d = rs.getDouble(i);
							else
								d = 0; // not ideal but we need some way to
										// handle
										// missing values numerically
							row.add(d);
						}
						data.add(row);
					} while (rs.next() && k++ < MAX_ROWS_RETRIEVED_PER_FETCH);
				} else
					Log.log(Level.SEVERE,
							new Exception("There is no data for this query "));
			}
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (NullPointerException e)
		{
			Log.log(Level.SEVERE, e);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

		return data.isEmpty() ? null : data;
	}
	/**
	 * @return Returns the prop.
	 */
	public Properties getProp()
	{
		return prop;
	}

	public long getRecordCount()
	{
		long count = 0;
		try
		{
			Statement stmt = getStatement();
			String sql = "SELECT COUNT(*) " + SQLString
					.substring(SQLString.toUpperCase().indexOf("FROM"));
			logSQL(sql);
			ResultSet rs = stmt.executeQuery(sql);
			if (rs != null && rs.next())
			{
				count = rs.getLong(1);
				rs.close();
			}
			stmt.close();

		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		return count;
	}
	/**
	 * @param rs
	 * @return Vector containing org.mltestbed.data for the current row
	 */
	protected Vector<Object> getRow(ResultSet rs)
	{
		Vector<Object> currRow = new Vector<Object>();
		int numberOfColumns;
		if (rs != null)
			try
			{
				ResultSetMetaData rsmd = rs.getMetaData();
				numberOfColumns = rsmd.getColumnCount();
				for (int i = 0; i < numberOfColumns; i++)
				{
					int type = rsmd.getColumnType(i);
					switch (type)
					{
						case Types.BIT :
						case Types.BOOLEAN :
							currRow.add(Boolean.valueOf(rs.getBoolean(i)));
							break;
						case Types.CHAR :
						case Types.VARCHAR :
							currRow.add(new String(rs.getString(i)));
							break;
						case Types.INTEGER :
						case Types.BIGINT :
							currRow.add(Long.valueOf(((rs.getLong(i)))));
							break;
						case Types.DECIMAL :
						case Types.REAL :
						case Types.FLOAT :
						case Types.DOUBLE :
							currRow.add(Double.valueOf((rs.getDouble(i))));
							break;
						default :
							Log.log(Level.SEVERE, new Exception(
									"I don't know how to process type:"
											+ rsmd.getColumnTypeName(i)));

					}

				}
			} catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return currRow;
	}
	/**
	 * @return Returns the SQLString.
	 */
	public String getSQLString()
	{
		return SQLString;
	}
	protected Statement getStatement()
	{

		Statement stmt = null;
		Connection con = null;
		try
		{
			// connection busy errors require separate connection
			// if (con == null)
			con = getNewConnection();
			if (con != null)
				stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE);
			// stmt = con.createStatement();
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		return stmt;
	}
	/**
	 * @return Returns the url.
	 */
	public String getUrl()
	{
		return url;
	}
	protected void initProps()
	{
		if (prop == null)
			prop = new Properties();
		connectString = prop.getProperty("connection", connectString);
		uid = prop.getProperty("userid", uid);
		pwd = prop.getProperty("password", pwd);
		driver = prop.getProperty("driver", driver);
		url = prop.getProperty("url", url);

	}
	/**
	 * @param sql
	 */
	protected String logSQL(String sql)
	{
		Log.getLogger().info(sql);
		return sql;
	}

	protected ResultSet populateRS(String bufSQL) throws Exception
	{
		ResultSet rs = null;
		String tmpSQL = null;
		Statement stmt = null;
		try
		{
			stmt = getStatement();
			if (stmt != null)
			{
				// stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				// ResultSet.CONCUR_UPDATABLE);
				Log.getLogger().info("Retrieving: " + bufSQL);

				int indexOfSelect = bufSQL.lastIndexOf("*/");
				if (indexOfSelect != -1)
					tmpSQL = bufSQL.substring(indexOfSelect + 2);
				else
					tmpSQL = bufSQL;
				rs = stmt.executeQuery(tmpSQL);
			} else
				throw new Exception("Connection object not initialised");
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
			throw e;
		}
		return rs;
	}
	/**
	 * Selects a random Key
	 */
	public synchronized String selectRandomKey()
	{
		String key = "";
		try
		{
			if (keys == null)
				do
				{
					keys = getKeys();
					if (keys != null & keys.isEmpty())
						deleteSelected();
				} while (keys == null || keys.isEmpty());
			int max = keys.size() - 1;
			int index = 0;
			if (max != 0)
				index = (int) Math.round(Math.random() * max);
			key = keys.get(index);
			keys.remove(index);
			writeSelectedKey(key);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		return key;
	}
	/**
	 * @param connectString
	 *            The ConnectString to set.
	 */
	public void setConnectString(String connectString)
	{
		this.connectString = connectString;
	}
	/**
	 * @param dataCollection
	 *            The dataCollection to set.
	 */
	public void setDataCollection(Vector<Vector<Object>> dataCollection)
	{
		this.dataCollection = dataCollection;
	}
	/**
	 * @param headSQLString
	 *            the headSQLString to set
	 */
	public void setHeadSQLString(String headSQLString)
	{
		this.SQLString = headSQLString;
	}
	/**
	 * @param keys
	 *            the keys to set
	 */
	public void setKeys(Vector<String> keys)
	{
		ReadData.keys = keys;
	}
	/**
	 * @param prop
	 *            The prop to set.
	 */
	public void setProp(Properties prop)
	{
		this.prop = prop;
	}
	/**
	 * @param string
	 *            The SQLString to set.
	 */
	public void setSQLString(String string)
	{
		SQLString = string;
	}
	/**
	 * @param url
	 *            The url to set.
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}
	public abstract void useTestData();
	public abstract void useTrainData();
	/**
	 * @param key
	 */
	public void writeSelectedKey(String key)
	{
		try
		{
			// closeMainStatement();
			// stmt = con.createStatement();
			Statement stmt = getStatement();
			String sql = "INSERT INTO selectedKeys(id) VALUES( ? )";

			sql = sql.replaceFirst("\\?", key);
			logSQL(sql);
			stmt.execute(sql);
			stmt.close();
			// closeMainStatement();

		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}

	}
}
