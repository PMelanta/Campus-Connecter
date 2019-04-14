<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*" import="java.text.*" import="java.util.*" import="cc.base.*" errorPage="" %>
<%@ page pageEncoding="UTF-8"%>

<%String projPath=request.getContextPath();%>
<input type="hidden" name="jsFile" value="<%=projPath %>/js/config/UserMaster.js">
<input type="hidden" name="jsFile" value="<%=projPath %>/js/config/RoleMaster.js">
<input type="hidden" name="jsFile" value="<%=projPath %>/js/config/Config.js">

        <input type="hidden" name="jsFile" value="<%=projPath %>/js/config/SearchRoles.js">
       
<div>
    <table width="100%" cellpadding="1" cellspacing="1">
        
        
        <tr>
            
            <td>
                <div style="overflow:auto; height:350px; valign:top; width:100%;">
                    <table name="productFeatureTbl" cellpadding="0" >
                        <%
                        ResultSet rs1 = (ResultSet) request.getAttribute("productFeatures");
                        
                        if(rs1 != null) {
                            
                            while(rs1.next()) {
                                //if(rs1.getInt("feature_group") == 0) {
                        %>
                        <tr>
                            <td class="smallLabel">
                                
                                <input type="checkbox" name="productFeature" value="<%= rs1.getInt("feature_rid")%>"><%= rs1.getString("feature_name")%>
                                <input type="hidden" name="productFeatureName" value="<%= rs1.getString("feature_name")%>">
                            </td>
                        </tr>
                        <%
                            }
                            }
                        %>
                    </table>
                </div>
            </td>
        </tr>
    </table>
</div>