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
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class FDRPSO extends ClassicPSO
{
	private static final String FDR_PSO = "Fitness Distance Ratio PSO";
	private double c3 = Double.NaN;
	private boolean decrementW;
	private double maxw = Double.NaN;
	private double minw = Double.NaN;
	private double w = Double.NaN;
	/**
	 * 
	 */
	public FDRPSO()
	{
		super();
		setDescription(FDR_PSO);
	}

	/**
	 * @param o
	 */
	protected FDRPSO(BaseSwarm o)
	{
		super(o);
		setDescription(FDR_PSO);
		c3 = ((FDRPSO) o).c3;
		w = ((FDRPSO) o).w;
		minw = ((FDRPSO) o).minw;
		maxw = ((FDRPSO) o).maxw;
		decrementW = ((FDRPSO) o).decrementW;
	}

	/**
	 * 
	 * 
	 */
	public FDRPSO(Topology neighbourhood, Properties params,
			Properties runparams) throws Exception
	{
		super(neighbourhood, params, runparams);
		setDescription(FDR_PSO);
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

	private double calcNBest(int i, Particle particle)
	{
		int nBestIndex = -1;
		double bestScore = Double.NaN;
		double scoreX = particle.getBestScore();
		for (int j = 0; j < mSwarm.size(); j++)
		{
			Particle cur = mSwarm.get(j);
			double scoreCur = cur.getBestScore();
//			if (testFunction.isMinimised())
//			{
//				//if minimised convert to cost
//				scoreCur = Double.MAX_VALUE - scoreCur;
//				scoreX = Double.MAX_VALUE - scoreX;
//			}
//			System.out.println((scoreCur - scoreX));
//			System.out.println(Math.abs(
//					cur.getPosition().get(i) - particle.getPosition().get(i)));
			double nScore = (scoreCur - scoreX) / Math.abs(
					cur.getPosition().get(i) - particle.getPosition().get(i));

//			if (testFunction.isMinimised())
//			{
//				if (nScore<bestScore || Double.isNaN(bestScore))
//				{
//					bestScore = nScore;
//					nBestIndex = j;
//				}
//			}
//			else
			if (Double.isNaN(bestScore) || nScore > bestScore)
			{
				bestScore = nScore;
				nBestIndex = j;
			}

		}
		return mSwarm.get(nBestIndex).getPosition().get(i);
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
		for (int i = 0; i < testFunction.getMDimension(); i++)
		{
			double v = velocity.get(i) // get the velocity
					.doubleValue(); // for the current dimension
			double pb = particle.getPbest().get(i).doubleValue();
			double p = position.get(i).doubleValue();
			double gb = gpbest.get(i).doubleValue();

//			double newv = ((c1 * rnd.nextDouble()) * (pb - p))
//					+ ((c2 * rnd.nextDouble()) * (gb - p))
//					+ ((c3 * rnd.nextDouble()) * (calcNBest(i, particle) - p));
			// TODO needs checking
			double newv = add(
					add(((c1 * rnd.nextDouble()) * subtract(pb, p)),
							((c2 * rnd.nextDouble()) * subtract(gb, p))),
					((c3 * rnd.nextDouble())
							* subtract(calcNBest(i, particle), p)));

			v = w * (v + newv);
			v = vmaxAdjust(i, v);

			// v = v >= -Math.abs(testFunction.getMMin(i)-
			// testFunction.getMMax(i)) ? v : -Math.abs(testFunction.getMMin(i)-
			// testFunction.getMMax(i));
			// v = v <= Math.abs(testFunction.getMMin(i)-
			// testFunction.getMMax(i)) ? v : Math.abs(testFunction.getMMin(i)-
			// testFunction.getMMax(i));
			p += v;
//			p = posAdjust(i, p);
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
			throw new Exception("Starting value of w is less than the minimum");
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
		this.w = ((FDRPSO) o).w;
		this.maxw = ((FDRPSO) o).maxw;
		this.minw = ((FDRPSO) o).minw;
		this.decrementW = ((FDRPSO) o).decrementW;
		this.c3 = ((FDRPSO) o).c3;
		super.copy(o);
	}

	@Override
	public void createParams()
	{
		super.createParams();
		params.setProperty("c1", "1.0");// after Peram et al
		params.setProperty("c2", "1.0");// after Peram et al
		params.setProperty("c3", "2.0");// after Peram et al
		params.setProperty("w", "0.729");// after van den
											// bergh
		params.setProperty("minw", "0.4"); // after Shi
		// params.setProperty("maxw","0.9"); // after Shi
		params.setProperty("decrementW", "false");
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
		maxw = w = Double.valueOf(params.getProperty("w", "0.729"))
				.doubleValue();

		try
		{
//			c1 = Double.valueOf(params.getProperty("c1","1")).doubleValue();
//			c2 = Double.valueOf(params.getProperty("c2","1")).doubleValue();
			c3 = Double.valueOf(params.getProperty("c3", "2.0")).doubleValue();
//			VMax = Double.valueOf(params.getProperty("VMax")).doubleValue();
			minw = Double.valueOf(params.getProperty("minw", "0"))
					.doubleValue();
		} catch (NumberFormatException e)
		{
			// e.printStackTrace();
		}
		decrementW = Boolean
				.parseBoolean(params.getProperty("decrementW", "true"));
	}
}
