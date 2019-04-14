package cc.base;

import cc.util.UDate;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.text.*;

import org.apache.log4j.Logger;


public class URequestContext extends HttpServlet {
    
    static Logger logger = Logger.getLogger(UHttpServlet.class);
    
    HttpServletRequest request = null;
    HttpServletResponse response = null;
    
    HttpSession session = null;
    
    UQueryEngine qe = null;
    Connection con = null;
    Statement stmt = null;
    
    String dbResourceName = null ;
    ServletConfig servletConfig = null;
    
    public URequestContext() {
    }
    
    public URequestContext(HttpServletRequest req, HttpServletResponse resp) {
        
        request = req;
        response = resp;
        
        session = req.getSession();
        
    }
    
    public URequestContext(HttpServletRequest req, HttpServletResponse resp, String dbResource) {
        
        request = req;
        response = resp;
        
        session = req.getSession();
        
        dbResourceName = dbResource ;
        
    }
    
    public HttpSession getSession() {
        
        return request.getSession(false);
    }
    
    public HttpServletRequest getHttpRequest() {
        
        return request;
    }
    
    public HttpServletResponse getHttpResponse() {
        
        return response;
    }
    
    public UQueryEngine getQueryEngine()
    throws UDBAccessException {
        
        if(qe == null){
            if(dbResourceName == null)
                qe = new UQueryEngine();
            else
                qe = new UQueryEngine(dbResourceName) ;
        }
        
        return qe;
    }
    
    public UQueryEngine getQueryEngine(String resourceName)
    throws UDBAccessException {
        
        if(resourceName != null)
            return new UQueryEngine(resourceName);
        else
            return null ;
    }
    
    public Connection getDBConnection()
    throws UDBAccessException {
        
        if(con == null) {
            logger.debug("**************** Creating DB connection ****************");
            con = UDBConnectionManager.getConnection();
        }
        
        return con;
    }
    
    public Statement getDBStatement()
    throws UDBAccessException {
        
        if(stmt == null) {
            Connection c = getDBConnection();
            
            try {
                stmt = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            } catch (SQLException e) {
                throw new UDBAccessException("Failed to create statement!", e);
            }
        }
        
        return stmt;
    }
    
    public String getParameter(String paramName) {
        
	String pval = request.getParameter(paramName);

	if(pval == null) {
	    Object obj = getAttribute(paramName);
	    
	    pval = obj != null ? obj.toString() : null;
	}

        return pval;
    }
    
    public String getParameter(String paramName, String defaultValue) {

	String pval = getParameter(paramName);

	return pval == null ? defaultValue : pval;
    }
    
    public String getParameter(String paramName, int index) {
        
        String[]  fieldValueArr = getParameterValues(paramName);
        
        if (fieldValueArr != null)
            return fieldValueArr[index];
        else
            return null;
    }
    
    public String[] getParameterValues(String paramName) {
        
        String [] fieldValueArr = request.getParameterValues(paramName);
               
        if(fieldValueArr != null)
            return fieldValueArr;
        else {
             Object [] obj = getAttributeValues(paramName);
             return (String[]) obj;             
        }
    }
    
    public int[] getIntParameterValues(String paramName) {
        
        String[] values = getParameterValues(paramName);
        
        if(values == null)
            return null;
        
        int[] intValues = new int[values.length];
        
        for(int i = 0; i < values.length; i++) {
            
            if(!"".equals(values[i].trim())) {
            intValues[i] = Integer.parseInt(values[i].trim());
            } else {
                intValues[i] = 0;
            }
        }
        
        return intValues;
    }
    
    public float[] getFloatParameterValues(String paramName) {
        
        String[] values = getParameterValues(paramName);
        
        if(values == null)
            return null;
        
        float[] floatValues = new float[values.length];
        
        for(int i = 0; i < values.length; i++) {
            
            if(!"".equals(values[i].trim()))
                floatValues[i] = Float.parseFloat(values[i].trim());
            else
                floatValues[i] = 0;
        }
        
        return floatValues;
    }
    
