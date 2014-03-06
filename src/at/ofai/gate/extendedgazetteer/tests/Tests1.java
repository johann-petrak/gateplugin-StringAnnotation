package at.ofai.gate.extendedgazetteer.tests;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.creole.ontology.Ontology;
import gate.util.GateException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ofai.gate.extendedgazetteer.ExtendedGazetteer;


import static org.junit.Assert.*;

public class Tests1 {

  private static File tmpDir;
  private static File testingDir;
  private static boolean isInitialized = false; 
  
  @BeforeClass
  public static void init() throws GateException, MalformedURLException {
    if(!isInitialized) {
    System.out.println("Inititalizing ...");
    Gate.init();
    File pluginHome =
        new File(".");
    System.out.println("Plugin home directory is "+pluginHome.getAbsolutePath());
    Gate.getCreoleRegister().registerDirectories(
            pluginHome.toURI().toURL());
    testingDir = new File(pluginHome,"tests");
    assertTrue(testingDir.exists());
    tmpDir = getUniqueTmpDir();
    assertTrue(tmpDir.canWrite());
    System.out.println("Init complete");
    } else {
      isInitialized = true;
    }
  }
  
  @AfterClass
  public static void cleanup() throws Exception {
    if(tmpDir != null) {
      FileUtils.deleteDirectory(tmpDir);
    }
  }
  

  @Test
  public void test01() throws ResourceInstantiationException, MalformedURLException {
    // create an instance of the ExtendedGazetteer
    FeatureMap fm = Factory.newFeatureMap();
    File defFile = new File(testingDir,"extgaz1.def");
    URL defURL = defFile.toURI().toURL();
    fm.put("listsURL", defURL);
    ExtendedGazetteer gaz = (ExtendedGazetteer)Factory.createResource(
            "at.ofai.gate.extendedgazetteer.ExtendedGazetteer", fm);
    String origclass = gaz.getClass().getName();
    ExtendedGazetteer dup = (ExtendedGazetteer)Factory.duplicate(gaz);
    assertEquals(origclass, dup.getClass().getName());
  }
  
  @Test
  public void test02() throws ResourceInstantiationException, MalformedURLException {
    // create an instance of the ExtendedGazetteer 
    FeatureMap fm = Factory.newFeatureMap();
    File defFile = new File(testingDir,"extgaz1.def");
    URL defURL = defFile.toURI().toURL();
    fm.put("listsURL", defURL);
    ExtendedGazetteer gaz = (ExtendedGazetteer)Factory.createResource(
            "at.ofai.gate.extendedgazetteer.ExtendedGazetteer", fm);
    String origclass = gaz.getClass().getName();
    // create a serial corpus controller and add the PR
    SerialAnalyserController cont = 
        (SerialAnalyserController)
        Factory.createResource(
            "gate.creole.SerialAnalyserController",
            Factory.newFeatureMap(),
            Factory.newFeatureMap(),
            "TestController");
    cont.add(gaz);
    // duplicate the controller and check what class the duplicated PR has 
    SerialAnalyserController dup = (SerialAnalyserController)Factory.duplicate(cont);
    ProcessingResource res = (ProcessingResource)(dup.getPRs()).iterator().next();
    assertEquals(origclass, res.getClass().getName());
  }

  
  protected static File getUniqueTmpDir() {
    String tmplocation = System.getProperty("run.java.io.tmpdir");
    if(tmplocation == null) {
      tmplocation = System.getProperty("java.io.tmpdir");
    }
    if(tmplocation == null) {
      tmplocation = "/tmp";
    }
    File tmpdir = new File(tmplocation);
    if(!tmpdir.exists()) {
       System.err.println("Temp dir does not exist: "+tmpdir.getAbsolutePath());
       System.exit(1);
    }
    String tmpString = Long.toString(System.currentTimeMillis(),36);
    File uniqTmpDir = new File(tmpdir,"gate-towlim-"+tmpString);
    uniqTmpDir.mkdir();
    return uniqTmpDir;
  }


}
