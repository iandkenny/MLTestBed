package org.mltestbed.heuristics.ANN;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.mltestbed.heuristics.ANN.activators.ActivationStrategy;
import org.mltestbed.heuristics.ANN.activators.RectifiedLinearActivationStrategy;
import org.mltestbed.util.Log;
import org.mltestbed.util.RandGen;
import org.mltestbed.util.Util;

public class ArtificialNeuralNetwork implements Serializable
{

	private static final String ANN = "ann";
	private static final String INVALID_XML_FILE_FORMAT = "Invalid XML file format";
	public static final double eps = 1e-9;
	private static ConcurrentHashMap<String, Neuron> neuronMap;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ConcurrentHashMap<String, Synapse> synapseMap;
	public static void initialize_weight(Layer x)
	{
		// nn.init.xavier_uniform_(x.weight)
		// if (x.bias == null)
		// nn.init.constant_(x.bias, 0);
		List<Neuron> neurons = x.getNeurons();

		for (Iterator<Neuron> iterator = neurons.iterator(); iterator
				.hasNext();)
		{
			Neuron neuron = (Neuron) iterator.next();
			List<Synapse> synapses = neuron.getSynapses();
			for (Iterator<Synapse> iterator2 = synapses.iterator(); iterator2
					.hasNext();)
			{
				Synapse synapse = (Synapse) iterator2.next();
				synapse.setWeight(RandGen.getLastCreated().nextGaussian());
			}
		}
	}

	public static ArtificialNeuralNetwork createFeedForwardLinearNetwork(
			int input_size, int output_size, float dropout_rate)
	{

		ArtificialNeuralNetwork ffn = new ArtificialNeuralNetwork("FNN");
		Layer layer1 = new Layer("FFN Layer 1", null);
		Layer layer2 = new Layer("FFN Layer 2", layer1);

		RectifiedLinearActivationStrategy activate = new RectifiedLinearActivationStrategy();
		for (int i = 0; i < input_size; i++)
		{
			layer1.addNeuron(
					new Neuron(layer1.getId() + "." + i, activate, false));

		}
		for (int i = 0; i < output_size; i++)
		{
			layer2.addNeuron(
					new Neuron(layer2.getId() + "." + i, activate, false));

		}
		layer2.setDropout(dropout_rate);
		return ffn;

	}
	public static ArtificialNeuralNetwork createANN(String name,
			ArrayList<Integer> neuronsperLayer,
			ArrayList<ActivationStrategy> activationStrategy)
	{
		ArtificialNeuralNetwork ann = new ArtificialNeuralNetwork(name);
		Layer previousLayer = null;
		Layer layer;
		boolean bias;
		for (int i = 0; i < neuronsperLayer.size(); i++)
		{
			String id = name + ".layer";
			bias = (neuronsperLayer.get(i) < 0) ? true : false;
			if (bias)
			{
				Neuron biasNeuron = new Neuron(id + ".bias",
						activationStrategy.get(i), false);
				layer = new Layer(id, previousLayer, biasNeuron);
			} else
				layer = new Layer(id, previousLayer);
			int absneurons = Math.abs(neuronsperLayer.get(i));
			for (int j = 0; j < absneurons; j++)
			{
				Neuron neuron = new Neuron(layer.getId() + "." + i + "." + j,
						activationStrategy.get(i), false);
				layer.addNeuron(neuron);
			}

			ann.addLayer(layer);
			previousLayer = layer;
		}

		return ann;

	}
	public static ArrayList<Layer> createLinearLayers(
			ArtificialNeuralNetwork ann, ArrayList<Integer> neuronsperLayer,
			ArrayList<ActivationStrategy> activationStrategy, float dropoutRate)
	{
		Layer previousLayer = null;
		Layer layer;
		ArrayList<Layer> layers = new ArrayList<>();

		for (int i = 0; i < neuronsperLayer.size(); i++)
		{
			String id = ann.getName() + ".layer";
			boolean bBias = (neuronsperLayer.get(i) < 0) ? true : false;
			if (bBias)
			{
				Neuron biasNueron = new Neuron(id + ".bias",
						activationStrategy.get(i), false);
				layer = new Layer(id, previousLayer, biasNueron);
			} else
				layer = new Layer(id, previousLayer);
			layer.setDropout(dropoutRate);
			int absneurons = Math.abs(neuronsperLayer.get(i));
			for (int j = 0; j < absneurons; j++)
			{
				Neuron neuron = new Neuron(layer.getId() + "." + i + "." + j,
						activationStrategy.get(i), layer.isUseDropout());
				layer.addNeuron(neuron);
			}

			ann.addLayer(layer);
			layers.add(layer);
			previousLayer = layer;
		}

		return layers;

	}

