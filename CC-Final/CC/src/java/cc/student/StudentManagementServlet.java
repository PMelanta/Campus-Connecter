/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.student;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import cc.attendance.AttendanceManager;
import cc.base.UConfig;
import cc.base.UHttpServlet;
import cc.base.URequestContext;
import cc.base.UServletException;
import cc.base.UServletHelper;
import cc.base.UWriterHelper;
import cc.branch.BranchManager;
import cc.user.FileManager;
import cc.user.UserRegistrationManager;
import cc.util.UDate;

/**
 *
 * @author suhas
 */
public class StudentManagementServlet extends UHttpServlet {

   static Logger logger = Logger.getLogger(StudentManagementServlet.class);
   public static final String studentBrowserJSP = "/jsp/sms/StudentBrowser.jsp";
   public static final String studentDetailsJSP = "/jsp/sms/StudentDetails.jsp";
   public static final String studentDetailsTabJSP = "/jsp/sms/StudentDetailsTab.jsp";
   public static final String addUpdateStudentJSP = "/jsp/sms/AddUpdateStudent.jsp";
   public static final String viewStudentJSP = "/jsp/sms/ViewStudent.jsp";
   public static final String studentSummarytJSP = "/jsp/sms/StudentSummary.jsp";
   public static final String responseJSP = "/jsp/common/uhDataEntryResponse.jsp";

   @Override
   public void handleGet(URequestContext ctxt) throws UServletException {
      String command = ctxt.getParameter("command");

      try {
         if ("loadStudentView".equals(command)) {
            ctxt.setAttribute("rsBranch", BranchManager.getBranchDetails(ctxt, 0));
            UServletHelper.sendJSP(ctxt, studentBrowserJSP);
         } else if ("viewStudentDetails".equals(command)) {
            _viewStudentDetails(ctxt);
            int viewType = ctxt.getIntParameter("viewType");
            if(viewType == 1){
            UServletHelper.sendJSP(ctxt, studentDetailsJSP);
            }else{
            UServletHelper.sendJSP(ctxt, studentDetailsTabJSP);
            }
         } else if ("addNewStudent".equals(command)) {
            ctxt.setAttribute("rsBranch", BranchManager.getBranchDetails(ctxt, 0));
            UServletHelper.sendJSP(ctxt, addUpdateStudentJSP);
         } else if ("editStudent".equals(command)) {
            int studentRid = ctxt.getIntParameter("studentRid");
            int branchRid = 0;
            ctxt.setAttribute("rsBranch", BranchManager.getBranchDetails(ctxt, 0));
            ResultSet rsStudent = StudentManager.getStudentDetails(ctxt, studentRid);
            if (rsStudent.first()) {
               branchRid = rsStudent.getInt("stud_branch_rid");
            }
            ctxt.setAttribute("rsStudentDetails", rsStudent);
            ctxt.setAttribute("rsBatch", BranchManager.getBatchDetails(ctxt, branchRid));
            UServletHelper.sendJSP(ctxt, addUpdateStudentJSP);
         } else if ("viewStudent".equals(command)) {
            int studentRidView = ctxt.getIntParameter("studentRid");
            ctxt.setAttribute("rsStudentDetails", StudentManager.getStudentDetails(ctxt, studentRidView));
            UServletHelper.sendJSP(ctxt, viewStudentJSP);
         } else if ("deleteStudent".equals(command)) {
            int delStudentRid = ctxt.getIntParameter("studentRid");
            StudentManager.deleteStudent(ctxt, delStudentRid);
            UWriterHelper.writeString(ctxt, "1");
         } else if ("loadStudSel".equals(command)) {
            _loadStudSel(ctxt);
         } else if ("loadStudSummary".equals(command)) {
            _loadStudSummary(ctxt);
         }else if("getBranchList".equals(command)){
             _getBranchList(ctxt);
         }
      } catch (Exception ex) {
         logger.error("Unable to process the request - " + ex.getMessage());
      }

   }

