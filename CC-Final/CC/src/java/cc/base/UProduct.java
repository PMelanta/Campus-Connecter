package cc.base;

import java.sql.*;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

public final class UProduct {

    static Logger logger = Logger.getLogger(UProduct.class);

    private UProduct() {
    }

    public static ResultSet loadProduct(URequestContext ctxt, int productRID) 
	throws UDBAccessException {

	String sql = "select * from u_product where prod_rid = " + productRID;

	UQueryEngine qe = ctxt.getQueryEngine();

	ResultSet rs = qe.executeQuery(sql);

	return rs;
    }

    public static String getProductName(URequestContext ctxt, int productRID) 
	throws UDBAccessException {

	String sql = "select prod_name from u_product where prod_rid = " + productRID;

	UQueryEngine qe = ctxt.getQueryEngine();

	ResultSet rs = qe.executeQuery(sql);

	String productName = "???";

	try {
	    if(rs != null && rs.next()) {
		productName = rs.getString("prod_name");
	    }
	} catch (Exception e) {
	    throw new UDBAccessException("Failed to get product name", e);
	}

	return productName;
    }

    public static String getProductTitle(URequestContext ctxt, int productRID) 
	throws UDBAccessException {

	String sql = "select prod_title from u_product where prod_rid = " + productRID;

	UQueryEngine qe = ctxt.getQueryEngine();

	ResultSet rs = qe.executeQuery(sql);

	String productTitle = "???";

	try {
	    if(rs != null && rs.next()) {
		productTitle = rs.getString("prod_title");
	    }
	} catch (Exception e) {
	    throw new UDBAccessException("Failed to get product title", e);
	}

	return productTitle;
    }

    public static ResultSet getFeatures(URequestContext ctxt, int productRID, boolean showValidOnly,boolean showAll,int rollRid, String searchStr) 
	throws UDBAccessException {

	String sql = "select * from u_feature where feature_name is not null  and  feature_prod_rid = " + productRID +" AND feature_parent_group <>0 ";
        
        if(!showAll)
            sql = sql + " and feature_rid not in (select priv_feature_rid from u_priv where priv_role_rid = " + rollRid + " and priv_prod_rid =" + productRID + ")";
	if(showValidOnly)
	    sql = sql + " and feature_valid = 1 ";

        sql = sql +  "AND  feature_name LIKE ('%"+ searchStr+"%')  order by feature_name";

	UQueryEngine qe = ctxt.getQueryEngine();

	ResultSet rs = qe.executeQuery(sql);

	return rs;
    }
        public static ResultSet getFeatures(URequestContext ctxt, int productRID, boolean showValidOnly)
	throws UDBAccessException {

	String sql = "select * from u_feature where feature_name is not null and feature_prod_rid = " + productRID ;
        
	if(showValidOnly)
	    sql = sql + " and feature_valid = 1";

	sql = sql + " order by feature_name";
        
        //sql = sql + " order by feature_group,feature_seq_num";

	UQueryEngine qe = ctxt.getQueryEngine();

	ResultSet rs = qe.executeQuery(sql);

	return rs;
    }
    public static boolean isVersionCompatibility(URequestContext ctxt, int productRID, String appVersion) 
	throws UDBAccessException {
        try {
            boolean isCompatible = false;
            String sql = "select  db_version, apv_db_version , apv_app_version from u_app_version_compatibility, u_db_version " +
                    " where apv_prod_rid = db_prod_rid and db_prod_rid =" + productRID ;
            
            UQueryEngine qe = ctxt.getQueryEngine();

            ResultSet rs = qe.executeQuery(sql);

            if (rs != null && rs.first())
            {
                if (rs.getString("db_version").trim().equals(rs.getString("apv_db_version").trim())
                        && appVersion.trim().equals(rs.getString("apv_app_version").trim())) {
                    isCompatible = true;
                } 
                
                /*
                else {
                    logger.error("App version :" + appVersion);
                    logger.error("App db version :" + rs.getString("apv_db_version"));
                    logger.error("Database version :" + rs.getString("db_version"));
                }
                 */
                 
            }

            return isCompatible;
        
        } catch (Exception e) 
            {
                throw new UDBAccessException("Failed to get application version", e);
            }
    }
  

}

