<%@ page contentType="text/html; charset=UTF-8" language="java" import="java.sql.*" errorPage="" %>
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

<% String projPath = request.getContextPath(); %>

<script language="javascript"> 
    var PROJECT_CTXT_PATH = "<%= request.getContextPath()%>"  ;
</script>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>SCM Central Administration</title>
		<link href="/scmCentral/style/bimColors.css" rel="stylesheet" type="text/css">
		<link href="/scmCentral/style/bimStyles.css" rel="stylesheet" type="text/css">
    </head>
<script language="javascript" src="<%= projPath%>/js/base/DynamicTableTemplate.js"></script>
<SCRIPT LANGUAGE="JavaScript" SRC="<%= projPath%>/js/base/xmlHelper.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript" SRC="<%= projPath%>/js/base/UbqValidations.js"></SCRIPT>

<script language="javascript">
	
function clearForm(prompt) {

  var ans = true;

  if(prompt)
    ans = confirm("Do you really want to clear the form?");

  if(ans) {

    roleMasterForm.reset();

    roleMasterForm.roleName.value = "";
    roleMasterForm.isActive.checked = true;

    roleMasterForm.roleRID.value = 0;
    roleMasterForm.command.value = "saveRole";

    dynTableDeleteAllRows("assignedFeatureTbl");
  }
}

function handleSuccess(msg) {
  clearForm(false);

  var url = PROJECT_CTXT_PATH + "/UMasterServlet";

  xmlLoadElementValues(url + "?command=reloadRoles", document.getElementById('roleSelection'));
}

function handleFailure(msg) {

  alert(msg);
}
	
function loadRole(roleRID) {

  var url = PROJECT_CTXT_PATH + "/UMasterServlet";

  roleMasterForm.command.value = "loadRoleMasterSorted";
  roleMasterForm.roleRID.value = roleRID;

  roleMasterForm.action = url;
  roleMasterForm.method = "get";

  roleMasterForm.target = "_self";

  roleMasterForm.submit();
}

function addFeatures() {

  var prodFeatures = document.getElementsByName("productFeature");

  var prodFeatureNames = document.getElementsByName("productFeatureName");

  for(i = 0; i < prodFeatures.length; i++) {

    var sel = prodFeatures[i];

     if(sel.checked) {

       var row = dynTableAppendRow("assignedFeatureTbl");

       var fRID = dynTableGetNodeInRow(row, "assignedFeature")
       fRID.value = sel.value;
       
       var txtBox = dynTableGetNodeInRow(row, "assignedFeatureName")
       txtBox.value = prodFeatureNames[i].value;

       sel.checked = false;
     }
  }

}

function removeFeatures() { 
  dynTableDeleteSelectedRows("assignedFeatureChk");
}

function formInit() {
  dynTableInit('assignedFeatureTbl');

  var lastRow = dynTableGetLastRow('assignedFeatureTbl');

  lastRow.parentNode.removeChild(lastRow);
}

function formValidate() {

  if(isEmpty(roleMasterForm.roleName.value)) {
    
    alert("Please enter a name for the role");

    roleMasterForm.roleName.focus();
    return false;
  }

  return true;
}

</script>

<style type="text/css">

.header {	
  font-family: verdana;
  font-weight: bold;
  font-size: 14px;
  color:#FFFFFF;
}

.myLabel {
  font-family: verdana; 
  font-size: 12px; 
}

#entryResponseFrame      { position: absolute; visibility: hidden }

</style>

<body bgcolor="CFCFD5" onLoad="formInit()" style="margin-left:0px;margin-right:0px;margin-bottom:0px;margin-top:0px;">

<form name="roleMasterForm" method="post" action="<%= projPath%>/UMasterServlet" target="entryResponseFrame" onSubmit="return formValidate();">

<input type="hidden" name="command" value="saveRole">

<%
  int roleRID = 0;
  String roleName = "";
  boolean isActive = true;

  ResultSet rs = (ResultSet) request.getAttribute("roleDetail");

  if(rs != null) {

    rs.next();

    roleRID = rs.getInt("role_rid");
    roleName = rs.getString("role_name");
    isActive = rs.getInt("role_valid") == 1;
  }
%>

<input type="hidden" name="roleRID" value="<%= roleRID%>">

