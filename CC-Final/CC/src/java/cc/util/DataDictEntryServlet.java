package cc.util;


import cc.base.UDataDictionary;
import cc.base.UServletHelper;
import cc.base.URequestContext;
import cc.base.UHttpServlet;
import cc.base.UWriterHelper;
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

/*
 *
 * @author 
 * @version
 */
public class DataDictEntryServlet extends UHttpServlet {
    
    static Logger logger = Logger.getLogger(DataDictEntryServlet.class) ;
    
    private void getNestedDDEntry(URequestContext ctxt)
    throws SQLException,Exception{
        
        if("true".equalsIgnoreCase(ctxt.getParameter("entitySpecific", ""))) {
            int parent_ddi_index = ctxt.getIntParameter("parentDdiTypeIndex");
            ResultSet rs = UDataDictionary.getDDItems(ctxt, parent_ddi_index, 0, ctxt.getUserEntityRID());
            ctxt.setAttribute("rsArray", rs);
        } else {
            ResultSet rs = DataDictionaryManager.getNDDItems(ctxt);
            ctxt.setAttribute("rsArray", rs);
        }
        
        UServletHelper.sendJSP(ctxt, servletConfig, "NestedDDEntryJSP");
        
    }
   private void _getVisitReasonDetails(URequestContext ctxt)
    throws Exception {
        int apptBookType = 0;
        String serviceName = "";
        int serviceRID = 0;
        int ddIndex = ctxt.getIntParameter("ddIndex");
        UQueryEngine qe = ctxt.getQueryEngine();
        ResultSet rs = null;
        String sql = "";
        sql = " select visitx_appt_book_type,ser_name,visitx_service_rid FROM u_visit_reason_ext " +
                "JOIN u_service_m ON ser_rid = visitx_service_rid " +
                "WHERE visitx_dd_index = "+ ddIndex;

       rs = qe.executeQuery(sql);
       if(rs != null && rs.next()) {
            apptBookType = rs.getInt("visitx_appt_book_type");
            serviceName = rs.getString("ser_name");
            serviceRID = rs.getInt("visitx_service_rid");
       } 
       String result = serviceName + "~" + apptBookType + "~"+ serviceRID;
       UWriterHelper.sendResponse(ctxt,result);
    }
    
    private void getDDEntry(URequestContext ctxt) throws UServletException {
        
        UServletHelper.sendJSP(ctxt, servletConfig, "DDEntryJSP");
        
    }
    
    private void _getVisitReason(URequestContext ctxt) throws UServletException {
        
        UServletHelper.sendJSP(ctxt, servletConfig, "VisitReasonJSP");
        
    }
    
    
    public void handleGet(URequestContext ctxt) throws UServletException {
        try {
            
            String command = ctxt.getParameter("command");
            
            if("NestedDDEntry".equals(command))
                getNestedDDEntry(ctxt);
            else if("DDEntry".equals(command))
                getDDEntry(ctxt);
            else if("visitReason".equals(command)) {
                _getVisitReason(ctxt);
            } else if ("loadvisitReasonDetail".equals(command)) {
                _getVisitReasonDetails(ctxt);
            }
            
        } catch (Exception e) {
            logger.error(e.toString());
            throw new UServletException(e.getMessage(), e);
        }
    }
    
    
    public void handlePost(URequestContext ctxt)
    throws UServletException {
        UQueryEngine qe = null ;
        try{
            qe = ctxt.getQueryEngine() ;
            qe.beginTransaction() ;
            String command = ctxt.getParameter("command");
            String loadValue = "";
            boolean isEntitySpecific = (ctxt.getParameter("isEntitySpecific") != null && "true".equals(ctxt.getParameter("isEntitySpecific")) ) ? true : false ;
            
            int status = DataDictionaryManager.checkDuplicateEntry(ctxt, ctxt.getParameter("ddict_item_name"),
                    ctxt.getParameter("code").replaceAll("'","''"),ctxt.getParameter("descBox").replaceAll("'","''"),
                    ctxt.getParameter("targetRid"), isEntitySpecific);
            
            if(status == 0){
                DataDictionaryManager.insertUpdateRow(ctxt, isEntitySpecific);
                ctxt.setAttribute("reloadPage","Data saved.");
                String ddItem = ctxt.getParameter("ddict_item_name");
                String nestedDD = ctxt.getParameter("ddiParentIndex");
                String loadCmd = ctxt.getParameter("ddict_item_name1");
                if(command.equals("DDEntry")){
                    loadValue = "DataDictEntryServlet?&ddiTypeIndex="+ddItem +"&command="+ command +
                            "&entitySpecific="+isEntitySpecific+"&ddict_item_name1="+loadCmd;
                } else if(command.equals("visitReason")){
                    loadValue = "PHRHospitalMasterServlet?command=loadHospBrowser&prg=1&" +
                            "featureURL=/DataDictEntryServlet?command=visitReason~@~ddiTypeIndex=192~@~ddict_item_name=VISIT_REASON~@~entitySpecific=TRUE&" +
                            "featureDesc=VISIT_REASON&featureName=Visit Reason";
                            //"DataDictEntryServlet?&ddiTypeIndex="+ddItem +"&command="+ command +
                           // "&entitySpecific="+isEntitySpecific+"&ddict_item_name1="+ddItem;
                
                } else{
                    loadValue = "DataDictEntryServlet?&ddiTypeIndex="+ddItem +"&parentDdiTypeIndex="+ nestedDD +
                            "&command="+ command +"&entitySpecific="+isEntitySpecific ;
                }
                
                ctxt.setAttribute("url",loadValue + "~" + loadCmd );
                
                
            } else {
                ctxt.setAttribute("errorMessage","Record already exists ");
                
            }
            qe.commitTransaction() ;
            UServletHelper.sendJSP(ctxt, servletConfig, "dataEntryResponseJSP");
        } catch (Exception e){
            ctxt.setAttribute("errorMessage","Save failed.Try Again.. ");
            logger.error(e.toString());
            try { if (qe != null )qe.rollbackTransaction(); } catch(Exception ex)  {
                logger.error("Rollback transcation failed. " + e.toString()) ;
            }
            UServletHelper.sendJSP(ctxt,servletConfig,"dataEntryResponseJSP") ;
            throw new UServletException(e.getMessage(),e) ;
        }
    }
}








