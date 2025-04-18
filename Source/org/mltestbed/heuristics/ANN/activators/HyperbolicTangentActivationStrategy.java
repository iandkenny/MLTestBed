
package org.mltestbed.heuristics.ANN.activators;

import java.io.Serializable;

public class HyperbolicTangentActivationStrategy extends ActivationStrategy
		implements
			Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public double activate(double weightedSum)
	{
		double a = Math.exp(weightedSum);
		double b = Math.exp(-weightedSum);
		return ((a - b) / (a + b));
	}

	public double derivative(double weightedSum)
	{
		return 1 - Math.pow(activate(weightedSum), 2.0);
	}

	public HyperbolicTangentActivationStrategy copy()
	{
		return new HyperbolicTangentActivationStrategy();
	}
}
