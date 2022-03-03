package org.mltestbed.testFunctions.multiModal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.data.readDB.ReadNERC;
import org.mltestbed.testFunctions.PsuedoSVM;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;
import org.mltestbed.util.SimpleRegression;

public class NERCQ95RiverSevern extends PsuedoSVM
{
	private static ReadNERC db = null;
	private static final String FACTOR_MAX = "factorMax";
	private static final String FACTOR_MIN = "factorMin";
	private static final String KEY = "key";

	private static final String PERFORM_TEST = "performTest";
	private LinkedList<ArrayList<Double>> data;
	private int factorMax;
	private String key;
	private boolean useBiggest;

	/**
	 * 
	 */
	public NERCQ95RiverSevern()
	{
		super();
		description = "NERC Q95 River Severn Data";
		info = "Second test function for NERC River Severn Data";
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

		try
		{
			if (db == null)
				db = new ReadNERC(false);
			factor = factorMin < db.getDim() ? db.getDim() : factor;
			setMDimension(db.getDim() * factor);

		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		factorMax = factorMin + 2;
		setDims();

		params.setProperty(KEY, "all");
		params.setProperty(PERFORM_TEST, "Q95Test1");
		// params.setProperty(USE_BIGGEST_DIFF, "false");
		// params.setProperty(RESETDB, "false");
		params.setProperty(FACTOR_MIN, Integer.toString(factorMin));
		params.setProperty(FACTOR_MAX, Integer.toString(factorMax));

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
	 * @see org.mltestbed.testFunctions.SVM#init()
	 */
	@Override
	public void init()
	{
		super.init();

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

		// buf = params.getProperty(RESETDB);
		// if (buf.equalsIgnoreCase("true"))
		// db.deleteSelected();
		String buf = params.getProperty(FACTOR_MIN,
				Integer.toString(factorMin));
		factorMin = Integer.parseInt(buf);
		buf = params.getProperty(FACTOR_MAX, Integer.toString(factorMax));
		factorMax = Integer.parseInt(buf);

		buf = params.getProperty(PERFORM_TEST);
		if (buf.equalsIgnoreCase("Q95Test1"))
			db.useQ95T1();
		else if (buf.equalsIgnoreCase("Q95Test2"))
			db.useQ95T2();
		else if (buf.equalsIgnoreCase("Q95Test3"))
			db.useQ95T3();
		else if (buf.equalsIgnoreCase("FT"))
			db.useFT();
		else if (buf.equalsIgnoreCase("FTTest1"))
		{
			db.useQ95T1();
			buf = db.getSQLString();
			buf = buf.replace("RSevernQ95", "[Full Training Data]");
			db.setSQLString(buf);
		} else if (buf.equalsIgnoreCase("FTTest2"))
		{
			db.useQ95T2();
			buf = db.getSQLString();
			buf = buf.replace("RSevernQ95", "[Full Training Data]");
			db.setSQLString(buf);
		}

		else if (buf.equalsIgnoreCase("TLQ95"))
			db.useTLQ95();
		else if (buf.equalsIgnoreCase("TLQ95Test1"))
		{
			db.useQ95T1();
			buf = db.getSQLString();
			buf = buf.replace("RSevernQ95", "[Training Less Than Q95 Data]");
			db.setSQLString(buf);
		} else if (buf.equalsIgnoreCase("TLQ95Test2"))
		{
			db.useQ95T2();
			buf = db.getSQLString();
			buf = buf.replace("RSevernQ95", "[Training Less Than Q95 Data]");
			db.setSQLString(buf);
		} else if (buf.equalsIgnoreCase("ResidTest1"))
			db.useResidT1();
		else if (buf.equalsIgnoreCase("ResidTest2"))
			db.useResidT2();
		setDims();
		HashMap<String, String> keys = new HashMap<String, String>();
		keys.put("Year", key);
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
		for (Iterator<ArrayList<Double>> iterator = data
				.iterator(); iterator.hasNext();)
		{
			ArrayList<Double> row = (ArrayList<Double>) iterator.next();
			Double expect = row.get(row.size() - 1);
			reqSum += expect;
			double score = super.NERCObjective(p, row);
			double abs = Math.abs(score);
			if (abs > biggest)
				biggest = abs;
			// sum += abs;
			sum += score;
			expected.add(expect);
			double predict = super.getPredicted();
			predicted.add(predict);
			sr.addData(expect, predict);
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
//				mResult = 1 - r2;
		}
		p.setFuncSpecific(
				"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><?xml-stylesheet type=\"text/xsl\" href=\"severnresults.xsl\"?><IterResult><Key>"
						+ key + "</Key>" + toXML(),
				true);
		transBuf(p);
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
				+ "</R2><Result>" + mResult + "</Result></IterResult>",
				false);

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
			db = new ReadNERC(true);
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
				db = new ReadNERC(true);
			int dim = db.getDim();
			factor = (dim > factorMin)
					? (dim < factorMax) ? dim : factorMax
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
			db = new ReadNERC(true);
		super.setMDimension(db.getDim() * factor);
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
			mMin[i] = -10;
			mMax[i] = 10;

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
