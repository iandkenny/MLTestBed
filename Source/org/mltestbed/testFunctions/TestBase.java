/*
 * Created on 13-Mar-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mltestbed.testFunctions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.data.ReadData;
import org.mltestbed.util.Log;
import org.mltestbed.util.MemoryBufferedFile;
import org.mltestbed.util.Particle;
import org.mltestbed.util.Util;

/**
 * @author Ian Kenny
 * 
 *         Basic abstract class defining a test function
 * 
 */

public abstract class TestBase implements Cloneable
{
	/**
	 * 
	 */
	private static final int BUFFER_SIZE = 4096;
	private static final String RUN_TEST = "runTest";
	private static final String USE_CROSS_ENTROPHY = "useCrossEntrophy";
	protected ArrayList<Vector<Object>> baseData = null;
	protected ReadData db = null;
	protected String description = "";
	private boolean entrophy = false;
	protected double functEval = 0;
	protected String info = "";
	private MemoryBufferedFile mb = null;
	protected double mBestKnown = Double.NaN;
	protected int mDimension = 10;
	protected double mGlobalOptima = 0;
	protected boolean minimised = true;
	protected double mMax[];
	protected double mMin[];
	protected double mResult = Double.NaN;
	protected Properties params;
	private boolean test = false;
	protected ArrayList<Vector<Object>> testdata = null;
	protected double threshold = 1.e-7;
	protected ArrayList<Vector<Object>> traindata = null;
	private boolean validate = false;
	/**
	 * 
	 */
	public TestBase()
	{
		super();
		params = new Properties();
		mb = new MemoryBufferedFile();
		createParams();
		// setRange();
		functEval = 0;
	}
	/**
	 * 
	 * 
	 */
	protected void appendLocalBuffer(String str, boolean reset)
	{
		mb.write(str, reset);
	}
	/**
	 * @param expected
	 * @param predicted
	 * @return
	 */
	protected double calcError(double expected, double predicted)
	{
		double result = Double.NaN;
		if (isEntrophy())
			result = -(expected * Math.log(predicted)
					+ ((1 - expected) * Math.log(predicted)));
		else
			result = Math.sqrt(Math.pow((expected - predicted), 2));
		return result;
	}
	public Object clone()
	{
		try
		{
			TestBase clone = (TestBase) super.clone();
			mb = new MemoryBufferedFile();
			return clone;
		} catch (CloneNotSupportedException e)
		{
			throw new InternalError(e.toString());
		}
	}

	/**
	 * @return success or failure
	 * @throws Exception
	 */
	protected boolean constraints() throws Exception
	{
		return true;
	}
	/**
	 * 
	 */
	protected void createParams()
	{
		if (params == null)
			params = new Properties();
		params.setProperty(USE_CROSS_ENTROPHY, "false");
		setEntrophy(false);
		params.setProperty(RUN_TEST, "false");
		setTest(false);
	}

	protected double crossEntrophy(Vector<Double> expected,
			Vector<Double> predicted)
	{
		double result = 0;
		int size = expected.size();
		if (size != predicted.size())
			return Double.NaN;
		for (int i = 0; i < size; i++)
		{
			double expect = (Double) expected.get(i);
			double pred = (Double) predicted.get(i);
			result += -(expect * Math.log(pred)
					+ ((1 - expect) * Math.log(pred)));
		}
		return result;
	}

	public synchronized void destroy()
	{

		if (mb != null)
		{
			mb.destroy();
			mb = null;
		}
	}

	protected void finalize() throws Throwable
	{
		destroy();
	}
	/**
	 * @return the baseData
	 */
	public ArrayList<Vector<Object>> getBaseData()
	{
		return baseData;
	}

	/**
	 * @return the db
	 */
	public ReadData getDb()
	{
		return db;
	}

	public String getDescription()
	{
		return description;
	}

	public String getFuncSpecific()
	{
		return mb.read();
	}
	public synchronized BufferedReader getFuncSpecificReader()
	{
		return mb.getReader();
	}

	/**
	 * @return Returns the functEval.
	 */
	public double getFunctEval()
	{
		return functEval;
	}

	public String getInfo()
	{
		return info;
	}

	/**
	 * @return key property
	 */
	public String getKey()
	{
		return params.getProperty("key", null);
	}

