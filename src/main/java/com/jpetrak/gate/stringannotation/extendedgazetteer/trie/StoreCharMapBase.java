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

import java.io.Serializable;

/**
 * The base class of the implementations for the two stages of storing 
 * CharMaps: first stage stores the whole charmap in a single char[] but each 
 * char[] chunk is stored as a separate object. Second stage stores all char[]
 * chunks in a single char[] array.
 * 
 * @author Johann Petrak
 *
 */
public abstract class StoreCharMapBase implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -1162248412493478826L;
  /**
   * Adds a new state for the given char to the char map. If mapIndex less than zero,
   * then the charmap will be created and its new index returned, otherwise, the charmap
   * at the given index will be used and the given index returned back. 
   * @param mapIndex
   * @param key
   * @param value
   * @return
   */
  //public abstract int putOld(int mapIndex, char key, State state);
  public abstract int put(int mapIndex, char key, int state);
  //public abstract void replaceOld(int mapIndex, char key, State newState, State oldState);
  public abstract void replace(int mapIndex, char key, int newState, int oldState);
  /**
   * Return the state index.
   * @param mapIndex
   * @param chr
   * @return
   */
  //public abstract State nextOld(int mapIndex, char chr);
  public abstract int next(int mapIndex, char chr);
}
