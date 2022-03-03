package org.mltestbed.testFunctions.multiModal;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.data.readDB.ReadGasSpectrometary;
import org.mltestbed.testFunctions.PsuedoSVM;
import org.mltestbed.testFunctions.TestBase;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;
import org.mltestbed.util.SimpleRegression;

public class MLGasSpectrometary extends PsuedoSVM implements Cloneable
{
	private static final int F2_OUTPUTS = 12;
	private static final String FACTOR_MAX = "factorMax";
	private static final String FACTOR_MIN = "factorMin";
	private static final String FUNC = "function";
	private static final String KEY = "key";
	private static final String PERFORM_TEST = "performTest";
	private static final String RESETDB = "resetDB";

	private static final String USE_SUM = "useSum";
	private LinkedList<ArrayList<Double>> data = null;
	private ReadGasSpectrometary db = null;
	private int factorMax;
	private String key;
	private String objFunc;
	private boolean useBiggest;
	/**
	 * 
	 */
	public MLGasSpectrometary()
	{
		super();
		description = "Machine Learning: Gas Spectrometary Data";
		info = "Test function for Gas Spectrometary Data";
		minimised = true;
		createParams();
		db = new ReadGasSpectrometary();
		db.useTrainData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#clone()
	 */
	@Override
	public Object clone()
	{
		TestBase clone = (TestBase) super.clone();
		tmpFile = null;
		if (data != null)
			data = new LinkedList<ArrayList<Double>>(data);
		return clone;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.SVM#createParams()
	 */
	@Override
	protected void createParams()
	{
		super.createParams();
		factor = factorMin = 1;
		factorMax = factorMin + 2;
		setDims();
		db.useTrainData();

		params.setProperty(KEY, "random");
		params.setProperty(PERFORM_TEST, "train");
		params.setProperty(FUNC, "f2");
		params.setProperty(RESETDB, "false");
		params.setProperty(USE_SUM, "true");
		params.setProperty(FACTOR_MIN, Integer.toString(factorMin));
		params.setProperty(FACTOR_MAX, Integer.toString(factorMax));
		// params.setProperty(USE_SUM, "true");

	}

	@Override
	protected void finalize() throws Throwable
	{
		if (tmpFile != null)
			tmpFile.delete();
		super.finalize();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#getNumKeys()
	 */
	@Override
	public int getNumKeys()
	{
		if (db == null)
			db = new ReadGasSpectrometary();
		performTest();
		Vector<String> keys = db.getKeys();
		return keys.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.SVM#init()
	 */
	@Override
	public void init()
	{
		super.init();
		performTest();

		String buf = params.getProperty(RESETDB);
		if (buf.equalsIgnoreCase("true"))
			db.deleteSelected();

		key = params.getProperty(KEY);
		if (key.equalsIgnoreCase("random"))
		{
			do
			{
				key = db.selectRandomKey();
			} while (key == null || key.equalsIgnoreCase("random"));
			// params.setProperty(KEY, key);
		} else if (key.equalsIgnoreCase("all"))
			key = "";
		// String buf = params.getProperty(USE_BIGGEST_DIFF);
		// useBiggest = (buf.equalsIgnoreCase("true")) ? true : false;
		objFunc = params.getProperty(FUNC, "f1");
		if (objFunc.equalsIgnoreCase("f1"))
			db.useSingle();
		else if (objFunc.equalsIgnoreCase("f2"))
			db.useBinary();
		useSum = Boolean.parseBoolean(params.getProperty(USE_SUM, "true"));
		buf = params.getProperty(FACTOR_MIN, Integer.toString(factorMin));
		factorMin = Integer.parseInt(buf);
		buf = params.getProperty(FACTOR_MAX, Integer.toString(factorMax));
		factorMax = Integer.parseInt(buf);
		try
		{
			setMDimension(0);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

		HashMap<String, String> keys = new HashMap<String, String>();
		if (!key.equals(""))
			keys.put("[id]", key);
		data = db.getData(keys);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.SVM#Objective(java.util.Vector)
	 */
	@Override
	public double Objective(Particle p)
	{
		if (objFunc.equalsIgnoreCase("f1"))
			Objective1(p);
		else if (objFunc.equalsIgnoreCase("f2"))
			Objective2(p);

		return super.Objective(p);

	}

	private double Objective1(Particle p)
	{
		double r2 = 0;
		appendLocalBuffer(null, true);
		SimpleRegression sr = new SimpleRegression();
		double reqSum = 0;
		double sum = 0;
		long count = 0;
		double biggest = 0;
		Vector<Double> expected = new Vector<Double>();
		Vector<Double> predicted = new Vector<Double>();
		for (Iterator<ArrayList<Double>> iterator = data.iterator(); iterator
				.hasNext();)
		{
			ArrayList<Double> row = (ArrayList<Double>) iterator.next();
			Double expect = row.get(row.size() - 1);
			reqSum += expect;
			double score = super.CosmologyObjective(p, row);
			appendLocalBuffer(p.getFuncSpecific(), false);
			double abs = Math.abs(score);
			if (abs > biggest)
				biggest = abs;
			sum += abs;
			// sum += score;
			expected.add(expect);
			double predict = super.getPredicted();
			predicted.add(predict);
			count++;
		}
		r2 = sr.getRSquare();
		sr = null;
		if (useBiggest)
		{
			mResult = biggest;
		} else
		{
			// mResult = (reqSum / count) - (sum / count);
			// *** RMSE
			// mResult = Math.sqrt(Math.pow((reqSum / count) - (sum /
			// count),
			// 2));
			// mResult = Math.sqrt(Math.pow(reqSum - sum, 2));
			// *** Absolute Diff
			// mResult = Math.abs((reqSum / count) - (sum / count));
			// mResult = Math.abs(reqSum - sum);
			// *** Just sum
			mResult = sum;
			// RSME
			// mResult = sum / count;
			// reciprocal R squared
			// mResult = 1 - r2;
		}

		p.setFuncSpecific(
				"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><?xml-stylesheet type=\"text/xsl\" href=\"gasresults.xsl\"?><IterResult><Key>"
						+ key + "</Key>" + toXML(),
				true);
		transBuf(p);
		p.setFuncSpecific("<Sum>" + sum + "</Sum><ReqSum>" + reqSum
				+ "</ReqSum><AvgSum>" + (sum / count)
				+ "</AvgSum><AvgRequiredSum>" + (reqSum / count)
				+ "</AvgRequiredSum><AME description = \"Absolute Maximum Error\">"
				+ biggest + "</AME><MAE description = \"Mean Absolute Error\">"
				+ MAE(expected, predicted)
				+ "</MAE><ME description = \"Mean Error\">"
				+ ME(expected, predicted)
				+ "</ME><RAE description = \"Relative Absolute Error\">"
				+ RAE(expected, predicted)
				+ "</RAE><RMSE description = \"Root Mean Squared Error\">"
				+ RMSE(expected, predicted)
				+ "</RMSE><R2 description = \"R Squared\">" + r2
				+ "</R2><Result>" + mResult + "</Result></IterResult>", false);

		return mResult;
	}
	private double Objective2(Particle p)
	{
		double r2 = 0;
		try
		{
			// Vector<Double> v = p.getPosition();
			// SimpleRegression sr = new SimpleRegression();
			double reqSum = 0;
			double sum = 0;
			long count = 0;
			double biggest = 0;
			Vector<Double> expected = new Vector<Double>();
			Vector<Double> predicted = new Vector<Double>();
			ArrayList<Vector<Double>> results = new ArrayList<Vector<Double>>();
			synchronized (data)
			{

				for (Iterator<ArrayList<Double>> iterator = data
						.iterator(); iterator.hasNext();)
				{
					ArrayList<Double> row = (ArrayList<Double>) iterator.next();
					// if (row.isEmpty())
					// Log.log(Level.WARNING,"row is empty, list="
					// + list.toString());
					expected.clear();
					for (int j = 0; j < F2_OUTPUTS; j++)
					{
						int i = row.size() - 1;
						expected.add(0, row.get(i));
						row.remove(i);
					}
					// reqSum += expect;
					double score = super.GasObjective2(p, row, expected);
					BufferedReader reader = null;
					do
					{
						reader = new BufferedReader(p.getFuncSpecificReader());
					} while (reader == null);
					String line;
					appendLocalBuffer(null, true);
					while ((line = reader.readLine()) != null)
					{
						appendLocalBuffer(line, false);

					}
					reader.close();
					reader = null;
					double abs = Math.abs(score);
					if (abs > biggest)
						biggest = abs;
					// sum += abs;
					sum += score;
					results.add(super.getResult());
					double predict = super.getPredicted();
					predicted.add(predict);
					// sr.addData(expect, predict);
					count++;
					// super.setFuncSpecific(null);
				}
			}

			// writer = null;
			// r2 = sr.getRSquare();
			// sr = null;
			if (useBiggest)
			{
				mResult = biggest;
			} else
			{
				// mResult = (reqSum / count) - (sum / count);
				// *** RMSE
				// mResult = Math.sqrt(Math.pow((reqSum / count) - (sum /
				// count),
				// 2));
				// mResult = Math.sqrt(Math.pow(reqSum - sum, 2));
				// *** Absolute Diff
				// mResult = Math.abs((reqSum / count) - (sum / count));
				// mResult = Math.abs(reqSum - sum);
				// *** Just sum
				mResult = sum;
				// RSME
				// mResult = sum / count;
				// reciprocal R squared
				// mResult = 1 - r2;

			}
			// setFuncSpecific(null);
			p.setFuncSpecific(
					"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><?xml-stylesheet type=\"text/xsl\" href=\"gasresults.xsl\"?><IterResult><Key>"
							+ key + "</Key>" + toXML(),
					true);
			transBuf(p);
			Vector<Double> finalprediction = new Vector<Double>();
			int size = results.get(0).size();
			for (int i = 0; i < size; i++)
			{
				finalprediction.add(0.0);
			}
			for (Iterator<Vector<Double>> iterator = results
					.iterator(); iterator.hasNext();)
			{
				Vector<Double> vector = (Vector<Double>) iterator.next();
				for (int i = 0; i < vector.size(); i++)
					finalprediction.set(i,
							finalprediction.get(i) + vector.get(i));
			}

			NumberFormat formatter = new DecimalFormat(
					"##################0.0##############################");
			String xml = "<PredictionVector>";
			if (finalprediction != null)
			{
				for (int i = 0; i < finalprediction.size(); i++)
				{
					xml += "<Cell>";
					xml += formatter.format(finalprediction.get(i));
					xml += "</Cell>";
				}
			}
			xml += "\n</PredictionVector>";

			p.setFuncSpecific("<Sum>" + sum + "</Sum><ReqSum>" + reqSum
					+ "</ReqSum><AvgSum>" + (sum / count)
					+ "</AvgSum><AvgRequiredSum>" + (reqSum / count)
					+ "</AvgRequiredSum><AME description = \"Absolute Maximum Error\">"
					+ biggest
					+ "</AME><MAE description = \"Mean Absolute Error\">"
					+ MAE(expected, predicted)
					+ "</MAE><ME description = \"Mean Error\">"
					+ ME(expected, predicted)
					+ "</ME><RAE description = \"Relative Absolute Error\">"
					+ RAE(expected, predicted)
					+ "</RAE><RMSE description = \"Root Mean Squared Error\">"
					+ RMSE(expected, predicted)
					+ "</RMSE><R2 description = \"R Squared\">" + r2
					+ "</R2><Result>" + mResult + "</Result>" + xml
					+ "</IterResult>", false);
		} catch (IOException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}

		return mResult;
	}

	/**
	 * 
	 */
	private void performTest()
	{
		String buf = params.getProperty(PERFORM_TEST);
		if (buf.equalsIgnoreCase("train"))
			db.useTrainData();
		else if (buf.equalsIgnoreCase("test"))
			db.useTestData();
		else if (buf.equalsIgnoreCase("full"))
			db.useAll();
		else if (buf.equalsIgnoreCase("filter"))
			db.useFiltered();
		else if (buf.equalsIgnoreCase("filtertrain"))
			db.useFilterTrain();
		else if (buf.equalsIgnoreCase("filtertest"))
			db.useFilterTest();
		else if (buf.equalsIgnoreCase("sample"))
			db.useSample();
		else if (buf.equalsIgnoreCase("sample3500"))
			db.use3500();
		else if (buf.equalsIgnoreCase("sample1500"))
			db.use1500();
		else if (buf.equalsIgnoreCase("sampletrain"))
			db.useSampleTrain();
		else if (buf.equalsIgnoreCase("sampletest"))
			db.useSampleTest();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#reset()
	 */
	@Override
	public void reset()
	{
		if (db == null)
			db = new ReadGasSpectrometary();
		db.deleteSelected();
		super.reset();
	}

	/**
	 * 
	 */
	private void setDims()
	{
		try
		{
			if (db == null)
				db = new ReadGasSpectrometary();
			inputs = db.getDim();
			if (objFunc != null && objFunc.equalsIgnoreCase("f2"))
				inputs -= (F2_OUTPUTS - 1);
			factor = (inputs > factorMin)
					? (inputs < factorMax) ? inputs : factorMax
					: factorMin;

		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#setMDimension(int)
	 */
	@Override
	public void setMDimension(int dimension) throws Exception
	{
		setDims();
		dimension = inputs * factor;
		super.setMDimension(dimension);
		setRange();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.SVM#setRange()
	 */
	@Override
	protected void setRange()
	{
		mMax = new double[mDimension];
		mMin = new double[mDimension];
		// bit of overkill; needs tailoring for each dimension
		for (int i = 0; i < mDimension; i++)
		{
			mMin[i] = -2;
			mMax[i] = 2;

		}
		super.setRange();
	}
	
}
