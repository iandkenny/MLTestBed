package org.mltestbed.testFunctions;

import java.util.logging.Level;

import org.mltestbed.heuristics.ANN.ArtificialNeuralNetwork;
import org.mltestbed.util.Log;

public class TestBaseANN extends TestBase
{
	private static final String RNN_LAYERS = "rnnLayers";
	private static final String ANN = "ann";
	private static final String BIAS = "bias";
	private static final String HIDDEN_LAYERS = "hiddenLayers";
	private static final String INPUTS = "inputs";
	private static final String OUTPUTS = "outputs";
	protected ArtificialNeuralNetwork ann = null;
	protected int inputs = 0;
	protected int hidden = 1;
	protected int outputs = 1;
	protected boolean bias = false;
	protected String annXML = "";
	private int rnnlayers = 0;

	/**
	 * 
	 */
	protected TestBaseANN()
	{
		super();
		description = "ANN Base Test";
	}

	@Override
	protected void createParams()
	{
		params.setProperty(INPUTS, "0");
		params.setProperty(HIDDEN_LAYERS, "1");
		params.setProperty(RNN_LAYERS, "0");
		params.setProperty(OUTPUTS, "1");
		params.setProperty(BIAS, "false");
		params.setProperty(ANN, "");
	}

	@Override
	public void init()
	{
		try
		{
			String buf = params.getProperty(INPUTS, Integer.toString(inputs));
			inputs = Integer.parseInt(buf);
			buf = params.getProperty(HIDDEN_LAYERS, Integer.toString(hidden));
			hidden = Integer.parseInt(buf);
			buf = params.getProperty(RNN_LAYERS, Integer.toString(rnnlayers));
			rnnlayers = Integer.parseInt(buf);
			if (rnnlayers > hidden)
			{
				hidden = rnnlayers + 1;
				params.setProperty(HIDDEN_LAYERS, Integer.toString(hidden));
			}
			buf = params.getProperty(OUTPUTS, Integer.toString(outputs));
			outputs = Integer.parseInt(buf);
			bias = Boolean.parseBoolean(
					params.getProperty(BIAS, Boolean.toString(bias)));
			annXML = params.getProperty(ANN, annXML);
			ann = new ArtificialNeuralNetwork(description);
		} catch (NumberFormatException e)
		{
			Log.getLogger().log(Level.SEVERE, e.getMessage(), e);
			e.printStackTrace();
		}
	}

	@Override
	protected void setRange()
	{
		// TODO Auto-generated method stub

	}

}
