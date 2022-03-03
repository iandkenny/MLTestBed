package org.mltestbed.topologies;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.mltestbed.heuristics.PSO.BaseSwarm;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;
import org.mltestbed.util.RandGen;
import org.mltestbed.util.Util;

/*
 * Implementation of:
 * Liu, Qingxue, et al. ‘DSSPSO Small World Network Topology for Particle Swarm Optimization’. International Journal of Pattern Recognition and Artificial Intelligence 30, no. 09 (22 July 2016).
 */
public class DTSWPSO extends Topology
{
	private static final String PROPS = "DTSWPSONeighbourhood.properties";
	private List<Particle> swarmMembers = null;
	private int size = 0;
	private static ConcurrentHashMap<Particle, List<Particle>> neighbourhoods = new ConcurrentHashMap<Particle, List<Particle>>();
	private long MaxiterationsDivTen = 0;
	private long currentIteration = Long.MIN_VALUE;
	private double probability;
	private Random rnd;

	public DTSWPSO(BaseSwarm swarm)
	{
		super(swarm);
		setDescription(
				"DTSWPSO - Dynamic Topology Particle Swarm Optimization");
		MaxiterationsDivTen = swarm.getMaxIterations() / 10;
		probability = 0.9;
		rnd = RandGen.getLastCreated();
	}
	@Override
	protected ArrayList<Particle> getNeighbourhood(int particle)
			throws Exception
	{
		Particle particleObj = null;
		ArrayList<Particle> neighbourhood = new ArrayList<Particle>();
		try
		{

			while (swarmMembers == null)
			{
				swarmMembers = Collections
						.synchronizedList(swarm.getSwarmMembers());
				size = swarmMembers.size();
			}
			particleObj = swarmMembers.get(particle);

			if (particleObj != null || (currentIteration != swarm.getIteration()
					|| !neighbourhoods.containsKey(particleObj)))
			// if we are still on the same iteration return the existing
			// neighbourhood if exists
			{
				neighbourhood = new ArrayList<Particle>();
				int k = Integer.parseInt(params.getProperty("k", "4"));
				int startIndex = particle - 1;

				if (startIndex < 0)
					startIndex = size + startIndex;

				Util.getSwarmui().updateLog("particle = " + particle + " "
						+ "startIndex = " + startIndex + "\n");
				// add the two adjacent neighbours
				int maxIndex = size - 1;
				neighbourhood.add(swarmMembers.get(startIndex % size));
				neighbourhood.add(swarmMembers.get((particle + 1) % size));
				int i = (particle + 2) % size;
				while (neighbourhood.size() < k && i != particle
						&& probability > 0.0)
				{
					Particle p = swarmMembers.get(i);
					if (rnd.nextDouble() <= probability
							&& !neighbourhood.contains(p))
						neighbourhood.add(p);
					i = (i + 1) % size;
				}
				neighbourhoods.put(particleObj, neighbourhood);
				if (currentIteration > 0 && particle == maxIndex
						&& currentIteration % MaxiterationsDivTen == 0)
					probability -= 0.1;

			} else
				neighbourhood = new ArrayList<Particle>(Collections
						.synchronizedList(neighbourhoods.get(particleObj)));
			currentIteration = swarm.getIteration();
		} catch (NumberFormatException e)
		{
			Log.log(Level.SEVERE,
					new Exception(e.getMessage() + " particle=" + particle
							+ " neighbourhood size=" + neighbourhood.size(),
							e));
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e)
		{
			Log.log(Level.SEVERE,
					new Exception(e.getMessage() + " particle=" + particle
							+ " neighbourhood size=" + neighbourhood.size(),
							e));
			e.printStackTrace();
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception(e.getMessage() + " particle=" + particle, e));

			e.printStackTrace();
		}
		neighbourhoodXML(particle, neighbourhood);
		if (particleObj != null)
			particleObj.setFuncSpecific(neighbourhoodSpecific, false);
		System.out.println(neighbourhoodSpecific);
		return neighbourhood;
	}

	@Override
	public void createParams()
	{
		params = new Properties();
		params.put("k", "4");
		try
		{
			params.load(new FileInputStream(PROPS));
		} catch (FileNotFoundException e)
		{

			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		} catch (IOException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		}

	}

	/**
	 * @return Returns the params.
	 */
	public Properties getParams()
	{
		return params;
	}

	/**
	 * @param params
	 *            The params to set.
	 */
	public void setParams(Properties params)
	{
		this.params = params;
	}

}
