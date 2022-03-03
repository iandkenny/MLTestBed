/**
 * 
 */
package org.mltestbed.util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.MemoryHandler;

/**
 * @author ian
 * 
 */

public class Log
{
	private static FileHandler fh;
	private static Logger logger;
	private static MemoryHandler mh;

	/**
	 * 
	 */
	public Log()
	{
		super();
		init();
	}
	/**
	 * 
	 */
	private static void init()
	{
		logger = Logger.getLogger(Log.class.getName());
		logger.setLevel(Level.ALL);
		try
		{
			fh = new FileHandler("swarm.%u.%g.log.xml", 1024 * 1024, 100);
			mh = new MemoryHandler(fh, 50, Level.SEVERE);
			logger.addHandler(mh);
		} catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 
	 */
	public static void push()
	{
		mh.push();
	}
	/**
	 * @return the logger
	 */
	public static Logger getLogger()
	{
		if (logger == null)
			init();
		return logger;
	}
	/**
	 * @param l
	 * @param msg
	 */
	public static void log(Level l, String msg)
	{
		try
		{
			if (logger == null)
				init();
			logger.log(l, msg);
		} catch (Exception e)
		{
			System.err.println(e.getMessage() + " " + msg);
			e.printStackTrace();
		}
	}

	/**
	 * @param l
	 * @param e
	 */
	public static void log(Level l, Exception e)
	{
		String msg = "";
		try
		{
			if (logger == null)
				init();
			msg = e.getMessage();
			msg += "\n" + e.getCause() + "\n";
			if (l == Level.SEVERE)
			{
				StackTraceElement elements[] = e.getStackTrace();
				for (int i = 0, n = elements.length; i < n; i++)
				{
					msg += elements[i].getFileName() + ":"
							+ elements[i].getLineNumber() + " ==> "
							+ elements[i].getMethodName() + "()\n";
				}

				if (e instanceof SQLException)
				{
					SQLException se = (SQLException) e;
					do
					{
						msg += se.getSQLState() + "\n";
						se = se.getNextException();

					} while (se != null);
				}
			}
			logger.log(l, msg);
		} catch (Exception e1)
		{
			System.err.println(e1.getMessage() + " " + msg);
			e1.printStackTrace();
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable
	{
		push();
		mh.close();
		fh.close();
//		super.finalize();
	}

}
