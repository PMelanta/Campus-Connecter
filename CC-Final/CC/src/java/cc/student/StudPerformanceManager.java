/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.student;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import cc.base.UDBAccessException;
import cc.base.UQueryEngine;
import cc.base.URequestContext;
import cc.notification.NotificationManager;
import cc.util.UDate;

/**
 *
 * @author suhas
 */
public class StudPerformanceManager {

   public static ResultSet loadExams(URequestContext ctxt, int branchRid)
           throws UDBAccessException {
      String sql = "select * from exams "
              + "where exam_branch_rid = " + branchRid + " AND exam_is_active = 1 "
              + "order by exam_type,exam_seq";

      return ctxt.getQueryEngine().executeQuery(sql);
   }

   public static int getExamType(URequestContext ctxt, int examRid)
           throws UDBAccessException {
      String sql = "select * from exams where exam_rid = " + examRid;
      try {
         ResultSet rsExam = ctxt.getQueryEngine().executeQuery(sql);
         if (rsExam.first()) {
            return rsExam.getInt("exam_type");
         }
      } catch (Exception ex) {
         throw new UDBAccessException(ex.getMessage(), ex);
      }
      return 0;
   }
   
   public static String getExamName(URequestContext ctxt, int examRid)
           throws UDBAccessException {
      String sql = "select * from exams where exam_rid = " + examRid;
      try {
         ResultSet rsExam = ctxt.getQueryEngine().executeQuery(sql);
         if (rsExam.first()) {
            return rsExam.getString("exam_name");
         }
      } catch (Exception ex) {
         throw new UDBAccessException(ex.getMessage(), ex);
      }
      return "";
   }

   public static ResultSet loadSubjects(URequestContext ctxt, int branchRid, int sem)
           throws UDBAccessException {
      String sql = "SELECT * FROM subject_details "
              + " WHERE is_active = 1 AND row_invalidated = 0 ";
      if (branchRid > 0) {
         sql += " AND sub_branch_rid=" + branchRid;
      }

      if (sem > 0) {
         sql += " AND sub_sem=" + sem;
      }
      sql += " ORDER BY sub_branch_rid,sub_sem,sub_seq";

      return ctxt.getQueryEngine().executeQuery(sql);
   }

   public static ResultSet loadMarksDetails(URequestContext ctxt, int branchRid,int batchRid, int sem, int examRid, 
           int studentRid,String studName,String regNo)
           throws UDBAccessException {
      if (branchRid <= 0 || sem <= 0) {
         throw new UDBAccessException("Invalid branch/sem sent in", null);
      }

      String sql = "SELECT * FROM student "
              + " LEFT JOIN stud_marks_sheet ON(stud_rid = sms_stud_rid AND stud_marks_sheet.row_invalidated = 0 AND sms_exam_rid=" + examRid + " AND sms_sem="+sem+")"
              + " LEFT JOIN exams ON(sms_exam_rid = exam_rid AND exam_is_active = 1)"
              + " LEFT JOIN subject_details ON(sms_sub_rid = sub_rid)"
              + " WHERE stud_branch_rid = " + branchRid;// + " AND stud_cur_sem = " + sem;

      if (null != studName && !"".equals(studName.trim())) {
         sql += " AND (stud_first_name like '" + studName+"%' OR stud_last_name like '"+studName+"%')";
      }
      
      if (null != regNo && !"".equals(regNo.trim())) {
         sql += " AND stud_reg_no like '" + regNo+"%'";
      }

      if (studentRid > 0) {
         sql += " AND stud_rid=" + studentRid;
      }
      
      if (batchRid > 0) {
         sql += " AND stud_batch_rid=" + batchRid;
      }
      sql += " ORDER BY sub_branch_rid,sub_sem,sms_stud_rid,sub_seq";

      return ctxt.getQueryEngine().executeQuery(sql);
   }

