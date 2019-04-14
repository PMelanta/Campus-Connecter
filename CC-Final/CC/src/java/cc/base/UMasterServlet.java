
package cc.base;

import cc.util.UString;
import cc.util.ULocale;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.sql.*;


import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

public class UMasterServlet extends UHttpServlet {
    
    static Logger logger = Logger.getLogger(UMasterServlet.class);
    private static final String OPEN_SELECTED_ROLES_DETAILS_JSP = "/jsp/config/SelectedRoles.jsp";
    private static final String OPEN_SELECTED_ACCESSIBLE_ROLES_DETAILS_JSP = "/jsp/config/SelectedAccessibleRoles.jsp";
    private final String EXPORT_ROLEMASTER_DETAILS_JSP = "/jsp/config/ExportRoleDetails.jsp";
    
    public void doUserMaster(URequestContext ctxt)
    throws UServletException, UDBAccessException {
        
        // Load Entities
        ResultSet rs = UEntity.getChildEntityWithoutparent(ctxt,ctxt.getUserRootEntityRID());
        ctxt.setAttribute("entities", rs);
        
        
        int productRID = ctxt.getProductRID();
        
        int entityRID = ctxt.getIntParameter("entityRID");
        
        // @@ Shouldn't we pass entityRID?
        rs = URole.getRoles(ctxt, ctxt.getUserEntityRID(), productRID, true);
        
        ctxt.setAttribute("roles", rs);
        
        int userRID = ctxt.getIntParameter("userRID");
        
        if(userRID > 0) {
            
            // Load User details
            rs = UUserManager.getUser(ctxt, userRID);
            ctxt.setAttribute("userDetails", rs);
            
            // Load user roles
            rs = UUserManager.getUserRoles(ctxt, userRID);
            ctxt.setAttribute("userRoles", rs);
        }
        
        UServletHelper.sendJSP(ctxt, servletConfig, "userMasterJSP");
    }
    
    public void doAppUserMaster(URequestContext ctxt)
    throws UServletException, UDBAccessException {
        //get appropriate connection
        String target_machine = ctxt.getParameter("target_machine") ;
        String target_db = ctxt.getParameter("db_name") ;
        String dateStr = null ;
        UQueryEngine qe = null ;
        
        try{
            if(ctxt.getDBConnection().getMetaData().getDatabaseProductName().equalsIgnoreCase("oracle"))
                qe = ctxt.getQueryEngine() ;
            else
                qe = new UQueryEngine(target_machine, target_db) ;
        } catch(Exception e){
            logger.error("Unable to get database connection") ;
            throw new UDBAccessException(e.getMessage(),e) ;
        }
        
        // Load Entities
        //ResultSet rs = UEntity.getEntities(ctxt, true);
        ResultSet rs = qe.executeQuery("select * from u_entity where ent_registered = 1 order by ent_name") ;
        ctxt.setAttribute("entities", rs);
        
        int productRID = ctxt.getProductRID();
        int entityRID = ctxt.getUserEntityRID();
        
        //rs = URole.getRoles(ctxt, ctxt.getUserEntityRID(), productRID, true);
        ResultSet rolesRS = qe.executeQuery("select * from u_role where (role_ent_rid = " + String.valueOf(entityRID) +
                " or role_ent_rid = 0) and role_prod_rid = " + String.valueOf(productRID) +
                " and role_valid = 1 order by role_name");
        ctxt.setAttribute("roles", rolesRS);
        
        int userRID = ctxt.getIntParameter("userRID");
        if(userRID > 0) {
            // Load User details
            ResultSet userRS = qe.executeQuery("select * from u_user where user_rid = " + String.valueOf(userRID)) ;
            ctxt.setAttribute("userDetails", userRS);
            // Load user roles
            //rs = UUserManager.getUserRoles(ctxt, userRID);
            ResultSet userRolesRS = qe.executeQuery("select * from u_user_role_map, u_role where ur_role_rid = role_rid and ur_user_rid = " + String.valueOf(userRID)) ;
            ctxt.setAttribute("userRoles", userRolesRS);
        }
        UServletHelper.sendJSP(ctxt, servletConfig, "userMasterJSP");
        qe.close() ;
    }
    
