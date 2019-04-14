LoadTemplate = function () {
    
    var self = this;
    
    self.portTemplateData = function(elem) {
        document.getElementById('waitingInfo').className = "visible";
        document.getElementById('portBtn').disabled = true;
        
        document.getElementById('dupCommand').value = 'port';
        document.portform.submit();

        document.getElementById('showExportLink').className = "hidden";
        
    }

    self.showValidationReport = function(){ 
          var templateName = document.getElementById('templateName');
          var sheetName = templateName.options[templateName.selectedIndex].text;
          var url = PROJECT_CTXT_PATH + "/LoadTemplateServlet?command=openPortingValidationDetails&sheetName=" + sheetName ;
         
          window.open(url, "_blank", "toolbar=yes,location=no,directories=no,status=no,menubar=yes,scrollbars=yes,resizable=yes,copyhistory=no,fullscreen=no,titlebar=yes");

    }
    
    self.setAttachmentPath = function(elem) {
        document.getElementById('waitingInfo').className = "hidden"; 
        var row = dynTableRow(elem);
        dynTableGetNodeInRow(row, "filePath").value = elem.value;
        dynTableGetNodeInRow(row, "attachmentFileName").innerHTML = elem.value;
        dynTableGetNodeInRow(row, "attachmentChanged").value = "1";        
    }
    
    
    self.hideInfoDiv = function() {
        document.getElementById('waitingInfo').className = "hidden"; 
    }
    
    this.handleAttachmentSuccess = function(message) {
        document.getElementById('waitingInfo').className = "hidden"; 
        document.getElementById('portBtn').disabled = false;
        alert(message);

            document.getElementById('showExportLink').className = "visible";
 

        if(message == "OPENING STOCK PORTED SUCESSFULLY") {
            var url = PROJECT_CTXT_PATH + "/LoadTemplateServlet?command=openStockPortingStatus" ;
        } else if(message == "DRUGS PORTED SUCESSFULLY") {
            var url = PROJECT_CTXT_PATH + "/LoadTemplateServlet?command=openDrugsPortingStatus" ;
        }
        var divID = document.getElementById('portingStatus');
        xmlLoadElementValues(url, divID);
        
    }
    
    self.handleAttachmentFailure = function(message) {
        alert(message);   
        document.getElementById('waitingInfo').className = "hidden";
        document.getElementById('portBtn').disabled = false;
    }
    
    self.openStockPortingFailedDetails = function() {  
        var url = PROJECT_CTXT_PATH + "/LoadTemplateServlet?command=openPortingFailedDetails" ;
        window.open(url, "_blank", "toolbar=yes,location=no,directories=no,status=no,menubar=yes,scrollbars=yes,resizable=yes,copyhistory=no,fullscreen=no,titlebar=yes");
    }
    
    self.openDrugsPortingFailedDetails = function() {  
        var url = PROJECT_CTXT_PATH + "/LoadTemplateServlet?command=openDrugsPortingFailedDetails" ;
        window.open(url, "_blank", "toolbar=yes,location=no,directories=no,status=no,menubar=yes,scrollbars=yes,resizable=yes,copyhistory=no,fullscreen=no,titlebar=yes");
    }
    
    self.portPatientDetails = function() {
        
        var url = PROJECT_CTXT_PATH + '/LoadTemplateServlet?&command=portPatientDetails';
        
        document.getElementById('info').className = "visible";
        document.getElementById('btnPort').disabled = true;
        var returnValue = xmlGetResultString(url);
        document.getElementById('info').className = "hidden"; 
        
        if(returnValue == 0) {
            alert('Unable to update status');
        }else{
            alert('Patients Saved Sucessfully');
        }   
        
        var url1 = PROJECT_CTXT_PATH + '/LoadTemplateServlet?&command=patientPortingStatus';
        xmlLoadElementValues(url1, document.getElementById("portingStatus")); 
    }    
    
    self.openPatientPortingFailedDetails = function() {  
        var url = PROJECT_CTXT_PATH + "/LoadTemplateServlet?command=openPatientPortingFailedDetails" ;
        window.open(url, "_blank", "toolbar=yes,location=no,directories=no,status=no,menubar=yes,scrollbars=yes,resizable=yes,copyhistory=no,fullscreen=no,titlebar=yes");
    }
    
    self.portFromDB = function(elem) {
        
        var responseStr = xmlPostForm(document.portform, "LoadTemplateServlet");
        if(responseStr == "SAVED SUCCESSFULLY"){
            var url = PROJECT_CTXT_PATH + "/LoadTemplateServlet?command=loadDBTemplate" ;
            var TemplateDetail = document.getElementById("TemplateDetail");
            
            xmlLoadElementValues(url, TemplateDetail);
        } 
    } 
    self.downloadTemplate = function() {
        var templateNameObj = document.getElementById('templateName');
        var templateName = templateNameObj.options[templateNameObj.selectedIndex].value;
        var fileName = "";
        if (templateName != '0') {
            if(templateName == 'SERVICES') {
                fileName = '/ServiceSheet.xls';
            } else if(templateName == 'Packages'){
                fileName = '/drugs.xls';
            }else if(templateName == 'Doctors') {
                fileName = '/doctor_template.xls';
            }else if (templateName == 'TPA') {
                fileName = '/TpaSheet.xls';
            }else if(templateName == 'DRUGS') {
                fileName = '/drugs_template.xls';
            }else if(templateName == 'OPENING_STOCK') {
                fileName = '/Opening_Stock.xls';
            }else if(templateName == 'SUPPLIERS') {
                fileName = '/suppliers.xls';
            }else if(templateName == 'Corporate') {
                fileName = '/CorporateSheet.xls';
            } else if (templateName == 'MATERIALS') {
                fileName = '/Materials.xls';
            }else {
                alert('There is no template details');
                return false;
            }
            
        }else {
            alert("Select template name");
            return false;
        }
        var url = PROJECT_CTXT_PATH + "/LoadTemplateServlet?command=downLoadTemplate&fileName=" + fileName;
        window.open(url, "_blank", "toolbar=yes,location=no,directories=no,status=no,menubar=yes,scrollbars=yes,resizable=yes,copyhistory=no,fullscreen=no,titlebar=yes");
    }
    
}
var loadTemplate = new LoadTemplate();



