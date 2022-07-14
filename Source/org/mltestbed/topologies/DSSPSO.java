package org.mltestbed.topologies;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.mltestbed.heuristics.PSO.BaseSwarm;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;
import org.mltestbed.util.RandGen;

public class DSSPSO extends Topology
{
	private static final String PROPS = "DSSPSONeighbourhood.properties";
	private class Neighbour
			implements
				Map.Entry<Particle, Long>,
				Comparable<Particle>,
				Comparator<Neighbour>
	{
		private long joinedTime;
		private Particle neighbour;

		public Neighbour()
		{
			this.neighbour = null;
			this.joinedTime = 0;
		}

		/**
		 * @param neighbour
		 * @param joinedTime
		 */
		public Neighbour(Particle neighbour, long joinedTime)
		{
			super();
			this.neighbour = neighbour;
			this.joinedTime = joinedTime;
		}
		@Override
		public int compare(Neighbour o1, Neighbour o2)
		{
			return Double.compare(o1.getKey().getBestScore(),
					o2.getKey().getBestScore());
		}
		@Override
		public int compareTo(Particle o)
		{
			return Double.compare(neighbour.getBestScore(), o.getBestScore());
		}
		@Override
		public Particle getKey()
		{
			return neighbour;
		}

		@Override
		public Long getValue()
		{
			return joinedTime;
		}
		@Override
		public Long setValue(Long value)
		{
			joinedTime = value;
			return joinedTime;
		}

	}
	@SuppressWarnings("unused")
	private static class SortNeighbourByJoined implements Comparator<Neighbour>
	{

		public int compare(Neighbour obj1, Neighbour obj2)
		{
			return (int) (obj1.getValue() - obj2.getValue());
		}
	}

	private ConcurrentHashMap<Particle, ConcurrentHashMap<Integer, Neighbour>> neighbourhoods = new ConcurrentHashMap<Particle, ConcurrentHashMap<Integer, Neighbour>>();
	private ConcurrentHashMap<ConcurrentHashMap<Integer, Neighbour>, Long> recruitTime;
	private Random rnd = null;
	private int initial = 2;
	private int minNeighbourSize = 2;
	private int maxNeighbourSize;