   private void _loadStudSummary(URequestContext ctxt) throws UServletException {
      try {
         int branchRid = ctxt.getIntParameter("branchRid");
         int batchRid = ctxt.getIntParameter("batchRid");
         int sem = ctxt.getIntParameter("sem");
         int examRid = ctxt.getIntParameter("examRid");
         int monthNo = ctxt.getIntParameter("monthNo");
         int studRid = ctxt.getIntParameter("studRid");

         ResultSet rsSubjects = StudPerformanceManager.loadSubjects(ctxt, branchRid, sem);
         ResultSet rsStudentMarksSheet = StudPerformanceManager.loadMarksDetails(ctxt, branchRid, batchRid, sem, examRid, studRid, null, null);
         ResultSet rsStudentAttSheet = AttendanceManager.loadAttendancePerc(ctxt, branchRid, batchRid, sem, monthNo, studRid);
         ctxt.setAttribute("rsSubjects", rsSubjects);
         ctxt.setAttribute("rsStudentMarksSheet", rsStudentMarksSheet);
         ctxt.setAttribute("rsStudentAttSheet", rsStudentAttSheet);
         ctxt.setAttribute("branchRid", branchRid);
         ctxt.setAttribute("batchRid", batchRid);
         ctxt.setAttribute("studRid", studRid);
         ctxt.setAttribute("monthNo", monthNo);
         ctxt.setAttribute("sem", sem);
         ctxt.setAttribute("examRid", examRid);
         ctxt.setAttribute("viewType", ctxt.getParameter("viewType"));
         ctxt.setAttribute("examName", StudPerformanceManager.getExamName(ctxt, examRid));
         ctxt.setAttribute("batchName", AttendanceManager.getBatchName(ctxt, batchRid));
         UServletHelper.sendJSP(ctxt, studentSummarytJSP);
      } catch (Exception ex) {
         logger.error(ex.getMessage());
      }
   }
   
   private void _sendStudSummary(URequestContext ctxt) throws UServletException {
      try {
         int branchRid = ctxt.getIntParameter("branchRid");
         int batchRid = ctxt.getIntParameter("batchRid");
         int sem = ctxt.getIntParameter("sem");
         int examRid = ctxt.getIntParameter("examRid");
         int monthNo = ctxt.getIntParameter("monthNo");
         int studRid = ctxt.getIntParameter("studRid");
         
         ResultSet rsSubjects = StudPerformanceManager.loadSubjects(ctxt, branchRid, sem);
         ResultSet rsStudentMarksSheet = StudPerformanceManager.loadMarksDetails(ctxt, branchRid, batchRid, sem, examRid, studRid, null, null);
         ResultSet rsStudentAttSheet = AttendanceManager.loadAttendancePerc(ctxt, branchRid, batchRid, sem, monthNo, studRid);
         ResultSet rsStudent = StudentManager.getStudentDetails(ctxt, studRid);
         
         String examName =  StudPerformanceManager.getExamName(ctxt, examRid);
         String batchName =  AttendanceManager.getBatchName(ctxt, batchRid);
         StudentManager.sendStudentSummary(ctxt,rsSubjects,rsStudent,rsStudentMarksSheet,rsStudentAttSheet,examName,batchName,monthNo,sem);
         
      } catch (Exception ex) {
         logger.error(ex.getMessage());
         ctxt.setAttribute("errorMessage", "Error: Unable to send email");
         throw new UServletException(ex.getMessage());
      }
   }

   private void _viewStudentDetails(URequestContext ctxt)
           throws UServletException {
      try {
         int branchRid = ctxt.getIntParameter("branchRid");
         int batchRid = ctxt.getIntParameter("batchRid");
         int sem = ctxt.getIntParameter("sem");
         String studName = ctxt.getParameter("studName");
         String regNo = ctxt.getParameter("regNo");

         ResultSet rsStudentDetails = StudentManager.getStudentDetails(ctxt, branchRid, batchRid, sem, studName, regNo, 0);
         ctxt.setAttribute("rsStudentDetails", rsStudentDetails);
      } catch (Exception ex) {
         logger.error("Unable to get student details " + ex.getMessage());
         throw new UServletException(ex.getMessage());
      }

   }

