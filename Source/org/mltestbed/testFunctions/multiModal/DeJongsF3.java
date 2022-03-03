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
public class DeJongsF3 extends TestBase
{

	/**
	 * 
	 * 
	 * Formulation: f(x) =sum i=1:n[[xi]
	 * n=5;X[-5.12,5.12]^n
	 * 
	 * global minimum f(x) = -30
	 * 
	 */
	public DeJongsF3()
	{
		super();
	    try
		{
			setMDimension(5);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mGlobalOptima = -30;
		setRange();
		description = "DeJongs F3 Function";
		info = "DeJongs F3: Minimize: f(x) =Formulation: f(x) =sum i=1:n[[xi]";
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
			mMin[i] = -5.12;
			mMax[i] = 5.12;

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
		Vector<Double> v = p.getPosition();
		double sum = 0.0;

		try
		{
			for (int i = 0; i < mDimension; i++)
			{
					sum +=Math.floor((Double) v.get(i));
			}
			mResult = sum;
		} catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return super.Objective(p);
	}

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
