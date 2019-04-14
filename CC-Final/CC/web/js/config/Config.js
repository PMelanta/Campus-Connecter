function  ConfigModule() {
     var me = this;   
    function loadPage(url, command) {
       
        
        url = PROJECT_CTXT_PATH + url;
        xmlLoadElementValues(url,document.getElementById('workingDiv')) ;
            
        url = "" ;
        
        if(command == 'Roles'){
            document.getElementById('currentActivity').value = ' Config : Roles';
            roleMaster.formInitRole();
        }else if(command == 'User'){
            document.getElementById('currentActivity').value = ' Config : Users';
            userMaster.formInitUser();
        }else if(command == 'AuditTrail'){
            document.getElementById('currentActivity').value = ' Config : Audit Trail';
        }
        // document.getElementById('workingDiv').innerHtml = "" ;        
        }
        
        me.configHandleSuccessReload = function (successMsg,url){

        
         desktop.confirmMessage(successMsg); 
          url="/UMasterServlet?command=loadRoleMasterSorted";
          loadPage(url,'Roles');
        
        };
       
        me.configHandleSuccess = function (responseMsg){
          
          // desktop.confirmMessage(responseMsg);
          alert(responseMsg);
          
            var url="/UMasterServlet?command=loadStaff"; 
            loadPage(url,'User');
          
        };
      
        me.configHandleFailure = function (responseMsg){
            roleMaster.roleSaveCnclenabled();
            alert(responseMsg);
            
      
        };    
                        
    
        function hideDiv(){
        
        var targetDiv = document.getElementById('alertDiv') ;
        var msgFrame=document.getElementById('msgFrame') ;
        targetDiv.style.visibility = 'hidden' ;
        targetDiv.style.display = 'none' ;
        msgFrame.style.visibility = 'hidden' ;
        msgFrame.style.display = 'none' ;
        
        }   
        
 var configCurrSelection ;

 me.highlightSelection = function (elem, url, command) {
    var row;
    if (!checkTransition ()) 
        return false;

    loadPage(url, command);

    if(configCurrSelection != null) {
        row = dynTableRow(configCurrSelection);
        row.className = "lhsMenuTable" ;
    } 

    row = dynTableRow(elem);
    row.className = "selectedRow";
    configCurrSelection = elem;
};

me.configInit = function(description){
        configCurrSelection = null; 
        if(description)
            if(document.getElementById('currentActivity'))
            document.getElementById('currentActivity').value = description;
};
}
    
var configModule = new ConfigModule();
