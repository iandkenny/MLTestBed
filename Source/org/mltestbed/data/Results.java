/*
 * Created on 24-Apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mltestbed.data;

import java.util.Iterator;
import java.util.logging.Level;

import org.mltestbed.heuristics.PSO.BaseSwarm;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;
import org.mltestbed.util.Util;

/**
 * @author Ian Kenny
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class Results implements Cloneable
{

	private static final String NOT_BUFFERING_STORING_RESULTS_IMMEDIATELY = "Not Buffering, storing results immediately";

	private static final String RESULTS_COMPLETE = "Swarm Results Complete";
	private static final String TERMINATING_CLEARING_BUFFERS = "Terminating... Clearing buffers";
	private boolean log = false;
	private OutputResults output;
	private BaseSwarm swarm;
	/**
	 * 
	 */
	public Results(BaseSwarm swarm, OutputResults out)
	{
		this.swarm = swarm;
		output = out;
		output.setSwarm(swarm);

	}
	public Results(OutputResults out)
	{
		output = out;

	}
	public void destroy()
	{
		output = null;
		swarm = null;
	}
	/**
	 * 
	 */
	public void clearQueue()
	{
		output.clearQueue();

	}
	public Object clone()
	{
		Results o = new Results((BaseSwarm) swarm.clone(),
				(OutputResults) output.clone());
		return o;
	}
	/**
	 * @return the run
	 */
	public long getRun()
	{
		return swarm.getRun();
	}
	public boolean isEmpty()
	{
		return output.getStoreResults().isEmpty();
	}

	/**
	 * @return the log
	 */
	public boolean isLog()
	{
		return log;
	}

	public void setEndTime()
	{
		output.setEndTime();

	}
	/**
	 * @param expDescription
	 *            The expDescription to set.
	 */
	public void setExpDescription(String expDescription)
	{
	}
	/**
	 * @param log
	 *            the log to set
	 */
	public void setLog(boolean log)
	{
		this.log = log;
		if (output != null)
			output.setLog(log);
	}

	/**
		 * 
		 */
	public void startOutput()
	{
		synchronized (output)
		{

			if (log && !output.isAlive())
			{
				// setPriority(MAX_PRIORITY);
				// output.setPriority(MAX_PRIORITY);
				output.start();
			} else
				Util.getSwarmui()
						.updateLog(NOT_BUFFERING_STORING_RESULTS_IMMEDIATELY);
		}
	}
	public synchronized void stopQueue()
	{
		Util.getSwarmui().updateLog(TERMINATING_CLEARING_BUFFERS);
		output.stopQueue();
		Util.getSwarmui().updateLog(RESULTS_COMPLETE);
	}
	/**
	 * @param run
	 * @param iter
	 * @param swarm
	 */
	public void store(long run, long iter)
	{
		try
		{
			if (iter == -1 && (!swarm.isHeirarchy() || swarm.isMaster()))
				output.writeSwarmsData(run);
			// output.storeResultsDB(run,results);
			// results.clear();
			output.storeResultsDB(run, iter);
			for (Iterator<Particle> iterator = swarm.getSwarmMembers()
					.iterator(); iterator.hasNext();)
			{
				Particle p = (Particle) iterator.next();
				p.setFuncSpecific("", true);
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

	}

	public synchronized void updateNotes(String notes)
	{
		output.updateNotes(notes);

	}

}
