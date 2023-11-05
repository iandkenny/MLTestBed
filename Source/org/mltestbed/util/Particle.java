/*
 * Created on 12-Mar-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mltestbed.util;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.testFunctions.TestBase;

/**
 * @author Ian Kenny
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class Particle implements Comparable<Particle>, Cloneable, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String[] getSupportedVelocityInit()
	{
		String buf[] =
		{"zero", "position", "random"};
		return buf;
	}
	private double avgchangesum = 0;
	private double bestScore = Double.NaN;
	private double currentScore = Double.NaN;
	private int identityNumber;
	private long mDimension;
	private Vector<Double> mPosition;
	private Vector<Double> mPreviousPosition;
	private Vector<Double> mVelocity;
	private Properties params;
	private Vector<Double> pbest;
	private long previousCount = 0;
	private double previousScore = Double.NaN;
	private TestBase testFunction;
	private MemoryBufferedFile mb = new MemoryBufferedFile();

	public Particle(Particle o)
	{
		super();
		copy(o);
	}

	/**
	 * @param o
	 */
	public void copy(Particle o)
	{
		this.bestScore = o.bestScore;
		this.currentScore = o.currentScore;
		this.avgchangesum = o.avgchangesum;
		this.previousCount = o.previousCount;
		this.identityNumber = o.identityNumber;
		this.mDimension = o.mDimension;
		this.mPosition = new Vector<Double>(o.mPosition);
		this.mPreviousPosition = new Vector<Double>(o.mPreviousPosition);
		this.mVelocity = new Vector<Double>(o.mVelocity);
		this.params = new Properties(o.params);
		this.pbest = new Vector<Double>(o.pbest);
		this.testFunction = (TestBase) o.testFunction.clone();
		mb.write(o.retrieveBuffer());
	}

	/**
	 *  
	 */
	public Particle(Properties params, TestBase T, Random random,
			int identityNumber)
	{
		try
		{
			this.params = params;
			testFunction = (TestBase) T.clone();
			// testFunction = T;
			mDimension = testFunction.getMDimension();
			bestScore = (testFunction.isMinimised())
					? Double.MAX_VALUE
					: Double.MIN_VALUE;
			currentScore = (testFunction.isMinimised())
					? Double.MAX_VALUE
					: Double.MIN_VALUE;
			mPosition = init(testFunction, random);
			pbest = new Vector<Double>(mPosition);
			mPreviousPosition = new Vector<Double>(mPosition);
			this.identityNumber = identityNumber;
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, new Exception(
					"Particle Initialisation failed: " + e.getMessage(), e));
			// e.printStackTrace();
		}
	}

	/**
	 * @return average rate of change in particle's score
	 */
	public double absAvgRateChange()
	{
		double d = Double.MAX_VALUE;

		if (previousScore != Double.NaN)
		{
			avgchangesum += Math
					.sqrt(Math.pow(previousScore - currentScore, 2));
			if (previousCount > 100)
				d = avgchangesum / previousCount;
			previousCount++;
		}

		return d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		Object clone;
		try
		{
			clone = super.clone();
			mb = new MemoryBufferedFile();
			return clone;
		} catch (Exception e)
		{
			// This should never happen
			throw new InternalError(e.toString());
		}
	}

	public int compareTo(Particle comp)
	{
		return (int) Math.round((comp.getBestScore() - getBestScore()));
	}

	/**
	 * 
	 */
	public synchronized void destroy()
	{
		if (mb != null)
			mb.destroy();
		if (testFunction != null)
			testFunction.destroy();
	}

	public double eval()
	{
		double result = Double.NaN;

		result = testFunction.Objective(this);
		// setFuncSpecific(testFunction.getFuncSpecific());
		setCurrentScore(result);

		if (testFunction.isMinimised())
		{
			if (result < bestScore)
			{

				setPbest(result);
			}
		} else
		{
			if (result > bestScore)
			{
				setPbest(result);

			}
		}
		return result;

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable
	{
		destroy();
//		super.finalize();
	}

	/**
	 * @return Returns current best score
	 */
	public double getBestScore()
	{
		return bestScore;
	}
	/**
	 * @return the currentScore
	 */
	public double getCurrentScore()
	{
		return currentScore;
	}
	/**
	 * @return Returns the history.
	 */
	// public LinkedList<Vector<Double>> getHistory()
	// {
	// return history;
	// }
	/**
	 * @return Returns the mDimension.
	 */
	public long getDimension()
	{
		return mDimension;
	}
	/**
	 * @return the funcSpecific
	 */
	public String getFuncSpecific()
	{
		return mb.read();
	}
	public BufferedReader getFuncSpecificReader()
	{

		return mb.getReader();
	}
	/**
	 * @return the identityNumber
	 */
	public int getIdentityNumber()
	{
		return identityNumber;
	}
	/**
	 * @return Returns the pbest.
	 */
	public Vector<Double> getPbest()
	{
		return pbest;
	}
	/**
	 * @return Returns the mPosition.
	 */
	public Vector<Double> getPosition()
	{
		return mPosition;
	}
	public Vector<Double> getPreviousPosition()
	{
		return mPreviousPosition;
	}

	/**
	 * @return the testFunction
	 */
	public TestBase getTestFunction()
	{
		return testFunction;
	}

	/**
	 * @return Returns the mVelocity.
	 */
	public Vector<Double> getVelocity()
	{
		return mVelocity;
	}
	private Vector<Double> init(TestBase T, Random random)
	{
		if (mb != null)
		{
			mb.destroy();
		}
		// Random random = new Random();
		mPosition = new Vector<Double>();
		mVelocity = new Vector<Double>();
		for (int i = 0; i < mDimension; i++)
		{
			mPosition.add(i, randInit(T, random, i));
			initVelocity(T, random, i);

		}
		return mPosition;
	}
	/**
	 * @param T
	 * @param random
	 * @param i
	 */
	private void initVelocity(TestBase T, Random random, int i)
	{
		String initVelType = params.getProperty("initveltype", "zero");
		if (initVelType.compareToIgnoreCase("random") == 0)
			mVelocity.add(i, randInit(T, random, i));
		else if (initVelType.compareToIgnoreCase("position") == 0)
			mVelocity.add(i, mPosition.get(i));
		else
			mVelocity.add(i, 0.0);
	}

	/**
	 * @param T
	 * @param random
	 * @param i
	 * @return random double in range
	 */
	private double randInit(TestBase T, Random random, int i)
	{
		double abs = Math.abs(T.getMin(i));
		return (abs + T.getMax(i) * random.nextDouble()) - abs;
		// the initialise below puts all the particles in positive space
		// return (T.getMMin(i) + (T.getMMax(i) - T.getMMin(i)))
		// * random.nextDouble();
	}
	public double reEvalBest()
	{
		if (pbest != null)
		{
			bestScore = testFunction.Objective(this);
			// setFuncSpecific(p.getFuncSpecific());
		}
		return bestScore;
	}
	public String retrieveBuffer()
	{

		return mb.retrieveBuffer();
	}
	/**
	 * @param currentScore
	 *            the currentScore to set
	 */
	public void setCurrentScore(double currentScore)
	{
		this.previousScore = this.currentScore;
		this.currentScore = currentScore;
//		if (Double.isNaN(this.currentScore) && Double.isNaN(this.previousScore))
//			synchronized (mPosition)
//			{
//				mPosition = init(testFunction, RandGen.getLastCreated());
//			}
	}

	/**
	 * @param mVelocity
	 *            the mVelocity to set
	 */
	public void setmVelocity(Vector<Double> mVelocity)
	{
		this.mVelocity = mVelocity;
	}

	/**
	 * @param bestScore
	 *            the bestScore to set
	 */
	public void setBestScore(double bestScore)
	{
		this.bestScore = bestScore;
	}

	/**
	 * @param dimension
	 *            The mDimension to set.
	 */
	public void setDimension(int dimension)
	{
		mDimension = dimension;
	}
	public void setFuncSpecific(BufferedReader funcSpecific)
	{
		mb.write(funcSpecific);
	}

	public void setFuncSpecific(String funcSpecific)
	{
		mb.write(funcSpecific);
	}
	public void setFuncSpecific(String funcSpecific, boolean reset)
	{
		mb.write(funcSpecific, reset);
	}
	/**
	 * @param identityNumber
	 *            the identityNumber to set
	 */
	public void setIdentityNumber(int identityNumber)
	{
		this.identityNumber = identityNumber;
	}
	/**
	 * 
	 */
	public void setPbest(double score)
	{
		bestScore = score;
		this.pbest = new Vector<Double>(mPosition);
	}

	public void setPosition(String position)
	{
		try
		{
			mPreviousPosition = mPosition;
			mPosition = new Vector<Double>();
			position = position.replace("[", "");
			position = position.replace("]", "");
			String[] a = position.split(",");
			for (int i = 0; i < a.length; i++)
			{
				mPosition.add(Double.valueOf(a[i]));
			}
		} catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param position
	 *            The mPosition to set.
	 */
	public void setPosition(Vector<Double> position)
	{
		mPreviousPosition = mPosition;
		mPosition = position;
	}
	/**
	 * @param testFunction
	 *            the testFunction to set
	 */
	public void setTestFunction(TestBase testFunction)
	{
		this.testFunction = testFunction;
	}

	public void setVelocity(String velocity)
	{
		try
		{
			mVelocity = new Vector<Double>();
			velocity = velocity.replace("[", "");
			velocity = velocity.replace("]", "");
			String[] a = velocity.split(",");
			for (int i = 0; i < a.length; i++)
			{
				mVelocity.add(Double.valueOf(a[i]));
			}
		} catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param velocity
	 *            The mVelocity to set.
	 */
	public void setVelocity(Vector<Double> velocity)
	{
		mVelocity = velocity;
	}

}
