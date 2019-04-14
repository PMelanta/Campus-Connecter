/*
 * UFileUploadContext.java
 *
 * Created on September 21, 2006, 5:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cc.util;
import cc.base.URequestContext;
import java.sql.*;
import java.util.*;
import java.text.*;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

/**
 *
 * @author suhas
 */
public class UFileUploadContext extends URequestContext{
  
  static Logger logger = Logger.getLogger(UFileUploadContext.class);
  
  private Vector ctxtElemVector = null;
  
  private HashMap ctxtElemMap = null;
  
  /** Creates a new instance of UFileUploadContext */
    /*
  public UFileUploadContext(URequestContext ctxt , Vector ctxtData) {
        super(ctxt.getHttpRequest(),ctxt.getHttpResponse());
        
        ctxtElemVector = ctxtData;
    }
  
  public String getParameter(String paramName){
    UCtxtElement tempElement = null ;
    for(int i = 0; i<ctxtElemVector.size(); i++){
      tempElement = (UCtxtElement) ctxtElemVector.get(i) ;
      if(tempElement.elemName.equals(paramName))
        return tempElement.elemValue ;
    }
    return null ;
  }
    */

  public UFileUploadContext(URequestContext ctxt , HashMap ctxtData) {
        super(ctxt.getHttpRequest(),ctxt.getHttpResponse());
        
        ctxtElemMap = ctxtData;
    }
  
  public String getParameter(String paramName){
      
      String pval = (String) ctxtElemMap.get(paramName);
      
      if(pval == null) {
          Object obj = getAttribute(paramName);
          
          pval = obj != null ? obj.toString() : null;
      }
      
      return pval;
  }
  
    public int getIntParameter(String paramName) {

	String paramValue = getParameter(paramName);

	if(paramValue != null && !paramValue.equals("")) {
	    paramValue = paramValue.trim();

	    return Integer.parseInt(paramValue);
	} else
	    return 0;
    }

    public float getFloatParameter(String paramName) {

	String paramValue = getParameter(paramName);

	if(paramValue != null && !paramValue.equals("")) {
	    paramValue = paramValue.trim();

	    return Float.valueOf(paramValue).floatValue();
	} else
	    return 0;
    }

    public int getCheckboxParameter(String paramName) {

	String paramValue = getParameter(paramName);

	if(paramValue == null)
	    return 0;
	else
	    return 1;
    }

    public java.sql.Date getDateParameter(String paramName) {

	String paramValue = getParameter(paramName);
        if (paramValue== null)
            return null;

	java.sql.Date d = null;

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	try {
	    return new java.sql.Date(sdf.parse(paramValue).getTime());
	} catch (ParseException e) {
	    return d;
	}
    }
  
  public String[] getParameterValues(String paramName){

        if(getParameter(paramName) != null) {
          
          //String [] paramNames = (getParameter(paramName)).split(new String("`"));
          String [] paramNames = UString.split(getParameter(paramName),'`');
	  return paramNames;
        } else {
          return null;            
        }  
    }
   
   public int[] getIntParameterValues(String paramName){
               
          if(getParameter(paramName) != null) {
           String [] values = (getParameter(paramName)).split("`");
           
           int[] intValues = new int[values.length];
           for(int i = 0; i < intValues.length; i++) {
               if(!"".equals(values[i].trim())) {
            intValues[i] = Integer.parseInt(values[i].trim());
            } else {
                intValues[i] = 0;
            }
           }
           return intValues;
       } else
           return null;
    }   
   
    /*
  public String[] getParameterValues(String paramName){
    UCtxtElement tempElement = null ;
    String tempStr = "";
    for(int i = 0; i<ctxtElemVector.size(); i++){
      tempElement = (UCtxtElement) ctxtElemVector.get(i) ;
      if(tempElement.elemName.equals(paramName))
        tempStr = tempStr + tempElement.elemValue + "~" ;
    }
    tempStr = tempStr.substring(0,tempStr.length()) ;
    return tempStr.split("~") ;
  }
    */
}
