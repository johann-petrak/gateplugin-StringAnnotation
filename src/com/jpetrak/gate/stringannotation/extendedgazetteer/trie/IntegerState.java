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


/**
 * A lightweight wrapper to make the integer-index based approach to states compatible
 * with the old object-based approach.
 * Once/if we abandon all old object-based implementations, this can be removed and 
 * StoreState used directly to avoid some obejct creation overhead.
 * @author Johann Petrak
 *
 */
public class IntegerState extends State {

  StoreStates store = null;
  int index = -1;
  
  public IntegerState(StoreStates store, int index) {
    this.store = store;
    this.index = index;
  }
  
  @Override
  public boolean isFinal() {
    return store.isFinal(this.index);
  }

  @Override
  public State next(char c) {
    int next = store.next(index,c);
    if(next < 0) {
      return null;
    } else {
      return new IntegerState(store,next);
    }
  }

  @Override
  public void put(char key, State value) {
    IntegerState st = (IntegerState)value;
    store.put(index, key, st.index);
  }

  @Override
  public void replace(char key, State newState, State oldState) {
    //
  }
  
  @Override
  public int getLookupIndex() {
    return store.getLookupIndex(index);
  }

  public String toString() {
    return "IntegerState:"+index+"("+store.toString(index)+")";
  }
  
}
