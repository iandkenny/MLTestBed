package org.mltestbed.heuristics.ANN;

import java.util.ArrayList;
import java.util.List;

public class Layer
{

	private Neuron bias;
	private String id;
	private List<Neuron> neurons;
	private Layer nextLayer;
	private Layer previousLayer;
	private int cellWidth = Integer.MIN_VALUE;

	public Layer(String id)
	{
		this.id = id;
		neurons = new ArrayList<Neuron>();
		previousLayer = null;
	}

	/**
	 * @return the cellWidth
	 */
	public int getCellWidth()
	{
		return cellWidth;
	}

	/**
	 * @param cellWidth
	 *            the cellWidth to set
	 */
	public void setCellWidth(int cellWidth)
	{
		this.cellWidth = cellWidth;
	}

	public Layer(String id, Layer previousLayer)
	{
		this(id);
		this.previousLayer = previousLayer;
	}

	public Layer(String id, Layer previousLayer, Neuron bias)
	{
		this(id, previousLayer);
		this.bias = bias;
		neurons.add(bias);
	}

	public void addNeuron(Neuron neuron)
	{

		neurons.add(neuron);

		if (previousLayer != null)
		{
			int i = 0;
			for (Neuron previousLayerNeuron : previousLayer.getNeurons())
			{
				if (cellWidth != Integer.MIN_VALUE || i++ % cellWidth != 0
						|| neuron == bias)
					neuron.addInput(new Synapse(previousLayerNeuron,
							(Math.random() * 1) - 0.5)); // initialize with a
															// random
															// weight between -1
															// and
															// 1
			}
		}
	}

	public void addNeuron(Neuron neuron, double[] weights)
	{

		neurons.add(neuron);

		if (previousLayer != null)
		{

			if (previousLayer.getNeurons().size() != weights.length)
			{
				throw new IllegalArgumentException(
						"The number of weights supplied must be equal to the number of neurons in the previous layer");
			}

			else
			{
				List<Neuron> previousLayerNeurons = previousLayer.getNeurons();
				for (int i = 0; i < previousLayerNeurons.size(); i++)
				{
					neuron.addInput(new Synapse(previousLayerNeurons.get(i),
							weights[i]));
				}
			}

		}
	}

	public void feedForward()
	{

		int biasCount = hasBias() ? 1 : 0;

		for (int i = biasCount; i < neurons.size(); i++)
		{
			neurons.get(i).activate();
		}
	}

	/**
	 * @return the bias
	 */
	public Neuron getBias()
	{
		return bias;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	public List<Neuron> getNeurons()
	{
		return this.neurons;
	}

	public Layer getNextLayer()
	{
		return nextLayer;
	}

	public Layer getPreviousLayer()
	{
		return previousLayer;
	}

	public String getXML()
	{
		String buf;
		buf = "<Layer id = \"" + id + "\"";
		if (hasBias())
			buf += " bias =\"" + bias.getId() + "\"";
		buf += ">";
		for (Neuron neuron : neurons)
		{
			buf += neuron.getXML() + System.getProperty("line.separator");
		}
		buf = "</Layer>";
		return buf;
	}

	public boolean hasBias()
	{
		return bias != null;
	}

	public boolean isOutputLayer()
	{
		return nextLayer == null;
	}

	/**
	 * @param bias
	 *            the bias to set
	 */
	public void setBias(Neuron bias)
	{
		this.bias = bias;
	}

	void setNextLayer(Layer nextLayer)
	{
		this.nextLayer = nextLayer;
	}

	void setPreviousLayer(Layer previousLayer)
	{
		this.previousLayer = previousLayer;
	}

}
