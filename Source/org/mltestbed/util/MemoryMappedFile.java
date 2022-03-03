package org.mltestbed.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;

public class MemoryMappedFile
{
	static int length = 0x8FFFFFF;
	private RandomAccessFile randomFile;
	private MappedByteBuffer out;
	private MappedByteBuffer in;
	private File file;
	private StringBuilder buffer = new StringBuilder();

	/**
	 * 
	 */
	public MemoryMappedFile()
	{
		super();
		try
		{
			file = Util.createTempFile();
			randomFile = new RandomAccessFile(file, "rw");
			out = randomFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0,
					length);
			in = randomFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0,
					randomFile.getChannel().size());
		} catch (IOException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

	}
	public MemoryMappedFile(File file)
	{
		super();
		try
		{
			if (file == null)
				file = Util.createTempFile();
			this.file = file;
			randomFile = new RandomAccessFile(file, "rw");
			out = randomFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0,
					length);
			in = randomFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0,
					randomFile.getChannel().size());

		} catch (IOException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}

	}

	public MemoryMappedFile(String buffer)
	{
		super();
		if (!Util.checkMemFree())
		{
			try
			{
				if (file == null)
					file = Util.createTempFile();
				randomFile = new RandomAccessFile(file, "rw");
				out = randomFile.getChannel()
						.map(FileChannel.MapMode.READ_WRITE, 0, length);
				in = randomFile.getChannel().map(FileChannel.MapMode.READ_ONLY,
						0, randomFile.getChannel().size());
				write(buffer);
			} catch (IOException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			}

		} else
			this.buffer = new StringBuilder(buffer);
	}
	public void destroy()
	{
		if (file != null)
			file.delete();
		in = null;
		out = null;
		randomFile = null;
		file = null;
	}
	@Override
	protected void finalize() throws Throwable
	{
		destroy();
		super.finalize();
	}
	public void reset()
	{
		try
		{
			out.clear();
			in.clear();
			randomFile.setLength(0);
		} catch (IOException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}
	}
	public String read()
	{
		String str;
		if (buffer.length() == 0)
		{
			str = "";
			// the buffer now reads the file as if it were loaded in memory.
			// System.out.println(in.isLoaded()); // prints false
			// System.out.println(in.capacity()); // Get the size based on
			// content
			// size
			// of file
			// You can read the file from
			// this buffer the way you like.
			for (int i = 0; i < in.limit(); i++)
			{
				str += in.get(i); // Get the contents of file
			}
		} else
			str = buffer.toString();
		return str.trim();
	}

	public void write(String str)
	{
		if (str != null)
		{
			if (buffer.length() == 0)

				out.put(str.getBytes());
			else
				buffer = new StringBuilder(
						(str == null || str == "") ? " " : str);// can't
			// allow
			// null
			// or
			// empty
			// string
//			System.out.println("Finished writing: "+str);
		}

		else
			buffer = new StringBuilder(" ");

	}
}