    public void loadUsers(URequestContext ctxt)
    throws UDBAccessException, UBaseException {
        
        int entityRID = ctxt.getIntParameter("entityRID");
        
        ResultSet rs = UUserManager.getUsers(ctxt, false);
        
        UWriterHelper.writeSelect(ctxt, rs, "user_rid", "user_full_name");
    }
    
    public void doRoleMaster(URequestContext ctxt)
    throws UServletException, UDBAccessException {
        
        int productRID = ctxt.getProductRID();
        
        ResultSet rs = URole.getRoles(ctxt, ctxt.getUserEntityRID(), productRID, false);
        
        ctxt.setAttribute("roles", rs);
        
        int roleRID = ctxt.getIntParameter("roleRID");
        
        if(roleRID > 0) {
            
            rs = URole.getRole(ctxt, roleRID);
            
            ctxt.setAttribute("roleDetail", rs);
            
            rs = URole.getRoleFeatures(ctxt, roleRID);
            
            ctxt.setAttribute("assignedFeatures", rs);
        }
        
        rs = UProduct.getFeatures(ctxt, productRID, true);
        
        ctxt.setAttribute("productFeatures", rs);
        
        UServletHelper.sendJSP(ctxt, servletConfig, "roleMasterJSP");
    }
    public void doRoleMaster(URequestContext ctxt,boolean showAll)
    throws UServletException, UDBAccessException {
        
        int productRID = ctxt.getProductRID();
        
        ResultSet rs = URole.getRoles(ctxt,false);
        ctxt.setAttribute("roles", rs);
        
        int roleRID = ctxt.getIntParameter("roleRID");
        
        if(roleRID > 0) {
            
            rs = URole.getRole(ctxt, roleRID);
            
            ctxt.setAttribute("roleDetail", rs);
            
            rs = URole.getRoleFeatures(ctxt, roleRID);
            
            ctxt.setAttribute("assignedFeatures", rs);
        }
        if(roleRID > 0)
            rs = UProduct.getFeatures(ctxt, productRID, true,false,roleRID,"");
        else
            rs = UProduct.getFeatures(ctxt, productRID, true,true,0, "");
        
        ctxt.setAttribute("productFeatures", rs);
        int roleMaster = ctxt.getIntParameter("roleMaster");
        if(roleMaster != 1)
            UServletHelper.sendJSP(ctxt, servletConfig, "roleBrowserJSP");
        else
            UServletHelper.sendJSP(ctxt, servletConfig, "roleMasterJSP");
    }
    
    private void doRoleMasterSorted(URequestContext ctxt) throws UDBAccessException, UServletException {
        int productRID = ctxt.getProductRID();
        
        UQueryEngine qe=ctxt.getQueryEngine();
        
        
        ResultSet rs = URole.getRoles(ctxt, ctxt.getUserEntityRID(), productRID, false);
        
        ctxt.setAttribute("roles", rs);
        
        int roleRID = ctxt.getIntParameter("roleRID");
        
        if(roleRID > 0) {
            
            rs = URole.getRole(ctxt, roleRID);
            
            ctxt.setAttribute("roleDetail", rs);
            
            rs = URole.getRoleFeatures(ctxt, roleRID);
            
            ctxt.setAttribute("assignedFeatures", rs);
        }
        
        String sql = " select * from u_feature join u_feature_entity_map on feature_rid = fem_feature_rid and "
                + " fem_entity_rid = " + ctxt.getUserEntityRID() + " where feature_prod_rid = " + productRID + " and "
                + " feature_valid = 1 and feature_name is not null order by feature_name";
        
        rs=qe.executeQuery(sql);
        
        ctxt.setAttribute("productFeatures", rs);
        
        UServletHelper.sendJSP(ctxt, servletConfig, "roleMasterJSP");
    }
    public void reloadRoles(URequestContext ctxt)
    throws UDBAccessException, UBaseException {
        
        int productRID = ctxt.getProductRID();
        
        ResultSet rs = URole.getRoles(ctxt, ctxt.getUserEntityRID(), productRID, false);
        
        UWriterHelper.writeSelect(ctxt, rs, "role_rid", "role_name");
    }
    
    
    
