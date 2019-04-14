<%-- 
    Document   : StudentAttendancetRW
    
--%>

<%@page import="java.sql.ResultSet"%>
<%@page import="cc.util.UDate"%>

<%
   int subjectCount = 0;
   int branchRid = Integer.parseInt(request.getAttribute("branchRid").toString());
   int batchRid = Integer.parseInt(request.getAttribute("batchRid").toString());
   String batchName = request.getAttribute("batchName").toString();
   int monthNo = Integer.parseInt(request.getAttribute("monthNo").toString());
   int sem = Integer.parseInt(request.getAttribute("sem").toString());
   
   boolean hasStudRecords = false;
   boolean hasSubs = false;
   
   ResultSet rsSubjects = (ResultSet) request.getAttribute("rsSubjects");
   ResultSet rsStudentAttSheet = (ResultSet) request.getAttribute("rsStudentAttSheet");
   ResultSet rsTotClasses = (ResultSet) request.getAttribute("rsTotClasses");
   if (null != rsSubjects && rsSubjects.first()) {
      rsSubjects.last();
      subjectCount = rsSubjects.getRow();
      rsSubjects.beforeFirst();
      hasSubs = true;
   }
   
   if (null != rsStudentAttSheet && rsStudentAttSheet.first()) {
      hasStudRecords = true;
      rsStudentAttSheet.beforeFirst();
   }
   
%>
<div>
   <input type="hidden" id="successHandler" name="successHandler" value="attendance.handleSuccess">
   <input type="hidden" id="failureHandler" name="failureHandler" value="attendance.handleFailure">
   <fieldset>
      <legend>Attendance Sheet [<b><%=batchName%>/<%=sem%>&nbsp;Sem/<%=UDate.monthNames[monthNo]%></b>]</legend>
      <form action="<%= request.getContextPath()%>/AttendanceServlet" method="POST" target="responseFrame">
         <input type="hidden" id="command" name="command" value="saveAttendanceSheet">
         <input type="hidden" id="branchRid" name="branchRid" value="<%=branchRid%>">
         <input type="hidden" id="batchRid" name="batchRid" value="<%=batchRid%>">
         <input type="hidden" id="sem" name="sem" value="<%=sem%>">
         <input type="hidden" id="monthNo" name="monthNo" value="<%=monthNo%>">
      
   <% if (subjectCount > 0 && hasStudRecords) {%>
   <table width="100%">
      <tr class="specialRow">
         <td>
            Reg. No.
         </td> 
         <% while (rsSubjects.next()) {%>
         <td>
            <%= rsSubjects.getString("sub_name") + " (" + rsSubjects.getString("sub_code") + ")"%> <br>
         </td>
         <%}%>
      </tr>
      <tr class="oddRow" style="background-color: gray;color: white">
         <td style="font-weight: bold">
            Total Classes
         </td> 
         <% while (rsTotClasses.next()) {%>
         <td>
            <input type="text" size="6" class="textBoxBG" onkeypress="editKeyBoard(event, this,keybNumber);" name="totSub<%=rsTotClasses.getInt("sub_seq")%>" id="totSub<%=rsTotClasses.getInt("sub_seq")%>" value="<%=rsTotClasses.getInt("atc_tot_class")%>">
         </td>
         <%}%>
      </tr>
      <%
         int attendance = 0;
         int rowPtr = 0;
         String className = "";
         if (rsStudentAttSheet.first()) {
            rsStudentAttSheet.beforeFirst();
            while (rsStudentAttSheet.next()) {
               rsSubjects.beforeFirst();
               className = rowPtr++%2 == 0?"evenRow":"oddRow";
               int prevStudRid = rsStudentAttSheet.getInt("stud_rid");
               int curStudRid = rsStudentAttSheet.getInt("stud_rid");
      %>
      <tr class="<%=className%>">
         <td style="font-weight: bold">
            <%= rsStudentAttSheet.getString("stud_reg_no")%>
            <input type="hidden" name="studRid" id="studRid" value="<%= rsStudentAttSheet.getInt("stud_rid")%>">
         </td>
         <%while (rsSubjects.next()) {%>
         <% if (prevStudRid == curStudRid) {
               attendance = rsStudentAttSheet.getInt("att_tot_present");
         %>
         <td>
            <input type="text" size="6" class="textBoxBG" onkeypress="editKeyBoard(event, this,keybNumber);" name="sub<%=rsSubjects.getInt("sub_seq")%>" id="sub<%=rsSubjects.getInt("sub_seq")%>" value="<%=attendance == 0?"":attendance%>">
         </td>
         <%
            prevStudRid = rsStudentAttSheet.getInt("stud_rid");
            if (prevStudRid == curStudRid && rsStudentAttSheet.next()) {
               curStudRid = rsStudentAttSheet.getInt("stud_rid");
               if(prevStudRid != curStudRid){rsStudentAttSheet.previous();}
            } else {
               curStudRid = -1;
            }
         } else {%>
         <td>
            <input type="text" class="textBoxBG" size="6" onkeypress="editKeyBoard(event, this,keybNumber);" name="sub<%=rsSubjects.getInt("sub_seq")%>" id="sub<%=rsSubjects.getInt("sub_seq")%>" value="">
         </td>
         <%}%>
         <%}%>
      </tr>
      <%}%>
      <%}%>
      <tr>
         <td colspan="100%" align="right">
            <input type="submit" value="Update Attendance"> &nbsp;&nbsp;&nbsp;
            <input type="reset" value="Reset">
         </td>
      </tr>
   </table>
    <%} else {
      
   %>
   <% if(!hasSubs){%>
   <b><i>No Subjects are allotted for the selected branch and sem</i></b><br>
   <%}%>
   <% if(!hasStudRecords){%>
   <b><i>No students are present in selected branch/sem/batch</i></b><br>
   <%}%>
   <%}%>
   </form>
</fieldset>
<iframe name="responseFrame" id="responseFrame" style="display: none"/>
</div>
