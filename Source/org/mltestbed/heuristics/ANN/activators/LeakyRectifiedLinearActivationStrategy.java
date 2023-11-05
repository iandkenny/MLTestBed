package org.mltestbed.heuristics.ANN.activators;

import java.io.Serializable;

public class LeakyRectifiedLinearActivationStrategy extends ActivationStrategy
		implements
			Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double alpha=0.3;


	/**
	 * 
	 */
	protected LeakyRectifiedLinearActivationStrategy(double alpha)
	{
		super();
		this.alpha = alpha;
	}

	public double activate(double weightedSum)
	{
		return Math.max(weightedSum/alpha,weightedSum);
	}

	public ActivationStrategy copy()
	{
		return new LeakyRectifiedLinearActivationStrategy(this.getAlpha());
	}

	public double derivative(double weightedSum)
	{
		return 1;
	}

	/**
	 * @return the alpha
	 */
	public double getAlpha()
	{
		return alpha;
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(double alpha)
	{
		this.alpha = alpha;
	}
}
