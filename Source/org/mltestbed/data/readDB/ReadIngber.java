package org.mltestbed.data.readDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.data.ReadData;
import org.mltestbed.testFunctions.multiModal.Ingber;
import org.mltestbed.util.Log;



/*
 *   double q_n, d_i, s_i, t_i, z_i, c_r;
 int k_i;
 ALLOC_INT i, j;
 static LONG_INT funevals = 0;

 s_i = 0.2;
 t_i = 0.05;
 c_r = 0.15;

 q_n = 0.0;
 for (i = 0; i < *parameter_dimension; ++i) {
 if (fabs (parameter_upper_bound[i] - parameter_lower_bound[i]) <
 (double) EPS_DOUBLE)
 continue;

 j = i % 4;
 switch (j) {
 case 0:
 d_i = 1.0;
 break;
 case 1:
 d_i = 1000.0;
 break;
 case 2:
 d_i = 10.0;
 break;
 default:
 d_i = 100.0;
 }
 if (x[i] > 0.0) {
 k_i = (int) (x[i] / s_i + 0.5);
 } else if (x[i] < 0.0) {
 k_i = (int) (x[i] / s_i - 0.5);
 } else {
 k_i = 0;
 }

 if (fabs (k_i * s_i - x[i]) < t_i) {
 if (k_i < 0) {
 z_i = k_i * s_i + t_i;
 } else if (k_i > 0) {
 z_i = k_i * s_i - t_i;
 } else {
 z_i = 0.0;
 }
 q_n += c_r * d_i * z_i * z_i;
 } else {
 q_n += d_i * x[i] * x[i];
 }
 }

 */
public class ReadIngber extends ReadData
{
	private static Vector<String> keys = null;
	private static final String SELECT_RANGES = "SELECT mintrial,maxtrial,minsample,maxsample FROM Ranges WHERE ";
	private boolean bScoring = false;
	private String headSQLString;
	private boolean useView = false;
	/**
	 * 
	 */
	public ReadIngber()
	{
		super();
		connectString = "IngberData";
		// connectString =
		// "Data Source=luke;Database=Ingber;Integrated Security=True;";
		initProps();
	}

	private String buildSQL(boolean useView, String currKey, long trial,
			long sample)
	{
		String bufSQL = SQLString;
		try
		{
			if (!bScoring)
			{
				long minValue = Long.valueOf(prop.get("mintrial").toString());
				if (trial < minValue)
				{
					bufSQL = "/* The value of 'trial' has been modified from "
							+ trial + " to " + prop.get("mintrial")
							+ " due to missing org.mltestbed.data in the database*/\n"
							+ bufSQL;
					trial = minValue;
				}
				minValue = Long.valueOf(prop.get("minsample").toString());
				if (sample < minValue)
				{
					bufSQL = "/* The value of 'sample' has been modified from "
							+ trial + " to " + prop.get("mintrial")
							+ " due to missing org.mltestbed.data in the database*/\n"
							+ bufSQL;
					sample = minValue;
				}
			}

			String where;
			if (useView)
			{
				bufSQL = bufSQL.replaceFirst("EEGData", "EEGDataMaxed");
				where = " Where id = '" + currKey + "' And trial = " + trial
						+ " And sample = " + sample;

			} else
			{
				if (!bScoring)
				{
					where = " Where id = '"
							+ currKey
							+ "' And trial In (Select  Max(trial) From EEGData Where id = '"
							+ currKey
							+ "' And trial <="
							+ trial
							+ ") And sample In (Select  Max(sample) From EEGData Where id = '"
							+ currKey + "' And trial <=" + trial
							+ " And sample <= " + sample + ")";
				} else
				{
					where = " Where id = '" + currKey + "' And trial = "
							+ trial + " And sample = " + sample;
				}
			}
			bufSQL = bufSQL.replaceFirst(" ORDER ", where + " ORDER ");
		} catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.buildSQL(bufSQL,currKey);
	}

	@Override
	protected Vector<String> getKeys()
	{
		return getKeys(null);
	}