    public void doEntityMaster(URequestContext ctxt)
    throws UServletException, UDBAccessException {
        
        ResultSet rs = UEntity.getEntities(ctxt,false);
        ctxt.setAttribute("entities", rs);
        ResultSet rst = UEntity.getEntityType(ctxt, false);
        ctxt.setAttribute("entityTypes", rst);
        
        int EntityRID = ctxt.getIntParameter("EntityRID");
        
        UServletHelper.sendJSP(ctxt, servletConfig, "entityMasterJSP");
    }
    
    
    public void loadAccessibleCommands(URequestContext ctxt)
    throws UDBAccessException, UBaseException {
        
        int userRID = ctxt.getUserRID();
        int productRID = ctxt.getProductRID();
        
        if(userRID < 0)
            throw new UServletException("Failed to fetch User ID from session!",
                    new UBaseException("Failed to fetch User ID from session!", null));
        else {
            Vector v;
            
            try {
                v = UUserManager.getAccessibleCommands(ctxt, productRID, userRID);
            } catch (Exception e) {
                throw new UServletException("Failed to fetch accessible commands! ",
                        new UBaseException(e.getMessage(), null));
            }
            
            ctxt.setAttribute("accessibleCommands", v);
            
            UServletHelper.sendJSP(ctxt, servletConfig, "commandsJSP");
        }
        
    }
    
    private void reconfigureLogging(URequestContext ctxt)
    throws UBaseException {
        
        String fileName = getServletContext().getInitParameter("log4j-init-file");
        
        String responseMessage = "";
        
        // if the log4j-init-file is not set, then no point in trying
        if(fileName != null) {
            String configFile =  getServletContext().getRealPath(fileName);
            
            logger.info("Loading log4j config from " + configFile);
            
            PropertyConfigurator.configure(configFile);
            
            responseMessage = "Logger settings reloaded from " + fileName;
            
        } else {
            
            BasicConfigurator.configure();
            
            responseMessage = "Logger settings set to system defaults";
        }
        
        UWriterHelper.sendResponse(ctxt, "<div><br>" + responseMessage + "</div>");
    }
    
    private void flushSystemCache(URequestContext ctxt)
    throws UBaseException {
        
        try {
            UConfig.flushCache();
            ULocale.clearCache();
            //LanguageSettingManager.clearLanguageSettings();
            ctxt.setAttribute("success", Boolean.TRUE);
            
        } catch(Exception e) {
            
            ctxt.setAttribute("success", Boolean.FALSE);
            
        } finally {
            UServletHelper.sendJSP(ctxt, servletConfig, "ClearCacheJSP");
        }
        
    }
    
