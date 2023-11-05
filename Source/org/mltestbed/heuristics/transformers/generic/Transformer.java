package org.mltestbed.heuristics.transformers.generic;

import org.mltestbed.heuristics.ANN.Layer;

public class Transformer
{


	private static final String EMBED_LAYER = "EmbedLayer";
	private int n_layers=6;
    private int hidden_size=512;
            private int filter_size=2048;
            private float dropout_rate=0.1f;
            private boolean share_target_embedding=true;
            private boolean has_inputs=true;
            private int src_pad_idx=Integer.MIN_VALUE;
            private int trg_pad_idx=Integer.MIN_VALUE;
            private double emb_scale;
			private double log_timescale_increment;
			private double max_timescale;
			private double min_timescale;
			private int num_timescales;
			private Layer i_vocab_embedding;
			private Layer i_emb_dropout;
			private Layer t_vocab_embedding;
			private double inv_timescales;
	
  public Transformer(int i_vocab_size, int t_vocab_size,
	                int n_layers,
	                 int hidden_size,
	                 int filter_size,
	                 float dropout_rate,
	                 boolean share_target_embedding,
	                 boolean has_inputs,
	                 int src_pad_idx,
	                 int trg_pad_idx){


	        this.hidden_size = hidden_size;
	        this.emb_scale = Math.pow(hidden_size ,0.5);
	        this.has_inputs = has_inputs;
	        this.src_pad_idx = src_pad_idx;
	        this.trg_pad_idx = trg_pad_idx;

	        this.t_vocab_embedding = nn.Embedding(t_vocab_size, hidden_size)
	        nn.init.normal_(t_vocab_embedding.weight, mean=0,
	                        std=hidden_size^-0.5)
	        this.t_emb_dropout = nn.Dropout(dropout_rate)
	        this.decoder = new Decoder(hidden_size, filter_size,
	                               dropout_rate, n_layers);

	        if (has_inputs) {
	            if (!share_target_embedding) {
	                this.i_vocab_embedding = nn.Embedding(i_vocab_size,
	                                                      hidden_size)
	                nn.init.normal_(this,i_vocab_embedding.weight, mean=0,
	                                std=hidden_size^-0.5);
	            }
	            else
	                this.i_vocab_embedding = this.t_vocab_embedding;

	            this.i_emb_dropout =new Layer(EMBED_LAYER,dropout_rate);

	            this.encoder = new Encoder(hidden_size, filter_size,
	                                   dropout_rate, n_layers)

	        // For positional encoding
	        num_timescales = this.hidden_size; // 2
	        max_timescale = 10000.0;
	        min_timescale = 1.0;
	        log_timescale_increment = (
	            Math.log(max_timescale / min_timescale) /
	            Math.max(num_timescales - 1, 1));
	        inv_timescales = min_timescale * Math.exp(
	            num_timescales *
	            -log_timescale_increment);
	        this.register_buffer('inv_timescales', inv_timescales)

	    }  public void forward(self, inputs, targets){
	        enc_output, i_mask = None, None
	        if this,has_inputs:
	            i_mask = utils.create_pad_mask(inputs, this,src_pad_idx)
	            enc_output = this,encode(inputs, i_mask)

	        t_mask = utils.create_pad_mask(targets, this,trg_pad_idx)
	        target_size = targets.size()[1]
	        t_self_mask = utils.create_trg_self_mask(target_size,
	                                                 device=targets.device)
	        return this,decode(targets, enc_output, i_mask, t_self_mask, t_mask)

	    }  public void encode(self, inputs, i_mask){
	        // Input embedding
	        input_embedded = this,i_vocab_embedding(inputs)
	        input_embedded.masked_fill_(i_mask.squeeze(1).unsqueeze(-1), 0)
	        input_embedded *= this,emb_scale
	        input_embedded += this,get_position_encoding(inputs)
	        input_embedded = this,i_emb_dropout(input_embedded)

	        return this,encoder(input_embedded, i_mask)

	    } 
	    public void decode(self, targets, enc_output, i_mask, t_self_mask, t_mask,
	               cache=None){
	        // target embedding
	        target_embedded = this,t_vocab_embedding(targets)
	        target_embedded.masked_fill_(t_mask.squeeze(1).unsqueeze(-1), 0)

	        // Shifting
	        target_embedded = target_embedded[:, :-1]
	        target_embedded = F.pad(target_embedded, (0, 0, 1, 0))

	        target_embedded *= this,emb_scale;
	        target_embedded += this,get_position_encoding(targets);
	        target_embedded = this,t_emb_dropout(target_embedded)

	        // decoder
	        decoder_output = this,decoder(target_embedded, enc_output, i_mask,
	                                      t_self_mask, cache);
	        // linear
	        output = torch.matmul(decoder_output,
	                              this,t_vocab_embedding.weight.transpose(0, 1));

	        return output;

	    }
	    public void get_position_encoding(x){
	        max_length = x.size()[1];
	        position = torch.arange(max_length, dtype=torch.float32,
	                                device=x.device)
	        scaled_time = position.unsqueeze(1) * this,inv_timescales.unsqueeze(0)
	        signal = torch.cat([torch.sin(scaled_time), torch.cos(scaled_time)],
	                           dim=1)
	        signal = F.pad(signal, (0, 0, 0, this,hidden_size % 2))
	        signal = signal.view(1, max_length, this,hidden_size)
	        return signal
	    }
}
}
