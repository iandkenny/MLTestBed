/**
 * 
 */
package org.mltestbed.testFunctions.multiModal;

import java.util.Iterator;
import java.util.Vector;

import org.mltestbed.testFunctions.TestBase;
import org.mltestbed.util.Particle;


/**
 * @author Ian Kenny
 *
 */
public class GeneralisedRosenbrock extends TestBase
{

	/**
	 * 
	 * 
	 * Formulation: Minimize: f(x) =sum[100(x(i)+1 - x(i)^2)^2+(x(i)-1)^2]
	 * n=30;X[-n,n]^n
	 * 
	 * global minimum f(x) = 0 x(i) = 1, i=1:n
	 * 
	 */
	public GeneralisedRosenbrock()
	{
		super();
		try
		{
			setMDimension(30);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setRange();
		description = "Generalised Rosenbrock";
		info = "Generalised Rosenbrock: Minimize: f(x) =sum[100(x(i)+1 - x(i)^2)^2+(x(i)-1)^2]";
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
			mMin[i] = -mDimension;
			mMax[i] = mDimension;

		}
	}

	/* (non-Javadoc)
	 * @see org.mltestbed.testFunctions.TestBase#Objective(java.util.Vector)
	 */
	@Override
	public double Objective(Particle p)
	{
		//	 Minimize: f(x) =sum[100(x(i)+1 - x(i)^2)^2+(x(i)-1)^2]
		
		double sum =0;
		
		try
		{
			Vector<Double> v = p.getPosition();
			for (Iterator<Double> iter = v.iterator(); iter.hasNext();)
			{
				
				double element = (Double) iter.next();;
				sum += 100*Math.pow(element+1 - Math.pow(element,2),2)+Math.pow(element-1, 2);

			}
			
			mResult = sum;
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
