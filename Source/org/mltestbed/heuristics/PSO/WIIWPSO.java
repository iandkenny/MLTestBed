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
public class WIIWPSO extends ClassicPSO
{
	private static final String New_IWPSO = "New IWPSO based include Worst Influance";
	private double c3 = Double.NaN;
	private boolean decrementW;
	private double maxw = Double.NaN;
	private double minw = Double.NaN;
	private double w = Double.NaN;
	/**
	 * 
	 */
	public WIIWPSO()
	{
		super();
		setDescription(New_IWPSO);
	}

	/**
	 * @param o
	 */
	public WIIWPSO(BaseSwarm o)
	{
		super(o);
		setDescription(New_IWPSO);
		c3 = ((WIIWPSO) o).c3;
		decrementW = ((WIIWPSO) o).decrementW;
		maxw = ((WIIWPSO) o).maxw;
		minw = ((WIIWPSO) o).minw;
		w = ((WIIWPSO) o).w;
	}

	/**
	 * 
	 * 
	 */
	public WIIWPSO(Topology neighbourhood, Properties params,
			Properties runparams) throws Exception
	{
		super(neighbourhood, params, runparams);
		setDescription(New_IWPSO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mltestbed.heuristics.BaseSwarm#afterCalc(org.mltestbed.util.Particle)
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
	 * @see org.mltestbed.heuristics.BaseSwarm#beforeCalc(org.mltestbed.util.
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

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.heuristics.PSO.ClassicPSO#calcNew(int)
	 */
	@Override
	protected Particle calcNew(int index) throws Exception
	{
		if (Double.isNaN(c1) || Double.isNaN(c2) || Double.isNaN(c3)
				|| Double.isNaN(VMax) || Double.isNaN(w) || Double.isNaN(minw)
				|| Double.isNaN(maxw))
			throw new Exception("A parmeter is invalid");

		// Random rnd = new Random();
		Particle particle = getSwarmMembers().get(index);
		Vector<Double> position = particle.getPosition();
		Vector<Double> velocity = particle.getVelocity();
		Vector<Double> gpbest = neighbourhood.getNbest(index).getPbest();
		Vector<Double> gpworst = neighbourhood.getNworst(index).getPbest();
		for (int i = 0; i < testFunction.getMDimension(); i++)
		{
			double v = velocity.get(i) // get the velocity
					.doubleValue(); // for the current dimension
			double pb = particle.getPbest().get(i).doubleValue();
			double p = position.get(i).doubleValue();
			double gb = gpbest.get(i).doubleValue();
			double gw = gpworst.get(i).doubleValue();

			double newv = add(
					add(((c1 * rnd.nextDouble())
							* (Math.signum(pb) * subtract(pb, p))),
							((c2 * rnd.nextDouble())
									* (Math.signum(gb) * subtract(gb, p)))),
					((c3 * rnd.nextDouble())
							* (Math.signum(gb) * subtract(gw, p))));
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
//			p=posAdjust(i, p);
			velocity.set(i, v);
			position.set(i, p);

		}
		particle.setPosition(position);
		particle.setVelocity(velocity);
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
	 * @see
	 * org.mltestbed.heuristics.PSO.ClassicPSO#copy(org.mltestbed.heuristics.PSO
	 * .BaseSwarm)
	 */
	@Override
	protected void copy(BaseSwarm o)
	{
		this.w = ((WIIWPSO) o).w;
		this.maxw = ((WIIWPSO) o).maxw;
		this.minw = ((WIIWPSO) o).minw;
		this.decrementW = ((WIIWPSO) o).decrementW;
		this.c3 = ((WIIWPSO) o).c3;
		super.copy(o);
	}
	@Override
	public void createParams()
	{
		params.setProperty("c1", "1.5");// after van den bergh
		params.setProperty("c2", "1.5"); // after van den bergh
		params.setProperty("c3", "1.5"); // after van den bergh
		params.setProperty("VMax", "4.0");// after van den bergh

		params.setProperty("w", "0.729");// after van den
											// bergh
		params.setProperty("minw", "0.4"); // after Shi
		params.setProperty("maxw", "0.9"); // after Shi
		params.setProperty("decrementW", "false");
		params.setProperty("VMax", "unset");
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
		w = maxw = Double.valueOf(params.getProperty("w", "0.729"))
				.doubleValue();

		try
		{
//				c1 = Double.valueOf(params.getProperty("c1")).doubleValue();
//				c2 = Double.valueOf(params.getProperty("c2")).doubleValue();
//				VMax = Double.valueOf(params.getProperty("VMax")).doubleValue();
			maxw = Double
					.parseDouble(params.getProperty("maxw", String.valueOf(w)));
			minw = Double.parseDouble(params.getProperty("minw", "0.4"));
		} catch (NumberFormatException e)
		{
			throw new Exception("A parameter is not a recognised number");
//				e.printStackTrace();
		}
		decrementW = Boolean
				.parseBoolean(params.getProperty("decrementW", "true"));
	}
}
