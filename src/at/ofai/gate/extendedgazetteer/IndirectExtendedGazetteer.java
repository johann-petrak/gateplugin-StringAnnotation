/*
 * IndirectExtendedGazeteer.java
 * 
 * $Id: IndirectExtendedGazetteer.java  $
 *
 */
package at.ofai.gate.extendedgazetteer;

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

import at.ofai.gate.virtualdocuments.AnnotatedDocumentTransformer;
import java.util.*;

import gate.*;
import gate.corpora.DocumentImpl;
import gate.creole.*;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.HiddenCreoleParameter;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.*;
import javax.naming.InvalidNameException;


/**
 *  See documentation in the wiki:
 * http://code.google.com/p/gateplugin-stringannotation/wiki/IndirectExtendedGazetteerOld
 *
 *  @author Johann Petrak
 */
@CreoleResource(
  name = "Indirect Extended List Gazetteer OLD",
  comment = "The Extended List Gazetteer running on a virtual document according to an annotation specification",
  icon="shefGazetteer.gif",
  helpURL="http://code.google.com/p/gateplugin-stringannotation/wiki/IndirectExtendedGazetteerOld"  
)
public class IndirectExtendedGazetteer
  extends AbstractExtendedGazetteer 
  implements LanguageAnalyser, ControllerAwarePR {
  private static final long serialVersionUID = 1L;

  @RunTime
  @Optional
  @CreoleParameter(
    comment = "The input annotation set for which to process the specifications",
  defaultValue = "")
  public void setInputAnnotationSetName(String ias) {
    this.inputAnnotationSetName = ias;
  }
  public String getInputAnnotationSetName() {
    return inputAnnotationSetName;
  }
  private String inputAnnotationSetName = "";
  
  
  @RunTime
  @CreoleParameter(comment = "A list of annotation specifications",
		  defaultValue = "")
  public void setAnnotationSpecifications(List<String> ss) {
    this.annotationSpecifications = ss;
  }  
  public List<String> getAnnotationSpecifications() {
    return annotationSpecifications;
  }
  private List<String> annotationSpecifications;
  
  @RunTime
  @CreoleParameter(comment = "Keep the virtual document(s) for debugging",
		  defaultValue = "false")
  public void setDebug(Boolean parm) {
    debug = parm;
  }  
  public Boolean getDebug() {
    return debug;
  }
  private Boolean debug = false;
  
  @RunTime
  @CreoleParameter(comment = "Insert a single blank space between what is selected according to the annotation specifications",
		  defaultValue = "true")
  public void setInsertSpace(Boolean parm) {
    insertSpace = parm;
  }  
  public Boolean getInsertSpace() {
    return insertSpace;
  }
  private Boolean insertSpace = true;
  

  @HiddenCreoleParameter
  @Override
  public void setContainingAnnotationSpec(String spec) {
    // just here to hide the parameter
  }
  
  
  
  private FeatureMap processingOptions = Factory.newFeatureMap();
  
  protected AnnotatedDocumentTransformer annotatedDocumentTransformer = null;
  
  @Override
  public void execute() throws ExecutionException {
    if(corpus == null) { startup(); }
    fireStatusChanged("IndirectLanguageAnalyserPR processing: "
            + getDocument().getName());


    if (!(document instanceof DocumentImpl)) {
      throw new GateRuntimeException("Can only handle DocumentImpl not " + 
    		  document.getClass());
    }
    String newText = annotatedDocumentTransformer.getStringForDocument(
            getDocument(), inputAnnotationSetName);
    FeatureMap theparms = Factory.newFeatureMap();
    theparms.put("encoding", ((DocumentImpl) document).getEncoding());
    theparms.put("stringContent", newText);
    FeatureMap thefeats = Factory.newFeatureMap();
    FeatureMap docfeats = document.getFeatures();
    thefeats.putAll(docfeats);

    String theName = document.getName();
    // create a copy of the current document
    Document newDoc;
    try {
      newDoc = (Document) Factory.createResource(
              "gate.corpora.DocumentImpl",
              theparms,
              thefeats,
              theName+"_virtual");
    } catch (ResourceInstantiationException ex) {
      throw new GateRuntimeException(ex);
    }

    doExecute(newDoc);

    List<String> effectiveMapFromAnnsetNames = new ArrayList<String>();
    if(getAnnotationSetName() == null) {
      effectiveMapFromAnnsetNames.add("");      
    } else {
      effectiveMapFromAnnsetNames.add(getAnnotationSetName());
    }
    //System.out.println("Mapping back annotations from "+effectiveMapFromAnnsetNames);
    annotatedDocumentTransformer.addBackMappedAnnotations(
              document, newDoc,
              effectiveMapFromAnnsetNames);
    

    if(!debug) {
      Factory.deleteResource(newDoc);
    }
    fireStatusChanged("IndirectExtendedGazetteer completed");

  }    
  

  @Override
  public void controllerExecutionAborted(Controller arg0, Throwable arg1)
    throws ExecutionException {
  }

  @Override
  public void controllerExecutionFinished(Controller arg0)
    throws ExecutionException {
  }

  @Override
  public void controllerExecutionStarted(Controller arg0)
    throws ExecutionException {
    startup();
  }

  public void startup() throws ExecutionException {
    if (getAnnotationSpecifications() == null || getAnnotationSpecifications().size() == 0) {
      throw new ExecutionException("SourceSpecifications must not be empty");
    }
    if(insertSpace) {
      processingOptions.put("separator"," ");
    } else {
      if(processingOptions.containsKey("separator")) {
        processingOptions.remove("separator");
      }
    }
    try {
      annotatedDocumentTransformer =
        new AnnotatedDocumentTransformer(
        getAnnotationSpecifications(), processingOptions,
        false, true);
    } catch (InvalidNameException ex) {
      throw new ExecutionException(ex);
    }

  }

} // ExtendedGazetteer

