

<%@page import="cc.util.UDate"%>
<%@page import="java.sql.ResultSet"%>
<%
   ResultSet rs = (ResultSet) request.getAttribute("rs");
   boolean hasRecords = false;
   int totalRecords = 0;
   if (null != rs && rs.first()) {
      hasRecords = true;
      rs.last();
      totalRecords = rs.getRow();
      rs.beforeFirst();
   }
%>
<div>
   <fieldset>
      <legend>Student Complaints <%= totalRecords > 0 ? " (<b>" + totalRecords + "</b>)" : ""%></legend>
      <table width="100%" cellspacing="5" cellpadding="5">
          <tr class="specialSubRow">
              <td>Subject</td>
              <td>Raised by</td>
              <td>Date</td>
              <td>Details</td>
         <% 
         String className = ""; int i=0;
         while (null != rs && rs.next()) {
             className = (i++%2 == 0)?"evenRow":"oddRow";
         %>
         <tr class="<%=className%>">
              <td><%= rs.getString("com_sub")%></td>
              <td><%= rs.getString("stud_first_name") +" "
                      +rs.getString("stud_last_name")+" ["
                      +rs.getString("stud_reg_no")+"]" %></td>
              <td><%= UDate.dbToDisplay(rs.getString("com_datetime"))%></td>
              <td><%= rs.getString("com_details")%></td>
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

