/**
 * 
 */
package org.mltestbed.testFunctions.multiModal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.data.ReadData;
import org.mltestbed.heuristics.ANN.ArtificialNeuralNetwork;
import org.mltestbed.testFunctions.TestBaseANN;
import org.mltestbed.util.Log;

/**
 * @author ian
 *
 */

public class CNN extends TestBaseANN
{
	private class CNNData extends ReadData
	{
		private String keySQLString = "";

		@Override
		protected Vector<String> getKeys()
		{
			if (keys != null && keys.isEmpty())
			{
				SQLString = getKeySQLString();
				try
				{
					ResultSet rs = populateRS(getKeySQLString());
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
					e.printStackTrace();
				}
			}
			return keys;
		}

		/**
		 * @return the keySQLString
		 */
		public String getKeySQLString()
		{
			return keySQLString;
		}

		@Override
		protected void initProps()
		{
			keySQLString = prop.getProperty(KEY_SQL, keySQLString);
			prop.setProperty(KEY_SQL, keySQLString);
			super.initProps();
		}

		@Override
		public void useTestData()
		{
			SQLString = testSQLString;

		}

		@Override
		public void useTrainData()
		{
			SQLString = trainSQLString;

		}
	}
	private static final String KEY = "key";
	private static final String KEY_SQL = "KeySQL";
	private static final String PROPS = "DefaultCNNProps.properties";
	private static final String TEST_SQL = "TestSQL";
	private static final String TRAIN_SQL = "TrainSQL";

	private ArrayList<ArtificialNeuralNetwork> anns = new ArrayList<ArtificialNeuralNetwork>();
	private CNNData db = new CNNData();;
	private String key = "";
	private String testSQLString = "";
	private String trainSQLString = "";
	/**
	 * 
	 */
	public CNN()
	{
		super();
		description = "Default CNN model";
		load(new Properties());

	}

}
