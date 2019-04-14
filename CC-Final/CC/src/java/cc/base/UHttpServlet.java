package cc.base;

import cc.util.UDate;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;


public class UHttpServlet extends HttpServlet {
    
    static Logger logger = Logger.getLogger(UHttpServlet.class);
    
    protected ServletConfig servletConfig = null;
    
    private String userId = null;
    private String commandStr = "";
    
    private void sendLoginPage(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        RequestDispatcher rd = request.getRequestDispatcher("Login");
        
        rd.forward(request, response);
        
    }
    
    private void doRelogin(HttpServletRequest request, HttpServletResponse response, String flag)
    throws ServletException, IOException  {
        
        if(flag != null) {
            request.setAttribute(flag, "Yes");
            
            logger.debug("Session " + flag + "!");
        }
        
        sendLoginPage(request, response);
    }
    
    // Returns true if session is valid. If not, it dispatches a Login page
    // and returns false.
    
    public boolean sessionCheck(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException, UBaseException {
        
        String isSourceXML =  request.getParameter("isSourceXML");
        
        boolean autoLogin = request.getParameter("autoLogin") != null;
        
        int productID = request.getParameter("productID") != null ?
            Integer.parseInt(request.getParameter("productID")) : 0;
        
        int userRID = 0;
        
        HttpSession session = request.getSession(false);
        
        if(session != null) {
            Integer i = (Integer) session.getAttribute("userRID");
            userRID = (i == null) ? 0 : i.intValue();
        }
        
        if(session == null || userRID == 0) {
            
            logger.debug("Invalid or non-existent session.");
            
            if(isSourceXML != null && "true".equals(isSourceXML)) {
                PrintWriter out = response.getWriter();
                // The string below is recognized by XML Helper
                // DO NOT CHANGE unless you are also changing this in the
                // XMLHelper
                out.print("Session Expired");
                out.flush();
                return false;
            } else if(autoLogin) {
                
                logger.debug("Auto login requested...");
                
                // Check cookie for user name and password
                // If not found, ask for user name and password. Setup
                // login page so that the page calls the current URL
                // and not the Welcome page
                
                String userName = UServletHelper.getCookieValue(request, "userName");
                String password = UServletHelper.getCookieValue(request, "password");
                if(userName == null || password == null){
                    
                    userName = request.getParameter("userName");
                    password = request.getParameter("password");                    
                }
                
               
                if(userName != null && password != null) {
                    
                    logger.debug("User name and password cookie found. User= " + userName);
                    
                    // We will try to login using the user name and password obtained from the Cookies
                    try {
                        UUser user = ULogin.loginUser(request, productID,userName, password);
                        
                        if(user != null) {
                            
                            logger.debug("Auto login succeeded...");
                            
                            return true;
                        } else {
                            
                            logger.debug("Auto login failed...");
                            
                            // Send login page with current request so that after login that
                            // functionality can be called directly
                            
                            String url = request.getServletPath() + "?prg=1&" + request.getQueryString();
                            
                            logger.debug("Forward command = '" + url + "'");
                            
                            request.setAttribute("forwardCommand", url);
                            
                            doRelogin(request, response, null);
                            
                            return false;
                        }
                    } catch (Exception e) {
                        
                        request.setAttribute("error", "true");
                    }
                    
                } else {
                    
                    logger.debug("User name and password cookie not found. Requesting Login...");
                    
                    // Send login page with current request so that after login that
                    // functionality can be called directly
                    
                    String url = request.getServletPath() + "?prg=1&" + request.getQueryString();
                    
                    logger.debug("Forward command = '" + url + "'");
                    
                    request.setAttribute("forwardCommand", url);
                    
                    doRelogin(request, response, null);
                    
                    return false;
                }
                
            } else {
                
                if(session == null)
                    doRelogin(request, response, "expired");
                else
                    doRelogin(request, response, "illegal");
                
                return false;
            }
        }
        
        return true;
    }
    
    public void init(ServletConfig config) throws ServletException {
        
        super.init(config);
        
        servletConfig = config;
    }
    
    public void handleGet(URequestContext ctxt)
    throws UServletException {
    }
    
    private void logInfo(HttpServletRequest request, String prefix) {
        
        HttpSession session = request.getSession(false);
        
        if(session != null) {
            userId = (String) session.getAttribute("userID");
        }
        
        Date date = new Date();
        
        logger.info(prefix + " " + request.getServletPath() + commandStr + ";User=" + userId + " " + date.toString());
        
        date = null;
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String command = request.getParameter("command");
        
        commandStr = "";
        
        if(command != null)
            commandStr = "?command=" + command;
        
        logInfo(request, "START GET ");
        
	URequestContext requestContext = null ;
        
        try {
	    if(!"no".equals(request.getParameter("sessionCheck")) && sessionCheck(request, response) == false)
		return;
        
	    String dbResourceName = null ;
        
	    dbResourceName = servletConfig.getInitParameter("dbResourceName");
        
	    if(dbResourceName != null)
		requestContext = new URequestContext(request, response, dbResourceName);
	    else
		requestContext = new URequestContext(request, response);

	    requestContext.setAttribute("serverCurrentDate", UDate.nowDisplayString());
	    requestContext.setAttribute("serverCurrentTime", UDate.formatTimeFor24HourDisplay(UDate.currentTime()));
	    requestContext.setAttribute("ctxt", requestContext);
            handleGet(requestContext);
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new ServletException(e);
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            requestContext.close();
            logInfo(request, "END   GET ");
        }
        
    }
    
    public void handlePost(URequestContext ctxt)
    throws UServletException {
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String command = request.getParameter("command");
        
        commandStr = "";
        
        if(command != null)
            commandStr = "?command=" + command;
        
        logInfo(request, "START POST");
        
	URequestContext requestContext = null ;
        
        try {
            
	    if(!"no".equals(request.getParameter("sessionCheck")) && sessionCheck(request, response) == false)
		return;
        
	    //       URequestContext requestContext = new URequestContext(request, response);
	    String dbResourceName = null ;
        
	    dbResourceName = servletConfig.getInitParameter("dbResourceName");
        
	    if(dbResourceName != null)
		requestContext = new URequestContext(request, response, dbResourceName);
	    else
		requestContext = new URequestContext(request, response);
        
        
            boolean redirectToGet = request.getParameter("prg") != null;
            
	    requestContext.setAttribute("serverCurrentDate", UDate.nowDisplayString());
	    requestContext.setAttribute("serverCurrentTime", UDate.formatTimeFor24HourDisplay(UDate.currentTime()));
            requestContext.setAttribute("ctxt", requestContext);
            
           
            if(redirectToGet) {
                logger.debug("Redirecting to GET...");
                handleGet(requestContext);
            } else
                handlePost(requestContext);
            
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new ServletException(e);
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new ServletException(e);
        } finally {
            requestContext.close();
            
            logInfo(request, "END   POST");
        }
    }
    
}
