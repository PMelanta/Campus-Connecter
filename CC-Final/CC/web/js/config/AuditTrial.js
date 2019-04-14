function AuditTrailModule() {
    var me = this;
    var self = this;
    
    me.searchPatientforAuditTrial = function (){
        var url = PROJECT_CTXT_PATH + "/PatientServlet?command=showSearchPatient";

        desktop.showPopup("Search Patient", url, {width:700,height:300, onLoad: function(){ patientSearch.init(self.handleAuditTrialPatientSelection); } });
    };

    self.handleAuditTrialPatientSelection = function (patientInfo) {
        
        if(patientInfo != null) {

            document.getElementById("txtPatientSearch").value = patientInfo["patientName"];
            document.getElementById('patRID').value = patientInfo["patientRID"];
        }
        return true;
    }

    me.showDetails = function (opt){

       
        var buttonID = 'viewInHtml';

        document.getElementById('commonErrorMsg').innerHTML = '';
        currObj =  document.getElementById('fromDateTime');

        if (!uscmIsEmpty(currObj.value)) {
            if(!uscmIsValidDate(currObj.value)) {
              document.getElementById('commonErrorMsg').innerHTML = "Please enter a valid Date(dd/mm/yyyy)"; 
              return false;
            }
        }
        currObj = document.getElementById('toDateTime');
        if (!uscmIsEmpty(currObj.value)) {
            if(!uscmIsValidDate(currObj.value)) {
                document.getElementById('commonErrorMsg').innerHTML = "Please enter a valid Date(dd/mm/yyyy)"; 
                return false;
            }
        }
        if(!uscmIsEmpty(document.getElementById('fromDateTime').value)&&!uscmIsEmpty(document.getElementById('toDateTime').value))
         if(uscmIsMoreDate(document.getElementById('fromDateTime').value , document.getElementById('toDateTime').value)){
           document.getElementById('commonErrorMsg').innerHTML = 'Enter proper Date range';
           return false;
        }

        var patRID = document.getElementById('patRID').value;
        var userRID = document.getElementById('userSelection').value; 
        var eventIndex = document.getElementById('eventSelection').value;    
        var fromDate = document.getElementById('fromDateTime').value;
        var toDate = document.getElementById('toDateTime').value;
        var limit = document.getElementById('displayLimit').value;
        if(limit == '')
            limit = 1000;
        if(document.getElementById("txtPatientSearch").value == '')
            patRID = 0;


        var auditRID = 0;
        var direction = 1;
        if(opt == 0){
             auditRID = 0;
        }else if(opt==1){
            currRow =document.getElementById('tblAuditDetails').rows[(document.getElementById('tblAuditDetails').rows.length)-1];
            auditRID =  dynTableGetNodeInRow(currRow, 'auditRID').value ;
            direction = 1;
        }else if(opt==2){
            currRow =document.getElementById('tblAuditDetails').rows[1];
            var auditRID =  dynTableGetNodeInRow(currRow, 'auditRID').value ;
            direction = -1;
        }else{

             auditRID = 0;
             direction = 1;
        }
        var url = PROJECT_CTXT_PATH + '/UAuditTrailServlet?command=showDetails&patRID='+patRID+'&userRID='+userRID+'&eventIndex='+eventIndex+'&fromDate='+fromDate+'&toDate='+toDate+'&direction='+direction+'&auditRID='+auditRID+'&buttonID='+buttonID;

//        xmlLoadElementValues(url, document.getElementById('auditDetailsDIV'));
        
        if (uscmIsEmpty(limit))
            limit = 100;
        
        paging.init( url, 'auditDetailsDIV', 'pageNavigationController', limit);

    };
    
    me.showDetailsInExcel = function(opt) {
        var buttonID = 'viewInExcel';
        var user = document.getElementById('userSelection').options[document.getElementById('userSelection').selectedIndex].text;
        var event = document.getElementById('eventSelection').options[document.getElementById('eventSelection').selectedIndex].text;
        document.getElementById('commonErrorMsg').innerHTML = '';
        currObj =  document.getElementById('fromDateTime');

        if (!uscmIsEmpty(currObj.value)) {
            if(!uscmIsValidDate(currObj.value)) {
              document.getElementById('commonErrorMsg').innerHTML = "Please enter a valid Date(dd/mm/yyyy)"; 
              return false;
            }
        }
        currObj = document.getElementById('toDateTime');
        if (!uscmIsEmpty(currObj.value)) {
            if(!uscmIsValidDate(currObj.value)) {
                document.getElementById('commonErrorMsg').innerHTML = "Please enter a valid Date(dd/mm/yyyy)"; 
                return false;
            }
        }
        if(!uscmIsEmpty(document.getElementById('fromDateTime').value)&&!uscmIsEmpty(document.getElementById('toDateTime').value))
         if(uscmIsMoreDate(document.getElementById('fromDateTime').value , document.getElementById('toDateTime').value)){
           document.getElementById('commonErrorMsg').innerHTML = 'Enter proper Date range';
           return false;
        }

        var patRID = document.getElementById('patRID').value;
        var userRID = document.getElementById('userSelection').value; 
        var eventIndex = document.getElementById('eventSelection').value;    
        var fromDate = document.getElementById('fromDateTime').value;
        var toDate = document.getElementById('toDateTime').value;
        var limit = document.getElementById('displayLimit').value;
        if(limit == '')
            limit = 1000;
        if(document.getElementById("txtPatientSearch").value == '')
            patRID = 0;


        var auditRID = 0;
        var direction = 1;
        if(opt == 0){
             auditRID = 0;
        }else if(opt==1){
            currRow =document.getElementById('tblAuditDetails').rows[(document.getElementById('tblAuditDetails').rows.length)-1];
            auditRID =  dynTableGetNodeInRow(currRow, 'auditRID').value ;
            direction = 1;
        }else if(opt==2){
            currRow =document.getElementById('tblAuditDetails').rows[1];
            var auditRID =  dynTableGetNodeInRow(currRow, 'auditRID').value ;
            direction = -1;
        }else{

             auditRID = 0;
             direction = 1;
        }
        var url = PROJECT_CTXT_PATH + '/UAuditTrailServlet?command=showDetails&patRID='+patRID+'&userRID='+userRID+'&eventIndex='+eventIndex+'&fromDate='+fromDate+'&toDate='+toDate+'&direction='+direction+'&auditRID='+auditRID+'&buttonID='+buttonID+'&user='+user+'&event='+event;
        window.open(url, "_blank", "toolbar=yes,location=no,directories=no, status=no,menubar=no,scrollbars=yes,resizable=yes, copyhistory=no,fullscreen=nod,titlebar=yes");                
       

    }
    
    me.clearDetails = function (){
         var url = PROJECT_CTXT_PATH + '/UAuditTrailServlet?command=initAuditTrail';
         
       xmlLoadElementValues(url, document.getElementById('workingDiv'));
    };
    
    function setActivityName(description)  {
        document.getElementById('currentActivity').value = description;
    }

}

var auditTrailModule = new AuditTrailModule();