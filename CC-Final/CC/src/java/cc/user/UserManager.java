/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.user;

import cc.base.UQueryEngine;
import cc.base.URequestContext;
import cc.base.UDBAccessException;
import java.sql.ResultSet;
import java.sql.SQLException;
import cc.util.UDate;

/**
 *
 * @author suhas
 */
public class UserManager {
    
    
    static int saveUserDetails(URequestContext ctxt, String name, String mobileNo, String address, String dob,
             String profilePicFileName,int ctRid) throws Exception {
        //check whether user already exists

        String sql = "SELECT * FROM old_age WHERE oa_mobile_no = '" + mobileNo + "'";

        ResultSet rs = ctxt.getQueryEngine().executeQuery(sql);
        int userRid;
        if (rs.first()) {
            userRid = rs.getInt("oa_rid");
            //update the existing Record
            sql = "UPDATE  old_age "
                    + " SET oa_name='" + name + "',"
                    + " oa_mobile_no='" + mobileNo + "',"
                    + " oa_dob='" + UDate.displayToDB(dob) + "',"
                    + " oa_address='" + address + "',"
                    + " oa_pic_name='" + profilePicFileName + "'"
                    + " WHERE oa_rid =  " + userRid;
            ctxt.getQueryEngine().executeUpdate(sql);
        } else {
            sql = "INSERT INTO old_age(oa_name,oa_mobile_no,oa_dob,oa_address,"
                    + "oa_pic_name,oa_last_update_datetime,oa_is_active,oa_ct_rid)"
                    + "VALUES('" + name + "','" + mobileNo + "','" + UDate.displayToDB(dob) + "',"
                    + "'" + address + "','" + profilePicFileName + "',NOW(),1,"+ctRid+")";

            userRid = ctxt.getQueryEngine().executeInsert(sql);
        }
        return userRid;
    }

   

}
