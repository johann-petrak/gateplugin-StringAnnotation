package com.jpetrak.gate.stringannotation.extendedgazetteer.trie2;

import static org.junit.Assert.*;
import gate.FeatureMap;
import gate.util.GateRuntimeException;
import gate.util.MethodNotImplementedException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.Test;

import com.jpetrak.gate.stringannotation.extendedgazetteer.GazStore;
import com.jpetrak.gate.stringannotation.extendedgazetteer.Lookup;
import com.jpetrak.gate.stringannotation.extendedgazetteer.Visitor;
import com.jpetrak.gate.stringannotation.extendedgazetteer.ListInfo;
import com.jpetrak.gate.stringannotation.extendedgazetteer.trie2.StoreArrayOfCharArrays;
import com.jpetrak.gate.stringannotation.extendedgazetteer.trie2.Utils;


public class GazStoreTrie2 extends GazStore {
  
  static final boolean useChars = true;
  
  public GazStoreTrie2() {
    //System.out.println("DEBUG: Creating a GazStoreTrie2!!");
  }
  
  // Ultimately, this is where we store all information about lookups:
  // - the mapping between key index numbers and key strings
  // - the set of per-entry key/value features
  // - the list of lookups per node
  StoreArrayOfCharArrays lookupStore = new StoreArrayOfCharArrays();
  
  // this is necessary during the creation of the lookup store to
  // keep trak of which key is mapped to which index
  HashMap<String,Integer> keyIndices = new HashMap<String,Integer>();
  
