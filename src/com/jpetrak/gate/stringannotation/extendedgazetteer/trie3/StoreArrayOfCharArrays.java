package com.jpetrak.gate.stringannotation.extendedgazetteer.trie3;

import java.io.Serializable;

import gate.util.GateRuntimeException;
import it.unimi.dsi.fastutil.chars.CharBigArrayBigList;

/**
 * This represents a store that can add and retrieve char[] elements. Each char[] chunk
 * is identified by an int index which is returned when adding the chunk and which
 * can be used to get it back. The number of chunks in a store is limited to 2^31, and
 * the length of each chunk is also limited to 2^31, but the total amount of chars in
 * the store can exceed 2^32 (unsigned 32 bit).
 * <p>
 *  The store supports storing the following:
 *  <ul>
 *  <li>Varying length chunks. These consist of two chars that encode the length of the data 
 *  (not including the length field) followed  by the data
 *  <li>Fixed length chunks: just data, the client must know the length and supply it both when
 *  adding and retrieving data
 *  <li>Lists: each list consists of one or more elements of varying length chunks. The first 
 *  of a list consists of the the chunk length, the list size, the index of the next element chunk
 *   and the actual chunk data for the first element. Elements 2-N consist of the chunk length,
 *   the index of the next element chunk and the actual chunk data. Next chunk index and in the 
 *   first element, the list size are included in chunk length, the chunk length field itself
 *   is not.
 *  </ul>
 * 
 * @author Johann Petrak
 *
 */
public class StoreArrayOfCharArrays implements Serializable {
   
  /**
   * 
   */
  private static final long serialVersionUID = 1238979454339893943L;
  CharBigArrayBigList theList = new CharBigArrayBigList();
  int curIndex = 0;
  
  private char[] zeroChars = Utils.int2TwoChars(0); 
  private char[] oneChars = Utils.int2TwoChars(1);
  
  /**
   * Add some data and get back the index under which we can get it back
   * @param data
   * @return
   */
  public int addData(char[] data) {
    // remember where we store the data
    int oldIndex = curIndex;
    // first store the length of the data
    // we split the int that represents the length into to chars
    int l = data.length;
    char[] lAsChars = Utils.int2TwoChars(l);
    addChars(lAsChars);
    addChars(data);
    // after storing, the new index is now moved by the length of the data
    // plus the two chars where we store the length
    curIndex += data.length+2;
    return oldIndex;
  }

  /**
   * Add some data and get back the index under which we can get it back. This will 
   * add a chunk of data of known length to the store: no length is stored in the 
   * store for this chunk. This chunk can only be retrieved with the getFixedLengthData
   * method.
   * 
   * @param data
   * @return
   */
  public int addFixedLengthData(char[] data) {
    // remember where we store the data
    int oldIndex = curIndex;
    addChars(data);
    // after storing, the new index is now moved by the length of the data
    curIndex += data.length;
    return oldIndex;
  }
  
  public int replaceFixedLengthData(int index, char[] data) {
    for(int i = 0; i<data.length; i++) {
      theList.set(index+i,data[i]);
    }
    return index;
  }
 
  
  /** 
   * Get the data from at the given index.
   * @param index
   * @return
   */
  public char[] getData(int index) {
    // retrieve the length 
    int l = Utils.twoChars2Int(theList.get(index), theList.get(index+1));
    // now retrieve the characters for this data block
    char data[] = new char[l];
    for(int i=0; i<l; i++) {
      data[i] = theList.get(index+2+i);
    }
    return data;
  }

  /** 
   * Get data of known length from at the given index.
   * @param index
   * @return
   */
  public char[] getFixedLengthData(int index, int length) {
    char data[] = new char[length];
    for(int i=0; i<length; i++) {
      data[i] = theList.get(index+i);
    }
    return data;
  }
  
  /**
   * Add a new list to the store and return its index. After this, a list with 
   * length of 1 is stored.
   * 
   * @param data
   * @return
   */
  public int addListData(char[] data) {
    // create the special first list entry: 
    // = length of list (int=2 chars), set to 1
    // = index of next list entry (int=2 chars), set to 0
    // = actual data
    // remember where we store the data
    int oldIndex = curIndex;
    // first store the length of the data: for the first list entry
    // this also includes size and next element index, so add 4
    // we split the int that represents the length into to chars
    int l = data.length+4;
    char[] lAsChars = Utils.int2TwoChars(l);
    addChars(lAsChars);
    addChars(oneChars);
    addChars(zeroChars);
    addChars(data);
    // after storing, the new index is now moved by the length of the data
    // plus the two chars where we store the length
    curIndex += data.length+6;
    return oldIndex;    
  }
  
  /**
   * Append additional data blocks to a list that already exists in the store at 
   * the given index. if index is <=0, add a new list.
   * 
   * @param index
   * @param data
   * @return
   */
  // TODO: instead of doing getData to get the element indices, just directly access
  // the characters
  public int addListData(int index, char[] data) {
    if(index <= 0) {
      return addListData(data);
    }

    int size = getListSize(index); 
    if(size < 1) {
      throw new GateRuntimeException("Adding to a list, but size is <1: "+size);
    }

    
    // update the size
    char sz[] = Utils.int2TwoChars(size+1);
    theList.set(index+2,sz[0]); // just skip the data length characters: 2 characters
    theList.set(index+3,sz[1]);
    
    
    // store the new data
    int newBlockIndex = addNewListBlock(data);
    
    // now add the index of that block to either the first block or 
    // dereference until we are the correct block
    
    int curBlockIndex = index; 
    // if we need to append not at the first block (size!=1, get the next block which
    // corresponds to size=2    
    // then if size > 2, iterate as often as still needed
    if(size > 1) {
      curBlockIndex = getNextElementIndex4First(index);
      for(int i=2; i<size; i++) {
        curBlockIndex = getNextElementIndex4Other(curBlockIndex);
      }
    }
    
    // encode the new block index 
    char idx[] = Utils.int2TwoChars(newBlockIndex);
    
    
    if(size == 1) {
      theList.set(index+4,idx[0]);
      theList.set(index+5,idx[1]);            
    } else {
      theList.set(curBlockIndex+2,idx[0]);
      theList.set(curBlockIndex+3,idx[1]);                  
    }
    
    
    return index;
  }
  
