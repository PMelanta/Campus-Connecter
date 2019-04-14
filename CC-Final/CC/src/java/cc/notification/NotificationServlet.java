/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cc.notification;

import cc.base.UDBAccessException;
import cc.base.UHttpServlet;
import cc.base.URequestContext;
import cc.base.UServletException;
import cc.base.UServletHelper;
import cc.base.UWriterHelper;
import java.sql.ResultSet;

/**
 *
 * @author suhas
 */
public class NotificationServlet extends UHttpServlet {
    
    String jsp = "jsp/sms/complaint.jsp";

    @Override
    public void handleGet(URequestContext ctxt) throws UServletException {
       String command = ctxt.getParameter("command");
        if("getNotification".equals(command)){
            _getNotification(ctxt);
        }else if("getExamMarks".equals(command)){
            _getExamMarks(ctxt);
        }else if("getAttendance".equals(command)){
            _getAttendance(ctxt);
        }else if("getEvents".equals(command)){
            _getEvents(ctxt);
        }else if("getLibrary".equals(command)){
            _getLibrary(ctxt);
        }else if("getComplaints".equals(command)){
            getComplaints(ctxt);
        }
        
    }

    @Override
    public void handlePost(URequestContext ctxt) throws UServletException {
        
        String command = ctxt.getParameter("command");
        if("createEvent".equals(command)){
            _createEvent(ctxt);
        }else if("submitComplaint".equals(command)){
            _submitComplaint(ctxt);
        }
        
    }

    private void _createEvent(URequestContext ctxt) throws UServletException{
        
        String res = "";
        try{
            int studRid = ctxt.getIntParameter("studRid");
            int isForAll = ctxt.getIntParameter("isForAll");
            String eventDateTime = ctxt.getParameter("eventDateTime");
            String notDateTime = ctxt.getParameter("notDateTime");
            String eventName = ctxt.getParameter("eventName");
            String eventDesc = ctxt.getParameter("eventDesc");
            String eventOrganiser = ctxt.getParameter("eventOrganiser");
            String eventLocation = ctxt.getParameter("eventLocation");
            String branchRids = ctxt.getParameter("branchRids");
            
            int notRid = NotificationManager.createEvent(ctxt,studRid,isForAll,eventDateTime,notDateTime,eventName,eventDesc,
                    eventOrganiser,eventLocation,branchRids);
            
            res="OK";
        }catch(Exception ex){
            res = "NOT_OK#"+ex.getMessage();
            throw new UServletException(ex);
        }finally{
            UWriterHelper.writeString(ctxt, res);
        }
        
        
    }
    private void _submitComplaint(URequestContext ctxt) throws UServletException{
        
        String res = "";
        try{
            int studentRid = ctxt.getIntParameter("studentRid");
            String sub = ctxt.getParameter("sub");
            String mes = ctxt.getParameter("mes");
            
            int notRid = NotificationManager.subComplaint(ctxt, studentRid, sub, mes);
            res="OK";
        }catch(Exception ex){
            res = "NOT_OK#"+ex.getMessage();
            throw new UServletException(ex);
        }finally{
            UWriterHelper.writeString(ctxt, res);
        }
        
        
    }

    private void _getNotification(URequestContext ctxt) throws UServletException{
        
        String resStr = "";
      try {
          int studentRid = ctxt.getIntParameter("studRid");
         ResultSet rs = NotificationManager.getPendingNotification(ctxt, studentRid);
         resStr = "OK";
         while(rs.next()){
             resStr += "#"+rs.getInt("not_rid")+"~"+rs.getString("not_header")
                     +"~"+rs.getString("not_ref_datetime")+"~"+rs.getInt("ntype");
         }
      } catch (Exception ex) {
         resStr = "NOT_OK#"+ex.getMessage();
      }finally{
          UWriterHelper.writeString(ctxt, resStr);
      }
    }
    
    private void _getExamMarks(URequestContext ctxt) throws UServletException{
        
        String resStr = "";
      try {
          int studentRid = ctxt.getIntParameter("studRid");
         ResultSet rs = NotificationManager.getExamMarks(ctxt, studentRid);
         resStr = "OK";
         while(rs.next()){
             resStr += "#"+rs.getInt("sms_sem")+"~"+rs.getString("exam_name")
                     +"~"+rs.getString("sub_name")
                     +"~"+rs.getString("not_ref_datetime")
                     +"~"+rs.getFloat("min_marks")
                     +"~"+rs.getFloat("max_marks")
                     +"~"+rs.getFloat("sms_obtained_marks")
                     +"~"+rs.getInt("exam_type");
         }
      } catch (Exception ex) {
         resStr = "NOT_OK#"+ex.getMessage();
      }finally{
          UWriterHelper.writeString(ctxt, resStr);
      }
    }
    private void _getAttendance(URequestContext ctxt) throws UServletException{
        
        String resStr = "";
      try {
          int studentRid = ctxt.getIntParameter("studRid");
         ResultSet rs = NotificationManager.getAttendance(ctxt, studentRid);
         resStr = "OK";
         while(rs.next()){
             resStr += "#"+rs.getInt("att_sem")+"~"+rs.getString("sub_name")
                     +"~"+rs.getString("not_ref_datetime")
                     +"~"+rs.getInt("atc_tot_class")
                     +"~"+rs.getInt("att_tot_present")
                     +"~"+rs.getInt("att_month_no");
         }
      } catch (Exception ex) {
         resStr = "NOT_OK#"+ex.getMessage();
      }finally{
          UWriterHelper.writeString(ctxt, resStr);
      }
    }
    private void _getEvents(URequestContext ctxt) throws UServletException{
        
        String resStr = "";
      try {
          int studentRid = ctxt.getIntParameter("studRid");
         ResultSet rs = NotificationManager.getEvents(ctxt, studentRid);
         resStr = "OK";
         while(rs.next()){
             resStr += "#"+rs.getInt("event_rid")+"~"+rs.getString("event_name")
                     +"~"+rs.getString("event_desc")
                     +"~"+rs.getString("event_date_time")
                     +"~"+rs.getString("event_location")
                     +"~"+rs.getString("event_organiser");
         }
      } catch (Exception ex) {
         resStr = "NOT_OK#"+ex.getMessage();
      }finally{
          UWriterHelper.writeString(ctxt, resStr);
      }
    }
    private void _getLibrary(URequestContext ctxt) throws UServletException{
        
        String resStr = "";
      try {
         
         ResultSet rs = NotificationManager.getLibrary(ctxt);
         resStr = "OK";
         while(rs.next()){
             resStr += "#"+rs.getString("book_title")
                     +"~"+rs.getString("book_author")
                     +"~"+rs.getString("book_edition")
                     +"~"+rs.getString("book_publication");
         }
      } catch (Exception ex) {
         resStr = "NOT_OK#"+ex.getMessage();
      }finally{
          UWriterHelper.writeString(ctxt, resStr);
      }
    }

    private void getComplaints(URequestContext ctxt) 
            throws UServletException{
        try{
        ResultSet rs = NotificationManager.getComplaints(ctxt);
        ctxt.setAttribute("rs", rs);
            UServletHelper.sendJSP(ctxt, jsp);
        }catch(Exception E){
            System.out.println(E);
            throw new UServletException(E);
        }
    }

    
}