	/**
	 * @return the neuronMap
	 */
	public static ConcurrentHashMap<String, Neuron> getNeuronMap()
	{
		return neuronMap;
	}
	/**
	 * @return the synapseMap
	 */
	public static ConcurrentHashMap<String, Synapse> getSynapseMap()
	{
		return synapseMap;
	}
	public static ArtificialNeuralNetwork loadANN(String xml)
	{
		ArtificialNeuralNetwork ann = null;
		Layer layer = null;
		Layer previousLayer = null;
		Neuron neuron = null;
		Synapse synapse = null;

		if (neuronMap == null)
			neuronMap = new ConcurrentHashMap<String, Neuron>();
		if (synapseMap == null)
			synapseMap = new ConcurrentHashMap<String, Synapse>();

		XMLInputFactory factory = XMLInputFactory.newInstance();
		try
		{
			XMLEventReader eventReader = factory.createXMLEventReader(
					new ByteArrayInputStream(xml.getBytes()));

			while (eventReader.hasNext())
			{
				XMLEvent event = eventReader.nextEvent();
				switch (event.getEventType())
				{
					case XMLStreamConstants.START_ELEMENT :
					{
						StartElement startElement = event.asStartElement();
						String qName = startElement.getName().getLocalPart();
						if (qName.equalsIgnoreCase(ANN))
						{
							@SuppressWarnings("unchecked")
							Iterator<Attribute> attributes = startElement
									.getAttributes();
							while (attributes.hasNext())
							{
								Attribute attr = attributes.next();
								String localPart = attr.getName()
										.getLocalPart();
								String attrValue = "";
								if (localPart.equalsIgnoreCase("name"))
								{
									attrValue = attr.getValue();
									ann = new ArtificialNeuralNetwork(
											attrValue);
								} else if (localPart
										.equalsIgnoreCase("rnnlayers"))
								{
									attrValue = attr.getValue();
									if (Util.isNumeric(attrValue))
										ann.setRnnLayers(new Double(
												Double.parseDouble(attrValue))
														.intValue());
								} else if (localPart
										.equalsIgnoreCase("dropout"))
								{
									attrValue = attr.getValue();
									if (Util.isNumeric(attrValue))
										ann.setDropout(new Double(
												Double.parseDouble(attrValue))
														.floatValue());

								}
							}
						}
						String biasid = null;
						if (qName.equalsIgnoreCase("layer"))
						{
							if (ann == null)
								throw new Exception(INVALID_XML_FILE_FORMAT);
							@SuppressWarnings("unchecked")
							Iterator<Attribute> attributes = startElement
									.getAttributes();
							while (attributes.hasNext())
							{
								Attribute attr = attributes.next();
								String localPart = attr.getName()
										.getLocalPart();
								if (localPart.equalsIgnoreCase("id"))
								{
									String attrValue = attr.getValue();

									layer = new Layer(attrValue);
									ann.addLayer(layer);
								} else if (localPart.equalsIgnoreCase("bias"))
									biasid = attr.getValue();
							}
						} else if (qName.equalsIgnoreCase("neuron"))
						{
							if (layer == null)
								throw new Exception(INVALID_XML_FILE_FORMAT);
							@SuppressWarnings("unchecked")
							Iterator<Attribute> attributes = startElement
									.getAttributes();
							String attrName = null;
							ActivationStrategy activate = null;
							boolean neuronDropout = false;
							if (attributes.hasNext())
							{
								Attribute attr = attributes.next();
								String localPart = attr.getName()
										.getLocalPart();
								String value = attr.getValue();
								if (value != null)
								{
									if (localPart.equalsIgnoreCase("id"))
									{
										attrName = value;
									} else if (localPart
											.equalsIgnoreCase("activate"))
									{
										String clazz = value;
										activate = (ActivationStrategy) Class
												.forName(clazz)
												.getDeclaredConstructor()
												.newInstance();
									} else if (localPart
											.equalsIgnoreCase("dropout"))
									{

										neuronDropout = Boolean.valueOf(value);
									}
								}
							}

							if (attrName != null && activate != null)
							{
								if (neuronMap.containsKey(attrName))
									neuron = neuronMap.get(attrName);
								else
								{
									neuron = new Neuron(attrName, activate,
											neuronDropout);
									neuronMap.put(neuron.getId(), neuron);
									if (attrName.equalsIgnoreCase(biasid))
									{
										layer.setBias(neuron);
										biasid = null;
									}
								}
								layer.addNeuron(neuron);
							}
						} else if (qName.equalsIgnoreCase("synapse"))
						{
							if (neuron == null)
								throw new Exception(INVALID_XML_FILE_FORMAT);
							synapse = null;
							@SuppressWarnings("unchecked")
							Iterator<Attribute> attributes = startElement
									.getAttributes();
							String id = "";
							double weight = -2;
							Neuron sourceNeuron = null;
							while (attributes.hasNext())
							{
								Attribute attr = attributes.next();
								String localPart = attr.getName()
										.getLocalPart();
								if (localPart.equalsIgnoreCase("id"))
								{
									id = attr.getValue();
									if (synapseMap.containsKey(id))
										synapse = synapseMap.get(id);
								} else if (localPart.equalsIgnoreCase("weight"))
									weight = Double
											.parseDouble(attr.getValue());
								else if (localPart
										.equalsIgnoreCase("sourceneuron")
										&& (neuronMap
												.containsKey(attr.getValue())))
									sourceNeuron = neuronMap
											.get(attr.getValue());
								else
									sourceNeuron = null;
							}

							if (synapse == null)
							{
								if (synapseMap.contains(id))
									synapse = synapseMap.get(id);
								else
								{
									if (weight == Double.NaN)
										weight = ((Math.random() * 1) - 0.5);
									synapse = new Synapse(id, sourceNeuron,
											weight);
									synapseMap.put(id, synapse);
								}
							}
							neuron.addInput(synapse);
						}
						break;
					}
					case XMLStreamConstants.CHARACTERS :
					{
						Characters characters = event.asCharacters();
						break;
					}
					case XMLStreamConstants.END_ELEMENT :
					{
						EndElement endElement = event.asEndElement();
						String qName = endElement.getName().getLocalPart();
						if (qName.equalsIgnoreCase(ANN))
						{
							return ann;

						} else if (qName.equalsIgnoreCase("layer"))
						{
							if (previousLayer != null)
								layer.setPreviousLayer(previousLayer);
							ann.addLayer(layer);
							previousLayer = layer;
							layer = null;
						} else if (qName.equalsIgnoreCase("neuron"))
						{
							neuron = null;
						} else if (qName.equalsIgnoreCase("synapse"))
						{
							synapse = null;
						}
						break;
					}
				}
			}
		} catch (XMLStreamException e)
		{
			Log.log(Level.SEVERE, e);

			e.printStackTrace();
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

		return ann;
	}
	/**
	 * @param neuronMap
	 *            the neuronMap to set
	 */
	public static void setNeuronMap(ConcurrentHashMap<String, Neuron> neuronMap)
	{
		ArtificialNeuralNetwork.neuronMap = neuronMap;
	}
	/**
	 * @param synapseMap
	 *            the synapseMap to set
	 */
	public static void setSynapseMap(
			ConcurrentHashMap<String, Synapse> synapseMap)
	{
		ArtificialNeuralNetwork.synapseMap = synapseMap;
	}

	private float dropout = 0;
	private Layer input;
	private List<Layer> layers;
	private String name;
	private Layer output;
	private int rnnLayers = 0;
	private boolean training = false;
	private boolean useDropout = false;

	public ArtificialNeuralNetwork(String name)
	{
		this.name = name;
		layers = new ArrayList<Layer>();
	}
	public void addLayer(Layer layer)
	{
		layers.add(layer);

		if (layers.size() == 1)
		{
			input = layer;
		}

		if (layers.size() > 1)
		{
			// clear the output flag on the previous output layer, but only if
			// we have more than 1 layer
			Layer previousLayer = layers.get(layers.size() - 2);
			previousLayer.setNextLayer(layer);
		}

		output = layers.get(layers.size() - 1);
	}
	public void applyDropout()
	{
		if (useDropout)
		{
			for (Layer layer : layers)
			{
				for (Neuron neuron : layer.getNeurons())
				{
					if (RandGen.getLastCreated().nextFloat() <= dropout)
						neuron.setDropout(true);
					else
						neuron.setDropout(false);

				}
			}

		}
	}
	public ArtificialNeuralNetwork copy()
	{
		ArtificialNeuralNetwork copy = new ArtificialNeuralNetwork(this.name);

		Layer previousLayer = null;
		for (Layer layer : layers)
		{

			Layer layerCopy;

			if (layer.hasBias())
			{
				Neuron bias = layer.getNeurons().get(0);
				Neuron biasCopy = new Neuron("0",
						bias.getActivationStrategy().copy(), bias.isDropout());
				biasCopy.setOutput(bias.getOutput());
				layerCopy = new Layer(layer.getId() + "x", null, biasCopy);
			}

			else
			{
				layerCopy = new Layer(layer.getId() + "x");
			}

			layerCopy.setPreviousLayer(previousLayer);

			int biasCount = layerCopy.hasBias() ? 1 : 0;

			for (int i = biasCount; i < layer.getNeurons().size(); i++)
			{
				Neuron neuron = layer.getNeurons().get(i);

				Neuron neuronCopy = new Neuron(Integer.toString(i),
						neuron.getActivationStrategy().copy(),
						neuron.isDropout());
				neuronCopy.setOutput(neuron.getOutput());
				neuronCopy.setError(neuron.getError());

				if (neuron.getSynapses().size() == 0)
				{
					layerCopy.addNeuron(neuronCopy);
				}

				else
				{
					double[] weights = neuron.getWeights();
					layerCopy.addNeuron(neuronCopy, weights);
				}
			}

			copy.addLayer(layerCopy);
			previousLayer = layerCopy;
		}

		return copy;
	}
	public void copyWeightsFrom(ArtificialNeuralNetwork sourceNeuralNetwork)
	{
		if (layers.size() != sourceNeuralNetwork.layers.size())
		{
			throw new IllegalArgumentException(
					"Cannot copy weights. Number of layers do not match ("
							+ sourceNeuralNetwork.layers.size()
							+ " in source versus " + layers.size()
							+ " in destination)");
		}

		int i = 0;
		for (Layer sourceLayer : sourceNeuralNetwork.layers)
		{
			Layer destinationLayer = layers.get(i);

			if (destinationLayer.getNeurons().size() != sourceLayer.getNeurons()
					.size())
			{
				throw new IllegalArgumentException(
						"Number of neurons do not match in layer " + (i + 1)
								+ "(" + sourceLayer.getNeurons().size()
								+ " in source versus "
								+ destinationLayer.getNeurons().size()
								+ " in destination)");
			}

			int j = 0;
			for (Neuron sourceNeuron : sourceLayer.getNeurons())
			{
				Neuron destinationNeuron = destinationLayer.getNeurons().get(j);

				if (destinationNeuron.getSynapses().size() != sourceNeuron
						.getSynapses().size())
				{
					throw new IllegalArgumentException(
							"Number of inputs to neuron " + (j + 1)
									+ " in layer " + (i + 1) + " do not match ("
									+ sourceNeuron.getSynapses().size()
									+ " in source versus "
									+ destinationNeuron.getSynapses().size()
									+ " in destination)");
				}

				int k = 0;
				for (Synapse sourceSynapse : sourceNeuron.getSynapses())
				{
					Synapse destinationSynapse = destinationNeuron.getSynapses()
							.get(k);

					destinationSynapse.setWeight(sourceSynapse.getWeight());
					k++;
				}

				j++;
			}

			i++;
		}
	}
	/**
	 * @return the dropout
	 */
	public float getDropout()
	{
		return dropout;
	}

	public List<Layer> getLayers()
	{
		return layers;
	}
	public String getName()
	{
		return name;
	}

	public Vector<Double> getOutput()
	{

		Vector<Double> outputs = new Vector<Double>(output.getNeurons().size());

		for (int i = 1; i < layers.size(); i++)
		{
			Layer layer = layers.get(i);
			layer.feedForward();
		}

		int i = 0;
		for (Neuron neuron : output.getNeurons())
		{
			outputs.add(i, neuron.getOutput());
			i++;
		}

		return outputs;
	}
	/**
	 * @return the rnnLayers
	 */
	public int getRnnLayers()
	{
		return rnnLayers;
	}
	public Vector<Double> getWeights()
	{

		Vector<Double> weights = new Vector<Double>();

		for (Layer layer : layers)
		{

			for (Neuron neuron : layer.getNeurons())
			{

				for (Synapse synapse : neuron.getSynapses())
				{
					weights.add(synapse.getWeight());
				}
			}
		}

		return weights;
	}
	public String getXML()
	{
		String buf;
		buf = "<ANN name = \"" + name + "\">";
		for (Layer layer : layers)
		{
			buf += layer.getXML() + System.getProperty("line.separator");
		}
		buf = "</ANN>";
		return buf;
	}

	/**
	 * @return the training
	 */
	public boolean isTraining()
	{
		return training;
	}

	/**
	 * @return the useDropout
	 */
	public boolean isUseDropout()
	{
		return useDropout;
	}

	public void persist()
	{
		String fileName = name.replaceAll(" ", "") + "-" + new Date().getTime()
				+ ".net";
		System.out
				.println("Writing trained neural network to file " + fileName);

		ObjectOutputStream objectOutputStream = null;

		try
		{
			objectOutputStream = new ObjectOutputStream(
					new FileOutputStream(fileName));
			objectOutputStream.writeObject(this);
		}

		catch (IOException e)
		{
			System.out.println("Could not write to file: " + fileName);
			e.printStackTrace();
		}

		finally
		{
			try
			{
				if (objectOutputStream != null)
				{
					objectOutputStream.flush();
					objectOutputStream.close();
				}
			}

			catch (IOException e)
			{
				System.out.println("Could not write to file: " + fileName);
				e.printStackTrace();
			}
		}
	}

	public void reset()
	{
		for (Layer layer : layers)
		{
			for (Neuron neuron : layer.getNeurons())
			{
				for (Synapse synapse : neuron.getSynapses())
				{
					synapse.setWeight(
							RandGen.getLastCreated().nextDouble() - 0.5);
				}
			}
		}
	}

	public void rnnIter()
	{
		if (rnnLayers > 0 && layers.size() >= rnnLayers + 1)
		{
			try
			{
				Layer layer = input;
				for (int i = 0; i < rnnLayers; i++)
					layer = layer.getNextLayer();

				while (layer != input)
				{
					int size = layer.getNeurons().size();
					int size2 = layer.getNextLayer().getNeurons().size();
					if (size == size2)
					{
						for (int j = 0; j < size; j++)
						{
							layer.getNeurons().get(j)
									.setOutput(layer.getPreviousLayer()
											.getNeurons().get(j).getOutput());
						}

					} else
						throw new Exception(
								"In order for the RNN to work (as currently implemented) the first "
										+ rnnLayers
										+ " must have the same number of neurons");
					layer = layer.getPreviousLayer();
				}
			} catch (Exception e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			}
		}
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
	public void setInputs(Vector<Double> inputs)
	{
		if (input != null)
		{

			int biasCount = input.hasBias() ? 1 : 0;

			if (input.getNeurons().size() - biasCount != inputs.size())
			{
				throw new IllegalArgumentException(
						"The number of inputs must equal the number of neurons in the input layer");
			}

			else
			{
				List<Neuron> neurons = input.getNeurons();
				for (int i = biasCount; i < neurons.size(); i++)
				{
					neurons.get(i).setOutput(inputs.get(i - biasCount));
				}
			}
		}
	}
	/**
	 * @param layers the layers to set
	 */
	public void setLayers(List<Layer> layers)
	{
		this.layers = layers;
	}

	/**
	 * @param rnnLayers
	 *            the rnnLayers to set
	 */
	public void setRnnLayers(int rnnLayers)
	{
		this.rnnLayers = rnnLayers;
	}

	/**
	 * @param training
	 *            the training to set
	 */
	public void setTraining(boolean training)
	{
		this.training = training;
		if (!training)
			useDropout = false;
		applyDropout();
	}

	/**
	 * @param useDropout
	 *            the useDropout to set
	 */
	private void setUseDropout(boolean useDropout)
	{
		this.useDropout = useDropout;
	}
	public void setWeights(Vector<Double> weights)
	{
		Enumeration<Double> eles = weights.elements();
		for (Layer layer : layers)
		{

			for (Neuron neuron : layer.getNeurons())
			{
				for (Synapse synapse : neuron.getSynapses())
				{
					if (eles.hasMoreElements())
						synapse.setWeight(eles.nextElement());
				}
			}
		}
	}
}
