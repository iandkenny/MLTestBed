/**
 * 
 */
package org.mltestbed.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.util.logging.Level;

/**
 * @author ian
 *
 */
public class MemoryBufferedFile
{
	private String funcSpecific;
	private File tmpFile;

	public MemoryBufferedFile()
	{
	}

	public MemoryBufferedFile(String str)
	{
		write(str);
	}

	public void destroy()
	{
		funcSpecific = null;
		if (tmpFile != null)
		{
			tmpFile.delete();
			tmpFile = null;
		}
	}
	public BufferedReader getReader()
	{
		BufferedReader reader = null;
		try
		{
			// must return a non null reader

			if (tmpFile != null && this.funcSpecific.isEmpty())
				synchronized (tmpFile)
				{
					if (tmpFile.exists())
						reader = new BufferedReader(new FileReader(tmpFile));
				}
			else
				reader = new BufferedReader(
						new StringReader(this.funcSpecific));

		} catch (IOException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

		return reader;
	}

	/**
	 * @return the funcSpecific
	 */
	public String read()
	{
		String str = "";
		if (tmpFile != null && this.funcSpecific.isEmpty())
		{
			StringBuilder funcSpecific = new StringBuilder("");
			try
			{
				// must return a non null reader
				if (tmpFile.exists())
				{
					BufferedReader reader = null;
					reader = new BufferedReader(new FileReader(tmpFile));
					synchronized (tmpFile)
					{
						String line;
						if (reader != null)
							while ((line = reader.readLine()) != null)
								funcSpecific.append(line);

						reader.close();
					}
					reader = null;
				}
				str = funcSpecific.toString();
			} catch (IOException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			}
		} else
			str = this.funcSpecific;
		return str;
	}
	public String retrieveBuffer()
	{

		String buf = "";
		try
		{
			if (tmpFile != null)
			{
				StringBuilder sb = new StringBuilder("");
				BufferedReader br = new BufferedReader(new FileReader(tmpFile));
				while ((buf = br.readLine()) != null)
					sb.append(buf);
				br.close();
				br = null;
				buf = sb.toString();
			} else
				buf = new String(funcSpecific);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}
		return buf;
	}

	public void write(BufferedReader funcSpecific)
	{
		if (funcSpecific != null)
		{
			try
			{
				if (Util.checkMemFree())
				{
					String line;
					StringBuilder str = new StringBuilder();
					if (funcSpecific != null)
						while ((line = funcSpecific.readLine()) != null)
							str.append(line);
					this.funcSpecific = str.toString();
				} else
				{
					this.funcSpecific = "";
					BufferedWriter writer = null;
					if (tmpFile == null)
						tmpFile = Util.createTempFile();
					else
					{
						// This is done due to previous issues with BufferWriter
						// appending when it shouldn't left in for compatibility
						RandomAccessFile randomAccessFile = new RandomAccessFile(
								tmpFile, "rw");
						randomAccessFile.setLength(0);
						randomAccessFile.close();
					}
					do
					{
						synchronized (tmpFile)
						{
							writer = new BufferedWriter(
									new FileWriter(tmpFile, false));
							String line;
							while ((line = funcSpecific.readLine()) != null)
							{
								writer.write(line);
								writer.newLine();
							}
							writer.flush();
							writer.close();
						}
					} while (writer == null);
					writer = null;
				}
			} catch (IOException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			}
		}

	}

	public void write(String funcSpecific)
	{
		if (Util.checkMemFree())
			this.funcSpecific = new String(funcSpecific);
		else
		{
			this.funcSpecific = "";
			BufferedWriter writer = null;
			try
			{
				if (tmpFile == null)
					tmpFile = Util.createTempFile();
				else
				{
					RandomAccessFile randomAccessFile = new RandomAccessFile(
							tmpFile, "rw");
					randomAccessFile.setLength(0);
					randomAccessFile.close();
				}

				do
				{
					synchronized (tmpFile)
					{

						writer = new BufferedWriter(
								new FileWriter(tmpFile, false));
						writer.write(funcSpecific);
						writer.newLine();
						writer.close();
					}
				} while (writer == null);
				writer = null;
			} catch (IOException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			}

		}
	}
	public void write(String funcSpecific, boolean reset)
	{
		if (funcSpecific == null)
		{
			reset = true;
			funcSpecific = "";
		}
		if (reset && tmpFile != null)
		{
			try
			{
//				this is done due to problems with FileWriter not clearing the
				// file and effectively appending content when it shouldn't
				RandomAccessFile randomAccessFile = new RandomAccessFile(
						tmpFile, "rw");
				randomAccessFile.setLength(0);
				randomAccessFile.close();
			} catch (FileNotFoundException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			} catch (IOException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			}
		}
		if (Util.checkMemFree() && tmpFile == null)
		{
			if (reset || this.funcSpecific == null)
				this.funcSpecific = new String(funcSpecific);
			else
				this.funcSpecific = this.funcSpecific
						.concat("\n" + funcSpecific);
		} else
			try
			{
				if (tmpFile == null || !tmpFile.exists())
					tmpFile = Util.createTempFile();
				if (this.funcSpecific != null)
				{
					funcSpecific = this.funcSpecific
							.concat("\n" + funcSpecific);
					this.funcSpecific = null;
				}
				if (tmpFile.exists())
				{

					BufferedWriter writer;
					do
					{
						synchronized (tmpFile)
						{
							writer = new BufferedWriter(
									new FileWriter(tmpFile, !reset));

							writer.append(funcSpecific);
							writer.newLine();
							writer.flush();
							writer.close();
						}
					} while (writer == null);
					writer = null;
				}
			} catch (IOException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			}

	}

}
