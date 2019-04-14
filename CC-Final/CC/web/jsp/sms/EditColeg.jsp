<%-- 
    Document   : EditColeg
    
--%>

<%@page import="java.sql.ResultSet"%>
<%
String colegName = "",colegCode = "",colegAddress = "",colegEmail = "",colegPhone = "";
int colegRid=0;
ResultSet rsColegDet = (ResultSet)request.getAttribute("rsColegDet");
if(null != rsColegDet && rsColegDet.first()){
   colegName = rsColegDet.getString("coleg_name");
   colegCode = rsColegDet.getString("coleg_code");
   colegAddress = rsColegDet.getString("coleg_address");
   colegEmail = rsColegDet.getString("coleg_email");
   colegPhone = rsColegDet.getString("coleg_phone");
   colegRid = rsColegDet.getInt("coleg_rid");
}
%>
<div style="width: 100%">
   <input type="hidden" id="successHandler" name="successHandler" value="coleg.handleSuccess">
   <input type="hidden" id="failureHandler" name="failureHandler" value="coleg.handleFailure">
   <form method="POST" action="<%= request.getContextPath()%>/AdminServlet" 
         target="responseFrame">
      <input type="hidden" id="command" name="command" value="saveColegDetails">
      <input type="hidden" id="colegRid" name="colegRid" value="<%=colegRid%>">
      <table width="500px" height="400px" style="margin: 10px">
         <tr class="specialRow">
            <td colspan="100%">
               COLLEGE MANAGEMENT
            </td>
         </tr>
         <tr>
            <td>
               College Name
            </td>
            <td>
               <input type="text" name="colegName" id="branchName" value="<%= colegName%>">
            </td>
         </tr>
         <tr>
            <td>
               College Code
            </td>
            <td>
               <input type="text" name="colegCode" id="branchCode" value="<%= colegCode%>">
            </td>
         </tr>
         <tr>
            <td>
               College Address
            </td>
            <td>
               <input type="text" name="colegAddress" id="colegAddress" value="<%= colegAddress%>">
            </td>
         </tr>
         <tr>
            <td>
               College E-mail
            </td>
            <td>
               <input type="text" name="colegEmail" id="colegEmail" value="<%= colegEmail%>">
            </td>
         </tr>
         <tr>
            <td>
               College Phone
            </td>
            <td>
               <input type="text" name="colegPhone" id="colegPhone" value="<%= colegPhone%>">
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