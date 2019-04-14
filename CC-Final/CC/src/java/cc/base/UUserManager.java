package cc.base;

import cc.util.UDate;
import java.sql.*;
import java.util.*;


import javax.servlet.http.*;

import org.apache.log4j.Logger;

public class UUserManager {

    static Logger logger = Logger.getLogger(UUserManager.class);
    public static final int AUDIT_EVENT_INDEX_ROLES_ASSIGNED_TO_USER = 192;

    public static String getEmailId(URequestContext ctxt, int userRid) throws Exception {
        String emailId = "";
        String sql = "SELECT USER_EMAIL FROM U_USER where user_rid=" + userRid;
        ResultSet rsEmail = ctxt.getQueryEngine().executeQuery(sql);
        if (null != rsEmail && rsEmail.first()) {
            emailId = rsEmail.getString("USER_EMAIL");
        }

        return emailId;
    }

    public static String getEmailId(URequestContext ctxt, String userLoginId) throws Exception {
        String emailId = "";
        String sql = "SELECT USER_EMAIL FROM U_USER where user_id='" + userLoginId + "'";
        ResultSet rsEmail = ctxt.getQueryEngine().executeQuery(sql);
        if (null != rsEmail && rsEmail.first()) {
            emailId = rsEmail.getString("USER_EMAIL");
        }

        return emailId;
    }

    public static int getUserRid(URequestContext ctxt, String userId, String password) throws Exception {
        UUser user = loginUser(ctxt.getProductRID(), userId, password, ctxt.getHttpRequest(), false);
        if (null != user) {
            return user.getUserRID();
        }

        return -1;
    }

    public static String getNewPasswordPlain(URequestContext ctxt, String userLoginId) throws Exception {
        String password = null;
        String sql = "SELECT 1 FROM U_USER where user_id='" + userLoginId + "'";
        ResultSet rsPassword = ctxt.getQueryEngine().executeQuery(sql);
        if (null != rsPassword && rsPassword.first()) {
            Random generator = new Random();
            int randomNo = generator.nextInt();

            UEncryptionService es = UEncryptionService.getInstance();
            String encryptedPassword = es.encrypt(String.valueOf(randomNo));
            ctxt.getQueryEngine().executeUpdate("UPDATE U_USER SET USER_PASSWORD='" + encryptedPassword + "' WHERE user_id='" + userLoginId + "'");
            ctxt.getQueryEngine().executeUpdate("UPDATE U_USER SET user_new_password=1 WHERE user_id='" + userLoginId + "'");
            password = String.valueOf(randomNo);
        }
        return password;
    }

    public static ResultSet getUsers(URequestContext ctxt, int productRID) 
                        throws Exception{
        String sql = "SELECT u_user.user_rid,user_full_name from u_user "
                + " JOIN user_project_map ON(u_user.user_rid=user_project_map.user_rid OR u_user.user_rid = "
                + ctxt.getUserRID() + ") WHERE project_rid=" + ctxt.getProjectRID()
                +" GROUP BY u_user.user_rid";
        return ctxt.getQueryEngine().executeQuery(sql);
    }

    public UUserManager() {
    }

    public static UUser loginUser(int productRID, String loginID, String password,
            HttpServletRequest request, boolean updateStatus)
            throws UAuthenticationException, UBaseException {

        UEncryptionService es = UEncryptionService.getInstance();

        String encryptedPassword = es.encrypt(password);

        logger.debug("Login request: User = " + loginID
                + ", Password = '" + encryptedPassword + "'");


        UQueryEngine qe = null;

        try {
            qe = new UQueryEngine();

            UUser user = null;
            if (updateStatus) {
                user = login(qe, loginID, encryptedPassword);
            } else {
                user = validateUser(qe, loginID, encryptedPassword);
            }
            if (user != null) {
                logger.debug("Login succeeded!");

                UAuditTrail.addEntry(qe, request, UAuditTrail.AUDIT_LOGIN, 0,
                        user.getUserRID(), 0, 0, "", "Login id: " + loginID);
                if (updateStatus) {
                    updateStatus(qe, loginID);
                }
            } else {
                logger.debug("Login failed!");

                UAuditTrail.addEntry(qe, request, UAuditTrail.AUDIT_LOGIN_FAILED, 0, "Login id: " + loginID);
            }

            return user;

        } catch (Exception e) {
            throw new UBaseException(e.getMessage(), e);
        } finally {
            if (qe != null) {
                qe.close();
            }
        }
    }

    private static void updateStatus(UQueryEngine qe, String loginID)
            throws UDBAccessException {

        String sql = "";
        sql = "UPDATE u_user set user_is_logged_in = 1 where  user_valid = 1 AND user_id = '" + loginID + "'";

        qe.executeUpdate(sql);

    }

    public static boolean hasPrivilege(URequestContext ctxt, int prodRID, int featureRID, int userRID)
            throws UDBAccessException, SQLException {

        String sql = "select * from u_user_priv_view where "
                + "up_feature_rid = " + featureRID
                + " and up_user_rid = " + userRID
                + " and up_feature_prod_rid = " + prodRID;


        return ctxt.getQueryEngine().executeQuery(sql).next();
    }

    public static boolean hasPrivilege(UQueryEngine qe, int prodRID, String privName, int userRID)
            throws UDBAccessException, SQLException {
        /*
         *u_user_priv_view view needs to be reviewed. Below query runs without indexes. Akhil
         *
        String sql = "select * from u_user_priv_view where " +
        "up_feature_code = '" + privName +
        "' and up_user_rid = " + userRID +
        " and up_feature_prod_rid = " + prodRID;
         */

        String sql = "select user_rid  from u_user "
                + "join u_user_role_map on user_rid = ur_user_rid "
                + "join u_role on ur_role_rid = role_rid "
                + "join u_priv on priv_role_rid = role_rid "
                + "join u_feature on priv_feature_rid = feature_rid  "
                + "where user_valid = 1 and feature_valid = 1 and feature_code = '" + privName + "'"
                + " and user_rid = " + userRID + " and feature_prod_rid = " + prodRID;

        return qe.executeQuery(sql).first();
    }

