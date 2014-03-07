/*
 *  SimpleRegexpAnnotator.java
 *
 *  $Id: SimpleRegexpAnnotator.java  $
 *
 */
package com.jpetrak.gate.stringannotation.regexpannotator;


import gate.*;
import gate.creole.metadata.*;


/** 
 * See online wiki page:
 * http://code.google.com/p/gateplugin-stringannotation/wiki/SimpleRegexpAnnotator
 *
 * @author Johann Petrak
 */
@CreoleResource(name = "Simple Regexp Annotator",
helpURL="http://code.google.com/p/gateplugin-stringannotation/wiki/SimpleRegexpAnnotator",
comment = "Create annotations based on Java regular expressions")
public class SimpleRegexpAnnotator 
  extends AbstractSimpleRegexpAnnotator
  implements ProcessingResource {

  private static final long serialVersionUID = 1L;

  
} // class SimpleRegexpAnnotator

