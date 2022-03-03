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
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.data.ReadData;
import org.mltestbed.util.Log;

public class ReadGasSpectrometary extends ReadData
{
	private static final String PROPS = "ReadGasSpectrometary.properties";

	private String binary;
	private String FilteredSQLString;

	private String FilterTestSQLString;

	private String FilterTrainSQLString;

	private String Sample3500SQLString;
	
	private String Sample1500SQLString;

	private String SampleSQLString;

	private String SampleTestSQLString;

	private String SampleTrainSQLString;

	private String single;

	private String TestSQLString;

	private String TrainSQLString;

	/**
	 * 
	 */
	public ReadGasSpectrometary()
	{
		super();
		driver = "org.mysql.jdbc.Driver";
		connectString = "mysql://dbs1:3306/MachineLearning?user=root&password=calvin";
		useTrainData();
		initProps();
	}

	public String buildSQL(String currKey)
	{

		String bufSQL = SQLString;

		if (!currKey.equals(""))
			// if (bufSQL.indexOf("ORDER") != -1)
			// bufSQL = bufSQL.replaceFirst("ORDER", " WHERE name = '"
			// + currKey + "' ORDER");
			// else
			bufSQL = bufSQL + " WHERE name = '" + currKey + "'";

		return super.buildSQL(bufSQL, currKey);
	}