   public static void saveMarksSheet(URequestContext ctxt)
           throws UDBAccessException, Exception {
      int branchRid = ctxt.getIntParameter("branchRid");
      int sem = ctxt.getIntParameter("sem");
      int examRid = ctxt.getIntParameter("examRid");

      String studRids[] = ctxt.getParameterValues("studRid");
      HashMap<String, Integer> subSeqMap = getSubSequence(ctxt, branchRid, sem,"sub");
      Set<String> subKeySet = subSeqMap.keySet();
      Iterator<String> keySetIterator = subKeySet.iterator();

      UQueryEngine qe = ctxt.getQueryEngine();
      int userRid = ctxt.getUserRID();
      while (keySetIterator.hasNext()) {
         String subKey = keySetIterator.next();
         String subMarks[] = ctxt.getParameterValues(subKey);
         int subRid = subSeqMap.get(subKey);
         for (int i = 0; i < studRids.length; i++) {
            saveStudentMark(qe,sem, examRid, subRid, Integer.parseInt(studRids[i]), parseFloat(subMarks[i]), userRid);
         }
      }
   }

   public static float parseFloat(String floatValue) {
      if (null == floatValue || "".equals(floatValue.trim())) {
         return 0;
      } else {
         try {
            return Float.parseFloat(floatValue);
         } catch (Exception e) {
            return 0;
         }
      }
   }

   public static void saveStudentMark(UQueryEngine qe, int sem,int examRid, int subRid, int studRid,
           float subMarks, int userRid) throws Exception {
      //check if its an update
      String sql = "select * from stud_marks_sheet where sms_stud_rid = " + studRid
              + " AND sms_exam_rid=" + examRid + " AND sms_sub_rid=" + subRid+" AND sms_sem="+sem;
      ResultSet rsSMS = qe.executeQuery(sql);
      if (rsSMS.first()) {
         int smsRid = rsSMS.getInt("sms_rid");
         rsSMS.close(); //closing coz we are going to update the same record!
         sql = "UPDATE stud_marks_sheet SET sms_obtained_marks = " + subMarks
                 + ",sms_mod_datetime = NOW(),sms_mod_user_rid=" + userRid + " WHERE sms_rid = " + smsRid;
         qe.executeUpdate(sql);
         
         int notRid = NotificationManager.getNotificationRid(qe, smsRid, NotificationManager.NOT_TYPE_EXAM);
         if(notRid > 0){
             NotificationManager.resetNotification(qe, notRid, UDate.currentDateTimeDBStr(), UDate.currentDateTimeDBStr());
         }else{
             NotificationManager.addNotification(qe, studRid, NotificationManager.NOT_TYPE_EXAM, 
                     "Marks Updated", UDate.currentDateTimeDBStr(), UDate.currentDateTimeDBStr(), smsRid);
         }
         
      } else {
         sql = "INSERT INTO stud_marks_sheet(sms_stud_rid,sms_sem,sms_exam_rid,sms_sub_rid,sms_obtained_marks,sms_created_datetime,"
                 + "sms_created_user_rid) "
                 + " VALUES(" + studRid + "," +sem+","+ examRid + "," + subRid + "," + subMarks + ",NOW()," + userRid + ")";
         int xmRid = qe.executeInsert(sql);
         
         NotificationManager.addNotification(qe, studRid, NotificationManager.NOT_TYPE_EXAM, 
                     "Marks Updated", UDate.currentDateTimeDBStr(), UDate.currentDateTimeDBStr(), xmRid);
      }
   }

   public static HashMap<String, Integer> getSubSequence(URequestContext ctxt, int branchRid, int sem,String prefix)
           throws UDBAccessException {
      try {
         ResultSet rsSubjects = loadSubjects(ctxt, branchRid, sem);
         rsSubjects.last();
         int totSub = rsSubjects.getRow();
         rsSubjects.beforeFirst();
         HashMap<String, Integer> subMap = new HashMap(totSub);
         while (rsSubjects.next()) {
            subMap.put(prefix + rsSubjects.getInt("sub_seq"), rsSubjects.getInt("sub_rid"));
         }
         return subMap;
      } catch (Exception ex) {
         throw new UDBAccessException(ex.getMessage(), ex);
      }
   }
}
