package cc.base;

import java.net.URI;
import java.security.GeneralSecurityException;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.URL;

import java.sql.*;

import nl.captcha.Captcha;
import org.apache.log4j.Logger;

public class ULoginServlet extends HttpServlet {

    static Logger logger = Logger.getLogger(ULoginServlet.class);
    ServletConfig servletConfig;
    public static final int AUDIT_EVENT_RELOGIN = 142;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        servletConfig = config;
    }

    protected void sendPage(HttpServletRequest request, HttpServletResponse response, String jspFile)
            throws ServletException, IOException, UDBAccessException {

        String appVersion = "";
        String pidStr = servletConfig.getInitParameter("productID");
        boolean islicensed = true;

        if (pidStr == null) {
            throw new ServletException("Product ID not defined!");
        }

        int productRID = Integer.parseInt(pidStr);

        URequestContext ctxt = new URequestContext(request, response);


        try {

            ResultSet rs = UProduct.loadProduct(ctxt, productRID);

            rs.next();

            ctxt.setAttribute("productTitle", rs.getString("prod_title"));
            ctxt.getSession().setAttribute("productTitle", rs.getString("prod_title")); 
            ctxt.setAttribute("productVersion", rs.getString("prod_version"));

            ctxt.getSession().setAttribute("productVersion", rs.getString("prod_version"));

           
        } catch (Exception e) {
            throw new ServletException("Failed to get product title", null);
        }



        UQueryEngine qe = null;

        try {
            HttpSession session = request.getSession(true);
            qe = new UQueryEngine();
            DatabaseMetaData dbMetaData = qe.getConnection().getMetaData();
            String dbProdName = dbMetaData.getDatabaseProductName();
            session.setAttribute("dbProductName", dbProdName);

        } catch (Exception e) {
            throw new ServletException("Failed to get customer locations. " + e.getMessage(), null);
        } finally {
            if (qe != null) {
                qe.close();
            }
        }



        // Get the login JSP file from the init params
        String login_jsp = servletConfig.getInitParameter(jspFile);

        if (login_jsp == null) {
            throw new ServletException("Login JSP not specified in init params");
        } else {
            RequestDispatcher rd = request.getRequestDispatcher(login_jsp);

            rd.forward(request, response);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String command = request.getParameter("command");
            if (command == null) {
                command = "";
            }

            if (command.equals("mainLogo")) {
                _writeLogo(command, response, request);
            } else if (command.equals("subLogo")) {
                _writeLogo(command, response, request);
            } else if ("loginSub".equals(command)) {
                sendPage(request, response, "login_smallJSP");
            } else if ("loginFailedSub".equals(command)) {
                request.setAttribute("error", "Login Failed!");
                sendPage(request, response, "login_smallJSP");
            } else if ("setPassWordSub".equals(command)) {
                sendPage(request, response, "setPasswordSmallJSP");
            } else {

                sendPage(request, response, "loginJSP");
            }
        } catch (Exception e) {

            String errMsg = "Failed to get login page. " + e.getMessage();

            logger.error(errMsg);

            throw new ServletException(errMsg);
        }
    }

    private void handleFailedLogin(HttpServletRequest request, HttpServletResponse response, String msg)
            throws ServletException, IOException, UBaseException {

        request.setAttribute("error", "Yes");
        request.setAttribute("errorMessage", msg);

        if ("relogin".equals(request.getParameter("command"))) {
            logger.warn("Re login failed!");

            UWriterHelper.writeString(response, "0");

        } else {
            sendPage(request, response, "loginJSP");
        }
        return;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UQueryEngine qe = null;

        try {
            qe = new UQueryEngine();

            String userID = request.getParameter("userName");
            String password = request.getParameter("password");

            int productID = Integer.parseInt(servletConfig.getInitParameter("productID"));

            UUser user = null;

            java.util.Date date = new java.util.Date();

            logger.info("START LOGIN;User=" + userID + ";Servlet=" + request.getServletPath() + " " + date.toString());



            date = null;

            try {
                user = UUserManager.loginUser(productID, userID, password, request, true);

            } catch (UAuthenticationException e) {
                // Unexpected error during login process
                handleFailedLogin(request, response, "System error! " + e.getMessage());
                return;
            }

            if (user == null) {
                
                HttpSession session = request.getSession(true);
                if(session != null && session.getAttribute("wrongPwdCounter") != null){
                    int wrongPwdCounter = Integer.parseInt(session.getAttribute("wrongPwdCounter").toString());
                    wrongPwdCounter = wrongPwdCounter + 1;
                    session.setAttribute("wrongPwdCounter", new Integer(wrongPwdCounter));
                    if(wrongPwdCounter >= 3){
                        request.setAttribute("captureCaptcha", "1");
                    }
                }else{
                    session.setAttribute("wrongPwdCounter", "1");
                }
                // Authentication failed
                handleFailedLogin(request, response, "Login failed! Please try again.");

                return;
            }else if(user != null && ("1").equals(request.getParameter("includesCaptcha"))){
                Captcha captcha = (Captcha) request.getSession().getAttribute(Captcha.NAME);
                String answer = request.getParameter("captchaDetail");
                
                if(captcha != null && answer != null){
                    if (!captcha.isCorrect(answer)) {
                        request.setAttribute("captureCaptcha", "1");
                        request.setAttribute("captcha", "Incorrect Captcha");
                        handleFailedLogin(request, response, "Login failed! Please try again.");
                        return;
                    }
                }
            }

            // Login succeeded
             request.getSession().setAttribute("wrongPwdCounter", "0");

            // Create a new Session object
            HttpSession session = request.getSession(true);


            session.setAttribute("persistSession", UConfig.getParameterValue(0, "PERSIST_SESSION", 0) == 0 ? "false" : "true");

            session.setAttribute("loggedIn", "true");

            session.setAttribute("productRID", new Integer(productID));

            // Add user related attributes for future use with transactions
            session.setAttribute("userRID", new Integer(user.getUserRID()));
            session.setAttribute("userName", user.getUserName());


            session.setAttribute("userID", userID);

            //set clients IP Address & HostName
            session.setAttribute("remoteAddr", request.getRemoteAddr());
            session.setAttribute("remoteHostName", request.getRemoteHost());

            session.setAttribute("userTitle", user.getUserTitle());



            session.setAttribute("remoteHost", request.getRemoteHost());
            USessionManager.persistSession(session);

            date = new java.util.Date();

            logger.info("END LOGIN;User=" + userID + ";" + date.toString());

            date = null;

            String userPwd = user.getUserPassword();


            //@@ For now, we will allow login with empty password for BATCH mode
            if ("relogin".equals(request.getParameter("command"))) {

                DatabaseMetaData dbMetaData = qe.getConnection().getMetaData();
                String dbProdName = dbMetaData.getDatabaseProductName();
                session.setAttribute("dbProductName", dbProdName);
                String generatedSessionID = request.getParameter("generatedSessionID");
                session.setAttribute("generatedSessionID", generatedSessionID);
                //UAuditTrail.addEntry(qe, request, AUDIT_EVENT_RELOGIN,0, user.getUserRID(), 0,0, "", "");
                PrintWriter out = response.getWriter();
                out.print("1");

            } else if ("".equals(userPwd)) {

                logger.debug("Sending Set Password screen");


                request.setAttribute("userName", userID);

                sendPage(request, response, "setPasswordJSP");

            } else if (user.getPasswordStatus() == 1) {
                logger.debug("Sending Set Password screen");

                request.setAttribute("userName", userID);

                sendPage(request, response, "setPasswordJSP");
            } else {
                request.setAttribute("userName", user.getUserName());

                request.setAttribute("productID", (productID));
                String generatedSessionID = USessionManager.getGeneratedSessionID(qe, productID);
                session.setAttribute("generatedSessionID", generatedSessionID);

                String nextURL = "/UDesktop?mode=0";

                RequestDispatcher rd = request.getRequestDispatcher(nextURL);

                rd.forward(request, response);
            }

            // Free up user
            user = null;

        } catch (Exception e) {
            // Nothing to do.
        } finally {
            if (qe != null) {
                qe.close();
                qe = null;
            }
        }
    }

    private void _writeLogo(String logo, HttpServletResponse response, HttpServletRequest request) throws Exception {
        boolean compress = false;
        int BUFFER_SIZE = 2048;
        String filePath = "";
        String defaultFile = "";
        BufferedInputStream input = null;
        FileInputStream fileInput = null;
        if (logo.equals("mainLogo")) {
            filePath = UConfig.getParameterValue(0, "PRODUCT_MAIN_LOGO");
            defaultFile = getServletContext().getRealPath("") + "/images/medics_logo.png";
        } else if (logo.equals("subLogo")) {
            filePath = UConfig.getParameterValue(((Integer) request.getSession().getAttribute("userEntityRID")).intValue(), "PRODUCT_SUB_LOGO");
            defaultFile = getServletContext().getRealPath("") + "/images/medics_logo_small.png";
        }

        if (filePath == null) {
            filePath = "";
        }

        File file = new File(filePath);

        try {
            if (!file.exists()) {
                filePath = defaultFile;
                file = new File(filePath);
            }

            boolean dd = file.exists();

            String fileName = file.getName();

            fileInput = new FileInputStream(filePath);
            input = new BufferedInputStream(fileInput, BUFFER_SIZE);

            response.setContentType("image/png");

            OutputStream out = null;
            out = response.getOutputStream();

            int count;
            byte data[] = new byte[BUFFER_SIZE];

            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            while ((count = input.read(data, 0, BUFFER_SIZE)) != -1) {
                out.write(data, 0, count);
            }

            out.flush();
            out.close();

        } catch (Exception e) {
            throw new UBaseException("Failed to write compressed file : " + e.getMessage(), e);
        } finally {
            try {
                if (fileInput != null) {
                    fileInput.close();
                }

                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                throw new UBaseException("Failed to close input streams : " + ex.getMessage(), ex);
            }
        }
    }
}
