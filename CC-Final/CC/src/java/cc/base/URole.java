package cc.base;

import java.sql.*;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

public final class URole {

    static Logger logger = Logger.getLogger(URole.class);
   public static final int AUDIT_EVENT_INDEX_ROLES_CREATION = 190;
   public static final int AUDIT_EVENT_INDEX_ROLES_MODIFICATION = 191;
    private URole() {
    }
//@suhas
    public static ResultSet getRoles(URequestContext ctxt, int projectRid, int productRID, boolean showValidOnly)
	throws UDBAccessException {
	String sql = "select * from u_role where "
                + " role_prod_rid = " + productRID;

	if(showValidOnly)
	    sql = sql + " and role_valid = 1";

	sql = sql + " order by role_name";

	UQueryEngine qe = ctxt.getQueryEngine();

	ResultSet rs = qe.executeQuery(sql);
        
	return rs;
    }
    
    //@suhas
     public static ResultSet getRoles(URequestContext ctxt, boolean showValidOnly)
	throws UDBAccessException {
	String sql = "select * from u_role ";

	if(showValidOnly)
	    sql = sql + " and role_valid = 1";

	sql = sql + " order by role_name";

	UQueryEngine qe = ctxt.getQueryEngine();

	ResultSet rs = qe.executeQuery(sql);
        
	return rs;
    }
    
    public static ResultSet getRole(URequestContext ctxt, int roleRID)
	throws UDBAccessException {

	String sql = "select * from u_role where role_rid = " + roleRID;

	sql = sql + " order by role_name";

	UQueryEngine qe = ctxt.getQueryEngine();

	ResultSet rs = qe.executeQuery(sql);

	return rs;
    }

    public static ResultSet getRoleByName(URequestContext ctxt, String roleName)
	throws UDBAccessException {
        /* @@ Entity check should be parameterized? 
         * @@ Discussed with Manju & Sunil, need to finalize with Dr. Amitava � Added by Girish */

	String sql = "select * from u_role where role_name = '" + roleName + "' and role_ent_rid = " + ctxt.getUserEntityRID() + 
                     " order by upper(role_name) ";

	return ctxt.getQueryEngine().executeQuery(sql);
    }

    public static int getRoleRIDByName(URequestContext ctxt, String roleName)
	throws UDBAccessException, SQLException {

	ResultSet rs = getRoleByName(ctxt, roleName);

	if(rs.next())
	    return rs.getInt("role_rid");
	
	return 0;
    }

    public static ResultSet getRoleFeatures(URequestContext ctxt, int roleRID) 
	throws UDBAccessException {

	String sql = "select u_feature.* from u_priv, u_feature where priv_feature_rid = feature_rid and " +
	    "priv_role_rid = " + roleRID + " and feature_valid = 1 and feature_name is not null order by feature_name";

	UQueryEngine qe = ctxt.getQueryEngine();

	ResultSet rs = qe.executeQuery(sql);

	return rs;
    }

    private static void insertPrivs(URequestContext ctxt, UQueryEngine qe, int roleRID) 
	throws UDBAccessException {

	String[] featureRIDs = ctxt.getParameterValues("assignedFeature");

	if(featureRIDs == null)
	    return;

	int productRID = ctxt.getProductRID();

	for(int i = 0; i < featureRIDs.length; i++) {

	    String sqlPriv = "insert into u_priv (priv_role_rid, priv_prod_rid, priv_feature_rid,priv_proj_id) values (" +
		roleRID + ", " + productRID + ", " + Integer.parseInt(featureRIDs[i]) + ","+ctxt.getProjectRID()+")";

	    qe.executeInsert(sqlPriv);
	}
    }

    private static boolean roleExists(URequestContext ctxt, String roleName, int roleRID)
	throws UDBAccessException {
        /* @@ Entity check should be parameterized? 
         * @@ Discussed with Manju & Sunil, need to finalize with Dr. Amitava � Added by Girish */
	try {
	    String sql = " select role_rid from u_role where role_name = '" + roleName + "' and role_valid = 1 " +
                         " and role_proj_rid = " + ctxt.getProductRID();

	    if(roleRID != 0)
		sql = sql + " and role_rid <> " + roleRID;

	    UQueryEngine qe = ctxt.getQueryEngine();

	    ResultSet rs = qe.executeQuery(sql);

	    if(rs != null && rs.next())
		return true;

	    return false;
	} catch(Exception e) {

	    throw new UDBAccessException(e.getMessage(), e);
	}

    }

