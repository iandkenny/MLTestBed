package org.mltestbed.ui;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import org.mltestbed.util.Log;



public class GetResource {

  protected String name;
  
  protected URL url;
  
  protected File file;
  
  public GetResource(String name) {
    this.name = name;
    findResource();
  }
  
  public void findResource() {
    //first see if the resource is a plain file
    File f = new File(name);
    if(f.exists()) {
      file = f;
      try {
        url = f.toURI().toURL();
      } catch (MalformedURLException e) {
        Log.log(Level.SEVERE, new Exception(new Exception("Could not create URL from path: "+f+"\n ",e)));
      }
      return;
    }
    
    //search for the resource on the classpath
    
    //get the default class/resource loader 
    ClassLoader cl = getClass().getClassLoader();
    url = cl.getResource(name);
    if(url != null) {
      file = new File( url.getFile() );
    }
  }
  
  public String toString() {
    String str = "Resource name:\t"+name+"\n";
    str += "File:\t\t"+file+"\n";
    str += "URL:\t\t"+url+"\n";
    return str;
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    if(args == null || args.length != 1) {
      System.out.println("Usage: java GetResource <name>");
      System.exit(1);
    }

    GetResource gr = new GetResource(args[0]);
    System.out.println(gr);
  }

}  
 
  
 
