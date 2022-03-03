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
	protected ArrayList<Vector<Object>> data = null;
	protected ReadData db = null;
	protected String description = "";
	protected double functEval = 0;
	protected String info = "";
	protected double mBestKnown = Double.NaN;
	protected int mDimension = 10;
	protected double mGlobalOptima = 0;
	protected boolean minimised = true;
	protected double mMax[];
	protected double mMin[];
	protected double mResult = Double.NaN;
	protected Properties params;
	protected ArrayList<Vector<Object>> testdata = null;
	protected double threshold = 1.e-7;
	protected File tmpFile = null;
	protected ArrayList<Vector<Object>> traindata = null;
	private String tmpBuf = null;
	private BufferedWriter writer;

	/**
	 * 
	 */
	public TestBase()
	{
		super();
		params = new Properties();
		createParams();
		// setRange();
		functEval = 0;
	}

	public Object clone()
	{
		try
		{
			TestBase clone = (TestBase) super.clone();
			tmpFile = null;
			return clone;
		} catch (CloneNotSupportedException e)
		{
			throw new InternalError(e.toString());
		}
	}

	/**
	 * 
	 * 
	 */
	protected void appendLocalBuffer(String str, boolean reset)
	{
		if (str == null)
		{
			reset = true;
			str = "";
		}
		if (reset && tmpFile != null)
		{
			try
			{
//				this is done due to problems with FileWriter not clearing the
				// file and effectively appending content when it shouldn't
				RandomAccessFile randomAccessFile = new RandomAccessFile(
						tmpFile, "rw");
				randomAccessFile.setLength(0);
				randomAccessFile.close();
			} catch (FileNotFoundException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			} catch (IOException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			}
		}
		if (Util.checkMemFree() && tmpFile == null)
		{
			if (reset || this.tmpBuf == null)
				this.tmpBuf = new String(str);
			else
				this.tmpBuf = this.tmpBuf.concat("\n" + str);
		} else
			try
			{
				if (tmpFile == null || !tmpFile.exists())
					tmpFile = Util.createTempFile();

				if (tmpFile.exists())
				{

					do
					{
						synchronized (tmpFile)
						{
							writer = new BufferedWriter(
									new FileWriter(tmpFile, !reset));

							writer.append(str);
							writer.newLine();
							writer.flush();
							writer.close();
						}
					} while (writer == null);
					writer = null;
				}
			} catch (IOException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
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
	abstract protected void createParams();

	protected double crossentrophy(Vector<Double> expected,
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
		if (writer != null)
		{
			try
			{
				writer.flush();
				writer.close();
				writer = null;
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (tmpFile != null)
		{
			tmpFile.delete();
			tmpFile = null;
		}
	}

	protected void finalize() throws Throwable
	{
		destroy();
	}

	public String getDescription()
	{
		return description;
	}
	public String getFuncSpecific()
	{

		String funcSpecific = "";
		if (tmpFile != null)
		{
			synchronized (tmpFile)
			{
				BufferedReader reader = null;
				do
				{
					try
					{
						reader = new BufferedReader(new FileReader(tmpFile));
						String line;
						while ((line = reader.readLine()) != null)
							funcSpecific += line;
						reader.close();

					} catch (IOException e)
					{
						Log.log(Level.SEVERE, e);
						e.printStackTrace();
					}
				} while (reader == null);
				reader = null;
			}
		}
		return funcSpecific;
	}
	public synchronized BufferedReader getFuncSpecificReader()
	{
		BufferedReader reader = null;
		if (tmpFile != null)
		{
			synchronized (tmpFile)
			{
				try
				{
					do
					{
						if (tmpFile != null)
							reader = new BufferedReader(
									new FileReader(tmpFile));

					} while (reader == null);

				} catch (IOException e)
				{
					Log.log(Level.SEVERE, e);
					e.printStackTrace();
				}
			}
		}
		return reader;
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
	 * @return the threshold
	 */
	public double getThreshold()
	{
		return threshold;
	}

	abstract public void init();
	/**
	 * @return Returns minimised
	 */
	public boolean isMinimised()
	{
		return minimised;
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
					params.setProperty(key, props.getProperty(key));
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
	 * @return dblScore; evaluated score
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
	 * functions ** This is the co-evolve version
	 * 
	 * @param v1
	 *            , v2; vectors to evaluate
	 * @return dblScore; evaluated score
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

	protected void runTest(Particle gbest)
	{
		gbest = new Particle(gbest);
		gbest.setIdentityNumber(Integer.MIN_VALUE);
		traindata = data;

		try
		{
			if (testdata == null)
			{
				db.useTestData();
				testdata = db.getData();
			}
			data = testdata;
			Objective(gbest);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
//			e.printStackTrace();
		}
		data = traindata;
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
	 * @param funcSpecific
	 *            the funcSpecific to set
	 */
	public void setFuncSpecific(String funcSpecific, boolean reset)
	{
		// this.funcSpecific = new String(funcSpecific);

		try
		{
			if (funcSpecific == null || funcSpecific.equals(""))
				reset = true;
			// this is done due to problems with FileWriter on Windows not
			// clearing the
			// file and effectively appending content when it shouldn't

			if (tmpFile == null)
				tmpFile = Util.createTempFile();
			else if (reset)
			{
//				tmpFile.delete();
//				tmpFile = null;
				RandomAccessFile randomAccessFile = new RandomAccessFile(
						tmpFile, "rw");
				randomAccessFile.setLength(0);
				randomAccessFile.close();
			}
			BufferedWriter writer = new BufferedWriter(
					new FileWriter(tmpFile, true));
			do
			{
				synchronized (tmpFile)
				{
					int index = BUFFER_SIZE;
					do
					{
						writer.append(funcSpecific.substring(
								index - (BUFFER_SIZE),
								((index + BUFFER_SIZE > funcSpecific.length())
										? funcSpecific.length()
										: index)));
						writer.newLine();
						writer.flush();
						index += BUFFER_SIZE;
					} while (index < funcSpecific.length());

					writer.close();
				}
			} while (writer == null);
			writer = null;
		} catch (IOException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

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
	 * @param threshold
	 *            the threshold to set
	 */
	public void setThreshold(double threshold)
	{
		this.threshold = threshold;
	}
	public double sigmod(double beta)
	{
		return (double) 1.0 / (1.0 + Math.exp(-beta));
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
		try
		{
			p.setFuncSpecific("<!--begin objective function data-->", false);
			if (tmpFile != null)
			{
				String line = null;
				FileReader in = new FileReader(tmpFile.getAbsolutePath());
				BufferedReader reader = new BufferedReader(in,
						(int) tmpFile.length());
				do
				{
					line = reader.readLine();
					if (line != null && !"".equals(line))
						p.setFuncSpecific(line, false);
				} while (line != null);

				reader.close();
			} else if (tmpBuf != null && !tmpBuf.toString().isEmpty())
				p.setFuncSpecific(tmpBuf.toString(), false);
			p.setFuncSpecific("<!--end objective function data-->", false);

		} catch (IOException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

	}

}
