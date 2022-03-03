/**
 * 
 */
package org.mltestbed.testFunctions.multiModal;

import java.util.Vector;

import org.mltestbed.testFunctions.TestBase;
import org.mltestbed.util.Particle;


/**
 * @author Ian Kenny
 *
 */
public class Shubert extends TestBase
{

	/**
	 * 
	 * 
	 * Formulation: f(x) =prod i=1:n[sum j=1:5[jcos((j+1)xi+j)]]
	 * n=2;X[-10,10]^n
	 * 
	 * global minimum f(x) = -186.73067 :nloc = 760
	 * 
	 */
	public Shubert()
	{
		super();
		try
		{
			setMDimension(2);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mGlobalOptima = -186.73067;
		setRange();
		description = "Shubert Function";
		info = "Shubert: Minimize: f(x) =prod i=1:n[sum j=1:5[jcos((j+1)xi+j)]]";
		minimised = true;
	}

	/**
	 * 
	 */
	protected void setRange()
	{
		mMin = new double[mDimension];
		mMax = new double[mDimension];
		for (int i = 0; i < mDimension; i++)
		{
			mMin[i] = -10;
			mMax[i] = 10;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#Objective(java.util.Vector)
	 */
	@Override
	public double Objective(Particle p)
	{
		// Minimize: f(x) =prod i=1:n[sum j=1:5[jcos((j+1)xi+j)]]

		double prod = 1.0;


		try
		{
			Vector<Double> v = p.getPosition();
			for (int i = 0; i < mDimension; i++)
			{
				double sum = 0.0;
				for (int j = 1; j <= 5; j++)
					sum += j * Math.cos((j + 1) * (Double) v.get(i) + j);
				prod *= sum;
			}
			mResult = prod;
		} catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return super.Objective(p);
	}

	/* (non-Javadoc)
	 * @see org.mltestbed.testFunctions.TestBase#createParams()
	 */
	@Override
	protected void createParams()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		
	}

}
