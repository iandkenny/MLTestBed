package org.mltestbed.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;

public class ReadTar
{
	public static InputStream getInputStream(String tarFileName) throws Exception{
	      if(tarFileName.substring(tarFileName.lastIndexOf(".") + 1, tarFileName.lastIndexOf(".") + 3).equalsIgnoreCase("gz")){
	         System.out.println("Creating an GZIPInputStream for the file");
	         return new GZIPInputStream(new FileInputStream(new File(tarFileName)));
	      }else{
	         System.out.println("Creating an InputStream for the file");
	         return new FileInputStream(new File(tarFileName));
	      }
	   }
	 
	   public static void readTar(InputStream in, String untarDir) throws IOException{
	      System.out.println("Reading TarInputStream... (using classes from http://www.trustice.com/java/tar/)");
	      TarInputStream tin = new TarInputStream(in);
	      TarEntry tarEntry = tin.getNextEntry();
	      if(new File(untarDir).exists()){
		      while (tarEntry != null){
		         File destPath = new File(untarDir + File.separatorChar + tarEntry.getName());
//		         System.out.println("Processing " + destPath.getAbsoluteFile());
		         if(!tarEntry.isDirectory()){
		            FileOutputStream fout = new FileOutputStream(destPath);
		            tin.copyEntryContents(fout);
		            fout.close();
		         }else{
		            destPath.mkdir();
		         }
		         tarEntry = tin.getNextEntry();
		      }
		      tin.close();
	      }else{
	         System.out.println("That destination directory doesn't exist! " + untarDir);
	      }
	   }


}
