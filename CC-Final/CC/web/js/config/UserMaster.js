function UserMaster(){
    var me =this;
    me.loadUserDetails = function (userRID) {


      var url = PROJECT_CTXT_PATH + "/UMasterServlet";
      //xmlLoadElementValues(url+"?command=loadSysUsers&userRID="+userRID,document.getElementById('workingDiv'))

      xmlLoadElementValues(url+"?command=loadStaff&userRID="+userRID,document.getElementById('workingDiv'))

       document.userMasterForm.userName.readOnly=true;
     // me.formInitUser();
     // userMasterForm.command.value = "loadUserMaster";
      //userMasterForm.userRID.value = userRID;

      //userMasterForm.action = url;
      //userMasterForm.method = "get";

     // userMasterForm.target = "_self";

      //userMasterForm.submit();
    };
    
    function loadUsers(entityRID) {

      var url = PROJECT_CTXT_PATH + "/UMasterServlet";

      xmlLoadElementValues(url + "?command=loadUsers&entityRID=" + entityRID, document.getElementById('userSelection'));

     // userMasterForm.userName.readOnly=true;
    }

    me.addRolesUser = function () {
        debugger;
      var allRoles = document.getElementsByName("role");

      var roleNames = document.getElementsByName("roleName");

      var assignedRoles= document.getElementsByName("assignedRole");

      for(i = 0; i < allRoles.length; i++) {

        var sel = allRoles[i];

         if(sel.checked) {

           
           var roleTbl = document.getElementById('assignedRolesTbl'); 
           var lastRow = roleTbl.rows[roleTbl.rows.length - 1];
           var templateRow = roleTbl.rows[0];
           var row = dynTableInsertGivenCloneAfterRow('assignedRolesTbl', lastRow, templateRow);
           var fRID = dynTableGetNodeInRow(row, "assignedRole")
           fRID.value = sel.value;
            
           var txtBox = dynTableGetNodeInRow(row, "assignedRoleName")
           txtBox.value = roleNames[i].value;

           sel.checked = false;
           row.className = 'myLabel';
           if(assignedRoles!=null){
                var flag=0;
                for(j = 0; j < assignedRoles.length; j++){
                    if(assignedRoles[j].value==sel.value){   
                        flag++;
                    }
                }
                if(flag>1){
                    dynTableDeleteRow(row);
                }
           }
         }
      }

    };

    me.removeRoles = function () {

      dynTableDeleteSelectedRows("assignedRoleChk");
    };

    me.clearFormUser = function (prompt) {

      var ans = true;

      if(prompt)
        ans = confirm("Do you really want to clear the form?");

      if(ans) {

        document.userMasterForm.reset();

       document.userMasterForm.userRID.value = 0;
        document.userMasterForm.userEntityRID.value = 0;

        document.userMasterForm.userName.value = "";
        document.userMasterForm.userLoginId.value = "";
        document.userMasterForm.userEmailId.value = "";

        document.userMasterForm.isActive.checked = true;

        document.userMasterForm.command.value = "saveSysUser";

        dynTableDeleteAllRows("assignedRolesTbl");

        document.userMasterForm.userName.readOnly=false;

         document.getElementById('userFullNameMsg').innerHTML=" *";
         document.getElementById('userIdMsg').innerHTML=" *";
         document.getElementById('userRolesMsg').innerHTML=" *";

      }
    };

    function handleSuccess(msg) {
       alert("handle success user");
      clearForm(true);

      var url = PROJECT_CTXT_PATH + "/UMasterServlet";

      var ent = document.userMasterForm.entityRID.value;
      if(ent > 0) 
        xmlLoadElementValues(url + "?command=loadUsers&entityRID=" + ent, document.getElementById('userSelection'));
    }

    function handleFailure(msg) {
      alert(msg);
    }

function isAlphaNumeric (sFieldValue)
{
	var iCount = 0;
	var iCode = 0;
	var bRetValue = true;

	sFieldValue = uTrim (sFieldValue, 2);

	if ("" == sFieldValue)
	{
		bRetValue = false;
	}
	else
	{
		var iFieldWidth = sFieldValue.length;
		for (iCount = 0; iCount < iFieldWidth; iCount++)
		{
			iCode = sFieldValue.charCodeAt (iCount);
			if (48 > iCode || (57 < iCode && 65 > iCode) || (90 < iCode && 97 > iCode) || iCode > 122 )
			{
				bRetValue = false;
				break;
			}
		}
	}
	return bRetValue;

}

   me.formValidateUser = function () {
        roleMaster.roleSaveCncldisabled();
        document.getElementById('userFullNameMsg').innerHTML=" *";
         document.getElementById('userIdMsg').innerHTML=" *";
         document.getElementById('userRolesMsg').innerHTML=" *";
      /*if(userMasterForm.userEntityRID.value == 0) {

        alert("Please select user's location");

        userMasterForm.userEntityRID.focus();
        return false;
      }*/
      var flag=true;
      if(isEmpty(document.userMasterForm.userName.value)) {

        document.getElementById('userFullNameMsg').innerHTML=" *Please enter user's full name";

        document.userMasterForm.userName.focus();

        flag=false;
        //return false;
      }

      if(isEmpty(document.userMasterForm.userLoginId.value)) {

        document.getElementById('userIdMsg').innerHTML=" *Please enter user's Login Id";

        document.userMasterForm.userLoginId.focus();

        flag=false;
       // return false;
      }else if(!isAlphaNumeric(document.userMasterForm.userLoginId.value)) {

         document.getElementById('userIdMsg').innerHTML=" *Login Id must be a single alphanumeric word";

        document.userMasterForm.userLoginId.focus();

        flag=false;
       // return false;
      }

      var roles = document.userMasterForm.assignedRoleChk;

      if(roles == null) {

        document.getElementById('userRolesMsg').innerHTML=" *Please assign a role to the user";

        flag=false;
       // return false;
      }

      /*if(isEmployee(userMasterForm.userLoginId.value)){
          if(confirm("Do you really want to convert this employee to system user")){
            return(true);
          }else{
            return(false);
          }
      }*/
      if(flag == false)
        roleMaster.roleSaveCnclenabled();
      return flag;
    };

    function isEmployee(empLogId){
        var employees=document.getElementsByName("empName");
        if(employees==null){
            return false;
        }

        for(var i=0;i<employees.length;i++){
            if(employees[i].value==empLogId){
                return true;
            }
        }
        return false;
    }

    me.formInitUser = function () {
     // dynTableInit('assignedRolesTbl');

     // var lastRow = dynTableGetLastRow('assignedRolesTbl');

     // lastRow.parentNode.removeChild(lastRow);
    };

    function doSearch(){

         var emp = window.showModalDialog(PROJECT_CTXT_PATH + "/PatientServlet?command=showSearchPatient",
                    "toolbar=no,location=no,directories=no, status=no,menubar=no,scrollbars=no,resizable=1,copyhistory=no", "dialogWidth:50em; dialogHeight:35em");

           if(emp == null)
                return;
          var name = emp['patientName'];
          var userRID = emp['patientUserRID'];

          me.loadUserDetails(userRID);

    }

//    me.toggleGeneratedPassword = function (elem) {
//
//        var targetElem = document.getElementById('generatedPasswordSpan') ;
//
//        if (elem.checked) {
//            targetElem.className = "" ;
//        } else {
//            targetElem.className = "hidden" ;
//        }
//
//    }

}

var userMaster = new UserMaster();