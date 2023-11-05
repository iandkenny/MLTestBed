/**
 * 
 */
package org.mltestbed.heuristics.DNN;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

import org.mltestbed.heuristics.ANN.ArtificialNeuralNetwork;
import org.mltestbed.heuristics.ANN.Layer;
import org.mltestbed.heuristics.ANN.Neuron;
import org.mltestbed.heuristics.ANN.Synapse;
import org.mltestbed.heuristics.ANN.activators.ActivationStrategy;
import org.mltestbed.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * @author ian
 *
 */
public class ConvolutionalNeuralNetwork implements Serializable
{
	private static final String ANN = "ann";
	private static final String CNN = "cnn";
	private static final String INVALID_XML_FILE_FORMAT = "Invalid XML file format";
	private static ArrayList<ArtificialNeuralNetwork> anns = new ArrayList<ArtificialNeuralNetwork>();
	private String name;
	/**
	 * 
	 */

	public ConvolutionalNeuralNetwork(String name)
	{
		this.name = name;
		anns = new ArrayList<ArtificialNeuralNetwork>();
	}
	public static ConvolutionalNeuralNetwork loadCNN(String xml)
	{
		ConvolutionalNeuralNetwork cnn = null;
		XMLInputFactory factory = XMLInputFactory.newInstance();
		try
		{
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
					xml.getBytes());
			XMLEventReader eventReader = factory
					.createXMLEventReader(byteArrayInputStream);

			while (eventReader.hasNext())
			{
				XMLEvent event = eventReader.nextEvent();
				switch (event.getEventType())
				{
					case XMLStreamConstants.START_ELEMENT :
					{
						StartElement startElement = event.asStartElement();
						String qName = startElement.getName().getLocalPart();
						if (qName.equalsIgnoreCase(CNN))
						{
							int annLayers = 0;
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
									cnn = new ConvolutionalNeuralNetwork(
											attrName);
								} else if (localPart
										.equalsIgnoreCase("annlayers"))
								{
									String attrName = attr.getValue();
									annLayers = Integer.parseInt(attrName);
								}
							}
							cnn.setAnnLayers(annLayers);
						}
						String biasid = null;
						if (qName.equalsIgnoreCase(ANN))
						{
							if (cnn == null)
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

//								layer = new Layer(attrValue);
									// ann.addLayer(layer);
								} else if (localPart.equalsIgnoreCase("bias"))
									biasid = attr.getValue();
							}
							DocumentBuilderFactory dbf = DocumentBuilderFactory
									.newInstance();

							// we are creating an object of builder to parse
							// the xml file.
							DocumentBuilder db = dbf.newDocumentBuilder();
							Document doc = (Document) db
									.parse(byteArrayInputStream);

							/*
							 * here normalize method Puts all Text nodes in the
							 * full depth of the sub-tree underneath this Node,
							 * including attribute nodes, into a "normal" form
							 * where only structure separates Text nodes, i.e.,
							 * there are neither adjacent Text nodes nor empty
							 * Text nodes.
							 */
							doc.getDocumentElement().normalize();
							System.out.println("Root element: "
									+ doc.getDocumentElement().getNodeName());

							// Here nodeList contains all the nodes with
							// name geek.
							NodeList nodeList = doc.getElementsByTagName(ANN);

							// Iterate through all the nodes in NodeList
							// using for loop.
							for (int i = 0; i < nodeList.getLength(); ++i)
							{
								Node node = nodeList.item(i);
								System.out.println(
										"\nNode Name :" + node.getNodeName());
								try
								{
									anns.add(ArtificialNeuralNetwork.loadANN(nodeToString(node)));
								} catch (Exception e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
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
						if (qName.equalsIgnoreCase(CNN))
						{
							return cnn;
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

		return cnn;
	}
	private void setAnnLayers(int annLayers)
	{

	}
	private static String nodeToString(Node node)
			throws TransformerException
			{
			    StringWriter buf = new StringWriter();
			    Transformer xform = TransformerFactory.newInstance().newTransformer();
			    xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			    xform.transform(new DOMSource(node), new StreamResult(buf));
			    return(buf.toString());
			}
}
