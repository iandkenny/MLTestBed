/**
 * 
 */
package org.mltestbed.util.interleaver;

/**
 * 
 */
public abstract class Interleaver
{

	public int[] Interleave(int[] input)
	{
		return null;
	}

	/**
	 * 
	 */
	public Interleaver(int length)
	{
	}
abstract public int get(int index) throws Exception;
abstract protected int[] generateIndexMapping(int length);	
}
