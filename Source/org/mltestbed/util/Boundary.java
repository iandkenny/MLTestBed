/**
 * 
 */
package org.mltestbed.util;

import org.mltestbed.testFunctions.TestBase;

/**
 * @author ian
 * 
 */
public class Boundary
{

	public static void wrap(Particle particle, TestBase test)
	{
		int size = particle.getPosition().size();
		for (int j = 0; j < size; j++)
		{
			double max = test.getMax(j);
			double min = test.getMin(j);
			Double p = particle.getPosition().get(j);
			if (p > max)
				p = min + (p - max);
			else if (p < min)
				p = max - (p - min);
			particle.getPosition().set(j, p);
		}
	}
	public static void stick(Particle particle, TestBase test)
	{
		int size = particle.getPosition().size();
		for (int j = 0; j < size; j++)
		{
			double max = test.getMax(j);
			double min = test.getMin(j);
			Double p = particle.getPosition().get(j);
			if (p > max)
				p = max;
			else if (p < min)
				p = min;
			particle.getPosition().set(j, p);
		}

	}
	public static void bounce(Particle particle, TestBase test)
	{
		int size = particle.getPosition().size();
		for (int j = 0; j < size; j++)
		{
			double max = test.getMax(j);
			double min = test.getMin(j);
			Double p = particle.getPosition().get(j);
			if (p > max)
				p = max - (p - max);
			else if (p < min)
				p = min + (p - min);
			particle.getPosition().set(j, p);
		}
	}
	public static String[] getSupportedBoundaries()
	{
		String buf[] =
		{"wrap", "bounce", "stick"};
		return buf;
	}
}
