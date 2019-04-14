<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*" import="java.text.*" import="java.util.*" import="cc.base.*" errorPage="" %>
<%@ page pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<% URequestContext ctxt =(URequestContext) request.getAttribute("ctxt");
int hideSelectRoles = ctxt.getIntParameter("hideSelectRoles");%>
<div>
    <div id="workingDiv">
        <%String projPath=request.getContextPath();%>
        <!-- end -->
       <%-- <input type="hidden" id="successHandler" name="successHandler" value="configModule.configHandleSuccess"> --%>
       <input type="hidden" id="successHandler" name="successHandler" value="roleMaster.roleMasterHandleSuccess">
        <input type="hidden" id="failureHandler" name="failureHandler" value="configModule.configHandleFailure">
        <input type="hidden" id="reloadSuccessHandler" name="reloadSuccessHandler" value="configModule.configHandleSuccessReload">
        <input type="hidden" id="onLoadFunction" name="onLoadFunction" value="configModule.configInit(' Config : Roles')">
        <div id="searchDiv">
            <table id="searchTable" border="0" cellpadding="0" cellspacing="0" width="100%" >
<!--                <tr height="30px" class="wellHeader" > 
                    <td  align="right">
                    <span class="infoButton" title="Information" onclick="showInfoDiv(' To add a new Role, enter data and click on the Save button.To make changes to an existing Role, select a Role below, make changes,and then click on the Save button.\'*\' indicates mandatory fields.');">&nbsp;i&nbsp;</span>
                    
                </tr>-->
                <tr class="specialRow">
            <th align="left">
               &nbsp;Roles
            </th>
        </tr>
            </table>
        </div>
        <div style="width:100%;" class="boxShape">
            
            <form name="roleMasterForm" method="post" action="<%= projPath%>/UMasterServlet" target="entryResponseFrame"  onKeyPress="setDirtyBit();">
                
                <div style="width:100%; height:100%; overflow:auto;" >
                    
                    <input type="hidden" name="command" value="saveRoleWithMesaage">
                    
                    <%
                    int roleRID = 0;
                    String strRoleName = "";
                    boolean isActive = true;
                    
                    ResultSet rs = (ResultSet) request.getAttribute("roleDetail");
                    
                    if(rs != null) {
                    
                    rs.next();
                    
                    roleRID = rs.getInt("role_rid");
                    strRoleName = rs.getString("role_name");
                    isActive = rs.getInt("role_valid") == 1;
                    }
                    %>
                    
                    <input type="hidden" name="roleRID" value="<%= roleRID %>">
                   
                    
                    <table border="0" cellpadding="1" cellspacing="1" width="100%">
                        <tr height="30px" class=<%= hideSelectRoles == 1 ? "hidden" : "visible"%>>
                            <td class="myLabel" colspan="2">	
                                &nbsp;Select Role&nbsp;
                                <select style="width:350px" name="roleSelection" id="select" onChange="roleMaster.loadRole(this.value)">
                                    <option value="0">&nbsp;</option>
                                    <%
                                    ResultSet rolesRS = (ResultSet) request.getAttribute("roles");
                                    
                                    if(rolesRS != null) {
                                    
                                    while(rolesRS.next()) {
                                    if(rolesRS.getInt("role_rid")>0){
                                    %>
                                    <option value="<%= rolesRS.getInt("role_rid") %>"><%= rolesRS.getString("role_name")%></option>
                                    <%
                                    }
                                    }
                                    }
                                    %>
                                </select>
                            </td>
                        </tr>
                        
                        <tr>
                        </tr>
                        
                        <tr>
                            <td colspan="2" class=<%= hideSelectRoles == 1 ? "hidden" : "visible"%>><hr ></td>
                        </tr>
                        <tr>
                            <td class="myLabel">&nbsp;Role name&nbsp;
                                <input type="text" name="roleName" value="<%= strRoleName %>" maxlength="50"> 
                                <label id="roleNameMsg" class="userInfo"> *</label>
                            </td>
                        </tr>
                        <tr height="10px"><td></td></tr>
                        <tr class="specialRow">
                            <td class="myLabel">&nbsp;Feature List
                        &nbsp;&nbsp;&nbsp;Search&nbsp;&nbsp;<input type="text" id="searchRoles" name="searchRoles" size="25" maxlength="40" onkeypress="selectedRoles.getRoles(this, event)"></td>
                            <td class="myLabel">&nbsp; Accessible Features
                       <%-- &nbsp;&nbsp;&nbsp;Search&nbsp;&nbsp;<input type="text" id="searchAccessibleRoles" name="searchAccessibleRoles" size="25" maxlength="40" onkeypress="selectedRoles.getAccessibleRoles(this, event)"></td> --%>
                        </tr>
                        
                        <tr>
                           
                           
                            <td class="workTableBodyColor" width="50%">
                                 <div id="searchRolesDiv">
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
                                        //}
                                        
                                        }
                                        %>
                                    </table>
                                </div>
                                 </div> 
                            </td>
                            
                            <td class="workTableBodyColor" width="50%">
                                <div id="searchAccessibleRolesDiv">
                                <div style="overflow:auto; height:350px; valign:top; width:100%;">
                                    <table name="assignedFeatureTbl" id="assignedFeatureTbl" cellpadding="0" >
                                        <tr class="hidden">
                                            <td class="smallLabel">
                                                <input type="checkbox" name="assignedFeatureChk" >
                                                <input type="hidden" name="assignedFeature" value="0">
                                                <input type="text" name="assignedFeatureName" value="" size="40" readonly class="workTableBodyColor" style="border:0px;" >
                                            </td>
                                            
                                        </tr>
                                        <%
                                        ResultSet rs2 = (ResultSet) request.getAttribute("assignedFeatures");
                                        
                                        if(rs2 != null) {
                                        
                                        while(rs2.next()) {
                                        //if(rs2.getString("feature_group") == 0) {
                                        %>
                                        <tr>
                                            <td class="smallLabel">                               
                                                <input type="checkbox" name="assignedFeatureChk">
                                                <input type="hidden" name="assignedFeature" value="<%= rs2.getInt("feature_rid")%>">
                                                <%= rs2.getString("feature_name")%>
                                                <!-- <input type="text" name="assignedFeatureName"  class="workTableBodyColor" style="border:0px;" readonly value=""> -->
                                            </td>
                                        </tr>
                                        <%
                                        //}
                                        }
                                        }
                                        }
                                        %>
                                        
                                        
                                    </table>
                                </div>
                                </div>
                            </td>
                        
                             
                        </tr>
                       
                        <tr>
                            <td align="center">
                                <input type="button" name="addFeatureBtn" value="Add Selected" onClick="roleMaster.addFeatures()">
                            </td>
                            <td align="center">
                                <input type="button" name="removeFeatureBtn" value="Remove Selected" onClick="roleMaster.removeFeatures()">
                            </td>
                            
                        </tr>
                        
                        <tr>
                            <td colspan="2"><input type="checkbox" name="isActive" <%= isActive ? "checked" : ""%> > &nbsp;Is Active</td>
                        </tr>
                        
                        <tr>
                            <td colspan="2" width="100%"><hr /></td>
                        </tr>
                        
                        <tr>
                            <td colspan="2" align="right">
                                <input type="button" value="Save" id="saveBtn" name="save" onclick="roleMaster.submitRoleMasterForm()"> &nbsp;
                                <input type="button" value="Clear" id="clearBtn" onClick="roleMaster.clearFormRole(true)">
                                <input type="button" id="closeBtn" value="Close" onclick="desktop.closePopup()" accesskey="C">
                            </td>
                        </tr>
                        
                    </table>
                </div>
                <iframe id=entryResponseFrame name=entryResponseFrame src="" width="0px" height="0px"></iframe>
                
            </form>
        </div>
    </div>
</div>