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

public class ReadCosmology extends ReadData
{
	private static final String PROPS = "ReadCosmology.properties";

	private String TrainSQLString;
	private String TestSQLString;

	private String FilteredSQLString;

	private String zphotoTestSQLString;
	private String zphotoTrainSQLString;
	private String zphotoFullSQLString;
	/**
	 * 
	 */
	public ReadCosmology()
	{
		super();
		driver = "org.mysql.jdbc.Driver";
		connectString = "mysql://dbs1:3306/Cosmology?user=root&password=calvin";

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

		prop.setProperty(
				"SQLString",
				prop.getProperty(
						"SQLString",
						"SELECT TOP 500 [2 RA],[3 Dec],[4 dered_u],[5 dered_g],[6 dered_r],[7 dered_i],[8 dered_z],[9 deVMag_i],[11 delta_sg],[10 z_phot]  FROM [Cosmology].[dbo].[megazlrg_full]"));
		SQLString = prop.getProperty("SQLString", SQLString);
		prop.setProperty(
				"TrainSQLString",
				prop.getProperty(
						"TrainSQLString",
						"SELECT TOP 500 [2 RA],[3 Dec],[4 dered_u],[5 dered_g],[6 dered_r],[7 dered_i],[8 dered_z],[9 deVMag_i],[11 delta_sg],[10 z_phot]  FROM [Cosmology].[dbo].[megazlrg_train]"));
		TrainSQLString = prop.getProperty("TrainSQLString", TrainSQLString);
		prop.setProperty(
				"TestSQLString",
				prop.getProperty(
						"TestSQLString",
						"SELECT TOP 500 [2 RA],[3 Dec],[4 dered_u],[5 dered_g],[6 dered_r],[7 dered_i],[8 dered_z],[9 deVMag_i],[11 delta_sg],[10 z_phot]  FROM [Cosmology].[dbo].[megazlrg_test]"));
		TestSQLString = prop.getProperty("TestSQLString", TestSQLString);
		prop.setProperty(
				"zphotoTestSQLString",
				prop.getProperty(
						"zphotoTestSQLString",
						"SELECT TOP 500 [20 Petrosian Mag u] ,[21 Petrosian Mag g] ,[22 Petrosian Mag r] ,[23 Petrosian Mag i] ,[24 Petrosian Mag z],[10 z_phot]   FROM [Cosmology].[dbo].[redshift_joined_test]"));
		zphotoTestSQLString = prop.getProperty("zphotoTestSQLString", zphotoTestSQLString);
		prop.setProperty(
				"zphotoTrainSQLString",
				prop.getProperty(
						"zphotoTrainSQLString",
						"SELECT TOP 500 [20 Petrosian Mag u] ,[21 Petrosian Mag g] ,[22 Petrosian Mag r] ,[23 Petrosian Mag i] ,[24 Petrosian Mag z],[10 z_phot]   FROM [Cosmology].[dbo].[redshift_joined_Train]"));
		zphotoTrainSQLString = prop.getProperty("zphotoTrainSQLString", zphotoTrainSQLString);
		prop.setProperty(
				"zphotoFullSQLString",
				prop.getProperty(
						"zphotoFullSQLString",
						"SELECT TOP 500 [20 Petrosian Mag u] ,[21 Petrosian Mag g] ,[22 Petrosian Mag r] ,[23 Petrosian Mag i] ,[24 Petrosian Mag z],[10 z_phot]   FROM [Cosmology].[dbo].[redshift_joined_Full]"));
		zphotoFullSQLString = prop.getProperty("zphotoFullSQLString", zphotoFullSQLString);

		prop.setProperty("connection",
				prop.getProperty("connection", connectString));
		connectString = prop.getProperty("connection", connectString);
		prop.setProperty("driver", prop.getProperty("driver", driver));
		driver = prop.getProperty("driver", driver);
		try
		{
			prop.store(new FileOutputStream(PROPS),
					"Read Cosmology db properties - Automatically saved");
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
	public void useFiltered()
	{
		SQLString = FilteredSQLString;
	}
	public void usezphotoTrain()
	{
		SQLString = zphotoTrainSQLString;
	}
	public void usezphotoTest()
	{
		SQLString = zphotoTestSQLString;
	}
	public void usezphotoFull()
	{
		SQLString = zphotoFullSQLString;
	}

	public void useAll()
	{
		SQLString = prop.getProperty("SQLString");
	}

}
