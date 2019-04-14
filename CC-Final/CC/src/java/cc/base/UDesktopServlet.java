/*
 * UDesktopServlet.java
 *
 * Created on June 30, 2008, 3:10 PM
 */

package cc.base;


import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;


/**
 *
 * @author suhas
 * @version
 */
public class UDesktopServlet extends UHttpServlet {
    public void handleGet(URequestContext ctxt)
    throws UServletException {
        try {
            String command = ctxt.getParameter("command");
            if("setSelectedFeature".equals(command)) {
                String selectedFeature = ctxt.getParameter("selectedFeature");
                if(selectedFeature != null && selectedFeature.lastIndexOf("/") < 0) {
                    ctxt.getSession().setAttribute("selectedFeature", selectedFeature);
                }
                
            } else if("checkConnection".equals(command)) {
                UWriterHelper.sendResponse(ctxt, "1");
            } else {
                _initDesktop(ctxt);
            }
        } catch(Exception ex) {
            logger.error("Failed to load desktop" + ex.getMessage());
            throw new UServletException("Failed to load desktop");
        }
    }
    
    public void handlePost(URequestContext ctxt)
    throws UServletException {
        try {
            _initDesktop(ctxt);
        } catch(Exception ex) {
            logger.error("Failed to load desktop" + ex.getMessage());
            throw new UServletException("Failed to load desktop");
        }
    }
    
    private void _initDesktop(URequestContext ctxt)
    throws UServletException, UBaseException, SQLException {
        
        String defaultFeature = ctxt.getContextPath() + "/Welcome"; //ctxt.getParameter("defaultFeature");
        ctxt.setAttribute("defaultFeature", defaultFeature);
        
        //Vector accessibleCommands = UUserManager.getAccessibleCommands(ctxt, ctxt.getProductRID(), ctxt.getUserRID(), 0);
        Vector accessibleCommands = UUserManager.getAccessibleCommands(ctxt);
        ctxt.setAttribute("accessibleCommands",new Vector(accessibleCommands));

        // below 2 lins are added for Paging purpose(oracle). Setting dbProdName as attribute and accessing this in Desktop.jsp file. - Vadiraj
        String dbProdName = (String)ctxt.getQueryEngine().getConnection().getMetaData().getDatabaseProductName();
        ctxt.setAttribute("dbProductName", dbProdName);
        
        UServletHelper.sendJSP(ctxt, servletConfig, "DesktopJSP");
    }
    
}