	/**
	 * @param type
	 * @return Vector of Keys
	 */
	public Vector<String> getKeys(String type)
	{
		Vector<String> keys = new Vector<String>();
		String bufSQL = headSQLString.replaceFirst("\\*", "Distinct A.id");
		bufSQL = bufSQL + " A WHERE A.id NOT IN (SELECT id FROM SelectedKeys)";

		if (type != null)

			if (type.equalsIgnoreCase("a") || type.equalsIgnoreCase("c"))
			{

				bufSQL += " AND A.isAlcoholic ='" + type.toUpperCase() + "'";
			}

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
	public Vector<Long> getTrials(String patientKey)
	{
		Vector<Long> keys = new Vector<Long>();
		String bufSQL = SQLString.substring(SQLString.indexOf("FROM"));
		int index = bufSQL.toUpperCase().indexOf("ORDER");
		if (index != -1)
			bufSQL = bufSQL.substring(0, index - 1);
		bufSQL = bufSQL.replaceFirst("FROM", "SELECT Distinct A.trial FROM");
		bufSQL = bufSQL
				+ " A WHERE A.id = '"
				+ patientKey
				+ "' AND A.trial NOT IN (SELECT trial FROM SelectedTrials WHERE id ='"
				+ patientKey + "')";

		try
		{
			ResultSet rs = populateRS(bufSQL);
			while (rs.next())
			{
				// Vector<Object> row = getRow();
				keys.add((Long) rs.getLong(1));
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

	public synchronized HashMap<String, Long> getRange(String key)
			throws SQLException
	{
		String sql = SELECT_RANGES + " id = '" + key + "'";
		HashMap<String, Long> range = new HashMap<String, Long>();
		try
		{
			ResultSet rs = populateRS(sql);
			rs.first();
			range.put("mintrial", Long.valueOf(rs.getLong(1)));
			range.put("maxtrial", Long.valueOf(rs.getLong(2)));
			range.put("minsample", Long.valueOf(rs.getLong(3)));
			range.put("maxsample", Long.valueOf(rs.getLong(4)));

			rs.close();
			prop.putAll(range);
			// rsmd = rs.getMetaData();
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
			throw e;
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return range;
	}
	/**
	 * @param key
	 * @param trial
	 * @param sample
	 * @return sensor values for the given trial,sample
	 */
	public TreeMap<String, Double> getTrialData(String key, long trial,
			long sample)
	{
		TreeMap<String, Double> data = new TreeMap<String, Double>();

		try
		{
			String bufSQL = buildSQL(useView, key, trial, sample);
			ResultSet rs = populateRS(bufSQL);
			if (rs.next())
			{
				do
				{
					String sensor = rs.getString("sensor");
					if (sensor == null)
						throw new NullPointerException("Sensor = null SQL="
								+ bufSQL);
					double value = rs.getDouble("value");
					data.put(new String(sensor), Double.valueOf(value));
					// keys.add(rs.getString(0));
				} while (rs.next());
				rs.getStatement().close();
				rs = null;
			} else
				Log.log(Level.SEVERE, new Exception(
						"There is no org.mltestbed.data for this query " + bufSQL));
			// rsmd = rs.getMetaData();
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (NullPointerException e)
		{
			Log.log(Level.SEVERE, e);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

		return data;
	}

	/**
	 * @param key
	 * @param trial
	 * @param sample
	 * @return score for trial sample
	 */
	public Double getTrialScore(String key, long trial, long sample)
	{
		Double data = null;

		try
		{
			String bufSQL = buildSQL(useView, key, trial, sample);
			bufSQL = bufSQL.replaceAll("EEGData", "preScored").replaceFirst(
					",sensor", "");
			String focusScore = "score";
			bufSQL = bufSQL.replaceFirst("\\*", focusScore);
			ResultSet rs = populateRS(bufSQL);
			if (rs.first())
			{
				data = rs.getDouble(focusScore);
				if (rs.wasNull())
					data = null;

				rs.getStatement().close();
				rs = null;
			} else
				Log.log(Level.SEVERE, new Exception(
						"There is no org.mltestbed.data for this query " + bufSQL));
			// rsmd = rs.getMetaData();
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (NullPointerException e)
		{
			Log.log(Level.SEVERE, e);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.data.ReadData#initProps()
	 */
	@Override
	protected void initProps()
	{

		super.initProps();
		prop.put("SQLString",
				"Select * From EEGData ORDER BY id,trial,sample,sensor");
		setSQLString(prop.getProperty("SQLString"));
		prop
				.put(
						"SQLDataString",
						"SELECT [AF1],[AF2],[AF7],[AF8],[AFZ],[C1],[C2],[C3],[C4],[C5],[C6],[CP1],[CP2],[CP3],[CP4],[CP5],[CP6],[CPZ],[CZ],[F1],[F2],[F3],[F4],[F5],[F6],[F7],[F8],[FC1],[FC2],[FC3],[FC4],[FC5],[FC6],[FCZ],[FP1],[FP2],[FPZ],[FT7],[FT8],[FZ],[nd],[O1],[O2],[OZ],[P1],[P2],[P3],[P4],[P5],[P6],[P7],[P8],[PO1],[PO2],[PO7],[PO8],[POZ],[PZ],[T7],[T8],[TP7],[TP8],[X],[Y]  FROM [PivotEEGData] ORDER BY [id],[trial],[sample]");
		prop.put("headSQLString", "Select * From EEGFILEHEADER");
		setHeadSQLString(prop.getProperty("headSQLString"));
		connectString = prop.getProperty("ConnectString", connectString);
		/*
		 * try { con = DriverManager.getConnection(url + connectString); stmt =
		 * con.createStatement(); } catch (SQLException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
	}

	public void preScore()
	{
		preScore1();
	}

	private void preScore1()
	{
		try
		{
			Ingber T = new Ingber();
			Vector<Double> v = new Vector<Double>(2);
			long recnum = 0;
			// store the score in field score to
			// test scoring algorithm equality
			String bufSQL = "Select Count(*) As Count From prescored where score is null";
			ResultSet rs = getLocalConnection().prepareStatement(bufSQL)
					.executeQuery();
			// ResultSet rs = populateRS(bufSQL);
			if (rs.next())
				recnum = rs.getLong(1);
			rs.getStatement().getConnection().close();
			bufSQL = "Select * From prescored where score is null";
			rs = populateRS(bufSQL);
			while (rs.next())
			{
				// rs.refreshRow();
				rs.getDouble("score");
				// make sure its still null
				if (rs.wasNull())
				{
					T.setPatientKey(rs.getString("id"));
					T.setScoring(true);
					T.init();
					double trial = rs.getLong("trial");
					v.add(0, trial);
					double sample = rs.getLong("sample");
					v.add(1, sample);
					T.Objective(v);
				}
				recnum--;
				Log.getLogger().info(recnum + " Records to process");
			}
			// connection.setAutoCommit(true);
			rs.getStatement().close();
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

	}
	@SuppressWarnings("unused")
	private void preScore2()
	{
		long recnum = 0;
		try
		{
			Ingber T = new Ingber();
			Vector<Double> v = new Vector<Double>(2);
			// store the score in field score to
			// test scoring algorithm equality
			String bufSQL = "Select top 1 * From prescored where score is null";
			ResultSet rs = populateRS(bufSQL);
			while (rs.next())
			{
				rs.getDouble("score");
				// make sure its still null
				if (rs.wasNull())
				{
					T.setPatientKey(rs.getString("id"));
					T.init();
					double trial = rs.getLong("trial");
					v.add(0, trial);
					double sample = rs.getLong("sample");
					v.add(1, sample);
					double result = T.Objective(v);
					// store the score in field score to
					// test scoring algorithm equality
					rs.updateDouble("score", result);
					rs.updateRow();
					// connection.commit();
				}
				recnum++;
				Log.getLogger().info(recnum + " records processed");
				rs.close();
				rs = populateRS(bufSQL);
			}
			rs.getStatement().close();
		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

	}

	/**
	 * Selects a random Key
	 */
	public synchronized String selectRandomKey(String type)
	{
		if (keys == null)
			keys = getKeys(type);
		int max = keys.size();
		int index = 0;
		if (max != 1)
			index = (int) Math.round(Math.random() * max);
		String key = keys.get(index);
		keys.remove(index);

		return key;
	}

	/**
	 * Selects a random Key
	 */
	public synchronized int selectRandomTrial(String patientKey)
	{
		Vector<Long> trials = getTrials(patientKey);
		int max = trials.size();
		int index = 0;
		if (max != 1)
			index = (int) Math.round(Math.random() * max);
		Long key = trials.get(index);
		return key.intValue();
	}

	/**
	 * @param headSQLString
	 *            the headSQLString to set
	 */
	public void setHeadSQLString(String headSQLString)
	{
		this.headSQLString = headSQLString;
	}

	public void setScoring(boolean scoring)
	{
		bScoring = scoring;

	}

	public void updateScore(String patientKey, long trial, long sample,
			double result)
	{
		try
		{
			// closeMainStatement();
			// stmt = con.createStatement();
			Statement stmt = getStatement();
			String sql = "UPDATE prescored SET score = " + result
					+ " WHERE [id]='" + patientKey + "' And trial = " + trial
					+ " And sample = " + sample;
			Log.getLogger().info(sql);
			stmt.execute(sql);
			stmt.close();
			// closeMainStatement();

		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}

	}

	/**
	 * @param key
	 */
	public void writeSelectedKey(String key)
	{
		try
		{
			// closeMainStatement();
			// stmt = con.createStatement();
			Statement stmt = getStatement();
			String sql = "INSERT INTO selectedkeys(id) VALUES( ? )";

			sql = sql.replaceFirst("\\?", "'" + key + "'");
			Log.getLogger().info(sql);
			stmt.execute(sql);
			stmt.close();
			// closeMainStatement();

		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}

	}

	public void writeSelectedTrial(String patientKey, int trial)
	{
		try
		{
			// closeMainStatement();
			// stmt = con.createStatement();
			Statement stmt = getStatement();
			String sql = "INSERT INTO selectedTrials(id,trial) VALUES( ?, ? )";

			sql = sql.replaceFirst("\\?", "'" + patientKey + "'");
			sql = sql.replaceFirst("\\?", Integer.toString(trial));
			Log.getLogger().info(sql);
			stmt.execute(sql);
			stmt.close();
			// closeMainStatement();

		} catch (SQLException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}

	}

	public void usePredictData()
	{
		setSQLString(prop.getProperty("SQLDataString"));

	}

	@Override
	public void useTestData()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void useTrainData()
	{
		// TODO Auto-generated method stub
		
	}

}
