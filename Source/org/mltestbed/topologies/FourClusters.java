package org.mltestbed.topologies;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.mltestbed.heuristics.PSO.BaseSwarm;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;
public class FourClusters extends Topology
{
	private static final int NO_OF_CLUSTERS = 4;
	private static final String PROPS = "FourClustersNeighbourhood.properties";
	private List<Particle> swarmMembers = null;
	private int size = 0;
	private static ConcurrentHashMap<Particle, ArrayList<Particle>> neighbourhoods = new ConcurrentHashMap<Particle, ArrayList<Particle>>();
	private boolean bFlag = false;

	public FourClusters(BaseSwarm swarm)
	{
		super(swarm);
		setDescription("Four Clusters Topology");

	}
	@Override
	protected ArrayList<Particle> getNeighbourhood(int particle)
			throws Exception
	{
		ArrayList<Particle> neighbourhood = null;
		Particle particleObj = null;
		try
		{
			if (swarmMembers == null)
			{
				bFlag = true;
				swarmMembers = Collections.synchronizedList(
						new ArrayList<Particle>(swarm.getSwarmMembers()));
				synchronized (swarmMembers)
				{
					int swarmSize = swarmMembers.size();
					size = Math.floorDiv(swarmSize, NO_OF_CLUSTERS);
					HashMap<Integer, ArrayList<Particle>> tempClusterStore = new HashMap<Integer, ArrayList<Particle>>();
					for (int i = 0; i < NO_OF_CLUSTERS; i++)
					{
						neighbourhood = new ArrayList<Particle>();
						tempClusterStore.put(i, neighbourhood);
						for (int j = 0; j < size; j++)
						{
							Particle p = swarmMembers.get((i * size) + j);
							neighbourhood.add(p);
							neighbourhoods.put(p, neighbourhood);
						}

					}
					// allocate the remainder to a cluster for each particle
					for (int i = (size * NO_OF_CLUSTERS)
							+ 1; i < swarmSize; i++)
					{
						ArrayList<Particle> n = tempClusterStore
								.get(i % NO_OF_CLUSTERS);
						Particle p = swarmMembers
								.get((swarmSize >= NO_OF_CLUSTERS) ? i : 0);
						n.add(n.size() - 1, p);
						neighbourhoods.put(p, n);
					}
					// interconnect clusters
					for (int i = 0; i < NO_OF_CLUSTERS; i++)
					{
						ArrayList<Particle> n1 = tempClusterStore.get(i);
						for (int j = 0; j < NO_OF_CLUSTERS; j++)
						{
							if (i != j)
							{
								ArrayList<Particle> n2 = tempClusterStore
										.get(j);
								Particle p = n2.get(
										(n2.size() >= NO_OF_CLUSTERS) ? i : 0);
								n1.add(n1.size(), p);
							}
						}
					}
					bFlag = false;
					swarmMembers.notifyAll();
				}
			}
			synchronized (swarmMembers)
			{
				while (bFlag)
					swarmMembers.wait();
				particleObj = swarmMembers.get(particle);
			}

			if (particleObj != null)
				neighbourhood = neighbourhoods.get(particleObj);
			else
				neighbourhood = null;

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
