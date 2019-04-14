/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.subject;

import java.sql.ResultSet;
import cc.base.UDBAccessException;
import cc.base.URequestContext;
import cc.util.UString;

/**
 *
 * @author suhas
 */
public class SubjectManager {
   
   public static ResultSet getSubjectDetails(URequestContext ctxt, int subjectRid)
           throws UDBAccessException {
      String sql = "select sub_rid,sub_branch_rid,sub_sem,sub_name,sub_code,sub_seq,sub_internal_max,sub_internal_min "
              + " ,sub_external_max,sub_external_min,subject_details.is_active,branch_rid,branch_name,branch_code,no_of_sems "
              + " from subject_details "
              + " join branch on(sub_branch_rid = branch_rid) "
              + " where subject_details.row_invalidated = 0";
      if (subjectRid > 0) {
         sql += " AND sub_rid = " + subjectRid;
      }
      sql += " ORDER BY sub_branch_rid,sub_sem,sub_seq";
      return ctxt.getQueryEngine().executeQuery(sql);
   }
   
   public static void saveSubjectDetails(URequestContext ctxt,int subjectRid,String subjectName,String subjectCode,
           int branchRid,int sem,int maxInternal,int minInternal,int maxExternal,int minExternal,int subSeq,int isActive)
           throws UDBAccessException {
      String sql = "";
      if (subjectRid > 0) {
         if(isDuplicateSeq(ctxt, subjectRid, branchRid, sem, subSeq)){
            ctxt.setAttribute("errorMessage", "Duplicate sequence number (should be unique for each branch)");
            throw new UDBAccessException("Duplicate sequence number (should be unique for each branch)", null);
         }
         sql = "UPDATE subject_details "
                 + " SET sub_name = '" + UString.escapeSpecialChars(subjectName) + "'"
                 + " , sub_code = '" + UString.escapeSpecialChars(subjectCode) + "'"
                 + " , sub_branch_rid = " + branchRid
                 + " , sub_sem = " + sem
                 + " , sub_internal_max = " + maxInternal
                 + " , sub_internal_min = " + minInternal
                 + " , sub_external_max = " + maxExternal
                 + " , sub_external_min = " + minExternal
                 + " , sub_seq = " + subSeq
                 + " , mod_datetime = NOW() "
                 + " , mod_user_rid = " + ctxt.getUserRID()
                 + " , is_active = " + isActive
                 + " WHERE sub_rid = " + subjectRid;
         ctxt.getQueryEngine().executeUpdate(sql);
      } else {
         if(isDuplicateSeq(ctxt, subjectRid, branchRid, sem, subSeq)){
            ctxt.setAttribute("errorMessage", "Duplicate sequence number (should be unique for each branch)");
            throw new UDBAccessException("Duplicate sequence number (should be unique for each branch)", null);
         }
         sql = "INSERT INTO subject_details(sub_name,sub_code,sub_branch_rid,sub_sem,sub_internal_max,"
                 + "sub_internal_min,sub_external_max,sub_external_min,sub_seq,is_active,created_user_rid,created_datetime) "
                 + " VALUES('" + UString.escapeSpecialChars(subjectName) + "','" + UString.escapeSpecialChars(subjectCode) + "'"
                 + "," + branchRid + "," + sem + "," + maxInternal + ","+ minInternal + ","+ maxExternal + ","+ minExternal+ ","+ subSeq  
                 + "," + isActive + "," + ctxt.getUserRID() + ",NOW())";
         ctxt.getQueryEngine().executeInsert(sql);

      }
   }
   
   public static boolean isDuplicateSeq(URequestContext ctxt,int subjectRid,int branchRid,int sem,int subSeq)
           throws UDBAccessException{
      String sql = "SELECT * FROM subject_details WHERE row_invalidated = 0 AND sub_branch_rid = "+branchRid+" AND sub_sem = "+sem+" AND sub_seq="+subSeq;
      if(subjectRid>0){
         sql += " AND sub_rid<>"+subjectRid;
      }
      try{
      ResultSet rsDupSeq = ctxt.getQueryEngine().executeQuery(sql);
      if(rsDupSeq.first()){
         return true;
      }
      }catch(Exception ex){
         throw new UDBAccessException(ex.getMessage(),ex);
      }
      return false;
   }
   
   public static void deleteSubject(URequestContext ctxt, int subjectRid)
           throws UDBAccessException {
      String sql = "UPDATE subject_details "
              + " SET row_invalidated = 1,"
              + " mod_datetime = NOW(),"
              + " mod_user_rid =  "+ctxt.getUserRID()
              + " WHERE sub_rid = " + subjectRid;
      ctxt.getQueryEngine().executeUpdate(sql);
   }
}
