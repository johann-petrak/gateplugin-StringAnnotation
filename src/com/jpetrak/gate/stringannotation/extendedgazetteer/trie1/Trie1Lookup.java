package com.jpetrak.gate.stringannotation.extendedgazetteer2.trie1;

import com.jpetrak.gate.stringannotation.extendedgazetteer2.Lookup;

// TODO: make this just an interface or abstract class, with the actual
// implementation a subclass in one of the implementation packages!

/**
 */
public class Trie1Lookup extends Lookup {

  /**
   */
  public Trie1Lookup(int  listInfoIndex, String[] entryFeatures) {
    this.listInfoIndex = listInfoIndex;
    this.entryFeatures = entryFeatures;
  }
  
  
  private Trie1Lookup() {}

  public int listInfoIndex = -1;
  public String[] entryFeatures = null;

  
  
  public String toString() {
    StringBuilder s = new StringBuilder();
    if(entryFeatures == null) {
      s.append("NULL");
    } else {
      for(int i = 0; i < entryFeatures.length; i++) {
        s.append(" ");
        s.append(entryFeatures[i]);
      }
    }
    s.append(" i=");
    s.append(listInfoIndex);
    return(s.toString());
    //return (entryFeatures == null ? "NULL" : entryFeatures.toString())+listInfoIndex;
  }

  /**
   * Two lookups are equal if they are booth Trie1Lookup instances and have the same string representation 
   * 
   * @param obj
   */
  public boolean equals(Object obj) {
    if(obj instanceof Trie1Lookup)
      return obj.toString().equals(toString());
    else return false;
  } // equals

  /**
   * *
   */
  public int hashCode() {
    return toString().hashCode();
  }
} // Trie1Lookup
