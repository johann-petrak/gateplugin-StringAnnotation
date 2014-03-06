/*
 * ExtendedGazeteer.java
 * 
 * $Id: ExtendedGazetteer.java 2386 2011-05-02 20:23:30Z johann $
 *
 */
package com.jpetrak.gate.stringannotation.extendedgazetteer3;

// TODO: allow extended config files: special lines in the def file to 
// e.g. define which features are allowed at all, the feature string 
// separator (instead of using the parameter), which features to intern,
// which features to interpret as uriRefs, the baseURI for all uriRefs etc.
// How: if the first line consists of a single non-whitespace character,
// this character is to be interpreted as the extended config line character.
// All lines starting with this character are extended config lines and follow
// the syntax of property files (should we use YAML?)
// inside an extended config line the "#" character is a comment so 
// #
// ## this is a comment
// 

// TODO: allow to specify the output Annotation type in addition to the output
// annotation set. It might also be interesting to overwrite this from
// the extended config file, potentially for each lst file separately:
// #
// # outputAnnotationType = Lookup1
// filename1.lst
// filename2.lst
// # outputAnnotationType = Lookup2

// TODO: optionally allow string interning - maybe just with extended config files?


// TODO: better case mapping by case-normalizing the document content string
// or the gazetteer input string instead of individual characters 
// (after normalization, see below).
// TODO: do Unicode string normalization (optionally) using 
// java.text.Normalizer (Normalizer.normalize("a\u0301", Normalizer.Form.NFC).equals("á"))
// for both the document string and the gazetteer strings.
// PROBLEM:  these two would change the length of the content string so 
// we would have to map offsets.
// Maybe it would be better to instead make this gazetteer to work as a 
// flexible gazetteer by including part of the virtualdocuments logic: 
// Optionally specify a annotation specification and do on the fly generation
// of the new content and mapping of othe generated annotations.
// This would be useful in itself but also allow to preprocess the document
// so that e.g. the tokens get a "normalizedUCString" feature that has normalized
// Unicode and uppercase representation for the gazetteer



// NOTE: for storing lots of features without the need to fit into memory:
//   Possibly useful: Berkley DB Java Edition
//   http://www.oracle.com/technetwork/database/berkeleydb/downloads/index.html?ssSourceSiteId=ocomen
//   or this: http://code.google.com/p/orient/
//

// NOTE: VR for default gazetteer cannot be overwritten/disabled


import gate.creole.metadata.CreoleResource;


/**
 *  See documentation in the wiki:
 * http://code.google.com/p/gateplugin-stringannotation/wiki/ExtendedGazetteer
 *
 *  @author Johann Petrak
 *  @author Valentin Tablan
 *  @author Borislav Popov
 */
/*
@CreoleResource(
  name = "Extended Gazetteer 3",
  comment = "An extended version of the GATE DefaultGazetteer that supports finding prefixes and suffixes, specification of word and non-word characters, reuse of datastructures for multiple identical copies of a gazetteer and more.",
  icon="shefGazetteer.gif",
  helpURL="http://code.google.com/p/gateplugin-stringannotation/wiki/ExtendedGazetteer2"
)
*/
public class ExtendedGazetteer3
  extends AbstractExtendedGazetteer {
  private static final long serialVersionUID = 1L;


} // ExtendedGazetteer

