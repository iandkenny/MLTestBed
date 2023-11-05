/*
 * Created on 12-Mar-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mltestbed.heuristics.PSO;

import java.util.ArrayList;
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
public class CLPSO extends ClassicPSO
{
	private static final String CL_PSO = "Comprehesive Learning PSO";
	private double w = Double.NaN;
	private double minw = Double.NaN;
	private double maxw = Double.NaN;
	private boolean decrementW;

	/**
	 * 
	 */
	public CLPSO()
	{
		super();
		setDescription(CL_PSO);
	}

	/**
	 * @param o
	 */
	public CLPSO(BaseSwarm o)
	{
		super(o);
		setDescription(CL_PSO);
		w = ((CLPSO) o).w;
		minw = ((CLPSO) o).minw;
		maxw = ((CLPSO) o).maxw;
		decrementW = ((CLPSO) o).decrementW;
	}

	/**
	 * @param neighbourhood
	 * @param params
	 * @param runparams
	 */
	protected CLPSO(Topology neighbourhood, Properties params,
			Properties runparams)
	{
		super(neighbourhood, params, runparams);
		setDescription(CL_PSO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.heuristics.PSO.ClassicPSO#calcNew(int)
	 */
	@Override
	protected Particle calcNew(int index) throws Exception
	{
		if (Double.isNaN(c1) || Double.isNaN(c2) || Double.isNaN(VMax)
				|| Double.isNaN(w) || Double.isNaN(minw) || Double.isNaN(maxw))
			throw new Exception("A parmeter is invalid");

		// Random rnd = new Random();
		Particle particle = getSwarmMembers().get(index);
		Vector<Double> position = particle.getPosition();
		Vector<Double> velocity = particle.getVelocity();
		// Vector<Double> gpbest = neighbourhood.getNbest(index).getPbest();
		for (int i = 0; i < testFunction.getMDimension(); i++)
		{
			double v = velocity.get(i) // get the velocity
					.doubleValue(); // for the current dimension
			double pb = selectPBestD(index, i).doubleValue();
			double p = position.get(i).doubleValue();
			// double gb = gpbest.get(i).doubleValue();

			double newv = ((c1 * rnd.nextDouble()) * subtract(pb, p));
			v = w * (v + newv);
			Util.getSwarmui().updateLog("particle =" + index + " dim=" + i);
			Util.getSwarmui().updateLog("w=" + w + " v=" + v);

			v = vmaxAdjust(i, v);
			Util.getSwarmui().updateLog("v post adjustment=" + v);
			// v = v >= -Math.abs(testFunction.getMMin(i)-
			// testFunction.getMMax(i)) ? v : -Math.abs(testFunction.getMMin(i)-
			// testFunction.getMMax(i));
			// v = v <= Math.abs(testFunction.getMMin(i)-
			// testFunction.getMMax(i)) ? v : Math.abs(testFunction.getMMin(i)-
			// testFunction.getMMax(i));
			p += v;
			// p = posAdjust(i, p);
			velocity.set(i, v);
			position.set(i, p);

		}
		particle.setPosition(position);
		particle.setVelocity(velocity);
		return particle;
	}

	/**
	 * @param particle
	 * @param dim
	 * @return best position for dimension
	 */
	private Double selectPBestD(int particle, int dim)
	{
		ArrayList<Particle> swarmMembers = getSwarmMembers();
		double pb = swarmMembers.get(particle).getPbest().get(dim);
		// Note: swarm index is 0 based
		double pc = 0.05
				+ 0.45 * (Math.exp(10 * particle) / swarmMembers.size() - 1)
						/ (Math.exp(10) - 1);
		if (rnd.nextDouble() < pc)
		{
			Particle p1 = swarmMembers
					.get(rnd.nextInt(swarmMembers.size() - 1));
			Particle p2 = swarmMembers
					.get(rnd.nextInt(swarmMembers.size() - 1));
			if (p1.getBestScore() > p2.getBestScore())
				pb = p1.getPbest().get(dim);
			else
				pb = p2.getPbest().get(dim);

		}
		return pb;

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.heuristics.BaseSwarm#beforeCalc(org.mltestbed.util
	 * .Particle)
	 */
	public void beforeCalc(Particle particle)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.heuristics.BaseSwarm#afterCalc(org.mltestbed.util
	 * .Particle)
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
		if (decrementW)
			w = ((maxw - minw) * (getMaxIterations() - iteration)
					/ getMaxIterations()) + minw;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.heuristics.BaseSwarm#beforeIter(long)
	 */
	public void beforeIter(long iteration)
	{

	}

	@Override
	public void createParams()
	{
		super.createParams();

		params.setProperty("c1", "1.5");// after van den bergh
		params.setProperty("c2", "1.5"); // after van den bergh
		params.setProperty("VMax", "4.0");// after van den bergh

		params.setProperty("w", "0.729");// after van den
											// bergh
		params.setProperty("minw", "0.4"); // after Shi
		params.setProperty("maxw", "0.9"); // after Shi
		params.setProperty("decrementW", "false");
		// params.setProperty("VMax", "unset");// was 4

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
		if (w < minw)
		{
			flag = false;
			throw new Exception(
					"Starting value of w is less than the minimum, minw");
		} else if (maxw < minw)
		{
			flag = false;
			throw new Exception(
					"Starting value of maxw is less than the minimum, minw");
		}

		flag &= super.constraints();
		return flag;
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
		try
		{
			w = maxw = Double.valueOf(params.getProperty("w", "0.729"))
					.doubleValue();
			c1 = Double.valueOf(params.getProperty("c1")).doubleValue();
			c2 = Double.valueOf(params.getProperty("c2")).doubleValue();
			VMax = Double.valueOf(params.getProperty("VMax")).doubleValue();
			maxw = Double
					.parseDouble(params.getProperty("maxw", String.valueOf(w)));
			minw = Double.parseDouble(params.getProperty("minw", "0.4"));
		} catch (NumberFormatException e)
		{
			throw new Exception("A parameter is not a recognised number");
			// e.printStackTrace();
		}
		decrementW = Boolean
				.parseBoolean(params.getProperty("decrementW", "true"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mltestbed.heuristics.PSO.ClassicPSO#copy(org.mltestbed.heuristics
	 * .PSO.BaseSwarm)
	 */
	@Override
	protected void copy(BaseSwarm o)
	{
		this.w = ((CLPSO) o).w;
		this.maxw = ((CLPSO) o).maxw;
		this.minw = ((CLPSO) o).minw;
		this.decrementW = ((CLPSO) o).decrementW;

		super.copy(o);
	}

}
