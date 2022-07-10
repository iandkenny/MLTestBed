package org.mltestbed.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.MemoryHandler;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.mltestbed.data.rawimport.ImportFDMData;
import org.mltestbed.data.rawimport.ImportIngberData;
import org.mltestbed.data.readDB.ReadIngber;
import org.mltestbed.util.Log;

public class Main
{
	private static Logger log = Logger.getLogger(MLUI.class.getName());
	private static boolean useMem = true;
	private static boolean useLog = false;;
	/**
	 * @return the useLog
	 */
	public static boolean isUseLog()
	{
		return useLog;
	}

	/**
	 * @param useLog the useLog to set
	 */
	public static void setUseLog(boolean useLog)
	{
		Main.useLog = useLog;
	}

	/**
	 * @return the log
	 */
	public static Logger getLog()
	{
		return log;
	}

	/**
	 * @param log
	 *            the log to set
	 */
	public static void setLog(Logger log)
	{
		Main.log = log;
	}

	private static final int MaxThreads = 10;
	/**
	 * @param useMem
	 *            the useMem to set
	 */
	public static void setUseMem(boolean useMem)
	{
		Main.useMem = useMem;
	}
	/**
	 * @return the useMem
	 */
	public static boolean isUseMem()
	{
		return useMem;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI()
	{
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		// Create and set up the window.
		final JFrame frame = new JFrame("Machine Learning Test Bed");
		final MLUI swarmUI = new MLUI(frame);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				swarmUI.quit(frame);
			}
		});
		// Create and set up the content pane.
		JComponent newContentPane = swarmUI;
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * 
	 */
	static void createLog()
	{
		new Log();
	}
	public static ArrayList<Class<Object>> getClasses(String pckgname)
			throws ClassNotFoundException
	{
		File directory = null;
		ClassLoader cld = Thread.currentThread().getContextClassLoader();
		if (cld == null)
		{
			throw new ClassNotFoundException("Can't get class loader.");
		}
		String path = pckgname.replace('.', '/');
		URL resource = cld.getResource(path);
		try
		{

			if (resource == null
					|| resource.toURI().toString().indexOf("jar!") != -1)
			{
				// throw new ClassNotFoundException("No resource for " + path);
				String classPath = System.getProperty("java.class.path");
				// if (!directory.isDirectory())
				return getClassesJar(pckgname, classPath);

			} else
			{
				// System.out.println(resource.toURI());
				// System.out.println(path);
				directory = new File(resource.toURI());

				return getClassesFile(pckgname, directory);
			}
		} catch (URISyntaxException e)
		{
			throw new ClassNotFoundException(pckgname + " (" + directory
					+ ") does not appear to be a valid package");

		}
	}

	public static ArrayList<Class<Object>> getClassesFile(String pckgname,
			File directory) throws ClassNotFoundException
	{
		ArrayList<Class<Object>> classes = new ArrayList<Class<Object>>();
		// Get a File object for the package

		if (directory.isDirectory())
		{
			// Get the list of the files contained in the package
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++)
			{
				// we are only interested in .class files
				if (files[i].endsWith(".class"))
				{
					// removes the .class extension
					Class<? extends Object> class1 = (Class<? extends Object>) Class
							.forName(pckgname + '.' + files[i].substring(0,
									files[i].length() - 6));
					classes.add((Class<Object>) class1);
				}
			}
		} else
		{
			throw new ClassNotFoundException(
					pckgname + " does not appear to be a valid package");
		}
		// Class[] classesA = new Class[classes.size()];
		// classes.toArray(classesA);
		return classes;
	}

	public static ArrayList<Class<Object>> getClassesJar(String pckgname,
			String classPath) throws ClassNotFoundException
	{
		ArrayList<Class<Object>> classes = new ArrayList<Class<Object>>();
		// Get a File object for the package
		JarFile jarFile = null;

		String path;
		try
		{
			path = pckgname.replace('.', '/');

			jarFile = new JarFile(classPath);

			// Get the list of the files contained in the package
			// String[] files = directory.list();
			for (Enumeration<JarEntry> jarEntries = jarFile
					.entries(); jarEntries.hasMoreElements();)
			{
				String name = jarEntries.nextElement().getName();
				// Log.getLogger().info(name);
				// we are only interested in .class files
				if (name.startsWith(path) && name.endsWith(".class"))
				{
					// removes the .class extension
					name = name.replace('/', '.');
					classes.add((Class<Object>) Class
							.forName(name.substring(0, name.length() - 6)));
				}
			}
			jarFile.close();
		} catch (NullPointerException x)
		{
			throw new ClassNotFoundException(pckgname + " (" + jarFile
					+ ") does not appear to be a valid package");
		} catch (IOException e)
		{
			throw new ClassNotFoundException(
					pckgname + " does not appear to be a valid package");
		}

		return classes;
	}

	public static void main(String[] args)
	{
		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("import=FDM"))
			{

				ImportFDMData[] ing = new ImportFDMData[MaxThreads];
				for (int i = 0; i < MaxThreads; i++)
				{
					ing[i] = new ImportFDMData(null);
					// ing [i] = new FDMImportController();
					ing[i].start();
					try
					{
						Thread.sleep(5000);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				try
				{

					boolean bFlag;
					do
					{
						bFlag = false;
						for (int i = 0; i < ing.length; i++)
						{
							ing[i].join();
							bFlag = bFlag && !ing[i].isAlive();
							// Thread.sleep(5000);
						}
					} while (!bFlag);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (args[0].equalsIgnoreCase("import=Ingber"))
			{
				ImportIngberData threads[] = new ImportIngberData[5];

				for (int i = 0; i < MaxThreads; i++)
				{
					ImportIngberData ing = new ImportIngberData(null, null);
					ing.start();
					threads[i] = ing;
				}
				try
				{

					boolean bFlag;
					do
					{
						bFlag = true;
						for (int i = 0; i < threads.length; i++)
						{
							bFlag = bFlag & threads[i].isFinished();
							Thread.sleep(5000);
						}
					} while (!bFlag);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (args[0].equalsIgnoreCase("score=Ingber"))
			{
				createLog();
				ReadIngber ingber = new ReadIngber();
				ingber.preScore();
			}

		} else
		{
			// Schedule a job for the event-dispatching thread:
			// creating and showing this application's GUI.
			javax.swing.SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					createAndShowGUI();
				}
			});
		}
	}
}