    public static boolean hasPrivilege(URequestContext ctxt, int prodRID, String privName, int userRID)
            throws UDBAccessException, SQLException {

        UQueryEngine qe = ctxt.getQueryEngine();

        return hasPrivilege(qe, prodRID, privName, userRID);
    }

    public static boolean hasPrivilege(URequestContext ctxt, String privName)
            throws UDBAccessException, SQLException {

        UQueryEngine qe = ctxt.getQueryEngine();

        return hasPrivilege(qe, ctxt.getProductRID(), privName, ctxt.getUserRID());
    }

    public static boolean hasPrivilege(int prodRID, String privName, int userRID)
            throws UDBAccessException, SQLException {

        UQueryEngine qe = new UQueryEngine();

        boolean ans = hasPrivilege(qe, prodRID, privName, userRID);
        String a = "";

        qe.close();

        return ans;
    }

    public static ResultSet getAuthorizedUsers(URequestContext ctxt, String featureCode, int entityRid)
            throws UDBAccessException {

        String sql = "select * from ( "
                + "select distinct u_user.* from u_user "
                + "join u_user_role_map on user_rid = ur_user_rid "
                + "join u_role on ur_role_rid = role_rid "
                + "join u_priv on priv_role_rid = role_rid "
                + "join u_feature on priv_feature_rid = feature_rid  "
                + "where  user_valid = 1 and feature_code = '" + featureCode + "' "
                + "and user_entity_rid = " + entityRid + " ) usr "
                + "order by user_full_name";

        UQueryEngine qe = ctxt.getQueryEngine();

        return qe.executeQuery(sql);
    }

    public static ResultSet getAuthorizedUsers(URequestContext ctxt, String featureCode)
            throws UDBAccessException {
        return getAuthorizedUsers(ctxt, featureCode, ctxt.getUserEntityRID());
    }

    public static ResultSet getAuthorizedUsers(URequestContext ctxt, int[] features)
            throws UDBAccessException, SQLException {

        String sql = "select u_user.*, count(distinct up_feature_rid) fcnt from u_user_priv_view, u_user "
                + " where up_user_rid = user_rid and up_feature_rid in (";

        String fids = "";

        for (int i = 0; i < features.length; i++) {
            if (i == 0) {
                fids = features[i] + "";
            } else {
                fids = fids + "," + features[i];
            }
        }

        sql = sql + fids + ") group by user_rid having fcnt = " + features.length;

        UQueryEngine qe = ctxt.getQueryEngine();

        return qe.executeQuery(sql);
    }

    public static void clearPassword(String userName)
            throws UDBAccessException, SQLException {

        String sql;
        sql = "update u_user set user_password = '' where user_id = '" + userName + "'";
        UQueryEngine qe = new UQueryEngine();
        qe.executeUpdate(sql);
        qe.close();
    }

    public static String setRandomPassword(String userName)
            throws UDBAccessException {

        Random r = new Random();
        String newPwd = userName;
        int len = userName.length();
        int number = r.nextInt();

        newPwd = newPwd + number + "07";
        UEncryptionService es = UEncryptionService.getInstance();
        String encryptedPassword = es.encrypt(newPwd);

        String sql = "update u_user set user_password = '" + encryptedPassword + "' where user_id = '" + userName + "'";
        UQueryEngine qe = new UQueryEngine();
        qe.executeUpdate(sql);
        return newPwd;
    }

    private static UUser login(UQueryEngine qe, String userName, String password)
            throws UAuthenticationException {

        ResultSet rs = null;

        int user_rid = 0;
        String user_name = "";
        String user_title = "";
        String user_password = "";

        try {

            // Authenticate the user.
            String sql = null;

            sql = "SELECT user_rid, user_password, user_full_name, user_gender,user_new_password from u_user "
                    + "WHERE user_id = '" + userName + "'" + " AND (user_password = '" + password + "'"
                    + " OR user_password is NULL OR user_password = '') and user_valid = 1";

            rs = qe.executeQuery(sql);

            if (rs.next() == false) // Authentication failed
            {
                return null;
            }

            // User successfully authenticated.


            int u_rid = rs.getInt("user_rid");
            user_password = rs.getString("user_password");
            user_name = rs.getString("user_full_name");

            int passwordStatus = rs.getInt("user_new_password");

            String gender = rs.getString("user_gender");

            if (user_password == null) {
                user_password = "";
            }

            // Yes, the user has the required permission. Store the user RID and return true.

            user_rid = u_rid;

            // @@ The following code is not I18N compliant!!!
            if (gender != null) {
                if (gender.equals("M")) {
                    user_title = "Mr.";
                } else if (gender.equals("F")) {
                    user_title = "Ms.";
                } else {
                    user_title = "";
                }
            }

            qe.executeUpdate("UPDATE U_USER SET user_new_password = 0 WHERE USER_RID =" + u_rid);

            return new UUser(user_rid, user_name, user_title, user_password, passwordStatus);

        } catch (Exception e) {
            throw new UAuthenticationException("Exception while attempting to login! " + e.getMessage(), e);
        } finally {

            rs = null;
        }
    }

    private static UUser validateUser(UQueryEngine qe, String userName, String password)
            throws UAuthenticationException {

        ResultSet rs = null;

        int user_rid = 0;
        String user_name = "";
        String user_title = "";
        String user_password = "";

        try {

            // Authenticate the user.
            String sql = null;

            sql = "SELECT user_rid, user_password, user_full_name, user_gender from u_user "
                    + " WHERE user_id = '" + userName + "'" + " AND user_password = '" + password + "'"
                    + " and user_valid = 1";

            rs = qe.executeQuery(sql);

            if (rs.next() == false) // Authentication failed
            {
                return null;
            }

            // User successfully authenticated.


            int u_rid = rs.getInt("user_rid");
            user_password = rs.getString("user_password");
            user_name = rs.getString("user_full_name");

            String gender = rs.getString("user_gender");

            if (user_password == null) {
                user_password = "";
            }

            // Yes, the user has the required permission. Store the user RID and return true.

            user_rid = u_rid;

            // @@ The following code is not I18N compliant!!!
            if (gender != null) {
                if (gender.equals("M")) {
                    user_title = "Mr.";
                } else if (gender.equals("F")) {
                    user_title = "Ms.";
                } else {
                    user_title = "";
                }
            }

            return new UUser(user_rid, user_name, user_title, user_password);

        } catch (Exception e) {
            throw new UAuthenticationException("Exception while attempting to login! " + e.getMessage(), e);
        } finally {

            rs = null;
        }
    }