    public double[] getDoubleParameterValues(String paramName) {
        String[] values = getParameterValues(paramName);
        
        if(values == null)
            return null;
        
        double[] doubleValues = new double[values.length];
        
        for(int i = 0; i < values.length; i++) {
            
            if(!"".equals(values[i].trim()))
                doubleValues[i] = Double.parseDouble(values[i].trim());
            else
                doubleValues[i] = 0;
        }
        
        return doubleValues;
    }
    
    public java.sql.Date[] getDateParameterValues(String paramName) {
        
        String[] values = getParameterValues(paramName);
        
        if(values == null)
            return null;
        
        java.sql.Date[] dateValues = new java.sql.Date[values.length];
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for(int i = 0; i < values.length; i++) {
            try {
                dateValues[i] = new java.sql.Date(sdf.parse(values[i]).getTime());
            } catch (ParseException e) {
                dateValues[i] = null;
            }
        }
        
        return dateValues;
    }
    
    public int getIntParameter(String paramName) {
        
        String paramValue = getParameter(paramName);
        
        if(paramValue != null && !paramValue.equals("")) {
            paramValue = paramValue.trim();
            
            return Integer.parseInt(paramValue);
        } else
            return 0;
    }
    
    public int getIntParameter(String paramName, int index) {
        
        String[]  fieldValueArr = getParameterValues(paramName);
        
        if (fieldValueArr != null)
            return Integer.parseInt(fieldValueArr[index].trim());
        else
            return 0;
    }
    
    public float getFloatParameter(String paramName) {
        
        String paramValue = getParameter(paramName);
        
        if(paramValue != null && !paramValue.equals("")) {
            paramValue = paramValue.trim();
            
            return Float.valueOf(paramValue).floatValue();
        } else
            return 0;
    }
    
    public float getFloatParameter(String paramName, int index) {
        
        String[]  fieldValueArr = getParameterValues(paramName);
        
        if (fieldValueArr != null)
            return Float.parseFloat(fieldValueArr[index].trim());
        else
            return 0;
    }
    
    public java.sql.Date getDateParameter(String paramName) {
        
        String paramValue = getParameter(paramName);

        if (paramValue== null)
            return null;
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        try {
            return new java.sql.Date(sdf.parse(paramValue).getTime());
        } catch (ParseException e) {
            return null;
        }
    }
    
    public java.sql.Date getDateParameter(String paramName, int index) {
        
        String[]  fieldValueArr = getParameterValues(paramName);
        
        if (fieldValueArr != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            try {
                return new java.sql.Date(sdf.parse(fieldValueArr[index].trim()).getTime());
            } catch (ParseException e) {
                return null;
            }
        } else
            return null;
    }

    // Assumes time is in 24 hour format    
    private java.sql.Time _parseTime(String timeStr) {

        if(timeStr == null)
            return null;

	if(timeStr == null || "".equals(timeStr))
	    return null;

	return UDate.parseTime(timeStr, "HH:mm");
    }

    // Assumes time is in 24 hour format
    public java.sql.Time getTimeParameter(String paramName) {
        
        String paramValue = getParameter(paramName);

	return _parseTime(paramValue);
    }

    // Assumes time is in 24 hour format    
    public java.sql.Time getTimeParameter(String paramName, int index) {
        
        String[]  fieldValueArr = getParameterValues(paramName);

        return _parseTime(fieldValueArr[index].trim());
    }
    
    public int getCheckboxParameter(String paramName) {
        
        String paramValue = getParameter(paramName);
        
        if(paramValue == null)
            return 0;
        else
            return 1;
    }
    
    public Object getAttribute(String attrName) {
        
        if(request == null)
            return null;
        
        return request.getAttribute(attrName);
    }
    
    public void setAttribute(String attrName, Object attrValue) {
        
        if(request == null)
            // Ignore
            return;
        
        request.setAttribute(attrName, attrValue);
    }
    
