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
   * If the String does not have a protocol/scheme, file:// is assumed and prepended.
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
   * the given dir URL must end in a slash. This method assumes that the first
   * URL always refers to a directory and therefore appends a slash if necessary.
   * <p>
   * Note: if the dirURL contains a query and/or a fragment, those parts are 
   * lost in the resulting URL.
   * @param dir
   * @param file 
   */
  public static URL newURL(URL dirURL, String fileName) {
    URI dirURI;
    try {
      dirURI = dirURL.toURI();
    } catch (URISyntaxException ex) {
      throw new GateRuntimeException("Cannot convert URL to URI: "+dirURL);
    }
    String path = dirURI.getPath();
    if(!path.endsWith("/")) path = path+"/";
    try {
      dirURI = new URI(
              dirURI.getScheme(),
              dirURI.getUserInfo(),
              dirURI.getHost(),
              dirURI.getPort(),
              path,
              dirURI.getQuery(),
              dirURI.getFragment());
    } catch (URISyntaxException ex) {
      throw new GateRuntimeException("Cannot conver URL to URI: "+dirURL);
    }
    try {
      URL ret = new URL(dirURI.toURL(),fileName);
      return ret;
    } catch (MalformedURLException ex) {
      throw new GateRuntimeException("Could not create URL for "+dirURL+"fileName");
    }    
  }
  
  /**
   * Return the last path component of a hierarchical path of URL.
   * @param url
   * @return 
   */
  public static String getName(URL url) {
    URI uri;
    try {
      uri = url.toURI();
    } catch (URISyntaxException ex) {
      throw new GateRuntimeException("Cannot convert URL to URI: "+url);
    }
    URL parentURL;
    try {
      parentURL = new URL(url,".");
    } catch (MalformedURLException ex) {
      throw new GateRuntimeException("Cannot find parent URL for URL "+url);
    }
    URI parentURI = null;
    try {
      parentURI = parentURL.toURI();
    } catch (URISyntaxException ex) {
      throw new GateRuntimeException("Cannot convert URL to URI: "+parentURL);
    }
    URI relative = parentURI.relativize(uri);
    return relative.toString();
  }
  
  /**
   * Return the parent path for a URL.
   * This is the path, with the last component of the path removed, i.e.
   * with that part removed that is returned by the getName() method.
   * 
   */
  public static URL getParentURL(URL url) {
    URL ret;
    try {
      ret = new URL(url,".");
    } catch (MalformedURLException ex) {
      throw new GateRuntimeException("Cannot convert URL to URI: "+url);
    }
    return ret;
  }
  
  /**
   * Return the parent path for a URL.
   * This is the path, with the last component of the path removed, i.e.
   * with that part removed that is returned by the getName() method.
   * 
   */
  public static String getParent(URL url) {
    return getParentURL(url).toString();
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
