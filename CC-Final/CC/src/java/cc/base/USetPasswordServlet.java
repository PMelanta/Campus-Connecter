package cc.base;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.sql.*;

import org.apache.log4j.Logger;

public class USetPasswordServlet extends UHttpServlet {
    
    static Logger logger = Logger.getLogger(USetPasswordServlet.class);
    
    ServletConfig servletConfig;
    
    public void init(ServletConfig config) throws ServletException {
        
        servletConfig = config;
    }
    
    private void sendLoginPage(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        // Get the login JSP file from the init params
        String login_jsp = servletConfig.getInitParameter("loginJSP");
        
        if(login_jsp == null)
            throw new ServletException("Login JSP not specified in init params");
        else {
            RequestDispatcher rd = request.getRequestDispatcher(login_jsp);
            
            rd.forward(request, response);
        }
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        try {
            
            // @@ HACK! REDESIGN the reset password functionality
            
            if(sessionCheck(request, response) == false)
                return;
            
            HttpSession session = request.getSession();
            
            int userRID = ((Integer) session.getAttribute("userRID")).intValue();
            
            String resetPassword = request.getParameter("resetPassword");
            
            if(resetPassword != null) {
                UUserManager.resetPassword(userRID);
                String resetPasswordJSP = servletConfig.getInitParameter("resetPasswordJSP");
                
                request.setAttribute("message", "Your password has been reset successfully");
                
                RequestDispatcher rd = request.getRequestDispatcher(resetPasswordJSP);
                
                rd.forward(request, response);
            } else
                sendLoginPage(request, response);
            
        } catch (Exception e) {
            logger.error(e.getMessage());
            
            request.setAttribute("errorMessage", "Resetting password failed! " + e.getMessage());
            
            sendLoginPage(request, response);
        }
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        try {
            if(sessionCheck(request, response) == false)
                return;
            
            HttpSession session = request.getSession();
            
            int userRID = ((Integer) session.getAttribute("userRID")).intValue();
            
            String password = request.getParameter("password");
            
            if(password == null) {
                UUserManager.resetPassword(userRID);
                return;
            } else
                UUserManager.setPassword(userRID, password);
            
            // Set password cookie
            // Keep it valid for 30 days (specified in secs)
            int expiry = 30 * 24 * 3600;
            
            UServletHelper.setCookieValue(response, "password", password, expiry);
            
            request.setAttribute("userName", session.getAttribute("userName"));
            
            String forwardCommand = request.getParameter("forwardCommand");
            
            String nextURL = null;
            
            UQueryEngine qe = null;


                if(forwardCommand != null && !"".equals(forwardCommand))
                    nextURL = forwardCommand;
                else
                    nextURL = "UDesktop";
            
            RequestDispatcher rd = request.getRequestDispatcher(nextURL);
            
            rd.forward(request, response);
            
        } catch (Exception e) {
            logger.error(e.getMessage());
            
            request.setAttribute("errorMessage", "Setting password failed! " + e.getMessage());
            
            sendLoginPage(request, response);
            
        }
    }
}
