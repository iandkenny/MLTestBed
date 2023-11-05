/**
 * 
 */
package org.mltestbed.heuristics.ANN;

import java.util.ArrayList;

import org.mltestbed.heuristics.ANN.activators.ActivationStrategy;
import org.mltestbed.util.RandGen;

/**
 * @author ian
 *
 */
public class LayerConv2D extends Layer
{

	private static final String CONV2D_LAYER = "Conv2D layer ";
	private ArrayList<Neuron[][]> A= new ArrayList<>();
	private ArrayList<Neuron[][]> B= new ArrayList<>();
	private int height;
	private ArrayList<Layer> internalLayers = new ArrayList<>();
	private int prevLayerNoNuerons;
	private int width;
	/**
	 * @param id
	 */
	public LayerConv2D(ArtificialNeuralNetwork ann, String id, int height, int width, ActivationStrategy as)
	{
		super(id);
		this.setAnn(ann);
		this.height = height;
		this.width = width;
		int size = getPreviousLayer().getNeurons().size();
		if (getPreviousLayer().hasBias())
			this.prevLayerNoNuerons = size-1;
		else
			this.prevLayerNoNuerons = size;
		A.add(new Neuron[height][width]);
		for (int i = 0; i < height; i++)
		{
			Layer innerLayer = new Layer(CONV2D_LAYER+"."+i);
			for (int j = 0; j < size; j++)
					innerLayer.addNeuron(new Neuron(CONV2D_LAYER+"."+i+".Neuron."+j,as.copy(), false));
			internalLayers.add(innerLayer);
			ann.addLayer(innerLayer);
		}
		ann.addLayer(this);
		int outneurons = (int) Math.ceil(prevLayerNoNuerons/this.width);
		this.getPreviousLayer().setNextLayer(null);
		this.setPreviousLayer(null);
		 
		for (int j = 0; j < outneurons; j++)
		{
			Neuron neuron = new Neuron(CONV2D_LAYER+".Out.Neuron."+j,as.copy(), false);
			this.addNeuron(neuron);
			neuron.addInput(new Synapse(neuron.getId()+".synapse",neuron,(RandGen.getLastCreated().nextDouble()*2)-1));
			neuron.setOutput(1);
		}
	}

	public String getXML()
	{
		String buf = "<Layer id = \"" + getId() + "\" type=\""
				+ this.getClass().getName() + "\"previd = \"" + getPreviousLayer().getId() + "\"";
		if (hasBias())
			buf += " bias =\"" + getBiasNeuron().getId() + "\"";
		buf += ">";
		for (Neuron neuron : getNeurons())
		{
			buf += neuron.getXML() + System.getProperty("line.separator");
		}
		buf = "</Layer>";
		return buf;
	}
}
