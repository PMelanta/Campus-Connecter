package cc.base;

import java.sql.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import java.util.Vector;

import org.apache.log4j.Logger;
import cc.util.UDate;

// import java.util.logging.*;

public class UQueryEngine {
    
    private static Logger logger = Logger.getLogger(UQueryEngine.class);
    
    private Connection con = null;
    
    private Vector stmtVector = new Vector();
    
    private Vector callableStmtVector = new Vector();
    
    public UQueryEngine() throws UDBAccessException {
        
        // BasicConfigurator.configure();
        
        try {
            
            con = UDBConnectionManager.getConnection();
            logger.debug("**************** Creating DB connection ****************");
            
        } catch (Exception e) {
            
            try {
                if(con != null) {
                    logger.debug("**************** Closing DB connection ****************");
                    con.close();
                    
                    con = null;
                }
            } catch (Exception ex) {
                // Nothing we can really do here
                ;
            }
            
            throw new UDBAccessException("Failed to create Query Engine!", e);
        }
    }
    
    public UQueryEngine(String resourceName) throws UDBAccessException {
        
        try {
            
            con = UDBConnectionManager.getConnection(resourceName);
            logger.debug("**************** Creating DB connection ****************");
            
        } catch (Exception e) {
            
            try {
                if(con != null) {
                    logger.debug("**************** Closing DB connection ****************");
                    con.close();
                    
                    con = null;
                }
            } catch (Exception ex) {
                // Nothing we can really do here
                ;
            }
            
            throw new UDBAccessException("Failed to create Query Engine!", e);
        }
    }
    
    public UQueryEngine(String db_type, String db_name, String db_user_name, String db_password,
            String db_driver_classname, String db_port, String db_machine_addr)
            throws UDBAccessException {
        
        // BasicConfigurator.configure();
        
        try {
            
            con = UDBConnectionManager.getConnection(db_type,db_name,db_user_name,db_password,db_driver_classname,db_port,db_machine_addr);
            logger.debug("**************** Creating DB connection ****************");
            
        } catch (Exception e) {
            
            try {
                if(con != null) {
                    logger.debug("**************** Closing DB connection ****************");
                    con.close();
                    
                    con = null;
                }
            } catch (Exception ex) {
                // Nothing we can really do here
                ;
            }
            
            throw new UDBAccessException("Failed to create Query Engine! " + e.getMessage(), e);
        }
    }
    
    public UQueryEngine(String ipAddress, String db_name) throws UDBAccessException {
        
        // BasicConfigurator.configure();
        
        try {
            
            con = UDBConnectionManager.getConnection(ipAddress, db_name);
            logger.debug("**************** Creating DB connection ****************");
            
        } catch (Exception e) {
            
            try {
                if(con != null) {
                    logger.debug("**************** Closing DB connection ****************");
                    con.close();
                    
                    con = null;
                }
            } catch (Exception ex) {
                // Nothing we can really do here
                ;
            }
            
            throw new UDBAccessException("Failed to create Query Engine! " + e.getMessage(), e);
        }
    }
    
    private Statement newStatement() throws SQLException {
        
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        
        stmtVector.addElement(stmt);
        
        return stmt;
    }
    
    private Statement newStatement(int optionConcurrency ) throws SQLException {
        
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,optionConcurrency);
        stmtVector.addElement(stmt);
        
