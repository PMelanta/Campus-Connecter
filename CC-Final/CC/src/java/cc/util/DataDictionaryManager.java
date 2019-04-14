/*
 * DataDictionaryManager.java
 *

 */

package cc.util;
import cc.base.URequestContext;
import cc.base.UQueryEngine;
import cc.base.UDBAccessException;
import cc.base.UServletException;
import cc.base.UDBUtils;
import java.sql.*;
import java.util.*;
import java.text.*;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

/**
 *
 * @author suhas
 */
public class DataDictionaryManager {
    static Logger logger = Logger.getLogger(DataDictionaryManager.class);
    
    public static final int COUNTRY = 176 ;
    public static final int STATE = 177 ;
    public static final int CITY = 179 ;
    public static final int GENDER = 4 ;
    public static final int PAYMENT_MODE = 5 ;
    public static final int REGION = 100 ;
    public static final int BRAND = 101 ;
    public static final int SUB_BRAND = 102 ;
    public static final int PACK = 103 ;
    public static final int DEFECT_CLASS = 104 ;
    public static final int CUSTOMER_TYPE = 110 ;
    public static final int SUPPLIER_TYPE = 111 ;
    public static final int STORE_TYPE = 112 ;
    public static final int TAX_CODE = 113 ;
    public static final int GOODS_TYPE = 114 ;
    public static final int PACK_CODE = 115 ;
    public static final int PRODUCT_TYPE = 116 ;
    public static final int TAX_TYPE = 117 ;
    public static final int BILL_TYPE = 118 ;
    public static final int DISCOUNT_REASON = 119 ;
    public static final int UOM_TYPE = 120 ;
    public static final int SKU_GROUP = 121 ;
    public static final int STOCK_TYPE = 122 ;
    public static final int TRANSACTION_TYPE = 123 ;
    public static final int HOLD_STOCK_REASON = 124 ;
    public static final int RELEASE_STOCK_REASON = 125 ;
    public static final int SALES_RETURN_REASON = 126 ;
    public static final int CUSTOMER_CHANNEL = 127 ;
    public static final int CUSTOMER_SUB_CHANNEL = 128 ;
    public static final int STAFF_CATEGORY = 129 ;
    public static final int ACCOUNT_TYPE = 130 ;
    public static final int ACCOUNT_GROUP_MASTER = 302;
    public static final int ACOUNT_TRANS_TYPE = 131 ;
    public static final int ACCOUNT_OPPERATION_T = 132 ;
    public static final int DATA_TYPE = 133 ;
    public static final int DESIGNATION =134; //DESIGNATION
    public static final int SPECIALITY = 200 ;
    public static final int FBC = 562 ;
    public static final int LEGAL_REGULATORY = 561 ;
    public static final int PROD_DEFECT_CATEGORY = 551 ;
    public static final int PROD_QUALITY_CAT = 552 ;
    public static final int PROD_PACK_DEF = 553 ;
    public static final int SS_RETAILER = 555 ;
    public static final int SS_DISTRIBUTOR = 556 ;
    public static final int ROUTE_TYPE = 185 ;
    public static final int STRENGTH_UOM = 186 ;
    public static final int UOM = 143 ;
    public static final int DIAGNOSIS = 167 ;
    public static final int PRESENTATION = 197 ;
    public static final int BLOOD_GROUP = 101 ;
    public static final int MARITAL_STATUS = 150 ;
    public static final int ITEM_TYPE = 140;
    public static final int SKU_GROUP3 = 145;
    public static final int STAFF_SUB_CATEGORY = 603 ;
    public static final int VISIT_REASON = 192;
    public static final int DISCOUNT_TYPE = 611;
    public static final int TRANSPORTATION_MODE = 208 ;
    public static final int DESIGNATION_TYPE = 20;
    
    public static final int LENS_MATERIAL = 633;
    public static final int LENS_BRAND = 638;
    public static final int LENS_VISION = 634;
    public static final int LENS_COLOR = 635;
    public static final int LENS_POWER = 637;
    public static final int LENS_COATING = 636;
    
