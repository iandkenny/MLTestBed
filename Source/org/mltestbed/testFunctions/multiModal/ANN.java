package org.mltestbed.testFunctions.multiModal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.data.ReadData;
import org.mltestbed.heuristics.ANN.ArtificialNeuralNetwork;
import org.mltestbed.heuristics.ANN.Layer;
import org.mltestbed.heuristics.ANN.Neuron;
import org.mltestbed.heuristics.ANN.activators.ActivationStrategy;
import org.mltestbed.heuristics.ANN.activators.LinearActivationStrategy;
import org.mltestbed.testFunctions.TestBaseANN;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;

public class ANN extends TestBaseANN
{
	private static final String INPUTS = "inputs";

	private class ANNData extends ReadData
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
			keySQLString = prop.getProperty(KEY_SQL, "");
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
	private static final String PROPS = "DefaultANNProps.properties";
	private static final String TEST_SQL = "TestSQL";

	private static final String TRAIN_SQL = "TrainSQL";
	private ANNData db = new ANNData();;
	private String key = "";
	private String testSQLString = "";
	private String trainSQLString = "";
	private String keySQLString = "";

	/**
	 * 
	 */
	public ANN()
	{
		super();
		description = "Default ANN model";
		load(new Properties());
	}
	protected ArtificialNeuralNetwork createANN(String name,
			ArrayList<Integer> neuronsperLayer,
			ArrayList<ActivationStrategy> activationStrategy)
	{
		ArtificialNeuralNetwork ann = new ArtificialNeuralNetwork(name);
		Layer previousLayer = null;
		Layer layer;
		boolean bias;
		for (int i = 0; i < neuronsperLayer.size(); i++)
		{
			String id = name + ".layer";
			bias = (neuronsperLayer.get(i) < 0) ? true : false;
			if (bias)
			{
				Neuron biasNeuron = new Neuron(id + ".bias",
						activationStrategy.get(i));
				layer = new Layer(id, previousLayer, biasNeuron);
			} else
				layer = new Layer(id, previousLayer);
			int absneurons = Math.abs(neuronsperLayer.get(i));
			for (int j = 0; j < absneurons; j++)
			{
				Neuron neuron = new Neuron(layer.getId() + "." + i + "." + j,
						activationStrategy.get(i));
				layer.addNeuron(neuron);
			}

			ann.addLayer(layer);
			previousLayer = layer;
		}

		return ann;

	}

	@Override
	protected void createParams()
	{
		if (params == null)
			params = new Properties();
		// the following three are added to make it easier to generalise the ANN
		// code initially
		params.setProperty(TEST_SQL, "");
		params.setProperty(TRAIN_SQL, "");
		params.setProperty(KEY_SQL, "");
		//
		params.setProperty(KEY, "");
		params.setProperty(INPUTS, "1");
		// outputs defined as the difference between inputs and total number of
		// variables
	}
	@Override
	public void destroy()
	{
		if (tmpFile != null)
		{
			tmpFile.delete();
			tmpFile = null;
		}
		super.destroy();
	}
	public double error(Vector<Double> result, Vector<Double> target)
	{
		double error = 0.0;
		for (int i = 0; i < result.size(); i++)
		{
			double diff = result.get(i) - target.get(i);
			error += diff * diff;
		}
		return error * 0.5;
	}

	@Override
	protected void finalize() throws Throwable
	{
		destroy();
		super.finalize();
	}

	/**
	 * @return the db
	 */
	public ReadData getData()
	{
		return db;
	}
	public void gradientUpdate()
	{
	}

