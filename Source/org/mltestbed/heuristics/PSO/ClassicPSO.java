/*
 * Created on 12-Mar-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mltestbed.heuristics.PSO;

import java.util.Properties;
import java.util.Vector;

import org.mltestbed.topologies.Topology;
import org.mltestbed.util.Particle;






/**
 * @author Ian Kenny
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ClassicPSO extends BaseSwarm
{
	private static final String CLASSIC_PSO_NO_INERTIA_WEIGHT = "Classic PSO - no inertia weight";

	protected double c1 = Double.NaN;
	protected double c2 = Double.NaN;
	/**
	 * 
	 */
	public ClassicPSO()
	{
		super();
		setDescription(CLASSIC_PSO_NO_INERTIA_WEIGHT);
	}
	/**
	 * @param o
	 */
	public ClassicPSO(BaseSwarm o)
	{
		super(o);
		c1 = ((ClassicPSO)o).c1;
		c2= ((ClassicPSO)o).c2;
		setDescription(CLASSIC_PSO_NO_INERTIA_WEIGHT);
	}

	
	/**
	 * @throws Exception
	 *  
	 */
	public ClassicPSO(Topology neighbourhood, Properties params, Properties runparams)
	{
		super(neighbourhood, params,runparams);
		setDescription(CLASSIC_PSO_NO_INERTIA_WEIGHT);
	}

	/* (non-Javadoc)
	 * @see org.mltestbed.heuristics.BaseSwarm#afterCalc(org.mltestbed.util.Particle)
	 */
	public void afterCalc(int index)
	{
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.mltestbed.heuristics.BaseSwarm#afterIter(long)
	 */
	public void afterIter(long iteration)
	{
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see org.mltestbed.heuristics.BaseSwarm#beforeCalc(org.mltestbed.util.Particle)
	 */
	public void beforeCalc(int index )
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.mltestbed.heuristics.BaseSwarm#beforeIter(long)
	 */
	public void beforeIter(long iteration)
	{
		// TODO Auto-generated method stub

	}

	
	/* (non-Javadoc)
	 * @see org.mltestbed.heuristics.BaseSwarm#calcNew(org.mltestbed.util.Particle)
	 */
	protected Particle calcNew(int index) throws Exception
	{
	
		
		if (c1 == Double.NaN || c2 == Double.NaN || VMax == Double.NaN)
			throw new Exception("A parmeter is invalid");
		
	
		Particle particle = getSwarmMembers().get((int) index);
		Vector<Double> position = particle.getPosition();
		Vector<Double> velocity = particle.getVelocity();
		for(int i=0; i<testFunction.getMDimension();i++)
		{
			try
			{
				double v= velocity.get(i) // get the velocity
								.doubleValue(); // for the current dimension
				double pb = particle.getPbest().get(i).doubleValue();
				double p = position.get(i).doubleValue();
				double gb = neighbourhood.getNbest(index).getPbest().get(i).doubleValue();
//				double newv = ((c1 * rnd.nextDouble()) * (pb - p)) + ((c2
//						* rnd.nextDouble()) * (gb  - p));
				double newv = add((c1 * rnd.nextDouble()) * (subtract(pb , p)) , ((c2
						* rnd.nextDouble()) * subtract (gb , p)));
				v +=newv;
				v = vmaxAdjust(i, v);
//		    v = v >= -Math.abs(testFunction.getMMin(i)- testFunction.getMMax(i)) ? v : -Math.abs(testFunction.getMMin(i)- testFunction.getMMax(i));
//			v = v <= Math.abs(testFunction.getMMin(i)- testFunction.getMMax(i)) ? v : Math.abs(testFunction.getMMin(i)- testFunction.getMMax(i));
				p += v;
//				p = posAdjust(i, p);
				velocity.set(i,v);
				position.set(i,p);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		particle.setPosition(position);
		particle.setVelocity(velocity);
		return particle;
	}
	@Override
	public boolean constraints() throws Exception
	{
		// TODO Auto-generated method stub
		return true;
	}



	@Override
	protected void copy(BaseSwarm o)
	{
		this.bFinished = o.bFinished;
		this.bHeirarchy = o.bHeirarchy;
		this.bMaster = o.bMaster;
		this.boundary = o.boundary;
		this.bStop = o.bStop;
		this.bStopExecution = o.bStopExecution;
		this.bSwitchOps = o.bSwitchOps;
		this.bWait = o.bWait;
		this.evals = o.evals;
		this.evalType = o.evalType;
		this.gbestparticle = o.gbestparticle;
		this.heir = o.heir;
		this.masterSync = o.masterSync;
		this.maxruns = o.maxruns;
		this.mDescription = o.mDescription;
		this.mIterations = o.mIterations;
		this.mSwarm = o.mSwarm;
		this.mSwarmNo = o.mSwarmNo;
		this.neighbourhood = o.neighbourhood;
		this.noMembers = o.noMembers;
		this.output = o.output;
		this.params = o.params;
		this.passGB = o.passGB;
		this.results = o.results;
		this.rnd = o.rnd;
		this.run = o.run;
		this.runparams = o.runparams;
		this.startTime = o.startTime;
		this.stepSize = o.stepSize;
		this.testFunction = o.testFunction;
		this.timer = o.timer;
		this.VMax = o.VMax;
		this.bReset = o.bReset;
		
		//ClassicPSO
		this.c1 = ((ClassicPSO)o).c1;
		this.c2 = ((ClassicPSO)o).c2;
		
	}

	@Override
	public void createParams()
	{
		params = new Properties();
		params.setProperty("c1","2.0");
		params.setProperty("c2","2.0");
		params.setProperty("VMax","2.0");
		
		
	}

	/* (non-Javadoc)
	 * @see org.mltestbed.heuristics.PSO.BaseSwarm#init()
	 */
	@Override
	protected void init() throws Exception
	{
		c1 = Double.valueOf(params.getProperty("c1"));
		c2 = Double.valueOf(params.getProperty("c2"));
		try
		{
			VMax = Double.valueOf(params.getProperty("VMax"));
		} catch (NumberFormatException e)
		{
			VMax =0;
//			e.printStackTrace();
		}
		super.init();
	}


}
