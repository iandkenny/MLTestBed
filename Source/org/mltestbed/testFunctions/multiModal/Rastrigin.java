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
public class Rastrigin extends TestBase
{

	/**
	 * 
	 * 
	 * Formulation: Minimize: f(x) = 10.0*n + sum(x(i)^2 - 10.0*cos(2*Pi*x(i)))
	 * 
	 * -5.12 <= x(i) <= 5.12
	 * 
	 * global minimum f(x) = 0 x(i) = 0, i=1:n
	 * 
	 */
	public Rastrigin()
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
		description = "Rastrigin Function";
		info = "Rastrigin: Minimise:f(x) = 10.0*n + sum(x(i)^2 - 10.0*cos(2*Pi*x(i)))";
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

	/* (non-Javadoc)
	 * @see org.mltestbed.testFunctions.TestBase#Objective(java.util.Vector)
	 */
	@Override
	public double Objective(Particle p)
	{
		//	 f(x) = 10.0*n + sum(x(i)^2 - 10.0*cos(2*Pi*x(i)))
		double dResult =Double.NaN;
		Vector<Double> v = p.getPosition();
		dResult = 10.0*v.size();
		double sum =0;
		
		for (Iterator<Double> iter = v.iterator(); iter.hasNext();)
		{
			double element =  (Double) iter.next();
			sum += Math.pow(element,2)- 10.0*Math.cos((2*Math.PI*element));

		}
		dResult += sum;
		mResult = dResult;

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
