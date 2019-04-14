/*
 * InsertUpdateRows.java
 *
 * Created on May 19, 2006, 4:35 PM
 */

package cc.util;

import cc.base.UServletHelper;
import cc.base.URequestContext;
import cc.base.UHttpServlet;
import cc.base.UQueryEngine;
import cc.base.UServletException;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;



 
public class InsertUpdateRows extends UHttpServlet {
    
    static Logger logger = Logger.getLogger(InsertUpdateRows.class) ;
    
    public void handleGet(URequestContext ctxt)
    throws UServletException {
        handlePost(ctxt) ;
        
    }
    private void PriceListReport(URequestContext ctxt )
    throws UServletException {
        try {
            java.util.Map param = new HashMap();
            
            param.put("entityrid", Integer.toString(ctxt.getUserEntityRID()));
            param.put("userName", ctxt.getUserName());
            param.put("reportHeader", "AW Price List");
        
                   
        } catch (Exception e) {
            throw new UServletException(e.toString());
        }
    }
    public void handlePost(URequestContext ctxt)
    throws UServletException {
        try{
            String victimTable = ctxt.getParameter("victimTable") ;
            UQueryEngine qe = ctxt.getQueryEngine() ;
            try {
                // added by sunil
                if(victimTable.equals("dosprint")){
                    logger.debug("printing price list...");
                    PriceListReport(ctxt);
                }
                // end
                qe.beginTransaction() ;
               
                if(victimTable.equals("u_ddict")){
                    logger.debug("calling Data dictionary manager...");
                    DataDictionaryManager.insertUpdateRow(ctxt, false);
                }
                ctxt.setAttribute("successMessage","Database Updated Successfully") ;
                qe.commitTransaction() ;
                UServletHelper.sendJSP(ctxt,servletConfig,"DataEntryResponse") ;
            } catch (Exception e){
                ctxt.setAttribute("errorMessage",e.getMessage()) ;
                qe.rollbackTransaction() ;
                UServletHelper.sendJSP(ctxt,servletConfig,"DataEntryResponse") ;
                throw new UServletException(e.getMessage(),e) ;
            }
        } catch (Exception e){
            throw new UServletException(e.getMessage(),e) ;
        }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
    }
    
    
}
