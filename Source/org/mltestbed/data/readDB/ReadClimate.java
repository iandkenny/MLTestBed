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

public class ReadClimate extends ReadData
{
	private static final String PROPS = "ReadClimate.properties";

	private String TrainSQLString;
	private String TestSQLString;

	/**
	 * 
	 */
	public ReadClimate()
	{
		super();
		connectString = "mysql://dbs1:3306/MachineLearning?user=root&password=calvin";

		initProps();

		// connectString =
		// "Data Source=luke;Database=Parkinsons;Integrated Security=True;";

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
				"SELECT  [vconst_corr]," + "[vconst_2],"
						+ "[vconst_3]," + "[vconst_4]," + "[vconst_5],"
						+ "[vconst_7]," + "[ah_corr]," + "[ah_bolus],"
						+ "[slm_corr]," + "[efficiency_factor],"
						+ "[tidal_mix_max]," + "[vertical_decay_scale],"
						+ "[convect_corr]," + "[bckgrnd_vdc1],"
						+ "[bckgrnd_vdc_ban]," + "[bckgrnd_vdc_eq],"
						+ "[bckgrnd_vdc_psim]," + "[Prandtl]," + "[outcome]+1 "
						+ "FROM [MachineLearning].[climate_model_simulation_crashes_dataset] order by [Study],[Run]"));
		SQLString = prop.getProperty("SQLString", SQLString);
		TrainSQLString = SQLString.replace("dataset", "dataset_train");
		prop.setProperty("TrainSQLString",
				prop.getProperty("TrainSQLString", TrainSQLString));
		TrainSQLString = prop.getProperty("TrainSQLString", TrainSQLString);
		TestSQLString = SQLString.replace("dataset", "dataset_test");
		prop.setProperty("TestSQLString",
				prop.getProperty("TestSQLString", TestSQLString));
		TestSQLString = prop.getProperty("TestSQLString", TestSQLString);

		prop.setProperty("connection",
				prop.getProperty("connection", connectString));
		connectString = prop.getProperty("connection", connectString);
		prop.setProperty("driver", prop.getProperty("driver", driver));
		driver = prop.getProperty("driver", driver);

		try
		{
			prop.store(new FileOutputStream(PROPS),
					"Read Climate db properties - Automatically saved");
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
