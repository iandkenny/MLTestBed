package org.mltestbed.util.interleaver;

import java.util.Random;

import org.mltestbed.util.RandGen;

public class SRandomInterleaver extends Interleaver 
{

	// Method to perform S-random interleaving
	public int[] sRandomInterleave(int[] input)
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
	public SRandomInterleaver(int length)
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
	// Method to generate the S-random index mapping
	protected int[] generateIndexMapping(int length)
	{
		int[] indexMapping = new int[length];
		Random random = RandGen.getLastCreated();

		// Initialize the index mapping in a sequential order (0, 1, 2, ...,
		// length-1)
		for (int i = 0; i < length; i++)
		{
			indexMapping[i] = i;
		}

		// Perform random swaps to create the S-random mapping
		for (int i = 0; i < length; i++)
		{
			int j = random.nextInt(length);
			int temp = indexMapping[i];
			indexMapping[i] = indexMapping[j];
			indexMapping[j] = temp;
		}

		return indexMapping;
	}

	public static void main(String[] args)
	{
		int[] input =
		{1, 2, 3, 4, 5, 6, 7, 8};
		SRandomInterleaver s = new SRandomInterleaver(input.length);
		// Perform S-random interleaving
		int[] interleaved = s.sRandomInterleave(input);

		// Display the interleaved baseData
		System.out.print("Interleaved baseData: ");
		for (int b : interleaved)
		{
			System.out.print(b + " ");
		}
	}
}