    public Object[] getAttributeValues(String attrName) {
        Object attr = request.getAttribute(attrName);
        
        if(attr != null && attr.getClass().isArray())
            return (Object[]) request.getAttribute(attrName);
        else
            return null;
    }
    
    public void setArrayAttribute(String attrName, Object attrValue, int index) {
        
        // See if we have already created this array attribute
        Object[] arrayValue = getAttributeValues(attrName);
        
        if(arrayValue == null) {
            // Not created. Let's create one with a default size of index + 50
            arrayValue = new Object[index + 50];
        }
        
        // Check if we need to extend the array
        if(index >= arrayValue.length) {
            // We will extend this by index + 50 again
            Object[] newArrayValue = new Object[index + 50];
            
            // Now copy values from old to new
            for(int i = 0; i < arrayValue.length; i++)
                newArrayValue[i] = arrayValue[i];
            
            arrayValue = newArrayValue;
        }
        
        arrayValue[index] = attrValue;
        
        request.setAttribute(attrName, arrayValue);
    }
    
    public void removeAttribute(String attrName) {
        
        request.removeAttribute(attrName);
    }
    
    public int getProductRID() {
        
        Integer i = (Integer) session.getAttribute("productRID");
        
        if(i == null)
            return -1;
        
        int productRID = i.intValue();
        
        return productRID;
    }
    
    public int getProjectRID() {
        
        Integer i = (Integer) session.getAttribute("projectRID");
        
        if(i == null)
            return 0;
        
        int projectRID = i.intValue();
        
        return projectRID;
    }
    
    public String getProjectName() {
        
        String projName = (String) session.getAttribute("projectName");
        return projName;
    }
    
    public int getUserRID() {
        
        Integer i = (Integer) session.getAttribute("userRID");
        
        if(i == null)
            return -1;
        
        int userRID = i.intValue();
        
        return userRID;
    }
    
    public String getUserID() {
        
        String userID = session.getAttribute("userID").toString();
        

        return userID;
    }
    
    public String getUserName() {
        
        return (String) session.getAttribute("userName");
    }
    
    public String getGeneratedSessionID() {
        return (String) session.getAttribute("generatedSessionID");
    }
    
    public int getUserEntityRID() {
        
//        int entRID = ((Integer) session.getAttribute("userEntityRID")).intValue();
//        
//        return entRID;
       return 0;
    }
    
    public String getUserEntityName() {
        
        return (String) session.getAttribute("userEntityName");
    }
    
    public String getUserEntityCode() {
        
        return (String) session.getAttribute("userEntityCode");
    }
    
    public String getProductCode() {
        
        return (String) session.getAttribute("productCode");
    }
    
    public int getUserUnitRID() {
        
        int unitRID = (session.getAttribute("unitRID") == null) ? 0 :
            ((Integer) session.getAttribute("unitRID")).intValue();
        
        return unitRID;
    }
    
    public String getUserUnitName() {
        
        return (String) session.getAttribute("unitName");
    }
    public int  getUserRootEntityRID(){
        
        int rootEntityRID = ((Integer) session.getAttribute("rootEntityRID")).intValue();
        
        return rootEntityRID;
    }
    public void close() {
        
        // Do all needed cleanups
        
        if(qe != null)
            qe.close();
        
        try {
            
            if(stmt != null)
                stmt.close();
            
            if(con != null && !con.isClosed()) {
                logger.debug("**************** Closing DB connection ****************");
                con.close();
            }
            
        } catch (SQLException e) {
            logger.error("Failed to close Request Context cleanly! " +
                    e.getMessage());
        }
    }
    
    public String getServletPath(){
        return request.getServletPath() ;
    }
    
    public String getContextPath(){
        return request.getContextPath() ;
    }
    
    public void setServletConfig(ServletConfig serConfig) {
        servletConfig = serConfig;
    }
    
    public ServletConfig getServletConfig() {
        return servletConfig;
    }
    
    public URequestContext copy() throws UBaseException {
        return new URequestContext(request, response);
    }
}
