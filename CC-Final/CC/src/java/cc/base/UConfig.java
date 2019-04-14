package cc.base;

import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

public final class UConfig {

    private static Logger logger = Logger.getLogger(UConfig.class);

    private static HashMap cache = null;

    private UConfig() {
    }

    public static HashMap getSystemParameters(URequestContext ctxt, HashMap map, String sql)
	throws UBaseException {

	try {
	    ResultSet rs = ctxt.getQueryEngine().executeQuery(sql);

	    while(rs.next()) {
		map.put(rs.getString("param_code"), rs.getString("param_value"));
	    }

	    return map;
	} catch (Exception e) {
	    throw new UBaseException("Error in reading system parameters : " + e.getMessage(), e);
	}
    }    

    // @@ Need this function with PARAM_TYPE, PARAM_ENTITY_RID,.... as argument
    public static HashMap getSystemParameters(URequestContext ctxt)
	throws UBaseException {

	HashMap map = new HashMap();

	try {

	    // Load application wide settings
	    String sql = "select * from u_sys_param where param_entity_rid = 0";
	    getSystemParameters(ctxt, map, sql);

	    // Load entity specific settings
	    sql = "select * from u_sys_param where param_entity_rid = " + ctxt.getUserEntityRID();
	    getSystemParameters(ctxt, map, sql);

	    return map;
	} catch (Exception e) {
	    throw new UBaseException("Error in reading system parameters : " + e.getMessage(), e);
	}
    }

    private synchronized static boolean _existsInCache(String code) {

	if(cache == null)
	    return false;

	return cache.containsKey(code);
    }

    private synchronized static String _lookupCache(String code) {
	if(cache == null)
	    return null;

	return (String) cache.get(code);
    }

    private synchronized static void _addToCache(String code, String value) {
	if(cache == null) {
	    cache = new HashMap();
	}

	cache.put(code, value);
    }

    public synchronized static void flushCache() {
	cache = null;
    }

    /*
     * @deprecated Please use getParameterValue(URequestContext ctxt, String code, ...) methods
     */
    private static String getParameterValue(UQueryEngine qe, int entityRID, String code)
	throws UBaseException {
        
        entityRID = 0;

	if(code == null)
	    throw new UBaseException("No code specified for system parameter");

	// @@ A quick way to support entity specific system parameters, tag each code with the entity RID
	// @@ and record it in the cache.
	String ecode = code + "~" + entityRID;

	// Return value from cache if it exists
	if(_existsInCache(ecode)) {
	    return _lookupCache(ecode);
	}

	boolean qeCreated = false;

	try {

	    if(qe == null) {
		qe = new UQueryEngine();
		
		qeCreated = true;
	    }

	    // Check for Entity specific value
	    String sql = "select * from u_sys_param where param_code = '" + code + "' and param_entity_rid = " +
		entityRID;

	    ResultSet rs = qe.executeQuery(sql);

	    String val = null;

	    if(rs.next()) {
		val = rs.getString("param_value");
	    } else if(entityRID != 0) {
		// No entity specific definition found. Look for application wide definition.
		sql = "select * from u_sys_param where param_code = '" + code + "' and param_entity_rid = 0";

		rs = qe.executeQuery(sql);

		if(rs.next()) {
		    val = rs.getString("param_value");
		}
	    }

            val = val == null?null:val.trim();
	    // Put in cache, even if there is no value defined (so that we don't keep hitting the database)
	    _addToCache(ecode, val);

	    return val;
	} catch (Exception e) {
	    throw new UBaseException("Error in fetching system parameter : " + e.getMessage(), e);
	} finally {
	    if(qeCreated)
		qe.close();
	}
    }

    /*
     * @deprecated Please use getParameterValue(URequestContext ctxt, String code)
     */
    public static String getParameterValue(int entityRID, String code)
	throws UBaseException {

	String val = getParameterValue((UQueryEngine) null, entityRID, code);

	return val;
    }

    public static String getParameterValue(URequestContext ctxt, String code)
	throws UBaseException {

	return getParameterValue(ctxt.getQueryEngine(), 0, code);
    }

    /*
     * @deprecated Please use getParameterValue(URequestContext ctxt, String code, String defaultVal)
     */
    public static String getParameterValue(int entityRID, String code, String defaultVal)
	throws UBaseException {

	String val = getParameterValue(entityRID, code);

	if(val == null)
	    return defaultVal;

	return val;
    }

    public static String getParameterValue(URequestContext ctxt, String code, String defaultVal)
	throws UBaseException {
	
	return getParameterValue(ctxt.getUserEntityRID(), code, defaultVal);
    }

    /*
     * @deprecated Please use getParameterValue(URequestContext ctxt, String code, int defaultVal)
     */
    public static int getParameterValue(int entityRID, String code, int defaultVal)
	throws UBaseException {

	String val = getParameterValue(entityRID, code, defaultVal + "");

	try {
	    int rval = Integer.parseInt(val);

	    return rval;
	} catch (Exception e) {

	    // @@ Is this the right thing to do or should we just throw an exception and let
	    // @@ the higher level deal with it? Revisit.
	    logger.error("System parameter value error " + e.getMessage());

	    return defaultVal;
	}

    }

    public static int getParameterValue(URequestContext ctxt, String code, int defaultVal)
	throws UBaseException {

	String val = getParameterValue(ctxt, code);

	if(val == null)
	    return defaultVal;

	try {
	    int rval = Integer.parseInt(val);

	    return rval;
	} catch (Exception e) {

	    // @@ Is this the right thing to do or should we just throw an exception and let
	    // @@ the higher level deal with it? Revisit.
	    logger.error("System parameter value error " + e.getMessage());

	    return defaultVal;
	}
    }

    public static void setParameterValue(URequestContext ctxt, String code, String value)
	throws UBaseException {

	if(code == null)
	    throw new UBaseException("No code specified for system parameter");

	if(value == null)
	    value = "";

	try {
	    String sql = "update u_sys_param set " +
		" param_value = '" + value + 
		"' where param_code = '" + code + "' and param_entity_rid = " + ctxt.getUserEntityRID();

	    ctxt.getQueryEngine().executeUpdate(sql);

	} catch (Exception e) {
	    throw new UBaseException("Error in setting parameter value for '" + code + "', entity : " + ctxt.getUserEntityRID() 
				     + " " + e.getMessage(), e);
	}
    }

}

