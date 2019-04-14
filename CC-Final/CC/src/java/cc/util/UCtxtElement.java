/*
 * UCtxtElement.java
 *
 * Created on September 21, 2006, 6:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cc.util;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

/**
 *
 * @author Gopinath
 */
public class UCtxtElement {
  static Logger logger = Logger.getLogger(Class.class);
  public String elemName ;
  public String elemValue ;
  /** Creates a new instance of UCtxtElement */
  public UCtxtElement(String name, String value) {
    elemName = name ;
    elemValue = value ;
  }
  
}
