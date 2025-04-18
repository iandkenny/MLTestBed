package org.mltestbed.util.randgen;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * from https://www.javamex.com/tutorials/random_numbers/numerical_recipes.shtml
 * ported from Numerical Recipes 3rd Edition: The Art of Scientific Computing
 * 
 * @author ian
 *
 */
public class HighQualityRandom extends Random
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final long multiplier = 0x5DEECE66DL;

	private Lock l = null;
	private long u;
	private long v = 4101842887655102017L;
	private long w = 1;
	public HighQualityRandom()
	{
		l = new ReentrantLock();
//		setSeed(multiplier ^ System.nanoTime());
		setSeed(System.nanoTime());
	}

	public HighQualityRandom(long seed)
	{
		l = new ReentrantLock();
		setSeed(seed);
	}
	protected int next(int bits)
	{
		return (int) (nextLong() >>> (64 - bits));
	}

	public long nextLong()
	{
		l.lock();
		try
		{
			u = u * 2862933555777941757L + 7046029254386353087L;
			v ^= v >>> 17;
			v ^= v << 31;
			v ^= v >>> 8;
			w = 4294957665L * (w & 0xffffffff) + (w >>> 32);
			long x = u ^ (u << 21);
			x ^= x >>> 35;
			x ^= x << 4;
			long ret = (x + v) ^ w;
			return ret;
		} finally
		{
			l.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Random#setSeed(long)
	 */
	@Override
	public synchronized void setSeed(long seed)
	{
		if (l == null)
			l = new ReentrantLock();
		l.lock();
		u = seed ^ v;
		nextLong();
		v = u;
		nextLong();
		w = v;
		nextLong();
		l.unlock();
//		super.setSeed(seed);
	}

}