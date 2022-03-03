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
public class MyTest extends TestBase
{

	/**
	 * 
	 * 
	 * Formulation: f(x) =sum i=1:n[[xi]
	 * n=10;X[-1000,100]^n
	 * 
	 * global minimum depends on n
	 * 
	 */
	public MyTest()
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
		mGlobalOptima = -1000*mDimension;
		setRange();
		description = "My Test Function";
		info = "My Test Function: Minimize: f(x) =Formulation: f(x) =sum i=1:n[i-1]xi+abs[x[i-1]]";
		minimised = true;
	}

	/**
	 * 
	 */
	protected void setRange()
	{
		mMin = new double[mDimension];
		mMax = new double[mDimension];
		mGlobalOptima = -1000*mDimension;
		for (int i = 0; i < mDimension; i++)
		{
			mMin[i] = -1000;
			mMax[i] = 100;

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
		

		try
		{
			Vector<Double> v = p.getPosition();
			double sum = 0;
			for (int j = 0;j<mDimension;j++)
			    sum = sum+((Double)v.get(j));    
			
		
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
