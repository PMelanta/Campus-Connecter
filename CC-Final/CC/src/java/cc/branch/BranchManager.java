/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.branch;

import java.sql.ResultSet;
import cc.base.UDBAccessException;
import cc.base.UQueryEngine;
import cc.base.URequestContext;
import cc.util.UString;

/**
 *
 * @author suhas
 */
public class BranchManager {

   public static final int EXAM_INTERNAL = 1;
   public static final int EXAM_EXTERNAL = 2;

   public static ResultSet getBranchDetails(URequestContext ctxt, int branchRid)
           throws UDBAccessException {
      String sql = "select branch_rid,branch_name,branch_code,no_of_sems,tot_intake,no_of_internals,is_active "
              + " from branch where row_invalidated = 0";
      if (branchRid > 0) {
         sql += " AND branch_rid = " + branchRid;
      }
      return ctxt.getQueryEngine().executeQuery(sql);
   }
   
   
    public static int saveBatchDetails(URequestContext ctxt,int branchRid,String batchName) throws UDBAccessException{
       String sql="insert into stud_batch(batch_branch_rid,batch_name) values('"+branchRid+"','"+batchName+"')";
   return ctxt.getQueryEngine().executeInsert(sql);
   }
    
   public static ResultSet getBatchDetails(URequestContext ctxt, int branchRid)
           throws UDBAccessException {
      String sql = "select * from stud_batch where batch_is_active = 1";
      if (branchRid > 0) {
         sql += " AND batch_branch_rid = " + branchRid;
      }
      sql += " ORDER BY batch_name";
      return ctxt.getQueryEngine().executeQuery(sql);
   }

   public static void saveBranchDetails(URequestContext ctxt, int branchRid, String branchName,
           String branchCode, int totSems, int totIntake, int noOfInternals, int isActive)
           throws UDBAccessException {
      String sql;
      if (branchRid > 0) {
         sql = "UPDATE branch "
                 + " SET branch_name = '" + UString.escapeSpecialChars(branchName) + "'"
                 + " , branch_code = '" + UString.escapeSpecialChars(branchCode) + "'"
                 + " , no_of_sems = " + totSems
                 + " , tot_intake = " + totIntake
                 + " , no_of_internals = " + noOfInternals
                 + " , is_active = " + isActive
                 + " WHERE branch_rid = " + branchRid;
         ctxt.getQueryEngine().executeUpdate(sql);
      } else {
         sql = "INSERT INTO branch(branch_name,branch_code,no_of_sems,tot_intake,no_of_internals,is_active,created_user_rid,created_datetime) "
                 + " VALUES('" + UString.escapeSpecialChars(branchName) + "','" + UString.escapeSpecialChars(branchCode) + "'"
                 + "," + totSems + "," + totIntake + "," + noOfInternals + "," + isActive + "," + ctxt.getUserRID() + ",NOW())";
         branchRid = ctxt.getQueryEngine().executeInsert(sql);
      }
      updateExamDetails(ctxt, branchRid, noOfInternals);
   }

   public static void updateExamDetails(URequestContext ctxt, int branchRid, int noOfInternals)
           throws UDBAccessException {
      try {
         int createdExams = 0;
         UQueryEngine qe = ctxt.getQueryEngine();
         String sql = "SELECT COUNT(1) as existing_exams FROM exams WHERE exam_branch_rid = " + branchRid + " AND exam_type=" + EXAM_INTERNAL;
         ResultSet rs = qe.executeQuery(sql);
         if (rs.first()) {
            createdExams = rs.getInt("existing_exams");
         }
         sql = "update exams set exam_is_active = 1 where exam_branch_rid = " + branchRid;
         qe.executeUpdate(sql);

         if (noOfInternals > createdExams) {
            createdExams = createdExams == 0 ? 1 : createdExams + 1;
            for (int i = createdExams; i <= noOfInternals; i++) {
               sql = "INSERT INTO exams(exam_branch_rid,exam_name,exam_type,exam_seq)"
                       + " VALUES(" + branchRid + ",'Internal " + i + "'," + EXAM_INTERNAL + "," + i + ")";
               qe.executeInsert(sql);
            }
         } else {
            sql = "update exams set exam_is_active = 0 where exam_branch_rid = " + branchRid + " AND exam_seq>" + noOfInternals;
            qe.executeUpdate(sql);
         }

         sql = "SELECT * FROM exams WHERE exam_branch_rid = " + branchRid + " AND exam_type=" + EXAM_EXTERNAL;
         rs = qe.executeQuery(sql);
         if (!rs.first()) {
            sql = "INSERT INTO exams(exam_branch_rid,exam_name,exam_type,exam_seq)"
                    + " VALUES(" + branchRid + ",'External'," + EXAM_EXTERNAL + ",-1)";
            qe.executeInsert(sql);
         }
      } catch (Exception ex) {
         throw new UDBAccessException(ex.getMessage(), ex);
      }
   }

   public static void deleteBranch(URequestContext ctxt, int branchRid)
           throws UDBAccessException {
      String sql = "UPDATE branch "
              + " SET row_invalidated = 1 "
              + " WHERE branch_rid = " + branchRid;
      ctxt.getQueryEngine().executeUpdate(sql);
   }
}
