package com.jpetrak.gate.stringannotation.extendedgazetteer.trie2;

import gate.util.GateRuntimeException;

import java.util.ArrayList;
import java.util.Collection;

import com.jpetrak.gate.stringannotation.extendedgazetteer.Lookup;

public class SingleCharState extends State {

  // a key of 0 identifies a state that is still unused!
  char key = 0;  
  State next;
  
  public SingleCharState() {
    nrNodes++;
    charNodes++;
  }
  
  @Override
  public State put(char key, State value) {
    // Now that we use replace for overwriting the state, this should never happen
    // unless our key is still 0
    if(this.key != 0) {
      throw new GateRuntimeException("SingleCharState: put of "+key+" when old key is "+this.key);
    }
    this.key = key;
    this.next = value;
    nrChars++;
    return value;
  }

  public State replace(char key, State newState, State oldState) {
    if(key != this.key) {
      throw new GateRuntimeException("SingleCharState: Trying to replace key "+this.key+" with key "+key);
    }
    if(next != oldState) {
      throw new GateRuntimeException("SingleCharState: old states differ for replace!");
    }
    this.key = key;
    this.next = newState;
    return newState;
  }
  

  @Override
  public State next(char chr) {
    if(chr == key) {
      return next;
    } else {
      return null;
    }
  }

}
