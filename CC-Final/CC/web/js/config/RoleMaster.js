function RoleMaster(){
    var me = this; 
    me.clearFormRole = function(prompt) {
        
        var ans = true;
        
        if(prompt)
            ans = confirm("Do you really want to clear the form?");
        
        if(ans) {
            
            document.roleMasterForm.reset();
            
            document.roleMasterForm.roleName.value = "";
            document.roleMasterForm.isActive.checked = true;
            
            document.roleMasterForm.roleRID.value = 0;
            document.roleMasterForm.command.value = "saveRoleWithMesaage";
            resetDirtyBit() ;
            dynTableDeleteAllRows("assignedFeatureTbl");
            document.getElementById('roleNameMsg').innerHTML=" *";
        }
    };
    
    me.loadRole = function (roleRID) {
        
        var url = PROJECT_CTXT_PATH + "/UMasterServlet?command=loadRoleMasterSorted&roleRID=" + roleRID;
        
        document.roleMasterForm.action = url;
        document.roleMasterForm.method = "get";
        
        xmlLoadElementValues(url, document.getElementById('workingDiv'));
        // me.formInitRole();
    };
    
    me.addFeatures = function () {
        
        var prodFeatures = document.getElementsByName("productFeature");
        
        var prodFeatureNames = document.getElementsByName("productFeatureName");
        
        var assignedFeatures=document.getElementsByName("assignedFeature");
        
        for(i = 0; i < prodFeatures.length; i++) {
            
            var sel = prodFeatures[i];
            
            if(sel.checked) {
                
                
                var featureTbl = document.getElementById('assignedFeatureTbl');
                
                var templateRow = featureTbl.rows[0];
                var lastRow = featureTbl.rows[featureTbl.rows.length - 1];
                var row = dynTableInsertGivenCloneAfterRow('assignedFeatureTbl', lastRow, templateRow);
                
                row.className = 'visible';
                // alert('step 2')
                var fRID = dynTableGetNodeInRow(row, "assignedFeature")
                fRID.value = sel.value;
                
                var txtBox = dynTableGetNodeInRow(row, "assignedFeatureName")
                txtBox.value = prodFeatureNames[i].value;
                
                sel.checked = false;
                
                if(assignedFeatures!=null){
                    var flag=0;
                    for(j=0;j<assignedFeatures.length;j++){
                        if(assignedFeatures[j].value==sel.value){
                            flag++;
                        }
                    }
                    if(flag>1){
                        dynTableDeleteRow(row)
                        
                    }
                }
            }
        }
        
    };
    
    me.removeFeatures = function () { 
        dynTableDeleteSelectedRows("assignedFeatureChk");
    }
    
    me.formInitRole =  function () {
        return
        dynTableInit('assignedFeatureTbl');
        //document.getElementById('successHandler').value = "roleMaster.handleSuccess"; 
        //document.getElementById('failureHandler').value = "roleMaster.handleFailure"; 
        var lastRow = dynTableGetLastRow('assignedFeatureTbl');
        
        lastRow.parentNode.removeChild(lastRow);
    }
    
    me.formValidateRole = function () {
        me.roleSaveCncldisabled();
        
        
        if(isEmpty(document.roleMasterForm.roleName.value)) {
            
            document.getElementById('roleNameMsg').innerHTML=" *Please enter a name for the role";
            
            document.roleMasterForm.roleName.focus();
            me.roleSaveCnclenabled();
            return false;
        }
        
        
        return true;
    }
    
    
    
    
    me.roleSaveCncldisabled = function (){
        document.getElementById('saveBtn').disabled = true;
        document.getElementById('clearBtn').disabled = true;
    }
    
    me.roleSaveCnclenabled = function (){
        document.getElementById('saveBtn').disabled = false;
        document.getElementById('clearBtn').disabled = false;
    }
    
    me.handleSuccess = function (msg) {
        alert("handle success role");
        
        clearForm(false);
        
        var url = PROJECT_CTXT_PATH + "/UMasterServlet";
        // alert("handle success roles");
        xmlLoadElementValues(url + "?command=reloadRoles", document.getElementById('roleSelection'));
    };
    
    me.handleFailure = function (msg) {
        
        alert(msg);
    };
    me.submitRoleMasterForm =  function() {
        if(me.formValidateRole()) {
            document.roleMasterForm.submit();           
        }
    }
    
    me.initializeRoleBrowser = function(){
         var url = PROJECT_CTXT_PATH + '/UMasterServlet?command=loadRoleMasterSorted'; 
        xmlLoadElementValues(url, document.getElementById('roleDetailsDiv'));
    }
    
    me.roleMasterHandleSuccess = function (responseMsg){
        //desktop.confirmMessage(responseMsg); 
        alert(responseMsg);
        desktop.closePopup();
        roleMaster.searchbyRoleName(); 
    };     
    
    me.modify = function(roleRID) {
        var url = PROJECT_CTXT_PATH + '/UMasterServlet?command=loadRoleMasterSorted&roleMaster=1&hideSelectRoles=1&roleRID='+ roleRID;
        desktop.showPopup("Modify Roles", url, {width:800,height:600}); 
    }
    
    me.addNewRole = function() {
        var url = PROJECT_CTXT_PATH + '/UMasterServlet?command=loadRoleMasterSorted&roleMaster=1&hideSelectRoles=1';
        desktop.showPopup("Add New Role", url, {width:800,height:600}); 
    }
    
    me.exportRoleDetails = function (){
        var roleName = document.getElementById('roleName').value;
        var showActive = document.getElementById('showActive');
        var url = PROJECT_CTXT_PATH + '/UMasterServlet?command=exportRoleMasterFeatures&roleName='+roleName;
        window.open(url, "_blank", "toolbar=yes,location=no,directories=no, status=no,menubar=no,scrollbars=yes,resizable=yes, copyhistory=no,fullscreen=nod,titlebar=yes");             
    };
    
    me.keyAscii = function(ev) {
        if (baGetKeyCode(ev) == 13){
            roleMaster.searchbyRoleName();   
        }
    }
    me.searchbyRoleName = function(){
        var roleName = document.getElementById('roleName').value;
        //var showActive = document.getElementById('showActive').value;
        var url = PROJECT_CTXT_PATH + '/UMasterServlet?command=searchbyRoleName&roleName='+roleName+'&showActive=0';
        //var displayLimit = document.getElementById("displayLimit").value;
        xmlLoadElementValues(url, document.getElementById('roleDetailsDiv'));
    }
    me.showModified = function(elem){
        var showActive = document.getElementById('showActive');
        if(showActive.checked == true){
            document.getElementById('showActive').value = 1;             
        }else{
            document.getElementById('showActive').value = 0; 
        }
    }
    
}

var roleMaster = new RoleMaster();   




