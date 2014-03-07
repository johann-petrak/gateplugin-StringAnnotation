package com.jpetrak.gate.stringannotation.extendedgazetteer.trie1;

import gate.FeatureMap;
import gate.util.GateRuntimeException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.jpetrak.gate.stringannotation.extendedgazetteer.GazStore;
import com.jpetrak.gate.stringannotation.extendedgazetteer.ListInfo;
import com.jpetrak.gate.stringannotation.extendedgazetteer.Lookup;
import com.jpetrak.gate.stringannotation.extendedgazetteer.Visitor;


public class GazStoreTrie1 extends GazStore {
  
  static final boolean useChars = true;
  
  public GazStoreTrie1() {
    //System.out.println("DEBUG: Creating a GazStoreTrie1!!");    
  }
  
  public void // com.jpetrak.gate.stringannotation.extendedgazetteer2.State 
    addLookup(String text, int infoIndex, String[] keyvals) {
    char currentChar;
    State currentState = initialState;
    State nextState;
    State lastState = null;
    char lastChar = 0xffff;

    //System.out.println("Adding "+text+"|"+lookup);
    
    for(int i = 0; i< text.length(); i++) {
      State.nrInput++;
      currentChar = text.charAt(i);
      if(currentChar == 0) {
        throw new GateRuntimeException("Cannot add a gazetteer entry that contains a binary 0 character!");
      }
      nextState = currentState.next(currentChar);
      if(nextState == null) {
        // TODO: if we get here, the current state could not find a successor
        // state for the given character. If the current state is a 
        // single character state and it is still unused,
        // then we can keep it and just add the character. if it 
        // is a single character state but already used, we must 
        // replace it with a charmap state.
        // TODO: at first create a new single char state here!
        // this will initialize the state as "unused" by setting its
        // character to 0
        
        if(useChars) {
          nextState = new SingleCharState();          
        } else {
          nextState = new CharMapState();                  
        }
        
        // TODO: first check if the current state is a single char state.
        // if yes, convert to a charmap state if required and update the link from where
        // we reach the current state!
        if(useChars) {
        if(currentState instanceof SingleCharState) {
          // if there is already something stored in that state we cannot put 
          // another char, so we need to replace this node with a charmap node
          if(((SingleCharState)currentState).key != 0) {
           //System.out.println("Trying tp replace with charmap for "+currentChar+" entry "+text+" lastChar="+lastChar+" lastState="+lastState);
           State oldState = currentState;
           currentState = new CharMapState((SingleCharState)currentState);
           // this should just replace what is already there for the lastChar anyways!!
           // lastChar should always be set here, because the root is initialized to be a CharMapState
           assert(lastChar != 0xFFFF);
           lastState.replace(lastChar,currentState,oldState);
          }
        }
        }
        // now the current state is either a CharMapState or an empty 
       // SingleCharState
        currentState.put(currentChar, nextState);
        // TODO: that loop should not be necessary anymore since we always
        // match normalized text where we get at most one space!
        // if(currentChar == ' ') nextState.put(' ',nextState);
      }
      lastChar = currentChar;
      lastState = currentState;
      currentState = nextState;
    } //for(int i = 0; i< text.length(); i++)

    // TODO: either here or inside the state.addLookup code, we should 
    // check if the lookup has already been added!
    // Depending on a parameter, either of two approaches:
    // = all relevant details must match for something to not get included,
    //   i.e. listinfo index same and all the entry-features too
    // = only entry features must match, the listinfo may be different:
    //   that way only the first entry from any list gets stored
    // Entry-features match iff: 
    // - the same keys are there and
    // - all values match for every key
    currentState.addLookup(new Trie1Lookup(infoIndex, keyvals));
    // return currentState;
    //System.out.println("text=>"+text + "<, " + lookup.majorType + "|" + lookup.minorType);

  } // addLookup

  public CharMapState initialState = new CharMapState();

  protected ArrayList<ListInfo> listInfos = new ArrayList<ListInfo>();  
  
  public String getListAnnotationType(int index) {
    return listInfos.get(index).getAnnotationType();
  }
  
  public FeatureMap getListFeatures(int index) {
    return listInfos.get(index).getFeatures();
  }
  
  @Override
  public int addListInfo(String type, String source, FeatureMap features) {
    listInfos.add(new ListInfo(type,source,features));
    return listInfos.size()-1;
  }
  public int getListInfoSize() {
    return listInfos.size();
  }
  
  public CharMapState getInitialState() {
    return initialState;
  }
  

  @Override
  public Visitor getVisitor() {
    // TODO Auto-generated method stub
    return null;
  }

  // TODO: this should probably be a method of the visitor!
  @Override
  public Iterator<Lookup> getLookups(com.jpetrak.gate.stringannotation.extendedgazetteer.State matchingState) {
    State s = (State)matchingState;
    return s.lookupSet.iterator();
  }

  @Override
  public ListInfo getListInfo(Lookup lookup) {
    Trie1Lookup l = (Trie1Lookup)lookup;
    return listInfos.get(l.listInfoIndex);
  }

  @Override
  public int getListInfoIndex(Lookup lookup) {
    Trie1Lookup l = (Trie1Lookup)lookup;
    return l.listInfoIndex;
  }
  
  @Override
  public void addLookupListFeatures(FeatureMap fm, Lookup lookup) {
    Trie1Lookup l = (Trie1Lookup)lookup;
    fm.putAll(listInfos.get(l.listInfoIndex).getFeatures());
  }
  
  @Override
  public void addLookupEntryFeatures(FeatureMap fm, Lookup lookup) {
    Trie1Lookup l = (Trie1Lookup)lookup;
    String[] entryFeatures = l.entryFeatures; 
      if(entryFeatures != null) {
        for(int i = 0; i<entryFeatures.length/2; i++) {
          int index = i*2;
          fm.put(entryFeatures[index],entryFeatures[index+1]);
        }
    }
    
  }
  
  @Override
  public String getLookupType(Lookup lookup) {
    Trie1Lookup l = (Trie1Lookup)lookup;
    return listInfos.get(l.listInfoIndex).getAnnotationType();
  }
  
  @Override
  public String statsString() {
    StringBuilder ret = new StringBuilder();
    ret.append("Number of States:     ");
    ret.append(CharMapState.nrNodes);
    ret.append("\n");
    ret.append("Number of MapStates:  ");
    ret.append(CharMapState.mapNodes);
    ret.append("\n");
    ret.append("Number of CharStates: ");
    ret.append(CharMapState.charNodes);
    ret.append("\n");
    ret.append("Number of Chars:      ");
    ret.append(CharMapState.nrChars);
    ret.append("\n");
    ret.append("Number of Inputchars: ");
    ret.append(CharMapState.nrInput);
    ret.append("\n");
    return ret.toString();
  }
  
  @Override
  public Collection<ListInfo> getListInfos() {
    return listInfos;
  }

  @Override
  public void compact() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void save(File whereTo) {
    // do nothing for this implementation!
    System.err.println("WARNING: not saving anything for trie1 implementation!");
  }

  public GazStore load(File whereFrom) {
    System.err.println("WARNING: not loading anything for trie1 implementation!");
    return null;
  }
  
}
