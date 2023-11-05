package org.mltestbed.heuristics.transformers.generic;

import org.mltestbed.heuristics.ANN.ArtificialNeuralNetwork;

public class Decoder extends ArtificialNeuralNetwork
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Decoder(int hidden_size, int filter_size, float dropout_rate, int n_layers){
	        super("Decoder");

	        decoders = [DecoderLayer(hidden_size, filter_size, dropout_rate)
	                    for _ in range(n_layers)]
	        this.layers = nn.ModuleList(decoders);

	        this.last_norm = nn.LayerNorm(hidden_size, eps=1e-6)

	    }  public void forward(self, targets, enc_output, i_mask, t_self_mask, cache){
	        decoder_output = targets
	        for i, dec_layer in enumerate(self.layers){
	            layer_cache = null
	            if cache is not None:
	                if i not in cache:
	                    cache[i] = {}
	                layer_cache = cache[i]
	            decoder_output = dec_layer(decoder_output, enc_output,
	                                       t_self_mask, i_mask, layer_cache)
	        return self.last_norm(decoder_output)

}
