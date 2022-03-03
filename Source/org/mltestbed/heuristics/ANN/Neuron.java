package org.mltestbed.heuristics.ANN;

import java.util.ArrayList;
import java.util.List;

import org.mltestbed.heuristics.ANN.activators.ActivationStrategy;

public class Neuron
{

	private String id;
	private List<Synapse> inputs;
	private ActivationStrategy activationStrategy;
	private double output;
	private double derivative;
	private double weightedSum;
	private double error;

	public Neuron(String id, ActivationStrategy activationStrategy)
	{
		this.id = id;
		inputs = new ArrayList<Synapse>();
		this.activationStrategy = activationStrategy;
		error = 0;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	public void addInput(Synapse input)
	{
		inputs.add(input);
	}

	public List<Synapse> getInputs()
	{
		return this.inputs;
	}

	public double[] getWeights()
	{
		double[] weights = new double[inputs.size()];

		int i = 0;
		for (Synapse synapse : inputs)
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
				+ activationStrategy.getClass().getName() + "\">";
		for (Synapse synapse : inputs)
		{
			buf += synapse.getXML() + System.getProperty("line.separator");
		}
		buf = "</Neuron>";
		return buf;
	}

	private void calculateWeightedSum()
	{
		weightedSum = 0;
		for (Synapse synapse : inputs)
		{
			weightedSum += synapse.getWeight()
					* synapse.getSourceNeuron().getOutput();
		}
	}

	public void activate()
	{
		calculateWeightedSum();
		output = activationStrategy.activate(weightedSum);
		derivative = activationStrategy.derivative(output);
	}

	public double getOutput()
	{
		return this.output;
	}

	public void setOutput(double output)
	{
		this.output = output;
	}

	public double getDerivative()
	{
		return this.derivative;
	}

	public ActivationStrategy getActivationStrategy()
	{
		return activationStrategy;
	}

	public double getError()
	{
		return error;
	}

	public void setError(double error)
	{
		this.error = error;
	}
}
