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
import org.mltestbed.util.Log;

public class ArtificialNeuralNetwork implements Serializable
{

	private static final String ANN = "ann";
	private static final String INVALID_XML_FILE_FORMAT = "Invalid XML file format";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ConcurrentHashMap<String, Neuron> neuronMap;
	private static ConcurrentHashMap<String, Synapse> synapseMap;
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
							int rnnLayers = 0;
							@SuppressWarnings("unchecked")
							Iterator<Attribute> attributes = startElement
									.getAttributes();
							while (attributes.hasNext())
							{
								Attribute attr = attributes.next();
								String localPart = attr.getName()
										.getLocalPart();
								if (localPart.equalsIgnoreCase("name"))
								{
									String attrName = attr.getValue();
									ann = new ArtificialNeuralNetwork(attrName);
								} else if (localPart
										.equalsIgnoreCase("rnnlayers"))
								{
									String attrName = attr.getValue();
									rnnLayers = Integer.parseInt(attrName);
								}
							}
							ann.setRnnLayers(rnnLayers);
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
							if (attributes.hasNext())
							{
								Attribute attr = attributes.next();
								String localPart = attr.getName()
										.getLocalPart();
								if (localPart.equalsIgnoreCase("id"))
								{
									attrName = attr.getValue();
								}
								if (localPart.equalsIgnoreCase("activate"))
								{
									String clazz = attr.getValue();
									activate = (ActivationStrategy) Class
											.forName(clazz)
											.getDeclaredConstructor()
											.newInstance();
								}
							}

							if (attrName != null && activate != null)
							{
								if (neuronMap.containsKey(attrName))
									neuron = neuronMap.get(attrName);
								else
								{
									neuron = new Neuron(attrName, activate);
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
								if (weight == -2)
									weight = ((Math.random() * 1) - 0.5);
								synapse = new Synapse(id, sourceNeuron, weight);
								synapseMap.put(id, synapse);
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
	 * @return the neuronMap
	 */
	public static ConcurrentHashMap<String, Neuron> getNeuronMap()
	{
		return neuronMap;
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
	 * @return the synapseMap
	 */
	public static ConcurrentHashMap<String, Synapse> getSynapseMap()
	{
		return synapseMap;
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
	private Layer input;
	private List<Layer> layers;
	private String name;

	private Layer output;
	private int rnnLayers = 0;

	public ArtificialNeuralNetwork(String name)
	{
		this.name = name;
		layers = new ArrayList<Layer>();
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
	 * @return the rnnLayers
	 */
	public int getRnnLayers()
	{
		return rnnLayers;
	}
	/**
	 * @param rnnLayers
	 *            the rnnLayers to set
	 */
	public void setRnnLayers(int rnnLayers)
	{
		this.rnnLayers = rnnLayers;
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
						bias.getActivationStrategy().copy());
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
						neuron.getActivationStrategy().copy());
				neuronCopy.setOutput(neuron.getOutput());
				neuronCopy.setError(neuron.getError());

				if (neuron.getInputs().size() == 0)
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

				if (destinationNeuron.getInputs().size() != sourceNeuron
						.getInputs().size())
				{
					throw new IllegalArgumentException(
							"Number of inputs to neuron " + (j + 1)
									+ " in layer " + (i + 1) + " do not match ("
									+ sourceNeuron.getInputs().size()
									+ " in source versus "
									+ destinationNeuron.getInputs().size()
									+ " in destination)");
				}

				int k = 0;
				for (Synapse sourceSynapse : sourceNeuron.getInputs())
				{
					Synapse destinationSynapse = destinationNeuron.getInputs()
							.get(k);

					destinationSynapse.setWeight(sourceSynapse.getWeight());
					k++;
				}

				j++;
			}

			i++;
		}
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

	public Vector<Double> getWeights()
	{

		Vector<Double> weights = new Vector<Double>();

		for (Layer layer : layers)
		{

			for (Neuron neuron : layer.getNeurons())
			{

				for (Synapse synapse : neuron.getInputs())
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
				for (Synapse synapse : neuron.getInputs())
				{
					synapse.setWeight((Math.random() * 1) - 0.5);
				}
			}
		}
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

	public void setWeights(Vector<Double> weights)
	{
		Enumeration<Double> eles = weights.elements();
		for (Layer layer : layers)
		{

			for (Neuron neuron : layer.getNeurons())
			{
				for (Synapse synapse : neuron.getInputs())
				{
					if (eles.hasMoreElements())
						synapse.setWeight(eles.nextElement());
				}
			}
		}
	}

}
