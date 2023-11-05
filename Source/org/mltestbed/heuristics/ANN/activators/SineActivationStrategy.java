
package org.mltestbed.heuristics.ANN.activators;

import java.io.Serializable;

public class SineActivationStrategy extends ActivationStrategy
		implements
			Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public double activate(double weightedSum)
	{
		return Math.sin(weightedSum);
	}

	public double derivative(double weightedSum)
	{
		return Math.cos(weightedSum);
	}

	public SineActivationStrategy copy()
	{
		return new SineActivationStrategy();
	}
}
