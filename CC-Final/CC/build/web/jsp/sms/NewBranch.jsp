<%-- 
    Document   : NewBranch
    
--%>

<%@page import="java.sql.ResultSet"%>
<%
String branchName = "",branchCode = "";
int noOfSems=0,totIntake=0,noOfInternals=0,isActive=1,branchRid=0;
ResultSet rsBranchDet = (ResultSet)request.getAttribute("rsBranchDet");
if(null != rsBranchDet && rsBranchDet.first()){
   branchName = rsBranchDet.getString("branch_name");
   branchCode = rsBranchDet.getString("branch_code");
   noOfSems = rsBranchDet.getInt("no_of_sems");
   totIntake = rsBranchDet.getInt("tot_intake");
   noOfInternals = rsBranchDet.getInt("no_of_internals");
   isActive = rsBranchDet.getInt("is_active");
   branchRid = rsBranchDet.getInt("branch_rid");
}
%>
<div style="width: 100%">
   <input type="hidden" id="successHandler" name="successHandler" value="branch.handleSuccess">
   <input type="hidden" id="failureHandler" name="failureHandler" value="branch.handleFailure">
   <form method="POST" action="<%= request.getContextPath()%>/BranchManagementServlet" 
         target="responseFrame">
      <input type="hidden" id="command" name="command" value="saveBranchDetails">
      <input type="hidden" id="branchRid" name="branchRid" value="<%=branchRid%>">
      <table width="500px" height="400px" style="margin: 10px">
         <tr class="specialRow">
            <td colspan="100%">
               BRANCH MANAGEMENT
            </td>
         </tr>
         <tr>
            <td>
               Branch Name
            </td>
            <td>
               <input type="text" name="branchName" id="branchName" value="<%= branchName%>">
            </td>
         </tr>
         <tr>
            <td>
               Branch Code
            </td>
            <td>
               <input type="text" name="branchCode" id="branchCode" value="<%= branchCode%>">
            </td>
         </tr>
         <tr>
            <td>
               No. of semesters
            </td>
            <td>
               <input type="text" name="totSems" id="totSems" value="<%= noOfSems == 0?"":noOfSems%>" onkeypress="editKeyBoard(event, this,keybNumber);">
            </td>
         </tr>
         <tr>
            <td>
               Total Intake
            </td>
            <td>
               <input type="text" name="totIntake" id="totIntake" value="<%= totIntake == 0?"":totIntake%>" onkeypress="editKeyBoard(event, this,keybNumber);">
            </td>
         </tr>
         <tr>
            <td>
               No. of internals/Sem.
            </td>
            <td>
               <input type="text" name="noOfInternals" id="noOfInternals" value="<%= noOfInternals == 0?"":noOfInternals%>" onkeypress="editKeyBoard(event, this,keybNumber);">
            </td>
         </tr>
         <tr>
            <td colspan="2">
               <input type="checkbox" name="isActive" id="isActive" <%= isActive == 1?"checked":""%>>
               <label for="isActive">Is Active</label>
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