    public static final int FRAMES_GENERAL = 639;
    public static final int FRAMES_CATEGORY = 640;
    public static final int FRAMES_MAKE = 641;
    public static final int FRAMES_SUB_CATEGORY = 642;
    public static final int FRAMES_MODEL_NAME = 643;
    public static final int FRAMES_COLOR = 644;
    public static final int QC_DEFECT_TYPE = 651;
    
    public static final int ASSET_CATEGORY = 2031;
    
    /*unit category for unit master*/
    public static final int UNIT_CATEGORY = 684;
    
    public static final int CL_LENSE_TYPE = 655;
    
    
    /* HRMS Releted */
    public static final int POSITION = 502;
    public static final int POSITION_CATEGORY = 501;
    public static final int GRADE =157;
    public static final int REQUISITION_LOCATION =504; //hrms
    public static final int OTH_REF_SOURCE =505; //hrms
    
    public static final String DD_ABBREVIATION = "dd_abbrv";
    public static final String DD_VALUE = "dd_value";
    public static final String DD_INDEX = "dd_index";
    public static final String DD_PARENT_INDEX = "dd_parent_index";
    
    public static final int EYE_CAMP_EMPLOYMENT = 663;
    public static final int EYE_CAMP_INFORMATION = 664;
    public static final int EYE_CAMP_REFERRAL = 665;
    public static final int PATIENT_REFERRAL = 180;
    public static final int STAFF_SPECIALITY= 604;
    
    public static final int YEAR = 2006;
    
    public static String REWARD_FREEQUENCY = "";
    
    public static String REWARD_SCOPE = "";
    public static final int PROJECT_TYPE = 680;
    public static final int PROJECT_AREA_UNIT = 681;
    public static final int DD_ATTACHMENT_CATEGORY = 303;
    
    public static final int EYE_CAMP_SPNSR_GRP = 2010;
    /** Creates a new instance of DataDictionaryManager */
    public DataDictionaryManager() {
    }
    
