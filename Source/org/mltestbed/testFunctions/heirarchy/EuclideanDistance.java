/**
 * 
 */
package org.mltestbed.testFunctions.heirarchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.heuristics.PSO.BaseSwarm;
import org.mltestbed.testFunctions.HeirarchyTestBase;
import org.mltestbed.testFunctions.TestBase;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;

/**
 * @author Ian Kenny
 * 
 */
public class EuclideanDistance extends HeirarchyTestBase
{

	private static final String INFO = "Measures the Euclidean distance between particles and groups them in clusters";
	private static final String EUCLIDEAN_DISTANCE_FUNCTION = "Euclidean Distance Function";

	class Sorted extends Vector<Particle>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final static int ASC = 1, DESC = 2;
		private int direction;
		private BaseSwarm swarm;
		private Vector<Particle> sortedDist = new Vector<Particle>();
		private Vector<Particle> sortedScore = new Vector<Particle>();

		public Sorted()
		{
			swarm = null;
		}
		public Sorted(TestBase test, BaseSwarm swarm)
		{
			this.swarm = swarm;
			ArrayList<Particle> swarmMembers = swarm.getSwarmMembers();
			if (swarmMembers != null)
				addAll(swarmMembers);
			if (test != null && test.isMinimised())
				direction = ASC;
			else
				direction = DESC;
			if (size() > 0)
				sortByScore();
		}

		public Vector<Particle> sortByScore()
		{
			ArrayList<Particle> list = swarm.getSwarmMembers();
			synchronized (list)
			{
				sortedScore.clear();
				sortedScore.addAll(list);

				for (int i = 0; i < sortedScore.size() - 1; i++)
				{
					for (int j = i; j < sortedScore.size() - 1; j++)
					{
						if (direction == ASC)
						{
							if (sortedScore.get(j)
									.compareTo(sortedScore.get(j + 1)) < 1)
							{
								Particle temp1 = sortedScore.get(j);
								Particle temp2 = sortedScore.get(j + 1);
								set(j, temp2);
								set(j + 1, temp1);

							}
						} else
						{
							if (sortedScore.get(j)
									.compareTo(sortedScore.get(j + 1)) > 1)
							{
								Particle temp1 = sortedScore.get(j);
								Particle temp2 = sortedScore.get(j + 1);
								set(j, temp2);
								set(j + 1, temp1);
							}
						}
					}
				}
			}
			return sortedScore;
		}
		public Vector<Particle> sortByDistance()
		{
			sortedDist.clear();
			sortedDist.addAll(swarm.getSwarmMembers());
			boolean swap = true;
			while (swap)
			{
				for (int j = 0; j < sortedDist.size() - 1; j++)
				{
					swap = false;
					Particle temp1 = sortedDist.get(j);
					Particle temp2 = sortedDist.get(j + 1);
					Vector<Double> vtemp1 = temp1.getPbest();
					Vector<Double> vtemp2 = temp2.getPbest();
					if (direction == ASC)
					{

						for (int k = 0; k < vtemp1.size(); k++)
						{
							if (vtemp1.get(k) > vtemp2.get(k))
							{
								swap = true;
								break;
							}

						}
					} else
					{
						for (int k = 0; k < vtemp1.size(); k++)
						{
							if (vtemp1.get(k) < vtemp2.get(k))
							{
								swap = true;
								break;
							}

						}

					}
					if (swap)
					{
						set(j, temp2);
						set(j + 1, temp1);
						break;
					}
				}
			}
			return sortedDist;

		}

		/**
		 * @return the swarm
		 */
		public BaseSwarm getSwarm()
		{
			return swarm;
		}

