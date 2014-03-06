package com.jpetrak.gate.stringannotation.extendedgazetteer3.radixtree;

/**
 * Visitor object for successively matching a sequence of 
 * characters against the tree and track if we are still matching,
 * where we match, if we are at a final node etc.
 * <p>
 * NOTE: the package private methods are used by RadixTree insert()
 * to directly access the state information once a match fails
 * and something has to be done in order to insert the
 * unmatched suffix at that point.
 * <p>
 * CONTRACTS:
 * <ul>
 * <li>At any moment the visitor is either at the reset state or at a state
 * after matching a character or at a state where matching a character 
 * failed. 
 * <li>After a match, the currentNode is either the node of the suffix 
 * of the string so far or null, if no more matching is possible. The lastNode
 * is the node where the last match was found.
 * <li>After a failed match, the currentNode is null and 
 * </ul>
 * 
 * 
 * @author Johann Petrak
 *
 */

public class Visitor {
  RadixTree tree;
  Node currentNode;
  Node nextNode;
  int currentStringIndex = -1;
  int currentPayload = -1;
  boolean matching = false;
  char currentChar = 0;
  
  public Visitor(RadixTree tree) {
    this.tree = tree;
    reset();
  }
  public Visitor() {
    throw new RuntimeException("Cannot create Visitor without a tree!");
  }
  /**
   * Reset the visitor object so a new matching process can start
   */
  public void reset() {
    currentNode = null;
    nextNode = tree.root;
    currentStringIndex = -1;
    currentPayload = -1;
    matching = false;
    currentChar = 0;
  }
  /**
   * Try to match the given character at the current state and return
   * true if we still match. This can only be called either after a reset
   * or after a match was already successful. 
   * @param c
   * @return
   */
  public boolean match(char c) {
    // the only way we can ever have currentNode==null is right after reset!!
    currentChar = c;
    if(currentNode == null) {
      // if we are at the root node, we know it is a CharNode
      Node currentNode = nextNode;
      currentPayload = ((CharNode)currentNode).getPayload(c);
      nextNode = ((CharNode)currentNode).getSuffixNode(c);
      if(nextNode != null || currentPayload >= 0) {
        matching = true;
      } 
    } else {
      // we must not match after a match was unsuccessful already!
      if(!matching) {
        throw new RuntimeException("Visitor.match: trying to match after a match already failed!");
      }
      // last match was successful
      // in order to continue matching we need to match against the start of the current suffix
      // the current suffix is represented either by a nextNode, if not null
      // if nextNode is null we could still be in a stringnode at which point we try to advance
      // the string pointer and match the next char.
      currentPayload = -1;
      matching = false;
      if(nextNode != null) {
        currentNode = nextNode;
        nextNode = null;
        // proceed depending on what kind of node we have now
        if(currentNode instanceof CharNode) {
          currentStringIndex = -1;
          currentPayload = ((CharNode)currentNode).getPayload(c);
          nextNode = ((CharNode)currentNode).getSuffixNode(c);
          if(nextNode != null || currentPayload >= 0) {
            matching = true;
          }           
        } else {
          // we are at a new String node
          currentStringIndex = 0;
          // check if we can match the first character of the string node
          // if(stringnode matches c at currentStringIndex)
          // matching = true;
          // else
          // matching = false; (is already set)
          
        }
      } else {
        // our next node was null, so lets see if our current node is a string node
        if(currentNode instanceof StringNode) {
          // yes: lets see if we can continue to match ..
          // if we are already at the end of the string, then we must fail because we 
          //   have found not successor last time!
          //   matching = false;
          // else 
          // if we are not already at the end of the string, check the next char
          currentStringIndex++;
          // if we can match, set the match to true
          // if(stringnode matches c at currentStringIndex)
          //   matching = true;
          //   if we are now also at the last character of the string, 
          //     then get the payload and get the nextNode
          // else (could not match at new position)
          //  matching = false;
          
        }
      }
    }
    return matching;
  }
  /**
   * Return if the current match also represents a full matching string
   * or just a prefix. True if we match a full string.
   * Throws a runtime exception if called when there is no current match
   * (the last call to match() returned false or no match was attempted
   * yet) 
   */
  public boolean isFinal() {
    return true;
  }
  /**
   * Return the payload of a matching string, i.e. when the current
   * state is final. The payload always must be >=0 where 0 is a special
   * value meaning "no payload". 
   * Throws an exception if the current state is not final or no matching
   * was done yet.
   * @return
   */
  public int get() {
    if(currentPayload < 0) {
      throw new RuntimeException("Visitor.get: no match, cannot get a payload");
    }
    return currentPayload;
  }
  
  /**
   * Add a payload to the current state which also makes the current 
   * state final. The payload must be >= 0 with 0 indicating no 
   * payload, just making the string final. 
   * 
   * @param payload
   */
  public void put(int payload) {
    if(payload < 0) {
      throw new RuntimeException("Payload must be >= 0");
    }
  }
  
  // ********************
  // package private methods for use by RadixTree below
  
  /**
   * Get the current node
   * @return
   */
  Node getCurrentNode() {
    return currentNode;
  }
  
  /**
   * If we were matching a StringNode, this is the index into the
   * string of our last attempted match. After a failed match
   * this is the index to the first non-matching character in the
   * string.
   * This will throw a runtime exception if you try to get it
   * when the last node we tried to match in was not a StringNode
   * @return
   */
  int getStringIndex() {
    if(currentStringIndex < 0) {
      throw new RuntimeException("Visitor:getStringIndex: last attempted match was not in a StringNode");
    }
    return currentStringIndex;
  }
  
  
}