    public static void insertUpdateRow(URequestContext ctxt, boolean isEntitySpecific)
    throws UServletException {
        logger.debug("starting DataDictionaryManager insertUpdateRows...") ;
        String sql = "";
        String[] inputParamValues = new String[1] ;
        String[] inputParamTypes = new String[1] ;
        boolean isModify = false ;
        logger.debug(ctxt.getParameter("ddict_item_name")) ;
        logger.debug(ctxt.getParameter("targetRid")) ;
        inputParamValues[0] = ctxt.getParameter("ddict_item_name") ;
        inputParamTypes[0] = "uString" ;
        int item_index = 0;
        int targetIndex = 0;
        
        if(ctxt.getParameter("targetRid").equals("0"))
            item_index = 0 ;
        else{
            item_index = Integer.valueOf(ctxt.getParameter("targetRid")).intValue() ;
            targetIndex = item_index; 
            isModify = true ;
        }
        
        
        //  if(ctxt.getParameter("ddict_item_name"))
        String item_code = null ;
        item_code = ctxt.getParameter("ddict_item_name");
        
        try {
            //logger.debug("getting next dd index for: " + inputParamValues[0]) ;
            UQueryEngine qe = ctxt.getQueryEngine();
            ResultSet rs ;
            ResultSet rs1 = null ;
            if(!isModify){
                inputParamValues[0] = ctxt.getParameter("ddict_item_name") ;
                inputParamTypes[0] = "uString" ;
                
                // ctxt.getParameter("ddict_item_name") is ddiTypeIndex need to change in all places
                sql = " select * from u_ddict_item_type where ddi_type_index = " + ctxt.getParameter("ddict_item_name") ;
                rs = qe.executeQuery(sql) ;
                // rs = qe.executeSP("get_next_dd_index",inputParamValues,inputParamTypes) ;
                if (rs != null && rs.next()) {
                    item_index = _getNewDdIndex(ctxt, rs.getInt("ddi_type_from_range"), rs.getInt("ddi_type_to_range"), isEntitySpecific) ;
                } else {
                    logger.debug("u_ddict_item_type table does not consists "+ ctxt.getParameter("ddict_item_name") );
                    throw new Exception("Type index entry is not there ") ;
                }
            }
            
            //get type_index of the data dictionary item
            
            //THIS CODE SEEMS TO BE REDUNDANT. --GOPI
            /*
            rs = qe.executeSP("u_get_dd_item_code", inputParamValues,inputParamTypes);
             
            if(rs.next()){
                item_code = rs.getString("ddi_type_index") ;
            }
            else{
                //flag error here
            }
            rs.close() ;
             */
            
            
            logger.debug("done..." + item_index) ;
        } catch (Exception e){
            logger.debug("unable to get next type_index");
            throw new UServletException("Unable to get next type_index",e) ;
        }
        String parentList = "";
        String code = ctxt.getParameter("code").replaceAll("'","''");
        String descBox = ctxt.getParameter("descBox").replaceAll("'","''");
        if(ctxt.getParameter("parentList") != null)
            parentList = ctxt.getParameter("parentList") ;
        else
            parentList = "0" ;
        
        int isActive = ctxt.getIntParameter("isActive");
        
        
        
        try {
            logger.debug("inserting data dictionary item") ;
            
            UQueryEngine qe = ctxt.getQueryEngine();
            
            if (isModify && isEntitySpecific){
                sql = "update u_ent_ddict set dd_abbrv = '"+ code +"',dd_value ='" + descBox + "',dd_parent_index = "+ parentList +
                        ",dd_valid = " + isActive + " where dd_index =" + item_index ;
                qe.executeUpdate(sql);
                
            } else if(isModify){
                sql = "update u_ddict set dd_abbrv = '"+ code +"',dd_value ='" + descBox + "',dd_parent_index = "+ parentList +
                        ",dd_valid = " + isActive + " where dd_index =" + item_index ;
                qe.executeUpdate(sql);
                
            } else if (isEntitySpecific) {
                sql ="insert into u_ent_ddict (dd_index,dd_ddi_type_index,dd_abbrv,dd_value,dd_parent_index,dd_valid, dd_ent_rid)"+
                        "values("+ item_index +","+ item_code +",'" + code +"','"+ descBox + "'," + parentList +"," + isActive + ","+ctxt.getUserEntityRID()+")";
                qe.executeInsert(sql);
            } else{
                sql ="insert into u_ddict (dd_index,dd_ddi_type_index,dd_abbrv,dd_value,dd_parent_index,dd_valid)"+
                        "values("+ item_index +","+ item_code +",'" + code +"','"+ descBox + "'," + parentList +"," + isActive + ")";
                qe.executeInsert(sql);
                
            }
           String visitReasonExt = ctxt.getParameter("visitReasonExt");
           
           if("yes".equals(visitReasonExt)) {
               if(targetIndex == 0) {
                   _insertVisitReasonEntry(ctxt, item_index, false);
               } else {
                   _insertVisitReasonEntry(ctxt, item_index, true);
               }
           }
        } catch (Exception e){
            logger.debug("Error inserting data dictionary item...") ;
            throw new UServletException("Error inserting data dictionary item",e) ;
        }
    }
    
    
    public static int checkDuplicateEntry(URequestContext ctxt, String ddi_type_index,
            String item_code, String description, String trgtRID, boolean isEntitySpecific)
            throws Exception {
        
        int status = 0;
        UQueryEngine qe = ctxt.getQueryEngine();
        ResultSet rs = null;
        String sql = "";
        
        ddi_type_index = ctxt.getParameter("ddict_item_name");
        item_code = ctxt.getParameter("code").replaceAll("'","''");
        description = ctxt.getParameter("descBox").replaceAll("'","''");
        trgtRID = ctxt.getParameter("targetRid");
        String parent_ddi_index = "";
        if(ctxt.getParameter("parentList") == null)
            parent_ddi_index = "0";
        else
            parent_ddi_index = ctxt.getParameter("parentList");
        
        
        sql = "select 1 from u_ddict where " +
                "dd_ddi_type_index = " + ddi_type_index + " and (dd_value ='" + description +
                "' or dd_abbrv ='" + item_code + "' ) and  dd_index != "+ trgtRID + " and dd_parent_index = " + parent_ddi_index +
                UDBUtils.limitReturnRows(ctxt, 1)  ;
        rs = qe.executeQuery(sql);
        
        if(rs != null && rs.next()) {
            return status = 1;
        } else
            return status = 0;
        
    }
    
