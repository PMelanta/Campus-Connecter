package cc.base;

import java.sql.*;
import java.util.Vector;

import org.apache.log4j.Logger;
import cc.util.DataDictionaryManager;

public final class UDataDictionary {

    private UDataDictionary () {
    }

    public static String loadDDValue(URequestContext ctxt, int dd_index) 
	throws UDBAccessException {

	try {
	    String sql = "select * from u_ddict where dd_index = " + dd_index;

	    ResultSet rs = ctxt.getQueryEngine().executeQuery(sql);

	    if(rs.next())
		return rs.getString("dd_value");
	    else
		return "";

	} catch (Exception e) {
	    throw new UDBAccessException("Error in loading Data Dictionary item. " + e.getMessage(), e);
	}
    }

    public static ResultSet loadDDDetails(URequestContext ctxt, int dd_index) 
	throws UDBAccessException {

	try {
	    String sql = "select * from u_ddict where dd_index = " + dd_index;

	    ResultSet rs = ctxt.getQueryEngine().executeQuery(sql);
            return rs;
	} catch (Exception e) {
	    throw new UDBAccessException("Error in loading Data Dictionary item. " + e.getMessage(), e);
	}
    }
    
    public static ResultSet getDDItems(URequestContext ctxt, int dd_item_type_index, int parent_index) 
	throws UDBAccessException {

	try {
	    String sql = "select dd_index, dd_ddi_type_index, dd_abbrv, dd_value, dd_parent_index " +
		" from u_ddict where dd_valid = 1";

	    sql = sql + " and dd_ddi_type_index = " + dd_item_type_index;

	    if(parent_index != 0) {
		sql = sql + " and dd_parent_index =" + parent_index;
	    }

	    sql = sql + " order by upper(dd_value) ";

	    UQueryEngine qe = ctxt.getQueryEngine();

	    ResultSet rs = qe.executeQuery(sql);

	    return rs;

	} catch (Exception e) {
	    throw new UDBAccessException("Error in loading Data Dictionary items!", e);
	}
    }
    
    public static ResultSet getDDItems(URequestContext ctxt, int dd_item_type_index, int parent_index, int ent_rid) 
	throws UDBAccessException {

	try {
	    String sql = "select dd_index, dd_ddi_type_index, dd_abbrv, dd_value, dd_parent_index, dd_ent_rid " +
		" from u_ent_ddict where dd_valid = 1";

	    sql = sql + " and dd_ddi_type_index = " + dd_item_type_index;

	    if(parent_index != 0) {
		sql = sql + " and dd_parent_index =" + parent_index;
	    }
            
            // Entity based u_ent_ddict lookups of dictionary values
            sql = sql +  " and dd_ent_rid = "+ ent_rid ;
            
	    sql = sql + " order by upper(dd_value) ";

	    UQueryEngine qe = ctxt.getQueryEngine();

	    ResultSet rs = qe.executeQuery(sql);

	    return rs;

	} catch (Exception e) {
	    throw new UDBAccessException("Error in loading Data Dictionary items!", e);
	}
    }

public static ResultSet getDDItemsAccrossEntity(URequestContext ctxt, int dd_item_type_index, int parent_index, int ent_rid) 
	throws UDBAccessException {

	try {
	    String sql = "select dd_index, dd_ddi_type_index, dd_abbrv, dd_value, dd_parent_index " +
		" from u_ent_ddict , u_entity  where dd_valid = 1";

	    sql = sql + " and dd_ddi_type_index = " + dd_item_type_index;

	    if(parent_index != 0) {
		sql = sql + " and dd_parent_index =" + parent_index;
	    }
            
            // Entity based u_ent_ddict lookups of dictionary values
            sql = sql +  " and dd_ent_rid = ent_rid " ;
            sql = sql +  " and ent_root_parent_rid = " +ctxt.getUserRootEntityRID()  ;
            
	    sql = sql + " order by upper(dd_value) ";

	    UQueryEngine qe = ctxt.getQueryEngine();

	    ResultSet rs = qe.executeQuery(sql);

	    return rs;

	} catch (Exception e) {
	    throw new UDBAccessException("Error in loading Data Dictionary items!", e);
	}
    }

