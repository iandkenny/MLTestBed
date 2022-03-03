/**
 * 
 */
package org.mltestbed.util;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingQueue;

import org.mltestbed.ui.MLUI;


/**
 * @author Ian Kenny
 * 
 */
public class UIHandler
{
	private static long minMem;
	private static Runtime runtime = Runtime.getRuntime();
	private static MLUI swarmui;

	public static void checkMemFree(LinkedBlockingQueue<?> q)
	{
		runtime.gc();
		if (runtime.freeMemory() < (minMem * 4))
			while (runtime.freeMemory() < (minMem * 7))
				runtime.gc();
	}
	public static String formatRuntime(long l)
	{
		String format = "";
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(l);
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		try
		{
			NumberFormat numberInstance = NumberFormat.getNumberInstance();
			numberInstance.setMinimumIntegerDigits(2);
			Integer day = cal.get(Calendar.DAY_OF_YEAR) - 1;
			Integer hour = cal.get(Calendar.HOUR_OF_DAY);
			Integer minute = cal.get(Calendar.MINUTE);
			Integer sec = cal.get(Calendar.SECOND);
			format = ((day == null) ? "00" : numberInstance.format(day)) + ":"
					+ ((hour == null) ? "00" : numberInstance.format(hour))
					+ ":"
					+ ((minute == null) ? "00" : numberInstance.format(minute))
					+ ":" + ((sec == null) ? "00" : numberInstance.format(sec));
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return format;
	}

	/**
	 * @return the swarmui
	 */
	public static MLUI getSwarmui()
	{
		return swarmui;
	}
	public UIHandler()
	{
		minMem = runtime.freeMemory() / 10;
	}
	public UIHandler(MLUI ui)
	{
		swarmui = ui;
		minMem = runtime.freeMemory() / 10;
	}
}
