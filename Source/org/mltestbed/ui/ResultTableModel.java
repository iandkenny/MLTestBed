/**
 * 
 */
package org.mltestbed.ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.table.AbstractTableModel;

import org.mltestbed.util.Log;


/**
 * @author Ian Kenny
 *
 */
public class ResultTableModel extends AbstractTableModel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> columns;
	private Properties runparams;
	
	private String url = "jdbc:odbc:";
	private String driver;
	private String connectString = "SwarmExperiments";
	private ResultSet rs;
//	private ResultSetMetaData rsmd;
	private final String resultSQL = "select * from results";
	private long ExpNum = -1;
	private String uid;
	private String pwd;

	/**
	 * 
	 */
	public ResultTableModel(Properties runparams)
	{
		super();
		this.runparams = runparams;
		initProps();
		columns = new ArrayList<String>();
		initColumnNames();
	}
	protected void initProps()
	{
//		SQLString = prop.getProperty("SQLString",SQLString);
		connectString = runparams.getProperty("connection",connectString);
		driver = runparams.getProperty("driver","sun.jdbc.odbc.JdbcOdbcDriver");
		url = runparams.getProperty("url",url);
		uid = runparams.getProperty("userid");
		pwd = runparams.getProperty("password");

	}

	private String whereClause()
	{
		return " Where ExpNum = " + ExpNum; 
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount()
	{
		int count = 0;
		try
		{
			Statement stmt = getStatement();
			String tmpSQL = resultSQL.substring(resultSQL.indexOf("from"));
			tmpSQL = "Select Count(*) " + tmpSQL + whereClause();
			ResultSet rs = stmt.executeQuery(tmpSQL);
			if (rs != null)
			{
				if(rs.first())

				count = rs.getInt(1);
			}
		} catch (ClassNotFoundException e)
		{
			Log.log(Level.SEVERE, e);
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
		}

		return count;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return columns.size();
	}


	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int x, int y)
	{
		Object value = null;
		try
		{
			Statement stmt = getStatement();
			
			ResultSet rs = stmt.executeQuery(resultSQL + whereClause());
			if (rs != null)
			{
				rs.absolute(y);

				value =getData(x);
			}
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int arg0)
	{
		return (String) columns.get(arg0);
	}
	
	private void initColumnNames()
	{
		

		columns = getTableHeads();
	}

	/**
	 * @param runparams The runparams to set.
	 */
	public void setRunparams(Properties runparams)
	{
		this.runparams = runparams;
	}
	/**
	 * @return ArrayList of column names
	 */
	private ArrayList<String> getTableHeads()
	{
		ArrayList<String> list = new ArrayList<String>();
		try
		{
			Statement stmt = getStatement();
			rs = stmt.executeQuery(resultSQL+whereClause());

			ResultSetMetaData rsmd = rs.getMetaData();
			int columns = rsmd.getColumnCount();
			for (int i = 1; i <= columns; i++)
				list.add(rsmd.getColumnName(i));

		} catch (ClassNotFoundException e)
		{
			Log.log(Level.SEVERE,e);
//			e.printStackTrace();
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
//			e.printStackTrace();
		}
		return list;
	}
	/**
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private Statement getStatement() throws ClassNotFoundException, SQLException
	{
		Connection con;
		Class.forName(driver);
		if(!uid.equals(""))
			con = DriverManager.getConnection(url + connectString);
		else
			con = DriverManager.getConnection(url + connectString,uid,pwd);
		Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		return stmt;
	}
	private Object getData(int field)
	{
		ResultSetMetaData rsmd;
		Object value=null; 
		try
		{
			rsmd = rs.getMetaData();

			int type = rsmd.getColumnType(field);
			switch (type)
			{
				case Types.BIT :
				case Types.BINARY :
					value = rs.getBoolean(field);
					break;
				case Types.VARCHAR :
				case Types.CHAR :
					value=rs.getString(field);
					break;
				case Types.BIGINT :
					value=rs.getLong(field);
					break;
				case Types.SMALLINT :
				case Types.INTEGER :
				case Types.TINYINT :
					value = rs.getInt(field);
					break;
				case Types.DECIMAL :
				case Types.REAL :
				case Types.FLOAT :
				case Types.DOUBLE :
					value = rs.getDouble(field);
					break;
				default :
					Log.getLogger().info("Don't know how to store "
							+ rsmd.getColumnName(field) + " of type "
							+ rsmd.getColumnType(field)
							+ "Metadata ruturns type index " + type);
					break;

			}
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#fireTableDataChanged()
	 */

	public void fireTableDataChanged(long Expnum)
	{
		this.ExpNum = Expnum;
		super.fireTableDataChanged();
	}
}
