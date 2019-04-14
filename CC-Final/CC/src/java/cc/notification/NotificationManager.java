/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.notification;

import cc.base.UDBAccessException;
import cc.base.UQueryEngine;
import cc.base.URequestContext;
import cc.student.StudentManager;
import cc.util.UDate;
import cc.util.UString;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author
 */
public class NotificationManager {

    public static final int NOT_TYPE_EXAM = 1;
    public static final int NOT_TYPE_ATTENDANCE = 2;
    public static final int NOT_TYPE_LIBRARY = 3;
    public static final int NOT_TYPE_EVENT = 4;

    public static int addNotification(UQueryEngine qe, int studentRid, int notType,
            String header, String notifyOn, String refDateTime, int contextRid) throws UDBAccessException {
        String sql = "INSERT INTO notification(not_header,not_type,not_context_rid,not_student_rid,"
                + "not_notify_on,not_ref_datetime,not_created_datetime)"
                + " values('" + header + "'," + notType + "," + contextRid + "," + studentRid + ",'" + notifyOn + "','" + refDateTime + "',NOW())";
        return qe.executeInsert(sql);
    }

    public static void resetNotification(UQueryEngine qe, int notRid, String notifyOn, String refDateTime) throws UDBAccessException {
        String sql = "Update notification SET  not_is_notified= 0"
                + " , not_notify_on = '" + notifyOn + "'"
                + " , not_ref_datetime = '" + refDateTime + "'"
                + ", not_created_datetime = NOW()"
                + " WHERE not_rid=" + notRid;
        qe.executeUpdate(sql);
    }

    public static int getNotificationRid(UQueryEngine qe, int contextRid, int notType)
            throws UDBAccessException, SQLException {
        String sql = "SELECT * FROM notification "
                + " WHERE not_context_rid=" + contextRid + " AND not_type=" + notType;
        ResultSet rs = qe.executeQuery(sql);
        if (rs.first()) {
            return rs.getInt("not_rid");
        }
        return 0;
    }

    public static ResultSet getPendingNotification(URequestContext ctxt, int studentRid)
            throws UDBAccessException {

        String sql = "SELECT 1 as not_rid, not_header, MAX(not_ref_datetime) as not_ref_datetime," + NOT_TYPE_EXAM + " as ntype"
                + " FROM notification "
                + " WHERE not_student_rid = " + studentRid + ""
                + " AND not_is_notified = 0 AND  not_notify_on <= NOW() AND not_type=" + NOT_TYPE_EXAM
                + " GROUP BY not_header "
                + " UNION "
                + " SELECT 2 as not_rid, not_header, MAX(not_ref_datetime) as not_ref_datetime ," + NOT_TYPE_ATTENDANCE + " as ntype"
                + " FROM notification "
                + " WHERE not_student_rid = " + studentRid + ""
                + " AND not_is_notified = 0 AND  not_notify_on <= NOW() AND not_type=" + NOT_TYPE_ATTENDANCE
                + " GROUP BY not_header "
                + " UNION "
                + " SELECT not_rid, not_header, not_ref_datetime,not_type as ntype "
                + " FROM notification "
                + " WHERE not_student_rid = " + studentRid + ""
                + " AND not_is_notified = 0 AND  not_notify_on <= NOW() AND not_type=" + NOT_TYPE_EVENT;

        ResultSet rs = ctxt.getQueryEngine().executeQuery(sql);
        ctxt.getQueryEngine().executeUpdate(
                "UPDATE notification "
                + " SET not_is_notified = 1 "
                + " WHERE not_student_rid = " + studentRid
                + " AND not_is_notified = 0 AND  not_notify_on <= NOW()");
        return rs;
    }

    public static ResultSet getExamMarks(URequestContext ctxt, int studentRid)
            throws UDBAccessException {

        String sql = "SELECT sms_sem,exam_name,exam_type,sub_name, "
                + " IF(exam_type = 1, sub_internal_min, sub_external_min) AS min_marks, "
                + " IF(exam_type = 1, sub_internal_max, sub_external_max) AS max_marks, "
                + " not_ref_datetime,sms_obtained_marks "
                + " FROM notification  "
                + " JOIN stud_marks_sheet ON(not_context_rid = sms_rid) "
                + " JOIN exams ON(sms_exam_rid = exam_rid) "
                + " JOIN subject_details ON(sms_sub_rid = sub_rid) "
                + " WHERE not_student_rid = " + studentRid + " AND not_type = " + NOT_TYPE_EXAM
                + " ORDER BY not_ref_datetime DESC,exam_name ASC";

        return ctxt.getQueryEngine().executeQuery(sql);
    }