    public static int insertRole(URequestContext ctxt, int projectRid) 
      throws UDBAccessException {

	String roleName = ctxt.getParameter("roleName");

	// Check if a role of THIS name already exists
	if(roleExists(ctxt, roleName, 0)) {

	    ctxt.setAttribute("errorMessage", "Role with name '" + roleName + 
			      "' already exists for this product. Please enter a different name.");
	    return 0;
	}

	// Not found. Go ahead and insert

	String sql = "insert into u_role (role_proj_rid, role_prod_rid, role_name, role_valid) values (";

	sql = sql + projectRid + ", " + ctxt.getProductRID() + ", '" + roleName + "', ";

	sql = sql + ("on".equals(ctxt.getParameter("isActive")) ? 1 : 0) + ")";

	UQueryEngine qe = ctxt.getQueryEngine();

	int rid = 0;

	try {
	    qe.beginTransaction();
            
            /*if(qe.getConnection().getMetaData().getDatabaseProductName().equalsIgnoreCase("oracle"))
                rid = _executeOracleInsert(qe,sql) ;
            else*/
                rid = qe.executeInsert(sql);

	    insertPrivs(ctxt, qe, rid);
	    //UAuditTrail.addEntry(ctxt, AUDIT_EVENT_INDEX_ROLES_CREATION, roleName);
            
	    qe.commitTransaction();

	} catch (Exception e) {

	    try {
  	      qe.rollbackTransaction();

	    } catch(Exception ex) {
		// Nothing to do
	    }

	    throw new UDBAccessException(e.getMessage(), e);
	}

	return rid;
    }
    
    private static int _executeOracleInsert(UQueryEngine qe, String sql)
    throws UDBAccessException{
        int nextRowID = 0 ;
        try{
            qe.executeInsert(sql) ;
            ResultSet rs = qe.executeQuery("select U_ROLE_0.currval from dual") ;
            if(rs!= null){
                rs.first() ;
                nextRowID = rs.getInt(1) ;
            }
        } catch (Exception e){
            logger.error("Error in inserting new Role") ;
            throw new UDBAccessException(e.getMessage(),e) ;
        }
        return nextRowID ;
    }

    //@suhas
    public static void updateRole(URequestContext ctxt, int projectRid, int roleRID) 
	throws UDBAccessException {

	String roleName = ctxt.getParameter("roleName");

	// Check if a role of THIS name already exists
	if(roleExists(ctxt, roleName, roleRID)) {

	    ctxt.setAttribute("errorMessage", "Role with name '" + roleName + 
			      "' already exists for this product. Please enter a different name.");
	    return;
	}

	// No dulicates found. Go ahead and update
	String sql = "update u_role set role_proj_rid = " + projectRid + 
	    ", role_prod_rid = " + ctxt.getProductRID() + 
	    ", role_name = '" + ctxt.getParameter("roleName") + "'" +
	    ", role_valid = " + ("on".equals(ctxt.getParameter("isActive")) ? 1 : 0) + 
	    " where role_rid = " + roleRID;

	UQueryEngine qe = null;

	try {
	    qe = ctxt.getQueryEngine();

	    qe.beginTransaction();

	    qe.executeUpdate(sql);

	    String sqlDel = "delete from u_priv where priv_role_rid = " + roleRID;

	    qe.executeUpdate(sqlDel);

	    insertPrivs(ctxt, qe, roleRID);
            //UAuditTrail.addEntry(ctxt, AUDIT_EVENT_INDEX_ROLES_MODIFICATION,  roleName);
            

	    qe.commitTransaction();

	} catch (Exception e) {

	    try { qe.rollbackTransaction(); } catch (Exception ex) {}

	    throw new UDBAccessException(e.getMessage(), e);
	}

    }
//@suhas
    public static void saveRole(URequestContext ctxt, int projectRid) 
	throws UDBAccessException {

	int roleRID = ctxt.getIntParameter("roleRID");

	if(roleRID > 0) 
	    updateRole(ctxt, projectRid, roleRID);
	else
	    insertRole(ctxt, projectRid);
    }

    public static int addPriv(URequestContext ctxt, int productRID, int roleRID, int featureRID) 
	throws UDBAccessException {

	String sql = "insert into u_priv (priv_role_rid, priv_prod_rid, priv_feature_rid) values (" +
	    roleRID + ", " + productRID + ", " + featureRID + ")";

	UQueryEngine qe = ctxt.getQueryEngine();

	return qe.executeInsert(sql);
    }
    
    public static ResultSet getRolesByName(URequestContext ctxt, String roleName, int showActive)
    throws UDBAccessException {
        try{
            String sql = " select  * " +
                    " from u_role " +
                    " where role_name like '" + roleName + "%' ";
            if(showActive == 1)
                sql = sql + " and role_valid = "+showActive;
            
            sql = sql + " order by role_name";
            
            UQueryEngine qe = ctxt.getQueryEngine();
            ResultSet rs = qe.executeQuery(sql);
            return rs;
        }catch(Exception e)  {
            logger.debug("retrieving staff details " + e.getMessage());
            throw new UDBAccessException(e.getMessage(), e);
        }
        
    }

}
