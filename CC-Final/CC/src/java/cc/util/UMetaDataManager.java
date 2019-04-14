/*
 * UMetaDataManager.java
 *
 * Created on November 11, 2010, 2:26 PM
 */

package cc.util;

import java.sql.ResultSet;
import cc.base.UDBAccessException;
import cc.base.URequestContext;


/**
 *
 * @author suhas
 * @version
 */
public class UMetaDataManager {
    
   
    public static String getMetaData(URequestContext ctxt, String objectType) 
    throws UDBAccessException  {
        try {
             
             String sql = "select * from u_metadata  where MD_OBJECT_TYPE = '" + objectType + "' and MD_ENTITY_RID = "+ ctxt.getUserEntityRID() ;
             ResultSet rsMetaData = ctxt.getQueryEngine().executeQuery(sql);
             if(rsMetaData != null && rsMetaData.first()) {
                return  rsMetaData.getString("MD_IDENTIFIER");
             } else {
                return null;
             }
         
        } catch(Exception e){
            throw new UDBAccessException("" + e.getMessage() , e);
        }
    }
    
    
}
