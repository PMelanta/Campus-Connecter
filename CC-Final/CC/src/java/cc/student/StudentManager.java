/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.student;

import java.sql.ResultSet;
import java.sql.SQLException;
import cc.base.UConfig;
import cc.base.UDBAccessException;
import cc.base.UMailer;
import cc.base.URequestContext;
import cc.util.UDate;
import cc.util.UString;

/**
 *
 * @author suhas
 */
public class StudentManager {

    public static ResultSet getStudentDetails(URequestContext ctxt, int branchRid, int batchRid, int sem, String studName, String regNo, int studentRid)
            throws UDBAccessException {
        String sql = "select * from student "
                + " join branch on(stud_branch_rid = branch_rid) "
                + " join stud_batch on(stud_batch_rid = batch_rid) "
                + " where student.row_invalidated = 0";
        if (studentRid > 0) {
            sql += " AND stud_rid = " + studentRid;
        }
        if (branchRid > 0) {
            sql += " AND stud_branch_rid = " + branchRid;
        }
        if (batchRid > 0) {
            sql += " AND stud_batch_rid = " + batchRid;
        }
        if (sem > 0) {
            sql += " AND stud_cur_sem = " + sem;
        }
        if (null != studName && !"".equals(studName.trim())) {
            sql += " AND stud_first_name like '" + studName + "%'";
        }
        if (null != regNo && !"".equals(regNo.trim())) {
            sql += " AND stud_reg_no like '" + regNo + "%'";
        }
        sql += " order by stud_cur_sem,stud_first_name";

        return ctxt.getQueryEngine().executeQuery(sql);
    }

    public static ResultSet getStudentDetails(URequestContext ctxt, String branchRids)
            throws UDBAccessException {
        String sql = "select * from student "
                + " join branch on(stud_branch_rid = branch_rid) "
                + " where student.row_invalidated = 0";
        if (null != branchRids && !"".equals(branchRids.trim())) {
            sql += " AND stud_branch_rid IN(" + branchRids + ")";
        }
        return ctxt.getQueryEngine().executeQuery(sql);
    }

    public static ResultSet loadStudentSel(URequestContext ctxt, int branchRid, int batchRid, int sem)
            throws UDBAccessException {
        String sql = "select stud_rid,CONCAT(stud_first_name,' ',stud_last_name,' (',stud_reg_no,')') as stud_name_reg "
                + " from student "
                + " where student.row_invalidated = 0";

        if (branchRid > 0) {
            sql += " AND stud_branch_rid = " + branchRid;
        }
        if (batchRid > 0) {
            sql += " AND stud_batch_rid = " + batchRid;
        }
        if (sem > 0) {
            sql += " AND stud_cur_sem = " + sem;
        }
        sql += " order by stud_first_name,stud_last_name";

        return ctxt.getQueryEngine().executeQuery(sql);
    }

    public static ResultSet getStudentDetails(URequestContext ctxt, int studentRid)
            throws UDBAccessException {
        String sql = "select * from student "
                + " join branch on(stud_branch_rid = branch_rid) "
                + " join stud_batch on(stud_batch_rid = batch_rid) "
                + " where student.row_invalidated = 0";
        if (studentRid > 0) {
            sql += " AND stud_rid = " + studentRid;
        }

        return ctxt.getQueryEngine().executeQuery(sql);
    }

    public static void deleteStudent(URequestContext ctxt, int studentRid)
            throws UDBAccessException {
        String sql = "UPDATE student "
                + " SET row_invalidated = 1,"
                + " mod_datetime = NOW(),"
                + " mod_user_rid =  " + ctxt.getUserRID()
                + " WHERE stud_rid = " + studentRid;
        ctxt.getQueryEngine().executeUpdate(sql);
    }

