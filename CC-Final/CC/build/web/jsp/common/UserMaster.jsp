<%@page import="cc.util.UString"%>
<%@ page contentType="text/html; charset=UTF-8" language="java" import="java.sql.*" import="java.util.*" import="cc.base.*" errorPage="" %>
<%@page pageEncoding="UTF-8"%>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%
    String projPath = request.getContextPath();
    URequestContext ctxt = (URequestContext) request.getAttribute("ctxt");
%>
<div>
    <div id="workingDiv" >
       <input type="hidden" id="jsFile" name="jsFile" value="<%=request.getContextPath()%>/js/config/UserMaster.js">
       <input type="hidden" id="jsFile" name="jsFile" value="<%=request.getContextPath()%>/js/config/Config.js">
        <input type="hidden" id="successHandler" name="successHandler" value="configModule.configHandleSuccess">
        <input type="hidden" id="failureHandler" name="failureHandler" value="configModule.configHandleFailure">
        <input type="hidden" id="reloadSuccessHandler" name="reloadSuccessHandler" value="configModule.configHandleSuccessReload">
        <div id="searchDiv">
            <table id="searchTable" border="0" cellpadding="0" cellspacing="0" width="100%" >
                <!--                <tr height="30px" class="wellHeader"> 
                                    <td  align="right">
                                        <span class="infoButton" title="Information" onclick="showInfoDiv(' To make changes to an existing User record,search for the User, make changes,and then click on the Save button.\'*\' indicates mandatory fields.');">&nbsp;i&nbsp;</span>
                                    </td>
                                </tr>-->
                <tr class="specialRow">
                    <th align="left">
                        &nbsp;Users
                    </th>
                </tr>
            </table>
        </div>
        <div class="boxShape">
            <form name="userMasterForm" method="post" action="<%= projPath%>/UMasterServlet" 
                  target="entryResponseFrame" onSubmit="return userMaster.formValidateUser();">
                <%
                    int userRID = 0;
                    int userProjectRID = 0;
                    String userFullName = "";
                    String userLoginId = "";
                    String emailId = "";
                    boolean isActive = true;
                    boolean isUserCommonUser = false;

                    ResultSet rs = (ResultSet) request.getAttribute("userDetails");

                    if (rs != null) {

                        rs.next();

                        userRID = rs.getInt("user_rid");
                        //userProjectRID = rs.getInt("user_entity_rid");
                        userFullName = rs.getString("user_full_name");
                        userLoginId = rs.getString("user_id");
                        if (userLoginId == null || userLoginId.trim().equalsIgnoreCase("null")) {
                            userLoginId = "";
                        }
                        emailId = rs.getString("user_email");
                        if (emailId == null || emailId.equalsIgnoreCase("null")) {
                            emailId = "";
                        }

                        isActive = rs.getInt("user_valid") == 1;

                    }

                %>
                <table bgcolor="FFFFFF" width="100%" border="0" cellspacing="1" cellpadding="1">
                    <tr>
                        <td>
                            <input type="hidden" name="command" value="saveSysUser">
                            <input type="hidden" name="userEntityRID" value="0">
                            <input type="hidden" name="userRID" value="<%= userRID%>">
                            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td class="myLabel" width="25%">
                                        &nbsp;Select User
                                    </td>  
                                    <td class="myLabel">
                                        <%
                                            ResultSet userRS = (ResultSet) request.getAttribute("sysUsers");
                                        %>
                                        <select style="width:200px" name="userSelection" id="userSelection" onChange="userMaster.loadUserDetails(this.value)">
                                            <option value="0">&nbsp;</option>
                                            <%
                                                while (userRS.next()) {
                                            %>       
                                            <option value="<%=userRS.getInt("USER_RID")%>"><%=userRS.getString("user_full_name")%>
                                                <%   }%>
                                        </select>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2"><hr /></td>
                    </tr>
                    <%
                        if (userRID == 0) {
                    %>
                    <tr>
                        <td colspan="2" class="myLabel">
                            &nbsp;To add a new User, enter data below and click on the "Save" button.<br>
                            <br>
                        </td>
                    </tr>
                    <%        }
                    %>
                    <tr>
                        <td colspan="2" class="myLabel">
                            <!-- &nbsp;To grant access to an employee,click on search button ("...") and select from the list<br>--><br>
                        </td>
                    </tr>

                    <tr><td colspan="2">
                            <table width="100%" border="0" cellpadding="0" >
                                <tr>
                                    <td class="myLabel" width="25%">&nbsp;User name</td>
                                    <td class="myLabel"><input type="text" name="userName" maxlength="50" value="<%= userFullName%>"><label class="userInfo" id="userFullNameMsg">*</label>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="myLabel">&nbsp;User Login ID

                                    </td>
                                    <td class="myLabel"> <input type="text" name="userLoginId"  maxlength="10" value="<%= userLoginId%>">
                                        <label  class="userInfo" id="userIdMsg">*</label></td>
                                </tr>

                                <tr>
                                    <td class="myLabel">&nbsp;Email ID</td>
                                    <td class="myLabel"> <input type="text" name="userEmailId" maxlength="50" value="<%= emailId%>"><label class="userInfo" id="userEmailIdMsg"></label></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr><td colspan="2">&nbsp;</tr>

                    <tr class="specialRow">
                        <td class="myLabel" width="50%">&nbsp;All Roles</td>
                        <td class="myLabel">Assigned Roles <label class="userInfo" id="userRolesMsg">*</label></td>
                    </tr>
                    <tr>
                        <td class="workTableBodyColor">
                            <div style="overflow:auto; height:200px; valign:top; width:100%;">
                                <table name="systemRolesTbl" cellpadding="0" cellspacing="0" border="0" >
                                    <%
                                        ResultSet rs1 = (ResultSet) request.getAttribute("roles");

                                        if (rs1 != null) {

                                            while (rs1.next()) {

                                                if (rs1.getInt("role_rid") > 0) {
                                    %>
                                    <tr>
                                        <td class="myLabel" width="25%">
                                            <input type="checkbox" name="role" value="<%= rs1.getInt("role_rid")%>"><%= rs1.getString("role_name")%>
                                            <input type="hidden" name="roleName" value="<%= rs1.getString("role_name")%>">
                                        </td>
                                    </tr>
                                    <%
                                                }
                                            }
                                        }
                                    %>
                                </table>
                            </div>
                        </td>
                        <td class="workTableBodyColor">
                            <div style="overflow:auto; height:200px; valign:top; width:100%;">
                                <table name="assignedRolesTbl" id="assignedRolesTbl" cellpadding="0" >
                                    <tr class="hidden">
                                        <td >
                                            <input type="checkbox" name="assignedRoleChk" >
                                            <input type="hidden" name="assignedRole" value="0">
                                            <input type="text" name="assignedRoleName" readonly value="" class="workTableBodyColor" style="border:0px;" >
                                        </td>
                                    </tr>
                                    <%
                                        ResultSet rs2 = (ResultSet) request.getAttribute("userRoles");

                                        if (rs2 != null) {

                                            while (rs2.next()) {
                                    %>
                                    <tr>
                                        <td class="myLabel">
                                            <input type="checkbox" name="assignedRoleChk">
                                            <input type="hidden" name="assignedRole" value="<%= rs2.getInt("role_rid")%>">
                                            <input type="text" name="assignedRoleName" class="workTableBodyColor" style="border:0px;" readonly value="<%= rs2.getString("role_name")%>">
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

                    <tr>
                        <td align="center">
                            <input type="button" name="addRoleBtn" value="Add Selected" onClick="userMaster.addRolesUser()">
                        </td>
                        <td align="center">
                            <input type="button" name="removeRoleBtn" value="Remove Selected" onClick="userMaster.removeRoles()">
                        </td>

                    </tr>

                    <tr><td colspan="2">&nbsp;</tr>
                    <%
                        if (userRID > 0) {
                    %>
                    <tr>
                        <td colspan="1" class="myLabel">
                            <input type="checkbox" name="emptyPassword" id="resetPasswordChckBox"> &nbsp;
                            <label for="resetPasswordChckBox">Reset password</label>
                        </td>
                    </tr>
                    <%                        }
                    %>
                    <tr>
                        <td colspan="2" class="myLabel"><input type="checkbox" name="isActive" <%= isActive ? "checked=\"checked\"" : ""%>> &nbsp;Is Active</td>
                    </tr>

                    <%--                    <tr>
                                            <td colspan="2" class="myLabel"><input type="checkbox" name="isCommonUser" <%= isUserCommonUser ? "checked" : ""%>> &nbsp;Is Common User</td>
                                        </tr>
                    --%>

                    <tr>
                        <td colspan="2" width="100%"><hr /></td>
                    </tr>

                    <tr>
                        <td colspan="2" align="right">
                            <input type="submit" value="Save" name="save" id="saveBtn"> &nbsp;
                            <input type="button" value="Clear" id="clearBtn" onClick="userMaster.clearFormUser(true)">
                        </td>
                    </tr>

                </table>

                <iframe style="visibility:hidden" id=entryResponseFrame name=entryResponseFrame src="" height="0" width="0"></iframe>

            </form>
        </div>
    </div>
</div>
