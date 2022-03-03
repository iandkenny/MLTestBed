/** 
 * 
 */
package org.mltestbed.testFunctions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.mltestbed.heuristics.PSO.BaseSwarm;
import org.mltestbed.util.Particle;



/**
 * @author Ian Kenny
 * 
 */
public abstract class HeirarchyTestBase extends TestBase implements Cloneable
{

	protected BaseSwarm swarm = null;
	private TestBase subTest = null;
	/**
	 * 
	 */
	public HeirarchyTestBase()
	{
		super();
		swarm = null;
		subTest = null;
	}

	/**
	 * @throws Exception
	 * 
	 */
	public HeirarchyTestBase(TestBase test, BaseSwarm swarm) throws Exception
	{
		if (test == null)
			throw new Exception("Sub swarm test function is required");
		subTest = test;
		this.swarm = swarm;
		setRange();
		functEval = 0;
		params = swarm.getParams();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#clone()
	 */
	@Override
	public Object clone()
	{
		// TODO Auto-generated method stub
		return super.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#createParams()
	 */
	@Override
	protected void createParams()
	{
		// TODO Auto-generated method stub

	}

	public Properties extraHeirInfo(Properties value)
	{
		Properties properties = new Properties();
		if (value != null)
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try
			{
				value.storeToXML(out, "Additional Heirarchy Data");
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			properties.setProperty("heirarchy", out.toString());
		}

		return properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#getMMax(int)
	 */
	@Override
	public double getMax(int i)
	{
		if (swarm != null)
			return swarm.getSwarmMembers().get(0).getTestFunction().getMax(i);
		else
			return Double.NaN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#getMMin(int)
	 */
	@Override
	public double getMin(int i)
	{
		if (swarm != null)
			return swarm.getSwarmMembers().get(0).getTestFunction().getMin(i);
		else
			return Double.NaN;
	}
	/**
	 * @return the swarm
	 */
	public BaseSwarm getSwarm()
	{
		return swarm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#init()
	 */
	@Override
	public void init()
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#Objective(java.util.Vector)
	 */
	@Override
	public double Objective(Particle p)
	{
		mResult = 0;
		String xml = p.getFuncSpecific();
		ArrayList<Particle> swarmMembers = swarm.getSwarmMembers();
		int size = swarmMembers.size();
		for (int i = 0; i < size; i++)
		{
			TestBase testFunction = (TestBase) swarmMembers.get(i)
					.getTestFunction().clone();
			mResult += Math.sqrt(Math.pow(testFunction.Objective(p), 2));
			xml += "<ChildSwarm value=\"" + testFunction.getKey() + "\">"
					+ p.getFuncSpecific() + "</ChildSwarm>";
			testFunction = null;
		}
		xml = xml.replaceAll("<\\?", "<!--<\\?").replaceAll("\\?>", "\\?>-->");
		p.setFuncSpecific(xml, true);
		return super.Objective(p);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#setRange()
	 */
	@Override
	protected void setRange()
	{
		// subTest.setRange();
		if (subTest != null)
		{
			mDimension = subTest.getMDimension();
			mMax = new double[mDimension];
			mMin = new double[mDimension];
			for (int i = 0; i < mDimension; i++)
			{
				mMax[i] = subTest.getMax(i);
				mMin[i] = subTest.getMin(i);

			}
		}

	}
}
