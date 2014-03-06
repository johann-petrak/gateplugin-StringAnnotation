package com.jpetrak.gate.stringannotation.extendedgazetteer3.radixtree;


/**
 * @author Johann Petrak
 *
 */
public class CharNode extends Node {
  char[] keys = null;
  Node[] nodes = null;
  int[]  values = null;
  
  
  /**
   * 
   * @param string
   * @param payload
   */
  public void add(char[] chr, int payload) {
    
  }
  
  /**
   * Return the successor node for the character at this node
   * @param x
   * @return
   */
  public Node getSuffixNode(char x) {
    return this; ///!!!!
  }
  
  public int getPayload(char x) {
    return 1;
  }
}