   @Override
   public void handlePost(URequestContext ctxt) throws UServletException {
      try {
         String command = ctxt.getParameter("command");
         if (null != command && "sendStudSummary".equals(command)) {
            _sendStudSummary(ctxt);
         } else {
            _saveDetails(ctxt);
         }
         ctxt.setAttribute("success", "Saved successfully!");
      } catch (Exception ex) {
         logger.error(ex.getMessage());
         if (ctxt.getAttribute("errorMessage") == null) {
            ctxt.setAttribute("errorMessage", "Error: Uanble to save student details");
         }
      } finally {
         UServletHelper.sendJSP(ctxt, responseJSP);
      }

   }

   private void _saveDetails(URequestContext ctxt)
           throws Exception {
      ctxt = FileManager.uploadAvatar(ctxt);
      String command = ctxt.getParameter("command");
      if ("saveStudentDetails".equals(command)) {
         String firstName, lastName, regNo, gender, dob, studEmail,
                 father, mother, parentEmail, contactNo, address, avatar = null;

         int studentRid = ctxt.getIntParameter("studentRid");
         int branchRid = ctxt.getIntParameter("branchSel");
         int batchRid = ctxt.getIntParameter("batchSel");
         int sem = ctxt.getIntParameter("semSel");
         firstName = ctxt.getParameter("firstName");
         lastName = ctxt.getParameter("lastName");
         regNo = ctxt.getParameter("regNo");
         gender = ctxt.getParameter("gender");
         studEmail = ctxt.getParameter("studEmail");
         father = ctxt.getParameter("father");
         mother = ctxt.getParameter("mother");
         parentEmail = ctxt.getParameter("parentEmail");
         contactNo = ctxt.getParameter("contactNo");
         address = ctxt.getParameter("address");
         int isActive = ctxt.getCheckboxParameter("isActive");
         int canCreateEvent = ctxt.getCheckboxParameter("canCreateEvent");
         dob = ctxt.getParameter("dob");
         if (dob.length() > 0) {
            dob = UDate.displayToDB(dob);
         } else {
            dob = null;
         }

         if (ctxt.getAttribute("uploadedFilesPath") != null) {
            String contextPath = UConfig.getParameterValue(0, "PROFILE_PIC_CONTEXT_PATH", "images/users");
            String uploadedFileName = ctxt.getAttribute("uploadedFilesName").toString().split("`")[0];
            if (null != uploadedFileName && !"".equals(uploadedFileName.trim())) {
               avatar = ctxt.getContextPath() + "/" + contextPath + "/" + uploadedFileName;
            } else {
               avatar = "";
            }

         }

         StudentManager.saveStudentDetails(ctxt, studentRid, branchRid, batchRid, sem, firstName, lastName, regNo, gender, dob, studEmail,
                 father, mother, parentEmail, contactNo, address, avatar, isActive,canCreateEvent);


      }
   }

   private void _loadStudSel(URequestContext ctxt) throws UServletException {
      try {
         int branchRid = ctxt.getIntParameter("branchRid");
         int batchRid = ctxt.getIntParameter("batchRid");
         int sem = ctxt.getIntParameter("sem");
         ResultSet rsStud = StudentManager.loadStudentSel(ctxt, branchRid, batchRid, 0);
         UWriterHelper.writeSelect(ctxt, rsStud, "stud_rid", "stud_name_reg", "regNoSel", false);
      } catch (Exception ex) {
         logger.error(ex.getMessage());
      }
   }
   private void _getBranchList(URequestContext ctxt) throws UServletException {
       String resStr = "";
      try {
         ResultSet rs = BranchManager.getBranchDetails(ctxt, 0);
         resStr = "OK";
         while(rs.next()){
             resStr += "#"+rs.getInt("branch_rid")+"~"+rs.getString("branch_name");
         }
      } catch (Exception ex) {
         logger.error(ex.getMessage());
         resStr = "NOT_OK#"+ex.getMessage();
      }finally{
          UWriterHelper.writeString(ctxt, resStr);
      }
   }
}
