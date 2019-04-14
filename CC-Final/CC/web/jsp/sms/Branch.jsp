<%-- 
    Document   : Branch
    
--%>

<%@page import="java.sql.ResultSet"%>
<%
   String projPath = request.getContextPath();
   ResultSet rsBranchDet = (ResultSet) request.getAttribute("rsBranchDet");
%>
<div>
   <input type="hidden" name="jsFile" id="jsFile" value="<%=request.getContextPath()%>/js/sms/Branch.js">
   <table width="100%" cellspacing="5" cellpadding="5">
      <tr class="specialRow">
         <td colspan="100%">
            Branch Details 
            <span style="float: right">
               <a href="#" onclick="branch.addNewBranch()">
                  Add New Branch
               </a>
                
                <a href="#" onclick="branch.addNewBatch()">
                    Add New Batch
                </a>
            </span>
         </td>
      </tr>
      <tr class="specialSubRow">
         <td>Branch Name</td>
         <td>Branch Code</td>
         <td>No. of Semesters</td>
         <td>Total Intake</td>
         <td>No of Internals/Sem</td>
         <td>Status</td>
         <td></td>
      </tr>
      <% int i = 0;
         String evenOdd = "";
         while (null != rsBranchDet && rsBranchDet.next()) {
            evenOdd = i++ % 2 == 0 ? "evenRow" : "oddRow";
      %>
      <tr class="<%=evenOdd%>">
         <td><%=rsBranchDet.getString("branch_name")%> </td>
         <td><%=rsBranchDet.getString("branch_code")%> </td>
         <td><%=rsBranchDet.getString("no_of_sems")%> </td>
         <td><%=rsBranchDet.getString("tot_intake")%> </td>
         <td><%=rsBranchDet.getString("no_of_internals")%> </td>
         <td><%=rsBranchDet.getInt("is_active") == 1 ? "Active" : "InActive"%> </td>
         <td>
            <span>
               <img src="<%= projPath%>/images/common/edit.png" height="15px" width="15px" title="Edit branch details"
                    onclick="branch.editBranchDetails('<%=rsBranchDet.getInt("branch_rid")%>')" style="cursor: pointer">
            </span>
            &nbsp;&nbsp;&nbsp;
            <span>
               <img src="<%= projPath%>/images/common/delete.png" height="15px" width="15px" title="Delete branch"
                    onclick="branch.deleteBranchDetails('<%=rsBranchDet.getInt("branch_rid")%>')" style="cursor: pointer">
            </span>
         </td>
      </tr>
      <% }%>
   </table>
</div>