    public static void setPassword(int userRID, String password)
            throws UDBAccessException {

        UQueryEngine qe = null;

        try {

            qe = new UQueryEngine();

            UEncryptionService es = UEncryptionService.getInstance();

            String encrypted_password = es.encrypt(password);

            String sql = "update u_user set user_password = '"
                    + encrypted_password + "' where user_rid = " + userRID;

            qe.executeUpdate(sql);

            return;
        } catch (Exception e) {
            throw new UDBAccessException("Failed to set password! " + e.getMessage(), e);
        } finally {
            if (qe != null) {
                qe.close();
            }
        }
    }

    public static void resetPassword(int userRID)
            throws UDBAccessException {

        UQueryEngine qe = null;

        try {
            qe = new UQueryEngine();

            String sql = "update u_user set user_password = null where user_rid = " + userRID;

            qe.executeUpdate(sql);

        } catch (Exception e) {
            throw new UDBAccessException("Failed to reset password! " + e.getMessage(), e);
        } finally {
            if (qe != null) {
                qe.close();
            }
        }
    }

    //@suhas
    public static ResultSet getUser(URequestContext ctxt, int userRID)
            throws UDBAccessException {

        String sql = "select * from u_user where user_rid = " + userRID;

        return ctxt.getQueryEngine().executeQuery(sql);
    }

    public static String getUserEmail(URequestContext ctxt, int userRID)
            throws UDBAccessException {

        ResultSet rs = getUser(ctxt, userRID);

        try {
            rs.next();

            return rs.getString("user_email");
        } catch (SQLException e) {
            throw new UDBAccessException(e.getMessage(), e);
        }
    }

    //@suhas
    public static ResultSet getUserRoles(URequestContext ctxt, int userRID)
            throws UDBAccessException {

        String sql = "select * from u_user_role_map, "
                + " u_role where ur_role_rid = role_rid and ur_user_rid = " + userRID;

        return ctxt.getQueryEngine().executeQuery(sql);
    }

    

    public static Vector getAccessibleCommands(URequestContext ctxt, int prodRID, int userRID)
            throws UAuthorizationException {

        String sql = "select distinct feature_name, feature_command, feature_help, "
                + "feature_group, feature_seq_num, feature_group_desc, "
                + "feature_bitmap_fname, feature_rid from u_feature, u_user_role_map, u_priv "
                + "where u_user_role_map.ur_user_rid = " + userRID + " and "
                + "u_user_role_map.ur_role_rid = u_priv.priv_role_rid and "
                + "u_priv.priv_feature_rid = u_feature.feature_rid and "
                + "u_feature.feature_prod_rid = " + prodRID + " and "
                + "u_feature.feature_valid = 1 ";


        sql = sql + " order by feature_group, feature_seq_num";

        try {

            UQueryEngine qe = ctxt.getQueryEngine();

            ResultSet rs = qe.executeQuery(sql);

            if (rs == null) {
                return null;
            }

            Vector v = new Vector();

            return _executeAccessibleCommandsSQL(ctxt, sql);

        } catch (Exception e) {
            throw new UAuthorizationException("Failed to get accessible commands!", e);
        }

    }

    public static Vector getAccessibleCommands(URequestContext ctxt, int prodRID, int userRID, String featureGroup)
            throws UAuthorizationException {

        String sql = "select distinct feature_name, feature_command, feature_help, feature_group, feature_seq_num, feature_group_desc, "
                + "feature_bitmap_fname, feature_rid from u_feature, u_user_role_map, u_priv "
                + "where u_user_role_map.ur_user_rid = " + userRID + " and "
                + "u_user_role_map.ur_role_rid = u_priv.priv_role_rid and "
                + "u_priv.priv_feature_rid = u_feature.feature_rid and "
                + "u_feature.feature_prod_rid = " + prodRID + " and "
                + "u_feature.feature_valid = 1 ";

        if (featureGroup != null) {
            sql = sql + "and feature_group = '" + featureGroup + "' ";
        }

        sql = sql + "order by feature_group, feature_seq_num";

        return _executeAccessibleCommandsSQL(ctxt, sql);
    }

    private static Vector _executeAccessibleCommandsSQL(URequestContext ctxt, String sql)
            throws UAuthorizationException {

        try {

            UQueryEngine qe = ctxt.getQueryEngine();

            ResultSet rs = qe.executeQuery(sql);

            if (rs == null) {
                return null;
            }

            Vector v = new Vector();

            while (rs.next()) {

                UFeature f =
                        new UFeature(rs.getString("feature_name"),
                        rs.getString("feature_command"),
                        rs.getString("feature_help"),
                        rs.getString("feature_group"),
                        rs.getString("feature_group_desc"),
                        rs.getInt("feature_rid"));

                v.addElement(f);
            }
            return v;

        } catch (Exception e) {
            throw new UAuthorizationException("Failed to get accessible commands!", e);
        }

    }

