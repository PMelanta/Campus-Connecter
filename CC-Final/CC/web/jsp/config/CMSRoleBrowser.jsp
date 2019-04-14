<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*" import="java.text.*" import="java.util.*" import="cc.base.*" errorPage="" %>
<%@ page pageEncoding="UTF-8"%>

<% URequestContext ctxt = (URequestContext) request.getAttribute("ctxt");
    ResultSet rolesRS = null;
    int isRoleValid = 0, roleRID = 0, count = 0;
    String roleName = "", rowClass = "";
    int foundRows = request.getAttribute("foundRows") != null ? Integer.parseInt(request.getAttribute("foundRows").toString()) : 0;
    int rowsToDisplay = 10;
    String roleNames = request.getParameter("roleName") != null ? (String) request.getParameter("roleName") : "";
    int showActive = request.getAttribute("showActive") != null ? Integer.parseInt(request.getAttribute("showActive").toString()) : 1;
%>

<div id="roleBrowserDiv">
    <%String projPath = request.getContextPath();%>
    <input type="hidden" name="jsFile" value="<%=projPath%>/js/config/RoleMaster.js">
    <input type="hidden" name="onLoadFunction" value="roleMaster.initializeRoleBrowser()">  
    <input type="hidden" id="foundRows" value="<%= foundRows%>">
    <input type="hidden" id="displayLimit" value="<%=rowsToDisplay%>" >


    <!--    <table width="100%" border="0" cellpadding="0" cellspacing="0" >
            <tr height="30px" class="wellHeader" >
                <td width="47%" ><span id="commonErrorMsg" class="userInfo"></span></td>
                <td width="50%">                                                                           
                </td>
                <td width="3%" align="right">
                    <span class="infoButton" title="Information" onclick="showInfo_DDEntry()">&nbsp;i&nbsp;</span>
                </td>
            </tr>
        </table>-->

    <table width="100%" border="0" cellpadding="4" cellspacing="0" >
        <tr style="padding-left:10px" class="specialRow" height="30px" colspan="100%">
            <th align="left" colspan="100%" style="padding-left:10px">
                Role Browser &nbsp; <a href="javascript:" onclick="roleMaster.addNewRole()" style="cursor:pointer" nowrap>Add New Role</a>
            </th>
        </tr>
        <tr height="10px"></tr>
        <tr height="30px">
            <td colspan="2" align="left">
                Role Names
                <input name="roleName" id="roleName" type="text"  size="25" value="<%= roleNames%>" onkeydown="roleMaster.keyAscii(event);">
                <span id="desc_error" class="userInfo"> </span>
            </td>
        </tr>

        <%--        <tr>
                    <td>
                        <input name="showActive" type="checkbox" id="showActive" <%= showActive == 1 ? "checked value=\"1\"" : "value=\"0\"" %> onclick="roleMaster.showModified(this);"> 
                        <label for="showActive">Show active only</label>
                    </td>
                    <td align="right" style="padding-right:10px">
                        <input type="button" value="Export" id="exportBtn" onclick="roleMaster.exportRoleDetails()">
                        <input name="searchName" type="button" id="searchName" value="Search" onclick="roleMaster.searchbyRoleName()" >                 
                    </td>
                </tr>
        --%>
        <tr height="10px"></tr>
    </table> 
    <table cellspacing="0" cellpadding="4" border="0" width="100%" id="roleDetailsDiv">
        <thead>
            <tr class="specialRow">               
                <th align="left" width="70%">
                    Roles
                </th>
                <th align="left" width="15%">
                    Active
                </th>
            </tr> 
        </thead>
        <%  rolesRS = (ResultSet) request.getAttribute("roles");
            rolesRS.first();
            rolesRS.last();%>
        <tbody style="height:<%= rolesRS.getRow() > 8 ? "250px" : ""%>;overflow:auto;overflow-y:scroll;overflow-x:hidden;">
            <% rolesRS.beforeFirst();
                if (rolesRS != null && rolesRS.first()) {
                    rolesRS.beforeFirst();
                    while (rolesRS.next()) {
                        rowClass = (count % 2 == 0) ? "oddRowColor" : "evenRowColor";
                        roleRID = rolesRS.getInt("role_rid");
                        roleName = rolesRS.getString("role_name");
                        isRoleValid = rolesRS.getInt("role_valid");
                        count++;
            %>
            <tr class="<%= rowClass%>" onclick="roleMaster.modify('<%=roleRID%>')" style="cursor:pointer">                              
                <td><%= roleName%></td>
                <td>
                    <input name="isActive" id="isActive" type="checkbox" value="1" disabled <%= (isRoleValid == 1) ? "checked" : ""%> >
                </td>           
            </tr>   
            <% }
            } else {%>
            <tr>
                <td colspan="2" align="left" height="20px">
                    <span class="searchResultStatus"> No records found </span>
                </td>
            </tr> 
            <% }%> 
        </tbody>
    </table>
    <table cellpadding="5" cellspacing="0" border="0" width="100%">
        <tr class="specialRow">
            <td height="25px" width="50%" align="left" id="pagingRowCountLabel" ></td>
            <td width="50%" align="right" style="padding-right:10px" id="navigationControl" ></td>
        </tr>
    </table>
</div>

