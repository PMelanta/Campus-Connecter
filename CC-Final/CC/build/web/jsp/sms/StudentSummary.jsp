<%-- 
    Document   : StudentSummary
    
--%>


<%@page import="java.sql.ResultSet"%>
<%@page import="cc.branch.BranchManager"%>
<%@page import="cc.util.UDate"%>
<%@page import="cc.util.UString"%>

<%
   int subjectCount = 0;

   int sem = Integer.parseInt(request.getAttribute("sem").toString());
   String batchName = request.getAttribute("batchName").toString();
   int monthNo = Integer.parseInt(request.getAttribute("monthNo").toString());
   int examRid = Integer.parseInt(request.getAttribute("examRid").toString());
   int batchRid = Integer.parseInt(request.getAttribute("batchRid").toString());
   int studRid = Integer.parseInt(request.getAttribute("studRid").toString());
   int branchRid = Integer.parseInt(request.getAttribute("branchRid").toString());
   String examName = request.getAttribute("examName").toString();
   
   String viewType = (String)request.getAttribute("viewType");
   
   boolean hasStudRecords = false;
   boolean hasStudAttRecords = false;
   boolean hasSubs = false;

   String parentEmailID = "", studEmailID = "";
   ResultSet rsSubjects = (ResultSet) request.getAttribute("rsSubjects");

   ResultSet rsStudentMarksSheet = (ResultSet) request.getAttribute("rsStudentMarksSheet");
   if (null != rsSubjects && rsSubjects.first()) {
      rsSubjects.last();
      subjectCount = rsSubjects.getRow();
      rsSubjects.beforeFirst();
      hasSubs = true;
   }
   
   String address = "";
   if (null != rsStudentMarksSheet && rsStudentMarksSheet.first()) {
      parentEmailID = rsStudentMarksSheet.getString("stud_parent_email");
      studEmailID = rsStudentMarksSheet.getString("stud_email");
      address = rsStudentMarksSheet.getString("stud_address");
      hasStudRecords = true;
      rsStudentMarksSheet.beforeFirst();
   }

   ResultSet rsStudentAttSheet = (ResultSet) request.getAttribute("rsStudentAttSheet");

   if (null != rsStudentAttSheet && rsStudentAttSheet.first()) {
//      studRid = rsStudentAttSheet.getInt("stud_rid");
//      parentEmailID = rsStudentAttSheet.getString("stud_parent_email");
//      studEmailID = rsStudentAttSheet.getString("stud_email");
      hasStudAttRecords = true;
      rsStudentAttSheet.beforeFirst();
   }


