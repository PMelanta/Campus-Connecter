function loadUser(userRID) {

  var url = PROJECT_CTXT_PATH + "/UMasterServlet";

  userMasterForm.command.value = "loadUserMaster";
  userMasterForm.userRID.value = userRID;

  userMasterForm.action = url;
  userMasterForm.method = "get";

  userMasterForm.target = "_self";

  userMasterForm.submit();
}

function loadUsers(entityRID) {

  var url = PROJECT_CTXT_PATH + "/UMasterServlet";

  xmlLoadElementValues(url + "?command=loadUsers&entityRID=" + entityRID, document.getElementById('userSelection'));
}

function loadRoles(appDB) {
 
  var url = PROJECT_CTXT_PATH + "/UMasterServlet";

  userMasterForm.command.value = "loadUserMaster";
  //userMasterForm.userRID.value = userRID;

  userMasterForm.action = url;
  userMasterForm.method = "get";

  userMasterForm.target = "_self";

  userMasterForm.submit();

}

function addRoles() {
  
    
  var allRoles = document.getElementsByName("role");

  var roleNames = document.getElementsByName("roleName");

  for(i = 0; i < allRoles.length; i++) {

    var sel = allRoles[i];

     if(sel.checked) {

       var row = dynTableAppendRow("assignedRolesTbl");

       var fRID = dynTableGetNodeInRow(row, "assignedRole")
       fRID.value = sel.value;
       
       var txtBox = dynTableGetNodeInRow(row, "assignedRoleName")
       txtBox.value = roleNames[i].value;

       sel.checked = false;
     }
  }

}

function removeRoles() {

  dynTableDeleteSelectedRows("assignedRoleChk");
}

function clearForm(prompt) {

  var ans = true;

  if(prompt)
    ans = confirm("Do you really want to clear the form?");

  if(ans) {

    userMasterForm.reset();

    userMasterForm.userRID.value = 0;
    userMasterForm.userEntityRID.value = 1;

    userMasterForm.userName.value = "";
    userMasterForm.userLoginId.value = "";

    userMasterForm.isActive.checked = true;

    userMasterForm.command.value = "saveSysUser";
    resetDirtyBit() ;
    dynTableDeleteAllRows("assignedRolesTbl");
  }
}

function formValidateApp() {

  /*
  if(userMasterForm.userEntityRID.value == 0) {

    alert("Please select user's location");

    userMasterForm.userEntityRID.focus();
    return false;
  }
*/
  if(isEmpty(userMasterForm.userName.value)) {
    
    alert("Please enter user's full name");

    userMasterForm.userName.focus();
    return false;
  }

  if(isEmpty(userMasterForm.userLoginId.value)) {
    
    alert("Please enter user's Login Id");

    userMasterForm.userLoginId.focus();
    return false;
  }

  if(!isAlphaNumeric(userMasterForm.userLoginId.value)) {
    
    alert("Login Id must be a single alphanumeric word");

    userMasterForm.userLoginId.focus();
    return false;
  }

  var roles = userMasterForm.assignedRoleChk;

  if(roles == null) {

    alert("Please assign a role to the user");

    return false;
  }

  return true;
}

function formInitApp() {
  
  
  

    dynTableInit('assignedRolesTbl');
 
  var lastRow = dynTableGetLastRow('assignedRolesTbl');

  lastRow.parentNode.removeChild(lastRow);

  //loadUsers(document.getElementById('entityRID').value)

}
