package org.mltestbed.heuristics.transformers.generic;

import org.mltestbed.heuristics.ANN.Layer;

public class MultiHeadAttention extends Layer 
{
    
	private static final String MULTI_HEAD_ATTENTION_LAYER = "MultiHeadAttention Layer";
	private int head_size =8;
	private int att_size;
	private double scale;
	public  MultiHeadAttention(int hidden_size, float dropout_rate, int head_size){
       super(MULTI_HEAD_ATTENTION_LAYER);

        this.head_size = head_size;

        this.att_size = att_size = Math.floorDiv(hidden_size, head_size);
        this.scale = Math.pow(att_size, -0.5);

        this.linear_q = nn.Linear(hidden_size, head_size * att_size, bias=False)
        this.linear_k = nn.Linear(hidden_size, head_size * att_size, bias=False)
        this.linear_v = nn.Linear(hidden_size, head_size * att_size, bias=False)
        initialize_weight(self.linear_q)
        initialize_weight(self.linear_k)
        initialize_weight(self.linear_v)

        this.att_dropout = nn.Dropout(dropout_rate)

        		this.output_layer = nn.Linear(head_size * att_size, hidden_size,
                                      bias=False)
        initialize_weight(self.output_layer)

    }
	public MultiHeadAttention(int hiddenSize, float dropoutRate)
	{
		// TODO Auto-generated constructor stub
	}
	public void forward(self, q, k, v, mask, cache=None){
        orig_q_size = q.size()

        d_k = att_size;
        d_v = att_size;
        batch_size = q.size(0);

        // head_i = Attention(Q(W^Q)_i, K(W^K)_i, V(W^V)_i)
        q = self.linear_q(q).view(batch_size, -1, self.head_size, d_k)
        if cache is not None and 'encdec_k' in cache:
            k, v = cache['encdec_k'], cache['encdec_v']
        else:
            k = self.linear_k(k).view(batch_size, -1, self.head_size, d_k)
            v = self.linear_v(v).view(batch_size, -1, self.head_size, d_v)

            if cache is not None:
                cache['encdec_k'], cache['encdec_v'] = k, v

        q = q.transpose(1, 2)                  // [b, h, q_len, d_k]
        v = v.transpose(1, 2)                  // [b, h, v_len, d_v]
        k = k.transpose(1, 2).transpose(2, 3)  // [b, h, d_k, k_len]

        // Scaled Dot-Product Attention.
        // Attention(Q, K, V) = softmax((QK^T)/sqrt(d_k))V
        q.mul_(self.scale)
        x = torch.matmul(q, k)  // [b, h, q_len, k_len]
        x.masked_fill_(mask.unsqueeze(1), -1e9)
        x = torch.softmax(x, dim=3)
        x = self.att_dropout(x)
        x = x.matmul(v)  // [b, h, q_len, attn]

        x = x.transpose(1, 2).contiguous()  // [b, q_len, h, attn]
        x = x.view(batch_size, -1, self.head_size * d_v)

        x = self.output_layer(x)

        assert x.size() == orig_q_size
        return x
    }

