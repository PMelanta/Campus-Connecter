package cc.base;

import java.sql.*;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

public final class UEntity {
    
    static Logger logger = Logger.getLogger(UEntity.class);
    
    public static final int REGISTERED = 1 ;
    public static final int UN_REGISTERED = 0 ;
    
    private UEntity() {
    }
    
    public static ResultSet getEntities(URequestContext ctxt, boolean showValidOnly)
    throws UDBAccessException {
        
        String sql = "select * from u_entity ";
        
        if(showValidOnly)
            sql = sql + " where ent_registered = 1";
        
        sql = sql + " order by ent_name ";
        
        UQueryEngine qe = ctxt.getQueryEngine();
        
        ResultSet rs = qe.executeQuery(sql);
        
        return rs;
    }
    
    public static ResultSet getEntities(URequestContext ctxt, boolean showValidOnly, int orderBy)
    throws UDBAccessException {
        
        String sql = "select * from u_entity ";
        
        if(showValidOnly)
            sql = sql + " where ent_registered = 1";
        
        sql = sql + " order by ";
        switch(orderBy) {
            case 0:
                sql += "ent_code";
                break;
            default:
                sql += "ent_name";
                break;
        }
        
        UQueryEngine qe = ctxt.getQueryEngine();
        
        ResultSet rs = qe.executeQuery(sql);
        
        return rs;
    }
    
    public static ResultSet getLocations(UQueryEngine qe, String customerID)
	throws UDBAccessException {
        
	try {
	    String sql = "select * from u_entity where upper(ent_code) = upper('" + customerID + "')";
        
	    ResultSet rs = qe.executeQuery(sql);
        
	    return rs;
	} catch (Exception e) {
	    throw new UDBAccessException(e.getMessage(), e);
	}
    }
    
    public static ResultSet getEntities(URequestContext ctxt, boolean showValidOnly,boolean showToplevelEntity)
    throws UDBAccessException {
        
        String sql = "select * from u_entity ";
        
        if(showValidOnly)
            sql = sql + " where ent_registered = 1";
        if(showToplevelEntity) {
            if(showValidOnly)
                sql = sql + " and ent_parent_rid = 0";
            else
                sql = sql + " where ent_parent_rid = 0";
        }
        sql = sql + " order by ent_name ";
        
        UQueryEngine qe = ctxt.getQueryEngine();
        
        ResultSet rs = qe.executeQuery(sql);
        
        return rs;
    }
    
    public static ResultSet getEntityType(URequestContext ctxt, boolean showValidOnly)
    throws UDBAccessException {
        
        String sql = "select * from u_entity ";
        
        if(showValidOnly)
            sql = sql + " where ent_registered = 1";
        
        sql = sql + " group by ent_type ";
        
        UQueryEngine qe = ctxt.getQueryEngine();
        
        ResultSet rs = qe.executeQuery(sql);
        
        return rs;
    }
    
    private static boolean entityExists(URequestContext ctxt, String entity, int entityRID)
    throws UDBAccessException {
        
        try {
            
            String sql= "select ent_name from u_entity where ent_name = '" + entity + "'";
            
            if(entityRID != 0)
                sql = sql + " and ent_rid <> " + entityRID;
            
            UQueryEngine qe = ctxt.getQueryEngine();
            
            ResultSet rs = qe.executeQuery(sql);
            
            if(rs != null && rs.next())
                return true;
            
            return false;
        } catch(Exception e) {
            
            throw new UDBAccessException(e.getMessage(), e);
        }
        
    }
    
    public static void insertEntity(URequestContext ctxt, int entityRID)
    throws UDBAccessException {
        String entity = ctxt.getParameter("entity");
        String entityType = ctxt.getParameter("entityType");
        String entityCode = ctxt.getParameter("entityCode");
        
        // Check if a role of THIS name already exists
        if(entityExists(ctxt, entity, 0)) {
            
            ctxt.setAttribute("errorMessage", "Entity with name '" + entity +
                    "' already exists for this product. Please enter a different name.");
            return;
        }
        
        // Not found. Go ahead and insert
        
        String sql = "insert into u_entity (ent_name, ent_type,ent_code, ent_registered) values (";
        
        sql = sql + "'" + entity + "' ,'" + entityType + "', '" + entityCode + "' , ";
        
        sql = sql + ("on".equals(ctxt.getParameter("isActive")) ? 1 : 0) + ")";
        
        //UQueryEngine qe = ctxt.getQueryEngine();
        
        try {
            
            UQueryEngine qe = ctxt.getQueryEngine();
            qe.executeInsert(sql);
            
        } catch (Exception e) {
            
            throw new UDBAccessException(e.getMessage(), e);
        }
        
        
    }
    