    public static Vector getAccessibleCommands(URequestContext ctxt, int prodRID, int userRID, int parentFeatureGroup)
            throws UAuthorizationException {

        String sql = "select distinct feature_name, feature_command, feature_help, feature_group, feature_seq_num, feature_group_desc, "
                + "feature_parent_group, feature_bitmap_fname, feature_rid, feature_code, feature_offline_supported, feature_is_unit_specific "
                + "from u_feature, u_user_role_map, u_priv "
                + "where u_user_role_map.ur_user_rid = " + userRID + " and "
                + "u_user_role_map.ur_role_rid = u_priv.priv_role_rid and "
                + "u_priv.priv_feature_rid = u_feature.feature_rid and "
                + "u_feature.feature_prod_rid = " + prodRID + " and feature_group != 'P' and "
                + "u_feature.feature_valid = 1 and " + "feature_parent_group = " + parentFeatureGroup
                + " order by feature_group, feature_seq_num";

        if (parentFeatureGroup == 0) {
            sql = " select distinct a.*, b.feature_name as root_parent_feature_name,  "
                    + " b.feature_rid as  root_parent_feature_rid "
                    + " from u_user_role_map, u_priv, u_feature a  left join u_feature b on  "
                    + " b.feature_rid = a.feature_parent_group where  u_user_role_map.ur_user_rid = " + userRID
                    + " and u_user_role_map.ur_role_rid = u_priv.priv_role_rid   "
                    + " and u_priv.priv_feature_rid = a.feature_rid and a.feature_prod_rid = " + prodRID + " and a.feature_group != 'P' "
                    + " and a.feature_valid = 1 and a.feature_parent_group != 0 "
                    + //" and (b.feature_command = '' || b.feature_command is null) " +
                    " order by b.feature_rid, a.feature_group, a.feature_seq_num ";
        }

        try {

            UQueryEngine qe = ctxt.getQueryEngine();

            ResultSet rs = qe.executeQuery(sql);

            if (rs == null) {
                return null;
            }

            Vector v = new Vector();

            while (rs.next()) {
                int root_parent_feature_rid = 0, featureOfflineSupported = 0;
                String root_parent_feature_name = "";
                try {
                    featureOfflineSupported = rs.getInt("feature_offline_supported");
                    root_parent_feature_rid = rs.getInt("root_parent_feature_rid");
                    root_parent_feature_name = rs.getString("root_parent_feature_name");
                } catch (Exception e) {
                    // do nothing;
                }

                UFeature f =
                        new UFeature(rs.getString("feature_name"),
                        rs.getString("feature_command"),
                        rs.getString("feature_help"),
                        rs.getString("feature_group"),
                        rs.getString("feature_group_desc"),
                        rs.getInt("feature_rid"),
                        rs.getInt("feature_parent_group"),
                        root_parent_feature_rid,
                        root_parent_feature_name,
                        rs.getString("feature_code"),
                        featureOfflineSupported,
                        rs.getInt("feature_is_unit_specific"));

                v.addElement(f);
            }

            return v;

        } catch (Exception e) {
            throw new UAuthorizationException("Failed to get accessible commands!", e);
        }
    }

    public static Vector getAccessibleCommands(URequestContext ctxt)
    throws UAuthorizationException {
        
        String sql = " select distinct a.*, b.feature_name as root_parent_feature_name,  " +
                " (case when b.feature_rid is null then 0 else b.feature_rid end) as  root_parent_feature_rid,b.feature_rid, " +
                " c.feature_rid feature_rid_l2, c.feature_code feature_code_l2, c.feature_group feature_group_l2, " +
                " c.feature_name feature_name_l2, c.feature_command feature_command_l2, COALESCE(c.feature_rid, a.feature_parent_group) feature_rid_sorted " +
                " from u_user_role_map, u_priv, " +
                " (select * from u_feature "+
                " ) a " +
                " left join u_feature b on  b.feature_rid = a.feature_parent_group " +
                " LEFT JOIN u_feature c ON c.feature_rid = b.feature_parent_group " +
                " where  u_user_role_map.ur_user_rid = " + ctxt.getUserRID() +
                " and u_user_role_map.ur_role_rid = u_priv.priv_role_rid   " +
                " and u_priv.priv_feature_rid = a.feature_rid " +
                " and a.feature_prod_rid = " + ctxt.getProductRID() +  " and a.feature_group != 'P' " +
                " and a.feature_valid = 1 and a.feature_parent_group != 0 " +
                // " and (b.feature_command = '' or b.feature_command is null) " +
                " order by feature_rid_sorted, b.feature_rid, a.feature_group, a.feature_seq_num ";
        
        try {
            
            UQueryEngine qe = ctxt.getQueryEngine();
            
            ResultSet rs = qe.executeQuery(sql);
            
            if(rs == null)
                return null;
            
            Vector v = new Vector();
            
            while(rs.next()) {
                int root_parent_feature_rid = 0, featureOfflineSupported = 0;
                String root_parent_feature_name = "";
                try{
//                    featureOfflineSupported = rs.getInt("feature_offline_supported");
                    root_parent_feature_rid = rs.getInt("feature_rid_sorted");
                    root_parent_feature_name = rs.getString("root_parent_feature_name");
                } catch( Exception e) {
                    // do nothing;
                }
                
                UFeature f =
                        new UFeature(rs.getString("feature_name"),
                        rs.getString("feature_command"),
                        rs.getString("feature_help"),
                        rs.getString("feature_group"),
                        rs.getString("feature_group_desc"),
                        rs.getInt("feature_rid"),
                        rs.getInt("feature_parent_group"),
                        root_parent_feature_rid,
                        root_parent_feature_name,
                        rs.getString("feature_code"),
                        0,
                        rs.getInt("feature_rid_l2"),
                        rs.getString("feature_name_l2"),
                        rs.getString("feature_command_l2"),
                        rs.getString("feature_code_l2"),
                        rs.getString("feature_group_l2"),
                        0);
                
                v.addElement(f);                
            }
            
            return v;
            
        } catch (Exception e) {
            throw new UAuthorizationException("Failed to get accessible commands!", e);
        }
    }
    
