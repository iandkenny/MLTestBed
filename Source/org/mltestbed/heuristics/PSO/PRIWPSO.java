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
public class PRIWPSO extends ClassicPSO
{
	private static final String New_PRIWPSO = "New IWPSO based - probalistic random particle influance";
	private double bias;
	private double c3 = Double.NaN;
	private boolean decrementW;
	private double maxw = Double.NaN;
	private double minw = Double.NaN;
	private double w = Double.NaN;

	/**
	 * 
	 */
	public PRIWPSO()
	{
		super();
		setDescription(New_PRIWPSO);
	}

	/**
	 * @param o
	 */
	public PRIWPSO(BaseSwarm o)
	{
		super(o);
		setDescription(New_PRIWPSO);
		bias = ((PRIWPSO) o).bias;
		c3 = ((PRIWPSO) o).c3;
		decrementW = ((PRIWPSO) o).decrementW;
		minw = ((PRIWPSO) o).minw;
		maxw = ((PRIWPSO) o).maxw;
		minw = ((PRIWPSO) o).minw;
		w = ((PRIWPSO) o).w;
	}

	/**
	 * 
	 * 
	 */
	public PRIWPSO(Topology neighbourhood, Properties params,
			Properties runparams) throws Exception
	{
		super(neighbourhood, params, runparams);
		setDescription(New_PRIWPSO);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.heuristics.BaseSwarm#afterCalc(org.mltestbed.util.
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
				|| Double.isNaN(maxw) || Double.isNaN(bias))
			throw new Exception("A parmeter is invalid");

		Particle particle = getSwarmMembers().get(index);
		Vector<Double> position = particle.getPosition();
		Vector<Double> velocity = particle.getVelocity();
		Vector<Double> gpbest = neighbourhood.getNbest(index).getPbest();

		for (int i = 0; i < testFunction.getMDimension(); i++)
		{
			double v = velocity.get(i) // get the velocity
					.doubleValue(); // for the current dimension
			double pb;
			double p = position.get(i).doubleValue();
			double gb;
			double rb;
			int ri1;
			do
			{
				ri1 = rnd.nextInt(getNoMembers());
			} while (ri1 == index);
			if (rnd.nextDouble() <= bias)
			{

				int ri2;
				do
				{
					ri2 = rnd.nextInt(getNoMembers());
				} while (ri2 == index || ri2 == ri1);
				int ri3;
				do
				{
					ri3 = rnd.nextInt(getNoMembers());
				} while (ri3 == index || ri3 == ri1 || ri3 == ri2);

				pb = getSwarmMembers().get(ri1).getPbest().get(i).doubleValue();
				gb = getSwarmMembers().get(ri2).getPbest().get(i).doubleValue();
				rb = getSwarmMembers().get(ri3).getPbest().get(i).doubleValue();
			} else
			{
				pb = particle.getPbest().get(i).doubleValue();
				gb = gpbest.get(i).doubleValue();
				rb = getSwarmMembers().get(ri1).getPbest().get(i).doubleValue();
			}
			double newv = add(
					add(((c1 * rnd.nextDouble()) * subtract(pb, p)),
							((c2 * rnd.nextDouble()) * subtract(gb, p))),
					((c3 * rnd.nextDouble()) * subtract(gb, p)));
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
			// p=posAdjust(i, p);
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
	 * @see org.mltestbed.heuristics.PSO.ClassicPSO#copy(org.mltestbed.
	 * heuristics.PSO.BaseSwarm)
	 */
	@Override
	protected void copy(BaseSwarm o)
	{
		this.w = ((PRIWPSO) o).w;
		this.maxw = ((PRIWPSO) o).maxw;
		this.minw = ((PRIWPSO) o).minw;
		this.decrementW = ((PRIWPSO) o).decrementW;
		this.c3 = ((PRIWPSO) o).c3;
		this.bias = ((PRIWPSO) o).bias;

		super.copy(o);
	}
	@Override
	public void createParams()
	{
		params.setProperty("c1", "1.25");
		params.setProperty("c2", "1.25");
		params.setProperty("c3", "1.25");
		params.setProperty("VMax", "4.0");

		params.setProperty("w", "0.729");
		params.setProperty("minw", "0.4"); // after Shi
		params.setProperty("maxw", "0.9"); // after Shi
		params.setProperty("decrementW", "false");
//		params.setProperty("VMax", "unset");
		params.setProperty("selectionBias", "0.2");
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
			c3 = Double.valueOf(params.getProperty("c3")).doubleValue();
			String buf = params.getProperty("VMax");
			if (Util.isNumeric(buf))
				VMax = Double.valueOf(buf).doubleValue();
			else
				VMax = Double.POSITIVE_INFINITY;
			maxw = Double
					.parseDouble(params.getProperty("maxw", String.valueOf(w)));
			minw = Double.parseDouble(params.getProperty("minw", "0.4"));
			bias = Double
					.parseDouble(params.getProperty("selectionBias", "0.2"));
		} catch (NumberFormatException e)
		{
			throw new Exception("A parameter is not a recognised number");
			// e.printStackTrace();
		}
		decrementW = Boolean
				.parseBoolean(params.getProperty("decrementW", "true"));
	}
}
