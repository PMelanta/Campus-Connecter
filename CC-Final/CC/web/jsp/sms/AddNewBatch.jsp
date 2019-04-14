<%-- 
    Document   : AddNewBatch
    Created on : Jan 14, 2016, 3:23:19 PM
    Author     : user
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.ResultSet"%>
<%
   ResultSet rsBranch = (ResultSet) request.getAttribute("rsBranchDet");
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
    <div style="width: 100%">
   <input type="hidden" id="successHandler" name="successHandler" value="branch.handleSuccess">
   <input type="hidden" id="failureHandler" name="failureHandler" value="branch.handleFailure">
    <form method="POST" action="<%= request.getContextPath()%>/BranchManagementServlet" 
         target="responseFrame">
       <input type="hidden" name="command" id="command" value="saveBatchDetails">
       
       <table width="500px" height="200px" style="margin: 10px">
         <tr class="specialRow">
            <td colspan="100%">
               BATCH MANAGEMENT
            </td>
         </tr>
         <tr>
            <td>
               Branch
            </td>
            <td>
               <select id="branchSel" name="branchSel" onchange="subject.loadSemesters(this.value)">
               <% while (null != rsBranch && rsBranch.next()) {%>
                  <option value="<%=rsBranch.getInt("branch_rid")%>">
                     <%=rsBranch.getString("branch_name") + " (" + rsBranch.getString("branch_code") + ")"%>
                  </option>
                  <%}%>
                </select> 
               <span class="userInfo">*</span>
            </td>
         </tr>
          <tr>
            <td>
               Batch Name
            </td>
            <td>
               <input type="text" name="batchName" id="batchName" value="">
            </td>
         </tr>
         <tr>
            <td colspan="2" align="right">
               <input type="submit" value="Save" > &nbsp;&nbsp;
               <input type="reset" value="Clear" >
            </td>
         </tr>
      </table>
    </form>
         <iframe id="responseFrame" name="responseFrame" class="hidden"></iframe>
  
    </div>
    </body>
</html>