    /*public static Vector getAccessibleCommands(URequestContext ctxt)
            throws UAuthorizationException {

//        String sql = " select distinct a.*, b.feature_rid, b.feature_name as root_parent_feature_name,  "
//                + " (case when b.feature_rid is null then 0 else b.feature_rid end) root_parent_feature_rid, "
//                + " c.feature_rid feature_rid_l2, c.feature_code feature_code_l2, c.feature_group feature_group_l2, "
//                + " c.feature_name feature_name_l2, c.feature_command feature_command_l2, "
//                + " (case when c.feature_rid is null then a.feature_parent_group else c.feature_rid end) feature_rid_sorted "
//                + " from u_user_role_map, u_priv, u_feature a "
//                + " left join u_feature b on  b.feature_rid = a.feature_parent_group "
//                + " LEFT JOIN u_feature c ON c.feature_rid = b.feature_parent_group "
//                + " where  u_user_role_map.ur_user_rid = " + ctxt.getUserRID()
//                + " and u_user_role_map.ur_role_rid = u_priv.priv_role_rid   "
//                + " and u_priv.priv_feature_rid = a.feature_rid "
//                + " and a.feature_prod_rid = " + ctxt.getProductRID() + " and a.feature_group != 'P' "
//                + " and a.feature_valid = 1 and a.feature_parent_group != 0 "
//                + // " and (b.feature_command = '' or b.feature_command is null) " +
//                " order by feature_rid_sorted, b.feature_rid, a.feature_group, a.feature_seq_num ";

//        String sql = " select distinct u_feature.*  "
//        + " from u_user_role_map, u_priv, u_feature "
//        + " where  u_user_role_map.ur_user_rid = " + ctxt.getUserRID()
//        + " and u_user_role_map.ur_role_rid = u_priv.priv_role_rid   "
//        + " and u_priv.priv_feature_rid = u_feature.feature_rid "
//        + " and u_feature.feature_prod_rid = " + ctxt.getProductRID() + " and u_feature.feature_group = 'M' "
//        + " and u_feature.feature_valid = 1 "
//        + " order by u_feature.feature_seq_num,u_feature.feature_group";
        String sql = " select distinct u_feature.*  "
                + " from u_feature "
                + " where u_feature.feature_prod_rid = " + ctxt.getProductRID() + " and u_feature.feature_group = 'M' "
                + " and u_feature.feature_valid = 1 "
                + " order by u_feature.feature_seq_num,u_feature.feature_group";

        try {

            UQueryEngine qe = ctxt.getQueryEngine();

            ResultSet rs = qe.executeQuery(sql);

            if (rs == null) {
                return null;
            }

            Vector v = new Vector();

            while (rs.next()) {
//                int root_parent_feature_rid = 0, featureOfflineSupported = 0;
//                String root_parent_feature_name = "";
//                try {
//                    featureOfflineSupported = rs.getInt("feature_offline_supported");
//                    root_parent_feature_rid = rs.getInt("feature_rid_sorted");
//                    root_parent_feature_name = rs.getString("root_parent_feature_name");
//                } catch (Exception e) {
//                    // do nothing;
//                }

                UFeature f =
                        new UFeature(rs.getString("feature_name"), rs.getString("feature_command"),
                        rs.getString("feature_help"), rs.getString("feature_group"), rs.getString("feature_group_desc"),
                        rs.getInt("feature_rid"));

                v.addElement(f);
            }

            return v;

        } catch (Exception e) {
            throw new UAuthorizationException("Failed to get accessible commands!", e);
        }
    }
*/

    public static void addToFrequentlyUsedCommands(URequestContext ctxt, int userRID, int featureRID)
            throws UDBAccessException {

        String sql = "insert into u_frequently_used_feature (fuf_user_rid, fuf_feature_rid) values ("
                + userRID + ", " + featureRID + ")";

        ctxt.getQueryEngine().executeInsert(sql);
    }

    public static void removeFromFrequentlyUsedCommands(URequestContext ctxt, int userRID, int featureRID)
            throws UDBAccessException {

        String sql = "delete from u_frequently_used_feature where fuf_user_rid = " + userRID
                + " and fuf_feature_rid = " + featureRID;

        ctxt.getQueryEngine().executeUpdate(sql);
    }

    public static Vector getFrequentlyUsedCommands(URequestContext ctxt, int prodRID, int userRID, int parentFeatureGroup)
            throws UAuthorizationException {

        String sql = "select distinct feature_name, feature_command, feature_help, feature_group, feature_seq_num, feature_group_desc, "
                + "feature_parent_group, feature_bitmap_fname, feature_rid, feature_code, feature_offline_supported,feature_is_unit_specific "
                + "from u_feature, u_user_role_map, u_priv, u_frequently_used_feature "
                + "where u_user_role_map.ur_user_rid = " + userRID + " and "
                + "u_user_role_map.ur_role_rid = u_priv.priv_role_rid and "
                + "u_priv.priv_feature_rid = u_feature.feature_rid and "
                + "u_feature.feature_prod_rid = " + prodRID + " and feature_group != 'P' and "
                + "u_feature.feature_valid = 1 and " + "feature_parent_group = " + parentFeatureGroup
                + " and fuf_feature_rid = a.feature_rid and fuf_user_rid = " + userRID
                + " order by root_parent_feature_name, feature_seq_num";

        if (parentFeatureGroup == 0) {
            sql = " select distinct a.*, b.feature_name as root_parent_feature_name,  "
                    + " b.feature_rid as root_parent_feature_rid "
                    + //" if(b.feature_rid is null, 0 , b.feature_rid) as  root_parent_feature_rid " +
                    " from u_user_role_map, u_priv, u_feature a  left join u_feature b on  "
                    + " b.feature_rid = a.feature_parent_group, u_frequently_used_feature where u_user_role_map.ur_user_rid = " + userRID
                    + " and u_user_role_map.ur_role_rid = u_priv.priv_role_rid   "
                    + " and u_priv.priv_feature_rid = a.feature_rid and a.feature_prod_rid = " + prodRID + " and a.feature_group != 'P' "
                    + " and a.feature_valid = 1 and a.feature_parent_group != 0 "
                    + //" and (b.feature_command = '' or b.feature_command is null) " +
                    " and fuf_feature_rid = a.feature_rid and fuf_user_rid = " + userRID
                    + " order by root_parent_feature_name, a.feature_name";
        }

        try {

            UQueryEngine qe = ctxt.getQueryEngine();

            ResultSet rs = qe.executeQuery(sql);

            if (rs == null) {
                return null;
            }

            Vector v = new Vector();

            while (rs.next()) {
                int root_parent_feature_rid = 0, featureOfflineSupported = 0;
                String root_parent_feature_name = "";
                try {
                    featureOfflineSupported = rs.getInt("feature_offline_supported");
                    root_parent_feature_rid = rs.getInt("root_parent_feature_rid");
                    root_parent_feature_name = rs.getString("root_parent_feature_name");
                } catch (Exception e) {
                    // do nothing;
                }

                UFeature f =
                        new UFeature(rs.getString("feature_name"),
                        rs.getString("feature_command"),
                        rs.getString("feature_help"),
                        rs.getString("feature_group"),
                        rs.getString("feature_group_desc"),
                        rs.getInt("feature_rid"),
                        rs.getInt("feature_parent_group"),
                        root_parent_feature_rid,
                        root_parent_feature_name,
                        rs.getString("feature_code"),
                        featureOfflineSupported,
                        rs.getInt("feature_is_unit_specific"));

                v.addElement(f);
            }

            return v;

        } catch (Exception e) {
            throw new UAuthorizationException("Failed to get accessible commands!", e);
        }
    }

