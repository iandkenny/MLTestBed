/**
 * 
 */
package org.mltestbed.testFunctions.ANN;

import org.mltestbed.data.readDB.ReadIEA;
import org.mltestbed.heuristics.ANN.ArtificialNeuralNetwork;
import org.mltestbed.heuristics.ANN.Layer;
import org.mltestbed.heuristics.ANN.Neuron;
import org.mltestbed.heuristics.ANN.activators.LinearActivationStrategy;
import org.mltestbed.testFunctions.TestBaseANN;

/**
 * 
 */
public class IEAANN extends TestBaseANN
{

	private ReadIEA db;
	/**
	 * 
	 */
	public IEAANN()
	{
		super();
		description = "IEA ANN Test";
	}

	@Override
	protected void createParams()
	{
		super.createParams();
		db = new ReadIEA();
		inputs = db.getDim();
		params.setProperty(INPUTS, Integer.toString(inputs));
		params.setProperty(HIDDEN_LAYERS, "1");
		params.setProperty(RNN_LAYERS, "0");
		params.setProperty(OUTPUTS, "1");
		params.setProperty(BIAS, "false");
		params.setProperty(ANN, "");
	}

	@Override
	public void init()
	{
		super.init();
		db = new ReadIEA();
		inputs = db.getDim();
		params.setProperty(INPUTS, Integer.toString(inputs));
		ann = new ArtificialNeuralNetwork("IEA ANN");
		Layer layer = new Layer("Input");
		for (int i = 0; i < inputs; i++)
		{
			layer.addNeuron(new Neuron("InputNeuron."+i, new LinearActivationStrategy(), bias));
			
		}
		ann.addLayer(layer);
		for (int i = 0; i < hidden; i++)
		{
			layer = new Layer("hidden."+i+1);
			double d = mMax[i];
			
		}
	}

	@Override
	protected void setRange()
	{
		// TODO Auto-generated method stub
		super.setRange();
	}

}
