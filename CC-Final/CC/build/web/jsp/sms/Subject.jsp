<%-- 
    Document   : Subject
    
--%>

<%@page import="java.sql.ResultSet"%>
<%
   String projPath = request.getContextPath();
   ResultSet rsSubjectDet = (ResultSet) request.getAttribute("rsSubjectDet");
%>
<div>
   <input type="hidden" name="jsFile" id="jsFile" value="<%=request.getContextPath()%>/js/sms/Subject.js">
   <table width="100%" cellspacing="5" cellpadding="5">
      <tr class="specialRow">
         <td colspan="100%">
            Subject Details 
            <span style="float: right">
               <a href="#" onclick="subject.addNewSubject()">
                  Add New Subject
               </a>
            </span>
         </td>
      </tr>
      <tr class="specialSubRow">
         <td>Subject Name</td>
         <td>Subject Code</td>
         <td>Branch</td>
         <td>Sem</td>
         <td>Max. Internal</td>
         <td>Min. Internal</td>
         <td>Max. External</td>
         <td>Min. External</td>
         <td>Sequence</td>
         <td>Status</td>
         <td></td>
      </tr>
      <% int i = 0;
         String evenOdd = "";
         while (null != rsSubjectDet && rsSubjectDet.next()) {
            evenOdd = i++ % 2 == 0 ? "evenRow" : "oddRow";
      %>
      <tr class="<%=evenOdd%>">
         <td><%=rsSubjectDet.getString("sub_name")%> </td>
         <td><%=rsSubjectDet.getString("sub_code")%> </td>
         <td><%=rsSubjectDet.getString("branch_name") +" ("+rsSubjectDet.getString("branch_code")+")"%> </td>
         <td><%=rsSubjectDet.getString("sub_sem")%> </td>
         <td><%=rsSubjectDet.getString("sub_internal_max")%> </td>
         <td><%=rsSubjectDet.getString("sub_internal_min")%> </td>
         <td><%=rsSubjectDet.getString("sub_external_max")%> </td>
         <td><%=rsSubjectDet.getString("sub_external_min")%> </td>
         <td><%=rsSubjectDet.getInt("sub_seq")%> </td>
         <td><%=rsSubjectDet.getInt("is_active") == 1 ? "Active" : "InActive"%> </td>
         <td>
            <span>
               <img src="<%= projPath%>/images/common/edit.png" height="15px" width="15px" title="Edit subject details"
                    onclick="subject.editSubjectDetails('<%=rsSubjectDet.getInt("sub_rid")%>')" style="cursor: pointer">
            </span>
            &nbsp;&nbsp;&nbsp;
            <span>
               <img src="<%= projPath%>/images/common/delete.png" height="15px" width="15px" title="Delete subject"
                    onclick="subject.deleteSubjectDetails('<%=rsSubjectDet.getInt("sub_rid")%>')" style="cursor: pointer">
            </span>
         </td>
      </tr>
      <% }%>
   </table>
</div>