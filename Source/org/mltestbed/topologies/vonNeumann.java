package org.mltestbed.topologies;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.mltestbed.heuristics.PSO.BaseSwarm;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;


public class vonNeumann extends Topology
{
	private static final String PROPS = "vonNeumannNeighbourhood.properties";

	public vonNeumann(BaseSwarm swarm)
	{
		super(swarm);
		setDescription("vonNeumann Topology");
		createParams();
	}

	@Override
	protected ArrayList<Particle> getNeighbourhood(int particle)
	{
		ArrayList<Particle> neighbourhood = null;
		try
		{
			int gridWidth = Integer.parseInt(params.getProperty("gridWidth",
					"3"));
			neighbourhood = new ArrayList<Particle>(gridWidth + 1);
			ArrayList<Particle> swarmMembers = swarm.getSwarmMembers();
			int north = (particle - gridWidth < 0) ? swarmMembers.size()
					+ (particle - gridWidth) : particle - gridWidth;
			// int east = particle + 1 % swarm.getNoMembers();
			int east = particle + 1 % swarmMembers.size();
			int south = (particle + gridWidth) % swarmMembers.size();
			int west = particle - 1 < 0 ? swarmMembers.size() : particle - 1;

			neighbourhood.add(swarmMembers.get(east));
			neighbourhood.add(swarmMembers.get(south));
			neighbourhood.add(swarmMembers.get(west));
			neighbourhood.add(swarmMembers.get(north));
			neighbourhood.add(swarmMembers.get(particle));
		} catch (NumberFormatException e)
		{
			Log.log(Level.SEVERE, new Exception(e.getMessage() + " particle=" + particle
					+ " neighbourhood size=" + neighbourhood.size(),e));
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e)
		{
			Log.log(Level.SEVERE, new Exception(e.getMessage() + " particle=" + particle
					+ " neighbourhood size=" + neighbourhood.size(),e));
			e.printStackTrace();
		}
		neighbourhoodXML(particle, neighbourhood);
		return neighbourhood;
	}

	@Override
	public void createParams()
	{
		params = new Properties();
		params.put("gridWidth", "3");
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