    private void loadSysUsers(URequestContext ctxt)
    throws UDBAccessException, UServletException {
        int productRID = ctxt.getProductRID();
        int entityRID = ctxt.getIntParameter("entityRID");
        ResultSet rs;
        // @@ Shouldn't we pass entityRID?
        rs = URole.getRoles(ctxt, ctxt.getUserEntityRID(), productRID, true);
        
        ctxt.setAttribute("roles", rs);
        
        int userRID = ctxt.getIntParameter("userRID");
        
        if(userRID > 0) {
            
            // Load User details
            rs = UUserManager.getUser(ctxt, userRID);
            ctxt.setAttribute("userDetails", rs);
            
            // Load user roles
            rs = UUserManager.getUserRoles(ctxt, userRID);
            ctxt.setAttribute("userRoles", rs);
        }
        
        //load all users
        String sql= "select * from u_user uu where uu.user_entity_rid="+ctxt.getUserEntityRID()+" and uu.user_account_rid=1";
        UQueryEngine qe=ctxt.getQueryEngine();
        rs=qe.executeQuery(sql);
        ctxt.setAttribute("sysUsers",rs);
        sql= "select * from u_user uu where uu.USER_ACCOUNT_RID=0 and uu.user_entity_rid = " + ctxt.getUserEntityRID();
        rs=qe.executeQuery(sql);
        ctxt.setAttribute("empUsersRS",rs);
        UServletHelper.sendJSP(ctxt, servletConfig, "userMasterJSP");
    }
    
    
    private void loadStaff(URequestContext ctxt)
    throws UDBAccessException, UServletException {
        int productRID = ctxt.getProductRID();
        ResultSet rs;
        
        rs = URole.getRoles(ctxt, ctxt.getProjectRID(), productRID, true);
        
        ctxt.setAttribute("roles", rs);
        
        int userRID = ctxt.getIntParameter("userRID");
        
        if(userRID > 0) {
            
            // Load User details
            rs = UUserManager.getUser(ctxt, userRID);
            ctxt.setAttribute("userDetails", rs);
            
            // Load user roles
            rs = UUserManager.getUserRoles(ctxt, userRID);
            ctxt.setAttribute("userRoles", rs);
        }
        
        //load all users
        String sql= "select * from u_user order by user_full_name" ;
        UQueryEngine qe=ctxt.getQueryEngine();
        rs=qe.executeQuery(sql);
        ctxt.setAttribute("sysUsers",rs);
        UServletHelper.sendJSP(ctxt, servletConfig, "userMasterJSP");
    }
    
    
    
    
    public void handleGet(URequestContext ctxt)
    throws UServletException {
        
        String command = ctxt.getParameter("command");
        
        try {
            if("loadRoleMaster".equals(command))
                doRoleMaster(ctxt);
            else if("reloadRoles".equals(command))
                reloadRoles(ctxt);
            else if("loadUserMaster".equals(command))
                doUserMaster(ctxt);
            else if("loadAppUsers".equals(command)) //added by gopi on 28-08-2006
                doAppUserMaster(ctxt);
            else if("loadUsers".equals(command))
                loadUsers(ctxt);
            else if("loadEntityMaster".equals(command))
                doEntityMaster(ctxt);
            else if("loadAccessibleCommands".equals(command))
                loadAccessibleCommands(ctxt);
            else if("reconfigureLogging".equals(command))
                reconfigureLogging(ctxt);
            else if("flushSystemCache".equals(command))
                flushSystemCache(ctxt);
            else if("loadRoleMasterForRole".equals(command))
                doRoleMaster(ctxt,false);
            else if("loadSysUsers".equals(command)) //added by Akhil on 27/05/07
                loadSysUsers(ctxt);
            else if("loadStaff".equals(command)) //added by Gopi on 17/15/2007
                loadStaff(ctxt);
            else if("loadRoleMasterSorted".equals(command))
                // commented by sunil for showing only the non-assigned privileges.
                //doRoleMasterSorted(ctxt);
                doRoleMaster(ctxt,false);
            else if("setDefaultFeature".equals(command)) //GOPI - 17/10/2007
                _setDefaultFeature(ctxt);
            else if("getBroadcastMessage".equals(command)) //GOPI - 08/04/2008
                _getBroadcastMessage(ctxt);
            else if("loadRolesList".equals(command))
                _getRoles(ctxt);            
            else if("exportRoleMasterFeatures".equals(command))
                _exportRoleMasterFeatures(ctxt);
            else if("searchbyRoleName".equals(command))
                _searchbyRoleName(ctxt);
            else if("loadRemoteApp".equals(command)) //GOPI - 29/Mar/2011
                _loadRemoteApp(ctxt);
        } catch(Exception e) {
            
            throw new UServletException(e.getMessage(), e);
        }
        
    }
    
    public void handlePost(URequestContext ctxt)
    throws UServletException {
        
        String command = ctxt.getParameter("command");
        
        if("loadUserMaster".equals(command)) {
            try {
                doUserMaster(ctxt);
            } catch(Exception e) {
                
                throw new UServletException(e.getMessage(), e);
            }
        } else {
            try {
                if("saveRole".equals(command))
                    URole.saveRole(ctxt, 0);
                else if("saveUser".equals(command))
                    UUserManager.saveUser(ctxt);
                else if("saveEntity".equals(command))
                    UEntity.saveEntity(ctxt);
                else if("saveSysUser".equals(command)) //akhil
                    _saveSysUser(ctxt);
                else if("saveRoleWithMesaage".equals(command)){
                    _saveRoleWithMesaage(ctxt);
                } else
                    ctxt.setAttribute("errorMessage", "UserMaster~Save failed. Please try again or contact your system administrator.");
                
            } catch(Exception e) {
                
                if(ctxt.getAttribute("errorMessage") == null)
                    ctxt.setAttribute("errorMessage", "UserMaster~Save failed. Please try again or contact your system administrator.");
            }
            
        }
        
        UServletHelper.sendJSP(ctxt, servletConfig, "dataEntryResponseJSP");
    }
    
