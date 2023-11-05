package org.mltestbed.heuristics.ANN;

import java.util.ArrayList;
import java.util.List;

import org.mltestbed.util.RandGen;

public class Layer
{
	private ArtificialNeuralNetwork ann;
	private Neuron biasNeuron;
	private int filterWidth = Integer.MIN_VALUE;
	private float dropout = 0;
	private String id="";
	private List<Neuron> neurons;
	private Layer nextLayer;
	private Layer previousLayer;
	private boolean useDropout = true;
	private boolean recurrent = false;

	public Layer(String id)
	{
		this.id = id;
		neurons = new ArrayList<Neuron>();
		previousLayer = null;
		dropout = 0;
		useDropout = false;
	}

	public Layer(String id, float dropout)
	{
		this.id = id;
		neurons = new ArrayList<Neuron>();
		previousLayer = null;
		this.dropout = dropout;
	}

	public Layer(String id, Layer previousLayer)
	{
		this(id);
		this.previousLayer = previousLayer;
	}

	public Layer(String id, Layer previousLayer, Neuron bias)
	{
		this(id, previousLayer);
		this.biasNeuron = bias;
		neurons.add(bias);
	}

	public void addNeuron(Neuron neuron)
	{

		neuron.setRecurrent(recurrent);
		neurons.add(neuron);

		if (previousLayer != null)
		{
			List<Neuron> prevLayerNeurons = previousLayer.getNeurons();
			if (filterWidth <= 0)
				for (Neuron previousLayerNeuron : prevLayerNeurons)
				{

					neuron.addInput(new Synapse(previousLayerNeuron,
							RandGen.getLastCreated().nextDouble() * 2 - 1)); // initialize
																				// with
																				// a
																				// random
																				// weight
																				// between
																				// -1
																				// and
																				// 1
				}
			else
			{
				int noNeurons = neurons.size();
				int startindex = noNeurons * filterWidth;
				int max = (startindex + filterWidth > prevLayerNeurons.size())
						? prevLayerNeurons.size()
						: startindex + filterWidth;

				for (int i = startindex; i < max; i++)
				{
					Neuron previousLayerNeuron = prevLayerNeurons.get(i);
					if (previousLayerNeuron.equals(previousLayer.biasNeuron))
						--i;
					else
						neuron.addInput(new Synapse(previousLayerNeuron,
								RandGen.getLastCreated().nextDouble() * 2 - 1)); // initialize
																					// with
																					// a
																					// random
																					// weight
																					// between
																					// -1
																					// and
																					// 1
				}
				if (previousLayer.biasNeuron != null)
					neuron.addInput(new Synapse(previousLayer.biasNeuron,
							RandGen.getLastCreated().nextDouble() * 2 - 1)); // initialize
																				// with
																				// a
																				// random
																				// weight
																				// between
																				// -1
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
			Neuron neuron = neurons.get(i);
			if (useDropout && (RandGen.getLastCreated().nextFloat() >= dropout))
				neuron.setDropout(true);
			else
				neuron.setDropout(false);
			neuron.activate();
		}
	}

	/**
	 * @return the ann
	 */
	public ArtificialNeuralNetwork getAnn()
	{
		return ann;
	}

	/**
	 * @return the biasNeuron
	 */
	public Neuron getBiasNeuron()
	{
		return biasNeuron;
	}

	/**
	 * @return the filterWidth
	 */
	public int getFilterWidth()
	{
		return filterWidth;
	}

	/**
	 * @return the dropout
	 */
	public float getDropout()
	{
		return dropout;
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
		String buf = "<Layer id = \"" + id + "\" type=\""
				+ this.getClass().getName() + "\"previd = \"" + getPreviousLayer().getId() + "\"";
		if (hasBias())
			buf += " bias =\"" + biasNeuron.getId() + "\"";
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
		return biasNeuron != null;
	}

	public boolean isOutputLayer()
	{
		return nextLayer == null;
	}

	/**
	 * @return the useDropout
	 */
	public boolean isUseDropout()
	{
		return useDropout;
	}

	/**
	 * @param ann
	 *            the ann to set
	 */
	public void setAnn(ArtificialNeuralNetwork ann)
	{
		this.ann = ann;
	}

	/**
	 * @param bias
	 *            the bias to set
	 */
	public void setBias(Neuron bias)
	{
		this.biasNeuron = bias;
	}

	/**
	 * @param biasNeuron
	 *            the biasNeuron to set
	 */
	public void setBiasNeuron(Neuron biasNeuron)
	{
		this.biasNeuron = biasNeuron;
	}

	/**
	 * @param width
	 *            the filterWidth to set
	 */
	public void setFilterWidth(int width)
	{
		this.filterWidth = width;
	}

	/**
	 * @param dropout
	 *            the dropout to set
	 */
	public void setDropout(float dropout)
	{
		setUseDropout(dropout > 0.0 && dropout <= 1.0);
		if (useDropout)
			this.dropout = dropout;
	}

	void setNextLayer(Layer nextLayer)
	{
		this.nextLayer = nextLayer;
	}

	void setPreviousLayer(Layer previousLayer)
	{
		this.previousLayer = previousLayer;
	}

	/**
	 * @param useDropout
	 *            the useDropout to set
	 */
	public void setUseDropout(boolean useDropout)
	{
		this.useDropout = useDropout;
	}

}
