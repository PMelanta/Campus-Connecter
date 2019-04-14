

<%@page import="cc.util.UDate"%>
<%@page import="java.sql.ResultSet"%>
<%
   ResultSet rsStudent = (ResultSet) request.getAttribute("rsStudentDetails");
   boolean hasRecords = false;
   int totalRecords = 0;
   if (null != rsStudent && rsStudent.first()) {
      hasRecords = true;
      rsStudent.last();
      totalRecords = rsStudent.getRow();
      rsStudent.beforeFirst();
   }
%>
<div>
   <fieldset>
      <legend>Search Results <%= totalRecords > 0 ? " (<b>" + totalRecords + "</b>)" : ""%></legend>
      <table width="100%" cellspacing="5" cellpadding="5">
          <tr class="specialSubRow">
              <td>Name</td>
              <td>Reg No</td>
              <td>Branch</td>
              <td>Batch</td>
              <td>DOB</td>
              <td>Fathers Name</td>
              <td>Address</td>
              <td>Contact</td>
              <td>Email</td>
              <td></td>
          </tr>
         <% 
         String className = ""; int i=0;
         while (null != rsStudent && rsStudent.next()) {
             className = (i++%2 == 0)?"evenRow":"oddRow";
         %>
         <tr class="<%=className%>">
             <td><%= rsStudent.getString("stud_first_name") +" "+rsStudent.getString("stud_last_name") %></td>
              <td><%= rsStudent.getString("stud_reg_no")%></td>
              <td><%= rsStudent.getString("branch_name")%></td>
              <td><%= rsStudent.getString("batch_name")%></td>
              <td><%= UDate.dbToDisplay(rsStudent.getString("stud_dob"))%></td>
              <td><%= rsStudent.getString("stud_fathers_name")%></td>
              <td><%= rsStudent.getString("stud_address")%></td>
              <td><%= rsStudent.getString("stud_parent_contact")%></td>
              <td><%= rsStudent.getString("stud_email")%></td>
            <td >
               
               <span>
                  <a href="#" onclick="student.editProfile('<%= rsStudent.getInt("stud_rid")%>')">Edit</a>
                  &nbsp;
                  <a href="#" onclick="student.deleteProfile('<%= rsStudent.getInt("stud_rid")%>')">Delete</a>
               </span>
            </td>
            
         <%} %>
         <% if (!hasRecords) {%>
         <tr>
            <td colspan="100%">
               <i><b>No records found! </b></i>
            </td>
         </tr>
         <%}%>
      </table>
   </fieldset>
</div>