%>
<div>
   <input type="hidden" id="successHandler" name="successHandler" value="email.handleSuccess">
   <input type="hidden" id="failureHandler" name="failureHandler" value="email.handleFailure">
   <fieldset>
      <legend>Student Summary [<b><%=examName + "/" + UDate.monthNames[monthNo] + "/" + sem + " Sem/" + batchName%></b>]</legend>
      <form id="frmStudentSummary" name="frmStudentSummary" action="<%= request.getContextPath()%>/StudentManagementServlet" method="POST" target="responseFrame">
         <input type="hidden" id="command" name="command" value="sendStudSummary">
         <input type="hidden" id="studRid" name="studRid" value="<%=studRid%>">
         <input type="hidden" id="examRid" name="examRid" value="<%=examRid%>">
         <input type="hidden" id="monthNo" name="monthNo" value="<%=monthNo%>">
         <input type="hidden" id="branchRid" name="branchRid" value="<%=branchRid%>">
         <input type="hidden" id="batchRid" name="batchRid" value="<%=batchRid%>">
         <input type="hidden" id="sem" name="sem" value="<%=sem%>">

         <% if (subjectCount > 0 && hasStudRecords) {%>
         <table width="100%">
            <tr class="specialRow">
               <td colspan="100%">
                  Marks Details
               </td>
            </tr>
            <tr>
               <td>
                  Reg. No.
               </td> 
               <% while (rsSubjects.next()) {%>
               <td>
                  <%= rsSubjects.getString("sub_name") + " (" + rsSubjects.getString("sub_code") + ")"%>
               </td>
               <%}%>
               <td>
                  Total
               </td>
            </tr>
            <%
               float marks = 0;
               if (rsStudentMarksSheet.first()) {

                  rsSubjects.beforeFirst();
                  float totMarks = 0;
            %>
            <tr class="oddRow" style="font-weight: bold">
               <td style="font-weight: bold">
                  <%= rsStudentMarksSheet.getString("stud_reg_no")%>
               </td>
               <%rsStudentMarksSheet.beforeFirst();
                  while (rsSubjects.next()) {
                     if (rsStudentMarksSheet.next()) {
                        marks = rsStudentMarksSheet.getFloat("sms_obtained_marks");
                     } else {
                        marks = 0;
                     }
                     totMarks += marks;
               %>
               <td>
                  <%=marks%>
               </td>
               <%}%>
               <td>
                  <%=totMarks%>
               </td>
            </tr>
            <%}%>
         </table>

         <%} else {
         %>
         <% if (!hasSubs) {%>
         <b><i>No Subjects are allotted for the selected branch and sem</i></b><br>
         <%}%>
         <% if (!hasStudRecords) {%>
         <b><i>No students are present in selected branch/sem/batch</i></b><br>
         <%}%>
         <%}%>
         <br>
         <hr class="thinLine">
         <br>
         <% if (subjectCount > 0 && hasStudAttRecords) {%>
         <table width="100%">
            <tr class="specialRow">
               <td colspan="100%">
                  Attendance Details (%)
               </td>
            </tr>
            <tr>
               <td>
                  Reg. No.
               </td> 
               <% rsSubjects.beforeFirst();
               while (rsSubjects.next()) {%>
               <td>
                  <%= rsSubjects.getString("sub_name") + " (" + rsSubjects.getString("sub_code") + ")"%>
               </td>
               <%}%>
            </tr>
            <%

               if (rsStudentAttSheet.first()) {
                  rsSubjects.beforeFirst();
                  float attendancePerc = 0;
            %>
            <tr class="oddRow" style="font-weight: bold">
               <td style="font-weight: bold">
                  <%= rsStudentAttSheet.getString("stud_reg_no")%>
               </td>
               <%
                  rsStudentAttSheet.beforeFirst();
                  while (rsSubjects.next()) {
                     if (rsStudentAttSheet.next()) {
                        attendancePerc = rsStudentAttSheet.getFloat("att_perc");
                     } else {
                        attendancePerc = 0;
                     }
               %>
               <td>
                  <%= UString.formatNumber(attendancePerc,2)%>
               </td>
               <%}%>
            </tr>
            <%}%>
         </table>

         <%} else {

         %>
         <% if (!hasSubs) {%>
         <b><i>No Subjects are allotted for the selected branch and sem</i></b><br>
         <%}%>
         <% if (!hasStudAttRecords) {%>
         <b><i>No students are present in selected branch/sem/batch</i></b><br>
         <%}%>
         <%}%>

         <% if (hasSubs && (hasStudRecords || hasStudAttRecords)) {%>
         <br>
         <hr class="thinLine">
         <% if( null != viewType && "post".equalsIgnoreCase(viewType)){%>
         <span>
            Parent Address :<br><b> 
            <%= address%>
            </b>
         </span>
         <%}else{%>
         <span>
            Parent Email ID :<b> <%= "".equals(parentEmailID.trim()) ? "Not Available" : parentEmailID%></b>
            &nbsp;
            <input type="checkbox" name="sendCopy" id="sendCopy" checked>
            <label for="sendCopy">Send copy to student</label>
            <input type="hidden" name="parentEmailID" id="parentEmailID" value="<%=parentEmailID%>">
            <input type="hidden" name="studEmailID" id="studEmailID" value="<%=studEmailID%>">
         </span>
         <br/>
         <span>
            
             <b>[SEND SMS] : </b>
             &nbsp;&nbsp;
             <input type="checkbox" name="sendMarks" id="sendMarks" value="1" >
             <label for="sendMarks">Send Marks</label>
             &nbsp;&nbsp;&nbsp;
             <input type="checkbox" name="sendAttendance" id="sendAttendance" value="1" >
             <label for="sendAttendance">Send Attendance</label>
         </span>
         <span id="sendEmailGIF" style="visibility: hidden;display: inline-block;border-style: solid;border-width: 1px;border-color: blue">
            <img src="<%= request.getContextPath()%>/images/common/send_mail.gif" height="30px" /> Sending please wait...
         </span>
         <br>
         <div class="buttonwrapper" style="float: right">
            <a class="ovalbutton" href="#" onclick="email.sendStudSummary()"><span>&nbsp;&nbsp;&nbsp;Send&nbsp;&nbsp;&nbsp;</span></a> 
         </div>
         <%}%>
         <%}%>

      </form>
   </fieldset>
   <iframe name="responseFrame" id="responseFrame" style="display: none"/>
</div>