	@Override
	public Vector<String> getKeys()
	{
		Vector<String> keys = new Vector<String>();
		String bufSQL = SQLString.substring(SQLString.indexOf("FROM"));
		int indexOf = bufSQL.indexOf("ORDER BY");
		if (indexOf != -1)
			bufSQL = "SELECT DISTINCT [id] " + bufSQL.substring(0, indexOf)
					+ " A WHERE A.[id] NOT IN (SELECT B.[id] FROM SelectedKeys B)";
		else
			bufSQL = "SELECT DISTINCT [id] " + bufSQL
					+ " A WHERE A.[id] NOT IN (SELECT B.[id] FROM SelectedKeys B)";;
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
		prop.setProperty("Single", prop.getProperty("Single", "[gas2value]"));
		single = prop.getProperty("Single");
		prop.setProperty("Binary", prop.getProperty("Binary",
				"[CO_n],[CO_L],[CO_M],[CO_H],[Me_n],[Me_L],[Me_M],[Me_H],[Et_n],[Et_L],[Et_M],[Et_H]"));
		binary = prop.getProperty("Binary");
		// "SELECT
		// [filename],[index],[id],[gas1],[gas2],[ch1],[ch2],[ch3],[ch4],[ch5],[ch6],[ch7],[ch8],[ch9],[ch10],[ch11]
		// FROM [MachineLearning].[dbo].[gas_dataset_twosources_raw] ORDER BY
		// [id], [index]"));
		prop.setProperty("SQLString", prop.getProperty("SQLString",
				"SELECT TOP 500 [ch1],[ch2],[ch3],[ch4],[ch5],[ch6],[ch7],[ch8],[ch9],[ch10],[ch11],[gas2value] FROM [MachineLearning].[dbo].[vw_gas_dataset_twosources_raw] ORDER BY [id], [index]"));
		SQLString = prop.getProperty("SQLString", SQLString);
		prop.setProperty("TrainSQLString", prop.getProperty("TrainSQLString",
				"SELECT  TOP 500 [ch1],[ch2],[ch3],[ch4],[ch5],[ch6],[ch7],[ch8],[ch9],[ch10],[ch11],[gas2value] FROM [MachineLearning].[dbo].[vw_gas_dataset_twosources_raw_train] ORDER BY [id], [index]"));
		TrainSQLString = prop.getProperty("TrainSQLString", TrainSQLString);
		prop.setProperty("TestSQLString", prop.getProperty("TestSQLString",
				"SELECT  TOP 500 [ch1],[ch2],[ch3],[ch4],[ch5],[ch6],[ch7],[ch8],[ch9],[ch10],[ch11],[gas2value] FROM [MachineLearning].[dbo].[vw_gas_dataset_twosources_raw_test] ORDER BY [id], [index]"));
		TestSQLString = prop.getProperty("TestSQLString", TestSQLString);
		prop.setProperty("FilterSQLString", prop.getProperty("FilterSQLString",
				"SELECT  TOP 500 [ch1],[ch2],[ch3],[ch4],[ch5],[ch6],[ch7],[ch8],[ch9],[ch10],[ch11],[gas2value] FROM [MachineLearning].[dbo].[vw_gas_dataset_twosources_raw_filter]"));
		FilteredSQLString = prop.getProperty("FilterSQLString",
				FilteredSQLString);
		prop.setProperty("FilterTestSQLString", prop.getProperty(
				"FilterTestSQLString",
				"SELECT  TOP 500 [ch1],[ch2],[ch3],[ch4],[ch5],[ch6],[ch7],[ch8],[ch9],[ch10],[ch11],[gas2value] FROM [MachineLearning].[dbo].[vw_gas_dataset_twosources_raw_test_filter]"));
		FilterTestSQLString = prop.getProperty("FilterTestSQLString",
				FilterTestSQLString);
		prop.setProperty("FilterTrainSQLString", prop.getProperty(
				"FilterTrainSQLString",
				"SELECT  TOP 500 [ch1],[ch2],[ch3],[ch4],[ch5],[ch6],[ch7],[ch8],[ch9],[ch10],[ch11],[gas2value] FROM [MachineLearning].[dbo].[vw_gas_dataset_twosources_raw_train_filter]"));
		FilterTrainSQLString = prop.getProperty("FilterTrainSQLString",
				FilterTrainSQLString);
		prop.setProperty("SampleSQLString", prop.getProperty("SampleSQLString",
				"SELECT  TOP 500 [ch1],[ch2],[ch3],[ch4],[ch5],[ch6],[ch7],[ch8],[ch9],[ch10],[ch11],[gas2value] FROM [MachineLearning].[dbo].[vw_gas_dataset_twosources_raw_filter] ORDER BY RAND()"));
		SampleSQLString = prop.getProperty("SampleSQLString", SampleSQLString);
		prop.setProperty("SampleTestSQLString", prop.getProperty(
				"SampleTestSQLString",
				"SELECT  TOP 500 [ch1],[ch2],[ch3],[ch4],[ch5],[ch6],[ch7],[ch8],[ch9],[ch10],[ch11],[gas2value] FROM [MachineLearning].[dbo].[vw_gas_dataset_twosources_raw_test_filter] ORDER BY RAND()"));
		SampleTestSQLString = prop.getProperty("SampleTestSQLString",
				SampleTestSQLString);
		prop.setProperty("SampleTrainSQLString", prop.getProperty(
				"SampleTrainSQLString",
				"SELECT  TOP 500 [ch1],[ch2],[ch3],[ch4],[ch5],[ch6],[ch7],[ch8],[ch9],[ch10],[ch11],[gas2value] FROM [MachineLearning].[dbo].[vw_gas_dataset_twosources_raw_train_filter] ORDER BY RAND()"));
		SampleTrainSQLString = prop.getProperty("SampleTrainSQLString",
				SampleTrainSQLString);
		// gas_dataset_twosources_raw_sampled
		prop.setProperty("Sample3500SQLString", prop.getProperty(
				"Sample3500SQLString",
				"SELECT [ch1],[ch2],[ch3],[ch4],[ch5],[ch6],[ch7],[ch8],[ch9],[ch10],[ch11],[gas2value] FROM [MachineLearning].[dbo].[gas_dataset_twosources_raw_sampled_3500]"));
		Sample3500SQLString = prop.getProperty("Sample3500SQLString",
				Sample3500SQLString);
		prop.setProperty("Sample1500SQLString", prop.getProperty(
				"Sample1500SQLString",
				"SELECT [ch1],[ch2],[ch3],[ch4],[ch5],[ch6],[ch7],[ch8],[ch9],[ch10],[ch11],[gas2value] FROM [MachineLearning].[dbo].[gas_dataset_twosources_raw_sampled_1500]"));
		Sample1500SQLString = prop.getProperty("Sample1500SQLString",
				Sample1500SQLString);
		prop.setProperty("connection",
				prop.getProperty("connection", connectString));
		connectString = prop.getProperty("connection", connectString);
		prop.setProperty("driver", prop.getProperty("driver", driver));
		driver = prop.getProperty("driver", driver);
		try
		{
			prop.store(new FileOutputStream(PROPS),
					"Read Gas Spectrometary db properties - Automatically saved");
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
	public void use3500()
	{
		SQLString = Sample3500SQLString;
	}
	public void use1500()
	{
		SQLString = Sample1500SQLString;
	}
	public void useAll()
	{
		SQLString = prop.getProperty("SQLString");
	}
	public void useBinary()
	{
		SQLString = SQLString.replace(single, binary);

	}

	public void useFiltered()
	{
		SQLString = FilteredSQLString;
	}

	public void useFilterTest()
	{
		SQLString = FilterTestSQLString;

	}

	public void useFilterTrain()
	{
		SQLString = FilterTrainSQLString;

	}

	public void useSample()
	{
		SQLString = SampleSQLString;

	}

	public void useSampleTest()
	{
		SQLString = SampleTestSQLString;

	}
	public void useSampleTrain()
	{
		SQLString = SampleTrainSQLString;

	}

	public void useSingle()
	{
		SQLString = SQLString.replace(binary, single);

	}

	public void useTestData()
	{
		SQLString = TestSQLString;
	}
	public void useTrainData()
	{
		SQLString = TrainSQLString;
	}

	@Override
	public void writeSelectedKey(String key)
	{
		try
		{
			// closeMainStatement();
			// stmt = con.createStatement();
			Statement stmt = getStatement();
			String sql = "INSERT INTO selectedkeys([id]) VALUES( ? )";

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
