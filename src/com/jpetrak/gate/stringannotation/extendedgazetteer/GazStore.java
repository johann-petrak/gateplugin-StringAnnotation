/*
 * Copyright (c) 2010- Austrian Research Institute for Artificial Intelligence (OFAI). 
 * Copyright (C) 2014-2016 The University of Sheffield.
 *
 * This file is part of gateplugin-ModularPipelines
 * (see https://github.com/johann-petrak/gateplugin-ModularPipelines)
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
package com.jpetrak.gate.stringannotation.extendedgazetteer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.io.Serializable;


import gate.FeatureMap;
import java.net.URL;

/**
 * A GazStore represents a loaded gazetteer plus all the methods for loading, caching,
 * and using it.
 * Each actual implementation is a subclass of this class.
 * 
 * @author Johann Petrak
 *
 */
public abstract class GazStore implements Serializable {

  
  public abstract void addLookup(String entry, int lookupInfoIndex, String[] keyvalues);
      
  /**
   * Return something that can be used to match a string against the GazStore
   * @return
   */
  public abstract Visitor getVisitor();


  public abstract int addListInfo(String annotationType, String string,
      FeatureMap listFeatures);
  
  public abstract Iterator<Lookup> getLookups(State matchingState);
  
  public abstract ListInfo getListInfo(Lookup lookup);
  public abstract int getListInfoIndex(Lookup lookup);
  
  public abstract void addLookupListFeatures(FeatureMap fm, Lookup lookup);
  
  public abstract void addLookupEntryFeatures(FeatureMap fm, Lookup lookup);
  public abstract String getLookupType(Lookup lookup);
  
  public abstract String statsString();
  
  
  public abstract Collection<ListInfo> getListInfos();

  /**
   * Match the given string and return the set of Lookups if there is a match.
   * This returns NULL if no match is found in order to distinguish from a match
   * that has an empty set of lookups.
   * 
   * @param toMatch
   * @return
   */
  public Iterator<Lookup> match(String toMatch) {
    State currentState = getInitialState();
    for (int i = 0; i < toMatch.length(); i++) {
      char currentChar = toMatch.charAt(i);
      currentState = currentState.next(currentChar);
      if (currentState == null) {
        break;
      }
      if (i==(toMatch.length()-1) && currentState.isFinal()) {  // we are at the last character
        return getLookups(currentState);
      }
    }
    return null;
  }
  
  // TODO: as long as Visitor is not implemented, we allow to get the initial state like this:
  public abstract State getInitialState();
  
  public abstract void compact();
  
  public abstract void save(File whereTo) throws FileNotFoundException, IOException;

  public abstract GazStore load(URL whereFrom) throws IOException;
  
  public int refcount = 0;
  
}
