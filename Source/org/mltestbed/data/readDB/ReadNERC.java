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

public class ReadNERC extends ReadData
{
	private static final String PROPS = "ReadNERC.properties";
	private boolean FTSQL = false;
	private boolean GlacierT1 = false;
	private boolean GlacierT2 = false;
	private boolean GlacierT3 = false;
	private boolean GlacierT4 = false;
	private boolean GlacierT4Complete = false;
	private boolean LSSQL = false;
	private boolean Q95T1 = false;
	private boolean Q95T2 = false;
	private boolean Q95T3 = false;
	private boolean ResidT1 = false;
	private boolean ResidT2 = false;
	private boolean RSDailyAvgT1 = false;
	private boolean TLQ95 = false;
	/**
	 * 
	 */
	public ReadNERC(boolean useLS)
	{
		super();
		connectString = "mysql://dbs1:3306/NERC?user=root&password=calvin";
		LSSQL = useLS;
		initProps();
		if (LSSQL)
			useLS();
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
		prop.setProperty("SQLString", prop.getProperty("SQLString",
				"Select  AvgDryBulb, AvgRain, AvgHafrenValue, AvgHoreValue, AvgTanllwythValue, AvgSevenValue FROM DailyAvgFlow ORDER BY Date"));
		setSQLString(prop.getProperty("SQLString"));
		prop.setProperty("HourlySQLString", prop.getProperty("HourlySQLString",
				"Select  AvgHafrenValue, AvgHoreValue, AvgTanllwythValue, AvgSevenValue FROM HourlyAvgFlowPerDay ORDER BY Date,Hour"));
		prop.setProperty("LSSQLString", prop.getProperty("LSSQLString",
				"SELECT [In1 54005 t] AS in1, [In2 54020 t] AS in2, [In3 54018 t] AS in3, [In4 54012 t] AS in4, [In5 54005 t-1] AS in5, [In6 54020 t-1] AS in6, [In7 54018 t-1] AS in7, [In8 54012 t-1] AS in8, [Out 54095 t] AS out FROM LSRSevern ORDER BY Year, Seq"));

		prop.setProperty("Q95Test1",
				"SELECT [S at M (t-1)] AS in1, [P at Y (t-1)] AS in2, [RB at H (t-1)] AS in3, [T at W (t-1)] AS in4,[S at B (t)] AS out FROM RSevernQ95 ORDER BY [Date]");
		prop.setProperty("Q95Test2",
				"SELECT [S at M (t-2)] AS in1,[P at Y (t-2)] AS in2,[RB at H (t-2)] AS in3, [T at W (t-2)] AS in4, [S at M (t-1)] AS in5, [P at Y (t-1)] AS in6, [RB at H (t-1)] AS in7, [T at W (t-1)] AS in8,[S at B (t)] AS out FROM RSevernQ95 ORDER BY [Date]");
		prop.setProperty("Q95Test3",
				"SELECT [S at M (t-3)] AS in1,[P at Y (t-3)] AS in2,[RB at H (t-3)] AS in3,[T at W (t-3)] AS in4,[S at M (t-2)] AS in5,[P at Y (t-2)] AS in6,[RB at H (t-2)] AS in7,[T at W (t-2)] AS in8,[S at M (t-1)] AS in9,[P at Y (t-1)] AS in10,[RB at H (t-1)] AS in11,[T at W (t-1)] AS in12, [S at B (t)] AS out FROM RSevernQ95 ORDER BY [Date]");
		prop.setProperty("FullTraining",
				"SELECT [S at M (t-3)] AS in1 ,[P at Y (t-3)] AS in2,[RB at H (t-3)] AS in3,[T at W (t-3)] AS in4,[S at M (t-2)] AS in5,[P at Y (t-2)] AS in6,[RB at H (t-2)] AS in7,[T at W (t-2)] AS in8,[S at M (t-1)] AS in9,[P at Y (t-1)] AS in10,[RB at H (t-1)] AS in11,[T at W (t-1)] AS in12,[S at M (t)] AS in13,[P at Y (t)] AS in14,[RB at H (t)] AS in15,[T at W (t)] AS in16,[S at B (t)] AS out FROM [Full Training Data] ORDER BY [Date]");
		prop.setProperty("TrainingLessQ95",
				"SELECT [S at M (t-3)] AS in1,[P at Y (t-3)] AS in2,[RB at H (t-3)] AS in3,[T at W (t-3)] AS in4,[S at M (t-2)] AS in5,[P at Y (t-2)] AS in6,[RB at H (t-2)] AS in7,[T at W (t-2)] AS in8,[S at M (t-1)] AS in9,[P at Y (t-1)] AS in10,[RB at H (t-1)] AS in11,[T at W (t-1)] AS in12,[S at M (t)] AS in13,[P at Y (t)] AS in14,[RB at H (t)] AS in15,[T at W (t)] AS in16,[S at B (t)] AS out FROM [Training Less Than Q95 Data] ORDER BY [Date]");
		prop.setProperty("ResidT1",
				"SELECT [S at M (t-1) (In1)] as in1,[P at Y (t-1) (In2)] as in2, [RB at H (t-1) (In 3)] as in3,[T at W (t-1) (In 4)] as in4, [S at B (t) (Out)] as in5, [Multiple Regression Residual (MRR)] as out  FROM [Residual modelling]");
		prop.setProperty("RSDailyAvgT1",
				"SELECT [S at M (t-1) (In1)] as in1,[P at Y (t-1) (In2)] as in2, [RB at H (t-1) (In 3)] as in3,[T at W (t-1) (In 4)] as in4, [S at B (t) (Out)] as out FROM [Full Training Data] ORDER BY [Date]");

		prop.setProperty("ResidT2",
				"SELECT in1, in2, in3, in4, out  FROM [AvgResidual]");
		prop.setProperty("GlacierT1",
				"SELECT [Noir Air Temp] as in1,[Noir Q] as in2,[Blanc Q] as in3,[Noir Inst# Load] as in4,[Blanc Inst# Load] as in5,[Road Br SSL] as out FROM [Glacier Data] ORDER BY [France Date /Time]");
		prop.setProperty("GlacierT2",
				"SELECT [Noir Q] as in2,[Blanc Q] as in3,[Noir Inst# Load] as in4,[Blanc Inst# Load] as in5,[Road Br SSL] as out FROM [Glacier Data] ORDER BY [France Date /Time]");
		prop.setProperty("GlacierT3",
				"SELECT [Noir Air Temp] as in1,[Noir Q] as in2,[Blanc Q] as in3,[Noir Inst# Load] as in4,[Blanc Inst# Load] as in5,[Road Br SSL] as out FROM [Glacier Data] WHERE [Date] NOT IN ('2005-07-12') ORDER BY [France Date /Time]");
		prop.setProperty("GlacierT4",
				"SELECT in1, in2, in3, in4, output FROM GlacierT4 ORDER BY Date");
		prop.setProperty("GlacierT4Complete",
				"SELECT in1, in2, in3, in4, output FROM GlacierT4Complete ORDER BY Date");
		prop.setProperty("GlacierT5",
				"SELECT in1, in2, in3, in4, output FROM GlacierT5 ORDER BY Date");
		prop.setProperty("GlacierT6",
				"SELECT in1, in2, in3, in4, output FROM GlacierT6 ORDER BY Date");

		prop.setProperty("connection",
				prop.getProperty("connection", connectString));
		connectString = prop.getProperty("connection", connectString);
		prop.setProperty("driver", prop.getProperty("driver", driver));
		driver = prop.getProperty("driver", driver);

		try
		{
			prop.store(new FileOutputStream(PROPS),
					"Read NERC db properties - Automatically saved");
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
	 * @return the fTSQL
	 */
	public boolean isFTSQL()
	{
		return FTSQL;
	}

	/**
	 * @return the glacierT1
	 */
	public boolean isGlacierT1()
	{
		return GlacierT1;
	}

	/**
	 * @return the glacierT2
	 */
	public boolean isGlacierT2()
	{
		return GlacierT2;
	}
	/**
	 * @return the glacierT3
	 */
	public boolean isGlacierT3()
	{
		return GlacierT3;
	}
	/**
	 * @return the glacierT4
	 */
	public boolean isGlacierT4()
	{
		return GlacierT4;
	}

	/**
	 * @return the glacierT4Complete
	 */
	public boolean isGlacierT4Complete()
	{
		return GlacierT4Complete;
	}
	/**
	 * @return the q95T1
	 */
	public boolean isQ95T1()
	{
		return Q95T1;
	}
	/**
	 * @return the q95T2
	 */
	public boolean isQ95T2()
	{
		return Q95T2;
	}

	/**
	 * @return the q95T3
	 */
	public boolean isQ95T3()
	{
		return Q95T3;
	}
	/**
	 * @return the residT1
	 */
	public boolean isResidT1()
	{
		return ResidT1;
	}

	/**
	 * @return the residT2
	 */
	public boolean isResidT2()
	{
		return ResidT2;
	}

	/**
	 * @return the rSDailyAvgT1
	 */
	public boolean isRSDailyAvgT1()
	{
		return RSDailyAvgT1;
	}

	/**
	 * @return the tLQ95
	 */
	public boolean isTLQ95()
	{
		return TLQ95;
	}

	public String selectRandomDay()
	{
		String key = null;

		try
		{
			synchronized (ReadNERC.class)
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
	 * @param fTSQL
	 *            the fTSQL to set
	 */
	public void setFTSQL(boolean fTSQL)
	{
		FTSQL = fTSQL;
	}
	/**
	 * @param glacierT1
	 *            the glacierT1 to set
	 */
	public void setGlacierT1(boolean glacierT1)
	{
		GlacierT1 = glacierT1;
	}
	/**
	 * @param glacierT2
	 *            the glacierT2 to set
	 */
	public void setGlacierT2(boolean glacierT2)
	{
		GlacierT2 = glacierT2;
	}

	/**
	 * @param glacierT3
	 *            the glacierT3 to set
	 */
	public void setGlacierT3(boolean glacierT3)
	{
		GlacierT3 = glacierT3;
	}
	/**
	 * @param glacierT4
	 *            the glacierT4 to set
	 */
	public void setGlacierT4(boolean glacierT4)
	{
		GlacierT4 = glacierT4;
	}

	/**
	 * @param glacierT4Complete
	 *            the glacierT4Complete to set
	 */
	public void setGlacierT4Complete(boolean glacierT4Complete)
	{
		GlacierT4Complete = glacierT4Complete;
	}

	/**
	 * @param q95t1
	 *            the q95T1 to set
	 */
	public void setQ95T1(boolean q95t1)
	{
		Q95T1 = q95t1;
	}
	/**
	 * @param q95t2
	 *            the q95T2 to set
	 */
	public void setQ95T2(boolean q95t2)
	{
		Q95T2 = q95t2;
	}
	/**
	 * @param q95t3
	 *            the q95T3 to set
	 */
	public void setQ95T3(boolean q95t3)
	{
		Q95T3 = q95t3;
	}

	/**
	 * @param residT1
	 *            the residT1 to set
	 */
	public void setResidT1(boolean residT1)
	{
		ResidT1 = residT1;
	}

	/**
	 * @param residT2
	 *            the residT2 to set
	 */
	public void setResidT2(boolean residT2)
	{
		ResidT2 = residT2;
	}

	/**
	 * @param rSDailyAvgT1
	 *            the rSDailyAvgT1 to set
	 */
	public void setRSDailyAvgT1(boolean rSDailyAvgT1)
	{
		RSDailyAvgT1 = rSDailyAvgT1;
	}

	/**
	 * @param tLQ95
	 *            the tLQ95 to set
	 */
	public void setTLQ95(boolean tLQ95)
	{
		TLQ95 = tLQ95;
	}

	public void useFT()
	{
		setFTSQL(true);
		setSQLString(prop.getProperty("FullTraining"));

	}

	public void useGlacierT1()
	{
		setGlacierT1(true);
		setSQLString(prop.getProperty("GlacierT1"));

	}

	public void useGlacierT2()
	{
		setGlacierT2(true);
		setSQLString(prop.getProperty("GlacierT2"));

	}

	public void useGlacierT3()
	{
		setGlacierT3(true);
		setSQLString(prop.getProperty("GlacierT3"));

	}

	public void useGlacierT4()
	{
		setGlacierT4(true);
		setSQLString(prop.getProperty("GlacierT4"));

	}

	public void useGlacierT4Complete()
	{
		setGlacierT4(true);
		setSQLString(prop.getProperty("GlacierT4Complete"));

	}

	public void useGlacierT5()
	{
		setGlacierT4(true);
		setSQLString(prop.getProperty("GlacierT5"));

	}

	public void useGlacierT6()
	{
		setGlacierT4(true);
		setSQLString(prop.getProperty("GlacierT6"));

	}

	public void useHourlyAvg()
	{
		setSQLString(prop.getProperty("HourlySQLString"));

	}

	public void useLS()
	{
		LSSQL = true;
		setSQLString(prop.getProperty("LSSQLString"));

	}

	public void useQ95T1()
	{
		setQ95T1(true);
		setSQLString(prop.getProperty("Q95Test1"));

	}

	public void useQ95T2()
	{
		setQ95T2(true);
		setSQLString(prop.getProperty("Q95Test2"));

	}

	public void useQ95T3()
	{
		setQ95T3(true);
		setSQLString(prop.getProperty("Q95Test3"));

	}

	public void useResidT1()
	{
		setResidT1(true);
		setSQLString(prop.getProperty("ResidT1"));

	}

	public void useResidT2()
	{
		setResidT2(true);
		setSQLString(prop.getProperty("ResidT2"));

	}

	public void useRSDailyAvgT1()
	{
		setRSDailyAvgT1(true);
		setSQLString(prop.getProperty("RSDailyAvgT1"));

	}

	@Override
	public void useTestData()
	{
		// TODO Auto-generated method stub

	}

	public void useTLQ95()
	{
		setTLQ95(true);
		setSQLString(prop.getProperty("TrainingLessQ95"));

	}

	@Override
	public void useTrainData()
	{
		// TODO Auto-generated method stub

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
