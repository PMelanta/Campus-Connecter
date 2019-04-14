/*
 * Paging.java
 *
 * Created on December 9, 2008, 9:30 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cc.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import cc.base.UDBAccessException;
import cc.base.UDBUtils;
import cc.base.URequestContext;

/**
 *
 * @author Sampathkumar
 */
public class Paging {
    
    public static final String CALC_FOUND_ROWS = "";//SQL_CALC_FOUND_ROWS";
    /** Creates a new instance of Paging */
    public Paging() {
    }
    
    public static String getPagingString(URequestContext ctxt, String sql)
    throws UDBAccessException {
        
        int fromLimit = ctxt.getIntParameter("fromLimit");
        int limit = ctxt.getIntParameter("toLimit") ;
        
        if(fromLimit > -1 && limit > 0) {
            
            try {
                String dbProdName = (String)ctxt.getQueryEngine().getConnection().getMetaData().getDatabaseProductName();
                
                if("oracle".equalsIgnoreCase(dbProdName)) {
                    
                    sql = " select outerTable.* from ( select originalTable.*, rownum rn from " +
                            " ( "+ sql +" ) originalTable ) outerTable " +
                            " where rn between "+ fromLimit +" and "+ limit;
                    
                } else if("mysql".equalsIgnoreCase(dbProdName)) {
                    
                    sql += " limit " + fromLimit + ", " + limit;
                } else {
                    
                    sql += " limit " + fromLimit + ", " + limit;
                }
                
            } catch(Exception e) {
                //
            }
        }
        return sql;
    }   
    
    public static int getFoundRows(URequestContext ctxt) throws UDBAccessException, SQLException{
        int count = 0;
        ResultSet rs = ctxt.getQueryEngine().executeQuery("select FOUND_ROWS() as count from dual");
        if(rs.first()) {
            count = rs.getInt("count");
        }
        
        return count;
    }
    
    public static int getFoundRows(URequestContext ctxt, String sql)  throws UDBAccessException, SQLException{
        int count = 0;
        
        try {
            String dbProdName = (String)ctxt.getQueryEngine().getConnection().getMetaData().getDatabaseProductName();
            if("oracle".equalsIgnoreCase(dbProdName)) {
                String newSql = " select count(ROWNUM) rn from (" + sql + " ) a ";
                ResultSet rs = ctxt.getQueryEngine().executeQuery(newSql);
                if(rs != null && rs.first()) {
                    count = rs.getInt("rn");
                }
            } else if("mysql".equalsIgnoreCase(dbProdName)) {
                ResultSet rs = ctxt.getQueryEngine().executeQuery(sql);
                rs.first();
                rs.last();
                count = rs.getRow();
            }
            return count;
        } catch(Exception e) {
            return 0;
            //throw new UDBAccessException("Error in found_rows finction :"+ e.getMessage(), e);
        }

        
    }
}