    public static ResultSet getAttendance(URequestContext ctxt, int studentRid)
            throws UDBAccessException {

        String sql = "SELECT att_sem,att_month_no,sub_name,att_tot_present,atc_tot_class,sub_name, not_ref_datetime "
                + " FROM notification "
                + " JOIN attendance ON(not_context_rid = att_rid) "
                + " JOIN subject_details ON(att_sub_rid=sub_rid) "
                + " JOIN attendance_tot_classes ON(sub_rid = atc_sub_rid) "
                + " WHERE "
                + " not_student_rid =" + studentRid + " AND not_type = " + NOT_TYPE_ATTENDANCE
                + " ORDER BY not_ref_datetime DESC,att_month_no ASC ";

        return ctxt.getQueryEngine().executeQuery(sql);
    }
    public static ResultSet getEvents(URequestContext ctxt, int studentRid)
            throws UDBAccessException {

        String sql = "SELECT * FROM event  " +
                    " JOIN notification ON(event_rid = not_context_rid) " +
                    " WHERE not_type = "+NOT_TYPE_EVENT+" AND not_student_rid = "+studentRid+
                    " AND not_notify_on <= NOW() " +
                    " ORDER BY not_ref_datetime DESC";

        return ctxt.getQueryEngine().executeQuery(sql);
    }
    public static ResultSet getLibrary(URequestContext ctxt)
            throws UDBAccessException {

        String sql = "SELECT * FROM book WHERE book_is_available=1 ";

        return ctxt.getQueryEngine().executeQuery(sql);
    }

    static int createEvent(URequestContext ctxt, int studRid, int forAll, String eventDateTime, String notDateTime,
            String eventName, String eventDesc, String eventOrganiser, String eventLocation, String branchRids)
            throws UDBAccessException, SQLException {
        eventDateTime = UDate.displayToDBTimestamp(eventDateTime);
        notDateTime = UDate.displayToDBTimestamp(notDateTime);

        eventName = UString.escapeSpecialChars(eventName);
        eventDesc = UString.escapeSpecialChars(eventDesc);
        eventLocation = UString.escapeSpecialChars(eventLocation);
        eventOrganiser = UString.escapeSpecialChars(eventOrganiser);

        String sql = "INSERT INTO event(event_name,event_desc,event_organiser,event_location,"
                + "event_date_time,event_created_stud_rid,event_created_datetime)"
                + " VALUES('" + eventName + "','" + eventDesc + "','" + eventOrganiser + "','" + eventLocation + "','" + eventDateTime + "',"
                + studRid + ",NOW())";
        int eventRid = ctxt.getQueryEngine().executeInsert(sql);

        //create notification
        ResultSet rsStud;
        if (forAll == 1) {
            rsStud = StudentManager.getStudentDetails(ctxt, null);
        } else {
            rsStud = StudentManager.getStudentDetails(ctxt, branchRids);
        }

        while (rsStud.next()) {
            addNotification(ctxt.getQueryEngine(), rsStud.getInt("stud_rid"),
                    NOT_TYPE_EVENT, "EVENT : " + eventName, notDateTime, eventDateTime, eventRid);
        }
        return eventRid;
    }
    
    static int subComplaint(URequestContext ctxt, int studRid, String sub, String mes)
            throws UDBAccessException, SQLException {
        String sql = "INSERT INTO complaint(com_sub,com_details,com_stud_rid,com_datetime)"
                + " VALUES('" + sub + "','" + mes + "','" + studRid + "',NOW())";
        return ctxt.getQueryEngine().executeInsert(sql);
    }

    static ResultSet getComplaints(URequestContext ctxt) throws UDBAccessException{
        String sql = "SELECT * FROM complaint JOIN student ON(com_stud_rid = stud_rid)"
                + " ORDER BY com_datetime DESC";
        return ctxt.getQueryEngine().executeQuery(sql);
    }

}
