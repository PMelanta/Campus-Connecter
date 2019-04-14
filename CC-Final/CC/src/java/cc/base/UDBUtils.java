package cc.base;

import java.sql.*;
import java.text.*;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

public final class UDBUtils {
    
    private static Logger logger = Logger.getLogger(UDBUtils.class);
    
    private UDBUtils() {
    }
    
    public static void applyAll(URequestContext ctxt, String collAttribute, String sp, String[] fixedArgsValue, String[] fixedArgsType)
    throws UDBAccessException {
        
        UQueryEngine qe = ctxt.getQueryEngine();
        
        String[] selectedElements = ctxt.getParameterValues(collAttribute);
        
        int userRID = ctxt.getUserRID();
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        
        // SP Args : Fixed args + RID, UserRID, and Date-Time
        
        int numArgs = (fixedArgsValue != null) ? fixedArgsValue.length + 3 : 3;
        
        String[] inParamValue = new String[numArgs];
        String[] inParamType = new String[numArgs];
        
        for(int i = 0; i < selectedElements.length; i++) {
            
            int j = 0;
            
            if(fixedArgsValue != null) {
                for(j = 0; j < fixedArgsValue.length; j++) {
                    inParamValue[j] = fixedArgsValue[j];
                    inParamType[j] = fixedArgsType[j];
                }
            }
            // added by sunil since i have added the location rid in the value of checkeck box along with inv rid.
            String[] invArray = selectedElements[i].split("~");
            
            inParamValue[j] = invArray[0];
            inParamType[j] = "uInt";
            
            inParamValue[j + 1] = "" + userRID;
            inParamType[j + 1] = "uInt";
            
            inParamValue[j + 2] = date.toString();
            inParamType[j + 2] = "uString";
            
            ResultSet rs = qe.executeSP(sp, inParamValue, inParamType);
        }
    }
    
    public static String convertDateToDB( String strDate) {
        //this function convert date to SQL dateformat yyyy/mm/dd
        java.sql.Date d = new java.sql.Date(0);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        try {
            return new java.sql.Date(sdf.parse(strDate).getTime()).toString();
        } catch (ParseException e) {
            return d.toString();
        }
    }
    public static String displayToDB(String dateStr) {
        try {
            String dbFormat = UConfig.getParameterValue(0, "DB_DATE_FORMAT","yyyy-MM-dd");
            String displayFormat = UConfig.getParameterValue(0, "DISPLAY_DATE_FORMAT", "dd/MM/yyyy");
            
            SimpleDateFormat fmt = new SimpleDateFormat(displayFormat);
            
            java.sql.Date dt = null;
            
            try {
                dt = new java.sql.Date(fmt.parse(dateStr).getTime());
            } catch(ParseException e) {
                return "<incorrect date format>";
            }
            
            //return dt.toString(); //--Gopi
            
            SimpleDateFormat dbFmt = new SimpleDateFormat(dbFormat);
            
            return dbFmt.format(dt);
        } catch(Exception e) 
{
            logger.error("Error while convering date to db format") ;
            return null;
        }
    }
    
    public static String MakeSQLDBFriendly(URequestContext ctxt, String sql)
    throws UDBAccessException {
        try {
            
            String retValue = "";
            
            
            
            return retValue;
            
        } catch(Exception e) {
            throw new UDBAccessException(e.getMessage(), e);
        }
    }
    
    public static String addLimitClause(URequestContext ctxt, String sql, int limit)
    throws UDBAccessException {
        return addLimitClause(ctxt, sql, 0, limit);
    }
    