    private void _saveSysUser(URequestContext ctxt) throws Exception{
        // UUserManager.saveUser(ctxt);
        try {
            int userRID = ctxt.getIntParameter("userRID");
            
            if(userRID > 0)
                UUserManager.updateUser(ctxt, userRID);
            else
                userRID=UUserManager.insertUser(ctxt);
            
            //UUserManager.updateAccountInfo(ctxt,userRID,1);
            
        }catch (Exception ex){
            throw new Exception();
        }
        ctxt.setAttribute("success", "User Details Saved Successfully");
    }
    
    private void _saveRoleWithMesaage(URequestContext ctxt) throws Exception {
        try{
            /* @@ Why entity is passed as 0 (Hard coded) ' URole.saveRole(ctxt, 0); - old code '
             * @@ Discussed with Manju & Sunil, need to finalize with Dr. Amitava � Added by Girish */
            URole.saveRole(ctxt, ctxt.getProjectRID());
            ctxt.setAttribute("success", "Roles Details Saved Successfully");
            //ctxt.setAttribute("url", "UMasterServlet?command=loadRoleMasterSorted&roleMaster=1");
        }catch (Exception ex){
            throw new Exception();
        }
    }
    
    private void _setDefaultFeature(URequestContext ctxt) throws Exception {
        String status = "success" ;
        try{
            int newFeatureRID = ctxt.getIntParameter("newFeatureRID") ;
            
            UUserManager.setPrimaryFeature(ctxt, newFeatureRID) ;
            
        }catch (Exception ex){
            status = "failure" ;
            throw new Exception();
        }
        
        finally{
            ctxt.getHttpResponse().getWriter().write(status) ;
        }
    }
    
    private void _getBroadcastMessage(URequestContext ctxt)
    throws UServletException {
        try{
            String serverDowntime = "";//UMaintenanceHelper.getServerDowntime(ctxt.getUserEntityRID()) ;
            
            ctxt.getHttpResponse().getWriter().write(serverDowntime) ;
            
        } catch (Exception e){
            throw new UServletException("Error in gettting server downtime: " + e.getMessage()) ;
        }
    }
    
    public void  _getRoles(URequestContext ctxt)
    throws UServletException, UDBAccessException {
        try {
            
            String searchString = ctxt.getParameter("searchStr");
            searchString = searchString.replaceAll(" " , "%");
            searchString = UString.escapeSpecialChars(searchString);
            
            int productRID = ctxt.getProductRID();
            
            ResultSet rs = null;
            int roleRID = ctxt.getIntParameter("roleRID");
            
            
            
            if(roleRID > 0)
                rs = UProduct.getFeatures(ctxt, productRID, true,false,roleRID,searchString);
            else
                rs = UProduct.getFeatures(ctxt, productRID, true,true,0, searchString);
            
            ctxt.setAttribute("productFeatures", rs);
            
            UServletHelper.sendJSP(ctxt, OPEN_SELECTED_ROLES_DETAILS_JSP);
        } catch (Exception e) {
            throw new UServletException(e.getMessage(), e);
        }
    }
    
    
    private void _exportRoleMasterFeatures(URequestContext ctxt)
    throws UServletException {
        try{
            int productRID = ctxt.getProductRID();
            int roleRID = ctxt.getIntParameter("roleRID");
            String roleName = ctxt.getParameter("roleName");
            int showActive = ctxt.getParameter("showActive") == null ? 1 : ctxt.getIntParameter("showActive");
            ctxt.setAttribute("showActive", showActive);
            ResultSet rs = null;//URole.getRolesByName(ctxt, roleName, showActive);
            ctxt.setAttribute("roles", rs);
            
            //rs = UProduct.getAllRolesFeatures(ctxt, productRID, true, roleName);
            
            ctxt.setAttribute("productFeatures", rs);
            
            String headingInExcel = "";
            String excelFileName = "";
            
            //headingInExcel = "Features assigned to Role list as on date "+ctxt.getDateTimeManager().nowDisplayString();
            excelFileName = "Features assigned to Roles List.xls";
            
            ctxt.setAttribute("headingInExcel", headingInExcel);
            ctxt.setAttribute("excelFileName", excelFileName);
            
            UServletHelper.sendJSP(ctxt, EXPORT_ROLEMASTER_DETAILS_JSP);
            
        } catch (Exception e){
            throw new UServletException("Error in exporting features " + e.getMessage()) ;
        }
    }
    