    public static ResultSet getDDItems(URequestContext ctxt, int dd_item_type_index, int parent_index, String order_by_field) 
	throws UDBAccessException {

	try {
	    String sql = "select dd_index, dd_ddi_type_index, dd_abbrv, dd_value, dd_parent_index " +
		" from u_ddict where dd_valid = 1";

	    sql = sql + " and dd_ddi_type_index = " + dd_item_type_index;

	    if(parent_index != 0) {
		sql = sql + " and dd_parent_index =" + parent_index;
	    }
            
            if ("".equals(order_by_field))    
                sql = sql + " order by UPPER(dd_value)";
            else
                sql = sql + " order by " + order_by_field;
                
	    UQueryEngine qe = ctxt.getQueryEngine();

	    ResultSet rs = qe.executeQuery(sql);

	    return rs;

	} catch (Exception e) {
	    throw new UDBAccessException("Error in loading Data Dictionary items!", e);
	}
    }    
    
    public static ResultSet getAllDDItems(URequestContext ctxt, int dd_item_type_index, int parent_index) 
	throws UDBAccessException {

	try {
	    String sql = "select dd_index, dd_ddi_type_index, dd_abbrv, dd_value, dd_parent_index, dd_valid " +
		" from u_ddict where ";

	    sql = sql + " dd_ddi_type_index = " + dd_item_type_index;

	    // @@ Enable the following when doing entity based u_ddict lookups
	    // sql = sql + " and (ref_entity_rid = 0 or ref_entity_rid = " + ctxt.getEntityRID() + ")";

	    if(parent_index != 0) {
		sql = sql + " and dd_parent_index =" + parent_index;
	    }

	    sql = sql + " order by UPPER(dd_value)";

	    UQueryEngine qe = ctxt.getQueryEngine();

	    ResultSet rs = qe.executeQuery(sql);

	    return rs;

	} catch (Exception e) {
	    throw new UDBAccessException("Error in loading Data Dictionary items!", e);
	}
    }
    
        public static ResultSet getAllDDItems(URequestContext ctxt, int dd_item_type_index, int parent_index, int ent_rid) 
	throws UDBAccessException {

	try {
	    String sql = "select dd_index, dd_ddi_type_index, dd_abbrv, dd_value, dd_parent_index, dd_valid " +
		" from u_ent_ddict where ";

	    sql = sql + " dd_ddi_type_index = " + dd_item_type_index;

	    if(parent_index != 0) {
		sql = sql + " and dd_parent_index =" + parent_index;
	    }
            
            // Entity based u_ent_ddict lookups of dictionary values
            sql = sql +  " and dd_ent_rid = " + ent_rid + " and dd_valid = 1 ";
            
	    sql = sql + " order by UPPER(dd_value)";

	    UQueryEngine qe = ctxt.getQueryEngine();

	    ResultSet rs = qe.executeQuery(sql);

	    return rs;

	} catch (Exception e) {
	    throw new UDBAccessException("Error in loading Data Dictionary items!", e);
	}
    }
    
    public static ResultSet getNestedDDItems(URequestContext ctxt, int dd_item_type_index, int parent_index) 
	throws UDBAccessException {

	try {
	    String sql = "select dd_index, dd_ddi_type_index, dd_abbrv, dd_value, dd_parent_index, dd_valid " +
		" from u_ddict where ";

	    sql = sql + " dd_ddi_type_index = " + dd_item_type_index;
	   
            if(parent_index != 0) {
                sql = sql + " and dd_parent_index =" + parent_index;	 
            }
            
	    sql = sql + " order by UPPER(dd_value)";

	    UQueryEngine qe = ctxt.getQueryEngine();

	    ResultSet rs = qe.executeQuery(sql);

	    return rs;

	} catch (Exception e) {
	    throw new UDBAccessException("Error in loading Data Dictionary Nested DD items!", e);
	}
    }

       public static ResultSet getNestedDDItems(URequestContext ctxt, int dd_item_type_index, int parent_index, int ent_rid) 
	throws UDBAccessException {

	try {
	    String sql = "select dd_index, dd_ddi_type_index, dd_abbrv, dd_value, dd_parent_index, dd_valid " +
		" from u_ent_ddict where ";

	    sql = sql + " dd_ddi_type_index = " + dd_item_type_index;
	   
            if(parent_index != 0) {
                sql = sql + " and dd_parent_index =" + parent_index;	 
            }
            
            // Entity based u_ent_ddict lookups of dictionary values
            sql = sql + " and dd_ent_rid = " + ent_rid ;
            
	    sql = sql + " order by UPPER(dd_value)";

	    UQueryEngine qe = ctxt.getQueryEngine();

	    ResultSet rs = qe.executeQuery(sql);

	    return rs;

	} catch (Exception e) {
	    throw new UDBAccessException("Error in loading Data Dictionary Nested DD items!", e);
	}
    }