    public static String addLimitClause(URequestContext ctxt, String sql, int startRow, int endRow)
    throws UDBAccessException {
        try {
            
            // This is a temporary function written with so much of hacks and loop holes, we need to change it. -- Netto
            // This function assumes below stated is true always
            //  1.  There should not be any extra spaces in clauses like 'group by', 'order by'
            //  2.  Clauses like 'group by', 'order by' and 'where' should not be used as a field alias name.
            //  3.  Its a single query that is getting passed into this function, which will have only one where clause.
            //      In simple words it should not have any inner queries.
            
            
            String retString = "";
            // Its a single query what we are getting here, and it is assumed that there is only one where clause in the query.
            // so we can directly search for the where clause and add required condition next to that
            
            // I am accepting the table name as a arguement, because it will avoid so much of string processing
                        
            // Find the string which we want to append
            // String dbProdName = (String) ctxt.getSession().getAttribute("dbProductName");  
             String dbProdName = (String)ctxt.getQueryEngine().getConnection().getMetaData().getDatabaseProductName(); 
             
          
            if("oracle".equalsIgnoreCase(dbProdName)) {
                // Find the where clause and replace with this
                
                // check whether there is where clause in the query
                if(((sql.toLowerCase()).indexOf("where")) > 0) {
                    retString = " where rownum >= " + startRow + " and rownum <= " + endRow + " and " ;  
                    sql = sql.replaceFirst("(?i)WHERE", retString);
                } else {
                    retString = " where rownum >= " + startRow + " and rownum <= " + endRow;  
                    // This query doesn't have where clause
                    // Search for group by 
                    if(sql.toLowerCase().indexOf("group by") > 0) {
                        // there is group by in the sql query, so place it before group by
                        // we can just replace the group by with retString and group by
                        sql = sql.replaceFirst("(?i)group by", retString + " group by ");
                    } else if(sql.toLowerCase().indexOf("order by") > 0) {
                        // there is a order by in the sql query, so place it before order by
                        // we can just replace the order by with retString and order by
                        sql = sql.toLowerCase().replaceFirst("(?i)order by", retString + " order by ");
                    } else {
                        // directly append it to the last of the query
                        sql += retString;
                    }                                                                                                            
                }
            }
            else if("mysql".equalsIgnoreCase(dbProdName)) {
                retString = " limit " + startRow + ", " + endRow;   // if the database is mysql
                sql += retString;
            }
            else {
                retString = " limit " + startRow + ", " + endRow;   // any other database
                sql += retString;
            }
                        
            // return the sql string
            return sql;
           
        } catch(Exception e) {
            throw new UDBAccessException(e.getMessage(), e);
        }
    }
    
    public static String limitReturnRows(URequestContext ctxt, int rows){
        String retString = "";
        
        String dbProdName = (String) ctxt.getSession().getAttribute("dbProductName");
        if("oracle".equalsIgnoreCase(dbProdName))
            retString = " and rownum =" + rows;
        else
            retString = " limit " + rows;   // if the database is mysql
        return retString;
    }
    
    public static String limitReturnRows(URequestContext ctxt, int startRow, int endRow){
        String retString = "";
        
        String dbProdName = (String) ctxt.getSession().getAttribute("dbProductName");
        if("oracle".equalsIgnoreCase(dbProdName))
            retString = " and rownum >= " + startRow + " and rownum <= " + endRow;
        else
            retString = " limit " + startRow + ", " + endRow;   // if the database is mysql
        return retString;
    }
    
    public static String getDBKeyWord(URequestContext ctxt) {
        try{
            String retValue = "";
            String dbProdName = (String) ctxt.getSession().getAttribute("dbProductName");
            if("oracle".equalsIgnoreCase(dbProdName))
                retValue = " from dual";
            else
                retValue = "";
            
            return retValue;
        } catch(Exception e) {
            logger.error("Error while getting the db keyword");
            return null;
        }
    }
    
    public static String getCurrentDateTime(URequestContext ctxt) {
        try {
            String retValue = "";
            String dbProdName = (String) ctxt.getSession().getAttribute("dbProductName");
            if("oracle".equalsIgnoreCase(dbProdName))
                retValue = " CURRENTDATETIME() ";
            else
                retValue = "now()";
            
            return retValue;
            
        } catch(Exception e) {
            logger.error("Error while loading the currentDateTime");
            return null;
        }
    }
    
}