        return stmt;
    }
    private Statement newStatement(int optionConcurrency,int optionHoldablility ) throws SQLException {
        
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,optionConcurrency,optionHoldablility);
        stmtVector.addElement(stmt);
        
        return stmt;
    }
    
    private CallableStatement newCallableStatement(String callStr) throws SQLException {
        
        CallableStatement cs = con.prepareCall(callStr,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        
        callableStmtVector.addElement(cs);
        
        return cs;
    }
    
    public void beginTransaction() throws SQLException {
        
        con.setAutoCommit(false);
    }
    
    public void commitTransaction() throws SQLException {
        
        con.commit();
        con.setAutoCommit(true);
    }
    
    public void rollbackTransaction() throws SQLException {
        
        con.rollback();
        con.setAutoCommit(true);
    }
    
    private ResultSet _executeQuery(String sql, boolean silent)
    throws UDBAccessException {
        
        try {
            
            if(!silent)
                logger.debug("SQL Query : " + sql);
            
            Statement stmt = newStatement();
            
            ResultSet rs = stmt.executeQuery(sql);
            
            return rs;
            
        } catch (Exception e) {
            throw new UDBAccessException("Failed to execute query! " + e.getMessage(), e);
        }
    }
    
    public ResultSet executeQuery(String sql)
    throws UDBAccessException {
        
        return _executeQuery(sql, false);
    }
    
    // The following function is being provided for daemon classes
    // just so that we don't fill up the log files with DEBUG statements
    
    public ResultSet silentExecuteQuery(String sql)
    throws UDBAccessException {
        
        return _executeQuery(sql, true);
    }
    
    // Returns :
    //   - the Primary Key if the insertion succeeds.
    
    public int executeInsert(String sql)
    throws UDBAccessException {
        Statement stmt = null;
        try {
            
            logger.debug("SQL Query : " + sql);
            
           stmt = newStatement(ResultSet.CONCUR_UPDATABLE);
            int autoIncID = 0;
            String tempValue= null;
            String firstField = "";
            if(stmt.getConnection().getMetaData().supportsGetGeneratedKeys()){
                
                stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                
                String dbProductName = stmt.getConnection().getMetaData().getDatabaseProductName();
                
                ResultSet rs = stmt.getGeneratedKeys();
                String tableID = rs.getMetaData().getTableName(1);
                if ("oracle".equalsIgnoreCase(dbProductName)) {
                    if (rs != null && rs.next() ) {
                        tempValue = rs.getString(1);
                    }
                    rs.getStatement().close() ;
                    rs.close() ;
                    
                    rs = null ;
                    
                    if ( tempValue != null) {
                        String sqlTemp = "select * from " + tableID +" where rowid ='" + tempValue + "'" ;
                        
                        rs = _executeQuery(sqlTemp,true);
                        
                        if(rs != null && rs.first())
                            firstField = rs.getString(1);
                        try {
                            autoIncID = Integer.parseInt(firstField);
                        } catch (NumberFormatException nfe) {
                            autoIncID =0;
                        }
                        rs.getStatement().close();
                        rs.close();
                    }
                } else {
                    if (rs != null && rs.next())
                        autoIncID = rs.getInt(1);
                }
            } else {
                //database does not support getGeneratedKeys() functionality
                stmt.executeUpdate(sql);
            }
            
            
            
            
            return autoIncID;
            
        } catch (Exception e) {
            throw new UDBAccessException("Failed to execute INSERT query! " + e.getMessage(), e);
        } finally {
            if(stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {}
            }
        }
    }
    
    public int executeUpdate(String sql)
    throws UDBAccessException {
        Statement stmt = null;
        try {
            
            stmt = newStatement(ResultSet.CONCUR_UPDATABLE);
            
            logger.debug("SQL Query : " + sql);
            
            stmt.executeUpdate(sql);
            
            int updateCount = stmt.getUpdateCount();
            
            return updateCount;
        } catch (Exception e) {
            throw new UDBAccessException("Failed to execute UPDATE query! " + e.getMessage(), e);
        } finally {
             if(stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {}
            }
        }
    }
    
    public ResultSet executeSP(String spName, String[] inputParamValue, String[] inputParaType)
    throws UDBAccessException {
        
        try {
            String params = "";
            String sParameter ="()";
            String callStmtStr =""; 
            int indexIncr =1;
            if (inputParamValue != null) {
                
                sParameter = "(" ;
                
                for (int i=0; i < inputParamValue.length ; i++) {
                    sParameter = sParameter + "?,";
                }
                sParameter = sParameter.substring(0, sParameter.length()-1);
                sParameter =  sParameter +")";
            }
            
            DatabaseMetaData dbMetaData = con.getMetaData();
            String dbName = dbMetaData.getDatabaseProductName() ;
            String obj_type = null ;
            
            if("oracle".equalsIgnoreCase(dbName)){
                ResultSet rs = executeQuery("select object_type from all_objects where upper(object_name) = " +
                        "upper('" + spName + "') and owner = '" + dbMetaData.getUserName() + "'") ;
                
                if(rs != null) {
                    rs.first() ;
                    obj_type = rs.getString(1) ;
                }
                
            }
            
            if("function".equalsIgnoreCase(obj_type)){
                callStmtStr = "{? = call " + spName + sParameter + "}" ;
                indexIncr = 2 ;
            } else
                callStmtStr = "{call " + spName + sParameter + "}" ;
            
            CallableStatement cs = newCallableStatement(callStmtStr);
            
            // CallableStatement cs = newCallableStatement("{call " + spName + sParameter + "}");
            
            if (inputParamValue != null) {
                for (int i=0; i < inputParamValue.length; i++) {
                    
                    if (inputParaType[i] == "uInt")
                        cs.setInt(i+indexIncr, Integer.parseInt(inputParamValue[i].trim()));
                    if (inputParaType[i] == "uString")
                        cs.setString(i+indexIncr, inputParamValue[i]);
                    if (inputParaType[i] == "uDouble")
                        cs.setDouble(i+indexIncr, Double.parseDouble(inputParamValue[i].trim()));
                    params = params + ("".equals(params) ? inputParamValue[i] : (", " + inputParamValue[i]));
                }
            }
            
            logger.debug("About to execute '" +   spName + "(" + params + ")'");
            
            cs.execute();
            
            logger.debug("SP executed");
            
            ResultSet rs = null ;
            
            if("oracle".equalsIgnoreCase(dbName)){
                if("function".equalsIgnoreCase(obj_type))
                    rs = (ResultSet) cs.getObject(1);
            } else
                rs = cs.getResultSet() ;                        
                                    
            return rs;
            
        } catch(Exception e) {
            throw new UDBAccessException(e.getMessage(), e);
        }
    }
    
    private Object getFieldValue(URequestContext ctxt, String fieldName,String fieldType) {
        Object fieldValue = ctxt.getAttribute(fieldName) ;
        if (fieldValue != null)
            return  fieldValue;
        else {
            fieldValue = ctxt.getParameter(fieldName);
            
            if("String".equals(fieldType))
                return fieldValue;
            
            if("int".equalsIgnoreCase(fieldType))
                return new Integer(Integer.parseInt(((String) fieldValue).trim()));
            
            if("double".equalsIgnoreCase(fieldType))
                return new Double(Double.parseDouble(((String) fieldValue).trim()));
            
            return fieldValue;
        }
    }
    
    private Object getFieldValue(URequestContext ctxt, String fieldName, String fieldType, int currentPosition) throws UBaseException {
        Object fieldValue = null;
        Object[]  fieldValueArr = null;
        //fieldValueArr = ctxt.getParameterValues(fieldName);
        
        try {
            //if (fieldValueArr != null) {
            fieldValue = ctxt.getParameter(fieldName,currentPosition);
            if (fieldValue != null) {
                //fieldValue = fieldValueArr[currentPosition];
                if("String".equals(fieldType))
                    return fieldValue;
                
                if("int".equalsIgnoreCase(fieldType))
                    return new Integer(Integer.parseInt(((String) fieldValue).trim()));
                
                if("double".equalsIgnoreCase(fieldType))
                    return new Double(Double.parseDouble(((String) fieldValue).trim()));
                
                return fieldValue;
            }
        }  catch (ArrayIndexOutOfBoundsException e ) {
            // do nothing
        }
        
        fieldValueArr = (Object[]) ctxt.getAttributeValues(fieldName);
        
        if (fieldValueArr != null) {
            fieldValue = fieldValueArr[currentPosition];
            return fieldValue;
        }
        
        fieldValue = getFieldValue(ctxt, fieldName,fieldType);
        if (fieldValue != null)
            return fieldValue;
        logger.debug("Failed to find value for -> fieldName: "+ fieldName + " fieldType: "+ fieldType + " index: " +  currentPosition);
        throw new UBaseException("Field not defined : " + fieldName, null);
    }
    
    private String getParameterList(String[] inputParamArr ) {
        
        String sParameter="()";
        if (inputParamArr == null)
            return sParameter;
        
        sParameter = "(" ;
        
        for (int i=0; i < inputParamArr.length ; i++) {
            sParameter = sParameter + "?,";
        }
        sParameter = sParameter.substring(0, sParameter.length()-1);
        sParameter =  sParameter +")";
        
        return sParameter;
    }
    
    private CallableStatement setValuetoCallableStatement(CallableStatement cs, String fieldName, Object fieldValue, int csIndex)
    throws SQLException{
        
        //logger.debug("setValuetoCallableStatement: fieldType=" + fieldName + ", fieldValue=" + fieldValue + ", index=" + csIndex);
        if (("String").compareToIgnoreCase(fieldName) == 0 )
            cs.setString(csIndex, (String) fieldValue);
        if (("Double").compareToIgnoreCase(fieldName) == 0)
            cs.setDouble(csIndex, ((Double) fieldValue).doubleValue());
        if (("Int").compareToIgnoreCase(fieldName) == 0)
            cs.setInt(csIndex, ((Integer) fieldValue).intValue());
        if (("Date").compareToIgnoreCase(fieldName) == 0) {
            if (fieldValue != null)
                cs.setString(csIndex, UDBUtils.displayToDB((String)fieldValue));
            else
                cs.setString(csIndex, null);
        }
        if("Float".equalsIgnoreCase(fieldName)) {
            cs.setFloat(csIndex, (Float)fieldValue);
        }
        if("Timestamp".equalsIgnoreCase(fieldName)) {
            try {
                String format = "yyyy-MM-dd";
                
                fieldValue = "2010-09-1 1:2:2";
                
                if(((String)fieldValue).matches("[\\d]{4}-[\\d]{1,2}-[\\d]{1,2}\\s[\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}"))
                    format = "yyyy-MM-dd HH:mm:ss";
                
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                java.util.Date date = simpleDateFormat.parse((String)fieldValue);
                cs.setTimestamp(csIndex, new Timestamp(date.getTime()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cs;
    }
    
    
    public ResultSet executeSP(URequestContext ctxt, String spName, String[] inputParamValue)
    throws UDBAccessException {
        
        return _executeSP(ctxt, spName, inputParamValue, -1);
        
    }
    
    public ResultSet executeSP(URequestContext ctxt, String spName, String[] inputParamValue, int currentPosition)
    throws UDBAccessException {
        
        return _executeSP(ctxt, spName, inputParamValue, currentPosition);
        
    }
    
    private ResultSet _executeSP(URequestContext ctxt, String spName, String[] inputParamValue, int currentPosition )
    throws UDBAccessException {
        try {
            String sParameter ="";
            String params = "";
            String callStmtStr = null ;
            
            int indexIncr = 1 ;
            
            sParameter = getParameterList(inputParamValue);
            
            String dbName = null ;
            
            DatabaseMetaData dbMetaData = con.getMetaData();
            dbName = dbMetaData.getDatabaseProductName() ;
            String obj_type = null ;
            
            if("oracle".equalsIgnoreCase(dbName)){
                ResultSet rs = executeQuery("select object_type from all_objects where upper(object_name) = " +
                        "upper('" + spName + "') and owner = '" + dbMetaData.getUserName() + "'") ;
                
                if(rs != null) {
                    rs.first() ;
                    obj_type = rs.getString(1) ;
                }
                
            }
            
            if("function".equalsIgnoreCase(obj_type)){
                callStmtStr = "{? = call " + spName + sParameter + "}" ;
                indexIncr = 2 ;
            } else
                callStmtStr = "{call " + spName + sParameter + "}" ;
            
            CallableStatement cs = newCallableStatement(callStmtStr);
            

            
            
            if (inputParamValue != null ) {
                for (int i = 0; i < inputParamValue.length; i++) {
                    
                    String fieldName[] = inputParamValue[i].split(":");
                    Object fieldValue = null;
                    String fieldType = fieldName.length == 1 ? "String" : fieldName[1];
                    String fieldIndex = Integer.toString(currentPosition) == "0" ? " " : Integer.toString(currentPosition);
                    
                    if (currentPosition == -1){
                        fieldValue = getFieldValue(ctxt, fieldName[0], fieldType);
                    } else{
                        fieldValue = getFieldValue(ctxt, fieldName[0], fieldType, currentPosition);
                    }
                    
                    if  (fieldName.length == 1){
                        //cs.setString(i+1,(String) fieldValue);
                        cs = setValuetoCallableStatement(cs, "String", fieldValue, i + indexIncr);
                    } else
                        cs = setValuetoCallableStatement(cs, fieldName[1], fieldValue, i + indexIncr);
                    
                    params = params + ("".equals(params) ? fieldValue : (", " + fieldValue));
                }
            }
            
            logger.debug("Executing SP:" + spName + "(" + params + ")");
            
            cs.execute();
            
            logger.debug("SP executed");
            
            ResultSet rs = null ;
            
            if("oracle".equalsIgnoreCase(dbName)){
                if("function".equalsIgnoreCase(obj_type))
                    rs = (ResultSet) cs.getObject(1);
            } else
                rs = cs.getResultSet() ;
            
            return rs;
        }   catch(Exception e) {
            e.printStackTrace() ;
            throw new UDBAccessException(e.getMessage(), e);
        }
    }
    
    public Connection getConnection() throws UBaseException {
        
        if(con == null)
            throw new UBaseException("No open connection");
        
        return con;
    }
    
    public void close() {
        
        try {
            
            for(int i = 0; i < stmtVector.size(); i++) {
                
                Statement stmt = (Statement) stmtVector.elementAt(i);
                stmt.close();
            }
        } catch (SQLException e) {
            logger.error("Failed to close Statement in Query Engine!");
            // Don't throw any exception. Nothing we can do about this.
        }
        
        try {
            for(int i = 0; i < callableStmtVector.size(); i++) {
                
                CallableStatement cs = (CallableStatement) callableStmtVector.elementAt(i);
                
                cs.close();
            }
        } catch (SQLException e) {
            logger.error("Failed to close Statement in Query Engine!");
            // Don't throw any exception. Nothing we can do about this.
        }
        
        try {
            if(con != null && !con.isClosed()) {
                logger.debug("**************** Closing DB connection ****************");
                con.close();
            }
            
        } catch (SQLException e) {
            logger.error("Failed to close Connection in Query Engine!");
            // Don't throw any exception. Nothing we can do about this.
        }
    }
    
    
    
    public void createSequenceNumber(String sequenceName)throws UDBAccessException {
        try{
            String sql = "CREATE SEQUENCE "+sequenceName + " MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE " ; 
            logger.debug("SQL Query : " + sql);
            Statement stmt = newStatement();
            stmt.executeUpdate(sql);
        } catch (Exception e){
            throw new UDBAccessException("Failed to execute query! " + e.getMessage(), e);
        }
        
    }
    public void createTrigger(String tblName, String columnName,String sequenceNumName) throws UDBAccessException {
     try{
         String triggerName = tblName+" "+columnName+" TRG";
         triggerName = triggerName.replaceAll(" ","_");
         String sql= "CREATE OR REPLACE TRIGGER " + triggerName + " BEFORE INSERT OR UPDATE ON " + tblName  +
                      " FOR EACH ROW " +
                      " DECLARE " +
                      " v_newval NUMBER(12) := 0; " +
                      " v_incval NUMBER(12) := 0; " +
                      " BEGIN " +
                      " IF INSERTING AND :new."+columnName + " IS NULL THEN " +
                      " SELECT " +sequenceNumName+".NEXTVAL INTO v_newval FROM DUAL; " +
                      " IF v_newval = 1 THEN  " +
                      " SELECT NVL(max("+columnName+"),0) INTO v_newval FROM "+ tblName + " ; "  +
                      " v_newval := v_newval + 1; " +
                      " LOOP " +
                      " EXIT WHEN v_incval>=v_newval; " +
                      " SELECT "+sequenceNumName+".nextval INTO v_incval FROM dual; " +
                      " END LOOP; " +
                      " END IF; " +
                      " :new."+columnName+" := v_newval; " +
                      " END IF; " +
                      " END; " ;
         logger.debug("SQL Query : " + sql);
         Statement stmt = newStatement();
         stmt.executeUpdate(sql);
     } catch(Exception e){
         throw new UDBAccessException("Failed to execute query! " + e.getMessage(), e);
     }
    }
    
}
