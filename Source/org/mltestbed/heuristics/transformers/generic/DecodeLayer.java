package org.mltestbed.heuristics.transformers.generic;

import java.util.ArrayList;

import org.mltestbed.heuristics.ANN.ArtificialNeuralNetwork;
import org.mltestbed.heuristics.ANN.Layer;
import org.mltestbed.heuristics.ANN.Neuron;
import org.mltestbed.heuristics.ANN.activators.ActivationStrategy;
import org.mltestbed.heuristics.ANN.activators.RectifiedLinearActivationStrategy;

public class DecodeLayer extends Layer
{

	private ArrayList<Layer> attentionLayers;
	private MultiHeadAttention attention;
	private ArrayList<Layer> enc_dec_attention_layers;
	private MultiHeadAttention enc_dec_attention;
	private ArrayList<Layer> ffn_norm;
	private FeedForwardNetwork ffn;
	public DecodeLayer(String id)
	{
		super(id);
		// TODO Auto-generated constructor stub
	}
	public void init(ArtificialNeuralNetwork ann, int hiddenSize, int filterSize, float dropoutRate)
	{
		ArrayList<Integer> neuronsperLayer = new ArrayList<Integer>();
		ArrayList<ActivationStrategy> activationStrategy = new ArrayList<ActivationStrategy>();
		
		neuronsperLayer.add(-hiddenSize);
		activationStrategy.add(new RectifiedLinearActivationStrategy());
		attentionLayers= ArtificialNeuralNetwork.createLinearLayers(ann, neuronsperLayer, activationStrategy,dropoutRate);
        attention = new MultiHeadAttention(hiddenSize, dropoutRate);
//        attention_dropout = nn.Dropout(dropoutRate)

        enc_dec_attention_layers = ArtificialNeuralNetwork.createLinearLayers(ann, neuronsperLayer, activationStrategy,dropoutRate);
        enc_dec_attention = new MultiHeadAttention(hiddenSize, dropoutRate);
//        self.enc_dec_attention_dropout = nn.Dropout(dropoutRate)

        ffn_norm = ArtificialNeuralNetwork.createLinearLayers(ann, neuronsperLayer, activationStrategy,dropoutRate);
        ffn = new FeedForwardNetwork(hiddenSize, filterSize, dropoutRate);
//        self.ffn_dropout = nn.Dropout(dropoutRate)

    }
	
	public void forward(Layer x, Layer enc_output, Layer self_mask, Layer i_mask, Layer cache){
        Layer y = self_attention_norm(x);
        y = self_attention(y, y, y, self_mask);
        y = self_attention_dropout(y);
        x = x + y;
    
        if (enc_output != null){
            y = enc_dec_attention_norm(x);
            y = enc_dec_attention(y, enc_output, enc_output, i_mask,
                                       cache);
            y = enc_dec_attention_dropout(y);
            x = x + y;
    }
        y = ffn_norm(x);
        y = ffn(y);
        y = ffn_dropout(y);
        x = x + y;
        return x;
	}

}
