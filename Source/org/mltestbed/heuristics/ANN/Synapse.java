
package org.mltestbed.heuristics.ANN;

public class Synapse
{

	private String id;
	private Neuron sourceNeuron;
	private double weight = 1e-9;
	private double output = 0;

	public Synapse(Neuron sourceNeuron, double weight)
	{
		this.id = sourceNeuron.getId() + "."
				+ sourceNeuron.getSynapses().size();
		this.sourceNeuron = sourceNeuron;
		this.weight = weight;
	}
	public Synapse(String id, Neuron sourceNeuron, double weight)
	{
		this.id = id;
		this.sourceNeuron = sourceNeuron;
		this.weight = weight;
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
		output = getWeight() * getSourceNeuron().getOutput();
		return output;
	}
	public double getPreviousOutput()
	{
		return output;
	}

	public Neuron getSourceNeuron()
	{
		return sourceNeuron;
	}

	public double getWeight()
	{
		return weight;
	}

	public String getXML()
	{
		return "<Synapse id=\"" + id + "\" sourceNeuron = \""
				+ sourceNeuron.getId() + "\" weight = "
				+ Double.toString(weight) + "/>";
	}
	public void setWeight(double weight)
	{
		this.weight = weight;
	}
}
