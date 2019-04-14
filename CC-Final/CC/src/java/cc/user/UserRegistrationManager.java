package cc.user;

import java.sql.ResultSet;
import cc.base.URequestContext;

/**
 *
 * @author suhas
 */
public class UserRegistrationManager {

    public static boolean checkAvailability(URequestContext ctxt, String userId)
            throws Exception {

        ResultSet rs = ctxt.getQueryEngine().executeQuery("select 1 from u_user where user_id='" + userId + "'");
        if (null != rs && rs.first()) {
            return false;
        }

        return true;
    }

    public static void saveUserDetails(URequestContext ctxt, String userId, String userName, String company,
            String email, String website, String gender, String dob, String phone, String mobile,
            String avatar, String about)
            throws Exception {
        if(dob != null){
            dob = "'"+dob+"'";
        }
        if(avatar != null){
            avatar = "'"+avatar+"'";
        }
        String sql = "INSERT INTO u_user(user_id,user_full_name,user_company,user_email,user_url,user_gender,"
                + "user_dob,user_phone,user_mobile,user_avatar,user_about,user_valid,user_row_invalidated)"
                + " VALUES('" + userId + "','" + userName + "','" + company + "','" + email + "','"
                + website + "','" + gender + "'," + dob + ",'" + phone + "','" + mobile + "'," + avatar + ",'" + about + "',1,0)";
        ctxt.getQueryEngine().executeInsert(sql);
    }
}