    public static Vector getAccessibleCommands(URequestContext ctxt, int prodRID, int userRID, int parentFeatureGroup, String featureGroup)
            throws UAuthorizationException {

        String sql = "select distinct feature_name, feature_command, feature_help, feature_group, feature_seq_num, feature_group_desc, "
                + "feature_parent_group, feature_bitmap_fname, feature_rid ,feature_is_unit_specificfrom u_feature, u_user_role_map, u_priv "
                + "where u_user_role_map.ur_user_rid = " + userRID + " and "
                + "u_user_role_map.ur_role_rid = u_priv.priv_role_rid and "
                + "u_priv.priv_feature_rid = u_feature.feature_rid and "
                + "u_feature.feature_prod_rid = " + prodRID + " and "
                + "u_feature.feature_valid = 1 and " + "feature_parent_group = " + parentFeatureGroup
                + " and feature_group = '" + featureGroup + "'"
                + " order by feature_group, feature_seq_num";

        if (parentFeatureGroup == 0) {
            sql = " select distinct a.*, b.feature_name as root_parent_feature_name,  "
                    + " if(b.feature_rid is null, 0 , b.feature_rid) as  root_parent_feature_rid  "
                    + " from u_user_role_map, u_priv, u_feature a  left join u_feature b on  "
                    + " b.feature_rid = a.feature_parent_group where  u_user_role_map.ur_user_rid = " + userRID
                    + " and u_user_role_map.ur_role_rid = u_priv.priv_role_rid   "
                    + " and u_priv.priv_feature_rid = a.feature_rid and a.feature_prod_rid = " + prodRID
                    + " and a.feature_valid = 1 and a.feature_parent_group != 0 and a.feature_group = '" + featureGroup + "'"
                    + " and (b.feature_command = '' || b.feature_command is null) "
                    + " order by b.feature_rid, a.feature_group, a.feature_seq_num ";
        }

        try {

            UQueryEngine qe = ctxt.getQueryEngine();

            ResultSet rs = qe.executeQuery(sql);

            if (rs == null) {
                return null;
            }

            Vector v = new Vector();

            while (rs.next()) {
                int root_parent_feature_rid = 0;
                String root_parent_feature_name = "";
                try {
                    root_parent_feature_rid = rs.getInt("root_parent_feature_rid");
                    root_parent_feature_name = rs.getString("root_parent_feature_name");
                } catch (Exception e) {
                    // do nothing;
                }

                UFeature f =
                        new UFeature(rs.getString("feature_name"),
                        rs.getString("feature_command"),
                        rs.getString("feature_help"),
                        rs.getString("feature_group"),
                        rs.getString("feature_group_desc"),
                        rs.getInt("feature_rid"),
                        rs.getInt("feature_parent_group"),
                        root_parent_feature_rid,
                        root_parent_feature_name,
                        rs.getString("feature_code"), 0,
                        rs.getInt("feature_is_unit_specific"));

                v.addElement(f);
            }

            return v;

        } catch (Exception e) {
            throw new UAuthorizationException("Failed to get accessible commands!", e);
        }
    }

    public static ResultSet getUsers(URequestContext ctxt, boolean showValidOnly)
            throws UDBAccessException {

        String sql = "select user_rid, user_full_name from u_user";

        if (showValidOnly) {
            sql = sql + " and user_valid = 1";
        }

        sql = sql + " order by user_full_name";

        UQueryEngine qe = ctxt.getQueryEngine();

        ResultSet rs = qe.executeQuery(sql);

        return rs;
    }

    public static boolean userExists(URequestContext ctxt, String userLoginId, int userRID)
            throws UDBAccessException {

        try {
            String sql = "select user_rid from u_user where user_id = '" + userLoginId + "'";


            if (userRID != 0) {
                sql = sql + " and user_rid <> " + userRID;
            }

            UQueryEngine qe = ctxt.getQueryEngine();

            ResultSet rs = qe.executeQuery(sql);

            if (rs != null && rs.next()) {
                return true;
            }

            return false;
        } catch (Exception e) {

            throw new UDBAccessException(e.getMessage(), e);
        }

    }

    public static boolean isUserValid(URequestContext ctxt, String userLoginId, String password)
            throws UDBAccessException {

        try {

            UEncryptionService es = UEncryptionService.getInstance();
            String encrypted_password = es.encrypt(password);
            String sql = "select user_id from u_user where user_id = '" + userLoginId + "' and user_password = '" + encrypted_password;
            UQueryEngine qe = ctxt.getQueryEngine();
            ResultSet rs = qe.executeQuery(sql);
            if (rs != null && rs.next()) {
                return true;
            }

            return false;
        } catch (Exception e) {

            throw new UDBAccessException(e.getMessage(), e);
        }

    }

    public static void setUserRoles(URequestContext ctxt, UQueryEngine qe, int userRID, int[] roleRIDs)
            throws UDBAccessException, SQLException {

        // Remove existing roles
        String sqlDel = "delete from u_user_role_map where ur_user_rid = " + userRID;
        qe.executeUpdate(sqlDel);

        if (roleRIDs == null) {
            return;
        }

        for (int i = 0; i < roleRIDs.length; i++) {

            if (roleRIDs[i] != 0) {
                String sql = "insert into u_user_role_map (ur_user_rid, ur_role_rid) values ("
                        + userRID + ", " + roleRIDs[i] + ")";

                qe.executeInsert(sql);
            }
        }
    }

