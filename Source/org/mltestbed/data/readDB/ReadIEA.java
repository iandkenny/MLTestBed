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

public class ReadIEA extends ReadData
{
	private static final String PROPS = "ReadIEA.properties";
	/**
	 * 
	 */
	public ReadIEA()
	{
		super();
		connectString = "mysql://dbs1:3306/IEA?user=root&password=calvin";
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

	public void deleteSelectedKeys()
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
		prop.setProperty("Q162CorrelatedTrainSQLString", prop.getProperty(
				"Q162CorrelatedTrainSQLString",
				"Q162CorrelatedTrainSQLString=SELECT Q48, Q49, Q50, Q34, Q35, Q36, Q37, Q38, REVERSE, REVERSE_0, REVERSE_1, REVERSE_2, REVERSE_3, Q110, Q111, Q112, Q113, Q114, Q115, Q116, Q117, Q118, Q119, Q135, Q136, Q137, Q138, Q139, Q140, Q141, Q142, Q143, Q123, Q126, Q127, Q128, Q129, Q130, Q131, Q72, Q73, Q74, Q75, Q76, Q77, Q78, Q79, Q80, Q81, Q146, Q147, Q148, Q151, Q152, Q153, Q154, Q155, Q158, Q159, Q160, Q161, Q163, Q164, Q165, Q166, Q167, Q55, Q56, Q57, Q58, Q59, Q60, Q61, Q62, Q63, Q64, Q170, Q174, Q176, Q177, Q178, Q84, Q86, Q88, Q95, Q96, Q97, Q98, Q100, Q101, Q107, Q162 AS Predict FROM IEA.IEASurveyTrain"));
		prop.setProperty("RSQLString", prop.getProperty("RSQLString",
				"RSQLString=SELECT Q69, Q16, Q19, Q20, Q21, Q22, Q23, Q24, Q25, Q26, Q27, Q32, Q46, Q47, Q48, Q49, Q50, Q52, Q51, Q34, Q35, Q36, Q37, Q38, REVERSE, REVERSE2, REVERSE3, REVERSE4, REVERSE5, Q110, Q111, Q112, Q113, Q114, Q115, Q116, Q117, Q118, Q119, Q134, Q135, Q136, Q137, Q138, Q139, Q140, Q141, Q142, Q143, Q122, Q123, Q124, Q125, Q126, Q127, Q128, Q129, Q130, Q131, Q72, Q73, Q74, Q75, Q76, Q77, Q78, Q79, Q80, Q81, Q146, Q147, Q148, Q149, Q150, Q151, Q152, Q153, Q154, Q155, Q158, Q159, Q160, Q161, Q163, Q164, Q165, Q166, Q167, Q55, Q56, Q57, Q58, Q59, Q60, Q61, Q62, Q63, Q64, Q170, Q171, Q172, Q173, Q174, Q175, Q176, Q177, Q178, Q179, Q84, Q86, Q85, Q87, Q88, Q89, Q90, Q91, Q94, Q95, Q96, Q97, Q98, Q99, Q100, Q101, Q104, Q105, Q106, Q107, Q162 AS Predict FROM IEA.IEASurvey_R_Data"));
		prop.setProperty("TrainSQLString", prop.getProperty("TrainSQLString",
				"SELECT AgeGroup, Q69, Q16, Q19, Q20, Q21, Q22, Q23, Q24, Q25, Q26, Q27, Q32, Q46, Q47, Q48, Q49, Q50, Q52, Q51, Q34, Q35, Q36, Q37, Q38, REVERSE, REVERSE_0, REVERSE_1, REVERSE_2, REVERSE_3, Q110, Q111, Q112, Q113, Q114, Q115, Q116, Q117, Q118, Q119, Q134, Q135, Q136, Q137, Q138, Q139, Q140, Q141, Q142, Q143, Q122, Q123, Q124, Q125, Q126, Q127, Q128, Q129, Q130, Q131, Q72, Q73, Q74, Q75, Q76, Q77, Q78, Q79, Q80, Q81, Q146, Q147, Q148, Q149, Q150, Q151, Q152, Q153, Q154, Q155, Q158, Q159, Q160, Q161, Q163, Q164, Q165, Q166, Q167, Q55, Q56, Q57, Q58, Q59, Q60, Q61, Q62, Q63, Q64, Q170, Q171, Q172, Q173, Q174, Q175, Q176, Q177, Q178, Q179, Q84, Q86, Q85, Q87, Q88, Q89, Q90, Q91, Q94, Q95, Q96, Q97, Q98, Q99, Q100, Q101, Q104, Q105, Q106, Q107, Q162 AS Predict FROM IEA.IEASurveyTrain"));
		setSQLString(prop.getProperty("TrainSQLString"));
		prop.setProperty("TestSQLString", prop.getProperty("TestSQLString",
				prop.getProperty(("TrainSQLString").replaceAll("IEASurveyTrain",
						"IEASurveyTest"))));
		prop.setProperty("AllSQLString", prop.getProperty("AllSQLString",
				prop.getProperty(("TrainSQLString").replaceAll("IEASurveyTrain",
						"IEASurveyAll"))));
		prop.setProperty("SQLString", prop.getProperty("SQLString",
				prop.getProperty(("TrainSQLString"))));
		prop.setProperty("nQuireTrainSQLString", prop.getProperty(
				"nQuireTrainSQLString",
				"SELECT Answer1p1,Answer1p3,Answer1p4, Answer1p5, Answer1p6, Answer1p7, Answer2p1, Answer2p2, Answer2p3, Answer2p4, Answer2p5, Answer2p6, Answer2p7, Answer2p8, Answer2p9, Answer2p10, Answer2p11, Answer2p12, Answer2p13, Answer2p14, Answer2p15, Answer2p16, Answer2p17, Answer2p18, Answer2p19, Answer2p20, Answer2p21, Answer3p1, Answer3p2, Answer3p3, Answer3p4, Answer3p5, Answer3p6, Answer3p7, Answer3p8, Answer3p9, Answer3p10, Answer4p1, Answer4p2, Answer4p3, Answer4p4, Answer5p1, Answer5p2, Answer5p3, Answer5p4, Answer5p5, Answer5p6, Answer5p7, Answer5p8, Answer5p9, Answer5p10, Answer6p1, Answer6p2, Answer6p3, Answer6p4, Answer6p5, Answer6p6, Answer6p7, Answer6p8, Answer6p9, Answer6p10, Answer7p1, Answer7p2, Answer7p3, Answer7p4, Answer7p5, Answer7p6, Answer7p7, Answer7p8, Answer7p9, Answer7p10, Answer7p11, Answer8p1, Target FROM Climate_change_and_you_train"));
		setSQLString(prop.getProperty("nQuireTrainSQLString"));
		prop.setProperty("nQuireTestSQLString", prop.getProperty(
				"nQuireTestSQLString", prop.getProperty(("nQuireTrainSQLString")
						.replaceAll("_train", "_test"))));

		prop.setProperty("connection",
				prop.getProperty("connection", connectString));
		connectString = prop.getProperty("connection", connectString);
		prop.setProperty("driver", prop.getProperty("driver", driver));
		driver = prop.getProperty("driver", driver);

		try
		{
			prop.store(new FileOutputStream(PROPS),
					"Read IEA db properties - Automatically saved");
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

	public String selectRandomKey()
	{
		String key = null;

		try
		{
			synchronized (ReadIEA.class)
			{
				if (keys == null)
					do
					{
						keys = getKeys();
						if (keys != null & keys.isEmpty())
							deleteSelectedKeys();
					} while (keys == null || keys.isEmpty());
				int max = keys.size() - 1;
				int index = 0;
				if (max > 0)
				{
					index = (int) Math.round(Math.random() * max);
					key = keys.get(index);
					keys.remove(index);
					writeSelectedKey(key);
				}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return key;
	}
	public void useAllData()
	{
		SQLString = prop.getProperty("AllSQLString");

	}

	public void usenQuireTestData()
	{
		SQLString = prop.getProperty("nQuireTestSQLString");

	}

	public void usenQuireTrainData()
	{
		SQLString = prop.getProperty("nQuireTrainSQLString");

	}

	@Override
	public void useTestData()
	{
		SQLString = prop.getProperty("TestSQLString");

	}
	@Override
	public void useTrainData()
	{
		SQLString = prop.getProperty("TrainSQLString");

	}
	public void writeSelectedKey(String key)
	{
		try
		{
			// closeMainStatement();
			// stmt = con.createStatement();
			Statement stmt = getStatement();
			String sql = "INSERT INTO selectedKeys(key) VALUES( ? )";

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
