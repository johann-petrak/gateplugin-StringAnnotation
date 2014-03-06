package com.jpetrak.gate.stringannotation.extendedgazetteer2;

import java.util.List;

/**
 * Store information about a successful match.
 * 
 * @author Johann Petrak
 *
 */
public abstract class Match {  
  public abstract List<Lookup> getLookups();
}