    public static ResultSet getModifiedDDItems(URequestContext ctxt, int ddTypeIndex, String fromDate) 
	throws UDBAccessException {

	try {
	    String sql = "select dd_index, dd_ddi_type_index, dd_abbrv, dd_value, dd_parent_index, dd_valid, " +         
	    " now() currentDateTime from u_ddict where ";
	    sql = sql + " dd_ddi_type_index = " + ddTypeIndex;
            if (fromDate !=null && !"".equals(fromDate)){
                 sql = sql + " and dd_mod_datetime > '" + fromDate +  "'";
            }  
	    
	    UQueryEngine qe = ctxt.getQueryEngine();

	    ResultSet rs = qe.silentExecuteQuery(sql);

	    return rs;

	} catch (Exception e) {
	    throw new UDBAccessException("Error in loading Data Dictionary items!", e);
	}
    }   
       
    public static ResultSet getNestedCityDetails(URequestContext ctxt, int dictionaryIndex) 
    throws UDBAccessException {
        try {
            UQueryEngine qe = ctxt.getQueryEngine();
            String sql = "SELECT cityDict.dd_index city_index, cityDict.dd_ddi_type_index city_type_index, cityDict.dd_value city_value, " +
                    "cityDict.dd_parent_index city_parent_index, " +
                    "stateDict.dd_index state_index, stateDict.dd_ddi_type_index state_type_index, stateDict.dd_value state_value, " +
                    "stateDict.dd_parent_index state_parent_index, " +
                    "countryDict.dd_index country_index, countryDict.dd_ddi_type_index country_type_index, countryDict.dd_value country_value, " +
                    "countryDict.dd_parent_index country_parent_index " +
                    "FROM u_ddict cityDict " +
                    "LEFT JOIN u_ddict stateDict ON (stateDict.dd_index = cityDict.dd_parent_index AND " +
                    "stateDict.dd_valid = 1 AND stateDict.dd_ddi_type_index = " + DataDictionaryManager.STATE + ")" + 
                    "LEFT JOIN u_ddict countryDict ON (countryDict.dd_index = stateDict.dd_parent_index AND countryDict.dd_valid = 1 " +
                    "AND countryDict.dd_ddi_type_index = " + DataDictionaryManager.COUNTRY + ")" +
                    "WHERE cityDict.dd_valid = 1 AND cityDict.dd_ddi_type_index = " + DataDictionaryManager.CITY + " AND cityDict.dd_index = " + dictionaryIndex;
            return qe.executeQuery(sql);
        } catch (Exception e) {
            throw new UDBAccessException("Exception fetching city values" + e.getMessage() , e);
        }
    }

    public static int getDDIndex(URequestContext ctxt, int ddItemTypeIndex, String ddValue) 
	throws UDBAccessException {

	try {
	    String sql = "select * from u_ddict where dd_ddi_type_index = " + ddItemTypeIndex + " and dd_value = '" + ddValue + "'";

	    ResultSet rs = ctxt.getQueryEngine().executeQuery(sql);

            return rs.next() ? rs.getInt("dd_index") : 0;
	} catch (Exception e) {
	    throw new UDBAccessException("Error in loading Data Dictionary item. " + e.getMessage(), e);
	}
    }
    
    public static ResultSet getDDByAbbr(URequestContext ctxt, int indexType, String abbr) 
    throws UDBAccessException {
        try {
            UQueryEngine qe = ctxt.getQueryEngine();
            String sql = "SELECT * FROM u_ddict WHERE dd_ddi_type_index = " + indexType + " AND dd_abbrv = '" + abbr + "' AND dd_valid = 1";
            return qe.executeQuery(sql);
        } catch (Exception e) {
            throw new UDBAccessException("Failed fetcing value, " + e.getMessage(), e);
        }
    }
}

