package org.mltestbed.data.readDB;
/*
 * SELECT AVG(field) FROM table GROUP BY INT(ROWNUMBER)/n
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.data.ReadData;
import org.mltestbed.util.Log;

public class ReadMinas extends ReadData
{
	private static final String PROPS = "ReadMinas.properties";
	private boolean MinasT1 = false;
	/**
	 * 
	 */
	public ReadMinas()
	{
		super();
		connectString = "mysql://dbs1:3306/MinasPassage?user=root&password=calvin";
		initProps();
	}

	public String buildSQL(String currKey)
	{

		String bufSQL = SQLString;

		if (!currKey.equals(""))
			bufSQL = bufSQL.replaceFirst("ORDER",
					" WHERE Year = " + currKey + " ORDER");

		return super.buildSQL(bufSQL, currKey);
	}

	public void deleteSelectedDays()
	{
		try
		{
			Statement stmt = getStatement();
			String sql = "DELETE FROM selectedDays";
			logSQL(sql);
			stmt.execute(sql);
			stmt.close();

		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}

	}

	public Vector<String> getDays()
	{
		Vector<String> keys = new Vector<String>();
		String bufSQL = SQLString.substring(SQLString.indexOf("FROM"));
		String replacestr = "ORDER BY [France Date /Time]";
		if (bufSQL.trim().contains(replacestr))
		{
			bufSQL = bufSQL.replace(replacestr, "A").replaceFirst("FROM",
					"SELECT DISTINCT A.date FROM")
					+ " WHERE A.date NOT IN (SELECT day FROM selectedDays)";
		} else
			bufSQL = bufSQL.replace("ORDER BY Date", "A").replaceFirst("FROM",
					"SELECT DISTINCT A.date FROM")
					+ " WHERE A.date NOT IN (SELECT day FROM selectedDays)";

		try
		{
			ResultSet rs = populateRS(bufSQL);
			while (rs.next())
			{
				keys.add(rs.getDate(1).toString());
			}
			rs.close();
			// rsmd = rs.getMetaData();
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return keys;
	}

	/**
	 * @return Vector of Keys
	 */
	public Vector<String> getKeys()
	{
		Vector<String> keys = new Vector<String>();
		String bufSQL = SQLString.substring(SQLString.indexOf("FROM"));
		bufSQL = bufSQL.replaceFirst("ORDER BY Date", "A").replaceFirst("FROM",
				"SELECT DISTINCT A.year FROM")
				+ " WHERE A.year NOT IN (SELECT id FROM selectedKeys)";

		try
		{
			ResultSet rs = populateRS(bufSQL);
			while (rs.next())
			{
				keys.add(rs.getString(1));
			}
			rs.close();
			// rsmd = rs.getMetaData();
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return keys;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.data.ReadData#initProps()
	 */
	@Override
	protected void initProps()
	{
		if (prop == null)
			prop = new Properties();

		try
		{
			prop.load(new FileInputStream(PROPS));
		} catch (FileNotFoundException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		} catch (IOException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		}

		prop.setProperty("MinasT1", prop.getProperty("MinasT1", "SELECT"
				+ " minasPassage2015.avgHourlyAirTemp,"
				+ " minasPassage2015.avgHourlyHumidity,"
				+ " minasPassage2015.avgHourlyRainfallRate,"
				+ " minasPassage2015.avgHourlyBarometricPressure"
				+ " FROM MinasPassage.minasPassage2015 ORDER BY minasPassage2015.date, minasPassage2015.hour LIMIT 0, 1000"));
		setSQLString(prop.getProperty("MinasT1"));
		String connect = prop.getProperty("connection", connectString);
		connectString = connect.isEmpty() ? connectString : connect;
		prop.setProperty("connection", connectString);
		prop.setProperty("driver", prop.getProperty("driver", driver));
		driver = prop.getProperty("driver", driver);

		try
		{
			prop.store(new FileOutputStream(PROPS),
					"Read Minas Passage db properties - Automatically saved");
		} catch (FileNotFoundException e)
		{

			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		} catch (IOException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		}
	}

	/**
	 * @return the MinasT1
	 */
	public boolean isMinasT1()
	{
		return MinasT1;
	}

	public String selectRandomDay()
	{
		String key = null;

		try
		{
			synchronized (ReadMinas.class)
			{
				if (keys == null)
					do
					{
						keys = getDays();
						if (keys != null & keys.isEmpty())
							deleteSelectedDays();
					} while (keys == null || keys.isEmpty());
				int max = keys.size() - 1;
				int index = 0;
				if (max != 0)
					index = (int) Math.round(Math.random() * max);
				key = keys.get(index);
				keys.remove(index);
				writeSelectedDay(key);
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return key;
	}

	/**
	 * @param minasT1
	 *            the minasT1 to set
	 */
	public void setMinasT1(boolean minasT1)
	{
		MinasT1 = minasT1;
	}
	public void useMinasT1()
	{
		setMinasT1(true);
		setSQLString(prop.getProperty("MinasT1"));

	}

	public void useTestData()
	{

	}

	@Override
	public void useTrainData()
	{
		useMinasT1();

	}

	public void writeSelectedDay(String key)
	{
		try
		{
			// closeMainStatement();
			// stmt = con.createStatement();
			Statement stmt = getStatement();
			String sql = "INSERT INTO selectedDays(day) VALUES( ? )";

			sql = sql.replaceFirst("\\?", "'" + key + "'");
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