	/**
	 * @return Returns the mMax for the ith dimension.
	 */
	public double getMax(int i)
	{
		Double d = null;
		try
		{
			if (mMax != null)
				d = Double.valueOf(mMax[i]);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("i=" + i + " mMax size=" + mMax.length, e));
			e.printStackTrace();
		}
		return (d == null) ? Double.NaN : d.doubleValue();

	}
	/**
	 * @return Returns the mBestKnown.
	 */
	public double getMBestKnown()
	{
		return mBestKnown;
	}
	/**
	 * @return Returns the mDimension.
	 */
	public int getMDimension()
	{
		return mDimension;
	}

	/**
	 * @return the mGlobalOptima
	 */
	public double getMGlobalOptima()
	{
		return mGlobalOptima;
	}
	/**
	 * @return Returns the mMin for the ith dimension.
	 */
	public double getMin(int i)
	{
		Double d = null;
		try
		{
			if (mMin != null)
				d = Double.valueOf(mMin[i]);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("i=" + i + " mMax size=" + mMax.length, e));
			e.printStackTrace();

		}
		return (d == null) ? Double.NaN : d.doubleValue();
	}
	public int getNumKeys()
	{
		return 0;
	}

	/**
	 * @return the params
	 */
	public Properties getParams()
	{
		return params;
	}
	/**
	 * @return the testdata
	 */
	public ArrayList<Vector<Object>> getTestdata()
	{
		return testdata;
	}
	/**
	 * @return the threshold
	 */
	public double getThreshold()
	{
		return threshold;
	}

	/**
	 * @return the traindata
	 */
	public ArrayList<Vector<Object>> getTraindata()
	{
		return traindata;
	}
	public void init()
	{
		String buf = params.getProperty(USE_CROSS_ENTROPHY, "false");
		setEntrophy(buf.equalsIgnoreCase("true"));
		buf = params.getProperty(RUN_TEST, "false");
		setTest(buf.equalsIgnoreCase("true"));

	}

	/**
	 * @return the entrophy
	 */
	public boolean isEntrophy()
	{
		return entrophy;
	}
	/**
	 * @return Returns minimised
	 */
	public boolean isMinimised()
	{
		return minimised;
	}
	/**
	 * @return the test
	 */
	public boolean isTest()
	{
		return test;
	}

	/**
	 * @return the validate
	 */
	public boolean isValidate()
	{
		return validate;
	}
	public void load(Properties props)
	{
		if (props != null)
		{
			params.clear();
			createParams();
			Enumeration<Object> keys = params.keys();
			while (keys.hasMoreElements())
			{
				String key = (String) keys.nextElement();
				if (props.containsKey(key))
					params.setProperty(key,
							props.getProperty(key, params.getProperty(key)));
			}
		}
	}
	/*
	 * Mean Absolute Error
	 */
	protected double MAE(Vector<Double> expected, Vector<Double> predicted)
	{
		double result = 0;
		int size = expected.size();
		if (size != predicted.size())
			return Double.NaN;
		for (int i = 0; i < size; i++)
		{
			double expect = (Double) expected.get(i);
			double pred = (Double) predicted.get(i);
			result += Math.abs(expect - pred);
		}
		if (size > 0)
			return ((double) 1 / size) * result;
		else
			return Double.NaN;
	}
	public Particle max(ArrayList<Particle> arrayList)
	{
		Particle maxParticle = null;
		if (arrayList != null)
		{
			try
			{
				maxParticle = arrayList.get(0);
				for (Iterator<Particle> iter = arrayList.iterator(); iter
						.hasNext();)
				{
					Particle element = (Particle) iter.next();
					if (element.getBestScore() > maxParticle.getBestScore())
						maxParticle = element;

				}
			} catch (Exception e)
			{
				if (arrayList.size() == 0)
					System.err.println(
							"There are no particles in the neighbourhood");
				else
				{
					Log.log(Level.SEVERE, e);
					e.printStackTrace();
				}

			}
		}
		return maxParticle;
	}
	/*
	 * Mean Error
	 */
	protected double ME(Vector<Double> expected, Vector<Double> predicted)
	{
		double result = 0;
		int size = expected.size();
		if (size != predicted.size())
			return Double.NaN;
		for (int i = 0; i < size; i++)
		{
			result += (expected.get(i) - predicted.get(i));
		}
		if (size > 0)
			return ((double) 1 / size) * result;
		else
			return Double.NaN;
	}
	public Particle min(ArrayList<Particle> arrayList)
	{
		Particle minParticle = null;
		if (arrayList != null)
		{
			try
			{
				minParticle = arrayList.get(0);
				for (Iterator<Particle> iter = arrayList.iterator(); iter
						.hasNext();)
				{
					Particle element = (Particle) iter.next();
					if (element.getBestScore() < minParticle.getBestScore())
						minParticle = element;

				}
			} catch (Exception e)
			{
				if (arrayList.isEmpty())
					System.err.println(
							"There are no particles in the neighbourhood");
				else
				{
					Log.log(Level.SEVERE, e);
					e.printStackTrace();
				}
			}
		}
		return minParticle;
	}

	/**
	 * Base Objective function ** this must be called by inheriting objective
	 * functions ** ** must be coded as reentrant
	 * 
	 * @param particle
	 *            ; vector to evaluate
	 * @return mResult; evaluated score
	 */
	public double Objective(Particle particle)
	{
		functEval++;
		if (Double.isNaN(mBestKnown))
			mBestKnown = mResult;
		if (minimised)
		{
			if (mBestKnown > mResult)
				mBestKnown = mResult;
		} else
		{
			if (mBestKnown < mResult)
				mBestKnown = mResult;
		}
		return mResult;
	}

	/**
	 * Base Objective function ** this must be called by inheriting objective
	 * functions ** ** must be coded as reentrant
	 * 
	 * @param v
	 *            ; vector to evaluate
	 * @return mResult; evaluated score
	 */
	public double Objective(Vector<Double> v)
	{
		functEval++;
		if (Double.isNaN(mBestKnown))
			mBestKnown = mResult;
		if (minimised)
		{
			if (mBestKnown > mResult)
				mBestKnown = mResult;
		} else
		{
			if (mBestKnown < mResult)
				mBestKnown = mResult;
		}
		return mResult;
	}
	/**
	 * Base Objective function ** this must be called by inheriting objective
	 * functions ** This is the co-evolve version
	 * 
	 * @param v1
	 *            , v2; vectors to evaluate
	 * @return mResult; evaluated score
	 */
	protected double Objective(Vector<Double> v1, Vector<Double> v2)
	{
		functEval++;
		if (mBestKnown == Double.NaN)
			mBestKnown = mResult;
		if (minimised)
		{
			if (mBestKnown > mResult)
				mBestKnown = mResult;
		} else
		{
			if (mBestKnown < mResult)
				mBestKnown = mResult;
		}
		return mResult;
	}
	/*
	 * Relative Absolute Error
	 */
	protected double RAE(Vector<Double> expected, Vector<Double> predicted)
	{
		double sum1 = 0;
		double sum2 = 0;
		int size = expected.size();
		double avg = sum(expected) / (size - 1);
		if (size != predicted.size())
			return Double.NaN;
		for (int i = 0; i < size; i++)
		{
			double expect = (Double) expected.get(i);
			double pred = (Double) predicted.get(i);
			sum1 += Math.abs(expect - pred);
			sum2 += Math.abs(expect - avg);
		}
		if (sum1 != 0 && sum2 != 0)
			return (sum1 / sum2);
		else
			return Double.NaN;
	}

	public double relu(Vector<Double> weights, Vector<Double> inputs)
	{
		double weightedSum = 0;
		if (weights.size() == inputs.size())
		{
			for (int i = 0; i < inputs.size(); i++)
			{
				weightedSum += inputs.get(i) * weights.get(i);

			}
			return Math.max(0, weightedSum);
		} else
			return Double.NaN;
	}
	public void reset()
	{

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
	public Particle doTest(Particle gb)
	{
		Particle gbest = new Particle(gb);
		gbest.setFuncSpecific("");
		gbest.setIdentityNumber(-gbest.getIdentityNumber());
		try
		{
			if (testdata == null)
			{
				db.useTestData();
				testdata = db.getData();
			}
			if (!testdata.isEmpty())
			{
				gbest.setPosition(gbest.getPbest());
				TestBase testFunction = gbest.getTestFunction();
				traindata = testFunction.getBaseData();
				testFunction.setBaseData(testdata);
				gbest.eval();
				gbest.setBestScore(gbest.getCurrentScore()); // since this is on new data
				testFunction.setBaseData(traindata);
				gb.eval(); // evaluate best on training data for the test iteration
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
//			e.printStackTrace();
		}
		return gbest;
	}
	public void runTest(Vector<Double> v)
	{

	}
	/**
	 * @param baseData the baseData to set
	 */
	public void setBaseData(ArrayList<Vector<Object>> baseData)
	{
		this.baseData = baseData;
	}
	/**
	 * @param db
	 *            the db to set
	 */
	public void setDb(ReadData db)
	{
		this.db = db;
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	protected void setDescription(String description)
	{
		this.description = description;
	}
	/**
	 * @param entrophy
	 *            the entrophy to set
	 */
	public void setEntrophy(boolean entrophy)
	{
		this.entrophy = entrophy;
	}

	/**
	 * @param funcSpecific
	 *            the funcSpecific to set
	 */
	public void setFuncSpecific(String funcSpecific, boolean reset)
	{
		mb.write(funcSpecific, reset);
	}
	/**
	 * @param info
	 *            The info to set.
	 */
	protected void setInfo(String info)
	{
		this.info = info;
	}
	/**
	 * @param bestKnown
	 *            the mBestKnown to set
	 */
	public void setMBestKnown(double bestKnown)
	{
		mBestKnown = bestKnown;
	}

	/**
	 * @param dimension
	 * @throws Exception
	 */
	public void setMDimension(int dimension) throws Exception
	{
		if (dimension < 1)
			throw new Exception("Dimensions must be >0");
		else
		{
			mDimension = dimension;
			params.setProperty("dimensions", Integer.toString(mDimension));
			mMax = new double[mDimension];
			mMin = new double[mDimension];
			setRange();
		}
	}
	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(Properties params)
	{

		this.params = params;
		try
		{
			setMDimension(
					Integer.parseInt(params.getProperty("dimensions", "1")));
		} catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	abstract protected void setRange();
	/**
	 * @param test
	 *            the test to set
	 */
	public void setTest(boolean test)
	{
		this.test = test;
	}

	/**
	 * @param testdata the testdata to set
	 */
	public void setTestData(ArrayList<Vector<Object>> testdata)
	{
		this.testdata = testdata;
	}
	/**
	 * @param threshold
	 *            the threshold to set
	 */
	public void setThreshold(double threshold)
	{
		this.threshold = threshold;
	}
	/**
	 * @param traindata the traindata to set
	 */
	public void setTraindata(ArrayList<Vector<Object>> traindata)
	{
		this.traindata = traindata;
	}
	/**
	 * @param validate
	 *            the validate to set
	 */
	public void setValidate(boolean validate)
	{
		this.validate = validate;
	}
	public double sigmod(double beta)
	{
		return (double) 1.0 / (1.0 + Math.exp(-beta));
	}

	public Vector<Double> SoftMax(Vector<Double> input)
	{
		Vector<Double> exp = new Vector<Double>();
		double sum = 0;
		double max = 0; // for numerical constancy
		for (int index = 0; index < input.size(); index++)
			max = input.get(index) > max ? input.get(index) : max;

		for (int index = 0; index < input.size(); index++)
		{
			exp.set(index, Math.exp(input.get(index) - max));
			sum += exp.get(index);
		}

		Vector<Double> output = new Vector<Double>();
		for (int index = 0; index < exp.size(); index++)
		{
			output.set(index, exp.get(index) / sum);
		}

		return output;
	}
	public Vector<Double> SoftMaxDerivative(Vector<Double> input)
	{
		Vector<Double> softmax = SoftMax(input);

		Vector<Double> output = new Vector<Double>();
		for (int index = 0; index < input.size(); index++)
		{
			output.set(index, softmax.get(index) * (1d - softmax.get(index)));
		}

		return output;
	}
	protected double StdDev(Vector<Double> elements)
	{
		double deviation = 0;
		double sum = 0;
		for (Iterator<Double> iterator = elements.iterator(); iterator
				.hasNext();)
		{
			double element = (Double) iterator.next();
			sum += element;
		}

		double average = sum / elements.size();
		sum = 0;
		for (Iterator<Double> iterator = elements.iterator(); iterator
				.hasNext();)
		{
			double element = (Double) iterator.next();
			sum += Math.pow((element - average), 2);

		}

		deviation = Math.sqrt(sum / (elements.size()));

		return deviation;
	}
	/*
	 * Vector sum
	 */
	protected double sum(Vector<Double> x)
	{
		double sum = 0;
		if (x != null)
		{
			for (Iterator<Double> iterator = x.iterator(); iterator.hasNext();)
			{
				sum += (Double) iterator.next();

			}
		}
		return sum;
	}
	public double threshold()
	{
		return threshold - mGlobalOptima;
	}
	protected void transBuf(Particle p)
	{
		p.setFuncSpecific("<!--begin objective function data-->", false);
		p.setFuncSpecific(mb.read(), false);
		p.setFuncSpecific("<!--end objective function data-->", false);

	}

}
