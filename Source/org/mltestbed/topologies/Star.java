package org.mltestbed.topologies;


import java.util.ArrayList;
import java.util.logging.Level;

import org.mltestbed.heuristics.PSO.BaseSwarm;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;


public class Star extends Topology
{

	public Star(BaseSwarm swarm)
	{
		super(swarm);
		setDescription("FIPS: Gbest or Star Topology");
	}

	@Override
	protected ArrayList<Particle> getNeighbourhood(int particle)
			throws Exception
	{
		ArrayList<Particle> neighbourhood = null;
		try
		{
			ArrayList<Particle> swarmMembers = swarm.getSwarmMembers();
			if (swarmMembers != null)
				neighbourhood = new ArrayList<Particle>(swarmMembers);
			// Remove the current particle from the neighbourhood
			if (neighbourhood != null && neighbourhood.size() != 0)
				neighbourhood.remove(particle);
			else
				throw new Exception(
						"Star Topology returned a null neighbourhood particle ="
								+ particle);

		} catch (ArrayIndexOutOfBoundsException e)
		{
			Log.log(Level.SEVERE, new Exception(e.getMessage() + " particle="
					+ particle + " neighbourhood size=" + neighbourhood.size(),
					e));
			// e.printStackTrace();
		}
		neighbourhoodXML(particle, neighbourhood);
		return neighbourhood;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.topologies.Topology#createParams()
	 */
	@Override
	public void createParams()
	{

	}

}