<table bgcolor="CFCFD5" height="650" width="100%" border="0" cellspacing="0" cellpadding="4">
  <tr height="25" width="100%" bgcolor="#4A4A4C">
    <td align="center"><span class="header"><strong>Role Management</strong></span></td>
  </tr>

  <tr>
    <td valign="top">
      <table border="0" cellpadding="0" cellspacing="4" bgcolor="CFCFD5">

        <tr>
          <td colspan="2" class="myLabel">
            &nbsp;To add a new Role, enter data and click on the "Save" button.<br><br>
            &nbsp;To make changes to an existing Role, select a Role below, make changes, <br>
            &nbsp;and then click on the "Save" button.<br><br>
          </td>
	</tr>

	<tr>
          <td colspan="2" class="myLabel">	
            &nbsp;Select Role
	  </td>
	</tr>

	<tr>
	  <td colspan="2" class="myLabel">
              &nbsp;<select style="width:200px" name="roleSelection" id="roleSelection" onChange="loadRole(this.value)">
                <option value="0">&nbsp;</option>
<%
    ResultSet rolesRS = (ResultSet) request.getAttribute("roles");

    if(rolesRS != null) {

        while(rolesRS.next()) {
%>
                <option value="<%= rolesRS.getInt("role_rid")%>"><%= rolesRS.getString("role_name")%></option>
<%
        }
    }
%>
              </select> 
          </td>
        </tr>

        <tr>
          <td colspan="2"><hr /></td>
        </tr>

        <tr>
          <td class="myLabel" colspan="2">Role name&nbsp;&nbsp;<input type="text" name="roleName" value="<%= roleName%>"></td>
        </tr>

	<tr><td colspan="2">&nbsp;</tr>

        <tr>
          <td class="tableHeader_black">Feature List</td>
          <td class="tableHeader_black">Accessible Features</td>
        </tr>

        <tr>
          <td class="workTableBodyColor">
            <div style="overflow:auto; height:200px; valign:top">
              <table name="productFeatureTbl" cellpadding="0">
<%
    ResultSet rs1 = (ResultSet) request.getAttribute("productFeatures");

    if(rs1 != null) {

        while(rs1.next()) {
%>
                <tr>
                  <td class="myLabel">
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
          <td class="workTableBodyColor">
            <div style="overflow:auto; height:200px; valign:top">
              <table name="assignedFeatureTbl" id="assignedFeatureTbl" cellpadding="0" >
<%
    ResultSet rs2 = (ResultSet) request.getAttribute("assignedFeatures");

    if(rs2 != null) {

        while(rs2.next()) {
%>
                <tr>
                  <td class="myLabel">
                    <input type="checkbox" name="assignedFeatureChk">
		    <input type="hidden" name="assignedFeature" value="<%= rs2.getInt("feature_rid")%>">
		    <input type="text" name="assignedFeatureName" class="workTableBodyColor" style="border:0px;" value="<%= rs2.getString("feature_name")%>">
                  </td>
                </tr>
<%
        }
    }
%>
                <tr>
                  <td class="myLabel">
                    <input type="checkbox" name="assignedFeatureChk" >
		    <input type="hidden" name="assignedFeature" value="">
		    <input type="text" name="assignedFeatureName" value="" class="workTableBodyColor" style="border:0px;" >
                  </td>
                </tr>
	      </table>
            </div>
          </td>
        </tr>

	<tr>
          <td align="center">
	    <input type="button" name="addFeatureBtn" value="Add Selected" onClick="addFeatures()">
	  </td>
          <td align="center">
	    <input type="button" name="removeFeatureBtn" value="Remove Selected" onClick="removeFeatures()">
	  </td>

	</tr>

	<tr><td colspan="2">&nbsp;</tr>

        <tr>
          <td colspan="2" class="myLabel"><input type="checkbox" name="isActive" <%= isActive ? "checked=\"checked\"" : ""%>> &nbsp;Is Active</td>
        </tr>

        <tr>
          <td colspan="2" width="100%"><hr /></td>
        </tr>

        <tr>
          <td colspan="2" align="right">
            <input type="submit" value="Save" name="save"> &nbsp;
            <input type="button" value="Clear" onClick="clearForm(true)">
          </td>
        </tr>
    </td>
  </tr>
</table>

<iframe id=entryResponseFrame name=entryResponseFrame src=""></iframe>

</form>

</body>

</html>
