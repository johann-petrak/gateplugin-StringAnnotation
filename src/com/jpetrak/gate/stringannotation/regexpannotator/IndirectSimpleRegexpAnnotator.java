/*
 *  SimpleRegexpAnnotator.java
 *
 *  $Id: SimpleRegexpAnnotator.java  $
 *
 */
package com.jpetrak.gate.stringannotation.regexpannotator;


import at.ofai.gate.virtualdocuments.AnnotatedDocumentTransformer;
import gate.*;
import gate.corpora.DocumentImpl;
import gate.creole.ControllerAwarePR;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.*;
import gate.util.GateRuntimeException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InvalidNameException;


/** 
 * See online wiki page:
 * http://code.google.com/p/gateplugin-stringannotation/wiki/SimpleRegexpAnnotator
 *
 * @author Johann Petrak
 */
@CreoleResource(name = "Indirect Simple Regexp Annotator",
helpURL="http://code.google.com/p/gateplugin-stringannotation/wiki/IndirectSimpleRegexpAnnotator",
comment = "Create annotations based on Java regular expressions on a virtual document created from annotation specifications")
public class IndirectSimpleRegexpAnnotator 
  extends AbstractSimpleRegexpAnnotator
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
  

  //@HiddenCreoleParameter
  //@Override
  //public void setContainingAnnotationSpec(String spec) {
    // just here to hide the parameter from the base class in case we
    // implement this feature some time!
  //}
  
  
  
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
    if(getOutputASName() == null) {
      effectiveMapFromAnnsetNames.add("");      
    } else {
      effectiveMapFromAnnsetNames.add(getOutputASName());
    }
    //System.out.println("Mapping back annotations from "+effectiveMapFromAnnsetNames);
    annotatedDocumentTransformer.addBackMappedAnnotations(
              document, newDoc,
              effectiveMapFromAnnsetNames);
    

    if(!debug) {
      Factory.deleteResource(newDoc);
    }
    fireStatusChanged("IndirectSimpleRegexpAnnotator completed");

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
    if (getAnnotationSpecifications() == null || getAnnotationSpecifications().isEmpty()) {
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
  
} // class SimpleRegexpAnnotator

