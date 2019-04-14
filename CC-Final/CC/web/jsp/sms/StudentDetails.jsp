<%-- 
    Document   : StudentDetails
    Author     : suhas
--%>

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
         <% while (null != rsStudent && rsStudent.next()) {%>
         <tr>
            <td class="details" width="33%">
               <img src="<%= (rsStudent.getString("stud_image_url") == null|| "".equals(rsStudent.getString("stud_image_url").trim()) ? request.getContextPath() + "/images/common/user.jpg" : rsStudent.getString("stud_image_url"))%>" 
                    width="60px" height="60px" style="padding-right: 4px;padding-bottom: 4px;float: left;"/>
               <span style="float: left;font-weight: bold;">
                  <%= rsStudent.getString("stud_first_name") + " " + rsStudent.getString("stud_last_name") +" ["+rsStudent.getString("batch_name")+"]"%>
               </span>
               <br />
               <span style="float: left">
                  <%= rsStudent.getString("branch_name") + " (" + rsStudent.getString("stud_cur_sem") + " sem)"%>
               </span>
               <br />
               <span style="float: left">
                  <%= rsStudent.getString("stud_reg_no")%>
               </span>
               <br />
               <span>
                  <a href="#" onclick="student.viewProfile('<%= rsStudent.getInt("stud_rid")%>')">View</a>
                  &nbsp;
                  <a href="#" onclick="student.editProfile('<%= rsStudent.getInt("stud_rid")%>')">Edit</a>
                  &nbsp;
                  <a href="#" onclick="student.deleteProfile('<%= rsStudent.getInt("stud_rid")%>')">Delete</a>
               </span>
            </td>
            <% if (rsStudent.next()) {%>
            <td class="details"  width="33%">
               <img src="<%= (rsStudent.getString("stud_image_url") == null|| "".equals(rsStudent.getString("stud_image_url").trim()) ? request.getContextPath() + "/images/common/user.jpg" : rsStudent.getString("stud_image_url"))%>" 
                    width="60px" height="60px" style="padding-right: 4px;padding-bottom: 4px;float: left;"/>
               <span style="float: left;font-weight: bold;">
                  <%= rsStudent.getString("stud_first_name") + " " + rsStudent.getString("stud_last_name")+" ["+rsStudent.getString("batch_name")+"]"%>
               </span>
               <br />
               <span style="float: left">
                  <%= rsStudent.getString("branch_name") + " (" + rsStudent.getString("stud_cur_sem") + " sem)"%>
               </span>
               <br />
               <span style="float: left">
                  <%= rsStudent.getString("stud_reg_no")%>
               </span>
               <br />
               <span>
                  <a href="#" onclick="student.viewProfile('<%= rsStudent.getInt("stud_rid")%>')">View</a>
                  &nbsp;
                  <a href="#" onclick="student.editProfile('<%= rsStudent.getInt("stud_rid")%>')">Edit</a>
                  &nbsp;
                  <a href="#" onclick="student.deleteProfile('<%= rsStudent.getInt("stud_rid")%>')">Delete</a>
               </span>
            </td>
            <%} else {%>
            <td>
            </td>
         </tr>
         <%}%>
         <% if (rsStudent.next()) {%>
         <td class="details"  width="33%">
            <img src="<%= (rsStudent.getString("stud_image_url") == null|| "".equals(rsStudent.getString("stud_image_url").trim()) ? request.getContextPath() + "/images/common/user.jpg" : rsStudent.getString("stud_image_url"))%>" 
                 width="60px" height="60px" style="padding-right: 4px;padding-bottom: 4px;float: left;"/>
            <span style="float: left;font-weight: bold;">
               <%= rsStudent.getString("stud_first_name") + " " + rsStudent.getString("stud_last_name")+" ["+rsStudent.getString("batch_name")+"]"%>
            </span>
            <br />
            <span style="float: left">
               <%= rsStudent.getString("branch_name") + " (" + rsStudent.getString("stud_cur_sem") + " sem)"%>
            </span>
            <br />
            <span style="float: left">
               <%= rsStudent.getString("stud_reg_no")%>
            </span>
            <br />
            <span>
               <a href="#" onclick="student.viewProfile('<%= rsStudent.getInt("stud_rid")%>')">View</a>
               &nbsp;
               <a href="#" onclick="student.editProfile('<%= rsStudent.getInt("stud_rid")%>')">Edit</a>
               &nbsp;
               <a href="#" onclick="student.deleteProfile('<%= rsStudent.getInt("stud_rid")%>')">Delete</a>
            </span>
         </td>
         <%} else {%>
         <td>
         </td>
         </tr>
         <%}%>
         <%}%>
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

