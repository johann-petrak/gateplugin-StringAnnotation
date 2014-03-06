/*
 * AbstractExtendedGazeteer.java
 * 
 * $Id: AbstractExtendedGazetteer.java 2386 2011-05-02 20:23:30Z johann $
 *
 */



package at.ofai.gate.extendedgazetteer;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.*;

import gate.*;
import gate.annotation.AnnotationSetImpl;
import gate.creole.*;
import gate.creole.gazetteer.*;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.*;
import java.io.BufferedReader;
import java.net.URL;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;


/**
 * This is the common base class for ExtendedGazetteer and 
 * IndirectExtendedGazetteer so that the code for both only needs to be defined
 * once.
 *
 *  @author Johann Petrak
 */
public abstract class AbstractExtendedGazetteer
  extends DefaultGazetteer {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Boolean prefixAnnotations = false;
  private Boolean suffixAnnotations = false;
  private Boolean includeStrings = false;
  private WCClass wordCharsClass = WCClass.NONWHITESPACE;
  private String wordBoundaryChars = "";
  private String wordBoundaryCharsValue = "";
  private String wordCharsValue = "";
  private String wordChars = "";

  public Boolean getPrefixAnnotations() {
    return prefixAnnotations;
  }

  // Parameters just inherited from the DefaultGazetteer
  @CreoleParameter(comment = "The document to be processed")
  @RunTime
  @Override
  public void setDocument(Document doc) {
    document = doc;
  }
  @Override
  public Document getDocument() {
    return document;
  }
  
  @CreoleParameter(
      comment = "The character used to separate features for entries in gazetteer lists. Accepts strings like &quot;\t&quot; and will unescape it to the relevant character. If not specified, this gazetteer does not support extra features.",
      defaultValue = "\\t"
      )
  @Optional
  @Override
  public void setGazetteerFeatureSeparator(String sep) {
    gazetteerFeatureSeparator = sep;
  }
  @Override
  public String getGazetteerFeatureSeparator() {
    return gazetteerFeatureSeparator;
  }
  
  @CreoleParameter(comment = "The annotation set to be used for the generated annotations")
  @Optional 
  @RunTime
  @Override
  public void setAnnotationSetName(String name) {
    annotationSetName = name;
  }
  @Override
  public String getAnnotationSetName() {
    return annotationSetName;
  }
  
  @CreoleParameter(comment = "The URL to the file with list of lists", defaultValue="")
  @Override
  public void setListsURL(java.net.URL theURL) {
    listsURL = theURL;
  }
  @Override
  public java.net.URL getListsURL() {
    return listsURL;
  }
  
  @CreoleParameter(comment = "The encoding used for reading the definitions",
    defaultValue="UTF-8")
  @Override
  public void setEncoding(String enc) {
    encoding = enc;
  }
  @Override
  public String getEncoding() {
    return encoding;
  }
  
  @CreoleParameter(comment = "Should this gazetteer differentiate on case",
    defaultValue = "true")
  @Override
  public void setCaseSensitive(Boolean yesno) {
    caseSensitive = yesno;
  }
  @Override
  public Boolean getCaseSensitive() {
    return caseSensitive;
  }
  
  @CreoleParameter(comment = "Should this gazetteer only match whole words",
    defaultValue = "true")
  @Override
  @RunTime
  public void setWholeWordsOnly(Boolean yesno) {
    wholeWordsOnly = yesno;
  }
  @Override
  public Boolean getWholeWordsOnly() {
    return wholeWordsOnly;
  }
  
  // Only relevant if wholewordsonly is true: if true, prefix
  // matches (i.e. matches where we have a suffix) are allowed too
  @CreoleParameter(comment = "Allow prefix matching",defaultValue="false")
  @RunTime
  @Optional
  public void setAllowPrefixMatches(Boolean yesno) { 
    allowPrefixMatches = yesno;
  }
  public Boolean getAllowPrefixMatches() { return allowPrefixMatches ; }
  private Boolean allowPrefixMatches = false;
  
  @CreoleParameter(comment = "Allow suffix matching",defaultValue="false")
  @RunTime
  @Optional
  public void setAllowSuffixMatches(Boolean yesno) { 
    allowSuffixMatches = yesno;
  }
  public Boolean getAllowSuffixMatches() { return allowSuffixMatches ; }
  private Boolean allowSuffixMatches = false;
  
  @CreoleParameter(
    comment = "Disallow whole word matches if wholeWordsOnly is set to eanable prefix or suffix matches",
    defaultValue = "false")
  @RunTime
  @Optional
  public void setDisallowWholeMatches(Boolean b) { disallowWholeMatches = b ; }
  public Boolean getDisallowWholeMatches() { return disallowWholeMatches ; }
  private Boolean disallowWholeMatches = false;
  
  @CreoleParameter(comment = "Should this gazetteer only match the longest string starting from any offset?",
    defaultValue = "true")
  @Override
  @RunTime
  public void setLongestMatchOnly(Boolean yesno) {
    longestMatchOnly = yesno;
  }
  @Override
  public Boolean getLongestMatchOnly() {
    return longestMatchOnly;
  }
  
  // Parameters added by this PR
  
  @CreoleParameter(comment = "Generate prefix annotations?", defaultValue="false")
  @RunTime
  public void setPrefixAnnotations(Boolean newPrefixAnnotations) {
    prefixAnnotations = newPrefixAnnotations;
  }

  public Boolean getSuffixAnnotations() {
    return suffixAnnotations;
  }

  @CreoleParameter(comment = "Generate suffix annotations?", defaultValue="false")
  @RunTime
  public void setSuffixAnnotations(Boolean newSuffixAnnotations) {
    suffixAnnotations = newSuffixAnnotations;
  }

  public Boolean getIncludeStrings() {
    return includeStrings;
  }

  @CreoleParameter(comment = "Include original string as feature?", defaultValue="false")
  @RunTime
  public void setIncludeStrings(Boolean newIncludeStrings) {
    includeStrings = newIncludeStrings;
  }

  public WCClass getWordCharsClass() {
    return wordCharsClass;
  }


  @CreoleParameter(comment = "Types of characters that make up words?", 
    defaultValue="LETTERS")
  public void setWordCharsClass(WCClass newWordCharsClass) {
    wordCharsClass = newWordCharsClass;
  }

  public String getWordBoundaryChars() {
    return wordBoundaryCharsValue;
  }

  @CreoleParameter(comment = "Additional word boundary characters?", 
    defaultValue="")
  public void setWordBoundaryChars(String newWordBoundaryChars) {
    wordBoundaryCharsValue = newWordBoundaryChars;
    wordBoundaryChars = wordBoundaryCharsValue.replaceAll("\\p{Z}", "");
  }

  public String getWordChars() {
    return wordCharsValue;
  }

  @CreoleParameter(comment = "Additional word characters?", 
    defaultValue="")
  public void setWordChars(String newWordChars) {
    wordCharsValue = newWordChars;    
    wordChars = wordCharsValue.replaceAll("\\p{Z}", "");
  }

  @CreoleParameter(comment = "Non-editable and memory saving mode, also enables FSM re-use for multiple copies", defaultValue="false")
  public void setMemorySavingMode(Boolean yesno) {
    memorySavingMode = yesno;
  }
  public Boolean getMemorySavingMode() {
    return memorySavingMode;
  }
  protected Boolean memorySavingMode;

  @CreoleParameter(comment = "Canonicalize strings - save memory with many repeated feature value strings", defaultValue="false")
  public void setCanonicalizeStrings(Boolean yesno) {
    canonicalizeStrings = yesno;
  }
  public Boolean getCanonicalizeStrings() {
    return canonicalizeStrings;
  }
  protected Boolean canonicalizeStrings = false;

  @RunTime
  @Optional
  @CreoleParameter(comment = "Type name of output annotations to create. Overwrites the definition in the def file, if present!", 
    defaultValue = "")
  public void setAnnotationTypeName(String value) {
    annotationTypeName = value;
  }
  public String getAnnotationTypeName() {
    return annotationTypeName;
  }
  protected String annotationTypeName = "";
  
  // in the program, we use only this method to find the annotation type
  // name to assign: if the annotation type is set as a runtime parameter,
  // always use that, otherwise use the one we get from the gazetteer list
  // specification in the def file.
  public String getAnnotationTypeName(String defaultType) {
    if(annotationTypeName == null || annotationTypeName.isEmpty()) {
      return defaultType;
    } else {
      return annotationTypeName;
    }
  }
  
  
  @RunTime
  @Optional
  @CreoleParameter(comment = "Annotation setname.typename - only within these, the annotation will be done")
  public void setContainingAnnotationSpec(String spec) {
    containingAnnotationSpec = spec;
  }
  public String getContainingAnnotationSpec() {
    return containingAnnotationSpec;
  }
  protected String containingAnnotationSpec = null;
  protected String containingAnnotationType = null;
  protected String containingAnnotationSet = null;
  
  protected String unescapedSeparator = null;

  protected static Map<String,FSM> loadedGazetteers = new HashMap<String,FSM>();

  protected Logger logger;

  protected class FSM {
    public Set<FSMState> fsmStates;
    public FSMState initialState;
    public FSM(Set<FSMState> states, FSMState init) {
      fsmStates = states;
      initialState = init;
    }
    public int refcount = 0;
  }

  // TODO: for now, make this a parameter?
  protected Locale caseConversionLocale = Locale.ENGLISH;
  
  private static final String ws_chars = 
    "\\u0009" // CHARACTER TABULATION
                        + "\\u000A" // LINE FEED (LF)
                        + "\\u000B" // LINE TABULATION
                        + "\\u000C" // FORM FEED (FF)
                        + "\\u000D" // CARRIAGE RETURN (CR)
                        + "\\u0020" // SPACE
                        + "\\u0085" // NEXT LINE (NEL) 
                        + "\\u00A0" // NO-BREAK SPACE
                        + "\\u1680" // OGHAM SPACE MARK
                        + "\\u180E" // MONGOLIAN VOWEL SEPARATOR
                        + "\\u2000" // EN QUAD 
                        + "\\u2001" // EM QUAD 
                        + "\\u2002" // EN SPACE
                        + "\\u2003" // EM SPACE
                        + "\\u2004" // THREE-PER-EM SPACE
                        + "\\u2005" // FOUR-PER-EM SPACE
                        + "\\u2006" // SIX-PER-EM SPACE
                        + "\\u2007" // FIGURE SPACE
                        + "\\u2008" // PUNCTUATION SPACE
                        + "\\u2009" // THIN SPACE
                        + "\\u200A" // HAIR SPACE
                        + "\\u2028" // LINE SEPARATOR
                        + "\\u2029" // PARAGRAPH SEPARATOR
                        + "\\u202F" // NARROW NO-BREAK SPACE
                        + "\\u205F" // MEDIUM MATHEMATICAL SPACE
                        + "\\u3000" // IDEOGRAPHIC SPACE
                        ;        
  private static final String ws_class = "[" + ws_chars + "]";
  private static final String ws_patternstring = ws_class + "+";
  private static Pattern ws_pattern;
  
  /** 
   * Build a gazetteer using the default lists from the gate resources
   */
  public AbstractExtendedGazetteer() {
    super();
    logger = Logger.getLogger(this.getClass().getName());
  }

  @Override
  public Resource init() throws ResourceInstantiationException {
    // precompile the pattern used to replace all unicode whitespace in gazetteer
    // entries with a single space.
    ws_pattern = Pattern.compile(ws_patternstring);
    // silently remove all whitespace from the wordChars and also from 
    // wordBoundaryChars (because Whitespace will always be a word Boundary)
    //System.out.println("Running init!");
    if(memorySavingMode) {
      incrementFSM();
      return this;
    } else {
      //System.out.println("Default mode!!");
      return super.init();
    }
  }

  protected String genUniqueFSMKey() {
    return 
      "wcc="+wordCharsClass + 
      " wchs="+wordChars + " wbchs=" +wordBoundaryChars +  
      " cs="+caseSensitive + " url="+ listsURL;
  }

  @Override
  public void cleanup() {
    if(memorySavingMode) {
      decrementFSM();
    }
  }

  private synchronized void incrementFSM() throws ResourceInstantiationException {
    //System.out.println("Memory saving mode!");
    String uniqueFSMKey = genUniqueFSMKey();
    FSM fsm = loadedGazetteers.get(uniqueFSMKey);
    if(fsm != null) { 
      // The FSM for this file/parm combination already has been compiled, just
      // reuse it for this PR
      fsmStates = fsm.fsmStates;
      initialState = fsm.initialState;
      fsm.refcount++;
      logger.info("Reusing already generated FSM for "+uniqueFSMKey);
    } else {
      // The FSM for this file/parm combination does not exist yet, create it!
      this.fsmStates = new HashSet();
      initialState = new FSMState(this);
      if (gazetteerFeatureSeparator != null && !gazetteerFeatureSeparator.isEmpty()) {
        unescapedSeparator = Strings.unescape(gazetteerFeatureSeparator);
      } 
      try {
        loadData();
      } catch (Exception ex) {
        throw new ResourceInstantiationException("Could not initialize", ex);
      }
      fsm = new FSM(fsmStates,initialState);
      fsm.refcount++;
      loadedGazetteers.put(uniqueFSMKey, fsm);
      logger.info("New FMS loaded for "+uniqueFSMKey);
    }    
  }
  
  private synchronized void decrementFSM() {
    String key = genUniqueFSMKey();
    FSM fsm = loadedGazetteers.get(key);
    fsm.refcount--;
    if (fsm.refcount == 0) {
      loadedGazetteers.remove(key);
      logger.info("Removing FSM for "+key);
    }    
  }

  private synchronized void removeFSM() {
    String key = genUniqueFSMKey();
    loadedGazetteers.remove(key);
    logger.info("reInit(): force-removing FSM for "+key);
  }
  
  @Override
  /**
   * CAUTION: this should only be called when no other PR that may use the FSM is still
   * running in a different thread!
   * This will always force-recreate the FSM from the current LST file, no matter if the
   * FSM is used by another PR. 
   */
  public void reInit() throws ResourceInstantiationException {
    // the default for this from DefaultGazettteer is to simply do an init().
    // However, if we are using memory saving mode, a simple reInit() will not 
    // actually reload anything if the same filename and parms are used by another 
    // PR. 
    if(memorySavingMode) {
      removeFSM();
      init();
    } else {
      init();
    }
  }

  protected void loadData() throws UnsupportedEncodingException, IOException, ResourceInstantiationException  {
    BufferedReader defReader =
      new BomStrippingInputStreamReader((listsURL).openStream(), encoding);
    String line;
    //System.out.println("Loading data");
    while (null != (line = defReader.readLine())) {
       String[] fields = line.split(":");
       if(fields.length == 0) {
         System.err.println("Empty line in file "+listsURL);
       } else {
         String listFileName = "";
         String majorType = "";
         String minorType = "";
         String languages = "";
         String annotationType = ANNIEConstants.LOOKUP_ANNOTATION_TYPE;
         listFileName = fields[0];
         if(fields.length > 1) {
           majorType = fields[1];
         }
         if(fields.length > 2) {
           minorType = fields[2];
         }
         if(fields.length > 3) {
           languages = fields[3];
         }
         if(fields.length > 4) {
           annotationType = fields[4];
         }
         if(fields.length > 5) {
           defReader.close();
           throw new ResourceInstantiationException("Line has more that 5 fields in def file "+listsURL);
         }
         logger.debug("Reading from "+listFileName+", "+majorType+"/"+minorType+"/"+languages+"/"+annotationType);
         loadListFile(listFileName,majorType,minorType,languages,annotationType);
      }
    } //while
    defReader.close();
  }

  void loadListFile(String listFileName,String majorType,String minorType,
      String languages, String annotationType) 
  throws MalformedURLException, IOException {
    // create a completely new fsm
    // query SOLR
    //System.out.println("Loading list file "+listFileName);
    Lookup defaultLookup = new Lookup(listFileName, majorType, minorType, 
            languages, annotationType);
    URL lurl = new URL(listsURL,listFileName);
    BufferedReader listReader =
      new BomStrippingInputStreamReader(lurl.openStream(), encoding);
    String line;
    int lines = 0;
    while (null != (line = listReader.readLine())) {
      GazetteerNode node = new GazetteerNode(line, unescapedSeparator, false);
      Lookup lookup = defaultLookup;
      Map<String,String> fm = node.getFeatureMap();
      if(fm != null && fm.size() > 0) {
        lookup = new Lookup(listFileName, majorType, minorType, 
                languages, annotationType);
        // if the only keys we have in the map are one of majorType, minorType,
        // or language, then just overwrite the values in the new lookup
        // object, we wont need an extra new feature map for that!
        Set<String> keyset = fm.keySet();
        if(keyset.size() <= 4) {
          Map<String,String> newfm = null;
          for(String key : keyset) {
            if(key.equals("majorType")) {
              String tmp = fm.get("majorType");
              if(canonicalizeStrings) { tmp.intern(); }
              lookup.majorType = tmp;
            } else if(key.equals("minorType")) {
              String tmp = fm.get("minorType");
              if(canonicalizeStrings) { tmp.intern(); }
              lookup.minorType = tmp;
            } else if(key.equals("languages")) {
              String tmp = fm.get("languages");
              if(canonicalizeStrings) { tmp.intern(); }
              lookup.languages = tmp;
            } else if(key.equals("annotationType")) {
              String tmp = fm.get("annotationType");
              if(canonicalizeStrings) { tmp.intern(); }
              lookup.annotationType = tmp;
            } else {
              // some other key, we copy over to the new map
              if(newfm == null) {
                newfm = new HashMap<String,String>();
              }
              String tmp = fm.get(key);
              if(canonicalizeStrings) { tmp.intern(); }
              newfm.put(key, tmp);
            }
          }
          // if we had to allocate the map, store it
          if(newfm != null) {
            lookup.features = newfm;
          }
        } else {
          if(canonicalizeStrings) {
            for(String key : fm.keySet()) {
              String tmp = fm.get(key);
              tmp.intern();
              fm.put(key,tmp);
            }
          }
          lookup.features = fm;
        }
      }
      //System.out.print("adding lookup for node: "+node);
      addLookup(node.getEntry(), lookup);
      lines++;
    } // while
    logger.debug("Lines read: "+lines);
  }



  /**
   * Tests whether a character is internal to a word (i.e. if it's a letter or
   * a combining mark (spacing or not)).
   * @param ch the character to be tested
   * @return a boolean value
   */
  public boolean isWithinWord(char ch) {
    if (wordCharsClass == WCClass.LETTERS) {
      return Character.isLetter(ch);
    } else if (wordCharsClass == WCClass.LETTERSORHYPHEN) {
      return Character.isLetter(ch) || ch == '-';
    } else if (wordCharsClass == WCClass.DIGITS) {
      return Character.isDigit(ch);
    } else if (wordCharsClass == WCClass.LETTERSORDIGITS) {
      return (Character.isDigit(ch) || Character.isLetter(ch));
    }
    // or, if we have WC_NONWHITESPACE
    if (Character.getType(ch) == Character.COMBINING_SPACING_MARK
      || Character.getType(ch) == Character.NON_SPACING_MARK) {
      return true;
    }
    if (Character.isWhitespace(ch) || Character.isSpaceChar(ch)) {
      return false;
    }

    if (wordChars.indexOf(ch) != -1) {
      return true;
    }
    if (wordBoundaryChars.indexOf(ch) != -1) {
      return false;
    }

    return true;   
  }

   public void execute() throws ExecutionException{
    doExecute(document);
   }

   public void doExecute(Document theDocument) throws ExecutionException {
    interrupted = false;
    AnnotationSet annotationSet;
    //check the input
    if(theDocument == null) {
      throw new ExecutionException(
        "No document to process!"
      );
    }

    if(annotationSetName == null ||
       annotationSetName.equals("")) annotationSet = theDocument.getAnnotations();
    else annotationSet = theDocument.getAnnotations(annotationSetName);

    if(getContainingAnnotationSpec() == null || getContainingAnnotationSpec().isEmpty()) {
      containingAnnotationType = null;
      containingAnnotationSet = null;      
    } else {
      String[] set_type = containingAnnotationSpec.split("\\.",2);
      if(set_type.length == 1) {
        containingAnnotationSet = "";
        containingAnnotationType = set_type[0];
      } else if(set_type.length == 2) {
        containingAnnotationSet = set_type[0];
        containingAnnotationType = set_type[1];
      } else {
        throw new GateRuntimeException("ContaininAnnotationSpec not valid");
      }
    }
    
    
    fireStatusChanged("Performing look-up in " + theDocument.getName() + "...");
    if(containingAnnotationType == null) {
      String content = theDocument.getContent().toString();
      doAnnotateRegion(theDocument,content,content,0,annotationSet);
    } else {
      AnnotationSet containings = theDocument.getAnnotations(containingAnnotationSet);
      containings = containings.get(containingAnnotationType);
      for(Annotation containing : containings) {
        String content = theDocument.getContent().toString();
        String region = gate.Utils.stringFor(theDocument, containing);
        doAnnotateRegion(theDocument,content,region,
          containing.getStartNode().getOffset().intValue(),annotationSet);
      }
    }

    fireProcessFinished();
    fireStatusChanged("Look-up complete!");
  } // execute

   public void doAnnotateRegion(
     Document theDocument,
     String content, String region, 
     int regionOffset, AnnotationSet annotationSet) 
     throws ExecutionException{
    interrupted = false;
    int length = region.length();
    char currentChar;
    FSMState currentState = initialState;
    FSMState nextState;
    FSMState lastMatchingState = null;
    int matchedRegionEnd = 0;
    int matchedRegionStart = 0;
    int charIdx = 0;
    int oldCharIdx = 0;

    // we do prefix annotation when the parameter requires it
    // and when we either have wholeWordsOnly false, which means
    // we can annotate anywhere as part of a word, or if we 
    // have wholeWordsOnly true and in addition also annotate
    // word suffixes (which leaves us with a prefix).
    boolean doPrefixAnnotation =
      getPrefixAnnotations() && 
        (!wholeWordsOnly || getAllowSuffixMatches());
    // analogously for the suffix annotations
    boolean doSuffixAnnotation =
      getSuffixAnnotations() && 
        (!wholeWordsOnly || getAllowPrefixMatches());

    while(charIdx < length) {
      currentChar = region.charAt(charIdx);
      if(!isWithinWord(currentChar)) currentChar = ' ';
      else currentChar = caseSensitive.booleanValue() ?
                          currentChar :
                          Character.toUpperCase(currentChar);
      nextState = currentState.next(currentChar);
      if(nextState == null) {
        //the matching stopped
        //if we had a successful match then act on it;
        if(lastMatchingState != null){
          createLookups(theDocument,lastMatchingState, 
            matchedRegionStart+regionOffset, 
            matchedRegionEnd+regionOffset,
            annotationSet, doPrefixAnnotation, doSuffixAnnotation,
            content);
          lastMatchingState = null;
        }
        //reset the FSM
        charIdx = matchedRegionStart + 1;
        matchedRegionStart = charIdx;
        currentState = initialState;
      } else{//go on with the matching
        currentState = nextState;
        // if we have a successful state, i.e. an end state:
        // either we are matching just whole words, then make sure that 
        // the begginning of this match and the current end are at word boundaries;
        // or we do not just match whole words, then go on.
        if(currentState.isFinal() &&
           (
            // if !wholeWordsOnly, everything is allowed
            (!wholeWordsOnly)
             ||
            // if wholewordsonly, check start if suffix not allowed
            // and check end if prefix not allowed
            // In other words, either suffix is allowed or check start
            // and either prefix is allowed or check end
            (!allowSuffixMatches && !allowPrefixMatches &&
              (matchedRegionStart == 0 ||
               !isWithinWord(region.charAt(matchedRegionStart - 1))) &&
              (charIdx + 1 >= region.length()   ||
                !isWithinWord(region.charAt(charIdx + 1)))
            ) ||
            (!allowSuffixMatches && allowPrefixMatches &&
              (matchedRegionStart == 0 ||
               !isWithinWord(region.charAt(matchedRegionStart - 1))))
             ||
             (!allowPrefixMatches && allowSuffixMatches &&
              (charIdx + 1 >= region.length()   ||
               !isWithinWord(region.charAt(charIdx + 1))))
             || 
             (allowPrefixMatches && allowSuffixMatches &&
                ( ( (matchedRegionStart == 0 ||
                     !isWithinWord(region.charAt(matchedRegionStart - 1))) &&
                    (charIdx + 1 < region.length()   &&
                     isWithinWord(region.charAt(charIdx + 1)))) ||
                  ( (matchedRegionStart > 0 ||
                     isWithinWord(region.charAt(matchedRegionStart - 1))) &&
                    (charIdx + 1 >= region.length()   ||
                     !isWithinWord(region.charAt(charIdx + 1))))
                )                
             )
           )
          ){
            // no that we are in here we have one additional situation still
            // not checked: if wholeWordsOnly and disallowWhole we 
            // prevent the following, if we have indeed a whole word check.
            // that way we can create gazetteer runs where only prefixes and/or
            // suffixes are allowed
            if(wholeWordsOnly && disallowWholeMatches &&
               (matchedRegionStart == 0 ||
                !isWithinWord(region.charAt(matchedRegionStart - 1))) &&
               (charIdx + 1 >= region.length()   ||
                !isWithinWord(region.charAt(charIdx + 1)))) {
              // do not do anything !!!
            } else {
          //we have a new match
          // if there is a previous matching state to act upon and we do not
          // just annotate the longest match, then annotate that previous
          // match before updating the last matching state.
          // TODO: all matches here will share the prefix with the longest match
          // and all matches will have a suffix ... should we annotate those
          // seperately?
          // for now, yes we will add all the prefix and suffix annotations
          if(!longestMatchOnly && lastMatchingState != null){
            createLookups(theDocument,lastMatchingState, 
              matchedRegionStart+regionOffset,
              matchedRegionEnd+regionOffset, 
              annotationSet,
              doPrefixAnnotation, doSuffixAnnotation,
              content);
          }
          matchedRegionEnd = charIdx;
          lastMatchingState = currentState; 
            } // check for wholeWordsOnly, disallowWholeMatches and whole match
        }
        charIdx ++;
        if(charIdx == region.length()){
          //we can't go on, use the last matching state and restart matching
          //from the next char
          if(lastMatchingState != null){
            //let's add the new annotation(s)
            createLookups(theDocument,lastMatchingState, 
              matchedRegionStart+regionOffset,
              matchedRegionEnd+regionOffset,
              annotationSet,
              doPrefixAnnotation, doSuffixAnnotation,
              content);
            lastMatchingState = null;
          }
          //reset the FSM
          charIdx = matchedRegionStart + 1;
          matchedRegionStart = charIdx;
          currentState = initialState;
        }
      }
      //fire the progress event
      if(charIdx - oldCharIdx > 256) {
        fireProgressChanged((100 * charIdx )/ length );
        oldCharIdx = charIdx;
        if(isInterrupted()) throw new ExecutionInterruptedException(
            "The execution of the " + getName() +
            " gazetteer has been abruptly interrupted!");
      }
    } // while(charIdx < length)
    //we've finished. If we had a stored match, then apply it.
    if(lastMatchingState != null) {
      createLookups(theDocument,lastMatchingState, 
        matchedRegionStart+regionOffset,
        matchedRegionEnd+regionOffset, 
        annotationSet,
        doPrefixAnnotation, doSuffixAnnotation,
        content);
    }
    fireProcessFinished();
    fireStatusChanged("Look-up complete!");
  } // execute
  
  
  
  
  protected void createLookups(
    Document theDocument,
    FSMState matchingState, int matchedRegionStart,
          int matchedRegionEnd, AnnotationSet annotationSet,
          boolean doPrefixAnnotation, boolean doSuffixAnnotation,
          String content)
  {
    Iterator<Annotation> lookupIter = matchingState.getLookupSet().iterator();
    while(lookupIter.hasNext()) {
      Lookup currentLookup = (Lookup)lookupIter.next();
      FeatureMap fm = Factory.newFeatureMap();
      fm.put(LOOKUP_MAJOR_TYPE_FEATURE_NAME, currentLookup.majorType);
      if (null!= currentLookup.oClass && null!=currentLookup.ontology){
        fm.put(LOOKUP_CLASS_FEATURE_NAME,currentLookup.oClass);
        fm.put(LOOKUP_ONTOLOGY_FEATURE_NAME,currentLookup.ontology);
      }

      if(null != currentLookup.minorType)
        fm.put(LOOKUP_MINOR_TYPE_FEATURE_NAME, currentLookup.minorType);
      if(null != currentLookup.languages)
        fm.put(LOOKUP_LANGUAGE_FEATURE_NAME, currentLookup.languages);
      if(null != currentLookup.features) {
        fm.putAll(currentLookup.features);
      }
      // added features for ExtendedGazetteer
      fm.put("firstcharCategory", Character.getType(content.charAt(matchedRegionStart)));
      if (Character.isUpperCase(content.charAt(matchedRegionStart))) {
        fm.put("firstcharUpper", true);
      } else {
        fm.put("firstcharUpper", false);
      }
      boolean atEnd = false;
      boolean atBeginning = false;
      boolean isWholeWord = false;
      if (!isWithinWord(content.charAt(Math.min(content.length() - 1, matchedRegionEnd + 1)))) {
        fm.put("atEnd", true);
        atEnd = true;
      } else {
        fm.put("atEnd", false);
      }
      if (matchedRegionStart > 0 && isWithinWord(content.charAt(matchedRegionStart - 1))) {
        fm.put("atBeginning", false);
      } else {
        fm.put("atBeginning", true);
        atBeginning = true;
      }
      if(atBeginning && atEnd) { isWholeWord = true; }
      fm.put("wholeWord", isWholeWord);
      // this would not be necessary any more now that JAPE can handle the
      // @string metafeature but we keep it for backwards compatibility
      if (includeStrings) {
        fm.put("string", content.substring(matchedRegionStart, matchedRegionEnd + 1));
      }
      if (null != currentLookup.features) {
        fm.putAll(currentLookup.features);
      }
      Integer lookupid =
        addAnAnnotation(theDocument,annotationSet, matchedRegionStart, matchedRegionEnd+1,
          getAnnotationTypeName(currentLookup.annotationType), fm);
      // prefix annotation, if wanted: starting with the region start,
      // go back, if possible while we still have word characters and annotate
      // those. Link the prefix to the lookup annotation above.
      //
      if(doPrefixAnnotation) {
        int prefixLength = determinePrefixLength(matchedRegionStart,content);
        if(prefixLength > 0) {
          // Annotate from matchedRegionStart-prefixLength to matchedRegionStart
          int prefixFrom = matchedRegionStart-prefixLength;
          int prefixTo = matchedRegionStart;
          FeatureMap fmprefix = Factory.newFeatureMap();
          fmprefix.put(LOOKUP_MAJOR_TYPE_FEATURE_NAME, currentLookup.majorType);
          if (null != currentLookup.oClass && null != currentLookup.ontology) {
            fmprefix.put(LOOKUP_CLASS_FEATURE_NAME, currentLookup.oClass);
            fmprefix.put(LOOKUP_ONTOLOGY_FEATURE_NAME, currentLookup.ontology);
          }
          if (null != currentLookup.minorType) {
            fmprefix.put(LOOKUP_MINOR_TYPE_FEATURE_NAME, currentLookup.minorType);
            if (null != currentLookup.languages) {
              fmprefix.put("language", currentLookup.languages);
            }
          }
          fmprefix.put("firstcharCategory", Character.getType(content.charAt(prefixFrom)));
          if (Character.isUpperCase(content.charAt(prefixFrom))) {
            fmprefix.put("firstcharUpper", true);
          } else {
            fmprefix.put("firstcharUpper", false);
          }
          if (includeStrings) {
            fmprefix.put("string", content.substring(prefixFrom, prefixTo));
          }
          if (null != currentLookup.features) {
            fmprefix.putAll(currentLookup.features);
          }
          Integer prefid =
            addAnAnnotation(theDocument,annotationSet, prefixFrom, prefixTo, 
            getAnnotationTypeName(currentLookup.annotationType) + "_prefix", fmprefix);
          fm.put("prefix_id", prefid);
          fmprefix.put("lookup_id", lookupid);
        }
      }
      // suffix annotation, if wanted: starting with the character after the
      // region end, go forward, if possible, while we still have word
      // characters and annotate those. Link the suffix to the lookup annotation
      // above.
      if (doSuffixAnnotation) {
        int suffixLength = determineSuffixLength(matchedRegionEnd, content);
        if (suffixLength > 0) {
          // Annotate from matchedRegionEnd+1 to matchedRegionEnd+1+suffixlength
          int suffixFrom = matchedRegionEnd + 1;
          int suffixTo = matchedRegionEnd + 1 + suffixLength;
          FeatureMap fmsuffix = Factory.newFeatureMap();
          fmsuffix.put(LOOKUP_MAJOR_TYPE_FEATURE_NAME, currentLookup.majorType);
          if (null != currentLookup.oClass && null != currentLookup.ontology) {
            fmsuffix.put(LOOKUP_CLASS_FEATURE_NAME, currentLookup.oClass);
            fmsuffix.put(LOOKUP_ONTOLOGY_FEATURE_NAME, currentLookup.ontology);
          }
          if (null != currentLookup.minorType) {
            fmsuffix.put(LOOKUP_MINOR_TYPE_FEATURE_NAME, currentLookup.minorType);
            if (null != currentLookup.languages) {
              fmsuffix.put("language", currentLookup.languages);
            }
          }
          fmsuffix.put("string", content.substring(suffixFrom, suffixTo));
          if (null != currentLookup.features) {
            fmsuffix.putAll(currentLookup.features);
          }
          Integer sufid =
            addAnAnnotation(theDocument,annotationSet, suffixFrom, suffixTo,
            getAnnotationTypeName(currentLookup.annotationType) + "_suffix", fmsuffix);
          fm.put("suffix_id", sufid);
          fmsuffix.put("lookup_id", lookupid);
        }
      }
    }//while(lookupIter.hasNext())
  }

  // given the region start index of a lookup, find the length of any prefix,
  // or return 0 if no prefix is present.
  protected int determinePrefixLength(int regionStart, String content) {
    int length = 0;
    while( regionStart-length-1 >= 0 // as long as not crossing the beginning
      && isWithinWord(content.charAt(regionStart-length-1))
      )
      length++; 
    return length;
  }

  // helper method that adds an annotation to an annotation set and if the
  // annotationset is a AnnotationSetImpl (currently all are, but who knows ...),
  // returns the id of the annotation, otherwise null.
  // This allows to link prefix, suffix and lookup annotations by their IDs
  protected Integer addAnAnnotation(Document theDocument,
    AnnotationSet set, int from, int to, String type, FeatureMap fm) {
    Integer id = null;
    if (set instanceof AnnotationSetImpl) {
      AnnotationSetImpl setasannimpl = (AnnotationSetImpl) set;
      try {
        id = setasannimpl.add(new Long(from), new Long(to), type, fm);
      } catch (InvalidOffsetException ex) {
        throw new GateRuntimeException("Invalid offset exception - doclen/from/to="
          + theDocument.getContent().size() + "/" + from + "/" + to, ex);
      }
    } else {
      try {
        set.add(new Long(from), new Long(to), type, fm);
      } catch (InvalidOffsetException ex) {
        throw new GateRuntimeException("Invalid offset exception - doclen/from/to="
          + theDocument.getContent().size() + "/" + from + "/" + to + " / ", ex);
      }
    }
    return id;
  }


  // given the region end index of a lookup (the index of the last character
  // of the lookup) find the length of any suffix, or return 0 if no suffix
  // is present.
  protected int determineSuffixLength(int regionEnd, String content) {
    int length = 0;
    while( regionEnd+length+1 < content.length()
      && isWithinWord(content.charAt(regionEnd+length+1))
      )
      length++;
    return length;
  }




  @Override
  public void addLookup(String text, Lookup lookup) {
    // 1) instead of translating every character that is not within a word
    // on the fly when adding states, first normalize the text string and then
    // trim it. If the resulting word is empty, skip the whole processing because
    // the original consisted only of characters that are not word characters!
    // 2) if something remains and we want not exact case matching, convert
    // the whole string to both only upper and only lower case first, then
    // compare the lengths. If the lengths differ, add both in addition to
    // the original!
    
    int textLen = text.length();
    char[] textChars = new char[textLen];
    for(int i = 0; i < textLen; i++) {
      if(isWithinWord(text.charAt(i))) {
        textChars[i] = text.charAt(i);
      } else {
        textChars[i] = ' ';
      }
    }
    
    String textNormalized = new String(textChars).trim();
    // convert anything that is a sequence of whitespace to a single space
    // WAS: textNormalized = textNormalized.replaceAll("  +", " ");
    textNormalized = ws_pattern.matcher(textNormalized).replaceAll(" ");
    if(textNormalized.isEmpty()) {
      //System.out.println("Ignoring, is empty");
      return;
    }
    
    // TODO: at some point this should get changed to allow for both totally
    // ignoring case (as now) and for matching either the original or a 
    // case-normalization (in the runtime). This would also need a setting
    // for specifying what the case normalization should be (e.g. UPPERCASE).
    
    // For now, we always normalize to upper case when case is ignored. 
    // The gazetteer should contain lowercase or firstCaseUpper words, but
    // better not ALLCAPS in order for lower case characters which get mapped
    // to two characters in uppercase to be mapped correctly.
    // For these special cases, we add two UPPERCASE normalizations:
    // the one with the two characters and the one where the char.toUpperCase 
    // is used.
    if(!caseSensitive) {
      String textNormalizedUpper = textNormalized.toUpperCase(caseConversionLocale);
      if(textNormalizedUpper.length() != textNormalized.length()) {
        actualAddLookup(textNormalizedUpper, lookup);
        char[] textChars2 = new char[textNormalized.length()];
        for(int i=0; i<textNormalized.length(); i++) {
          textChars2[i] = Character.toUpperCase(textNormalized.charAt(i));
        }
        actualAddLookup(new String(textChars2), lookup);
      } else {
        // if both version are of the same length, it is sufficient to add the 
        // upper case version
        actualAddLookup(textNormalizedUpper, lookup);
      }
    } else {
      actualAddLookup(textNormalized, lookup);
    }
    

  } // addLookup

  public void actualAddLookup(String text, Lookup lookup) {
    char currentChar;
    FSMState currentState = initialState;
    FSMState nextState;
    Lookup oldLookup;

    //System.out.println("Adding "+text+"|"+lookup);
    
    for(int i = 0; i< text.length(); i++) {
        currentChar = text.charAt(i);
      nextState = currentState.next(currentChar);
      if(nextState == null){
        nextState = new FSMState(this);
        currentState.put(currentChar, nextState);
        if(currentChar == ' ') nextState.put(' ',nextState);
      }
      currentState = nextState;
    } //for(int i = 0; i< text.length(); i++)

    currentState.addLookup(lookup);
    //System.out.println("text=>"+text + "<, " + lookup.majorType + "|" + lookup.minorType);

  } // addLookup

  /**
   * Our way to duplicate the PR differs from how DefaultGazetteer, from which we inherit
   * does it, so this method overrides the inherited behavior, we simply create a new instance
   * of the gazetteer, FSM sharing will be done automatically if memory saving mode is 
   * true.
   */
  @Override
  public Resource duplicate(Factory.DuplicationContext ctx)
      throws ResourceInstantiationException {
    return Factory.defaultDuplicate(this, ctx);
  }
  

} // ExtendedGazetteer

