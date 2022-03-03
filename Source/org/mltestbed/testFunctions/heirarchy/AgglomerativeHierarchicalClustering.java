/**
 * 
 */
package org.mltestbed.testFunctions.heirarchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Set;
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
public class AgglomerativeHierarchicalClustering extends HeirarchyTestBase
{

	class Sorted extends Vector<Particle>
	{
		private final static int ASC = 1, DESC = 2;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int direction;
		private boolean sortedDist = false;
		private boolean sortedScore = false;

		public Sorted()
		{
		}
		public Sorted(TestBase test, BaseSwarm swarm)
		{
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Vector#clear()
		 */
		@Override
		public void clear()
		{
			sortedDist = false;
			sortedScore = false;
			super.clear();
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
			removeAllElements();
			addAll(swarm.getSwarmMembers());
		}

		public synchronized Vector<Particle> sortByDistance()
		{
			if (sortedDist)
				return this;
			sortedScore = false;
			for (int i = 0; i < size() - 1; i++)
			{
				for (int j = i; j < size() - 1; j++)
				{
					boolean swap = false;
					Particle temp1 = get(j);
					Particle temp2 = get(j + 1);
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

					}
				}
			}
			sortedDist = true;
			return this;

		}

		public synchronized Vector<Particle> sortByScore()
		{
			int i = 0;
			int j = 0;
			if (sortedScore)
				return this;
			sortedDist = false;
			try
			{
				for (i = 0; i < size() - 1; i++)
				{
					for (j = i; j < size() - 1; j++)
					{
						if (direction == ASC)
						{
							if (get(j).compareTo(get(j + 1)) < 1)
							{
								Particle temp1 = get(j);
								Particle temp2 = get(j + 1);
								set(j, temp2);
								set(j + 1, temp1);

							}
						} else
						{
							if (get(j).compareTo(get(j + 1)) > 1)
							{
								Particle temp1 = get(j);
								Particle temp2 = get(j + 1);
								set(j, temp2);
								set(j + 1, temp1);
							}
						}
					}
				}
			} catch (ArrayIndexOutOfBoundsException e)
			{
				Log.log(Level.SEVERE, new Exception(
						"Array Size = " + size() + "i= " + i + "j= " + j, e));
				// e.printStackTrace();
			}
			sortedScore = true;
			return this;
		}
	}
	private static final String AGGLOMERATIVE_HIERARCHICAL_CLUSTERING = "Agglomerative Hierarchical Clustering";
	private static final String INFO = "Implements Agglomerative Hierarchical Clustering";
	private HashMap<Particle, HashMap<Integer, Particle>> clusters;
	private double radius = 0;
	private Sorted sorted = null;
	/**
	 * 
	 */
	public AgglomerativeHierarchicalClustering()
	{
		super();
		description = AGGLOMERATIVE_HIERARCHICAL_CLUSTERING;
		info = INFO;

	}

	/**
	 * @param test
	 * @throws Exception
	 */
	public AgglomerativeHierarchicalClustering(TestBase test, BaseSwarm swarm)
			throws Exception
	{
		super(test, swarm);
		description = AGGLOMERATIVE_HIERARCHICAL_CLUSTERING;
		info = INFO;

		if (test != null && swarm != null)
			sorted = new Sorted(test, swarm);
	}

	private double avgDistToParticle(int index)
	{
		double distsum = 0;
		int i;

		Vector<Particle> sortDist = sorted.sortByDistance();
		for (i = 0; i < index; i++)
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
	@Override
	protected void createParams()
	{
		// TODO Auto-generated method stub

	}
	private double d(Vector<Double> x, Vector<Double> y) throws Exception
	{
		if (x.size() != y.size())
			throw new Exception("Vectors are not the same size");
		double distance = 0;
		double sum = 0;
		for (int i = 0; i < y.size(); i++)
		{
			sum += (x.get(i) - y.get(i)) * (x.get(i) - y.get(i));
		}
		distance = Math.sqrt(sum);
		return distance;
	}
	@Override
	public Properties extraHeirInfo(Properties prop)
	{
		if (clusters != null)
		{
			if (prop == null)
				prop = new Properties();
			prop.setProperty("clusters", clusters.toString());
		}
		return prop;
	}

	private Vector<Particle> generateSeeds()
	{
		Vector<Particle> seeds = new Vector<Particle>();
		sorted.clear();
		sorted.addAll(swarm.getSwarmMembers());
		Vector<Particle> sortedScore = sorted.sortByScore();

		for (ListIterator<Particle> iterator = sortedScore
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
					Log.log(Level.SEVERE, e.getMessage());
				}
			}
			if (!found)
				seeds.add(particle1);

		}
		return seeds;
	}

	/**
	 * @return the clusters
	 */
	public HashMap<Particle, HashMap<Integer, Particle>> getClusters()
	{
		HashMap<Particle, HashMap<Integer, Particle>> clusters = new HashMap<Particle, HashMap<Integer, Particle>>();
		Vector<Particle> seeds = generateSeeds();

		for (Iterator<Particle> iterator = seeds.iterator(); iterator
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
						// break;
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

	/**
	 * @return the radius
	 */
	public double getRadius()
	{
		return radius;
	}

	/**
	 * @return the sorted
	 */
	public Sorted getSorted()
	{
		return sorted;
	}

	@Override
	public void init()
	{
		super.init();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#Objective(java.util.Vector)
	 */
	@Override
	public double Objective(Particle p)
	{
		String buf = "";
		clusters = getClusters();
		// setFuncSpecific(null,true);
		Set<Particle> keys = clusters.keySet();
		for (Iterator<Particle> iterator = keys.iterator(); iterator.hasNext();)
		{
			Particle key = (Particle) iterator.next();
			buf += "<Cluster centre=" + key.getIdentityNumber() + ">";
			HashMap<Integer, Particle> cluster = clusters.get(key);
			Set<Integer> elements = cluster.keySet();
			for (Iterator<Integer> iterator2 = elements.iterator(); iterator2
					.hasNext();)
			{
				Integer element = (Integer) iterator2.next();
				Particle particle = cluster.get(element);
				buf += "<Element>" + particle.getIdentityNumber()
						+ "</Element>";

			}
			buf += "</Cluster>";
		}
		p.setFuncSpecific(
				"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><?xml-stylesheet type=\"text/xsl\" href=\"Agglomerativeresults.xsl\"?>"
						+ "<Clusters>" + buf + "</Custers>",
				true);
		return super.Objective(p);
	}

	/**
	 * @param radius
	 *            the radius to set
	 */
	public void setRadius(double radius)
	{
		this.radius = radius;
	}

	@Override
	protected void setRange()
	{
		// TODO Auto-generated method stub

	}

}
