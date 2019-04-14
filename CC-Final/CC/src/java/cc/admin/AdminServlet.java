/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.admin;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import cc.base.UHttpServlet;
import cc.base.URequestContext;
import cc.base.UServletException;
import cc.base.UServletHelper;
import cc.base.UWriterHelper;
import cc.base.UHttpServlet;

/**
 *
 * @author suhas
 */
public class AdminServlet extends UHttpServlet {
//manu

   static Logger logger = Logger.getLogger(AdminServlet.class);
   public static final String colegJSP = "/jsp/sms/Coleg.jsp";
   public static final String colegDetailsJSP = "/jsp/sms/EditColeg.jsp";
   public static final String responseJSP = "/jsp/common/uhDataEntryResponse.jsp";

   @Override
   public void handleGet(URequestContext ctxt) throws UServletException {
      String command = ctxt.getParameter("command");
      try {
         if ("loadCollegeView".equals(command)) {
            ctxt.setAttribute("rsColegDet", Coleg.getColegDetails(ctxt, 0));
            UServletHelper.sendJSP(ctxt, colegJSP);
         } else if ("editColeg".equals(command)) {
            int colegRid = ctxt.getIntParameter("colegRid");
            ctxt.setAttribute("rsColegDet", Coleg.getColegDetails(ctxt, colegRid));
            UServletHelper.sendJSP(ctxt, colegDetailsJSP);

         }
      } catch (Exception ex) {
         logger.error("Unable to process the request - " + ex.getMessage());
      }
   }

   @Override
   public void handlePost(URequestContext ctxt) throws UServletException {
      String command = ctxt.getParameter("command");
      try {
         if ("saveColegDetails".equals(command)) {
            _saveColegDetails(ctxt);
         }
      } catch (Exception ex) {
         logger.error("Unable to process the request - " + ex.getMessage());
      }
   }

   private void _saveColegDetails(URequestContext ctxt)
           throws UServletException {
      try {
         int colegRid = ctxt.getIntParameter("colegRid"); //won't apply for newly created branch
         String colegName = ctxt.getParameter("colegName");
         String colegCode = ctxt.getParameter("colegCode");
         String colegAddress = ctxt.getParameter("colegAddress");
         String colegEmail = ctxt.getParameter("colegEmail");
         String colegPhone = ctxt.getParameter("colegPhone");
         Coleg.saveColegDetails(ctxt, colegRid, colegName, colegCode, colegAddress, colegEmail, colegPhone);
         ctxt.setAttribute("success", "Saved Successfully");
      } catch (Exception ex) {
         logger.error("Unable to process the request - " + ex.getMessage());
         ctxt.setAttribute("errorMessage", "Unable to update branch details");
      } finally {
         UServletHelper.sendJSP(ctxt, responseJSP);
      }
   }
}
