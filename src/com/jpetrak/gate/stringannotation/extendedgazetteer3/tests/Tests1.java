package com.jpetrak.gate.stringannotation.extendedgazetteer3.tests;

import static org.junit.Assert.*;
import gate.util.GateException;

import java.net.MalformedURLException;

import org.junit.*;

import com.jpetrak.gate.stringannotation.extendedgazetteer3.stores.StoreArrayOfCharArrays;

public class Tests1 {

  private static boolean isInitialized = false; 
  
  @BeforeClass
  public static void init() throws GateException, MalformedURLException {
    if(!isInitialized) {
      System.out.println("Inititalizing ...");
      isInitialized = true;
    } else {
      System.out.println("Already initialized ...");
    }
  }
  
  @AfterClass
  public static void cleanup() throws Exception {
    System.out.println("Cleaning up ...");
  }
  

  @Test
  public void test01()  {
    StoreArrayOfCharArrays as = new StoreArrayOfCharArrays();
    
    int i;
    char[] r;
    i = as.addData("asdfjk".toCharArray());
    assertEquals(0,i);
    i = as.addData("qwertyqwerty".toCharArray());
    assertEquals(8,i);
    r = as.getData(0);
    assertEquals("asdfjk",new String(r));
    r = as.getData(8);
    assertEquals("qwertyqwerty",new String(r));
    
    // test the list methods
    i = as.addListData("l1:4567890".toCharArray());
    assertEquals(22,i);
    int size = as.getListSize(i);
    assertEquals(1,size);
    r = as.getListData(i, 0);
    assertEquals("l1:4567890",new String(r));
    
    i = as.addListData(i,"l2:4567890".toCharArray());
    assertEquals(22,i);
    size = as.getListSize(i);
    assertEquals(2,size);
    r = as.getListData(i,1);
    assertEquals("l2:4567890",new String(r));
    
    i = as.addListData(i,"l3:4567890".toCharArray());
    assertEquals(22,i);
    size = as.getListSize(i);
    assertEquals(3,size);
    r = as.getListData(i,2);
    assertEquals("l3:4567890",new String(r));
    
    i = as.addListData("another".toCharArray());
    size = as.getListSize(i);
    assertEquals(1,size);
    r = as.getListData(i,0);
    assertEquals("another",new String(r));
  }
  

}
