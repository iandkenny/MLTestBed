package org.mltestbed.testFunctions.multiModal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.data.readDB.ReadIEA;
import org.mltestbed.testFunctions.PsuedoSVM;
import org.mltestbed.testFunctions.TestBase;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;
import org.mltestbed.util.SimpleRegression;

public class IEASurveys extends PsuedoSVM implements Cloneable
{
	private static final String FACTOR_MAX = "factorMax";
	private static final String FACTOR_MIN = "factorMin";
	private static final String KEY = "key";
	private static final String PERFORM_TEST = "performTest";
	private static final String RESETDB = "resetDB";

	private static final String USE_SUM = "useSum";
	private LinkedList<ArrayList<Double>> data = null;
	private ReadIEA db = null;
	private int factorMax;
	private String key;
	private boolean useBiggest;
	/**
	* 
	*/
	public IEASurveys()
	{
		super();
		description = "Prolific & nQuire Data";
		info = "First test function for Prolific & nQuire IEA Data";
		minimised = true;
		createParams();
		db = new ReadIEA();
		super.setDb(db);
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
		if (data != null)
//			baseData = new LinkedList<ArrayList<Double>>(baseData);
			data = ((IEASurveys) clone).getData(); // this shouldn't be
		// necessary
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
		factor = factorMin = 3;
		factorMax = factorMin + 2;
		setDims();
		db.useTrainData();

		params.setProperty(KEY, "all");
		params.setProperty(PERFORM_TEST, "Train");
		params.setProperty(RESETDB, "false");
		params.setProperty(FACTOR_MIN, Integer.toString(factorMin));
		params.setProperty(FACTOR_MAX, Integer.toString(factorMax));
		params.setProperty(USE_SUM, "true");
	}

	@Override
	protected void finalize() throws Throwable
	{
		destroy();
		super.finalize();
	}

	private LinkedList<ArrayList<Double>> getData()
	{
		return data;
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
			db = new ReadIEA();
		performTest();
		Vector<String> keys;

		keys = db.getKeys();
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
		String buf;
		performTest();
		buf = params.getProperty(RESETDB);
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

		if (data == null)
		{
			HashMap<String, String> keys = new HashMap<String, String>();
			if (!key.equals(""))
				keys.put("Key", key);
			data = db.getData(keys);
		}

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
		SimpleRegression sr = new SimpleRegression();
		double reqSum = 0;
		double sum = 0;
		long count = 0;
		double biggest = 0;
		Vector<Double> expected = new Vector<Double>();
		Vector<Double> predicted = new Vector<Double>();
		appendLocalBuffer(null, true);
		appendLocalBuffer(
				"<SQL>" + db.getSQLString() + "</SQL>" + p.getFuncSpecific(),
				false);

		for (Iterator<ArrayList<Double>> iterator = data.iterator(); iterator
				.hasNext();)
		{
			ArrayList<Double> row = iterator.next();
			double expect = row.get(row.size() - 1);
			reqSum += expect;
			double score = super.genericObjective(p, row);
			double predict = Math.round(super.getPredicted());

			appendLocalBuffer(p.getFuncSpecific(), false);
			double abs = Math.abs(score);
			if (isUseAbs())
				score = abs;
			if (abs > biggest)
				biggest = abs;
			// sum += abs;
			sum += score;
			expected.add(expect);

			predicted.add(predict);
			sr.addData(expect, predict);
			count++;
			// super.setFuncSpecific(null);
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
//			mResult = Math.round(sum);
			// RSME
			// mResult = sum / count;
			// reciprocal R squared
			// mResult = 1 - r2;

		}
		// setFuncSpecific(null);
		p.setFuncSpecific(
				"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><?xml-stylesheet type=\"text/xsl\" href=\"IEAresults.xsl\"?><IterResult>",
				true);
		p.setFuncSpecific("<Key>" + key + "</Key>" + toXML(), false);
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

	/**
	 * 
	 */
	private void performTest()
	{
		String buf = params.getProperty(PERFORM_TEST);
		if (buf.equalsIgnoreCase("nQuireTrain"))
			db.usenQuireTrainData();
		else if (buf.equalsIgnoreCase("nQuireTest"))
			db.usenQuireTestData();
		else if (buf.equalsIgnoreCase("Train"))
			db.useTrainData();
		else if (buf.equalsIgnoreCase("Test"))
			db.useTestData();
		else if (buf.equalsIgnoreCase("All"))
			db.useAllData();

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
			db = new ReadIEA();
		db.deleteSelected();
		super.reset();
	}
	@Override
	public Particle doTest(Particle gb)
	{
		Particle gbest = new Particle(gb);
		gbest.setFuncSpecific("");
		gbest.setIdentityNumber(-gbest.getIdentityNumber());
		HashMap<String, String> keys = new HashMap<String, String>();
		if (!key.equals(""))
			keys.put("Key", key);
		IEASurveys gbtest = (IEASurveys) gbest.getTestFunction();
		LinkedList<ArrayList<Double>> olddata = gbtest.getData();
		String buf = params.getProperty(PERFORM_TEST);
		if (buf.toLowerCase().startsWith("nquire"))
			db.usenQuireTestData();
		else
			db.useTestData();
		LinkedList<ArrayList<Double>> testdata = db.getData(keys);
		gbtest.setData(testdata);
//		gbest.setCurrentScore(gbtest.Objective(gbest));
		gbest.eval();
		gbest.setBestScore(gbest.getCurrentScore());
		gbtest.setData(olddata);
		return gbest;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(LinkedList<ArrayList<Double>> data)
	{
		this.data = data;
	}

	/**
	 * 
	 */
	private void setDims()
	{
		try
		{
			if (db == null)
				db = new ReadIEA();
			inputs = db.getDim();
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
			mMin[i] = -9;
			mMax[i] = 9;

		}
		super.setRange();
	}

}
