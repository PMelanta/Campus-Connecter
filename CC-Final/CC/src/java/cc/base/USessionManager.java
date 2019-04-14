/*
 * USessionManager.java
 *
 */

package cc.base;
import java.sql.*;
import java.util.*;
import java.text.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import cc.util.UString;

/**
 *
 * @author suhas
 */
public class USessionManager {
    static Logger logger = Logger.getLogger(Class.class);
    
    /** Creates a new instance of USessionManager */
    public USessionManager() {
    }
    
    public static void persistSession(HttpSession session)
    throws ServletException{
        try{
            UQueryEngine qe = new UQueryEngine() ;
            try{
                
                qe.executeUpdate("delete from u_session_details where session_user_rid = " + session.getAttribute("userRID") +
                        " and session_remote_host = '" + session.getAttribute("remoteHost") + "'") ;
                
                String insertQuery = "insert into u_session_details " +
                        " (session_id, session_logged_in, session_product_rid, " +
                        " session_user_rid, session_user_name, session_user_id, session_remote_host)" +
                        " values (" +
                        "'" + session.getId() + "'," + 
                        (("true".equals(session.getAttribute("loggedIn")))?"1":"0") + "," +
                        session.getAttribute("productRID") + "," +
                        session.getAttribute("userRID") + "," +
                        "'" + UString.escapeSpecialChars(session.getAttribute("userName").toString()) + "'," +
                        "'" + session.getAttribute("userID") + "'," +
                        "'" + session.getAttribute("remoteHost") + "')" ;
                qe.executeInsert(insertQuery) ;
            }catch(Exception e){
                e.printStackTrace() ;
                throw new ServletException("Error in saving session details",e) ;
            } finally {
                qe.close() ;
            }
        } catch (Exception e){
            e.printStackTrace() ;
            throw new ServletException("Error in saving session details",e) ;
        }
        
    }
    
    public static void deleteSessionDetails(HttpSession session)
    throws UServletException{
        try{
            UQueryEngine qe = new UQueryEngine() ;
            try{
                qe.executeUpdate("delete from u_session_details where session_id = '" + session.getId() + "'") ;
            }catch(Exception e){
                e.printStackTrace() ;
                throw new UServletException("Error in deleting session details",e) ;
            } finally{
                qe.close() ;
            }
        } catch (Exception e){
            e.printStackTrace() ;
            throw new UServletException("Error in deleting session details",e) ;
        }
    }
    
    static String getGeneratedSessionID(UQueryEngine qe, int productRID) throws UDBAccessException {
        try {                        
            
            String sessionID = "";
            
            String sql = " UPDATE u_session_seq_generator SET ssg_seq_no = ssg_seq_no + 1 WHERE ssg_product_rid = " + productRID;
            qe.executeUpdate(sql);
            
            sql = "SELECT ssg_seq_no FROM u_session_seq_generator WHERE ssg_product_rid = " + productRID;
            
            ResultSet rs = qe.executeQuery(sql);
            
            if(rs != null && rs.first()) {
                sessionID = rs.getString("ssg_seq_no");
            }
            
            return sessionID;                                                                                               
            
        } catch (Exception ex) {
            throw new UDBAccessException(ex.getMessage() + "!! Failed to generate session id ", ex);
        }
    }
    
}
