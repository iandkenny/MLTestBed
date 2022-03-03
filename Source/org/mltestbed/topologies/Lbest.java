package org.mltestbed.topologies;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.mltestbed.heuristics.PSO.BaseSwarm;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;
import org.mltestbed.util.Util;
public class Lbest extends Topology
{
	private static final String PROPS = "lBestNeighbourhood.properties";
	private List<Particle> swarmMembers = null;
	private int size = 0;
	private ConcurrentHashMap<Particle, ArrayList<Particle>> neighbourhoods = new ConcurrentHashMap<Particle, ArrayList<Particle>>();

	public Lbest(BaseSwarm swarm)
	{
		super(swarm);
		setDescription("Lbest or Ring Topology");

	}
	/*
	 * @Override protected ArrayList<Particle> getNeighbourhood(int particle) {
	 * ArrayList<Particle> neighbourhood = new ArrayList<Particle>(); try { int
	 * k = Integer.parseInt(params.getProperty("k", "2")); // neighbourhood =
	 * new ArrayList<Particle>(k + 1); List<Particle> swarmMembers =
	 * Collections.synchronizedList(swarm.getSwarmMembers()); int startIndex =
	 * particle - (k / 2);
	 * 
	 * // int endIndex =(particle +(k/2)) % swarm.getSwarm().size(); int size =
	 * swarmMembers.size(); if (startIndex < 0) startIndex = size + startIndex;
	 * Util.getSwarmui().updateLog("particle = " + particle + " " +
	 * "startIndex = " + startIndex + "\n"); // "endIndex = "+ endIndex +"\n");
	 * for (int j = startIndex; j <= startIndex + k; j++) {
	 * Util.getSwarmui().updateLog("Neighbourhood index=" + j);
	 * Util.getSwarmui().updateLog(
	 * "Neighbourhood Member=(Neighbourhood index % swarm size)=" +
	 * Integer.valueOf((j % size)).toString());
	 * neighbourhood.add(swarmMembers.get((j % size)));
	 * 
	 * } neighbourhoodXML(particle, neighbourhood);
	 * swarmMembers.get(particle).setFuncSpecific(neighbourhoodSpecific, false);
	 * 
	 * System.out.println(neighbourhoodSpecific); } catch (NumberFormatException
	 * e) { Exception e1 = new Exception(e.getMessage() + " particle=" +
	 * particle + " neighbourhood size=" + neighbourhood.size(), e);
	 * Log.log(Level.SEVERE, e1); e.printStackTrace(); } catch
	 * (ArrayIndexOutOfBoundsException e) { Exception e1 = new
	 * Exception(e.getMessage() + " particle=" + particle +
	 * " neighbourhood size=" + neighbourhood.size(), e); Log.log(Level.SEVERE,
	 * e1); e.printStackTrace(); } catch (Exception e) { Exception e1 = new
	 * Exception(e.getMessage() + " particle=" + particle +
	 * " neighbourhood size=" + neighbourhood.size(), e); Log.log(Level.SEVERE,
	 * e1); e.printStackTrace(); } return neighbourhood; }
	 */

	@Override
	protected ArrayList<Particle> getNeighbourhood(int particle)
			throws Exception
	{
		ArrayList<Particle> neighbourhood = new ArrayList<Particle>();
		Particle particleObj = swarmMembers.get(particle);
		try
		{
			int k = Integer.parseInt(params.getProperty("k", "2"));
//			neighbourhood = new ArrayList<Particle>(k + 1);
			int startIndex = particle - (k / 2);
			// int endIndex =(particle +(k/2)) % swarm.getSwarm().size();
			while (swarmMembers == null)
			{
				swarmMembers = Collections
						.synchronizedList(swarm.getSwarmMembers());
				size = swarmMembers.size();
			}

			if (particleObj != null)
			{
				if (startIndex < 0)
					startIndex = size + startIndex;

				Util.getSwarmui().updateLog("particle = " + particle + " "
						+ "startIndex = " + startIndex + "\n");
				// "endIndex = "+ endIndex +"\n");
				for (int j = startIndex; j <= startIndex + k; j++)
				{
//					Util.getSwarmui().updateLog("Neighbourhood index=" + j);
//					Util.getSwarmui().updateLog(
//							"Neighbourhood Member=(Neighbourhood index % swarm size)="
//									+ Integer.valueOf((j % size)).toString());
					neighbourhood.add(swarmMembers.get((j % size)));

				}
				neighbourhoods.put(particleObj, neighbourhood);
			} else
				neighbourhood = neighbourhoods.get(particleObj);
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
		if(particleObj!= null)
			particleObj.setFuncSpecific(neighbourhoodSpecific, false);
		System.out.println(neighbourhoodSpecific);
		return neighbourhood;
	}

	@Override
	public void createParams()
	{
		params = new Properties();
		params.put("k", "2");
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
