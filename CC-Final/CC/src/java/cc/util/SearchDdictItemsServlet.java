/*
 * SearchDdictItemsServlet.java
 *
 * Created on May 16, 2006, 4:28 PM
 */


package cc.util;
import cc.base.UServletHelper;
import cc.base.URequestContext;
import cc.base.UHttpServlet;
import cc.base.UServletException;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;


/**
 *
 * @author Gopinath P.S
 * @version
 */

public class SearchDdictItemsServlet extends UHttpServlet {
    
    static Logger logger = Logger.getLogger(SearchDdictItemsServlet.class) ;
    
    public void handleGet(URequestContext ctxt)
    throws UServletException {
        handlePost(ctxt) ;
    }
    
    public void handlePost(URequestContext ctxt)
    throws UServletException {
        try{
            ResultSet rsSupType = null ;
            
            int itemStatus = ctxt.getIntParameter("itemStatus");
            
            if(itemStatus == 1) {
                
                rsSupType = DataDictionaryManager.searchValidDdictItem(ctxt, ctxt.getParameter("ddict_item_name1"), ctxt.getParameter("searchTxt"));
                
                
            } else {
                //logger.debug(ctxt.getParameter("ddict_item_name")) ;        

                //logger.debug(ctxt.getParameter("searchTxt")) ;
                if(ctxt.getParameter("searchTxt") == null)
                    rsSupType = DataDictionaryManager.getDdictItem(ctxt, ctxt.getParameter("ddict_item_name"));
                else{
                    rsSupType = DataDictionaryManager.searchDdictItem(ctxt, ctxt.getParameter("ddict_item_name"), ctxt.getParameter("searchTxt"));
                }
            
            }
            if(rsSupType != null){
                if(rsSupType.first()){
                    //logger.debug("result set not null...");
                    rsSupType.beforeFirst() ;
                    ctxt.setAttribute("supTypeRs", rsSupType);
                    if(ctxt.getParameter("nextPage") == null)
                        UServletHelper.sendJSP(ctxt,servletConfig,"taxMasterResultsPage") ;
                    else
                        UServletHelper.sendJSP(ctxt,servletConfig,ctxt.getParameter("nextPage")) ;
                } else{
                    ctxt.setAttribute("errorStr","Search did not result any entries") ;
                  
                }
            }
         }
        catch (Exception e){
            throw new UServletException(e.getMessage(),e) ;
        }
    }
    
}
