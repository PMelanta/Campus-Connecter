package cc.base;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

import java.sql.*;

import org.apache.log4j.Logger;

public final class ULogin {

    static Logger logger = Logger.getLogger(ULogin.class);

    public static UUser loginUser(HttpServletRequest request, int productID, String userName, String password)
	throws UAuthenticationException, UBaseException {

	UUser user = null;
                
	java.util.Date date = new java.util.Date();

	logger.info("START LOGIN;User=" + userName +  ";Servlet=" + request.getServletPath() + " " + date.toString());

	date = null;

	user = UUserManager.loginUser(productID, userName, password, request,true);

	if(user == null)
	    return null;
            
	// Login succeeded

	// Create a new Session object
	HttpSession session = request.getSession(true);

	session.setAttribute("loggedIn", "true");
	session.setAttribute("productRID", new Integer(productID));

	// Add user related attributes for future use with transactions
	session.setAttribute("userRID", new Integer(user.getUserRID()));
	session.setAttribute("userName", user.getUserName());

	// @@ Confusing!! userName here is actually user ID (the name that the user uses to login). Fix it. 
	session.setAttribute("userID", userName);

	session.setAttribute("userTitle", user.getUserTitle());



	date = new java.util.Date();

	logger.info("END LOGIN;User=" + userName + "; " + date.toString());

	return user;
    }
}
