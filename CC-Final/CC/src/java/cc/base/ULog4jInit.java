package cc.base;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.PrintWriter;
import java.io.IOException;

public class ULog4jInit extends HttpServlet {

    static Logger logger = Logger.getLogger(ULog4jInit.class);

  public void init(ServletConfig config) throws ServletException {

      super.init(config);

      String fileName = getServletContext().getInitParameter("log4j-init-file");

      // If the log4j-init-file is not set, then no point in trying
      if(fileName != null) {
	  String configFile =  getServletContext().getRealPath(fileName);

	  PropertyConfigurator.configure(configFile);
	  logger.info("Loaded log4j config from " + configFile);
      } else 
	  BasicConfigurator.configure();
      
      logger.debug("Exiting ULog4jInit");
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res) {
  }
}
