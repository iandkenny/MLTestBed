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
	private String buffer;
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
		buffer = null;
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

			if (tmpFile != null && this.buffer.isEmpty())
				synchronized (tmpFile)
				{
					if (tmpFile.exists())
						reader = new BufferedReader(new FileReader(tmpFile));
				}
			else
				reader = new BufferedReader(new StringReader(this.buffer));

		} catch (IOException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

		return reader;
	}

	/**
	 * @return the buffer
	 */
	public String read()
	{
		String str = "";
		if (tmpFile != null && this.buffer.isEmpty())
		{
			StringBuilder buf = new StringBuilder("");
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
								buf.append(line);

						reader.close();
					}
					reader = null;
				}
				str += buf.toString();
			} catch (IOException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			}
		} else
			str += this.buffer;
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
				buf = new String(buffer);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}
		return buf;
	}

	public void write(BufferedReader buffer)
	{
		if (buffer != null)
		{
			try
			{
				if (Util.checkMemFree())
				{
					String line;
					StringBuilder str = new StringBuilder();
					if (buffer != null)
						while ((line = buffer.readLine()) != null)
							str.append(line);
					this.buffer = str.toString();
				} else
				{
					this.buffer = "";
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
							while ((line = buffer.readLine()) != null)
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

	public void write(String str)
	{
		try
		{
			if (Util.checkMemFree())
			{
				this.buffer = new String(str);
				if (tmpFile != null)
				{
					RandomAccessFile randomAccessFile = new RandomAccessFile(
							tmpFile, "rw");
					randomAccessFile.setLength(0);
					randomAccessFile.close();
				}
			} else
			{
				this.buffer = "";
				BufferedWriter writer = null;

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
						writer.write(str);
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
			if (reset || this.buffer == null)
				this.buffer = new String(funcSpecific);
			else
				this.buffer = this.buffer.concat("\n" + funcSpecific);
		} else
			try
			{
				if (tmpFile == null || !tmpFile.exists())
					tmpFile = Util.createTempFile();
				if (this.buffer != null)
				{
					funcSpecific = this.buffer.concat("\n" + funcSpecific);
					this.buffer = null;
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