    public static ResultSet getNDDItems(URequestContext ctxt)
    throws UDBAccessException ,SQLException{
        
        UQueryEngine qe = ctxt.getQueryEngine();
        ResultSet rs = null;
        String sql = "";
        
        String item_index = ctxt.getParameter("ddiTypeIndex");
        String parent_ddi_index = ctxt.getParameter("parentDdiTypeIndex");
        
        sql = "select dd_index ,dd_value from u_ddict where dd_ddi_type_index = " + parent_ddi_index +
                " and dd_valid = 1 order by lower(dd_value)";
        
        rs = qe.executeQuery(sql);
        return rs;
    }
    
    
    public static ResultSet getDdictItem(URequestContext ctxt, String item_name)
    throws Exception {
        
        UQueryEngine qe = ctxt.getQueryEngine();
        ResultSet rs = null;
        String sql = "";
        sql = "select * from u_ddict where dd_ddi_type_index = (select  ddi_type_index from u_ddict_item_type" +
                " where upper(ddi_type_code) = upper('" + item_name + "')) and dd_valid = 1 order by lower(dd_value) ";
        rs = qe.executeQuery(sql);
        
        return rs;
    }
    
    public static ResultSet getDdictItem(URequestContext ctxt, String item_name, int ent_rid)
    throws Exception {
        
        UQueryEngine qe = ctxt.getQueryEngine();
        ResultSet rs = null;
        String sql = "";
        sql = "select * from u_ent_ddict where dd_ddi_type_index = (select  ddi_type_index from u_ddict_item_type" +
                " where upper(ddi_type_code) = upper('" + item_name + "')) " +
                " and dd_ent_rid = " + ent_rid +
                " and dd_valid = 1 order by lower(dd_value) ";
        rs = qe.executeQuery(sql);
        
        return rs;
    }
    
    public static ResultSet getDdictItem(URequestContext ctxt, int ddiTypeIndex, int entityRid)
    throws UDBAccessException {
        UQueryEngine qe = ctxt.getQueryEngine();
        ResultSet rs = null;
        String sql = "";
        sql = "select * from u_ent_ddict where dd_ddi_type_index = " + ddiTypeIndex +
                " and dd_ent_rid = " + entityRid + " and dd_valid = 1 " +
                " order by lower(dd_value) ";
        rs = qe.executeQuery(sql);
        
        return rs;
    }
    
    public static ResultSet getDdictItem(URequestContext ctxt, int ddiTypeIndex)
    throws Exception {
        
        UQueryEngine qe = ctxt.getQueryEngine();
        ResultSet rs = null;
        String sql = "";
        sql = "select * from u_ddict where dd_ddi_type_index =" + ddiTypeIndex +" and dd_valid = 1 order by dd_value";
        rs = qe.executeQuery(sql);
        
        return rs;
    }
    
    public static ResultSet searchDdictItem(URequestContext ctxt, String item_name, String searchStr)
    throws Exception {
        
        UQueryEngine qe = ctxt.getQueryEngine();
        ResultSet rs = null;
        String sql = "";
        sql = "select * from u_ddict where " +
                "dd_ddi_type_index = (select  ddi_type_index from u_ddict_item_type where upper(ddi_type_code) = upper('" + item_name + "'))" +
                "and (upper(dd_value) like upper('" + searchStr + "%') or upper(dd_abbrv) like upper('" + searchStr + "%') ) order by lower(dd_value)";
        rs = qe.executeQuery(sql);
        
        return rs;
        
    }
    
    public static ResultSet searchDdictItem(URequestContext ctxt, String item_name, String searchStr, int ent_rid)
    throws Exception {
        
        UQueryEngine qe = ctxt.getQueryEngine();
        ResultSet rs = null;
        String sql = "";
        sql = "select * from u_ent_ddict where " +
                "dd_ddi_type_index = (select  ddi_type_index from u_ddict_item_type " +
                "where upper(ddi_type_code) = upper('" + item_name + "'))" +
                "and (upper(dd_value) like upper('" + searchStr + "%') " +
                "or upper(dd_abbrv) like upper('" + searchStr + "%') ) " +
                " and dd_ent_rid = " + ent_rid +
                " order by lower(dd_value)";
        rs = qe.executeQuery(sql);
        
        return rs;
        
    }
    
