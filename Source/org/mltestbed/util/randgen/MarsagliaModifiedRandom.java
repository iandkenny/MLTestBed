/**
 * 
 */
package org.mltestbed.util.randgen;

import java.util.Random;

/**
 * @author Ian Kenny
 *
 */
public class MarsagliaModifiedRandom extends Random
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final long multiplier = 0x5DEECE66DL;

	private static long x = 1;

	private long seed = 1;
	/**
	 * 
	 */
	public MarsagliaModifiedRandom()
	{
//		setSeed(System.nanoTime() >>> Thread.currentThread().getId());
		//setSeed(Thread.currentThread().getId());
		setSeed(1);
	}

	/**
	 * @param seed
	 */
	protected MarsagliaModifiedRandom(long seed)
	{
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
		x = modified();
		return x;
	}

	/**
	 * 
	 */
	private long modified()
	{

		/*
		 * After: Bastos-Filho, C. J. A. et al. (2010) ‘Impact of the Random
		 * Number generator quality on particle swarm optimization algorithm
		 * running on graphic processor units’, in 2010 10th International
		 * Conference on Hybrid Intelligent Systems. IEEE, pp. 85–90. doi:
		 * 10.1109/HIS.2010.5601073.
		 */
		x = System.nanoTime() + System.nanoTime() * seed;
		// x ^= (x << 21);
		// x ^= (x >>> 35);
		// x ^= (x << 4);
		// after L'Ecuyer & Simard (2007)
		x ^= (x << 13);
		x ^= (x >>> 7);
		x ^= (x << 17);
		if (x < 0)
			x = -x;
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
		this.seed = x = seed;
	}

}
