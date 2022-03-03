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

public class ReadHeart extends ReadData
{
	private static final String PROPS = "ReadHeart.properties";

	private String TrainSQLString;
	private String TestSQLString;

	private String ClevelandSQLString;

	/**
	 * 
	 */
	public ReadHeart()
	{
		super();
		connectString = "mysql://dbs1:3306/MachineLearning?user=root&password=calvin";

		initProps();
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

		prop.setProperty("SQLString", prop.getProperty("SQLString",
				"SELECT [heart_disease].[#3  (age)], [heart_disease].[#4  (sex)],   [heart_disease].[#9  (cp)],[heart_disease].[#10 (trestbps)],[heart_disease].[#12 (chol)],[heart_disease].[#16 (fbs)],[heart_disease].[#19 (restecg)],  [heart_disease].[#32 (thalach)],[heart_disease].[#38 (exang)],[heart_disease].[#40 (oldpeak)],[heart_disease].[#41 (slope)],   [heart_disease].[#44 (ca)],[heart_disease].[#51 (thal)],[heart_disease].[#58 (num)] FROM [MachineLearning].[heart_disease];"));;
		SQLString = prop.getProperty("SQLString", SQLString);
		TrainSQLString = SQLString.replace("heart_disease", "heart_disease_train");
		prop.setProperty("TrainSQLString",
				prop.getProperty("TrainSQLString", TrainSQLString));
		TrainSQLString = prop.getProperty("TrainSQLString", TrainSQLString);
		TestSQLString = SQLString.replace("heart_disease", "heart_disease_test");
		prop.setProperty("TestSQLString",
				prop.getProperty("TestSQLString", TestSQLString));
		TestSQLString = prop.getProperty("TestSQLString", TestSQLString);
		ClevelandSQLString = SQLString.replace("heart_disease", "heart_processed_cleveland");
		prop.setProperty("ClevelandSQLString",
				prop.getProperty("ClevelandSQLString", ClevelandSQLString));
		ClevelandSQLString = prop.getProperty("ClevelandSQLString", ClevelandSQLString);

		prop.setProperty("connection",
				prop.getProperty("connection", connectString));
		connectString = prop.getProperty("connection", connectString);
		prop.setProperty("driver", prop.getProperty("driver", driver));
		driver = prop.getProperty("driver", driver);

		try
		{
			prop.store(new FileOutputStream(PROPS),
					"Read Heart Disease db properties - Automatically saved");
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
	public void useClevelandData()
	{
		SQLString = ClevelandSQLString;
	}

	public void useAll()
	{
		SQLString = prop.getProperty("SQLString");

	}

}