    public static ResultSet getNestedDdictItem(URequestContext ctxt, String item_name, String parent_code)
    throws Exception {
        UQueryEngine qe = ctxt.getQueryEngine();
        ResultSet rs = null;
        String sql = "";
        sql = " select dd_value,dd_index from u_ddict where dd_ddi_type_index = " +
                " (select ddi_type_index from u_ddict_item_type where upper(ddi_type_code) = upper('" + item_name + "'))" +
                " and dd_parent_index = " + parent_code + "  and dd_valid = 1 order by lower(dd_value)";
        rs = qe.executeQuery(sql);
        
        return rs;
        
        
    }
    
    public static ResultSet getDDItemSiblings(URequestContext ctxt, String item_dd_index)
    throws Exception {
        String[] params = new String[1] ;
        String[] dataTypes = new String[1] ;
        params[0] = item_dd_index ;
        dataTypes[0] = "uInt" ;
        UQueryEngine qe = ctxt.getQueryEngine();
        return qe.executeSP("u_get_dd_item_siblings", params, dataTypes) ;
    }
    
    public static ResultSet searchValidDdictItem(URequestContext ctxt, String item_name, String searchStr)
    throws Exception {
        //search string is compared with dd_abbrv & dd_value fields!!!
        UQueryEngine qe = ctxt.getQueryEngine();
        ResultSet rs = null;
        String sql = "";
        sql = "select * from u_ddict where " +
                "dd_ddi_type_index = (select  ddi_type_index from u_ddict_item_type where ddi_type_code = '" + item_name + "' )" +
                "and (dd_value like '%" + searchStr + "%' or dd_abbrv like '%" + searchStr + "%' ) and dd_valid = 1";
        rs = qe.executeQuery(sql);
        
        return rs;
        
    }
    
    public static ResultSet loadFromDD(URequestContext ctxt, String ddItemCode)
    throws Exception {
        logger.debug("loadFromDD start...");
        String[] inParamValue= new String[1];
        String[] inParamType=new String[1];
        inParamValue[0]= ddItemCode;
        inParamType[0]= "uInt";
        logger.debug("set parameter value");
        UQueryEngine qe = ctxt.getQueryEngine();
        ResultSet rsDDI = qe.executeSP("get_DD_Item", inParamValue,inParamType);
        return rsDDI;
    }
    
    public static void bulkDelete(URequestContext ctxt, String idsList)
    throws Exception{
        String delQuery = "delete from u_ddict where dd_index in " + idsList + ";" ;
        UQueryEngine qe = ctxt.getQueryEngine() ;
        qe.executeUpdate(delQuery) ;
    }
    
    public static ResultSet getDBRow(URequestContext ctxt)
    throws Exception{
        //logger.debug("in getDDRow") ;
        String inputValue = ctxt.getParameter("inputValue") ;
        String ddict_item_name = ctxt.getParameter("ddict_item_name") ;
        //logger.debug("in getDDRow: " + inputValue + " - " + ddict_item_name) ;
        
        String sqlQuery = "select * from u_ddict where dd_abbrv  like '" + inputValue + "' and " +
                " dd_ddi_type_index = (select  ddi_type_index from u_ddict_item_type where ddi_type_code = '" + ddict_item_name + "')" ;
        //logger.debug(sqlQuery) ;
        UQueryEngine qe = ctxt.getQueryEngine();
        return qe.executeQuery(sqlQuery);
    }
    
    public static ResultSet getNestedDBRow(URequestContext ctxt)
    throws Exception{
        String inputValue = ctxt.getParameter("inputValue") ;
        String ddict_item_name = ctxt.getParameter("ddict_item_name") ;
        String dd_parent_index = ctxt.getParameter("dd_parent_index") ;
        
        String sqlQuery = "select * from u_ddict where dd_abbrv  like '" + inputValue + "' and " +
                " dd_parent_index = " + dd_parent_index + " and " +
                " dd_ddi_type_index = (select  ddi_type_index from u_ddict_item_type where ddi_type_code = '" + ddict_item_name + "')" ;
        //logger.debug(sqlQuery) ;
        UQueryEngine qe = ctxt.getQueryEngine();
        return qe.executeQuery(sqlQuery);
    }
    
    
    