    private static void reinsertUserRoles(URequestContext ctxt, UQueryEngine qe, int userRID)
            throws UDBAccessException, SQLException, Exception {

        int[] roleRIDs = ctxt.getIntParameterValues("assignedRole");

        setUserRoles(ctxt, qe, userRID, roleRIDs);
    }

    public static void updateUserInfo(URequestContext ctxt, UQueryEngine qe, int userRID, String userName,
            String userPhoneNumber, String userAddress, java.sql.Date userDOB, int userStatus)
            throws UDBAccessException {

        String updateDOB = userDOB != null ? "user_dob = '" + userDOB.toString() + "', " : "";

        String sql = "update u_user set user_full_name = '" + userName + "', "
                + "user_phone = '" + userPhoneNumber + "', "
                + "user_street_addr = '" + userAddress + "', "
                + updateDOB
                + "user_mod_datetime = '" + UDate.nowDBString() + "', "
                + "user_mod_user_rid = " + ctxt.getUserRID() + ", user_valid=" + userStatus
                + " where user_rid = " + userRID;

        qe.executeUpdate(sql);
    }

    public static void updateUserInfo(URequestContext ctxt, UQueryEngine qe, int userRID, String userName,
            String userPhoneNumber, String email, String userAddress, java.sql.Date userDOB,
            String loginID, int roleRID)
            throws UDBAccessException, SQLException, UBaseException {

        if (loginID != null && userExists(ctxt, loginID, userRID)) {
            ctxt.setAttribute("errorMessage", "User with login id " + loginID
                    + " already exists. Please enter another login id.");

            throw new UBaseException("Login id exists", null);
        }

        String updateDOB = userDOB != null ? "user_dob = '" + userDOB.toString() + "', " : "";

        String sql = "update u_user set user_full_name = '" + userName + "', "
                + "user_phone = '" + userPhoneNumber + "', "
                + "user_email = '" + email + "', "
                + (loginID != null ? ("user_id = '" + loginID + "', ") : "")
                + "user_street_addr = '" + userAddress + "', "
                + updateDOB
                + "user_mod_datetime = '" + UDate.nowDBString() + "', "
                + "user_mod_user_rid = " + ctxt.getUserRID()
                + " where user_rid = " + userRID;

        qe.executeUpdate(sql);

        int[] roleRIDs = {roleRID};

        setUserRoles(ctxt, qe, userRID, roleRIDs);
    }

    public static void updateUser(URequestContext ctxt, int userRID)
            throws UDBAccessException, UBaseException {

        String userLoginId = ctxt.getParameter("userLoginId");

        if (userExists(ctxt, userLoginId, userRID)) {
            ctxt.setAttribute("errorMessage", "UserMaster~User with login id " + userLoginId
                    + " already exists. Please enter another login id.");

            throw new UBaseException("UserMaster~Login id exists", null);
        }

        UQueryEngine localQE = null;
        try {
            localQE = ctxt.getQueryEngine();
            localQE.beginTransaction();
            _updateUserDetails(ctxt, localQE, userRID);
            reinsertUserRoles(ctxt, localQE, userRID);

            localQE.commitTransaction();
        } catch (Exception e) {
            logger.error("Error in updating user details");
            throw new UDBAccessException(e.getMessage(), e);
        }
    }

    public static int addBaseUser(URequestContext ctxt, UQueryEngine qe, String userName, String phoneNumber,
            String email, String loginID, int defaultRoleRID)
            throws UDBAccessException, UBaseException {

        try {
            String now = UDate.nowDBString();
            String dateStr = "'" + UDate.nowDBString() + "'";

            // If loginID is specified, check if a user already exists with that loginID
            if (loginID != null && userExists(ctxt, loginID, 0)) {
                ctxt.setAttribute("errorMessage", "User with login id " + loginID
                        + " already exists. Please enter another login id.");

                throw new UBaseException("Login id exists", null);
            }

            String sql = "insert into u_user (user_full_name, user_id, user_phone, user_email, "
                    + "user_valid, user_mod_user_rid, user_mod_datetime) values (";

            sql = sql + "'" + userName + "', "
                    + "'" + loginID.trim() + "', "
                    + "'" + phoneNumber + "', "
                    + "'" + email + "', "
                    + ", 1, "
                    + ctxt.getUserRID() + ", " + dateStr + ")";

            int userRID = qe.executeInsert(sql);

            if (loginID == null || "".equals(loginID.trim())) {
                // @@ For now we will create a login id which is the same as the userRID
                sql = "update u_user set user_id = '" + userRID + "' where user_rid = " + userRID;

                qe.executeUpdate(sql);
            }

            int[] roleRIDs = {defaultRoleRID};

            setUserRoles(ctxt, qe, userRID, roleRIDs);

            return userRID;
        } catch (Exception e) {
            throw new UDBAccessException(e.getMessage(), e);
        }
    }

    public static int quickUserAdd(URequestContext ctxt, UQueryEngine qe)
            throws UDBAccessException, UBaseException {

        return addBaseUser(ctxt, qe, ctxt.getParameter("userName"), ctxt.getParameter("phoneNumber"), "", "", 0);
    }

    public static int quickUserAdd(URequestContext ctxt, UQueryEngine qe, String userName)
            throws UDBAccessException, UBaseException {

        return addBaseUser(ctxt, qe, userName, ctxt.getParameter("phoneNumber"), "", "", 0);
    }

