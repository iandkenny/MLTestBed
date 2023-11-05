package org.mltestbed.heuristics.transformers.generic;

import java.util.Vector;

import org.mltestbed.heuristics.ANN.ArtificialNeuralNetwork;

/**
 * @author ian
 * 
 *         adapted from
 *         https://github.com/tunz/transformer-pytorch/blob/e7266679f0b32fd99135ea617213f986ceede056/model/transformer.py#L201
 *
 */
public abstract class TransformerBase extends ArtificialNeuralNetwork
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_TRANSFORMER = "Default Transformer";

	public TransformerBase(String name)
	{
		super(name);
	}
	public TransformerBase()
	{
		super(DEFAULT_TRANSFORMER);
	}

	// pylint: disable=arguments-differ


	@Override
	public Vector<Double> getOutput()
	{
		// TODO Auto-generated method stub
		return super.getOutput();
	}

	public ArtificialNeuralNetwork forward(ArtificialNeuralNetwork x)
	{
		getOutput();
		return x;
	}

}