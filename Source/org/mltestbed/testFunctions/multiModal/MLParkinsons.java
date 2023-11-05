package org.mltestbed.testFunctions.multiModal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.data.readDB.ReadParkinsons;
import org.mltestbed.testFunctions.PsuedoSVM;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;
import org.mltestbed.util.SimpleRegression;

public class MLParkinsons extends PsuedoSVM
{

	private static final String DATASET = "Dataset";
	private static final String FACTOR_MAX = "FactorMax";
	private static final String FACTOR_MIN = "FactorMin";
	private static final String KEY = "name";
	private static final String USE_FILTER = "useFilter";
	private static final String USE_SUM = "useSum";

	private LinkedList<ArrayList<Double>> data;

	private String dataset;
	private ReadParkinsons db = null;
	private int factorMax;
	private String key;
	private boolean useBiggest;
	private boolean useFilter;

	/**
	 * 
	 */
	public MLParkinsons()
	{
		super();
		description = "Machine Learning: Parkinsons speech Data";
		info = "Predict if Patient has Parkinsons based on speech pattern";
		createParams();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#clone()
	 */
	@Override
	public Object clone()
	{
		// TODO Auto-generated method stub
		return super.clone();
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
		factor = factorMin = 3;
		factorMax = factorMin + 2;
		setDims();

		params.setProperty(FACTOR_MIN, Integer.toString(factorMin));
		params.setProperty(FACTOR_MAX, Integer.toString(5));
		params.setProperty(USE_FILTER, "true");
		params.setProperty(USE_SUM, "false");
		params.setProperty(DATASET, "train");
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
		key = params.getProperty(KEY, "");
		if (key.equalsIgnoreCase("random"))
		{
			do
			{
				key = db.selectRandomKey();
			} while (key == null || key.equalsIgnoreCase("random"));
			// params.setProperty(KEY, key);
		} else if (key.equalsIgnoreCase("all"))
			key = "";
		dataset = params.getProperty(DATASET);
		if (dataset.equalsIgnoreCase("train"))
			db.useTrainData();
		else if (dataset.equalsIgnoreCase("test"))
			db.useTestData();
		else if (dataset.equalsIgnoreCase("train4"))
			db.useTrain4Data();
		else if (dataset.equalsIgnoreCase("test4"))
			db.useTest4Data();
		else if (dataset.equalsIgnoreCase("all"))
			db.useAll();
		if (dataset.equalsIgnoreCase("pdfeaturetrain"))
			db.usePDFeaturesTrain();
		else if (dataset.equalsIgnoreCase("pdfeaturetest"))
			db.usePDFeaturesTest();

		useFilter = Boolean
				.parseBoolean(params.getProperty(USE_FILTER, "true"));
		if (useFilter)
			db.useFiltered();
		useSum = Boolean.parseBoolean(params.getProperty(USE_SUM, "true"));
		String buf = params.getProperty(FACTOR_MIN,
				Integer.toString(factorMin));
		factorMin = Integer.parseInt(buf);
		buf = params.getProperty(FACTOR_MAX, Integer.toString(factorMax));
		factorMax = Integer.parseInt(buf);
		setDims();
		HashMap<String, String> keys = new HashMap<String, String>();
		keys.put("Name", key);
		if (data == null)
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
			double score = super.PDObjective(p, row);
			double abs = Math.abs(score);
			if (abs > biggest)
				biggest = abs;
//				sum += abs;
			sum += score;
			expected.add(expect);
			double predict = super.getPredicted();
			predicted.add(predict);
			count++;
			appendLocalBuffer(p.getFuncSpecific(), false);
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
				"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><?xml-stylesheet type=\"text/xsl\" href=\"parkinsonsresults.xsl\"?><IterResult><Key>"
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

		return super.Objective(p);
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
			db = new ReadParkinsons();
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
				db = new ReadParkinsons();
			inputs = db.getDim();
			factor = (inputs > factorMin)
					? (inputs < factorMax) ? inputs : factorMax
					: factorMin;

			// dim forced inside function
			setMDimension(0);

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
		if (db == null)
			db = new ReadParkinsons();
		if (inputs == 0)
			inputs = db.getDim();
		super.setMDimension(inputs * factor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.SVM#setRange()
	 */
	@Override
	protected void setRange()
	{
		for (int i = 0; i < mDimension; i++)
		{
			mMin[i] = -5;
			mMax[i] = 5;

		}
		super.setRange();
	}

	protected double RMSE(Vector<Double> expected, Vector<Double> predicted)
	{
		double result = 0;
		int size = expected.size();
		if (size != predicted.size())
			return Double.NaN;
		for (int i = 0; i < size; i++)
		{
			double expect = (Double) expected.get(i);
			double pred = (Double) predicted.get(i);
			result += Math.pow(expect - pred, 2);
		}
		if (size > 0)
			return Math.sqrt(result / (size - 1));
		else
			return Double.NaN;
	}

}
