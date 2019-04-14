/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.student;

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
public class StudPerformanceServlet extends UHttpServlet {

   static Logger logger = Logger.getLogger(StudPerformanceServlet.class);
   public static final String studPerformanceJSP = "/jsp/sms/StudPerformance.jsp";
   public static final String studPerformanceConJSP = "/jsp/sms/StudPerformanceCon.jsp";
   public static final String studentMarkSheetRWJSP = "/jsp/sms/StudentMarkSheetRW.jsp";
   public static final String studentMarkSheetROJSP = "/jsp/sms/StudentMarkSheetRO.jsp";
   public static final String sendEmailJSP = "/jsp/sms/EmailSummary.jsp";
   public static final String PostStudSummaryJSP = "/jsp/sms/AcademicSummary.jsp";
   public static final String responseJSP = "/jsp/common/uhDataEntryResponse.jsp";

   @Override
   public void handleGet(URequestContext ctxt) throws UServletException {
      String command = ctxt.getParameter("command");
      try {
         if ("loadStudPerfView".equals(command)) {
            ctxt.setAttribute("rsBranch", BranchManager.getBranchDetails(ctxt, 0));
            UServletHelper.sendJSP(ctxt, studPerformanceJSP);
         }else if ("loadStudPerfConView".equals(command)) {
            ctxt.setAttribute("rsBranch", BranchManager.getBranchDetails(ctxt, 0));
            UServletHelper.sendJSP(ctxt, studPerformanceConJSP);
         }else if ("loadEmailView".equals(command)) {
            ctxt.setAttribute("rsBranch", BranchManager.getBranchDetails(ctxt, 0));
            UServletHelper.sendJSP(ctxt, sendEmailJSP);
         }else if ("loadPostView".equals(command)) {
            ctxt.setAttribute("rsBranch", BranchManager.getBranchDetails(ctxt, 0));
            UServletHelper.sendJSP(ctxt, PostStudSummaryJSP);
         } else if ("loadExams".equals(command)) {
            _loadExams(ctxt);
         } else if ("loadMarkSheet".equals(command)) {
            _loadMarkSheet(ctxt);
         } else if ("loadMarkSheetCon".equals(command)) {
            _loadMarkSheetCon(ctxt);
         }
      } catch (Exception ex) {
         logger.error(ex.getMessage());
      }
   }

   private void _loadExams(URequestContext ctxt) throws UServletException {
      try {
         int branchRid = ctxt.getIntParameter("branchRid");
         ResultSet rsExams = StudPerformanceManager.loadExams(ctxt, branchRid);
         UWriterHelper.writeSelect(ctxt, rsExams, "exam_rid", "exam_name", "examSel", false);
      } catch (Exception ex) {
         logger.error(ex.getMessage());
      }
   }
   

   private void _loadMarkSheet(URequestContext ctxt) throws UServletException {
      try {
         int branchRid = ctxt.getIntParameter("branchRid");
         int batchRid = ctxt.getIntParameter("batchRid");
         int sem = ctxt.getIntParameter("sem");
         int examRid = ctxt.getIntParameter("examRid");
         String studName = ctxt.getParameter("studName");
         String regNo = ctxt.getParameter("regNo");

         ResultSet rsSubjects = StudPerformanceManager.loadSubjects(ctxt, branchRid, sem);
         ResultSet rsStudentMarksSheet = StudPerformanceManager.loadMarksDetails(ctxt, branchRid,batchRid, sem, examRid, 0,studName,regNo);
         ctxt.setAttribute("rsSubjects", rsSubjects);
         ctxt.setAttribute("rsStudentMarksSheet", rsStudentMarksSheet);
         ctxt.setAttribute("branchRid", branchRid);
         ctxt.setAttribute("sem", sem);
         ctxt.setAttribute("examRid", examRid);
         ctxt.setAttribute("examType", StudPerformanceManager.getExamType(ctxt, examRid));
         ctxt.setAttribute("examName", StudPerformanceManager.getExamName(ctxt, examRid));
         UServletHelper.sendJSP(ctxt, studentMarkSheetRWJSP);
      } catch (Exception ex) {
         logger.error(ex.getMessage());
      }
   }
   private void _loadMarkSheetCon(URequestContext ctxt) throws UServletException {
      try {
         int branchRid = ctxt.getIntParameter("branchRid");
         int batchRid = ctxt.getIntParameter("batchRid");
         int sem = ctxt.getIntParameter("sem");
         int examRid = ctxt.getIntParameter("examRid");
         String viewType = ctxt.getParameter("viewType");
         
         ResultSet rsSubjects = StudPerformanceManager.loadSubjects(ctxt, branchRid, sem);
         ResultSet rsStudentMarksSheet = StudPerformanceManager.loadMarksDetails(ctxt, branchRid,batchRid, sem, examRid, 0,null,null);
         ctxt.setAttribute("rsSubjects", rsSubjects);
         ctxt.setAttribute("rsStudentMarksSheet", rsStudentMarksSheet);
         ctxt.setAttribute("branchRid", branchRid);
         ctxt.setAttribute("sem", sem);
         ctxt.setAttribute("examRid", examRid);
         ctxt.setAttribute("viewType", viewType);
         ctxt.setAttribute("examType", StudPerformanceManager.getExamType(ctxt, examRid));
         ctxt.setAttribute("examName", StudPerformanceManager.getExamName(ctxt, examRid));
         UServletHelper.sendJSP(ctxt, studentMarkSheetROJSP);
      } catch (Exception ex) {
         logger.error(ex.getMessage());
      }
   }

   @Override
   public void handlePost(URequestContext ctxt) throws UServletException {

      //saveMarksSheet
      String command = ctxt.getParameter("command");
      try {
         if ("saveMarksSheet".equals(command)) {
            _saveMarksSheet(ctxt);
         }
      } catch (Exception ex) {
         logger.error(ex.getMessage());
      }
   }

   private void _saveMarksSheet(URequestContext ctxt) throws UServletException {
      try {
         ctxt.getQueryEngine().beginTransaction();
         StudPerformanceManager.saveMarksSheet(ctxt);
         ctxt.getQueryEngine().commitTransaction();
         ctxt.setAttribute("success", "Saved Successfully");
      } catch (Exception ex) {
         logger.error(ex.getMessage());
         try{
         ctxt.getQueryEngine().rollbackTransaction();
         }catch(Exception rollEx){
            logger.error(rollEx.getMessage());
         }
         ctxt.setAttribute("errorMessage", "Unable to Update Marks Sheet");
      } finally {
         UServletHelper.sendJSP(ctxt, responseJSP);
      }
   }
}