  /**
   * Return the list element at the given index.
   * @param index
   * @param element
   * @return
   */
  public char[] getListData(int index, int element) {
    // get the first block which must exist
    int size = getListSize(index);
    if(size <= 0) {
      throw new GateRuntimeException("getting list data but size is <=0: "+size);
    }
    assert(element<size);
    if(element >= size) {
      throw new GateRuntimeException("getting list data but element>=size: "+element+"/"+size);
    }
    
    if(size == 1 && element != 0) {
      throw new GateRuntimeException("getting list data but size=1 and element!=0: "+element);
    }
    if(element == 0) {
      return getDataWithout(index, 4);
    }
    // if we need an element >0, 
    // de-reference the current block "element" times and get the data from there
    int nextBlockIndex = getNextElementIndex4First(index);
    // we did already the first dereferencing from the first block, so if necessary
    // dereference element-1 more times
    for(int i=0;i<(element-1);i++) {
      nextBlockIndex = getNextElementIndex4Other(nextBlockIndex);
    }
    // we have the index of the block we want, return it
    return getDataWithout(nextBlockIndex,2);
  }
  
  /** 
   * Find the chunk among all the list elements stored at index and 
   * return the index of the element (>= 0) if found or -1 if not found.
   * 
   * @param index index of the list in the store
   * @param chunk the chunk to find
   * @return the index of the chunk in the list or -1 if not found
   */
  public int findListData(int index, char[] chunk) {
    int elementIndex = 0;
    
    // if the list exists at all, there always must be at least one element, so
    // always check the first element.
    // Find the start and the length of the first element and compare
    int length = Utils.twoChars2Int(theList.get(index), theList.get(index+1));
    int chunkIndex = index+6;  // 2 for the chunk length, 2 for list size,, 2 for next element index
    if(isChunkEqual(chunkIndex,length-4,chunk)) {
      return elementIndex;
    }
    // get the next chunk pointer
    int nextBlockIndex = getNextElementIndex4First(index);
    while(nextBlockIndex != 0) {
      elementIndex++;
      // now check the block at this index!
      length = Utils.twoChars2Int(theList.get(nextBlockIndex), theList.get(nextBlockIndex+1));
      chunkIndex = nextBlockIndex+4; // 2 for chunk length, 2 for next element index
      if(isChunkEqual(chunkIndex,length-2,chunk)) {
        return elementIndex;
      }
      nextBlockIndex = getNextElementIndex4Other(nextBlockIndex);
    }
    return -1;
  }

  
  /**
   * Check if the characters in the store, starting at index and having length length,
   * are identical to the characters of the chunk. 
   * @param index
   * @param length
   * @param chunk
   * @return
   */
  protected boolean isChunkEqual(int index, int length, char[] chunk) {
    if(chunk.length != length) {
      return false;
    }
    for(int i = 0; i<length; i++) {
      if(theList.get(index+i) != chunk[i]) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Return the size of the list at the given index
   * @param index
   * @return
   */
  public int getListSize(int index) {
   return Utils.twoChars2Int(theList.get(index+2), theList.get(index+3));
  }
  
  //*******************************************************************
  
  
  private int getNextElementIndex4First(int index) {
    return Utils.twoChars2Int(theList.get(index+4), theList.get(index+5));
  }
  private int getNextElementIndex4Other(int index) {
    return Utils.twoChars2Int(theList.get(index+2), theList.get(index+3));
  }
  
  // special method to retrieve just the data from a list element: same as 
  // the ordinary get data, except the we have to skip to chars at the 
  // beginning which is the next element pointer, or the size and next element pointer
  // without is 2 or 4 for these.
  private char[] getDataWithout(int index, int without) {
    // retrieve the length 
    int l = Utils.twoChars2Int(theList.get(index), theList.get(index+1));
    // now retrieve the characters for this data block
    char data[] = new char[l-without];
    for(int i=0; i<(l-without); i++) {
      data[i] = theList.get(index+2+without+i);
    }
    return data;
  }
  
  // similar to addData but also adds the empty next block entry at the beginning 
  private int addNewListBlock(char[] data) {
    // remember where we store the data
    int oldIndex = curIndex;
    // first store the length of the data
    // we split the int that represents the length into to chars
    int l = data.length+2;
    char[] lAsChars = Utils.int2TwoChars(l);
    addChars(lAsChars);
    addChars(zeroChars);
    addChars(data);
    // after storing, the new index is now moved by the length of the data
    // plus the two chars where we store the length
    curIndex += data.length+4;
    return oldIndex;    
  }
  
  
  private void addChars(char[] cs) {
    for(char c : cs) {
      theList.add(c);
    }
  }
  
  
}
