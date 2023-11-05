/**
 * 
 */
package org.mltestbed.util.randgen;

import java.util.Random;

/**
 * @author Ian Kenny
 *
 */
public class KISSRandom extends Random
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static long kiss_x = 1;
	private static long kiss_y = 2;
	private static long kiss_z = 4;
	private static long kiss_w = 8;
	private static long kiss_carry = 0;
	private static long kiss_k;
	private static long kiss_m;
	/**
	 * 
	 */
	public KISSRandom()
	{
		setSeed(System.nanoTime());
	}

	/**
	 * @param seed
	 */
	public KISSRandom(long seed)
	{
		setSeed(seed);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Random#next(int)
	 */
	@Override
	protected int next(int bits)
	{
		return (int) (nextLong() >>> (64 - bits));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Random#nextLong()
	 */
	@Override
	public long nextLong()
	{
//		kiss_x = 69069 * kiss_x + 1234567;
		kiss_x = kiss_x * 69069 + 1;
		kiss_y ^= kiss_y << 13;
		kiss_y ^= kiss_y >>> 17;
		kiss_y ^= kiss_y << 5;
		kiss_k = (kiss_z >> 2) + (kiss_w >> 3) + (kiss_carry >> 2);
		kiss_m = kiss_w + kiss_w + kiss_z + kiss_carry;
		kiss_z = kiss_w;
		kiss_w = kiss_m;
		kiss_carry = kiss_k >> 30;
		return kiss_x + kiss_y + kiss_w;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Random#setSeed(long)
	 */
	@Override
	public synchronized void setSeed(long seed)
	{
		kiss_x = seed | 1;
		kiss_y = seed | 2;
		kiss_z = seed | 4;
		kiss_w = seed | 8;
		kiss_carry = 0;

	}

}