    private void _searchbyRoleName(URequestContext ctxt)
    throws UServletException {
        String roleName = ctxt.getParameter("roleName");
        try{
            int showActive = ctxt.getParameter("showActive") == null ? 1 : ctxt.getIntParameter("showActive");
            ctxt.setAttribute("showActive", showActive);
            ResultSet rs = URole.getRolesByName(ctxt, roleName, showActive);
            ctxt.setAttribute("roles", rs);
            UServletHelper.sendJSP(ctxt, servletConfig, "roleBrowserJSP");
        } catch (Exception e){
            throw new UServletException("Error in exporting features " + e.getMessage()) ;
        }
    }
    
    private void _loadRemoteApp(URequestContext ctxt) throws UServletException{
        try{
            String partialURL = ctxt.getParameter("url") ;
            HttpServletRequest req = ctxt.getHttpRequest() ;
            HttpServletResponse resp = ctxt.getHttpResponse() ;
            String remoteProdCode = ctxt.getParameter("remoteProdCode") ;
            
            
            
//            ResultSet prodRS = UProduct.loadProduct(ctxt, ctxt.getProductRID());
//            
//            if(prodRS != null && prodRS.first()){
                /*
                String final_url = partialURL + "?sessionID=" + ctxt.getSession().getId() +
                        "&db_type=" + prodRS.getString("prod_db_type") +
                        "&db_name=" + prodRS.getString("prod_db_name") +
                        "&db_user_name=" + prodRS.getString("prod_db_user_name") +
                        "&db_password=" + prodRS.getString("prod_db_password") +
                        "&db_driver_classname=" + prodRS.getString("prod_db_driver_classname") +
                        "&db_port=" + prodRS.getString("prod_db_port") +
                        "&db_machine_addr=" + prodRS.getString("prod_db_host_addr")  ;
                 
                logger.debug("Launching App at: " + final_url) ;
                 resp.sendRedirect(final_url) ;
                 */
                
//                ctxt.setAttribute("remoteAppURL", partialURL) ;
//                ctxt.setAttribute("db_type", prodRS.getString("prod_db_type")) ;
//                ctxt.setAttribute("db_name", prodRS.getString("prod_db_name")) ;
//                ctxt.setAttribute("db_user_name", prodRS.getString("prod_db_user_name")) ;
//                ctxt.setAttribute("db_password", prodRS.getString("prod_db_password")) ;
//                ctxt.setAttribute("db_driver_classname", prodRS.getString("prod_db_driver_classname")) ;
//                ctxt.setAttribute("db_port", prodRS.getString("prod_db_port")) ;
//                ctxt.setAttribute("db_machine_addr", prodRS.getString("prod_db_host_addr")) ;
//                ctxt.setAttribute("prodName", prodRS.getString("prod_name")) ;
                ctxt.setAttribute("session_logged_in", "1");
                ctxt.setAttribute("session_product_rid", String.valueOf(ctxt.getProductRID()));
                ctxt.setAttribute("session_user_name", ctxt.getUserName());
                ctxt.setAttribute("session_user_rid", String.valueOf(ctxt.getUserRID()));
                ctxt.setAttribute("remoteAppURL", partialURL) ;               
                ctxt.setAttribute("remoteProdCode", remoteProdCode) ;
                
                UServletHelper.sendJSP(ctxt, servletConfig, "redirectToRemoteAppJSP") ;
                
            //}
        }catch(Exception e){
            e.printStackTrace() ;
            throw new UServletException("Error in loading Application", e) ;
        }
    }
    
    
}
