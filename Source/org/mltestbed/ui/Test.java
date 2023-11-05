package org.mltestbed.ui;

import java.awt.Point;
import java.util.Vector;

import org.mltestbed.util.Log;

public class Test
{

	public static void main(String[] args)
	{
		Vector<Long> v = new Vector<Long>();
		long i = 0;
		long j = (long) Integer.MAX_VALUE * 2;
		while (i < j)
		{
			v.add(new Long(i++));
		}
		System.out.println("last element value =" + v.lastElement());
	}

	/**
	 *
	 */
	public static void sameObject()
	{
		Point pnt1 = new Point(0, 0);
		Point pnt2 = new Point(0, 0);
		Log.getLogger().info("X: " + pnt1.x + " Y: " + pnt1.y);
		Log.getLogger().info("X: " + pnt2.x + " Y: " + pnt2.y);
		Log.getLogger().info(" ");
		tricky(pnt1, pnt2);
		Log.getLogger().info("X: " + pnt1.x + " Y:" + pnt1.y);
		Log.getLogger().info("X: " + pnt2.x + " Y: " + pnt2.y);

		Vector<Integer> one = new Vector<>();
		Vector<Integer> two = new Vector<>();

		one.add(Integer.valueOf(1));
		two.addAll(one);
		if (one.get(0).equals(two.get(0)))

			Log.getLogger().info("The same object");
	}

	public static void tricky(Point arg1, Point arg2)
	{
		arg1.x = 100;
		arg1.y = 100;
		Point temp = arg1;
		arg1 = arg2;
		arg2 = temp;
	}

}
