package org.mltestbed.util.interleaver;

/*
 * [14:17] Dhouha.Kbaier

ARP

[14:17] Dhouha.Kbaier

for (i=1;i<=NDAT;i++)

     {

      mat_ent[i]=1+((p0*i+i0)%NDAT);

     

      mat_des[mat_ent[i]]=i;

      //printf(" i= %d mat_ent_post[%d]=%d et mat_des_post[%d]=%d \n ",i,i, mat_ent_post[i],mat_ent_post[i],mat_des_post[mat_ent_post[i]]);    

     }

NDAT= 128

p0 i0 formulas from ARP

[14:20] Dhouha.Kbaier

: P0 ≈ √ 2P ⇒ P0 =√ 2*128


p0 needs to b e prime with P the size of your block


i0 =p0/2
 */

public class ARPInterleaver extends Interleaver
{

	// Method to perform ARP interleaving
	public int[] ARPInterleave(int[] input)
	{

		int length = input.length;
		int[] output = new int[length];
		int[] indexMapping = generateIndexMapping(length);

		for (int i = 0; i < length; i++)
		{
			output[indexMapping[i]] = input[i];
		}

		return output;
	}

	private int[] indexMapping;

	/**
	 * 
	 */
	public ARPInterleaver(int length)
	{
		super(length);
		indexMapping = generateIndexMapping(length);
	}

	public int get(int index) throws Exception
	{
		if (index >= indexMapping.length)
			throw new Exception("index exceeds length");
		return indexMapping[index];

	}
	private int calcP0(int length)
	{
		int p = length;
		int p0 = (int) Math.sqrt(2 * p);
		while (p0 % p != 0)
			p0++;
		return p0;
	}
	// Method to generate the ARP index mapping
	protected int[] generateIndexMapping(int length)
	{
		int[] indexMapping = new int[length];
		int[] map = new int[length];
		// Initialize the index mapping in a sequential order (0, 1, 2, ...,
		// length-1)
		for (int i = 0; i < length; i++)
		{
			indexMapping[i] = i;
		}
		int p0 = calcP0(length);
		int i0 = p0/2;
		for (int i = 0; i < length; i++)

		{

			map[i] = 1 + ((p0 * i + i0) % length);

			indexMapping[map[i]] = i;

			// printf(" i= %d mat_ent_post[%d]=%d et mat_des_post[%d]=%d \n
			// ",i,i,
			// mat_ent_post[i],mat_ent_post[i],mat_des_post[mat_ent_post[i]]);

		}

		return indexMapping;
	}

	public static void main(String[] args)
	{
		int[] input =
		{1, 2, 3, 4, 5, 6, 7, 8};
		ARPInterleaver s = new ARPInterleaver(input.length);
		// Perform S-random interleaving
		int[] interleaved = s.ARPInterleave(input);

		// Display the interleaved baseData
		System.out.print("Interleaved baseData: ");
		for (int b : interleaved)
		{
			System.out.print(b + " ");
		}
	}
}