    public static ResultSet getSubBrandParents(URequestContext ctxt)
    throws Exception{
        //logger.debug("parents for: " + ctxt.getParameter("input_index")) ;
        String input_index = ctxt.getParameter("input_index") ;
        String[] params = new String[1] ;
        String[] dataTypes = new String[1] ;
        params[0] = input_index ;
        dataTypes[0] = "uInt" ;
        UQueryEngine qe = ctxt.getQueryEngine();
        return qe.executeSP("u_get_sub_brand_parents", params, dataTypes) ;
    }
    
    public static ResultSet getSKUParents(URequestContext ctxt)
    throws Exception{
        String input_index = ctxt.getParameter("input_index") ;
        String[] params = new String[1] ;
        String[] dataTypes = new String[1] ;
        params[0] = input_index ;
        dataTypes[0] = "uInt" ;
        UQueryEngine qe = ctxt.getQueryEngine();
        return qe.executeSP("u_get_sku_parents", params, dataTypes) ;
    }
    
    public static int getNextDDIndex(URequestContext ctxt, String ddItemName)
    throws UServletException {
        try {
            String[] inputParamValues = new String[1] ;
            String[] inputParamTypes = new String[1] ;
            inputParamValues[0] = ddItemName  ;
            inputParamTypes[0] = "uString" ;
            int nextDDIndex = 0 ;
            UQueryEngine qe = ctxt.getQueryEngine();
            ResultSet rs ;
            rs = qe.executeSP("get_next_dd_index",inputParamValues,inputParamTypes) ;
            if(rs.next()){
                nextDDIndex = rs.getInt("dd_index") ;
            }
            return nextDDIndex ;
        } catch (Exception e) {
            throw new UServletException("Unable to get next dd index",e) ;
        }
    }
    
    public static String getDDValue(URequestContext ctxt, int ddIndex)
    throws UServletException {
        try {
            
            String sql = "select dd_value from u_ddict where dd_index = " + ddIndex ;
            ResultSet rs = ctxt.getQueryEngine().executeQuery(sql);
            String ddValue = "";
            if (rs != null && rs.next())
                ddValue = rs.getString("dd_value");
            
            return ddValue ;
        } catch (Exception ex) {
            logger.error("Error while getting dd value. " + ex.toString() );
            throw new UServletException(ex);
            
        }
    }
    private static int _getNewDdIndex(URequestContext ctxt, int fromRangeIndex, int toRangeIndex, boolean isEntitySpecific)
    throws UServletException {
      /*
       * -1 indicates dd entry in the range is not yet started
       *
       */
        
        int newDdIndex = 0 ;
        String sql = "" ;
        ResultSet rs = null ;
        try {
            // This query will get max dd entry records in that range which may be belongs to different type index
            sql = " select max(dd_index) as dd_index from u_ddict where dd_index >= " + fromRangeIndex +
                    " and dd_index <= " + toRangeIndex + " " + UDBUtils.limitReturnRows(ctxt, 1) ;
            if (isEntitySpecific) {
                sql = " select max(dd_index) as dd_index from u_ent_ddict " +
                        " where dd_index >= " + fromRangeIndex +
                        " and dd_index <= " + toRangeIndex +
                        UDBUtils.limitReturnRows(ctxt, 1);
            }
            
            rs = ctxt.getQueryEngine().executeQuery(sql) ;
            
            if (rs != null && rs.next()) {
                int index = rs.getInt("dd_index") ;
                if (index == 0) // Here if index is zero (or null) then entry not yet started
                    newDdIndex = fromRangeIndex ;
                else if(index < toRangeIndex ) // Here if index is less than the upper range (toRangeIndex)
                    newDdIndex = index + 1 ;
                else newDdIndex = 0 ; // In other case dd entry considered as beyond the limit
            }
            
            // newDdIndex will be zero, when dd entry beyond the range limit
            if (newDdIndex == 0){
                sql = " select (max(dd_index) + 1) as dd_index from u_ddict";
                if (isEntitySpecific) {
                    sql = " select (max(dd_index) + 1) as dd_index from u_ent_ddict " ;
                }
                rs = ctxt.getQueryEngine().executeQuery(sql) ;
                if (rs != null && rs.next()) {
                    newDdIndex = rs.getInt("dd_index") ;
                }
            }
        } catch(Exception ex){
            logger.error("Error while getting dd index. " + ex.toString() );
            throw new UServletException(ex);
        }
        return newDdIndex ;
    }
    
