 package cc.base;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;

import java.sql.*;

import java.util.Vector;
import java.text.*;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

public final class UServletHelper {

    static Logger logger = Logger.getLogger(UServletHelper.class);

    private UServletHelper() {
    }

    public static void receiveFiles(URequestContext ctxt, String dir)
	throws UServletException {

	try {

	    // Create target directory if required
	    File dirFile = new File(dir);

	    if(!dirFile.exists()) {
		
		// Target directory does not exist. Create it.

		logger.debug("Creating directory '" + dir + "'...");

		if(!dirFile.mkdirs()) {
		    throw new UServletException("Failed to create '" + dir + "'");
		}

		logger.debug("Done");
	    }
	    
	    DiskFileUpload fu = new DiskFileUpload();
	    // If file size exceeds, a FileUploadException will be thrown
	    fu.setSizeMax(UConfig.getParameterValue(ctxt, "UPLOAD_FILE_SIZE_LIMIT", 6000000));
	    HttpServletRequest request = ctxt.getHttpRequest();

	    List fileItems = fu.parseRequest(request);
	    Iterator itr = fileItems.iterator();

	    logger.debug("Ready to receive files...");

	    while(itr.hasNext()) {
		FileItem fi = (FileItem)itr.next();

		logger.debug("Found an element in request body...: ");

		//Check if not form field so as to only handle the file inputs
		//else condition handles the submit button input
		if(!fi.isFormField()) {

		    logger.info(">> Receiving filename: " + fi.getName() + ", size = " + fi.getSize());

		    File fNew= new File(dir, fi.getName());

		    logger.debug("Saving in file : " + fNew.getAbsolutePath());

		    fi.write(fNew);
		}
	    }

	    logger.debug("Done receiving files.");
	} catch (Exception e) {
	    // @@ Log error with sufficient details
	    throw new UServletException(e.getMessage(), e);
	}

    }

    public static void forwardRequest(URequestContext ctxt, String url)
	throws UServletException {

	HttpServletRequest request = ctxt.getHttpRequest();
	HttpServletResponse response = ctxt.getHttpResponse();

	try {

	    RequestDispatcher rd = request.getRequestDispatcher(url);

	    rd.forward(request, response);

	} catch (Exception e) {
	    throw new UServletException(e.getMessage(), e);
	}
    }

    public static void includeRequest(URequestContext ctxt, String url)
	throws UServletException {

	HttpServletRequest request = ctxt.getHttpRequest();
	HttpServletResponse response = ctxt.getHttpResponse();

	try {

	    RequestDispatcher rd = request.getRequestDispatcher(url);

	    rd.include(request, response);

	} catch (Exception e) {
	    throw new UServletException(e.getMessage(), e);
	}
    }

    public static void sendJSP(URequestContext ctxt, String jspFilename)
	throws UServletException {

	HttpServletRequest request = ctxt.getHttpRequest();
	HttpServletResponse response = ctxt.getHttpResponse();
        
        //To make sure that pages are not cached in the browser. --Gopi
        if(!"1".equals(ctxt.getParameter("doNotExpirePages")))
            response.setHeader("Cache-Control", "max-age=2, must-revalidate") ;
        
	try {
	    RequestDispatcher rd = request.getRequestDispatcher(jspFilename);

	    rd.forward(request, response);
    	} catch (Exception e) {
	    throw new UServletException(e.getMessage(), e);
	}
    }
    
    public static void sendJSP(URequestContext ctxt, ServletConfig servletConfig, String jspName)
	throws UServletException {
        
	String jspFilename = servletConfig.getInitParameter(jspName);

	try {
	    if(jspFilename == null)
		throw new UServletException(jspName + " not defined in init params!", null);
	    else
		sendJSP(ctxt, jspFilename);
	} catch (Exception e) {
	    throw new UServletException(e.getMessage(), e);
	}
    }
    
    public static void includeJSP(URequestContext ctxt, String jspFilename)
    throws UServletException {

        HttpServletRequest request = ctxt.getHttpRequest();
        HttpServletResponse response = ctxt.getHttpResponse();
        
        //To make sure that pages are not cached in the browser. --Gopi
        if(!"1".equals(ctxt.getParameter("doNotExpirePages")))
            response.setHeader("Cache-Control", "max-age=2, must-revalidate") ;
        
        try {
	    RequestDispatcher rd = request.getRequestDispatcher(jspFilename);
	    rd.include(request, response);
        } catch (Exception e) {
            throw new UServletException(e.getMessage(), e);
        }
    }

    public static void includeJSP(URequestContext ctxt, ServletConfig servletConfig, String jspName)
	throws UServletException {
        
        String jspFilename = servletConfig.getInitParameter(jspName);
        
        try {
            if(jspFilename == null)
                throw new UServletException(jspName + " not defined in init params!", null);
            else {
                includeJSP(ctxt, jspFilename);
            }
        } catch (Exception e) {
            throw new UServletException(e.getMessage(), e);
        }
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
	
	Cookie[] cookies = request.getCookies();

	if(cookies == null)
	    return null;

	for(int i = 0; i < cookies.length; i++) {
	    
	    if(name.equals(cookies[i].getName()))
		return cookies[i].getValue();
	}

	return null;
    }

    public static void setCookieValue(HttpServletResponse response, String name, String value, int expiry) {
	
	Cookie cookie = new Cookie(name, value);
	
	cookie.setMaxAge(expiry);

	response.addCookie(cookie);
    }
    
    public static void loadJSP(URequestContext ctxt, ServletConfig servletConfig, boolean commitRequest, String jspName)
    throws SQLException, UBaseException {
        if(commitRequest) {
            UServletHelper.sendJSP(ctxt, servletConfig, jspName);
        } else {
            UServletHelper.includeJSP(ctxt, servletConfig, jspName);
        }
    }
    
}
