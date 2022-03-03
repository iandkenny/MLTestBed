/**
 * 
 */
package org.mltestbed.testFunctions.multiModal;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.testFunctions.TestBase;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;


/**
 * @author Ian Kenny
 *
 */
public class Griewank extends TestBase 
{

	/**
	 * Formulation:
	 * Minimize:
	 * f(x) = 1/4000*sum(xi-100)^2 - prod((xi-100)/sqrt(i)) + 1
	 * 
	 * -600 <= x(i) <= 600
	 * 
	 * 
	 * global minimum
	 * f(x) = 0
	 * x(i) = 0, i=1:n

	 */
	public Griewank()
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
		setRange();
		description = "Griewank Function";
		info = "Griewank: Minimise:f(x) = 1/4000*sum(xi-100)^2 - prod((xi-100)/sqrt(i)) + 1";
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
			mMin[i] =-600;
			mMax[i] =600;
			
		}
	}

	/* (non-Javadoc)
	 * @see org.mltestbed.testFunctions.TestBase#Objective(java.util.Vector)
	 */
	public double Objective(Particle p)
	{
		try
		{
			Vector<Double> v = p.getPosition();
			double dResult = Double.NaN;
			double product = 1;
//			dResult = 1 / 4000;
			double sum = 0;
			long i = 0;
			for (Iterator<Double> iter = v.iterator(); iter.hasNext();)
			{
				double element = (Double) iter.next();
				
				sum +=  (element * element / 4000);
				product *= (Math.cos(element / Math.sqrt(i + 1)));
				i++;

			}
			dResult = 1 +sum - product;
//			System.out.print("pos (inside griewank eval) =[");
//			for (int k = 0; k < v.size(); k++)
//			{
//				System.out.print(v.get(k)+",");
//				
//			}
//			Log.getLogger().info("]");
		

			mResult = dResult;
//			Log.getLogger().info("mResult = "+mResult);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
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
