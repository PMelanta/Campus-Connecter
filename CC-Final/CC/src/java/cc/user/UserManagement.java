package cc.user;

import java.sql.ResultSet;
import cc.base.UDBAccessException;
import cc.base.UEncryptionService;
import cc.base.UQueryEngine;
import cc.base.URequestContext;

/**
 *
 * @author suhas
 */
public class UserManagement {

    public static boolean checkAvailability(URequestContext ctxt, String userId)
            throws Exception {

        ResultSet rs = ctxt.getQueryEngine().executeQuery("select 1 from care_taker where ct_email='" + userId + "'");
        if (null != rs && rs.first()) {
            return false;
        }

        return true;
    }
    
    public static ResultSet getUser(URequestContext ctxt, String userId, String plainPwd)
            throws Exception {
        
        String sql = "SELECT * FROM student WHERE stud_reg_no='"+userId+"' AND stud_app_password='"+plainPwd+"'";
        return ctxt.getQueryEngine().executeQuery(sql);
        
    }
    public static ResultSet getOAUser(URequestContext ctxt, String mobileNo)
            throws Exception {
        
        return ctxt.getQueryEngine().executeQuery("select * from old_age "
                                                    + " join care_taker on(oa_ct_rid = ct_rid) "
                                                    + " where oa_mobile_no='" + mobileNo + "'");
        
    }
    public static void updateOAUserHomeLoc(URequestContext ctxt, int oaRid, double lat, double lon)
            throws Exception {
        
        String sql = "UPDATE old_age "
                + " SET oa_home_latitude="+lat
                + ", oa_home_longitude="+lon
                + ", oa_last_update_datetime=NOW()"
                +" WHERE oa_rid="+oaRid;
        
        ctxt.getQueryEngine().executeUpdate(sql);
        
    }
    public static void updateOAUserCurLoc(URequestContext ctxt, int oaRid, double lat, double lon)
            throws Exception {
        
        String sql = "UPDATE old_age "
                + " SET oa_cur_latitude="+lat
                + ", oa_cur_longitude="+lon
                + ", oa_last_update_datetime=NOW()"
                +" WHERE oa_rid="+oaRid;
        
        ctxt.getQueryEngine().executeUpdate(sql);
        
    }
    public static ResultSet getOAStatusList(URequestContext ctxt, int oaRid)
            throws Exception {
        
        return ctxt.getQueryEngine().executeQuery("SELECT status_rid,status_desc,"
                + " IFNULL(oa_cur_status_rid,0) AS oa_cur_status_rid FROM oa_status " +
                " LEFT JOIN old_age ON(status_rid = oa_cur_status_rid AND oa_rid = "+oaRid+")" +
                " WHERE status_is_active = 1" +
                " ORDER BY status_desc");
        
    }
    
    public static ResultSet getUser(URequestContext ctxt, String email)
            throws Exception {
       
        return ctxt.getQueryEngine().executeQuery("select * from care_taker where ct_email='" 
                + email + "'");
        
    }

    public static int updateUserDetails(URequestContext ctxt, String email, String usn, String password, String mobile, String bkpNo)
            throws Exception {
        
        ResultSet rs = ctxt.getQueryEngine().executeQuery("select * from student where stud_reg_no = '"+usn+"'");
        if( ! rs.first()){
            throw new Exception("INVALID");
        }else if(rs.first() && rs.getInt("is_registered_for_app") == 1){
            throw new Exception("ALREADY_REGISTERED");
        }
        
        int studRid = 0;
        
        if(rs.first()){
         studRid = rs.getInt("stud_rid");
        }
        
        String sql = "UPDATE student "
                + " SET stud_mobile_no='"+mobile+"'"
                + ", stud_bkp_no='"+bkpNo+"'"
                + ", stud_email='"+email+"'"
                + ", stud_app_password='"+password+"'"
                + ", is_registered_for_app=1"
                + " WHERE stud_rid="+studRid;
        ctxt.getQueryEngine().executeUpdate(sql);
        return studRid;
    }

    static ResultSet getOARecords(URequestContext ctxt, int ctRid) throws UDBAccessException {
        return ctxt.getQueryEngine().executeQuery("SELECT old_age.*,status_desc,DATEDIFF(CURRENT_DATE ,oa_dob)/365 as age "
                + " FROM old_age left join oa_status ON(oa_cur_status_rid=status_rid) WHERE oa_ct_rid = "+ctRid+" ORDER BY oa_name");
    }

    static void updateOAStatus(URequestContext ctxt, int userRid, int statusRid) throws UDBAccessException{
        
        ctxt.getQueryEngine().executeUpdate("UPDATE  old_age "
                + " SET oa_cur_status_rid="+statusRid
                + " ,oa_cur_status_update_datetime=NOW()"
                + " WHERE oa_rid = "+userRid);
        
    }
    
    static ResultSet getLocations(UQueryEngine qe) throws UDBAccessException{
        String sql = "SELECT * FROM location WHERE loc_is_active=1 ORDER BY loc_name ";
        return qe.executeQuery(sql);
    }
}
