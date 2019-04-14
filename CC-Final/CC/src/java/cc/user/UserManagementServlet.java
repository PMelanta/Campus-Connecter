/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.user;

import cc.base.UMailer;
import cc.base.UQueryEngine;
import cc.base.URequestContext;
import cc.base.UServletException;
import cc.base.UUserManager;
import cc.base.UWriterHelper;

import cc.util.LocationUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *
 * @author 
 */
public class UserManagementServlet extends HttpServlet {

    protected ServletConfig servletConfig = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        URequestContext ctxt = null;
        try {
            ctxt = new URequestContext(request, response);
            String command = ctxt.getParameter("command");

             if ("newUser".equals(command)) {
                _newUser(ctxt);
            } else if ("login".equals(command)) {
                _login(ctxt);
            } else if ("oaLogin".equals(command)) {
                _oaLogin(ctxt);
            } else if ("setAsOAHome".equals(command)) {
                _setAsOAHome(ctxt);
            } else if ("setAsOACurrent".equals(command)) {
                _setAsOACurrent(ctxt);
            } else if ("getStatus".equals(command)) {
                _getStatus(ctxt);
            } else if ("forgotPassword".equals(command)) {
                _forgotPassword(ctxt);
            } else if ("getOARecords".equals(command)) {
                getOARecords(ctxt);
            } else if ("updateStatus".equals(command)) {
                updateStatus(ctxt);
            } else if ("checkUserIdAvailability".equals(command)) {
                String userId = ctxt.getParameter("userId");
                if (UserRegistrationManager.checkAvailability(ctxt, userId)) {
                    UWriterHelper.writeString(ctxt, "1");
                } else {
                    UWriterHelper.writeString(ctxt, "0");
                }
            } else if ("getLocations".equals(command)) {
            _getLocations(ctxt);
            }
             
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            if (ctxt != null) {
                ctxt.close();
            }
        }

    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        servletConfig = config;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String contentType = request.getContentType();
        if(contentType.contains("multipart")){
            URequestContext ctxt = new URequestContext(request, response);
            try{
            //_handleUserRegistration(ctxt);
            }catch(Exception ex){
                
            }finally{
               ctxt.close();
            }
        }else{
        processRequest(request, response);
        }
    }
    
    private void _getLocations(URequestContext ctxt) throws ServletException {
        String responseStr = "OK";
        try {
            UQueryEngine qe = ctxt.getQueryEngine();
            ResultSet rs = UserManagement.getLocations(qe);
            while (rs.next()) {
                responseStr += "#" + rs.getInt("loc_rid") + "~" + rs.getString("loc_name") 
                        + "~" + rs.getString("loc_latitude")
                        + "~" + rs.getString("loc_longitude");
            }
        } catch (Exception ex) {
            responseStr = "NOT_OK#" + ex.getMessage();
            throw new ServletException(ex.getMessage());
        } finally {
            try {
                UWriterHelper.writeString(ctxt, responseStr);
            } catch (UServletException ex) {
                Logger.getLogger(UserManagementServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    

    private void _newUser(URequestContext ctxt)
            throws Exception {

        String response = "";
        
        try {
            String usn, bkpNo, email, mobile, password;
            email = ctxt.getParameter("email");
            usn = ctxt.getParameter("usn");
            bkpNo = ctxt.getParameter("bkpNo");
            mobile = ctxt.getParameter("mobile");
            password = ctxt.getParameter("password");
            

            int userRid = UserManagement.updateUserDetails(ctxt, email, usn, password, mobile, bkpNo);
            response = "OK:" + userRid;
        } catch (Exception ex) {
            response = "NOT_OK:" + ex.getMessage();
            throw ex;
        } finally {
                UWriterHelper.writeString(ctxt, response);
        }
    }

    private void _login(URequestContext ctxt) throws Exception {
        String response = "";
        try {
            String loginId = ctxt.getParameter("loginId");
            String password = ctxt.getParameter("password");

            ResultSet rsUser = UserManagement.getUser(ctxt, loginId, password);
            if (rsUser.first()) {
                response = "OK#" + rsUser.getInt("stud_rid") 
                        + "~" + rsUser.getString("stud_first_name")+" "+rsUser.getString("stud_last_name")
                        + "~" + rsUser.getString("stud_email") + "~" + rsUser.getString("stud_mobile_no")
                        + "~" + rsUser.getString("stud_bkp_no")
                        +"~"+rsUser.getInt("can_create_events");
            } else {
                response = "NOT_OK#Invalid User";
            }

        } catch (Exception ex) {
            response = "NOT_OK#" + ex.getMessage();
            throw ex;
        } finally {
            UWriterHelper.writeString(ctxt, response);
        }

    }
    private void _oaLogin(URequestContext ctxt) throws Exception {
        String response = "";
        try {
            String mobileNo = ctxt.getParameter("mobileNo");
            if(mobileNo != null && mobileNo.length() > 10)
                mobileNo = mobileNo.substring(mobileNo.length()-10, mobileNo.length());

            ResultSet rsOAUser = UserManagement.getOAUser(ctxt, mobileNo);
            if (rsOAUser.first()) {
                response = "OK#" + rsOAUser.getInt("oa_rid")
                        +"~"+rsOAUser.getString("oa_name")
                        +"~"+rsOAUser.getString("ct_mobile_no")
                        +"~"+rsOAUser.getString("ct_bkp_no");
            } else {
                response = "NOT_OK#Invalid User";
            }

        } catch (Exception ex) {
            response = "NOT_OK#" + ex.getMessage();
            throw ex;
        } finally {
            UWriterHelper.writeString(ctxt, response);
        }

    }
    private void _setAsOAHome(URequestContext ctxt) throws Exception {
        String response = "";
        try {
            double lat = ctxt.getFloatParameter("lat");
            double lon = ctxt.getFloatParameter("lon");
            int oaRid = ctxt.getIntParameter("oaRid");
            
            UserManagement.updateOAUserHomeLoc(ctxt, oaRid, lat, lon);
            response ="OK";

        } catch (Exception ex) {
            response = "NOT_OK#" + ex.getMessage();
            throw ex;
        } finally {
            UWriterHelper.writeString(ctxt, response);
        }

    }
    private void _setAsOACurrent(URequestContext ctxt) throws Exception {
        String response = "";
        try {
            double lat = ctxt.getFloatParameter("lat");
            double lon = ctxt.getFloatParameter("lon");
            int oaRid = ctxt.getIntParameter("oaRid");
            
            UserManagement.updateOAUserCurLoc(ctxt, oaRid, lat, lon);
            response ="OK";

        } catch (Exception ex) {
            response = "NOT_OK#" + ex.getMessage();
            throw ex;
        } finally {
            UWriterHelper.writeString(ctxt, response);
        }

    }
    private void _getStatus(URequestContext ctxt) throws Exception {
        String response = "";
        try {
            int oaRid = ctxt.getIntParameter("oaRid");
          
            ResultSet rsStatus = UserManagement.getOAStatusList(ctxt, oaRid);
            if (rsStatus.first()) {
                response = "OK";
                while(rsStatus.next()){
                    response += "#"+rsStatus.getInt("status_rid")+"~"
                            +rsStatus.getString("status_desc")+"~"+rsStatus.getInt("oa_cur_status_rid");
                }
            } else {
                response = "NOT_OK#Invalid User";
            }

        } catch (Exception ex) {
            response = "NOT_OK#" + ex.getMessage();
            throw ex;
        } finally {
            UWriterHelper.writeString(ctxt, response);
        }

    }
    private void updateStatus(URequestContext ctxt) throws Exception {
        String response = "";
        try {
            int userRid = ctxt.getIntParameter("userRid");
            int statusRid = ctxt.getIntParameter("statusRid");
          
            UserManagement.updateOAStatus(ctxt, userRid,statusRid);
            response = "OK";
        } catch (Exception ex) {
            response = "NOT_OK#" + ex.getMessage();
            throw ex;
        } finally {
            UWriterHelper.writeString(ctxt, response);
        }

    }

    private void _forgotPassword(URequestContext ctxt) throws Exception {
        String response = "";
        try {
            String emailId = ctxt.getParameter("email");

            String password = UUserManager.getNewPasswordPlain(ctxt, emailId);
            if (password == null) {
                throw new Exception("Invalid UserId");
            }

            String subject = "Forgot Password ";
            String messageText = "Your details \n";
            messageText += "Login Id :" + emailId + "\n";
            messageText += "New Password :  " + password;

            if (null != emailId && !"".equals(emailId.trim())) {
                UMailer.sendMail(ctxt, "", emailId, subject, messageText);
                UMailer.flushMailQueue(ctxt.getQueryEngine());
            }

            response = "OK#Mail sent";

        } catch (Exception ex) {
            response = "NOT_OK#" + ex.getMessage();
            throw ex;
        } finally {
            UWriterHelper.writeString(ctxt, response);
        }

    }
    
    private void getOARecords(URequestContext ctxt)
            throws ServletException {
        int ctRid = ctxt.getIntParameter("ctRid");
        String resultStr = "";
        StringBuilder sb = new StringBuilder();
        try {
                ResultSet rs = UserManagement.getOARecords(ctxt, ctRid);
                boolean firstItr = true;
                //OK#name~email~mobileNo~fileName~serverUserRid~long~lat#.....
                while (rs.next()) {
                    if (!firstItr) {
                        sb.append("#");
                    }
                    if (firstItr) {
                        firstItr = false;
                    }
                    
                    double distFromHome = LocationUtil.getDistance(
                            rs.getDouble("oa_home_latitude"), rs.getDouble("oa_home_longitude"), 
                            rs.getDouble("oa_cur_latitude"), rs.getDouble("oa_cur_longitude"), 
                            LocationUtil.UNIT_KM);
                    //String name, String address, String mobile, String dob, int age, float distance, String status
                    
                    sb.append(rs.getString("oa_rid"))
                            .append("~")
                            .append(rs.getString("oa_name"))
                            .append("~")
                            .append(rs.getString("oa_address"))
                            .append("~")
                            .append(rs.getString("oa_mobile_no"))
                            .append("~")
                            .append(rs.getString("oa_dob"))
                            .append("~")
                            .append(rs.getInt("age"))
                            .append("~")
                            .append(distFromHome)
                            .append("~")
                            .append(rs.getString("status_desc") == null?"":rs.getString("status_desc"))
                            .append("~")
                            .append(rs.getString("oa_pic_name"))
                            .append("~")
                            .append(rs.getDouble("oa_cur_latitude"))
                            .append("~")
                            .append(rs.getDouble("oa_cur_longitude"));
                }
                
            resultStr = "OK#" + sb.toString();
        } catch (Exception ex) {
            resultStr = "NOT_OK";
            throw new ServletException(ex);
        } finally {
            try {
                UWriterHelper.writeString(ctxt, resultStr);
            } catch (UServletException ex) {
                Logger.getLogger(UserManagementServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }

}
