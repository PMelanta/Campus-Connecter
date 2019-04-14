/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.attendance;

import java.sql.ResultSet;
import org.apache.log4j.Logger;
import cc.base.UHttpServlet;
import cc.base.URequestContext;
import cc.base.UServletException;
import cc.base.UServletHelper;
import cc.branch.BranchManager;
import cc.student.StudPerformanceManager;

/**
 *
 * @author suhas
 */
public class AttendanceServlet extends UHttpServlet {
   static Logger logger = Logger.getLogger(AttendanceServlet.class);
   public static final String studAttendanceJSP = "/jsp/sms/StudAttendance.jsp";
   public static final String studAttendanceConJSP = "/jsp/sms/StudAttendanceCon.jsp";
   public static final String studentAttendanceRWJSP = "/jsp/sms/StudentAttendanceRW.jsp";
   public static final String studentAttendanceROJSP = "/jsp/sms/StudentAttendanceRO.jsp";
   public static final String responseJSP = "/jsp/common/uhDataEntryResponse.jsp";

   @Override
   public void handleGet(URequestContext ctxt) throws UServletException {
      String command = ctxt.getParameter("command");
      try {
         if ("loadAttendanceView".equals(command)) {
            ctxt.setAttribute("rsBranch", BranchManager.getBranchDetails(ctxt, 0));
            UServletHelper.sendJSP(ctxt, studAttendanceJSP);
         }else if ("loadAttendanceConView".equals(command)) {
            ctxt.setAttribute("rsBranch", BranchManager.getBranchDetails(ctxt, 0));
            UServletHelper.sendJSP(ctxt, studAttendanceConJSP);
         } else if ("loadAttendanceSheet".equals(command)) {
            _loadAttendanceSheet(ctxt);
         } else if ("loadAttendanceSheetCon".equals(command)) {
            _loadAttendanceSheetCon(ctxt);
         }
      } catch (Exception ex) {
         logger.error(ex.getMessage());
      }
   }
  
   private void _loadAttendanceSheet(URequestContext ctxt) 
           throws UServletException{
      try{
         int branchRid = ctxt.getIntParameter("branchRid");
         int batchRid = ctxt.getIntParameter("batchRid");
         int monthNo = ctxt.getIntParameter("monthNo");
         int sem = ctxt.getIntParameter("sem");
         String studName = ctxt.getParameter("studName");
         String regNo = ctxt.getParameter("regNo");
         ResultSet rsSubjects = StudPerformanceManager.loadSubjects(ctxt, branchRid, sem);
         ResultSet rsStudentAttSheet = AttendanceManager.loadAttendanceDetails(ctxt, branchRid,batchRid, sem,monthNo, 0, studName, regNo);
         ResultSet rsTotClasses = AttendanceManager.getTotClasses(ctxt, branchRid,batchRid,sem,monthNo);
         ctxt.setAttribute("rsSubjects", rsSubjects);
         ctxt.setAttribute("rsStudentAttSheet", rsStudentAttSheet);
         ctxt.setAttribute("rsTotClasses", rsTotClasses);
         ctxt.setAttribute("branchRid", branchRid);
         ctxt.setAttribute("sem", sem);
         ctxt.setAttribute("batchRid", batchRid);
         ctxt.setAttribute("batchName", AttendanceManager.getBatchName(ctxt, batchRid));
         ctxt.setAttribute("monthNo", monthNo);
         UServletHelper.sendJSP(ctxt, studentAttendanceRWJSP);
      }catch(Exception ex){
         logger.error(ex.getMessage());
      }
   }
   private void _loadAttendanceSheetCon(URequestContext ctxt) 
           throws UServletException{
      try{
         int branchRid = ctxt.getIntParameter("branchRid");
         int batchRid = ctxt.getIntParameter("batchRid");
         int monthNo = ctxt.getIntParameter("monthNo");
         int sem = ctxt.getIntParameter("sem");
         String viewType = ctxt.getParameter("viewType");
         
         ResultSet rsSubjects = StudPerformanceManager.loadSubjects(ctxt, branchRid, sem);
         ResultSet rsStudentAttSheet = AttendanceManager.loadAttendanceDetails(ctxt, branchRid,batchRid, sem,monthNo, 0, null, null);
         ResultSet rsTotClasses = AttendanceManager.getTotClasses(ctxt, branchRid,batchRid,sem,monthNo);
         ctxt.setAttribute("rsSubjects", rsSubjects);
         ctxt.setAttribute("rsStudentAttSheet", rsStudentAttSheet);
         ctxt.setAttribute("rsTotClasses", rsTotClasses);
         ctxt.setAttribute("branchRid", branchRid);
         ctxt.setAttribute("sem", sem);
         ctxt.setAttribute("viewType", viewType);
         ctxt.setAttribute("batchRid", batchRid);
         ctxt.setAttribute("batchName", AttendanceManager.getBatchName(ctxt, batchRid));
         ctxt.setAttribute("monthNo", monthNo);
         UServletHelper.sendJSP(ctxt, studentAttendanceROJSP);
      }catch(Exception ex){
         logger.error(ex.getMessage());
      }
   }

   @Override
   public void handlePost(URequestContext ctxt) throws UServletException {

      //saveMarksSheet
      String command = ctxt.getParameter("command");
      try {
         if ("saveAttendanceSheet".equals(command)) {
            _saveAttendanceSheet(ctxt);
         }
      } catch (Exception ex) {
         logger.error(ex.getMessage());
      }
   }

   private void _saveAttendanceSheet(URequestContext ctxt) throws UServletException {
      try {
         ctxt.getQueryEngine().beginTransaction();
         AttendanceManager.saveAttendanceSheet(ctxt);
         ctxt.getQueryEngine().commitTransaction();
         ctxt.setAttribute("success", "Saved Successfully");
      } catch (Exception ex) {
         logger.error(ex.getMessage());
         try{
         ctxt.getQueryEngine().rollbackTransaction();
         }catch(Exception rollEx){
            logger.error(rollEx.getMessage());
         }
         ctxt.setAttribute("errorMessage", "Unable to Update Attendance Sheet");
      } finally {
         UServletHelper.sendJSP(ctxt, responseJSP);
      }
   }
   
   
}
