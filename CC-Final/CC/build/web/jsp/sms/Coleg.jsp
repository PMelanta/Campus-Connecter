<%-- 
    Document   : Coleg
    
--%>

<%@page import="java.sql.ResultSet"%>
<%
   String projPath = request.getContextPath();
   ResultSet rsColegDet = (ResultSet) request.getAttribute("rsColegDet");
%>
<div>
    <input type ="hidden" name ="jsFile" value="<%=request.getContextPath()%>/js/sms/Coleg.js">
    <table width="100%" cellspacing="5" cellpadding="5">
       <tr class="specialRow">
         <td>College Name</td>
         <td>College Code</td>
         <td>College Address</td>
         <td>College E-mail</td>
         <td>College Phone</td>
         <td></td>
      </tr>
      <% while (null != rsColegDet && rsColegDet.next()) {
            //evenOdd = i++ % 2 == 0 ? "evenRow" : "oddRow";
      %>
      <tr>
         <td><%=rsColegDet.getString("coleg_name")%> </td>
         <td><%=rsColegDet.getString("coleg_code")%> </td>
         <td><%=rsColegDet.getString("coleg_address")%> </td>
         <td><%=rsColegDet.getString("coleg_email")%> </td>
         <td><%=rsColegDet.getString("coleg_phone")%> </td>
         <td>
            <span>
               <img src="<%=projPath%>/images/common/edit.png" height="15px" width="15px" title="Edit Coleg Details"
                    onclick="coleg.editColegDetails('<%=rsColegDet.getInt("coleg_rid")%>')" style="cursor: pointer">
            </span>
            &nbsp;&nbsp;&nbsp;
            </td>
      </tr>
      <% }%>
   </table>    
</div>