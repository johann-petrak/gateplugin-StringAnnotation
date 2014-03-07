/*
 *  CharMapState.java
 *  
 */

package com.jpetrak.gate.stringannotation.extendedgazetteer.trie2;

import gate.util.GateRuntimeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.jpetrak.gate.stringannotation.extendedgazetteer.Lookup;

/** 
 * 
 *
 */
public class CharMapState extends State {


  /** Constructs a new CharMapState object and adds it to the list of
   * states of the owner.
   *
   * @param owner 
   */
  public CharMapState() {
    nrNodes++;
    mapNodes++;
  }
  
  public CharMapState(SingleCharState state) {
    // do not increment the nodes, because eventually this node will
    // just replace the old SingleCharState state!
    // Just copy over the stuff
    this.lookupIndex = state.lookupIndex;
    put(state.key,state.next);
    mapNodes++;
    charNodes--;
    // we must not count the char we copied over twice!
    nrChars--;
  }


  /** This method is used to access the transition function of this state.
   */
  public State next(char chr) {
    return (State)get(chr);
  }



  char[] itemsKeys = null;
  State[] itemsObjs = null;

  /**
   * resize the containers by one, leaving empty element at position 'index'
   */
  void resize(int index)
  {
      int newsz = itemsKeys.length + 1;
      nrChars++;
      char[] tempKeys = new char[newsz];
      State[] tempObjs = new State[newsz];
      System.arraycopy(itemsKeys, 0, tempKeys, 0, index);
      System.arraycopy(itemsObjs, 0, tempObjs, 0, index);
      System.arraycopy(itemsKeys, index, tempKeys, index + 1, newsz - index - 1);
      System.arraycopy(itemsObjs, index, tempObjs, index + 1, newsz - index - 1);

      itemsKeys = tempKeys;
      itemsObjs = tempObjs;
  } // resize

/**
* get the object from the map using the char key
*/
  State get(char key)
  {
      if (itemsKeys == null) return null;
      int index = Arrays.binarySearch(itemsKeys, key);
      if (index<0)
          return null;
      return itemsObjs[index];
  }
/**
* put the object into the char map using the char as the key
*/
  public State put(char key, State value)
  {
      if (itemsKeys == null)
      {
          itemsKeys = new char[1];
          nrChars++;
          itemsKeys[0] = key;
          itemsObjs = new State[1];
          itemsObjs[0] = value;
          return value;
      }// if first time
      int index = Arrays.binarySearch(itemsKeys, key);
      if (index<0)
      {
          index = ~index;
          resize(index);
          itemsKeys[index] = key;
          itemsObjs[index] = value;
      }
      return itemsObjs[index];
  } // put

  public State replace(char key, State newState, State oldState) {
    int index = Arrays.binarySearch(itemsKeys, key);
    if(index<0) {
      throw new GateRuntimeException("CharMapState: should have key but not found: "+key);
    }
    if(itemsObjs[index] != oldState) {
      throw new GateRuntimeException("CharMapState: old states differ!");
    }
    itemsObjs[index] = newState;
    return newState;
  }
  
  

} // class CharMapState
