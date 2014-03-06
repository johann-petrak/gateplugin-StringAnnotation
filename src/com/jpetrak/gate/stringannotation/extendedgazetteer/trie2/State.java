package com.jpetrak.gate.stringannotation.extendedgazetteer2.trie2;

import java.util.ArrayList;
import java.util.Collection;

import com.jpetrak.gate.stringannotation.extendedgazetteer2.Lookup;

public abstract class State extends com.jpetrak.gate.stringannotation.extendedgazetteer2.State {
  public static int nrNodes = 0;
  public static int mapNodes = 0;
  public static int charNodes = 0;
  public static int finalNodes = 0;
  public static int nrChars = 0;
  public static int nrInput = 0;
  
  public abstract State put(char key, State value);
  

  protected int lookupIndex = -1;


  
  public void addLookup(int index) {
    lookupIndex = index;
  }
  
  public boolean isFinal() {
    return lookupIndex >= 0;
  }    
  
  public abstract State next(char chr);
  
  public abstract State replace(char key, State newState, State oldState);
}
