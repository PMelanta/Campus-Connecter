/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.attendance;

import cc.base.UDBAccessException;
import cc.base.UQueryEngine;
import cc.base.URequestContext;
import cc.notification.NotificationManager;
import cc.student.StudPerformanceManager;
import cc.util.UDate;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author suhas
 */
public class AttendanceManager {

   public static ResultSet loadAttendanceDetails(URequestContext ctxt, int branchRid, int batchRid, int sem, int monthNo,
           int studentRid, String studName, String regNo)
           throws UDBAccessException {
      if (branchRid <= 0 || sem <= 0) {
         throw new UDBAccessException("Invalid branch/sem sent in", null);
      }

      String sql = "SELECT * FROM student "
              + " LEFT JOIN attendance ON(stud_rid = att_stud_rid AND att_month_no=" + monthNo + " AND att_sem="+sem+") "
              + " LEFT JOIN subject_details ON(att_sub_rid = sub_rid) "
              + " WHERE stud_branch_rid = " + branchRid;// + " AND stud_cur_sem = " + sem;

      if (null != studName && !"".equals(studName.trim())) {
         sql += " AND (stud_first_name like '" + studName + "%' OR stud_last_name like '" + studName + "%')";
      }

      if (null != regNo && !"".equals(regNo.trim())) {
         sql += " AND stud_reg_no like '" + regNo + "%'";
      }

      if (studentRid > 0) {
         sql += " AND stud_rid=" + studentRid;
      }

      if (batchRid > 0) {
         sql += " AND stud_batch_rid=" + batchRid;
      }
      sql += " ORDER BY sub_branch_rid,sub_sem,stud_first_name,sub_seq";

      return ctxt.getQueryEngine().executeQuery(sql);
   }
   
   public static ResultSet loadAttendancePerc(URequestContext ctxt, int branchRid, int batchRid, int sem, int monthNo,
           int studentRid)
           throws UDBAccessException {
      if (branchRid <= 0 || sem <= 0 || studentRid<=0||monthNo<=0||sem<=0) {
         throw new UDBAccessException("Invalid params sent in", null);
      }

      String sql = "SELECT (100 * (IFNULL(sum(att_tot_present),0)))/IFNULL(sum(atc_tot_class),0) as att_perc,stud_reg_no,sub_seq FROM student "
              + " LEFT JOIN attendance ON(stud_rid = att_stud_rid AND att_month_no=" + monthNo + " AND att_sem="+sem+") "
              + " LEFT JOIN attendance_tot_classes ON(atc_sub_rid = att_sub_rid "
              + " AND atc_batch_rid = stud_batch_rid AND att_sem = atc_sem AND  atc_month_no = att_month_no) "
              + " LEFT JOIN subject_details ON(att_sub_rid = sub_rid) "
              + " WHERE stud_branch_rid = " + branchRid;// + " AND stud_cur_sem = " + sem;

      if (studentRid > 0) {
         sql += " AND stud_rid=" + studentRid;
      }

      if (batchRid > 0) {
         sql += " AND stud_batch_rid=" + batchRid;
      }
      sql+= " GROUP BY stud_reg_no,sub_seq";
      
      sql += " ORDER BY stud_reg_no,sub_seq";

      return ctxt.getQueryEngine().executeQuery(sql);
   }

   public static ResultSet getTotClasses(URequestContext ctxt, int branchRid, int batchRid, int sem, int monthNo)
           throws UDBAccessException {
      if (branchRid <= 0 || sem <= 0) {
         throw new UDBAccessException("Invalid branch/sem sent in", null);
      }

      String sql = "SELECT * FROM subject_details "
              + " LEFT JOIN attendance_tot_classes ON(sub_rid = atc_sub_rid "
              + " AND sub_sem = atc_sem AND atc_batch_rid = " + batchRid + " AND atc_month_no=" + monthNo + ") "
              + " WHERE sub_branch_rid = " + branchRid + " AND sub_sem=" + sem;

      sql += " ORDER BY sub_branch_rid,sub_sem,sub_seq";

      return ctxt.getQueryEngine().executeQuery(sql);
   }

   public static String getBatchName(URequestContext ctxt, int batchRid)
           throws UDBAccessException {
      String sql = "select * from stud_batch where batch_rid = " + batchRid;
      try {
         ResultSet rsBatch = ctxt.getQueryEngine().executeQuery(sql);
         if (rsBatch.first()) {
            return rsBatch.getString("batch_name");
         }
      } catch (Exception ex) {
         throw new UDBAccessException(ex.getMessage(), ex);
      }
      return "";
   }

