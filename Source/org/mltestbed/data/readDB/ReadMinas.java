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
	private boolean MinasT2 = false;
	private boolean MinasT3 = false;
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
		int idx;
		bufSQL = bufSQL
				.substring(0,
						((idx = bufSQL.indexOf("ORDER BY")) > -1)
								? idx
								: bufSQL.length())
				.replaceFirst("FROM", "SELECT DISTINCT A.date FROM")
				+ "A WHERE A.date NOT IN (SELECT day FROM selectedDays)";

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
			Log.log(Level.SEVERE, e);
//			e.printStackTrace();
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
		int idx;
		bufSQL = bufSQL
				.substring(0,
						((idx = bufSQL.indexOf("ORDER BY")) > -1)
								? idx
								: bufSQL.length())
				.replaceFirst("FROM", "SELECT DISTINCT A.year FROM")
				+ "A WHERE A.year NOT IN (SELECT id FROM selectedKeys)";

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
			Log.log(Level.SEVERE, e);
//			e.printStackTrace();
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
				+ " minasPassage2015HourlyRestricted.avgHourlyAirTemp,"
				+ " minasPassage2015HourlyRestricted.avgHourlyHumidity,"
				+ " minasPassage2015HourlyRestricted.avgHourlyRainfallRate,"
				+ " minasPassage2015HourlyRestricted.avgHourlyBarometricPressure"
				+ " FROM MinasPassage.minasPassage2015HourlyRestricted ORDER BY minasPassage2015HourlyRestricted.Date, minasPassage2015HourlyRestricted.Hour"));

		prop.setProperty("MinasT2", prop.getProperty("MinasT2", "SELECT"
				+ " minasPassage2015Hourly.avgHourlyAirTemp,"
				+ " minasPassage2015Hourly.avgHourlyHumidity,"
				+ " minasPassage2015Hourly.avgHourlyRainfallRate,"
				+ " minasPassage2015Hourly.avgHourlyBarometricPressure"
				+ " FROM MinasPassage.minasPassage2015Hourly ORDER BY minasPassage2015Hourly.Date, minasPassage2015Hourly.Hour"));
		
		prop.setProperty("MinasT3", prop.getProperty("MinasT3", "SELECT"
				+ " minasPassage2015HourlyFirst6Months.avgHourlyAirTemp,"
				+ " minasPassage2015HourlyFirst6Months.avgHourlyHumidity,"
				+ " minasPassage2015HourlyFirst6Months.avgHourlyRainfallRate,"
				+ " minasPassage2015HourlyFirst6Months.avgHourlyBarometricPressure"
				+ " FROM MinasPassage.minasPassage2015HourlyFirst6Months ORDER BY minasPassage2015HourlyFirst6Months.Date, minasPassage2015HourlyFirst6Months.Hour"));

		prop.setProperty("MinasV1", prop.getProperty("MinasV1", "SELECT"
				+ " minasPassage2016HourlyRestricted.avgHourlyAirTemp,"
				+ " minasPassage2016HourlyRestricted.avgHourlyHumidity,"
				+ " minasPassage2016HourlyRestricted.avgHourlyRainfallRate,"
				+ " minasPassage2016HourlyRestricted.avgHourlyBarometricPressure"
				+ " FROM MinasPassage.minasPassage2016HourlyRestricted ORDER BY minasPassage2016HourlyRestricted.Date, minasPassage2016HourlyRestricted.Hour"));

		prop.setProperty("MinasV2", prop.getProperty("MinasV2", "SELECT"
				+ " minasPassage2016Hourly.avgHourlyAirTemp,"
				+ " minasPassage2016Hourly.avgHourlyHumidity,"
				+ " minasPassage2016Hourly.avgHourlyRainfallRate,"
				+ " minasPassage2016Hourly.avgHourlyBarometricPressure"
				+ " FROM MinasPassage.minasPassage2016Hourly ORDER BY minasPassage2016Hourly.Date, minasPassage2016Hourly.Hour"));

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

	/**
	 * @return the MinasT2
	 */
	public boolean isMinasT2()
	{
		return MinasT2;
	}

	/**
	 * @return the minasT3
	 */
	public boolean isMinasT3()
	{
		return MinasT3;
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

	/**
	 * @param minasT2
	 *            the minasT2 to set
	 */
	public void setMinasT2(boolean minasT2)
	{
		MinasT2 = minasT2;
	}

	/**
	 * @param minasT3 the minasT3 to set
	 */
	public void setMinasT3(boolean minasT3)
	{
		MinasT3 = minasT3;
	}

	public void useMinasT1()
	{
		setMinasT1(true);
		setMinasT2(false);
		setMinasT3(false);
		setSQLString(prop.getProperty("MinasT1"));

	}
	public void useMinasT2()
	{
		setMinasT2(true);
		setMinasT1(false);
		setMinasT3(false);
		setSQLString(prop.getProperty("MinasT2"));

	}
	public void useMinasT3()
	{
		setMinasT3(true);
		setMinasT2(false);
		setMinasT1(false);
		setSQLString(prop.getProperty("MinasT3"));

	}
	public void useMinasV1()
	{
		setSQLString(prop.getProperty("MinasV1"));
	}
	public void useMinasV2()
	{
		setSQLString(prop.getProperty("MinasV2"));
	}
	public void useTestData()
	{
		if (MinasT1)
			useMinasV1();
		else if (MinasT2)
			useMinasV2();
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