    public static void updateEntity(URequestContext ctxt, int entityRID)
    throws UDBAccessException {
        String entity = ctxt.getParameter("entity");
        
        // Check if a role of THIS name already exists
        if(entityExists(ctxt, entity, entityRID)) {
            
            ctxt.setAttribute("errorMessage", "Entity with name '" + entity +
                    "' already exists for this product. Please enter a different name.");
            return;
        }
        
        // No dulicates found. Go ahead and update
        String sql = "update u_entity set ent_rid = " + entityRID +
                ", ent_name = '" + ctxt.getParameter("entity") + "'" +
                ", ent_type = '" + ctxt.getParameter("entityType") + "'" + ", ent_code = '" + ctxt.getParameter("entityCode") + "'" + ", ent_registered = " + ("on".equals(ctxt.getParameter("isActive")) ? 1 : 0) +
                " where ent_rid = " + entityRID;
        
        UQueryEngine qe = null;
        
        try {
            
            qe = ctxt.getQueryEngine();
            qe.executeUpdate(sql);
            
        } catch (Exception e) {
            
            throw new UDBAccessException(e.getMessage(), e);
        }
        
    }
    
    
    public static void saveEntity(URequestContext ctxt)
    throws UDBAccessException {
        
        int entityRID = ctxt.getIntParameter("entityRID");
        
        if(entityRID > 0)
            updateEntity(ctxt, entityRID);
        else
            insertEntity(ctxt, entityRID);
        
        
    }
    
    public static ResultSet getEntityDetails(UQueryEngine qe, int entityRid) //Akhil
    throws UDBAccessException{
        String sql = "select * from u_entity where ent_rid=" + entityRid;
        ResultSet rs = qe.executeQuery(sql);
        return rs;
    }
    
    public static ResultSet getEntityCompleteDetail(URequestContext ctxt, int ent_rid) throws UBaseException {
        String sql = " select e.*, city.dd_value uCity, state.dd_value uState, country.dd_value uCountry " +
                " from u_entity e "+
                " left outer join u_ddict city on city.dd_index = ent_city  "+
                " left outer join u_ddict state on state.dd_index = ent_state  "+
                " left outer join u_ddict country on country.dd_index = ent_country  "+
                " where ent_registered = " + REGISTERED + " and ent_rid = " + ent_rid ;
        
        ResultSet rs = ctxt.getQueryEngine().executeQuery(sql);
        return rs;
    }
    
    public static ResultSet getChildEntity(URequestContext ctxt, int rootEntityRID)
    throws UDBAccessException {
        
        try {
            
            String sql= " select ent_rid, ent_code, ent_name " +
                        " from u_entity " +
                        " where ent_registered = " + REGISTERED ;
            
            sql = sql + " and (ent_root_parent_rid = " + rootEntityRID + " or ent_rid = " + rootEntityRID + ") " ;
            
            return ctxt.getQueryEngine().executeQuery(sql);
            
        } catch(Exception e) {
            
            throw new UDBAccessException(e.getMessage(), e);
        }
        
    }

    public static ResultSet getChildEntityWithoutparent(URequestContext ctxt, int rootEntityRID)
    throws UDBAccessException {

        try {

            String sql= " select ent_rid, ent_code, ent_name,  ent_short_name " +
                        " from u_entity " +
                        " where ent_registered = " + REGISTERED ;

            sql = sql + " and (ent_root_parent_rid = " + rootEntityRID + " AND ent_rid <> " + rootEntityRID + ")"
                    + " order by ent_short_name " ;

            return ctxt.getQueryEngine().executeQuery(sql);

        } catch(Exception e) {

            throw new UDBAccessException(e.getMessage(), e);
        }

    }

    public static int getRootParentRID(URequestContext ctxt, int entityRID)
    throws UDBAccessException {
        
        try {
            int rootParentRID =0;
            String sql= " select ent_root_parent_rid " +
                        " from u_entity " +
                        " where ent_rid = " + entityRID   ;

            
            ResultSet rs = ctxt.getQueryEngine().executeQuery(sql);
            if (rs != null && rs.next())
                rootParentRID =  rs.getInt("ent_root_parent_rid");
            
            return rootParentRID;
            
        } catch(Exception e) {
            
            throw new UDBAccessException(e.getMessage(), e);
        }
    }

    public static ResultSet getEntitySiblings(URequestContext ctxt, int entityId) 
    throws UDBAccessException {
        try {
            UQueryEngine qe = ctxt.getQueryEngine();
            String sql = "SELECT DISTINCT childEntity.ent_city FROM u_entity childEntity JOIN " +
                    " u_entity currentEntity ON (currentEntity.ent_parent_rid = childEntity.ent_parent_rid " +
                    " AND currentEntity.ent_rid = " + entityId + ")" +
                    " UNION ALL " +
                    "SELECT ent_city FROM u_entity WHERE ent_rid =" + entityId;
            return qe.executeQuery(sql);
        } catch (Exception e) {
            throw new UDBAccessException(e.getMessage(), e);
        }
    }
}

