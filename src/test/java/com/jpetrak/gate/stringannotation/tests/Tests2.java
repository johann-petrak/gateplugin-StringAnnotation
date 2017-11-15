/*
 * Copyright (c) 2010- Austrian Research Institute for Artificial Intelligence (OFAI). 
 * Copyright (C) 2014-2016 The University of Sheffield.
 *
 * This file is part of gateplugin-ModularPipelines
 * (see https://github.com/johann-petrak/gateplugin-ModularPipelines)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jpetrak.gate.stringannotation.tests;

import static org.junit.Assert.assertTrue;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jpetrak.gate.stringannotation.extendedgazetteer.ExtendedGazetteer;
import com.jpetrak.gate.stringannotation.extendedgazetteer.trie.GazStoreTrie3;

public class Tests2 {

  private static boolean isInitialized = false; 
  private static File testingDir;
  @BeforeClass
  public static void init() throws GateException, MalformedURLException {
    if(!isInitialized) {
      System.out.println("Tests2: Inititalizing ...");
      isInitialized = true;
      Gate.runInSandbox(true);
      Gate.init();
      File pluginHome = new File(".");
      System.out.println("Plugin home directory is "+pluginHome.getAbsolutePath());
      Gate.getCreoleRegister().registerDirectories(
              pluginHome.toURI().toURL());
      testingDir = new File(pluginHome,"tests");
      assertTrue("Directory 'tests' does not exist",testingDir.exists());
    } else {
      System.out.println("Already initialized ...");
    }
  }
  
  @AfterClass
  public static void cleanup() throws Exception {
    System.out.println("Tests2: Cleaning up ...");
  }
  

  
  @Test
  public void testGazetteerApplicationBig_BE3() 
      throws ResourceInstantiationException, ExecutionException, IOException {
    System.out.println("Running big gazetteer application test");
    FeatureMap parms = Factory.newFeatureMap();
    File defFile = new File(testingDir,"pref_en_500K.def");
    URL gazURL = defFile.toURI().toURL();
    parms.put("configFileURL", gazURL);
    System.gc();
    long before = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
    System.out.println("Memory used before loading gazetteer: "+before);
    long startTime = System.currentTimeMillis();
    ExtendedGazetteer eg = (ExtendedGazetteer)Factory.createResource(
            "com.jpetrak.gate.stringannotation.extendedgazetteer.ExtendedGazetteer", parms);
    long endTime = System.currentTimeMillis();
    System.gc();
    long after = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
    System.out.println("Memory used after loading gazetteer: "+after);
    System.out.println("Elapsed time: "+((endTime-startTime)/1000.0));
    System.out.println("Memory used up in between: "+(after-before));
    System.out.println("Saving to test-big.gazbin");
    File save = new File("test-big.gazbin");
    eg.save(save);
    Factory.deleteResource(eg);
    eg = null;
    System.gc();
    System.out.println("Saving completed, trying to load into a new gaz store");
    before = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
    startTime = System.currentTimeMillis();
    GazStoreTrie3 gs = new GazStoreTrie3();
    gs = (GazStoreTrie3)gs.load(save.toURI().toURL());
    endTime = System.currentTimeMillis();
    after = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
    System.out.println("Loading completed");
    System.out.println("Elapsed time for cache loading: "+((endTime-startTime)/1000.0));
    System.out.println("Memory used up for cache loading: "+(after-before));
    System.out.println("Big gazetteer application test finished");
  }


}