   public static void saveAttendanceSheet(URequestContext ctxt)
           throws UDBAccessException, Exception {
      int branchRid = ctxt.getIntParameter("branchRid");
      int batchRid = ctxt.getIntParameter("batchRid");
      int sem = ctxt.getIntParameter("sem");
      int monthNo = ctxt.getIntParameter("monthNo");
      
      //save total classes
      saveTotClasses(ctxt, branchRid,batchRid, sem, monthNo);
      
      String studRids[] = ctxt.getParameterValues("studRid");
      HashMap<String, Integer> subSeqMap = StudPerformanceManager.getSubSequence(ctxt, branchRid, sem, "sub");
      Set<String> subKeySet = subSeqMap.keySet();
      Iterator<String> keySetIterator = subKeySet.iterator();

      UQueryEngine qe = ctxt.getQueryEngine();
      int userRid = ctxt.getUserRID();
      while (keySetIterator.hasNext()) {
         String subKey = keySetIterator.next();
         String subAttendances[] = ctxt.getParameterValues(subKey);
         int subRid = subSeqMap.get(subKey);
         for (int i = 0; i < studRids.length; i++) {
            saveStudentAttendance(qe,sem, monthNo, subRid, Integer.parseInt(studRids[i]), parseInt(subAttendances[i]), userRid);
         }
      }
   }

   public static void saveTotClasses(URequestContext ctxt, int branchRid,int batchRid, int sem, int monthNo)
           throws UDBAccessException {
      HashMap<String, Integer> subSeqMap = StudPerformanceManager.getSubSequence(ctxt, branchRid, sem, "totSub");
      Set<String> subKeySet = subSeqMap.keySet();
      Iterator<String> keySetIterator = subKeySet.iterator();
      int totClasses = 0;
      int subRid = 0;
      String sql = "";
      UQueryEngine qe = ctxt.getQueryEngine();

      while (keySetIterator.hasNext()) {
         String subKey = keySetIterator.next();
         totClasses = ctxt.getIntParameter(subKey);
         subRid = subSeqMap.get(subKey);
         int atcRid = getTotClassesRowID(qe, batchRid, sem, monthNo, subRid);
         if (atcRid > 0) {
            sql = "update attendance_tot_classes set atc_tot_class=" + totClasses + " WHERE atc_rid=" + atcRid;
            qe.executeUpdate(sql);
         } else {
            sql = "INSERT INTO attendance_tot_classes(atc_sub_rid,atc_batch_rid,atc_sem,atc_month_no,atc_tot_class)"
                    + " VALUES(" + subRid + "," + batchRid + "," + sem + "," + monthNo + "," + totClasses + ")";
            qe.executeInsert(sql);
         }
      }
   }

   public static int getTotClassesRowID(UQueryEngine qe, int batchRid, int sem, int monthNo, int subRid)
           throws UDBAccessException {
      try {
         String sql = "Select * from attendance_tot_classes "
                 + " WHERE atc_sub_rid = " + subRid + " AND atc_batch_rid=" + batchRid + " AND atc_sem=" + sem + " AND atc_month_no=" + monthNo;
         ResultSet rsTotClass = qe.executeQuery(sql);
         if (rsTotClass.first()) {
            return rsTotClass.getInt("atc_rid");
         }
         return 0;
      } catch (Exception ex) {
         throw new UDBAccessException(ex.getMessage(), ex);
      }
   }

   public static int parseInt(String intValue) {
      if (null == intValue || "".equals(intValue.trim())) {
         return 0;
      } else {
         try {
            return Integer.parseInt(intValue);
         } catch (Exception e) {
            return 0;
         }
      }
   }

   public static void saveStudentAttendance(UQueryEngine qe,int sem, int monthNo, int subRid, int studRid,
           int subAttendance, int userRid) throws Exception {
      //check if its an update
      String sql = "select * from attendance where att_stud_rid = " + studRid
              + " AND att_month_no=" + monthNo + " AND att_sub_rid=" + subRid+" AND att_sem="+sem;
      ResultSet rsSAS = qe.executeQuery(sql);
      if (rsSAS.first()) {
         int sasRid = rsSAS.getInt("att_rid");
         rsSAS.close(); //closing coz we are going to update the same record!
         
         sql = "UPDATE attendance SET att_tot_present = " + subAttendance
                 + ",att_mod_datetime = NOW(),att_mod_user_rid=" + userRid + " WHERE att_rid = " + sasRid;
         int notRid = NotificationManager.getNotificationRid(qe, sasRid, NotificationManager.NOT_TYPE_ATTENDANCE);
         if(notRid > 0){
             NotificationManager.resetNotification(qe, notRid, UDate.currentDateTimeDBStr(), UDate.currentDateTimeDBStr());
         }else{
             NotificationManager.addNotification(qe, studRid, NotificationManager.NOT_TYPE_ATTENDANCE, 
                     "Attendance Updated", UDate.currentDateTimeDBStr(), UDate.currentDateTimeDBStr(), sasRid);
         }
         
         qe.executeUpdate(sql);
      } else {
         sql = "INSERT INTO attendance(att_stud_rid,att_sem,att_sub_rid,att_month_no,att_tot_present,att_created_datetime,att_created_user_rid) "
                 + " VALUES(" + studRid +","+sem+","+subRid+ "," +monthNo+","+ subAttendance + ",NOW()," + userRid + ")";
         int attRid = qe.executeInsert(sql);
         
          NotificationManager.addNotification(qe, studRid, NotificationManager.NOT_TYPE_ATTENDANCE, 
                     "Attendance Updated", UDate.currentDateTimeDBStr(),  UDate.currentDateTimeDBStr(),attRid);
      }
   }
}
