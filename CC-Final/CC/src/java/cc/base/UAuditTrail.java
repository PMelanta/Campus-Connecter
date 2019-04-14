package cc.base;

import cc.util.Paging;
import cc.util.UDate;
import cc.util.UString;
import com.sun.org.apache.bcel.internal.generic.SIPUSH;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;


import org.apache.log4j.Logger;

public final class UAuditTrail {
    
    private static Logger logger = Logger.getLogger(UAuditTrail.class);
    
    protected final static int AUDIT_LOGIN = 1;
    protected final static int AUDIT_LOGIN_FAILED = 2;
    protected final static int AUDIT_LOGOUT = 3;
    public final static int PROFILE_UPDATED = 4;
    public final static int APPRAISAL_CONDUCTED = 5;
    public final static int POSITION_CHANGED = 6;
    public final static int BUDGET_CREATED = 7;
    public final static int BUDGET_MODIFIED = 8;
    public final static int INTERVIEWED = 9;
    public final static int JOINED = 10;
    private final static int AUDIT_DISPLAY_LIMIT = 1000;
    
    private UAuditTrail() {}
    
    public static ResultSet getAuditEvents(URequestContext ctxt)
    throws UDBAccessException {
        
        String sql = "select * from u_audit_event";
        
        return ctxt.getQueryEngine().executeQuery(sql);
    }
    
    public static ResultSet getAuditTrailConfig(URequestContext ctxt, int entityRID)
    throws UDBAccessException {
        
        String sql = "select * from u_audit_config where aconfig_entity_rid = " + entityRID;
        
        return ctxt.getQueryEngine().executeQuery(sql);
    }
    
    public static void addEntry(UQueryEngine qe, HttpServletRequest req, int auditEvent, int entityRID, int userRID,
            int patientRID, int contextObjRID, String query, String details)
            throws UDBAccessException {
        
        String currentDateTime = UDate.nowDBString() + " " + UDate.currentTime();
        
        String remoteAddr = "";
        String remoteHost = "";
        
        if(req != null) {
            remoteAddr = req.getRemoteAddr();
            remoteHost = req.getRemoteHost();
        }
        
        if(query == null)
            query = "";
        else
            query = UString.escapeSpecialChars(query);
        
        if(details == null)
            details = "";
        else
            details = UString.escapeSpecialChars(details);
        
        String sql = "insert into u_audit_trail (audit_entity_rid, audit_event_index, audit_patient_rid, " +
                "audit_context_obj_rid, audit_query, audit_other_details, audit_user_rid, audit_datetime, audit_client_ip_address, " +
                "audit_client_host_name) values (" +
                entityRID + ", " + auditEvent + ", " + patientRID + ", " +
                contextObjRID + ", '" + query + "', '" + details + "', " + userRID + ", " +
                "'" + currentDateTime + "', '" + remoteAddr + "', '" + remoteHost + "')";
        
        qe.executeInsert(sql);
    }
    
    public static void addEntry(URequestContext ctxt, int auditEvent, int patientRID, int contextObjRID,
            String query, String details)
            throws UDBAccessException {
        
        addEntry(ctxt.getQueryEngine(), ctxt.getHttpRequest(), auditEvent, ctxt.getUserEntityRID(),
                ctxt.getUserRID(), patientRID, contextObjRID, query, details);
    }
    
    public static void addEntry(URequestContext ctxt, int auditEvent, String details)
    throws UDBAccessException {
        
        addEntry(ctxt.getQueryEngine(), ctxt.getHttpRequest(), auditEvent, ctxt.getUserEntityRID(),
                ctxt.getUserRID(), 0, 0, "", details);
    }
    
    public static void addEntry(URequestContext ctxt, int auditEvent, String details, String sqlQry)
    throws UDBAccessException {
        
        addEntry(ctxt.getQueryEngine(), ctxt.getHttpRequest(), auditEvent, ctxt.getUserEntityRID(),
                ctxt.getUserRID(), 0, 0, sqlQry, details);
    }
    
    // To be used only when a CTXT is not available (e.g. Failed logins)
    public static void addEntry(UQueryEngine qe, HttpServletRequest request, int auditEvent, int entityRID, String details)
    throws UDBAccessException {
        
        addEntry(qe, request, auditEvent, entityRID, 0, 0, 0, "", details);
    }
    
