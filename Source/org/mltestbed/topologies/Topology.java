/*
 * Created on 22-Mar-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mltestbed.topologies;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;

import org.mltestbed.heuristics.PSO.BaseSwarm;
import org.mltestbed.testFunctions.TestBase;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;

/**
 * @author Ian Kenny
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class Topology implements Cloneable
{

	/**
	 * 
	 */
	private static final String TOPOLOGY_PROPERTIES = "topology.properties";
	protected String description = "";
	protected long iteration;
	protected String neighbourhoodSpecific = "";
	protected Properties params = new Properties();
	protected BaseSwarm swarm = null;

	/**
	 * 
	 */
	public Topology(BaseSwarm swarm)
	{
		super();
		createParams();
		setSwarm(swarm);
	}

	/**
	 * @param description
	 * @param iteration
	 * @param neighbourhoodSpecific
	 * @param params
	 * @param swarm
	 */
	protected Topology(Topology o)
	{
		super();
		this.description = o.description;
		this.iteration = o.iteration;
		this.neighbourhoodSpecific = new String(o.neighbourhoodSpecific);
		this.params = new Properties(o.params);
		this.setSwarm(o.swarm);
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

	/**
	 * create the default settings for the parameter for this neighbourhood;
	 */
	protected abstract void createParams();
	/**
	 * @return Returns the description.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @return Particle with gBest best score
	 */
	public Particle getGBest()
	{
		return swarm.getGBest();
	}

	/**
	 * @param particleIndex
	 * @return best particle in neighbourhood
	 */
	public Particle getNbest(int particleIndex)
	{
		Particle particle = null;
		try
		{

			ArrayList<Particle> neighbourhood = getNeighbourhood(particleIndex);
			if (neighbourhood.isEmpty())

				throw new Exception(
						"There are no particles in the neighbourhood");
			TestBase testFunction = swarm.getTestFunction();
			if (testFunction.isMinimised())
			{
				particle = testFunction.min(neighbourhood);
			} else
			{
				particle = testFunction.max(neighbourhood);
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

		return particle;

	}

	/**
	 * 
	 * 
	 * @param particle
	 * @return Vector<Particle> contain the given particle's neighbourhood
	 * @throws Exception
	 */
	protected abstract ArrayList<Particle> getNeighbourhood(int particle)
			throws Exception;
	/**
	 * @return the neighbourhoodSpecific
	 */
	public String getNeighbourhoodSpecific()
	{
		return neighbourhoodSpecific;
	}

	/**
	 * @param particleIndex
	 * @return worst particle in neighbourhood
	 */
	public Particle getNworst(int particleIndex)
	{
		Particle particle = null;
		try
		{

			ArrayList<Particle> neighbourhood = getNeighbourhood(particleIndex);
			if (neighbourhood.isEmpty())

				throw new Exception(
						"There are no particles in the neighbourhood");
			TestBase testFunction = swarm.getTestFunction();
			if (!testFunction.isMinimised())
			{
				particle = testFunction.min(neighbourhood);
			} else
			{
				particle = testFunction.max(neighbourhood);
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}

		return particle;

	}

	/**
	 * @return Returns the params.
	 */
	public Properties getParams()
	{
		return params;
	}

	/**
	 * @return Returns the swarm.
	 */
	public BaseSwarm getSwarm()
	{
		return swarm;
	}

	public void load(Properties props)
	{
		if (props != null)
		{
			params.clear();
			createParams();
			Enumeration<Object> keys = params.keys();
			while (keys.hasMoreElements())
			{
				String key = (String) keys.nextElement();
				if (props.containsKey(key))
					params.setProperty(key, props.getProperty(key));
			}
		}
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setIteration(long iteration)
	{
		this.iteration = iteration;

	}

	/**
	 * @param params
	 *            The params to set.
	 */
	public void setParams(Properties params)
	{
		this.params = params;
	}
	/**
	 * @param swarm
	 *            The swarm to set.
	 */
	public void setSwarm(BaseSwarm swarm)
	{
		this.swarm = swarm;
		// this.swarm.setNeighbourhood(this);
	}

	/**
	 * Stores the current properties in a file
	 */
	public void storeProperties()
	{
		if (params != null)
			try
			{
				params.store(new FileOutputStream(TOPOLOGY_PROPERTIES),
						description);
			} catch (FileNotFoundException e)
			{
				Log.log(Level.SEVERE, e);
				// e.printStackTrace();
			} catch (IOException e)
			{
				Log.log(Level.SEVERE, e);
				// e.printStackTrace();
			}
	}

	/**
	 * @param particle
	 * @param neighbourhood
	 */
	protected void neighbourhoodXML(int particle,
			ArrayList<Particle> neighbourhood)
	{
		if (neighbourhood != null && !neighbourhood.isEmpty())
		{
			Particle particleObj = swarm.getSwarmMembers().get(particle);
			neighbourhoodSpecific = "<ParticleNeighbourhood particle = \""
					+ particleObj.getIdentityNumber() + "\">" + description
					+ "(";
			for (Iterator<Particle> iterator = neighbourhood.iterator(); iterator
					.hasNext();)
			{
				Particle neighbour = (Particle) iterator.next();
				neighbourhoodSpecific += neighbour.getIdentityNumber() + ",";;
			}
			neighbourhoodSpecific = neighbourhoodSpecific.substring(0,
					neighbourhoodSpecific.length() - 1)
					+ ")</ParticleNeighbourhood>";
		} else
			neighbourhoodSpecific = "<ParticleNeighbourhood>" + description
					+ "</ParticleNeighbourhood>";
	}
}
