<%@page import="java.sql.ResultSet"%>
<%@page import="cc.branch.BranchManager"%>

<%
   int subjectCount = 0;
   int branchRid = Integer.parseInt(request.getAttribute("branchRid").toString());
   int sem = Integer.parseInt(request.getAttribute("sem").toString());
   int examRid = Integer.parseInt(request.getAttribute("examRid").toString());
   boolean hasStudRecords = false;
   boolean hasSubs = false;
   ResultSet rsSubjects = (ResultSet) request.getAttribute("rsSubjects");
   ResultSet rsStudentMarksSheet = (ResultSet) request.getAttribute("rsStudentMarksSheet");
   if (null != rsSubjects && rsSubjects.first()) {
      rsSubjects.last();
      subjectCount = rsSubjects.getRow();
      rsSubjects.beforeFirst();
      hasSubs = true;
   }
   if (null != rsStudentMarksSheet && rsStudentMarksSheet.first()) {
      hasStudRecords = true;
      rsStudentMarksSheet.beforeFirst();
   }
   int examType = Integer.parseInt(request.getAttribute("examType").toString());
   String examName = request.getAttribute("examName").toString();
   
%>
<div>
   <input type="hidden" id="successHandler" name="successHandler" value="studPerformance.handleSuccess">
   <input type="hidden" id="failureHandler" name="failureHandler" value="studPerformance.handleFailure">
   <fieldset>
      <legend>Marks Sheet [<b><%=examName%></b>]</legend>
      <form action="<%= request.getContextPath()%>/StudPerformanceServlet" method="POST" target="responseFrame">
         <input type="hidden" id="command" name="command" value="saveMarksSheet">
         <input type="hidden" id="branchRid" name="branchRid" value="<%=branchRid%>">
         <input type="hidden" id="sem" name="sem" value="<%=sem%>">
         <input type="hidden" id="examRid" name="examRid" value="<%=examRid%>">
         <input type="hidden" id="examType" name="examType" value="<%=examType%>">
      
   <% if (subjectCount > 0 && hasStudRecords) {%>
   <table width="100%">
      <tr class="specialRow">
         <td>
            Reg. No.
         </td> 
         <% while (rsSubjects.next()) {%>
         <td>
            <%= rsSubjects.getString("sub_name") + " (" + rsSubjects.getString("sub_code") + ")"%> <br>
            Max:<%= examType == 1?rsSubjects.getInt("sub_internal_max"):rsSubjects.getInt("sub_external_max")%>&nbsp;
            Min: <%= examType == 1?rsSubjects.getInt("sub_internal_min"):rsSubjects.getInt("sub_external_min")%> <br>
         </td>
         <%}%>
      </tr>
      <%
         float marks = 0;
         //boolean firstLoop = true;
         int rowPtr = 1;
         String className = "";
         if (rsStudentMarksSheet.first()) {
            rsStudentMarksSheet.beforeFirst();
            while (rsStudentMarksSheet.next()) {
               rsSubjects.beforeFirst();
//               if (!firstLoop) {
//                  rsStudentMarksSheet.previous();
//               }
//               firstLoop = false;
               className = rowPtr++%2 == 0?"evenRow":"oddRow";
               int prevStudRid = rsStudentMarksSheet.getInt("stud_rid");
               int curStudRid = rsStudentMarksSheet.getInt("stud_rid");
      %>
      <tr class="<%=className%>">
         <td style="font-weight: bold">
            <%= rsStudentMarksSheet.getString("stud_reg_no")%>
            <input type="hidden" name="studRid" id="studRid" value="<%= rsStudentMarksSheet.getInt("stud_rid")%>">
         </td>
         <%while (rsSubjects.next()) {%>
         <% if (prevStudRid == curStudRid) {
               marks = rsStudentMarksSheet.getFloat("sms_obtained_marks");
         %>
         <td>
            <input type="text" size="6" class="textBoxBG" onkeypress="editKeyBoard(event, this,keybDecimal);" name="sub<%=rsSubjects.getInt("sub_seq")%>" id="sub<%=rsSubjects.getInt("sub_seq")%>" value="<%=marks == 0?"":marks%>">
         </td>
         <%
            prevStudRid = rsStudentMarksSheet.getInt("stud_rid");
            if (prevStudRid == curStudRid && rsStudentMarksSheet.next()) {
               curStudRid = rsStudentMarksSheet.getInt("stud_rid");
               if(prevStudRid != curStudRid){rsStudentMarksSheet.previous();}
            } else {
               curStudRid = -1;
            }
         } else {%>
         <td>
            <input type="text" class="textBoxBG" size="6" onkeypress="editKeyBoard(event, this,keybDecimal);" name="sub<%=rsSubjects.getInt("sub_seq")%>" id="sub<%=rsSubjects.getInt("sub_seq")%>" value="">
         </td>
         <%}%>
         <%}%>
      </tr>
      <%}%>
      <%}%>
      <tr>
         <td colspan="100%" align="right">
            <input type="submit" value="Update MarksSheet"> &nbsp;&nbsp;&nbsp;
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
