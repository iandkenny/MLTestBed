package org.mltestbed.heuristics.PSO.Heirarchy;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.heuristics.PSO.BaseSwarm;
import org.mltestbed.testFunctions.TestBase;
import org.mltestbed.topologies.Topology;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;

public class Heirarchy implements Cloneable
{
	private static final int MASTERSWARMNO = -100;
	BaseSwarm baseChild = null;
	Vector<BaseSwarm> childSwarms = null;
	private long masterIterations;
	BaseSwarm masterSwarm = null;
	private int noSwarms;
	private Properties params;
	private Properties runParams;
	/**
	 * 
	 */
	public Heirarchy(BaseSwarm master, BaseSwarm baseChild, Properties params,
			Properties runParams)
	{
		this.params = new Properties();
		this.params.putAll(params);
		this.runParams = new Properties();
		this.runParams.putAll(runParams);
		masterSwarm = master;
		this.baseChild = baseChild;
	}
	public Object clone()
	{
		try
		{
			return super.clone();
		} catch (CloneNotSupportedException e)
		{
			// This should never happen
			throw new InternalError(e.toString());
		}
	}
	private void createParams()
	{
		params.setProperty("noSwarms", "10");

	}
	/**
	 * @return the baseChild
	 */
	public BaseSwarm getBaseChild()
	{
		return baseChild;
	}
	/**
	 * @return ArrayList<Particle> of gbest particles
	 */
	public ArrayList<Particle> getChildSwarmBests()
	{
		ArrayList<Particle> bests = new ArrayList<Particle>();
		for (int i = 0; i < childSwarms.size(); i++)
		{
//			childSwarms.get(i).recalcGBest();
			Particle best = childSwarms.get(i).getGBest();
			bests.add(i, best);
		}
		return bests;
	}
	/**
	 * @return the childSwarms
	 */
	public Vector<BaseSwarm> getChildSwarms()
	{
		return childSwarms;
	}
	public long getMasterIterations()
	{

		return masterIterations;
	}
	/**
	 * @return the masterSwarm
	 */
	public BaseSwarm getMasterSwarm()
	{
		return masterSwarm;
	}
	/**
	 * @return the props
	 */
	public Properties getParams()
	{
		return params;
	}
	/**
	 * @return the runParams
	 */
	public Properties getRunParams()
	{
		return runParams;
	}
	public void init()
	{
		masterSwarm.setHeir(this);
		masterSwarm.setMaster(true);
		baseChild.setHeir(this);
		baseChild.setMaster(false);
		masterSwarm.setSwarmNo(MASTERSWARMNO);
		masterIterations = Long.parseLong(runParams.getProperty(
				"heirarchyiterations", "1000"));
		if (runParams != null && runParams.containsKey("heirarchyswarms"))
		{
			baseChild.getTestFunction().reset();
			if (runParams.getProperty("heirarchyswarms").equalsIgnoreCase(
					"auto"))
				noSwarms = baseChild.getTestFunction().getNumKeys();
			else
				noSwarms = Integer.parseInt(runParams
						.getProperty("heirarchyswarms"));
			if (childSwarms != null)
			{
				stopChildren();
				childSwarms.clear();
				childSwarms = null;
			}
			childSwarms = new Vector<BaseSwarm>();
			synchronized (childSwarms)
			{

				for (int i = 0; i < noSwarms; i++)
				{
					BaseSwarm base = (BaseSwarm) baseChild.clone();
					base.setNeighbourhood((Topology) baseChild
							.getNeighbourhood().clone());
					base.setTestFunction((TestBase) baseChild.getTestFunction()
							.clone());
					base.setName("Child Swarm: " + i);
					base.setSwarmNo(i);
					base.start();
					base.setRunning(true);
					childSwarms.add(base);

				}
			}
		}
	}
	/**
	 * @param bufProps
	 */
	public void load(Properties props)
	{
		if (props != null)
		{
			createParams();
			Enumeration<Object> keys = params.keys();
			for (String key = (String) keys.nextElement(); keys
					.hasMoreElements();)
			{
				if (props.contains(key))
					params.setProperty(key, props.getProperty(key));
			}
		}
	}
	/**
	 * @param baseChild
	 *            the baseChild to set
	 */
	public void setBaseChild(BaseSwarm baseChild)
	{
		this.baseChild = baseChild;
	}
	public void setMasterSwarm(BaseSwarm swarm)
	{
		this.masterSwarm = swarm;
	}
	/**
	 * @param props
	 *            the props to set
	 */
	public void setParams(Properties props)
	{
		if (this.params == null)
			this.params = new Properties();
		this.params.putAll(props);
	}
	/**
	 * @param runParams
	 *            the runParams to set
	 */
	public void setRunParams(Properties runParams)
	{
		if (this.runParams == null)
			this.runParams = new Properties();
		this.runParams.putAll(runParams);
	}
	/**
	 * 
	 */
	public void startChildren()
	{
		synchronized (childSwarms)
		{

			for (Iterator<BaseSwarm> iterator = childSwarms.iterator(); iterator
					.hasNext();)
			{
				BaseSwarm swarm = (BaseSwarm) iterator.next();
				// The isRunning test shouldn't be necessary but jdk6 seems to
				// have problems with isAlive resulting in threads starting
				// twice
				if (!swarm.isAlive() && !swarm.isRunning())
				{
					swarm.start();
					swarm.setRunning(true);
				}
			}
		}

	}
	/**
	 * 
	 */
	public void stopChildren()
	{
		if (childSwarms != null)
			for (Iterator<BaseSwarm> iterator = childSwarms.iterator(); iterator
					.hasNext();)
			{
				try
				{
					BaseSwarm swarm = (BaseSwarm) iterator.next();
					if (swarm.isAlive())
					{
						swarm.stopRunning();
						swarm.interrupt();
					}
				} catch (Exception e)
				{
					Log.log(Level.SEVERE, e);
//					e.printStackTrace();
				}
			}

	}

}
