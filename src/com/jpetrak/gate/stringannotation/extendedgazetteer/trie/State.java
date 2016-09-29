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
package com.jpetrak.gate.stringannotation.extendedgazetteer.trie;


public abstract class State extends com.jpetrak.gate.stringannotation.extendedgazetteer.State {
  public static int nrNodes = 0;
  public static int mapNodes = 0;
  public static int charNodes = 0;
  public static int finalNodes = 0;
  public static int nrChars = 0;
  public static int nrInput = 0;
  
  static StoreCharMapBase store = null;
  
  public abstract void put(char key, State value);
  

  protected int lookupIndex = -1;
  public int getLookupIndex() {
    return lookupIndex;
  }

  
  public void addLookup(int index) {
    lookupIndex = index;
  }
  
  public boolean isFinal() {
    return lookupIndex >= 0;
  }    
  
  public abstract State next(char chr);
  
  public abstract void replace(char key, State newState, State oldState);
}
