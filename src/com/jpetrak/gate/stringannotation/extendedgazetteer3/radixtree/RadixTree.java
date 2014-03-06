package com.jpetrak.gate.stringannotation.extendedgazetteer3.radixtree;

/**
 * A datastructure to store strings and associate them with a payload
 * which is a single int. The payload may only be positive values
 * i.e. values >= 0. In some contexts, a payload of -1 indicates
 * that the given string is not stored in the tree and a payload
 * of 0 indicates that the string is in the tree but not associated
 * with an actual payload value.
 * 
 * @author Johann Petrak
 *
 */
public class RadixTree {
  Node root = new CharNode();
  /**
   * Insert the string and store the payload with it. The string must not
   * be empty and the payload must be >= 0.
   * 
   * @param string
   * @param payload
   */
  public void insert(String string, int payload) {
    if(string.isEmpty()) {
      throw new RuntimeException("RadixTree.insert: string must not be empty!");
    }
    if(payload < 0) {
      throw new RuntimeException("RadixTree.insert: payload must not be < 0!");
    }
    
    // use a visitor to find node where the first mismatch is found or to
    // find a node that is a final match. We need to keep track of the 
    // lastNode (at all times, the node which points to the currentNode) and
    // if lastNode was a charnode, the char of the match so we can replace 
    // the current node by some other node, if necessary
    // if mismatch found:
    //   - if we are at a charnode, insert the char we are at
    //     add a suffix node for for the char
    //     if the suffix is > 1 char use a stringnode otherwise a charnode
    //   - if we are at a stringnode, check at which position we are:
    //     = first: replace the current node by a Char node and make next
    //       a stringnode (except when the string is only 2 in total already
    //     = middle replace the current node by a different stringnode and 
    //       make next a stringnode or charnode, depending on length
    //     = last and last>2: make the current node a stringnode and make
    //       the next node a charnode
    //     = make the current node a charnode and the next one too
  }
  
  /**
   * Retrieve the payload for this string or -1 if the 
   * string is not in the tree.
   * @param string
   * @return
   */
  public int get(String string) {
    return 1;
  }
  
  /**
   * Get a freshly initialized visitor instance
   * @return
   */
  public Visitor getVisitor() {
    return new Visitor(this);
  }
  
}