    public static void saveStudentDetails(URequestContext ctxt, int studentRid, int branchRid, int batchRid, int sem,
            String firstName, String lastName, String regNo,
            String gender, String dob, String studEmail, String father, String mother, String parentEmail,
            String contactNo, String address, String avatar, int active, int canCreateEvent) throws UDBAccessException, Exception {
        String sql = "";
        if (isDuplicateRegNo(ctxt, studentRid, regNo)) {
            ctxt.setAttribute("errorMessage", "Duplicate Reg. No. Already assigned to other student!");
            throw new Exception("Duplicate Reg. No., already assigned to other student!");
        }
        if (studentRid > 0) {
            sql = "UPDATE student "
                    + " SET stud_first_name = '" + firstName + "'"
                    + ",stud_last_name = '" + lastName + "'"
                    + ",stud_reg_no = '" + regNo + "'"
                    + ",stud_branch_rid = " + branchRid
                    + ",stud_batch_rid = " + batchRid
                    + ",stud_cur_sem	= " + sem
                    + ",stud_gender = '" + gender + "'"
                    + ",stud_dob  = '" + dob + "'"
                    + ",stud_fathers_name = '" + father + "'"
                    + ",stud_mothers_name = '" + mother + "'"
                    + ",stud_address	 = '" + UString.escapeSpecialChars(address) + "'"
                    + ",stud_email = '" + studEmail + "'"
                    + ",stud_parent_email = '" + parentEmail + "'"
                    + ",stud_parent_contact = '" + contactNo + "'"
                    + ",mod_datetime = NOW()"
                    + ",mod_user_rid = " + ctxt.getUserRID()
                    + ",is_active = " + active
                    + ",can_create_events = " + canCreateEvent;
            if (null != avatar && !"".equals(avatar.trim())) {
                sql += ",stud_image_url = '" + avatar + "'";
            }
            sql += " WHERE stud_rid = " + studentRid;
            ctxt.getQueryEngine().executeUpdate(sql);
        } else {
            sql = "INSERT INTO student(stud_first_name,stud_last_name,stud_reg_no,stud_branch_rid,stud_batch_rid,stud_cur_sem,"
                    + "stud_gender,stud_dob,stud_image_url,stud_fathers_name,stud_mothers_name,stud_address,"
                    + "stud_email,stud_parent_email,stud_parent_contact,created_datetime,created_user_rid,is_active,can_create_events) "
                    + " VALUES('" + firstName + "','" + lastName + "','" + regNo + "'" + "," + branchRid + "," + batchRid + "," + sem + ",'"
                    + gender + "','" + dob + "','" + avatar + "','" + father + "','" + mother + "','" + UString.escapeSpecialChars(address) + "','"
                    + studEmail + "','" + parentEmail + "','" + contactNo + "',NOW()," + ctxt.getUserRID() + "," + active + "," + canCreateEvent + ")";
            ctxt.getQueryEngine().executeInsert(sql);

        }
    }

    public static boolean isDuplicateRegNo(URequestContext ctxt, int studentRid, String regNo)
            throws UDBAccessException, SQLException {
        String sql = "select 1 from student where stud_reg_no = '" + regNo + "' AND  row_invalidated = 0 ";
        if (studentRid > 0) {
            sql += " AND stud_rid <> " + studentRid;
        }
        ResultSet rs = ctxt.getQueryEngine().executeQuery(sql);
        if (rs.first()) {
            return true;
        }
        return false;

    }

