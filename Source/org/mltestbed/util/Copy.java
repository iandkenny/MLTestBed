package org.mltestbed.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Copy
{
	/**
	 * @param sourceObject
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static <T> T deepCopy(final T sourceObject) throws IOException,
			ClassNotFoundException

	{
		T result = null;
		ObjectInputStream in = null;
		try
		{

			// serialize
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(sourceObject);

			out.flush();
			out.close();

			// de-serialize
			ByteArrayInputStream bin = new ByteArrayInputStream(
					bos.toByteArray());
			in = new ObjectInputStream(bin);

			result = ((T) in.readObject());
		} catch (IOException cnfe)
		{
			throw new IOException(cnfe);
		} finally
		{
			if(in != null)
			in.close();
		}
		return result;
	}
}