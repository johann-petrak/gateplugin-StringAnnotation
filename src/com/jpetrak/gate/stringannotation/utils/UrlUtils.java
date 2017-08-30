/*
 * Copyright (c) 2010- Austrian Research Institute for Artificial Intelligence (OFAI). 
 * Copyright (C) 2014-2016 The University of Sheffield.
 *
 * This file is part of gateplugin-StringAnnotation
 * (see https://github.com/johann-petrak/gateplugin-StringAnnotation)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jpetrak.gate.stringannotation.utils;

import gate.util.GateRuntimeException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Class with utility methods for handling URLs.
 * 
 * @author Johann Petrak
 */
public class UrlUtils {
  
  /**
   * Create a URL from the String.
   * If the String does not have a protocol/scheme, file:// is prepended.
   * @param str
   * @return 
   */
  public static URL newURL(String str) {
    try {
      if(new URI(str).getScheme() == null) {
        str = "file://"+str;
      }
      return new URL(str);
    } catch (Exception ex) {
      throw new GateRuntimeException("Cannot create URL from string "+str,ex);
    }
  }
  
  /**
   * Equivalent of creating a new file from a directory file and name string.
   * 
   * This tries to roughly provide the equivalent of "new File(dirFile, nameString)"
   * for a URL and a name String. The catch is that in order for this to work,
   * the given dir URL must end in a slash. This method adds the slash if necessary
   * and returns the URL of the combined paths.
   * 
   * @param dir
   * @param file 
   */
  public static URL newURL(URL dirURL, String fileName) {
    String s = dirURL.toExternalForm();
    if(!s.endsWith("/")) {
      try {
        dirURL = new URL(s+"/");
      } catch (MalformedURLException ex) {
        throw new GateRuntimeException("Could not create URL for "+s+"/",ex);
      }
    }
    try {
      URL ret = new URL(dirURL,fileName);
      return ret;
    } catch (MalformedURLException ex) {
      throw new GateRuntimeException("Could not create URL for "+dirURL+"fileName");
    }    
  }
  
  /**
   * Return the last path component of a hierarchical path of URL.
   * This assumes a URL which ends in a hierarchical path, without a query 
   * or anchor or anything else following the path.
   * If there is nothing that looks like it could be the last path component,
   * the empty string is returned.
   * NOTE: if the URL ends with a slash, then the last component without a trailing
   * slash is returned.
   * <p>
   * This is made intentionally to work similar to the someFile.getName() method.
   * @param url
   * @return 
   */
  public static String getName(URL url) {
    String p;
    try {
      p = url.toURI().getPath();
    } catch (URISyntaxException ex) {
      throw new GateRuntimeException("Cannot convert URL to URI: "+url,ex);
    }
    // remove any trailing slash
    if(p.endsWith("/")) {
      p=p.substring(0,p.length()-1);
    }
    int i = p.lastIndexOf("/");
    if(i<0) return "";  // no slash at all, we do not have a path 
    return p.substring(i+1);
  }
  
  /**
   * Return the parent path for a URL.
   * This is the path, with the last component of the path removed, i.e.
   * with that part removed that is returned by the getName() method.
   * <p>
   * This returns null if the path is a slash or something without a slash.
   * 
   */
  public static String getParent(URL url) {
    String p;
    try {
      p = url.toURI().getPath();
    } catch (URISyntaxException ex) {
      throw new GateRuntimeException("Cannot convert URL to URI: "+url,ex);
    }
    if(p.equals("/")) return null;
    // NOTE: we ignore odd cases where the path is just several slashes in succession
    
    // .. not just a lone slash, remove any trailing slash
    if(p.endsWith("/")) {
      p=p.substring(0,p.length()-1);
    }    
    int i = p.lastIndexOf("/");
    if(i<0) return null;  // no slash at all
    
    // we found the slash that separates the last name part from the rest
    // the parent is whatever comes before that
    return p.substring(0,i);    
  }
  
  
  /** 
   * Returns true if the URL can be opened for reading.
   * 
   * @param url
   * @return 
   */
  public static boolean exists(URL url) {
    boolean ret = true;
    try (InputStream is = url.openStream()) {
      // do nothing, we only want to check the opening
    } catch (IOException ex) {
      ret = false;
    }
    return ret;
  }
  
  /**
   * Return truen if the URL is a file URL.
   * @param url
   * @return 
   */
  public static boolean isFile(URL url) {
    return "file".equals(url.getProtocol());
  }
  
  
}
