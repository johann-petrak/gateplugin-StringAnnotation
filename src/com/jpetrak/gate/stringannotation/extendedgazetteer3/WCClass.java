package com.jpetrak.gate.stringannotation.extendedgazetteer3;

/**
 * Enums for easy selection of those character classes that the gazetteer regards to be 
 * valid parts of a word. Everything else is considered part of word separation.
 * Both the part of word characters and the part of word separation characters can be extended
 * with additional parameters wordChars and wordBoundaryChars.
 * 
 * @author Johann Petrak
 */
public enum WCClass {
  /**
   * Everything that is not whitespace is considered to belong to the word (including 
   * punctuation and numbers). 
   */
  NONWHITESPACE, 
  /**
   * Only letters are considered part of the word.
   */
  LETTERS, 
  /**
   * Only letters and hyphens are considered part of the word.
   */
  LETTERSORHYPHEN, 
  /**
   * Only digits are considered part of the word.
   */
  DIGITS, 
  /**
   * Only letters or digits are considered part of the word.
   */
  LETTERSORDIGITS;
}
