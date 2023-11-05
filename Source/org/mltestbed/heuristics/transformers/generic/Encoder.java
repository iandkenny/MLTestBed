package org.mltestbed.heuristics.transformers.generic;

public class Encoder extends TransformerBase
{
	public Encoder(TransformerBase t)
	{
		super(t);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Encoder(int hidden_size, int filter_size, float dropout_rate, int n_layers){
        super(this);

        encoders = [EncoderLayer(hidden_size, filter_size, dropout_rate)
                    for _ in range(n_layers)]
        self.layers = nn.ModuleList(encoders)

        self.last_norm = nn.LayerNorm(hidden_size, eps=1e-6)

    }  public void forward(self, inputs, mask){
        encoder_output = inputs
        for enc_layer in self.layers:
            encoder_output = enc_layer(encoder_output, mask)
        return self.last_norm(encoder_output)


}
