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

/**
 * A visitor for some GazStore implementation. The actual visitor returned by 
 * a concrete GazStore implementation is a subclass of this class.
 * 
 * @author Johann Petrak
 *
 */
public abstract class Visitor {
  /**
   * Reset the visitor object for a new match attempt.
   */
  public abstract void reset();
  
  /**
   * Try to match another character and return if the match was successful or failed.
   * @param c
   * @return
   */
  public abstract boolean match(char c);
  
  /**
   * Check if the current matched state is a final state and thus represents a match
   * of the current prefix.
   * @return
   */
  public abstract boolean isFinal();
  
  // TODO: how to best represent the list of "Lookups" in a generic way?
  // If the state matches and is final, there will be a payload that is a set/list
  // of (gazetteerListIndex,entryFeatures).
  
  /**
   * Return an object that represents a matching state. That way, a client can 
   * remember several matching situations before starting to act on one of them.
   * @return
   */
  public abstract Match getMatch();
  
}
