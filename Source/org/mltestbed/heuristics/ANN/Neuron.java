package org.mltestbed.heuristics.ANN;

import java.util.ArrayList;
import java.util.List;

import org.mltestbed.heuristics.ANN.activators.ActivationStrategy;

public class Neuron
{

	private ActivationStrategy activationStrategy;
	private double derivative;
	private boolean dropout;
	private double error;
	private String id;
	private double output;

	private boolean recurrent = false;

	private List<Synapse> synapses;
	private double weightedSum;
	public Neuron(String id, ActivationStrategy activationStrategy,
			boolean dropout)
	{
		this.id = id;
		synapses = new ArrayList<Synapse>();
		this.activationStrategy = activationStrategy;
		this.dropout = dropout;
		error = 0;
	}
	public void activate()
	{
		if (!dropout && activationStrategy != null)
		{
			calculateWeightedSum();
			output = activationStrategy.activate(weightedSum);
			derivative = activationStrategy.derivative(output);
		} else
		{
			output = derivative = 0.0;
		}
	}

	public void addInput(Synapse input)
	{
		synapses.add(input);
	}

	private void calculateWeightedSum()
	{
		weightedSum = 0;
		for (Synapse synapse : synapses)
		{
			weightedSum += (recurrent)
					? synapse.getPreviousOutput() * synapse.getOutput()
					: synapse.getOutput();
		}
	}

	public ActivationStrategy getActivationStrategy()
	{
		return activationStrategy;
	}

	public double getDerivative()
	{
		return this.derivative;
	}

	public double getError()
	{
		return error;
	}
	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	public double getOutput()
	{
		return this.output;
	}

	public List<Synapse> getSynapses()
	{
		return this.synapses;
	}

	public double[] getWeights()
	{
		double[] weights = new double[synapses.size()];

		int i = 0;
		for (Synapse synapse : synapses)
		{
			weights[i] = synapse.getWeight();
			i++;
		}

		return weights;
	}

	public String getXML()
	{
		String buf;
		buf = "<Neuron id = \"" + id + "\" activate = \""
				+ activationStrategy.getClass().getName() + "\" dropout=\""
				+ Boolean.toString(isDropout()) + "\">";
		for (Synapse synapse : synapses)
		{
			buf += synapse.getXML() + System.getProperty("line.separator");
		}
		buf = "</Neuron>";
		return buf;
	}

	/**
	 * @return the dropout
	 */
	public boolean isDropout()
	{
		return dropout;
	}

	/**
	 * @return the recurrent
	 */
	public boolean isRecurrent()
	{
		return recurrent;
	}

	/**
	 * @param dropout
	 *            the dropout to set
	 */
	public void setDropout(boolean dropout)
	{
		this.dropout = dropout;
	}

	public void setError(double error)
	{
		this.error = error;
	}

	public void setOutput(double output)
	{
		this.output = output;
	}

	/**
	 * @param recurrent
	 *            the recurrent to set
	 */
	public void setRecurrent(boolean recurrent)
	{
		this.recurrent = recurrent;
	}
}