	public DSSPSO(BaseSwarm swarm)
	{
		super(swarm);
		setDescription("Dynamic Self-Selecting Network Topology PSO (DSSPSO)");
		rnd = RandGen.getLastCreated();
		try
		{
			minNeighbourSize = Integer.parseInt(
					(String) params.getProperty("minNeighbourhoodSize", "1"));
			maxNeighbourSize = Integer.parseInt(
					(String) params.getProperty("maxNeighbourhoodSize", "10"));

			int no = Integer.parseInt(
					(String) params.getProperty("initialNoNeighbourhoodMembers",
							String.valueOf(minNeighbourSize)));
			initial = no < 1 ? minNeighbourSize : no;
			initial = no > maxNeighbourSize ? maxNeighbourSize : no;
		} catch (NumberFormatException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}

	}
	private ConcurrentHashMap<Integer, Neighbour> initNeighbourhood(
			Particle particle)
	{
		ConcurrentHashMap<Integer, Neighbour> neighbourhood = null;
		if (swarm != null)
		{
			ArrayList<Particle> swarmMembers = swarm.getSwarmMembers();
			int size = swarmMembers.size();

			int index = swarmMembers.indexOf(particle);
			neighbourhood = new ConcurrentHashMap<Integer, Neighbour>();
			Particle neighbourParticle;
			for (int i = 0; i < initial; i++)
			{

				int identityNumber;
				do
				{
					index = (int) (rnd.nextDouble() * size);
					neighbourParticle = swarmMembers.get(index);
					identityNumber = neighbourParticle.getIdentityNumber();
				} while (neighbourhood.containsKey(identityNumber)
						|| particle.equals(neighbourParticle));
				neighbourhood.put(identityNumber,
						new Neighbour(neighbourParticle, iteration));
			}
			neighbourhoods.put(particle, neighbourhood);
		}

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
		String buf = params.getProperty("initialNoNeighbourhoodMembers", "2");
		params.setProperty("initialNoNeighbourhoodMembers", buf);
		buf = params.getProperty("minNeighbourhoodSize", "5");
		params.setProperty("minNeighbourhoodSize", buf);
		buf = params.getProperty("maxNeighbourhoodSize", "10");
		params.setProperty("maxNeighbourhoodSize", buf);
		try
		{
			params.store(new FileOutputStream(PROPS),
					"DSSPSO Neighbourhood properties - Automatically saved");
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
	
	@Override
	protected ArrayList<Particle> getNeighbourhood(int particle)

	{
		ArrayList<Particle> neighbourhood = new ArrayList<Particle>();
		Particle particleObj = swarm.getSwarmMembers().get(particle);
		try
		{
			recruitNeighbour(particleObj);
			ConcurrentHashMap<Integer, Neighbour> neighbours = neighbourhoods
					.get(particleObj);
			Iterator<Neighbour> iterator = neighbours.values().iterator();
			while (iterator.hasNext())
			{
				Neighbour neighbour = iterator.next();
				// if this has happened the particle identifier is the same and
				// the particle has been given itself as a neighbour by another
				// swarm
				Particle key = neighbour.getKey();
				if (key.equals(particleObj))
					neighbours.remove(key.getIdentityNumber());
				else
					neighbourhood.add(key);
			}
			removeNeighbour(particleObj);
		} catch (ArrayIndexOutOfBoundsException e)
		{
			Log.log(Level.SEVERE,
					new Exception(e.getMessage() + " particle=" + particle
							+ " neighbourhood size=" + neighbourhood.size(),
							e));
			e.printStackTrace();
		}
		neighbourhoodXML(particle, neighbourhood);
		particleObj.setFuncSpecific(neighbourhoodSpecific, false);
		System.out.println(neighbourhoodSpecific);
		return neighbourhood;
	}
	private void recruitNeighbour(Particle particle)
	{
		try
		{
			synchronized (particle)
			{
				if (recruitTime == null)
					recruitTime = new ConcurrentHashMap<ConcurrentHashMap<Integer, Neighbour>, Long>();
				ArrayList<Particle> localSwarm = new ArrayList<Particle>(Collections.synchronizedList(swarm.getSwarmMembers()));
				Collections.sort(localSwarm);
				ConcurrentHashMap<Integer, Neighbour> neighbourhood = neighbourhoods
						.get(particle);
				if (neighbourhood == null || neighbourhood.isEmpty())
					neighbourhood = initNeighbourhood(particle);
				if (neighbourhood.size() < maxNeighbourSize)
				{

					ArrayList<Neighbour> sortneighbourhood = new ArrayList<DSSPSO.Neighbour>(
							neighbourhood.values());
					Collections.sort(sortneighbourhood, new Neighbour());

					int rank = localSwarm.indexOf(particle);
					double p1 = (1 / (rank + 1)) * rnd.nextDouble();
					long lastRecruit;
					if (recruitTime.containsKey(neighbourhood))
						lastRecruit = recruitTime.get(neighbourhood);
					else
						lastRecruit = 0;
					double p2 = (1 - (1 / ((iteration + 1) - lastRecruit)))
							* rnd.nextDouble();
					if (p1 + p2 > (rnd.nextDouble()))
					{
						localSwarm = swarm.getSwarmMembers();
						Particle neighbour;
						int identityNumber = Integer.MIN_VALUE;
						// select a new neighbour
						do
						{
							neighbour = localSwarm.get((int) (localSwarm.size()
									* rnd.nextDouble()));
						} while (particle.equals(neighbour));
						identityNumber = neighbour.getIdentityNumber();
						if (!neighbourhood.containsKey(identityNumber) ||
						// if this has happened the particle identifier is the
						// same and
						// the neighbourhood contains the other particle which
						// we update
						// NB this only happens with a hierarchy
								!neighbourhood.get(identityNumber).getKey()
										.equals(neighbour))
						{
							Neighbour map = new Neighbour(neighbour, iteration);
							neighbourhood.put(identityNumber, map);
							neighbourhoods.put(particle, neighbourhood);
							recruitTime.put(neighbourhood, iteration);
						}
					}
				}
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
	}
	private void removeNeighbour(Particle particle)
	{
		try
		{
			synchronized (particle)
			{
				ConcurrentHashMap<Integer, Neighbour> neighbourhood = neighbourhoods
						.get(particle);
				if (neighbourhood.size() > minNeighbourSize)
				{
					ArrayList<Neighbour> sortneighbourhood = new ArrayList<DSSPSO.Neighbour>(
							neighbourhood.values());
					Collections.sort(sortneighbourhood, new Neighbour());
					Neighbour queryRemove = sortneighbourhood
							.get(neighbourhood.size() - 1);
					long added = queryRemove.getValue();
					long rank = sortneighbourhood.indexOf(queryRemove);
					// Collections.sort(neighbourhood, new
					// SortNeighbourByJoined());
					double p1 = (1 - (1 / ((iteration + 1) - added)))
							* rnd.nextDouble();
					double p2 = 1 - (1 / (rank + 1)) * rnd.nextDouble();

					if (p1 + p2 > (rnd.nextDouble() * 2))
					{
						neighbourhood.remove(
								queryRemove.getKey().getIdentityNumber());
						neighbourhoods.put(particle, neighbourhood);
					}
				}
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}
	}
}
