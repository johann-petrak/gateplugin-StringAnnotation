/*
 *  OurFSMState.java
 *  
 */

package com.jpetrak.gate.stringannotation.extendedgazetteer3;

import java.util.ArrayList;
import java.util.Arrays;

/** 
 * 
 *
 */
public class OurFSMState  {


  public static int nrNodes = 0;
  public static int nrChars = 0;
  /** Constructs a new OurFSMState object and adds it to the list of
   * states of the owner.
   *
   * @param owner 
   */
  public OurFSMState() {
    nrNodes++;
  }


  /** This method is used to access the transition function of this state.
   */
  public OurFSMState next(char chr) {
    return (OurFSMState)get(chr);
  }


  /** Checks whether this state is a final one
   */
  public boolean isFinal() {
    if (lookupSet==null)
        return false;
    return !lookupSet.isEmpty();
  }

  /** Returns a set of {@link Lookup} objects describing the types of lookups
   * the phrase for which this state is the final one belongs to
   */
  public Iterable<Lookup> getLookups() {
    return lookupSet;
  }

  /** Adds a new looup description to this state's lookup descriptions set
   */
  public void addLookup(Lookup lookup) {
    if (lookupSet == null)
        lookupSet = new ArrayList(4);
    lookupSet.add(lookup);
  } // addLookup


  protected ArrayList lookupSet;

  char[] itemsKeys = null;
  OurFSMState[] itemsObjs = null;

  /**
   * resize the containers by one, leaving empty element at position 'index'
   */
  void resize(int index)
  {
      int newsz = itemsKeys.length + 1;
      nrChars++;
      char[] tempKeys = new char[newsz];
      OurFSMState[] tempObjs = new OurFSMState[newsz];
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
  Object get(char key)
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
  public OurFSMState put(char key, OurFSMState value)
  {
      if (itemsKeys == null)
      {
          itemsKeys = new char[1];
          nrChars++;
          itemsKeys[0] = key;
          itemsObjs = new OurFSMState[1];
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


} // class OurFSMState
