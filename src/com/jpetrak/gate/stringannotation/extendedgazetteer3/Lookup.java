package com.jpetrak.gate.stringannotation.extendedgazetteer3;


/**
 * Used to describe a type of lookup annotations. A lookup is described by a
 * major type a minor type and a list of languages. Added members are :
 * ontologyClass and list. All these values are strings (the list of languages
 * is a string and it is intended to represesnt a comma separated list). An
 * optional features field stores arbitary features as part of the lookup
 * annotation. This can be used to set meta-data for a gazetteer entry.
 */
public class Lookup {

  /**
   */
  public Lookup(int  listInfoIndex, String[] entryFeatures) {
    this.listInfoIndex = listInfoIndex;
    this.entryFeatures = entryFeatures;
  }


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
    s.append(listInfoIndex);
    return(s.toString());
    //return (entryFeatures == null ? "NULL" : entryFeatures.toString())+listInfoIndex;
  }

  /**
   * Two lookups are equal if they have the same string representation (major
   * type and minor type).
   * 
   * @param obj
   */
  public boolean equals(Object obj) {
    if(obj instanceof Lookup)
      return obj.toString().equals(toString());
    else return false;
  } // equals

  /**
   * *
   */
  public int hashCode() {
    return toString().hashCode();
  }
} // Lookup