  // TODO: very ultimately, we will store all nodes in an array
  // of chars too. This could be another StoreSrrayOfChars, but
  // maybe a separate, specific implementation with equal length
  // chunks for each node is better?
  
  
  public void // com.jpetrak.gate.stringannotation.extendedgazetteer2.State 
    addLookup(String text, int infoIndex, String[] entryFeatures) {
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
    
    // TODO: CHECK IF THIS LOOKUP IS ALREADY IN THE LIST?  
    
    int curIndex = currentState.lookupIndex;
    currentState.addLookup(addLookupToStore(curIndex,infoIndex,entryFeatures));
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

  // TODO: this should really be a method of the visitor!
  public Iterator<Lookup> getLookups(com.jpetrak.gate.stringannotation.extendedgazetteer.State matchingState) {
    State s = (State)matchingState;
    return new OurLookupIterator(s);
  }
  
  @Override
  public void addLookupEntryFeatures(FeatureMap fm, Lookup lookup) {
    OurLookup l = (OurLookup)lookup;
    addToFmFromChunk(fm,l.chunk);
  }
  
  @Override
  public void addLookupListFeatures(FeatureMap fm, Lookup lookup) {
    OurLookup l = (OurLookup)lookup;
    int lookupIndex = getListInfoFromChunk(l.chunk);
    FeatureMap lfm = listInfos.get(lookupIndex).getFeatures();
    fm.putAll(lfm);
  }
  
  @Override
  public String getLookupType(Lookup lookup) {
    OurLookup l = (OurLookup)lookup;
    int lookupIndex = getListInfoFromChunk(l.chunk);
    return listInfos.get(lookupIndex).getAnnotationType();      
  }
  
  @Override
  public ListInfo getListInfo(Lookup lookup) {
    OurLookup l = (OurLookup)lookup;
    int lookupIndex = getListInfoFromChunk(l.chunk);
    return listInfos.get(lookupIndex);            
  }
  
  @Override
  public int getListInfoIndex(Lookup lookup) {
    OurLookup l = (OurLookup)lookup;
    return getListInfoFromChunk(l.chunk);
  }
  
  @Override
  public Collection<ListInfo> getListInfos() {
    return listInfos;
  }
  
  ////////////////////////////////////////////////////////////////////////////////////
  /// non-public below here ...
  
  
  // helper: if storeIndex < 0 create a new store list entry and return the index.
  // otherwise convert the lookupinfoindex and the keyvals to a list entry and 
  // add to the store at the given store index. Always return the store index.
  protected int addLookupToStore(int storeIndex, int lookupInfoIndex, String[] keyvals) {
    char[] chunk = lookup2chunk(lookupInfoIndex, keyvals);
    if(storeIndex < 0) {
      return lookupStore.addListData(chunk);
    } else {
      return lookupStore.addListData(storeIndex, chunk);
    }
  }
  
  /**
   * Convert an array of alternating key/value strings to a single character
   * array representing those key-values. The keys are represented as integers
   * of the entry actually holding the key characters.
   * 
   * @param keyvalues
   * @return
   */
  protected char[] lookup2chunk(int listInfo, String[] keyvalues) {
    // the length we need is, if 2*n is the number of elements in keyvalues 
    // 2 char for the initial int that holds the number of key/value pairs
    // 2 char for the int that holds the listInfo
    // n*2 char for n entry lengths
    // n*2 char for n key indices
    // the sum of all the lengths of the odd entries in keyvalues (just the values)
    int n = keyvalues.length/2;
    int length = 4 + 4*n;
    for(int i=1; i<keyvalues.length; i=i+2) {
      length += keyvalues[i].length();
    }
    char[] ret = new char[length];
    char[] c2  = Utils.int2TwoChars(keyvalues.length/2);
    int curindex = 0;
    ret[curindex++] = c2[0];
    ret[curindex++] = c2[1];
    c2  = Utils.int2TwoChars(listInfo);
    ret[curindex++] = c2[0];
    ret[curindex++] = c2[1];    
    for(int i=0; i<keyvalues.length; i=i+2) {
      //System.out.println("Adding key/value: "+(keyvalues[i]+"/"+keyvalues[i+1]));
      c2 = Utils.int2TwoChars(keyvalues[i+1].length()+4); // total length including length and key index
      //System.out.println("Setting length to "+(keyvalues[i+1].length()+4));
      ret[curindex++] = c2[0];
      ret[curindex++] = c2[1];
      int keyIndex = addKey(keyvalues[i]);
      //System.out.println("Added key got index "+keyIndex);
      c2 = Utils.int2TwoChars(keyIndex);
      ret[curindex++] = c2[0];
      ret[curindex++] = c2[1];
      int l = keyvalues[i+1].length();
      System.arraycopy(keyvalues[i+1].toCharArray(), 0, ret, curindex, l);
      curindex += l;
    }
    return ret;
  }
  
  protected void addToFmFromChunk(FeatureMap fm, char[] chunk) {
    int curindex = 0;
    // first get the number of entries    
    int nrEntries = Utils.twoChars2Int(chunk[0],chunk[1]);
    // int listInfo = Utils.twoChars2Int(chunk[2],chunk[3]);
    //System.out.println("Number of entries of chunk is "+nrEntries);
    curindex = 4;
    for(int i = 0; i<nrEntries; i++) {
      // get the length of this entry
      int thisLength = Utils.twoChars2Int(chunk[curindex],chunk[curindex+1]);
      //System.out.println("Length of this chunk: "+thisLength);
      int thisKeyIndex = Utils.twoChars2Int(chunk[curindex+2],chunk[curindex+3]);
      //System.out.println("Key index is "+thisKeyIndex);
      char[] val = new char[thisLength-4];
      System.arraycopy(chunk, curindex+4, val, 0, thisLength-4);
      fm.put(getKey(thisKeyIndex), new String(val));
      curindex += thisLength;
    }
  }
  
  protected int getListInfoFromChunk(char[] chunk) {
    return Utils.twoChars2Int(chunk[2],chunk[3]);
  }
  
  
  /**
   * Adds a key to the store unless it is already there. In both cases, returns the 
   * index of the key in the key store
   * @param key
   * @return
   */
  protected int addKey(String key) {
    if(keyIndices.containsKey(key)) {
      return (keyIndices.get(key));
    } else {
      int index = lookupStore.addData(key.toCharArray());
      keyIndices.put(key, index);
      return index;
    }
  }
  
  protected char[] getKeyChars(int index) {
    return lookupStore.getData(index);
  }
  
  protected String getKey(int index) {
    return new String(getKeyChars(index));
  }
  
  
  @Test
  public void runImplementationTests() {
    System.out.println("Running GazStoreTrie1 tests ");
    String key1 = "key1";
    int i1 = addKey(key1);
    System.out.println("key1 added, index is "+i1);
    int i1a = addKey(key1);
    assertEquals(i1,i1a);
    String key2 = "key2";
    int i2 = addKey(key2);
    System.out.println("key2 added, index is "+i2);
    
    String[] keyvals1 = new String[]{"k1","v1","k2", "value2", "keynumber3", "v3", "k4", ""};
    char[] chunk1 = lookup2chunk(0,keyvals1);
    
    FeatureMap fm = gate.Factory.newFeatureMap();
    addToFmFromChunk(fm,chunk1);
    System.out.println("FeatureMap is "+fm);
    assertEquals("v1",(String)fm.get("k1"));
    assertEquals("value2",(String)fm.get("k2"));
    assertEquals("",(String)fm.get("k4"));
    
    String[] keyvals2 = new String[]{"k1","nother","k2","asasassa","k3","xxxxx"};
    char[] chunk2 = lookup2chunk(0,keyvals2);
    FeatureMap fm2 = gate.Factory.newFeatureMap();
    addToFmFromChunk(fm2,chunk2);
    assertEquals("nother",(String)fm2.get("k1"));
    assertEquals("asasassa",(String)fm2.get("k2"));
    assertEquals(null,fm2.get("k4"));
    assertEquals("xxxxx",(String)fm2.get("k3"));
    
    int si1 = addLookupToStore(-1,12,keyvals1);
    addLookupToStore(si1,23,keyvals2);
    
    int si2 = addLookupToStore(-1,12,keyvals1);
    addLookupToStore(si2,23,keyvals2);
    
    
  }
  
  protected class OurLookup extends Lookup {
    OurLookup(char[] chunk) {
      this.chunk = chunk;
    }
    char[] chunk;
    
    
  }
  
  protected class OurLookupIterator implements Iterator<Lookup> {

    // The iterator represents the lookups simply as indices into the store
    // and into the elemts of lists in the store.
    // The iterator is initialized to point at the first element in the list, if any
    // Each call to next() consumes the element and points to the next, if any.
    // If the curLookup is >= nrEntries, then all elements have consumed and nothing
    // more is available
    
    
    int storeIndex;  // the index of the list of lookups in the store
    int curLookup;   // the index of the current lookup within the list
    int nrEntries;
    
    public OurLookupIterator(State theState) {
      storeIndex = theState.lookupIndex;
      nrEntries = lookupStore.getListSize(storeIndex);
      curLookup = 0;
    }
    
    @Override
    public boolean hasNext() {
      return curLookup < nrEntries;
    }

    @Override
    public OurLookup next() {
      if(curLookup < nrEntries) {
        //System.out.println("Trying to get list index "+storeIndex+" curlookup "+curLookup);
        char[] chunk = lookupStore.getListData(storeIndex, curLookup);
        curLookup++;
        return new OurLookup(chunk);
      } else {
        throw new GateRuntimeException("Tried to access next() but no more lookups");
      }
    }

    @Override
    public void remove() {
      // TODO Auto-generated method stub
      throw new MethodNotImplementedException();
    }
    
  }

  public String statsString() { 
    return "";
  }

  @Override
  public void compact() {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void save(File whereTo) {
    // do nothing for this implementation!
    System.err.println("WARNING: not saving anything for trie2 implementation!");
  }
  
  public GazStore load(File whereFrom) {
    System.err.println("WARNING: not loading anything for trie2 implementation!");
    return null;
  }
  
  
}