    public static String getDDItemDesc(URequestContext ctxt, int ddIndex)
    throws UDBAccessException {
        String ddValue = "";
        try {
            String sql = "select dd_value from u_ddict where dd_index =" +  ddIndex ;
            ResultSet rs =  ctxt.getQueryEngine().executeQuery(sql);
            if (rs != null && rs.next())
                ddValue = rs.getString("dd_value");
            return ddValue ;
        } catch (Exception ex) {
            logger.error("Error while getting dd value. " + ex.toString() );
            throw new UDBAccessException("Error while getting dd value.", ex);
        }        
    }
    
    public static String getDDValue(URequestContext ctxt, int ddIndex, int entityRID)
    throws UDBAccessException {
        String ddValue = "";
        try {
            String sql = "select dd_value from u_ent_ddict where dd_index =" +  ddIndex ;
            ResultSet rs =  ctxt.getQueryEngine().executeQuery(sql);
            if (rs != null && rs.next())
                ddValue = rs.getString("dd_value");
            return ddValue ;
        } catch (Exception ex) {
            logger.error("Error while getting entity dd value. " + ex.toString() );
            throw new UDBAccessException("Error while getting entity dd value.", ex);
        }   
    }
    
    public static ResultSet getDDItemWithSearch(URequestContext ctxt, String searchStr , int skuValid ,
            int skuTypeIndex, int skuParentIndex,int profileRID) throws UDBAccessException {
        
        String sql = null;
        sql = "select * from ( ";
        sql = sql +  " select dd_index ,dd_value from u_ddict ";
        String whereClause = " where dd_ddi_type_index =" + skuTypeIndex ;
        whereClause = whereClause + " and dd_valid =" + skuValid;
        
        if (skuParentIndex != 0)
            whereClause = whereClause +  " and dd_parent_index =" + skuParentIndex ;
        
        if (!"".equals(searchStr))
            whereClause = whereClause + " and upper(dd_value) like upper('" + searchStr + "%')";
        
        whereClause = whereClause + " order by dd_value ";
        
        sql = sql + whereClause ;
        
        sql = sql + " ) t1 where t1.dd_index not in( " +
                " select al_dd_index from phr_allergies where al_profile_rid = " + profileRID +
                " )";
        
        return ctxt.getQueryEngine().executeQuery(sql);
    }
    
    //added by anju
    public static ResultSet getDDItemsByOrder(URequestContext ctxt,  int typeIndex, String orderBy)
    throws UDBAccessException {
        try {
            UQueryEngine qe = ctxt.getQueryEngine();
            String sql = "select * from u_ddict where dd_ddi_type_index = " + typeIndex + " and dd_valid = 1 " +
                    " order by " + orderBy;
            ResultSet rs = qe.executeQuery(sql);
            return rs;
            
        } catch (Exception e) {
            throw new UDBAccessException("Exception in getDDItemsByOrder() " + e.getMessage(), e.getCause());
        }
    }
    
public static void  _insertVisitReasonEntry(URequestContext ctxt, int ddIndex, boolean isUpdate)
    throws UDBAccessException {
        String sql = "";
        try {
            int apptBookTypeIndex = ctxt.getIntParameter("apptBookType");
            int serviceRID = ctxt.getIntParameter("serviceRID");
            if(isUpdate) {
                sql  = "update u_visit_reason_ext set " +
                        " visitx_appt_book_type= " + apptBookTypeIndex + "," +
                        " visitx_service_rid = " + serviceRID +
                        " where visitx_dd_index= " + ddIndex;
               ctxt.getQueryEngine().executeUpdate(sql);
                
            } else {
               sql = "insert into u_visit_reason_ext ( visitx_dd_index, visitx_service_rid, visitx_appt_book_type," +
                       " visitx_is_schedulable) " +
                       " values(" + ddIndex + ","+ serviceRID + "," + apptBookTypeIndex +",1)";
               ctxt.getQueryEngine().executeInsert(sql);
            }
           
           
        } catch (Exception ex) {
            logger.error("Error while getting dd value. " + ex.toString() );
            throw new UDBAccessException("Error while getting dd value.", ex);
        }        
    }
}
