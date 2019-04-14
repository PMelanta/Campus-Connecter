/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.branch;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import cc.base.UHttpServlet;
import cc.base.URequestContext;
import cc.base.UServletException;
import cc.base.UServletHelper;
import cc.base.UWriterHelper;

/**
 *
 * @author suhas
 */
public class BranchManagementServlet extends UHttpServlet {

    static Logger logger = Logger.getLogger(BranchManagementServlet.class);
    public static final String branchJSP = "/jsp/sms/Branch.jsp";
    public static final String branchDetailsJSP = "/jsp/sms/NewBranch.jsp";
    public static final String responseJSP = "/jsp/common/uhDataEntryResponse.jsp";
      public static final String addNewBatch = "/jsp/sms/AddNewBatch.jsp";

    @Override
    public void handleGet(URequestContext ctxt) throws UServletException {
        String command = ctxt.getParameter("command");
        try {
            if ("loadBranchView".equals(command)) {
                ctxt.setAttribute("rsBranchDet", BranchManager.getBranchDetails(ctxt, 0));
                UServletHelper.sendJSP(ctxt, branchJSP);
             }else if("addNewBatch".equals(command)){
            ctxt.setAttribute("rsBranchDet", BranchManager.getBranchDetails(ctxt, 0)); 
             UServletHelper.sendJSP(ctxt, addNewBatch); 
            } else if ("addNewBranch".equals(command)) {
                UServletHelper.sendJSP(ctxt, branchDetailsJSP);
            } else if ("editBranch".equals(command)) {
                int branchRid = ctxt.getIntParameter("branchRid");
                ctxt.setAttribute("rsBranchDet", BranchManager.getBranchDetails(ctxt, branchRid));
                UServletHelper.sendJSP(ctxt, branchDetailsJSP);
            } else if ("deleteBranch".equals(command)) {
                int delBranchRid = ctxt.getIntParameter("branchRid");
                BranchManager.deleteBranch(ctxt, delBranchRid);
                UWriterHelper.writeString(ctxt, "1");
            }
        } catch (Exception ex) {
            logger.error("Unable to process the request - " + ex.getMessage());
        }
    }

    @Override
    public void handlePost(URequestContext ctxt) throws UServletException {
        String command = ctxt.getParameter("command");
        try {
            if ("saveBranchDetails".equals(command)) {
                _saveBranchDetails(ctxt);
            }else if("saveBatchDetails".equals(command)){
             _saveBatchDetails(ctxt);
         }
            }catch (Exception ex) {
         logger.error("Unable to process the request - " + ex.getMessage());
      }
        }
    
      private void _saveBatchDetails(URequestContext ctxt)
        throws UServletException {
        try{
        int branchRid=ctxt.getIntParameter("branchSel");
        String batchName=ctxt.getParameter("batchName");
        BranchManager.saveBatchDetails(ctxt, branchRid, batchName);
         ctxt.setAttribute("success", "Saved Successfully");
    }catch(Exception e){
       logger.error("Unable to process the request - " + e.getMessage());
         ctxt.setAttribute("errorMessage", "Unable to update batchName details"); 
    }finally{
       UServletHelper.sendJSP(ctxt, responseJSP);     
        }
    }

   

    private void _saveBranchDetails(URequestContext ctxt)
            throws UServletException {
        try {
            int branchRid = ctxt.getIntParameter("branchRid"); //won't apply for newly created branch
            String branchName = ctxt.getParameter("branchName");
            String branchCode = ctxt.getParameter("branchCode");
            int totSems = ctxt.getIntParameter("totSems");
            int totIntake = ctxt.getIntParameter("totIntake");
            int noOfInternals = ctxt.getIntParameter("noOfInternals");
            int isActive = ctxt.getCheckboxParameter("isActive");
            BranchManager.saveBranchDetails(ctxt, branchRid, branchName, branchCode, totSems,
                    totIntake, noOfInternals, isActive);
            ctxt.setAttribute("success", "Saved Successfully");
        } catch (Exception ex) {
            logger.error("Unable to process the request - " + ex.getMessage());
            ctxt.setAttribute("errorMessage", "Unable to update branch details");
        } finally {
            UServletHelper.sendJSP(ctxt, responseJSP);
        }
    }
}
