package org.mltestbed.heuristics.ANN;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.mltestbed.util.RandGen;
import org.mltestbed.util.Util;

public class LayerNorm extends Layer
{

	public static final byte maxMinNorm = 1;

	public static final byte zScore = 2;
	private byte normType = 0;
	/**
	 * @param id
	 */
	public LayerNorm(String id)
	{
		super(id);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param id
	 * @param dropout
	 */
	protected LayerNorm(String id, float dropout)
	{
		super(id, dropout);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param id
	 * @param previousLayer
	 */
	protected LayerNorm(String id, Layer previousLayer)
	{
		super(id, previousLayer);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param id
	 * @param previousLayer
	 * @param bias
	 */
	protected LayerNorm(String id, Layer previousLayer, Neuron bias)
	{
		super(id, previousLayer, bias);
		// TODO Auto-generated constructor stub
	}

	private void applyNorm(byte normType)
	{
		switch (normType)
		{
			case maxMinNorm :
				maxMinNorm(getNeurons());
				break;
			case zScore :
				zScoreNorm(getNeurons());
				break;
			default :
				break;
		}
	}
	@Override
	public void feedForward()
	{
		Layer previousLayer = getPreviousLayer();
		if (previousLayer != null)
		{
			List<Neuron> prevneurons = previousLayer.getNeurons();
			List<Neuron> neurons = getNeurons();
			for (int i = 0; i < prevneurons.size(); i++)
			{
				Neuron neuron = neurons.get(i);
				if (neuron == null)
				{
					neuron = new Neuron("LayerNorm Neuron " + i, null, false);
					neurons.set(i, neuron);
				}
				neuron.setOutput(prevneurons.get(i).getOutput());
			}
		}
		applyNorm(getNormType());
	}
	/**
	 * @return the normType
	 */
	public byte getNormType()
	{
		return normType;
	}

	public String getXML()
	{
		String buf = "<LayerNorm id = \"" + getId() + "\" type=\""
				+ this.getClass().getName() + "\"previd = \""
				+ getPreviousLayer().getId() + "\"";
		if (hasBias())
			buf += " bias =\"" + getBiasNeuron().getId() + "\"";
		buf += ">";
		for (Neuron neuron : getNeurons())
		{
			buf += neuron.getXML() + System.getProperty("line.separator");
		}
		buf = "</LayerNorm>";
		return buf;
	}
	private void maxMinNorm(List<Neuron> neurons)
	{
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		int biasCount = hasBias() ? 1 : 0;
		for (int i = biasCount; i < neurons.size(); i++)
		{
			min = Math.min(min, neurons.get(i).getOutput());
			max = Math.min(max, neurons.get(i).getOutput());
		}
		for (int i = biasCount; i < neurons.size(); i++)
			neurons.get(i).setOutput(
					(neurons.get(i).getOutput() - min) / (max - min));
	}
	/**
	 * @param normType
	 *            the normType to set
	 */
	public void setNormType(byte normType)
	{
		this.normType = normType;
	}

	private void zScoreNorm(List<Neuron> neurons)
	{
		double mean = 0.0;
		double sd = 0.0;
		int biasCount = hasBias() ? 1 : 0;
		double[] values = new double[neurons.size() - biasCount];
		for (int i = biasCount; i < neurons.size(); i++)
			values[i - biasCount] = neurons.get(i).getOutput();
		mean = Util.mean(values);
		sd = Util.standardDeviation(values);
		for (int i = biasCount; i < neurons.size(); i++)
			neurons.get(i).setOutput((neurons.get(i).getOutput() - mean) / sd);
	}

}
