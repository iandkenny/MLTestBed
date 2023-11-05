package org.mltestbed.testFunctions.multiModal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.data.readDB.ReadNERC;
import org.mltestbed.testFunctions.IFS;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;

public class IFSGlacierData extends IFS
{
	private static final String KEY = "key";
	private static final String PERFORM_TEST = "performTest";
	private static final String RESETDB = "resetDB";
	private static final String USE_YEAR = "useYear";
	private LinkedList<ArrayList<Double>> data;
	private ReadNERC db = null;
	// private int factorMax;
	private String key;
	private boolean useYear;
	/**
	 * 
	 */
	public IFSGlacierData()
	{
		super();
		description = "Glacier Data - IFS implementation";
		info = "IFS function for Glacier Data";
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
		db = new ReadNERC(false);
		db.useGlacierT1();

		setDims();
		params.setProperty(KEY, "random");
		params.setProperty(PERFORM_TEST, "GlacierTest1");
		params.setProperty(RESETDB, "false");
		params.setProperty(USE_YEAR, "true");
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
			db = new ReadNERC(true);
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

		key = params.getProperty(KEY);
		if (key.equalsIgnoreCase("random"))
		{
			do
			{
				if (useYear)
					key = db.selectRandomKey();
				else
					key = db.selectRandomDay();
			} while (key == null || key.equalsIgnoreCase("random"));
			// params.setProperty(KEY, key);
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
		double sum = 0;
		sum = super.Objective(p, data);
		mResult = Math.sqrt(Math.pow(sum, 2));
		p.setFuncSpecific(
				"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><?xml-stylesheet type=\"text/xsl\" href=\"ifsglacierresults.xsl\"?><IterResult><Key>"
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
		if (buf.equalsIgnoreCase("GlacierTest1"))
			db.useGlacierT1();
		else if (buf.equalsIgnoreCase("GlacierTest2"))
			db.useGlacierT2();
		else if (buf.equalsIgnoreCase("GlacierTest3"))
			db.useGlacierT3();
		else if (buf.equalsIgnoreCase("GlacierTest4"))
			db.useGlacierT4();
		else if (buf.equalsIgnoreCase("GlacierTest4Complete"))
			db.useGlacierT4Complete();
		else if (buf.equalsIgnoreCase("GlacierTest5"))
			db.useGlacierT5();
		else if (buf.equalsIgnoreCase("GlacierTest6"))
			db.useGlacierT6();
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
				db = new ReadNERC(true);
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
		if (db == null)
		{
			db = new ReadNERC(false);
			db.useGlacierT1();
		}
		super.setMDimension(db.getDim());
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
		mMin = new double[mDimension];
		mMax = new double[mDimension];
		// bit of overkill; needs tailoring for each dimension
		for (int i = 0; i < mDimension; i++)
		{
			mMin[i] = -10;
			mMax[i] = 10;

		}
		super.setRange();
	}

}
