package com.jpetrak.gate.stringannotation.extendedgazetteer3.radixtree;

/**
 * A String node represents a substring inside the trie. The node
 * contains the payload for the string that ends with the string
 * stored in this node. It also contains a successor for 
 * any string for which the string so far is a prefix.
 * Either the payload or the successor must be non-empty. 
 *
 */
public class StringNode extends Node {
  char[] substring;
  Node next = null;
  int payload = -1;
  public int getPayload() {
    return payload;
  }
  public boolean matchesAt(char ch, int position) {
    if(position >= substring.length || position < 0) {
      throw new RuntimeException("StringNode.matchesAt: trying to match "+
        (new Character(ch).toString())+" at "+position+" but >"+
        (new String(substring))+"< is only "+substring.length);
    }
    return (substring[position] == ch);
  }
  public boolean atEnd(int position) {
    if(position >= substring.length || position < 0) {
      throw new RuntimeException("StringNode.atEnd: trying to check for atEnd "+
        " at "+position+" but >"+
        (new String(substring))+"< is only "+substring.length);
    }
    return (position == (substring.length - 1));    
  }
  public void setPayload(int payload) {
    this.payload = payload;
  }
  public void setSuffixNode(Node node) {
    this.next = node;
  }
}
