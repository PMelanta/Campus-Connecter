/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.subject;

import java.sql.ResultSet;
import org.apache.log4j.Logger;
import cc.base.UHttpServlet;
import cc.base.URequestContext;
import cc.base.UServletException;
import cc.base.UServletHelper;
import cc.base.UWriterHelper;
import cc.branch.BranchManager;

/**
 *
 * @author suhas
 */
public class SubjectManagementServlet extends UHttpServlet {

   static Logger logger = Logger.getLogger(SubjectManagementServlet.class);
   public static final String subjectJSP = "/jsp/sms/Subject.jsp";
   public static final String subjectDetailsJSP = "/jsp/sms/NewSubject.jsp";
   public static final String responseJSP = "/jsp/common/uhDataEntryResponse.jsp";

   @Override
   public void handleGet(URequestContext ctxt) throws UServletException {
      String command = ctxt.getParameter("command");
      try {
         if ("loadSubjectView".equals(command)) {
            ctxt.setAttribute("rsSubjectDet", SubjectManager.getSubjectDetails(ctxt, 0));
            UServletHelper.sendJSP(ctxt, subjectJSP);
         } else if ("addNewSubject".equals(command)) {
            ctxt.setAttribute("rsBranch", BranchManager.getBranchDetails(ctxt, 0));
            UServletHelper.sendJSP(ctxt, subjectDetailsJSP);
         } else if ("loadSemesters".equals(command)) {
            _loadSemesters(ctxt);
         } else if ("loadBatches".equals(command)) {
            _loadBatches(ctxt);
         } else if ("editSubject".equals(command)) {
            int subjectRid = ctxt.getIntParameter("subjectRid");
            ctxt.setAttribute("rsBranch", BranchManager.getBranchDetails(ctxt, 0));
            ctxt.setAttribute("rsSubjectDet", SubjectManager.getSubjectDetails(ctxt, subjectRid));
            UServletHelper.sendJSP(ctxt, subjectDetailsJSP);
         } else if ("deleteSubject".equals(command)) {
            int delSubjectRid = ctxt.getIntParameter("subjectRid");
            SubjectManager.deleteSubject(ctxt, delSubjectRid);
            UWriterHelper.writeString(ctxt, "1");
         }
      } catch (Exception ex) {
         logger.error("Unable to process the request - " + ex.getMessage());
      }
   }

   private void _loadSemesters(URequestContext ctxt)
           throws UServletException {
      try {
         int branchRid = ctxt.getIntParameter("branchRid");
         String selectName = ctxt.getParameter("selName");
         String onChangeFun = ctxt.getParameter("onChangeFun");
         ResultSet rsBranch = BranchManager.getBranchDetails(ctxt, branchRid);
         if (rsBranch.first()) {
            int noOfSems = rsBranch.getInt("no_of_sems");
            int includeZero = ctxt.getIntParameter("includeZero");
            int startingPos = includeZero == 1 ? 0 : 1;
            UWriterHelper.writeSelect(ctxt, selectName, onChangeFun, startingPos, noOfSems);
         }
      } catch (Exception ex) {
         logger.error("Unable to process the request - " + ex.getMessage());
      }

   }

   private void _loadBatches(URequestContext ctxt)
           throws UServletException {
      try {
         int branchRid = ctxt.getIntParameter("branchRid");
         int includeZero = ctxt.getIntParameter("includeZero");
         String selectName = ctxt.getParameter("selName");
         ResultSet rsBatch = BranchManager.getBatchDetails(ctxt, branchRid);
         boolean includeEmpty = includeZero == 1?true:false;
         UWriterHelper.writeSelect(ctxt, rsBatch, "batch_rid", "batch_name", selectName, includeEmpty);
      } catch (Exception ex) {
         logger.error("Unable to process the request - " + ex.getMessage());
      }

   }

   @Override
   public void handlePost(URequestContext ctxt) throws UServletException {
      String command = ctxt.getParameter("command");
      try {
         if ("saveSubjectDetails".equals(command)) {
            _saveSubjectDetails(ctxt);
         }
      } catch (Exception ex) {
         logger.error("Unable to process the request - " + ex.getMessage());
      }
   }

   private void _saveSubjectDetails(URequestContext ctxt)
           throws UServletException {
      try {
         int subjectRid = ctxt.getIntParameter("subjectRid"); //won't apply for newly created subject
         int branchRid = ctxt.getIntParameter("branchSel");
         int sem = ctxt.getIntParameter("semSel");

         String subName = ctxt.getParameter("subName");
         String subCode = ctxt.getParameter("subCode");

         int maxInternal = ctxt.getIntParameter("maxInternal");
         int minInternal = ctxt.getIntParameter("minInternal");
         int maxExternal = ctxt.getIntParameter("maxExternal");
         int minExternal = ctxt.getIntParameter("minExternal");
         int subSeq = ctxt.getIntParameter("subSeq");
         int isActive = ctxt.getCheckboxParameter("isActive");

         SubjectManager.saveSubjectDetails(ctxt, subjectRid, subName, subCode, branchRid, sem, maxInternal,
                 minInternal, maxExternal, minExternal, subSeq, isActive);
         ctxt.setAttribute("success", "Saved Successfully");
      } catch (Exception ex) {
         logger.error("Unable to process the request - " + ex.getMessage());
         if (ctxt.getAttribute("errorMessage") == null) {
            ctxt.setAttribute("errorMessage", "Unable to update subject details");
         }
      } finally {
         UServletHelper.sendJSP(ctxt, responseJSP);
      }
   }
}
