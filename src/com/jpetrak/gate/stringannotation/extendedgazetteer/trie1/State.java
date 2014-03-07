package com.jpetrak.gate.stringannotation.extendedgazetteer.trie1;

import java.util.ArrayList;
import java.util.Collection;

import com.jpetrak.gate.stringannotation.extendedgazetteer.Lookup;

public abstract class State  extends com.jpetrak.gate.stringannotation.extendedgazetteer.State {
  public static int nrNodes = 0;
  public static int mapNodes = 0;
  public static int charNodes = 0;
  public static int finalNodes = 0;
  public static int nrChars = 0;
  public static int nrInput = 0;
  
  public abstract State put(char key, State value);
  

  // At least for now, we only have states that contain this field, so
  // we keep it at the root class
  protected ArrayList<Lookup> lookupSet;


  
  // TODO: the a common implementation for adding a lookup: 
  // check if the lookup has already been added!
  // Depending on a parameter, either of two approaches:
  // = all relevant details must match for something to not get included,
  //   i.e. listinfo index same and all the entry-features too
  // = only entry features must match, the listinfo may be different:
  //   that way only the first entry from any list gets stored
  // Entry-features match iff: 
  // - the same keys are there and
  // - all values match for every key
  // NEED some fast method to do this
  // - if entry features = missing (null or -1)
  // => add
  // otherwise create internal rep. of features,
  // then compare internal rep with existing
  // internal rep:
  // ultimately this is a list of key/value
  // pairs where a key is an index and the value is 
  // a string, ordered by increasing key index.
  // So a fast match is possible by 
  // - if not same number of features => add
  // - go through features in sequence
  //   = if key index differ => add
  //   = if value differ =>
  public void addLookup(Lookup lookup) {
    if (lookupSet == null) {
      lookupSet = new ArrayList(4);
      lookupSet.add(lookup);
    } else {
      // before adding the lookup, check if it is already there!
      // TODO: this is a very simple and not optimal approach at the moment:
      // * If the entry comes from different lists but there are no list specific features or
      // they are all the same, the entry is still considered different, but should be considered equal
      // * If the entry-specific features are the same but appear in a different order in
      // the gazetteer line, this is considered different but should be considered equal
      // * using the string representation may be slow and put a lot of temporary garbage on the heap
      for(Lookup existing : lookupSet) {
        if(existing.equals(lookup)) {
          return;
        }
      }
      lookupSet.add(lookup);
    }
  }
  
  public Collection<Lookup> getLookups() {
    return lookupSet;
  }
  public boolean isFinal() {
    if (lookupSet==null)
      return false;
    return !lookupSet.isEmpty();
  }    
  
  public abstract State next(char chr);
  
  public abstract State replace(char key, State newState, State oldState);
}
