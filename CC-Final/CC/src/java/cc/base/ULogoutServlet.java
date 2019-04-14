package cc.base;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

public class ULogoutServlet extends UHttpServlet {
    
    static Logger logger = Logger.getLogger(ULogoutServlet.class);
    
    public void handleGet(URequestContext ctxt)
    throws UServletException {
        
        HttpSession session = ctxt.getSession();
        String startupPage;
        try {
            startupPage = UConfig.getParameterValue(ctxt, "STARTUP_LOGIN_PAGE", "");
        } catch (UBaseException e) {
            throw new UServletException(e.getMessage(), null);
        }
        
        String silentLogin = (String) session.getAttribute("silentLogin") ;
        
        String suicideJSPpath = servletConfig.getInitParameter("suicideJSP") ;
        
        if(silentLogin == null)
            silentLogin = "false" ;
        
        if(session != null){
            USessionManager.deleteSessionDetails(session) ;
            
            try {
                UAuditTrail.addEntry(ctxt, UAuditTrail.AUDIT_LOGOUT, "");
            } catch (UDBAccessException e) {
                throw new UServletException("Failed to record audit trail. " + e.getMessage(), null);
            }
            
            try {
                session.invalidate();
            } catch (Exception ex){
                logger.error("Failed to invalidated session, " + ex.getMessage());
            }
        }
        
        HttpServletRequest request = ctxt.getHttpRequest();
        HttpServletResponse response = ctxt.getHttpResponse();
        RequestDispatcher rd = null ;
        if(!"".equals(startupPage)) {
            try {
                response.sendRedirect(response.encodeRedirectURL(startupPage));
                return;
            } catch (Exception e) {
                throw new UServletException(e.getMessage(), null);
            }
        }
        
        if(silentLogin != null && silentLogin.equals("true"))
            rd = request.getRequestDispatcher(suicideJSPpath);
        else
            rd = request.getRequestDispatcher("Login");
        
        try {
            rd.forward(request, ctxt.getHttpResponse());
        } catch (Exception e) {
            
            throw new UServletException(e.getMessage(), null);
        }
    }
    
    public void handlePost(URequestContext ctxt)
    throws UServletException {
        
        handleGet(ctxt);
    }
}
