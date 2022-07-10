package org.mltestbed.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.mltestbed.data.Experiment;
import org.mltestbed.ui.MLUI;
import org.mltestbed.ui.Main;



public class RunExperiments extends Thread
{
	// private MLUI swarmUI;
	private HashMap<String, Experiment> experiments;

	public RunExperiments(HashMap<String, Experiment> experiments)
	{
		super("Experiment");
		this.experiments = experiments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		try
		{
			if (experiments != null)
			{
				Set<String> keySet = experiments.keySet();
				MLUI swarmui = Util.getSwarmui();
				Main.setUseMem(swarmui.getUseMemBuffersCheck().isSelected());
				for (Iterator<String> iter = keySet.iterator(); iter.hasNext();)
				{
					String key = (String) iter.next();
					Experiment experiment = (Experiment) experiments.get(key);

					experiment.runExp();
					if (experiment.isHasRun() && swarmui != null)
						swarmui.updateLog(key + " completed");
					Runtime.getRuntime().gc();
				}
				if (swarmui != null)
				{
					swarmui.updateLog("Experiments completed");
					swarmui.getProgressBar().setString("Overall progress");
					swarmui.fireExperimentsComplete();
				}

			}
		} catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.run();
	}
	public void stopExperiments()
	{
		Set<String> keySet = experiments.keySet();
		for (Iterator<String> iter = keySet.iterator(); iter.hasNext();)

		{
			String key = (String) iter.next();
			Experiment experiment = (Experiment) experiments.get(key);
			experiment.stopRunning();
			try
			{
				experiment.getSwarm().join();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			experiments.remove(key);
			experiment = null;
			Util.getSwarmui().updateLog(key + " canceled");

		}

	}

}
