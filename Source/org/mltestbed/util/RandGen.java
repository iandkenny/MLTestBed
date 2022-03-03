/**
 * 
 */
package org.mltestbed.util;

import java.security.SecureRandom;
import java.util.Random;

import org.mltestbed.util.randgen.HighQualityRandom;
import org.mltestbed.util.randgen.KISSRandom;
import org.mltestbed.util.randgen.MarsagliaModifiedRandom;
import org.mltestbed.util.randgen.MarsagliaRandom;

/**
 * @author Ian Kenny
 * 
 */
public class RandGen
{
	private static int getLast = 0;
	public static final int HIGHQUALITY = 1;
	public static final int JAVARANDOM = 0;
	public static final int JAVASECURERANDOM = 5;
	public static final int KISSRANDOM = 2;
	public static final int MARSAGLIARANDOM = 3;
	public static final int MODIFIEDMARSAGLIARANDOM = 4;

	public static Random getLastCreated()
	{
		return getRand(getLast);
	}
	private static int getOrDefault(String rand, int index)
	{
		String[] buf = getSupportedText();
		for (int i = 0; i < buf.length; i++)
		{
			if (buf[i].equalsIgnoreCase(rand))
			{
				index = i;
				break;
			}
		}
		return index;
	}
	/**
	 * @param rnd
	 * @return requested random number generator
	 */
	public static Random getRand(int rnd)
	{
		Random rand;
		switch (rnd)
		{
			case HIGHQUALITY :
				rand = new HighQualityRandom();
				break;
			case KISSRANDOM :
				rand = new KISSRandom();
				break;
			case MARSAGLIARANDOM :
				rand = new MarsagliaRandom();
				break;
			case MODIFIEDMARSAGLIARANDOM :
				rand = new MarsagliaModifiedRandom();
				break;
			case JAVASECURERANDOM :
				rand = new SecureRandom();
				break;
			default :
				rand = new Random();
				break;
		}
		getLast = rnd;
		return rand;
	}
	public static Random getRand(String rand)
	{

		return getRand(getOrDefault(rand, JAVARANDOM));

	}
	public static String[] getSupportedText()
	{
		String[] buf = new String[6];

		buf[JAVARANDOM] = "Standard Java Random Number Generator";
		buf[HIGHQUALITY] = "High Quality Random Number Generator";
		buf[KISSRANDOM] = "KISS Random Number Generator";
		buf[MARSAGLIARANDOM] = "Marsaglia Random Number Generator";
		buf[MODIFIEDMARSAGLIARANDOM] = "Modified Marsaglia Random Number Generator";
		buf[JAVASECURERANDOM] = "Secure Java Random Number Generator";
		return buf;
	}
}