	@Override
	public void init()
	{
		try
		{
			super.init();
			trainSQLString = params.getProperty(TRAIN_SQL, trainSQLString);
			testSQLString = params.getProperty(TEST_SQL, testSQLString);
			key = params.getProperty(KEY, key);
			int length = trainSQLString.split(",").length;
			if (length != inputs + outputs)
				inputs = length - outputs;
			if (inputs <= 0)
				throw new Exception(
						"Data Source doesn't specify sufficent columns for specified inputs and outputs");
			if (annXML.isEmpty())
			{
				ArrayList<Integer> neuronsperLayer = new ArrayList<Integer>();
				ArrayList<ActivationStrategy> activationStrategy = new ArrayList<ActivationStrategy>();
				int multiplier = (bias) ? -1 : 1;
				int k = 0;
				neuronsperLayer.set(k++, inputs * multiplier);
				for (int i = 0; i < hidden; i++)
					neuronsperLayer.set(k++, inputs * multiplier);
				neuronsperLayer.set(k, outputs * multiplier);
				for (int i = 0; i < 3; i++)
					activationStrategy.set(i, new LinearActivationStrategy());
				ann = createANN(description, neuronsperLayer,
						activationStrategy);
				db.useTrainData();
			} else
			{
				ann = ArtificialNeuralNetwork.loadANN(annXML);
				db.useTestData();
			}
			Vector<String> keys;
			if (!key.trim().equals(""))
			{
				keys = new Vector<String>(Arrays.asList(key.split(",")));
				db.setKeys(keys);
			}
		} catch (NumberFormatException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}
	}
	public void load(Properties props)
	{
		if (props != null)
		{
			if (props.isEmpty())
				try
				{
					props.load(new FileInputStream(PROPS));
				} catch (FileNotFoundException e)
				{

					Log.getLogger().info(e.getMessage());
					// e.printStackTrace();
				} catch (IOException e)
				{
					Log.getLogger().info(e.getMessage());
					// e.printStackTrace();
				}

			if (params == null)
				params = new Properties();
			else
				params.clear();
			createParams();
			params.putAll(db.getProp());
			Enumeration<Object> keys = params.keys();
			while (keys.hasMoreElements())
			{
				String key = (String) keys.nextElement();
				if (props.containsKey(key))
					params.setProperty(key, props.getProperty(key));
			}
			try
			{
				props.store(new FileOutputStream(PROPS),
						"Default ANN properties - Automatically saved");
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
	}
	@Override
	public double Objective(Particle particle)
	{
		ann.setWeights(particle.getPosition());
		ArrayList<ArrayList<Object>> dataValues = db.getData(true);
		ArrayList<Vector<Double>> expected = new ArrayList<Vector<Double>>();
		ArrayList<Vector<Double>> predicted = new ArrayList<Vector<Double>>();
		double rmse = 0;
		while (dataValues != null)
		{
			appendLocalBuffer("", true);
			for (ArrayList<Object> row : dataValues)
			{
				Vector<Double> inputvec = new Vector<Double>();
				for (int i = 0; i < inputs; i++)
				{
					inputvec.add((Double) row.get(i));
				}
				Vector<Double> expect = new Vector<Double>();
				for (int i = inputs; i < row.size(); i++)
				{
					expect.add((Double) row.get(i));
				}
				ann.setInputs(inputvec);
				Vector<Double> outputs = ann.getOutput();
				ann.rnnIter();
				expected.add(expect);
				predicted.add(outputs);
				double rowRMSE = RMSE(expect, outputs);
				appendLocalBuffer("<Output><Input>" + inputvec.toString()
						+ "</Input><RequiredValue>" + expected.toString()
						+ "</RequiredValue><Predicted>" + outputs.toString()
						+ "</Predicted><RowRMSE>" + rowRMSE
						+ "</RowRMSE></Output>", false);
				rmse += rowRMSE;
			}
			dataValues = db.getData(false);
		}

		particle.setFuncSpecific(
				"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><?xml-stylesheet type=\"text/xsl\" href=\"annresults.xsl\"?><IterResult><Key>"
						+ key + "</Key>" + ann.getXML(),
				true);
		transBuf(particle);
		particle.setFuncSpecific("<RMSE>" + rmse + "</RMSE></IterResult>",
				false);
		return super.Objective(particle);
	}
	@Override
	protected void runTest(Particle gbest)
	{
		// TODO Auto-generated method stub
		super.runTest(gbest);
	}

	@Override
	public void setMDimension(int dimension) throws Exception
	{
		if (ann != null)
			dimension = ann.getWeights().size();
		super.setMDimension(dimension);
	}

	@Override
	protected void setRange()
	{

		mMin = new double[mDimension];
		mMax = new double[mDimension];

		for (int i = 0; i < mDimension; i++)
		{
			mMin[i] = -2;
			mMax[i] = 2;

		}
	}

}
