package org.mltestbed.testFunctions.multiModal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.data.readDB.ReadMinas;
import org.mltestbed.testFunctions.IFS;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;

public class IFSMinasData extends IFS
{
	private static final String DAY = "day";
	private static final String PERFORM_TEST = "performTest";
	private static final String RESETDB = "resetDB";
	private static final String USE_YEAR = "useYear";
	private LinkedList<ArrayList<Double>> data;
	private ReadMinas db = null;
	// private int factorMax;
	private String key;
	private boolean useYear;
	/**
	 * 
	 */
	public IFSMinasData()
	{
		super();
		description = "Minas Passage Data - IFS implementation";
		info = "IFS function for Minas Passage Data";
		minimised = true;
//		createParams();
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
		super.createParams();;
		try
		{
			db = new ReadMinas();
			db.useMinasT1();

			setMDimension(0);
			params.setProperty(DAY, "random");
			params.setProperty(PERFORM_TEST, "MinasTest1");
			params.setProperty(RESETDB, "false");
			params.setProperty(USE_YEAR, "true");
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}
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
			db = new ReadMinas();
		Vector<String> keys = db.getDays();
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

		key = params.getProperty(DAY);
		if (key.equalsIgnoreCase("random"))
		{
			do
			{
				if (useYear)
					key = db.selectRandomKey();
				else
					key = db.selectRandomDay();
			} while (key == null || key.equalsIgnoreCase("random"));
			// params.setProperty(DAY, key);
		} else if (key.equalsIgnoreCase("all"))
			key = "";
		// String buf = params.getProperty(USE_BIGGEST_DIFF);
		// useBiggest = (buf.equalsIgnoreCase("true")) ? true : false
		buf = params.getProperty(RESETDB);
		if (buf.equalsIgnoreCase("true"))
			db.deleteSelectedDays();
		// buf = params.getProperty(FACTOR_MIN, Integer.toString(factorMin));
		// factorMin = Integer.parseInt(buf);
		// buf = params.getProperty(FACTOR_MAX, Integer.toString(factorMax));
		// factorMax = Integer.parseInt(buf);
		setDims();
		if (data == null)
		{
			HashMap<String, String> keys = new HashMap<String, String>();
			if (!key.equals(""))
				if (useYear)
					keys.put("Year", key);
				else
					keys.put("Date", key);
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
		double sum = Double.NaN;
		sum = super.Objective(p, data);
		mResult = Math.sqrt(Math.pow(sum, 2));
		p.setFuncSpecific(
				"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><?xml-stylesheet type=\"text/xsl\" href=\"ifs.xsl\"?><IterResult><Key>"
						+ key + "</Key>" + toXML() + p.getFuncSpecific()
						+ "<AvgDiff>"
						+ ((sum == 0) ? "NA" : (sum / data.size()))
						+ "</AvgDiff></IterResult>",
				true);

		return super.Objective(p);
	}

	/**
	 * 
	 */
	public void performTest()
	{
		useYear = Boolean.parseBoolean(params.getProperty(USE_YEAR, "false"));
		String buf = params.getProperty(PERFORM_TEST);
		if (buf.equalsIgnoreCase("MinasTest1"))
			db.useMinasT1();
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
			db = new ReadMinas();
		db.deleteSelectedDays();
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
				db = new ReadMinas();
			inputs = db.getDim();
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
		try
		{
			if (db == null)
				db = new ReadMinas();
			inputs = db.getDim();
			super.setMDimension(inputs);
			setRange();
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.SVM#setRange()
	 */
	@Override
	protected void setRange()
	{
		mMin = new double[mDimension];
		mMax = new double[mDimension];
		// bit of overkill; needs tailoring for each dimension
		// the super function adjusts the first index in every function
		// for probability
		for (int i = 0; i < mDimension; i++)
		{
			mMin[i] = -10;
			mMax[i] = 10;

		}
		super.setRange();
	}

}
