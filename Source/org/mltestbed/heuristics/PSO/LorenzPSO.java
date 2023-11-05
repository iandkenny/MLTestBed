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
import org.mltestbed.util.Eval;
import org.mltestbed.util.Particle;
import org.mltestbed.util.Util;

/**
 * @author Ian Kenny
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class LorenzPSO extends ClassicPSO
{
	private static final String DESCRIPTION = "Inertia Weight PSO using Lorenz Attractor equations";

	private double sigma = 10;
	private double beta = 8 / 3;
	private double rho = 28;

	private double w = 0.729;

	/**
	 * 
	 */
	public LorenzPSO()
	{
		super();
		setDescription(DESCRIPTION);
	}

	/**
	 * @param o
	 */
	public LorenzPSO(BaseSwarm o)
	{
		super(o);
		setDescription(DESCRIPTION);
		w = ((LorenzPSO) o).w;
		sigma = ((LorenzPSO) o).sigma;
		rho = ((LorenzPSO) o).rho;
		beta = ((LorenzPSO) o).beta;
	}

	/**
	 * 
	 * 
	 */
	public LorenzPSO(Topology neighbourhood, Properties params,
			Properties runparams) throws Exception
	{
		super(neighbourhood, params, runparams);
		setDescription(DESCRIPTION);
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
		if (Double.isNaN(c1) || Double.isNaN(c2) || Double.isNaN(VMax)
				|| Double.isNaN(w) || Double.isNaN(sigma) || Double.isNaN(rho)
				|| Double.isNaN(beta))
			throw new Exception("A parmeter is invalid");

		Vector<Double> nbest = neighbourhood.getNbest(index).getPbest();
		Particle particle = getSwarmMembers().get(index);
		Vector<Double> position = particle.getPosition();
		Vector<Double> velocity = particle.getVelocity();
		int mDimension = testFunction.getMDimension();
		if (particle.equals(getGBest()) && mDimension > 2)
		{
			// Lorenz Attractor equations
//			sigma = 10;
//			beta = 8/3;
//			rho = 28;
//			f = @(t,a) [-sigma*a(1) + sigma*a(2); rho*a(1) - a(2) - a(1)*a(3); -beta*a(3) + a(1)*a(2)];
			// x' = sigma*(y-x)
			// y' = x*(rho - z) - y
			// z' = x*y - beta*z
			// int d = (int) (mIterations * rnd.nextDouble());
			// for (int j = 0; j < d; j++)
			Vector<Double> oldposition = new Vector<Double>(nbest);
			int i = 2;
			while (i < mDimension)
			{
				int j = 0;
				switch (j)
				{
					case 0 :

						velocity.set(i - 2,
								velocity.get(i-2)+(rnd.nextDouble() * sigma
										* (oldposition.get(i - 1).doubleValue()
												- oldposition.get(i - 2)
														.doubleValue())));
						break;
					case 1 :

						velocity.set(i - 1,velocity.get(i-1)+ (rnd.nextDouble() * (oldposition
								.get(i - 2).doubleValue()
								* (rho - oldposition.get(i).doubleValue())
								- oldposition.get(i - 1).doubleValue())));
						break;

					default :

						velocity.set(i, velocity.get(i)+((oldposition.get(i - 2)
								* oldposition.get(i - 1))
								- (beta * oldposition.get(i).doubleValue())));
						break;
				}
				j++;
				j %= 3;
				i += 3;
			}


			for (int k = 0; k < mDimension; k++)
			{
				double v = velocity.get(k) // get the velocity
						.doubleValue(); // for the current dimension
				double pb = particle.getPbest().get(k).doubleValue();
				double p = position.get(k).doubleValue();
				double gb = nbest.get(k).doubleValue();

				double newv = add(((c1 * rnd.nextDouble()) * subtract(pb, p)),
						((c2 * rnd.nextDouble()) * subtract(gb, p)));
				v = w * (v + newv);
				Util.getSwarmui().updateLog("particle =" + index + " dim=" + k);
				Util.getSwarmui().updateLog("w=" + w + " v=" + v);

				v = vmaxAdjust(k, v);
				Util.getSwarmui().updateLog("v post adjustment=" + v);
				// v = v >= -Math.abs(testFunction.getMMin(i)-
				// testFunction.getMMax(i)) ? v :
				// -Math.abs(testFunction.getMMin(i)-
				// testFunction.getMMax(i));
				// v = v <= Math.abs(testFunction.getMMin(i)-
				// testFunction.getMMax(i)) ? v :
				// Math.abs(testFunction.getMMin(i)-
				// testFunction.getMMax(i));
				p += v;
				// p = posAdjust(i, p);
				velocity.set(k, v);
				position.set(k, p);

			}

		}
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
		return true;
	}

	@Override
	public void createParams()
	{
		sigma = 10;
		beta = 8 / 3;
		rho = 28;
		w = 0.729;
		VMax = 4.0;
		params.setProperty("VMax", "4.0");// after van den bergh
		params.setProperty("w", "0.729");// after van den bergh
		params.setProperty("sigma", "10");
		params.setProperty("beta", "8/3");
		params.setProperty("rho", "28");

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
			String buf = params.getProperty("sigma", "10");
			sigma = Eval.eval(buf);
			buf = params.getProperty("beta", "8/3");
			beta = Eval.eval(buf);
			buf = params.getProperty("rho", "28");
			rho = Eval.eval(buf);
sigma = c1;
rho =c2;
			VMax = Double.valueOf(params.getProperty("VMax")).doubleValue();
			w = Double.valueOf(params.getProperty("w")).doubleValue();
		} catch (NumberFormatException e)
		{
			throw new Exception("A parameter is not a recognised number");
			// e.printStackTrace();
		}
	}
}
