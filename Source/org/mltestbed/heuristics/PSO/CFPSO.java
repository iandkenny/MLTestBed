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
import org.mltestbed.util.Util;

/**
 * @author Ian Kenny
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class CFPSO extends ClassicPSO
{
	private static final String CF_PSO = "Constriction Factor PSO";
	private double phi;
	private double K;

	/**
	 * 
	 */
	public CFPSO()
	{
		super();
		setDescription(CF_PSO);
	}

	/**
	 * @param o
	 */
	public CFPSO(BaseSwarm o)
	{
		super(o);
		setDescription(CF_PSO);
	}

	/**
	 * @param neighbourhood
	 * @param params
	 * @param runparams
	 */
	protected CFPSO(Topology neighbourhood, Properties params,
			Properties runparams)
	{
		super(neighbourhood, params, runparams);
		setDescription(CF_PSO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mltestbed.heuristics.BaseSwarm#afterCalc(org.mltestbed.util.
	 * Particle)
	 */
	public void afterCalc(Particle particle)
	{
		// TODO Auto-generated method stub

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.heuristics.BaseSwarm#afterIter(long)
	 */
	public void afterIter(long iteration)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mltestbed.heuristics.BaseSwarm#beforeCalc(org.mltestbed.util.
	 * Particle)
	 */
	public void beforeCalc(Particle particle)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.heuristics.BaseSwarm#beforeIter(long)
	 */
	public void beforeIter(long iteration)
	{
		// TODO Auto-generated method stub

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.heuristics.BaseSwarm#calcNew(org.mltestbed.util.
	 * Particle)
	 */
	@Override
	protected Particle calcNew(int index) throws Exception
	{
		if (c1 == Double.NaN || c2 == Double.NaN || VMax == Double.NaN
				|| K == Double.NaN)
			throw new Exception("A parmeter is invalid");

		Particle particle = getSwarmMembers().get(index);
		Vector<Double> position = particle.getPosition();
		Vector<Double> velocity = particle.getVelocity();
		Vector<Double> gpbest = neighbourhood.getNbest(index).getPbest();
		for (int i = 0; i < testFunction.getMDimension(); i++)
		{
			double v = velocity.get(i) // get the velocity
					.doubleValue(); // for the current dimension
			double pb = particle.getPbest().get(i).doubleValue();
			double p = position.get(i).doubleValue();
			double gb = gpbest.get(i).doubleValue();

			double r1 = rnd.nextDouble();
			double r2 = rnd.nextDouble();
//			double newv = ((c1 * r1) * (pb - p)) + ((c2
//					* r2) * (gb  - p));
			double newv = add((c1 * r1) * (subtract(pb, p)),
					((c2 * r2) * subtract(gb, p)));
			Util.getSwarmui().updateLog("particle =" + index + " dim=" + i);
			Util.getSwarmui().updateLog("v before new=" + v + " newv=" + newv);
			v = K * (v + newv);
			Util.getSwarmui().updateLog("K=" + K + " v=" + v);
			v = vmaxAdjust(i, v);
			Util.getSwarmui().updateLog("v post adjustment=" + v);
//		    v = v >= -Math.abs(testFunction.getMMin(i)- testFunction.getMMax(i)) ? v : -Math.abs(testFunction.getMMin(i)- testFunction.getMMax(i));
//			v = v <= Math.abs(testFunction.getMMin(i)- testFunction.getMMax(i)) ? v : Math.abs(testFunction.getMMin(i)- testFunction.getMMax(i));
			p += v;
			Util.getSwarmui().updateLog("New p before adjustment=" + p);
//			p = posAdjust(i, p);

			velocity.set(i, v);
			position.set(i, p);

		}
		particle.setPosition(position);
		particle.setVelocity(velocity);
//		Util.getSwarmui().updateLog(particle.getMPosition().toString());
		return particle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.heuristics.PSO.ClassicPSO#constraints()
	 */
	@Override
	public boolean constraints() throws Exception
	{
		boolean flag = true;
		double c1 = Double.valueOf(params.getProperty("c1"));
		double c2 = Double.valueOf(params.getProperty("c2"));
		if (!(c1 + c2 > 4))
			flag = false;
		flag &= super.constraints();
		return flag;
	}

	@Override
	public void createParams()
	{
		super.createParams();
		params.setProperty("c1", "2.05");
		params.setProperty("c2", "2.05");
		params.setProperty("VMax", "4");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.heuristics.PSO.BaseSwarm#init()
	 */
	@Override
	protected void init() throws Exception
	{
		super.init();
		phi = c1 + c2;
		K = 2 / (Math.abs(2 - phi - Math.sqrt((phi * phi) - (4 * phi))));
	}
}