    public static void sendStudentSummary(URequestContext ctxt, ResultSet rsSubjects, ResultSet rsStudent,
            ResultSet rsStudentMarksSheet, ResultSet rsStudentAttSheet,
            String examName, String batchName, int monthNo, int sem) throws UDBAccessException {
        //preparing email string
        String parentEmail = "";
        String studEmail = "";
        String subject = "";
        String parentMobileNo = "";
        String smsMarks = "";
        String smsAttendance = "";
        String subArray[] = new String[12];
        float marksArray[] = new float[12];
        float attArray[] = new float[12];
        int subcount = 0;
        int markcount = 0;
        int attcount = 0;
        try {
            int sendCopyToStudent = ctxt.getCheckboxParameter("sendCopy");

            if (rsStudent.first()) {
                parentEmail = rsStudent.getString("stud_parent_email");
                studEmail = rsStudent.getString("stud_email");
                subject = "Academic summary of " + rsStudent.getString("stud_first_name") + " " + rsStudent.getString("stud_last_name") + "/" + rsStudent.getString("stud_reg_no");

                smsMarks = "Marks of " + rsStudent.getString("stud_first_name") + " - " + rsStudent.getString("stud_reg_no") + " : ";
                smsAttendance = "Attendance of " + rsStudent.getString("stud_first_name") + " - " + rsStudent.getString("stud_reg_no") + " : ";

                parentMobileNo = rsStudent.getString("stud_parent_contact");
            }

            String summaryStr = "<html><body><fieldset> "
                    + "<legend style='background-color: gray;font-weight: bold;color: white;'>Student Summary [<b>" + examName + "/" + UDate.monthNames[monthNo] + "/" + sem + " Sem/" + batchName + "</b>]</legend>"
                    + "<table width='100%'>"
                    + "<tr style='background-color: #CFD6E3;'>"
                    + "<td colspan='100%'>"
                    + "Marks Details"
                    + "</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td>"
                    + "Reg. No."
                    + "</td>";
            while (rsSubjects.next()) {
                summaryStr += "<td>" + rsSubjects.getString("sub_name") + " (" + rsSubjects.getString("sub_code") + ")" + "</td>";
                subArray[subcount] = rsSubjects.getString("sub_name");
                subcount++;
            }

            // smsMarks += "\n "+rsSubjects.getString("sub_name") +" : "+
            summaryStr += "<td>Total</td></tr>";

            float marks = 0;
            if (rsStudentMarksSheet.first()) {

                rsSubjects.beforeFirst();
                float totMarks = 0;

                summaryStr += "<tr style='font-weight: bold'>"
                        + "<td style='font-weight: bold'>" + rsStudentMarksSheet.getString("stud_reg_no") + "</td>";
                rsStudentMarksSheet.beforeFirst();

                while (rsSubjects.next()) {
                    if (rsStudentMarksSheet.next()) {
                        marks = rsStudentMarksSheet.getFloat("sms_obtained_marks");
                    } else {
                        marks = 0;
                    }
                    totMarks += marks;
                    marksArray[markcount] = marks;
                    markcount++;

                    summaryStr += "<td>" + marks + "</td>";
                }
                summaryStr += "<td>" + totMarks + "</td></tr>";
            }
            summaryStr += "</table>";

            summaryStr += "<br><hr style='border: none;height: 1px;color: #0b559b;background: #0b559b;'><br>";

            summaryStr += "<table width='100%'> "
                    + "<tr style='background-color: #CFD6E3;'>"
                    + "<td colspan='100%'>"
                    + "Attendance Details (%)"
                    + "</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td>"
                    + "Reg. No."
                    + "</td>";

            rsSubjects.beforeFirst();
            while (rsSubjects.next()) {
                summaryStr += "<td>" + rsSubjects.getString("sub_name") + " (" + rsSubjects.getString("sub_code") + ")</td>";
            }
            summaryStr += "</tr>";

            if (rsStudentAttSheet.first()) {
                rsSubjects.beforeFirst();
                float attendancePerc = 0;
                summaryStr += "<tr  style='font-weight: bold'>"
                        + "<td style='font-weight: bold'>" + rsStudentAttSheet.getString("stud_reg_no") + "</td>";

                rsStudentAttSheet.beforeFirst();
                while (rsSubjects.next()) {
                    if (rsStudentAttSheet.next()) {
                        attendancePerc = rsStudentAttSheet.getFloat("att_perc");
                    } else {
                        attendancePerc = 0;
                    }
                    attArray[attcount] = attendancePerc;
                    attcount++;

                    summaryStr += "<td> " + UString.formatNumber(attendancePerc, 2) + "</td>";
                }
                summaryStr += "</tr>";
            }
            summaryStr += "</table></fieldset></body></html>";

            summaryStr = UString.escapeSpecialChars(summaryStr);

            String fromEmail = UConfig.getParameterValue(ctxt, "DFM_APP_EMAIL_ID", "projectdemomail@gmail.com");

            UMailer.sendMail(ctxt, fromEmail, parentEmail, subject, summaryStr, "text/html");
            if (sendCopyToStudent == 1) {
                UMailer.sendMail(ctxt, fromEmail, studEmail, subject, summaryStr, "text/html");
            }
            UMailer.flushMailQueue();

            //Message
            int sendMarks = ctxt.getIntParameter("sendMarks");
            int sendAttendance = ctxt.getIntParameter("sendAttendance");

            if (parentMobileNo != null && !"".equals(parentMobileNo.trim())) {
                if (sendMarks == 1) {
                    for(int x=0; x<markcount && x<subcount; x++){
                        smsMarks += ", "+subArray[x]+" : "+marksArray[x];
                    }
                    String sql = " INSERT INTO message_queue(mq_message,mq_mob_num,mq_created_datetime)"
                            + " VALUES('"+smsMarks+"','"+parentMobileNo+"',NOW())";
                    ctxt.getQueryEngine().executeInsert(sql);
                }
                
                if(sendAttendance == 1){
                    for(int x=0; x<attcount && x<subcount; x++){
                        smsAttendance += ", "+subArray[x]+" : "+attArray[x];
                    }
                    String sql = " INSERT INTO message_queue(mq_message,mq_mob_num,mq_created_datetime)"
                            + " VALUES('"+smsAttendance+"','"+parentMobileNo+"',NOW())";
                    ctxt.getQueryEngine().executeInsert(sql);
                }
            }

        } catch (Exception ex) {
            throw new UDBAccessException(ex.getMessage(), ex);
        }
    }
}
