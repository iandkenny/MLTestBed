package org.mltestbed.heuristics.ANN.activators;

import java.io.Serializable;

public class LinearActivationStrategy extends ActivationStrategy
		implements
			Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public double activate(double weightedSum)
	{
		return weightedSum;
	}

	public double derivative(double weightedSum)
	{
		return 1;
	}

	public ActivationStrategy copy()
	{
		return new LinearActivationStrategy();
	}
}