    private static int _saveUserDetails(URequestContext ctxt, UQueryEngine qe, int userRID)
            throws UDBAccessException, UBaseException {

        String userLoginId = ctxt.getParameter("userLoginId");

        // If loginID is specified, check if a user already exists with that loginID
        if (userLoginId != null && userExists(ctxt, userLoginId, 0)) {
            ctxt.setAttribute("errorMessage", "UserMaster~User with login id " + userLoginId
                    + " already exists. Please enter another login id.");

            throw new UBaseException("Login id exists", null);
        }

        String dateStr = null;

        dateStr = "'" + UDate.nowDBString() + "'";

        String sql = "insert into u_user (";

        if (userRID > 0) {
            sql = sql + "user_rid,";
        }

        sql = sql + "user_id, user_password, user_full_name, "
                + " user_valid, user_mod_user_rid, user_mod_datetime, user_row_invalidated, user_email) values (";

        String userEmailId = ctxt.getParameter("userEmailId");

        if (userEmailId == null) {
            userEmailId = "";
        }

        if (userRID > 0) {
            sql = sql + String.valueOf(userRID) + ",";
        }

        sql = sql + "'" + userLoginId + "', '', '" + ctxt.getParameter("userName") + "', ";


        sql = sql + ("on".equals(ctxt.getParameter("isActive")) ? 1 : 0) + ", "
                + ctxt.getUserRID() + ", " + dateStr + ", 0" + ", '" + userEmailId + "')";

        return (qe.executeInsert(sql));
    }

    private static void _updateUserDetails(URequestContext ctxt, UQueryEngine qe, int userRID)
            throws UDBAccessException, UBaseException {

        String userLoginId = ctxt.getParameter("userLoginId");

        // If loginID is specified, check if a user already exists with that loginID
        if (userLoginId != null && userExists(ctxt, userLoginId, userRID)) {
            ctxt.setAttribute("errorMessage", "UserMaster~User with login id " + userLoginId
                    + " already exists. Please enter another login id.");

            throw new UBaseException("Login id exists", null);
        }

        String dateStr = null;

        dateStr = "'" + UDate.nowDBString() + "'";

        String userEmailId = ctxt.getParameter("userEmailId");
        if (userEmailId == null) {
            userEmailId = "";
        }

        String sql = "update u_user set user_id = '" + userLoginId + "', "
                + "user_full_name = '" + ctxt.getParameter("userName") + "', "
                + (ctxt.getParameter("emptyPassword") == null ? "" : "user_password = '', ")
                + "user_valid = " + ("on".equals(ctxt.getParameter("isActive")) ? 1 : 0) + ", "
                + "user_mod_datetime = " + dateStr + ", "
                + "user_mod_user_rid = " + ctxt.getUserRID() + ", user_email = '" + userEmailId + "'"
                + " where user_rid = " + userRID;

        qe.executeUpdate(sql);
    }

    public static int insertUser(URequestContext ctxt)
            throws UDBAccessException, UBaseException {

        String userLoginId = ctxt.getParameter("userLoginId");

        if (userExists(ctxt, userLoginId, 0)) {

            ctxt.setAttribute("errorMessage", "UserMaster~User with login id " + userLoginId
                    + " already exists. Please enter another login id.");

            throw new UBaseException("Login id exists", null);
        }

        UQueryEngine qe = ctxt.getQueryEngine();

        int rid = 0;
        UQueryEngine localQE = null;

        try {
            localQE = ctxt.getQueryEngine();

            localQE.beginTransaction();

            rid = _saveUserDetails(ctxt, localQE, 0);

            reinsertUserRoles(ctxt, localQE, rid);
            
            localQE.commitTransaction();

        } catch (Exception e) {
            throw new UDBAccessException("Error in saving user details. " + e.getMessage(), e);
        }


        return rid;

    }

    public static int saveUser(URequestContext ctxt)
            throws UDBAccessException, UBaseException {

        int userRID = ctxt.getIntParameter("userRID");

        if (userRID > 0) {
            updateUser(ctxt, userRID);
        } else {
            userRID = insertUser(ctxt);
        }

        return userRID;
    }

    public static void removeIfTemporaryUser(URequestContext ctxt, UQueryEngine qe, int userRID)
            throws UDBAccessException {

        String sql = "delete from u_user where user_rid = " + userRID
                + " and user_id is null";

        qe.executeUpdate(sql);
    }

    public static String getPrimaryFeature(URequestContext ctxt)
            throws UDBAccessException {

        try {
            UQueryEngine qe = ctxt.getQueryEngine();
            String primaryFeatCmd = null;

            ResultSet rs = qe.executeQuery("select feature_command, feature_rid from u_user,u_feature where user_rid = " + ctxt.getUserRID()
                    + " and user_feature_rid = feature_rid");

            if (rs.first()) {

                primaryFeatCmd = rs.getString("feature_command") + "&featureRID=" + rs.getString("feature_rid");

                primaryFeatCmd = ("null".equalsIgnoreCase(primaryFeatCmd)) ? null : primaryFeatCmd;
            }

            return primaryFeatCmd;
        } catch (Exception e) {
            throw new UDBAccessException("Error in retrieving primary feature. " + e.getMessage(), e);
        }

    }

    public static void setPrimaryFeature(URequestContext ctxt, int featureRID)
            throws UDBAccessException {

        try {
            UQueryEngine qe = ctxt.getQueryEngine();

            qe.executeUpdate("update u_user set user_feature_rid = " + featureRID
                    + " where user_rid = " + ctxt.getUserRID());

        } catch (Exception e) {
            throw new UDBAccessException("Error in setting primary feature. " + e.getMessage(), e);
        }

    }

    static boolean hasRoleAssigned(URequestContext ctxt, String roleName) throws UAuthorizationException {
        try {
            return hasRoleAssigned(ctxt.getQueryEngine(), roleName, ctxt.getUserRID());
        } catch (Exception ex) {
            throw new UAuthorizationException(ex.getMessage(), ex);
        }
    }

    static boolean hasRoleAssigned(UQueryEngine qe, String roleName, int userRID) throws UAuthorizationException {
        try {
            String sql = "select * from u_user "
                    + "join u_user_role_map on ur_user_rid = user_rid "
                    + "join u_role on role_rid = ur_role_rid "
                    + "where role_name =  '" + roleName + "' and role_valid = 1 "
                    + "and user_rid = " + userRID;

            return qe.executeQuery(sql).first();
        } catch (Exception ex) {
            throw new UAuthorizationException("Failed to get accessible roles!", ex);
        }
    }
}
