/**
 * 
 */
package org.mltestbed.util.randgen;

import java.util.Random;

/**
 * @author Ian Kenny
 *
 */
public class MarsagliaRandom extends Random
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final long multiplier = 0x5DEECE66DL;

	private static long x = 1;
	/**
	 * 
	 */
	public MarsagliaRandom()
	{
//		setSeed(multiplier ^ System.nanoTime());
		setSeed(System.nanoTime());
	}

	/**
	 * @param seed
	 */
	protected MarsagliaRandom(long seed)
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
//		return super.next(bits);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Random#nextLong()
	 */
	@Override
	public long nextLong()
	{
		x = original();
		return x;
	}

	/**
	 * 
	 */
	private long original()
	{
		// x ^= (x << 21);
		// x ^= (x >>> 35);
		// x ^= (x << 4);
		// after L'Ecuyer & Simard (2007)
		x ^= (x << 13);
		x ^= (x >>> 7);
		x ^= (x << 17);
		return x;
	}
	@SuppressWarnings("unused")
	private long modified()
	{

		/*
		 * After: Bastos-Filho, C. J. A. et al. (2010) ‘Impact of the Random
		 * Number generator quality on particle swarm optimization algorithm
		 * running on graphic processor units’, in 2010 10th International
		 * Conference on Hybrid Intelligent Systems. IEEE, pp. 85–90. doi:
		 * 10.1109/HIS.2010.5601073.
		 */
		x = System.nanoTime()
				+ System.nanoTime() * Thread.currentThread().getId();
		x ^= (x << 21);
		x ^= (x >>> 35);
		x ^= (x << 4);

		return x;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Random#setSeed(long)
	 */
	@Override
	public synchronized void setSeed(long seed)
	{
		x = seed;
	}

}
