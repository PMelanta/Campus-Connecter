package cc.base;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.PrintWriter;
import java.io.IOException;

public class UMailDaemonInit extends HttpServlet {

    static Logger logger = Logger.getLogger(UMailDaemonInit.class);

  public void init(ServletConfig config) throws ServletException {

      UMailDaemon md = new UMailDaemon();

      md.start();
  }
}