		/**
		 * @param swarm
		 *            the swarm to set
		 */
		public void setSwarm(BaseSwarm swarm)
		{
			this.swarm = swarm;
			removeAllElements();
			addAll(swarm.getSwarmMembers());
		}
	}
	private Sorted sorted = null;
	private double radius = 1;
	private HashMap<Particle, HashMap<Integer, Particle>> clusters;
	/**
	 * 
	 */
	public EuclideanDistance()
	{
		super();
		description = EUCLIDEAN_DISTANCE_FUNCTION;
		info = INFO;

	}

	/**
	 * @param test
	 * @throws Exception
	 */
	public EuclideanDistance(TestBase test, BaseSwarm swarm) throws Exception
	{
		super(test, swarm);
		description = EUCLIDEAN_DISTANCE_FUNCTION;
		info = INFO;
		if (swarm != null)
			sorted = new Sorted(test, swarm);
	}

	private double d(Vector<Double> x, Vector<Double> y) throws Exception
	{
		if (x.size() != y.size())
			throw new Exception("Vectors are not the same size");
		double distance = 0;
		double sum = 0;
		for (int i = 0; i < y.size(); i++)
		{
			double sqr = (x.get(i) - y.get(i)) * (x.get(i) - y.get(i));
			sum += sqr;
		}
		distance = Math.sqrt(sum);
		return distance;
	}
	private double avgDistToParticle(int index)
	{
		double distsum = 0;
		int i;

		Vector<Particle> sortDist = sorted.sortByDistance();
		for (i = 0; i < index - 1; i++)
		{
			try
			{
				double d = d(sortDist.get(i).getPbest(),
						sortDist.get(i + 1).getPbest());
				// have we found a possible cluster separator
				// for the moment assume 1.5 the average distance so far
				if (d > (distsum * 1.5))
					distsum = 0;
				distsum += d;
				distsum /= index;
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return distsum;
	}
	private Vector<Particle> generateSeeds()
	{
		Vector<Particle> seeds = new Vector<Particle>();
		Vector<Particle> sortScore = sorted.sortByScore();

		for (ListIterator<Particle> iterator = sortScore
				.listIterator(); iterator.hasNext();)
		{
			int index = iterator.nextIndex();
			Particle particle1 = iterator.next();
			boolean found = false;
			radius = avgDistToParticle(index);
			for (Iterator<Particle> iterator2 = seeds.iterator(); iterator2
					.hasNext();)
			{
				Particle particle2 = iterator2.next();
				try
				{

					if (d(particle1.getPosition(),
							particle2.getPosition()) < radius)
					{
						found = true;
						break;
					}
				} catch (Exception e)
				{
					Log.log(Level.SEVERE, new Exception(e.getMessage()));
				}
				if (!found)
					seeds.add(particle2);

			}

		}
		return seeds;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#Objective(java.util.Vector)
	 */
	@Override
	public double Objective(Particle p)
	{
		clusters = getClusters();
		p.setFuncSpecific(
				"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><?xml-stylesheet type=\"text/xsl\" href=\"Euclideanresults.xsl\"?>"
						+ clusters.toString(),
				true);
		return super.Objective(p);
	}

	/**
	 * @return the radius
	 */
	public double getRadius()
	{
		return radius;
	}

	/**
	 * @param radius
	 *            the radius to set
	 */
	public void setRadius(double radius)
	{
		this.radius = radius;
	}

	/**
	 * @return the sorted
	 */
	public Sorted getSorted()
	{
		return sorted;
	}

	/**
	 * @return the clusters
	 */
	public HashMap<Particle, HashMap<Integer, Particle>> getClusters()
	{
		HashMap<Particle, HashMap<Integer, Particle>> clusters = new HashMap<Particle, HashMap<Integer, Particle>>();
		generateSeeds();

		for (Iterator<Particle> iterator = sorted.iterator(); iterator
				.hasNext();)
		{
			Particle seed = iterator.next();
			HashMap<Integer, Particle> cluster = new HashMap<Integer, Particle>();
			for (int i = 0; i < swarm.getSwarmMembers().size(); i++)
			{
				Particle particle = swarm.getSwarmMembers().get(i);
				try
				{
					if (d(seed.getPosition(), particle.getPosition()) < radius)
					{
						cluster.put(i, particle);
						break;
					}
				} catch (Exception e)
				{
					Log.log(Level.SEVERE, e);
				}
			}
			clusters.put(seed, cluster);
		}
		this.clusters = clusters;
		return clusters;
	}

	@Override
	protected void createParams()
	{
		params.setProperty("radius", Double.toString(radius));

	}

	@Override
	public void init()
	{
		super.init();
		radius = Double.parseDouble(params.getProperty("radius"));

	}

	@Override
	public Properties extraHeirInfo(Properties prop)
	{
		prop = new Properties();
		prop.setProperty("clusters", clusters.toString());

		return prop;
	}

	@Override
	protected void setRange()
	{
		// TODO Auto-generated method stub

	}

}
