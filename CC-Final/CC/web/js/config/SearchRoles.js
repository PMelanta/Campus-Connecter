SelectedRoles = function() {
    var self = this;
    var RET = 13;

    self.getRoles = function(element, event) {
        
        if(baGetKeyCode(event) == RET) {
           self.loadRoles();
            }
    }

    self.loadRoles = function() {
       var searchStr = ""; 
       searchStr = document.getElementById("searchRoles").value;
       var url = PROJECT_CTXT_PATH + '/UMasterServlet?command=loadRolesList&searchStr=' + searchStr;

       if(document.getElementById('roleRID')) 
            url += '&roleRID=' + document.getElementById('roleRID').value;
       
       var searchRolesDiv = document.getElementById('searchRolesDiv');
       xmlLoadElementValues(url, searchRolesDiv);
        }
        
    self.getAccessibleRoles = function(element,event) {
        if(baGetKeyCode(event) == RET) {
           self.loadAccessibleRoles();
            }

    }

    self.loadAccessibleRoles = function() {
       var searchStr = ""; 
       searchStr = document.getElementById("searchAccessibleRoles").value;
       var url = PROJECT_CTXT_PATH + '/UMasterServlet?command=loadAccessibleRolesList&searchStr=' + searchStr;
       var searchAccessibleRolesDiv = document.getElementById('searchAccessibleRolesDiv');
       xmlLoadElementValues(url, searchAccessibleRolesDiv);
        }

}

var selectedRoles = new SelectedRoles();


