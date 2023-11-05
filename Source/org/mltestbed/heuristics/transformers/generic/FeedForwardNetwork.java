package org.mltestbed.heuristics.transformers.generic;

import org.mltestbed.heuristics.ANN.ArtificialNeuralNetwork;

public class FeedForwardNetwork extends ArtificialNeuralNetwork
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	FeedForwardNetwork(int hidden_size, int filter_size, float dropout_rate)
	{
		super("FFN");
		ArtificialNeuralNetwork ffn = createFeedForwardLinearNetwork( hidden_size, filter_size, dropout_rate);
		
		setLayers(ffn.getLayers());

//	        layer1 = new Layer(hidden_size, filter_size)
//	        self.relu = nn.ReLU()
//	        self.dropout = nn.Dropout(dropout_rate)
//	        layer2 = nn.Linear(filter_size, hidden_size)

//	        initialize_weight(layer1)
//	        initialize_weight(layer2)
	}
//	    def forward(self, x):
//	        x = self.layer1(x)
//	        x = self.relu(x)
//	        x = self.dropout(x)
//	        x = self.layer2(x)
//	        return x


}
