/**
 * 
 */
package org.mltestbed.testFunctions.uniModal;

import java.util.Vector;

import org.mltestbed.testFunctions.TestBase;
import org.mltestbed.util.Particle;


/**
 * @author Ian Kenny
 * 
 */
public class Trid extends TestBase
{

	/**
	 * 
	 * 
	 * Formulation: f(x) =sum i=1:n[[xi] n=5;X[-5.12,5.12]^n
	 * 
	 * global minimum depends on n
	 * 
	 */
	public Trid()
	{
		super();
		try
		{
			setMDimension(10);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mGlobalOptima = -210;
		setRange();
		description = "Trid Function";
		info = "Trid: Minimize: f(x) =Formulation: f(x) =sum i=1:n[xi^2]-sum i=2:n-1[xi*x[i-1]]";
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
			mMin[i] = -Math.pow(mDimension, 2);
			mMax[i] = Math.pow(mDimension, 2);

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

		Vector<Double> v;
		try
		{
			v = p.getPosition();
			double s1 = 0;
			double s2 = 0;
			for (int j = 0; j < mDimension; j++)
				s1 = s1 + Math.pow(((Double) v.get(j) - 1), 2);

			for (int j = 1; j < mDimension; j++)
				s2 = s2 + (Double) v.get(j) * (Double) v.get(j - 1);

			mResult = s1 - s2;
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
