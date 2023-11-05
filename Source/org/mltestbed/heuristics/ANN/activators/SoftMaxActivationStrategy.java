package org.mltestbed.heuristics.ANN.activators;

import java.io.Serializable;

import org.mltestbed.heuristics.ANN.Layer;
import org.mltestbed.heuristics.ANN.Neuron;

public class SoftMaxActivationStrategy extends ActivationStrategy
		implements
			Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Activation function which enforces that output neurons have probability
	 * distribution (sum of all outputs is one)
	 */
	private Layer layer;
	private double totalLayerInput;

	public SoftMaxActivationStrategy(final Layer layer)
	{
		this.layer = layer;
	}

	@Override
	public double activate(double netInput)
	{
		totalLayerInput = 0;
		// add max here for numerical stability - find max netInput for all
		// neurons in this layer
		double max = 0;

		for (Neuron neuron : layer.getNeurons())
		{
			totalLayerInput += Math.exp(neuron.getOutput() - max);
		}

		double output = Math.exp(netInput - max) / totalLayerInput;
		return output;
	}

	@Override
	public double derivative(double net)
	{
		return activate(net) * (1d - activate(net));
		// -yi * yj
	}
	public ActivationStrategy copy()
	{
		return new SoftMaxActivationStrategy(this.layer);
	}
}