    public static ResultSet getAuditTrail(URequestContext ctxt, int entityRID, int userRID, int eventIndex,
            int patientRID, java.sql.Date fromDate, java.sql.Time fromTime,
            java.sql.Date toDate, java.sql.Time toTime, int auditRID,
            int direction, int displayLimit)
            throws UDBAccessException {
        try {
            String sql = "select " + Paging.CALC_FOUND_ROWS + " audit_rid, audit_datetime, event_name, user_full_name, pat_name, " +
                    "audit_other_details, audit_client_ip_address from (u_audit_trail LEFT JOIN u_patient " +
                    "on audit_patient_rid = pat_rid) LEFT JOIN u_user on audit_user_rid = user_rid, " +
                    "u_audit_event where audit_event_index = event_index";
            
            if(entityRID > 0){
                sql += " and audit_entity_rid = " + entityRID;
            }
            if(userRID > 0){
                sql += " and audit_user_rid = " + userRID;
                
            }
            if(patientRID > 0){
                sql += " and audit_patient_rid = " + patientRID;
                
            }
            if(eventIndex > 0){
                sql += " and audit_event_index = " + eventIndex;
                
            }
            
            if(fromDate != null) {
                String time = "00:00:00";
                
                if(fromTime != null)
                    time = fromTime.toString();
                
                sql += " and audit_datetime >= '" + fromDate + " " + time + "'";
                
            }
            
            if(toDate != null) {
                String time = "23:59:59";
                
                if(toTime != null)
                    time = toTime.toString();
                
                sql += " and audit_datetime < '" + toDate + " " + time + "'";
                
            }
            
            if(auditRID > 0 && direction < 0) {
                sql += " and audit_rid < " + auditRID  ;
            } else if(auditRID > 0 && direction > 0) {
                sql += " and audit_rid > " + auditRID ;
            }
            
            sql += " order by audit_datetime " ;
            if(direction < 0)
                sql+= " desc";
            
            ctxt.setAttribute("foundRows", Paging.getFoundRows(ctxt, sql));
            sql = Paging.getPagingString(ctxt, sql);
            
            ResultSet rs = ctxt.getQueryEngine().executeQuery(sql);
            
            
            
            return rs;
        } catch (Exception ex) {
            throw new UDBAccessException(ex.getMessage(), ex);
        }
    }
    
    public static ResultSet getAuditDetails(URequestContext ctxt)
    throws UDBAccessException{
        
        int patRID = ctxt.getIntParameter("patRID");
        int userRID = ctxt.getIntParameter("userRID");
        int eventIndex = ctxt.getIntParameter("eventIndex");
        int direction = ctxt.getIntParameter("direction");
        int auditRID = ctxt.getIntParameter("auditRID");
        int displayLimit = ctxt.getIntParameter("limit");
        
        DateFormat dt = new SimpleDateFormat();
        
        java.sql.Date fromDate = ctxt.getDateParameter("fromDate");
        java.sql.Date toDate = ctxt.getDateParameter("toDate");
        java.sql.Time fromTime = UDate.parseTime("00:00", "hh:mm a");
        java.sql.Time toTime = UDate.parseTime("24:00", "hh:mm a");
        ResultSet rs = null;
        
        rs = getAuditTrail(ctxt,ctxt.getUserEntityRID(),userRID,eventIndex,patRID,fromDate,fromTime,toDate,toTime,auditRID,direction,displayLimit);
        return rs;
        
        
    }
    
    public  static void loadAuditTrail(URequestContext ctxt)
    throws UDBAccessException{
        ResultSet rs = null;
        
        String sql = "select * from u_user uu, u_staff us where uu.user_entity_rid = " + ctxt.getUserEntityRID() +
                " and uu.user_account_rid = 1" +
                " and uu.user_rid = us.staff_user_rid order by user_full_name" ;
        
        UQueryEngine qe = ctxt.getQueryEngine();
        rs = qe.executeQuery(sql);
        
        ctxt.setAttribute("sysUsers",rs);
        
        sql = "select event_index,event_name from u_audit_event order by event_name";
        rs = qe.executeQuery(sql);
        
        ctxt.setAttribute("eventsRS",rs);
    }
    
    
}
