/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.user;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cc.base.UConfig;
import cc.base.URequestContext;
import cc.base.UServletHelper;
import cc.base.UWriterHelper;
import cc.util.UDate;

/**
 *
 * @author suhas
 */
public class UserRegistrationServlet extends HttpServlet {

    protected ServletConfig servletConfig = null;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            URequestContext ctxt = new URequestContext(request, response);
            String contentType = ctxt.getHttpRequest().getHeader("Content-Type");

            if (null == contentType || contentType.split(";")[0].equals("application/x-www-form-urlencoded")) {
                String command = ctxt.getParameter("command");
                if ("userRegistration".equals(command)) {
                    UServletHelper.sendJSP(ctxt, servletConfig, "UserRegistrationJSP");
                } else if ("checkUserIdAvailability".equals(command)) {
                    String userId = ctxt.getParameter("userId");
                    if (UserRegistrationManager.checkAvailability(ctxt, userId)) {
                        UWriterHelper.writeString(ctxt, "1");
                    } else {
                        UWriterHelper.writeString(ctxt, "0");
                    }
                }
            } else {
                _handleMultiTypeData(ctxt);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        servletConfig = config;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void _handleMultiTypeData(URequestContext ctxt)
            throws Exception {
        ctxt = FileManager.uploadAvatar(ctxt);
        String command = ctxt.getParameter("command");
        if ("saveUserDetails".equals(command)) {
            String userId, userName, company, email, website, gender, dob, phone, mobile, avatar=null, about;
            userId = ctxt.getParameter("userId");
            userName = ctxt.getParameter("userName");
            company = ctxt.getParameter("comapany");
            email = ctxt.getParameter("email");
            website = ctxt.getParameter("url");
            gender = ctxt.getParameter("mgender") == null ? (ctxt.getParameter("fgender") == null ? "" : "F") : "M";
            dob = ctxt.getParameter("dob");
            if (dob.length() > 0) {
                dob = UDate.displayToDB(dob);
            }else{
                dob = null;
            }

            phone = ctxt.getParameter("phone");
            mobile = ctxt.getParameter("mobile");
            if (ctxt.getAttribute("uploadedFilesPath") != null) {
                String contextPath = UConfig.getParameterValue(0, "PROFILE_PIC_CONTEXT_PATH", "images/users");
                avatar = ctxt.getContextPath()+"/"+contextPath+"/"+ctxt.getAttribute("uploadedFilesName").toString().split("`")[0];
            }
            
            //avatar = ctxt.getParameter("userAvatar");
            about = ctxt.getParameter("desc");

            UserRegistrationManager.saveUserDetails(ctxt, userId, userName, company, email, website,
                    gender, dob, phone, mobile, avatar, about);
            ctxt.setAttribute("closePopUp", "1");
            UServletHelper.sendJSP(ctxt, servletConfig, "dataEntryResponse");

        }
    }
}
