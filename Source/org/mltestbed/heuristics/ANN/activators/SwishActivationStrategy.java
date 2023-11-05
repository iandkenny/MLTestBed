package org.mltestbed.heuristics.ANN.activators;

import java.io.Serializable;

public class SwishActivationStrategy extends ActivationStrategy
		implements
			Serializable
{
	private static final int beta = 1;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public double activate(double weightedSum)
	{
		return weightedSum / (Math.pow(Math.E, -weightedSum) + beta);
	}

	public double derivative(double weightedSum)
	{
		return (weightedSum * Math.pow(Math.E, -weightedSum)
				+ (Math.pow(Math.E, -weightedSum) + beta)
						/ Math.pow(Math.pow(Math.E, -weightedSum) + beta, 2));
	}

	public SwishActivationStrategy copy()
	{
		return new SwishActivationStrategy();
	}
}
