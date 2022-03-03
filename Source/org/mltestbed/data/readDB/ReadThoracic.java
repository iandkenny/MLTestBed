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
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.data.ReadData;
import org.mltestbed.util.Log;

public class ReadThoracic extends ReadData
{
	private static final String PROPS = "ReadThoracic.properties";

	private String TrainSQLString;
	private String TestSQLString;

	/**
	 * 
	 */
	public ReadThoracic()
	{
		super();
	
		connectString = "mysql://dbs1:3306/MachineLearning?user=root&password=calvin";
		initProps();
		useAll();
		// connectString =
		// "Data Source=luke;Database=Thoracic;Integrated Security=True;";

	}

	public String buildSQL(String currKey)
	{

		String bufSQL = SQLString;

		if (!currKey.equals(""))
			bufSQL = bufSQL + " WHERE name = '" + currKey + "'";

		return super.buildSQL(bufSQL, currKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.data.ReadData#initProps()
	 */
	@Override
	protected void initProps()
	{

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

		super.initProps();
		// including name
		// SELECT [name],[MDVP Fo(Hz)],[MDVP Fhi(Hz)] ,[MDVP Flo(Hz)] ,[MDVP
		// Jitter(%)] ,[MDVP Jitter(Abs)] ,[MDVP RAP] ,[MDVP PPQ] ,[Jitter DDP]
		// ,[MDVP Shimmer] ,[MDVP Shimmer(dB)] ,[Shimmer APQ3] ,[Shimmer APQ5]
		// ,[MDVP APQ] ,[Shimmer DDA] ,[NHR]
		// ,[HNR],[RPDE],[DFA],[spread1],[spread2] ,[D2],[PPE], [status] FROM
		// [MachineLearning].[dbo].[Thoracic.data]
		prop.setProperty(
				"SQLString",
				prop.getProperty(
						"SQLString",
						"SELECT  [DIAG], [PRE4] ,[PRE5],[PRE6_num] as PRE6,[PRE7],[PRE8],[PRE9],[PRE10],[PRE11],[PRE14_num] as PRE14,[PRE17],[PRE19],[PRE25],[PRE30],[PRE32],[AGE],[Risk1Yr]  FROM [MachineLearning].[dbo].[ThoracicSurgery]"));
		SQLString = prop.getProperty("SQLString", SQLString);
		prop.setProperty(
				"TrainSQLString",
				prop.getProperty(
						"TrainSQLString",
						"SELECT  [DIAG], [PRE4] ,[PRE5],[PRE6_num] as PRE6,[PRE7],[PRE8],[PRE9],[PRE10],[PRE11],[PRE14_num] as PRE14,[PRE17],[PRE19],[PRE25],[PRE30],[PRE32],[AGE],[Risk1Yr]  FROM [MachineLearning].[dbo].[ThoracicSurgery_train]"));
		TrainSQLString = prop.getProperty("TrainSQLString", TrainSQLString);
		prop.setProperty(
				"TestSQLString",
				prop.getProperty(
						"TestSQLString",
						"SELECT [DIAG], [PRE4] ,[PRE5],[PRE6_num] as PRE6,[PRE7],[PRE8],[PRE9],[PRE10],[PRE11],[PRE14_num] as PRE14,[PRE17],[PRE19],[PRE25],[PRE30],[PRE32],[AGE],[Risk1Yr] FROM [MachineLearning].[dbo].[ThoracicSurgery_test]"));
		TestSQLString = prop.getProperty("TestSQLString", TestSQLString);
		prop.setProperty("connection",
				prop.getProperty("connection", connectString));
		connectString = prop.getProperty("connection", connectString);
		prop.setProperty("driver", prop.getProperty("driver", driver));
		driver = prop.getProperty("driver", driver);

		try
		{
			prop.store(new FileOutputStream(PROPS),
					"Read Thoracic db properties - Automatically saved");
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

	@Override
	protected Vector<String> getKeys()
	{
		Vector<String> keys = new Vector<String>();
		String bufSQL = SQLString.substring(SQLString.indexOf("FROM"));
		bufSQL = "SELECT name " + bufSQL;
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
	public void useTrainData()
	{
		SQLString = TrainSQLString;
	}
	public void useTestData()
	{
		SQLString = TestSQLString;
	}

	public void useAll()
	{
		SQLString = prop.getProperty("SQLString");
	}

}
