/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.admin;
import java.sql.ResultSet;
import cc.base.UDBAccessException;
import cc.base.UQueryEngine;
import cc.base.URequestContext;
import cc.util.UString;
/**
 *
 * @author Manu
 */
public class Coleg {
    public static ResultSet getColegDetails(URequestContext ctxt, int colegRid)
           throws UDBAccessException {
      String sql = "select coleg_rid,coleg_name,coleg_code,coleg_address,coleg_email,coleg_phone from coleg where row_invalidated = 0";
      if (colegRid > 0) {
         sql += " AND coleg_rid = " + colegRid;
      }
      
      UQueryEngine qe = ctxt.getQueryEngine();
      ResultSet rsColeg = qe.executeQuery(sql);
      return rsColeg;
      
      //return ctxt.getQueryEngine().executeQuery(sql);
   }
     public static void saveColegDetails(URequestContext ctxt, int colegRid, String colegName, String colegCode, String colegAddress, String colegEmail, String colegPhone)
           throws UDBAccessException {
      String sql = "";
      if (colegRid > 0) {
         sql = "UPDATE coleg "
                 + " SET coleg_name = '" + UString.escapeSpecialChars(colegName) + "'"
                 + " , coleg_code = '" + UString.escapeSpecialChars(colegCode) + "'"
                 + ", coleg_address = '" + UString.escapeSpecialChars(colegAddress)+ "'"
                 +", coleg_email = '" + UString.escapeSpecialChars(colegEmail) +"'"
                 +", coleg_phone = '"+UString.escapeSpecialChars(colegPhone) +"'"
                 + " WHERE coleg_rid = " + colegRid;
         ctxt.getQueryEngine().executeUpdate(sql);
      } else {
         sql = "INSERT INTO coleg(coleg_name,coleg_code,coleg_address,coleg_email, coleg_phone) "
                 + " VALUES('" + UString.escapeSpecialChars(colegName) + "','" + UString.escapeSpecialChars(colegCode) + "'"
                 + ",'" + colegAddress + "','" + colegEmail + "','" + UString.escapeSpecialChars(colegPhone)+"')";
         ctxt.getQueryEngine().executeInsert(sql);

      }
   }
}
