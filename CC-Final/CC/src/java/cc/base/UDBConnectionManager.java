package cc.base;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

final class UDBConnectionManager {

    private Logger logger = Logger.getLogger(UDBConnectionManager.class);
    
    private static Context initCtx = null;
    private static Context envCtx = null;
    private static DataSource ds = null;

    private static Object lock = new Object();

    private UDBConnectionManager() {
    }

    public static Connection getConnection()
    throws UDBAccessException {
        
        try {
            
            if(initCtx == null) {
		synchronized(lock) {
		    // Check again just in case some other thread already set initCtx
		    // while the current thread was waiting on the lock
		    if(initCtx == null) {
			initCtx = new InitialContext();
			envCtx = (Context) initCtx.lookup("java:comp/env");
		    }
		}
            }
            
            ds = (DataSource) envCtx.lookup("jdbc/AppDB");
            
            Connection con = ds.getConnection();
            
            return con;
            
        } catch (Exception e) {
            
            initCtx = null;
            envCtx = null;
            ds = null;
            
            throw new UDBAccessException("Failed to get database connection!", e);
        }
    }
    
    public static Connection getConnection(String resourceName) 
	throws UDBAccessException {

	try {

		Context initCtx1 = new InitialContext();
		Context envCtx1 = (Context) initCtx1.lookup("java:comp/env");

		ds = (DataSource) envCtx1.lookup(resourceName);

	    Connection con = ds.getConnection();

	    return con;

	} catch (Exception e) {

	    initCtx = null;
	    envCtx = null;
	    ds = null;

	    throw new UDBAccessException("Failed to get database connection to resource: " + resourceName, e);
	}
    }
    
    public static Connection getConnection(String db_type, String db_name, String db_user_name, String db_password,
            String db_driver_classname, String db_port, String db_machine_addr)
            throws UDBAccessException{
        
        Connection con = null;
        String conStr = null ;
        
        try{
            //load oracle driver
            Class.forName(db_driver_classname);

            if("oracle".equalsIgnoreCase(db_type))
                conStr = getOracleConStr(db_name, db_port, db_machine_addr) ;
            else if("mysql".equalsIgnoreCase(db_type))
                conStr = getMySqlConStr(db_name, db_port, db_machine_addr) ;
            else
                throw new Exception("Error in creating connection string") ;
            
            System.out.println("Connection Str: " + conStr) ;
            //get connection to database
            con = DriverManager.getConnection(conStr, db_user_name, db_password);
            
        }catch (Exception e){
            con = null ;
            throw new UDBAccessException("Error in creating connection to remote database", e) ;
        }finally{
            return con ;
        }
    }

    private static String getOracleConStr(String db_name, String db_port, String db_machine_addr) {
            return "jdbc:oracle:thin:@" + db_machine_addr + ":" + db_port + ":" + db_name ;
    }

    private static String getMySqlConStr(String db_name, String db_port, String db_machine_addr) {
        return "jdbc:mysql://" + db_machine_addr + ":" + db_port + "/" + db_name + "?autoReconnect=true";
    }
    
    public static Connection getConnection(String ipAddress, String db_name)
    throws UDBAccessException{
      //String mysql_url = "jdbc:mysql://localhost:3306/brit_db";
      
      String mysql_url = "jdbc:mysql://" + ipAddress + ":3306/" + db_name;
      
      Connection con = null;
        
        try {
          Class.forName("com.mysql.jdbc.Driver");
          
          //logger.info("MySQL JDBC Driver loaded");
          
          con = DriverManager.getConnection(mysql_url, "root", "ubqxyz");
          
          return con ;
          
          //logger.info("Succeeded in getting connection to Ubq DB");
          
        } catch (Exception e) {
          e.printStackTrace() ;
          throw new UDBAccessException(e.getMessage(), e) ;
          //logger.error("Exception: " + ex.getMessage());
        }
    }

}
