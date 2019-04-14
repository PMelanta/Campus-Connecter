LogBrowser = function(){
    
    var self=this;
    var helpMessage = '';
    var url='';
    
    self.loadLogDetail = function(){
        
        var logLevel = document.getElementById("setLogLevel").value;
        var fromDate = document.getElementById("fromDate").value;
        var toDate = document.getElementById("toDate").value;        
        var toTime = document.getElementById("toTime").value;
        var fromTime = document.getElementById("fromTime").value;
        var userID = document.getElementById("userSelection").value; 
        var logDate = document.getElementById("logDate").value;        
               
        if(logLevel == ""){
            alert("Select the Log Level");
            document.getElementById("setLogLevel").focus();
            return;
        }   
       /* if(logLevel == "DEBUG"){
            if(processor == ""){
                alert("Please Enter the Processor Number");
                document.getElementById("processor").focus();
                return;
            }
        } */
        /*
        if(fromDate == ""){
            alert("please Enter From Date");
            document.getElementById("fromDate").focus();
            return;
        }else if(fromDate != ""){
            if(!isValidDate(document.getElementById("fromDate"), false)){
                document.getElementById("fromDate").focus();
                return;
            }
        }
        if(toDate == ""){
            alert("please Enter To Date");
            document.getElementById("toDate").focus();
            return;
        }else if(toDate != ""){
            if(!isValidDate(document.getElementById("toDate"), false)){
                document.getElementById("toDate").focus();
                return;
            }  
        }
        
        if(uscmIsMoreDate(fromDate, toDate)) {
            alert("To date can't be less than from date");
            document.getElementById("toDate").focus();
            return;
        }
        if(fromTime != ""){
            if(document.getElementById("fromTime").value.length != 5) {
                alert("Invalid time");
                document.getElementById("fromTime").focus();
                return;
            }
            else if(!isValidTime(document.getElementById("fromTime"), false)){
                document.getElementById("fromTime").focus();
                return;
            }
        }
        if(toTime != ""){
            if(document.getElementById("toTime").value.length != 5) {
                alert("Invalid time");
                document.getElementById("toTime").focus();
                return;
            }else if(!isValidTime(document.getElementById("toTime"), false)){
                document.getElementById("toTime").focus();
                return;
            }
        }
        if(fromDate == toDate){
            
            if(toTime < fromTime) { 
                
                alert("To time can't be less than from time");
                document.getElementById("toTime").focus();
                return;
            }
        }
        
        if(logDate == ""){
            alert("please Enter LogDate");
            document.getElementById("logDate").focus();
            return;
        }else if(logDate != ""){
            if(!isValidDate(document.getElementById("logDate"), false)){
                document.getElementById("logDate").focus();
                return;
            }  
        }*/
        
        url = PROJECT_CTXT_PATH + "/ConfigServlet?command=loadLogDetail&level=" + document.getElementById("setLogLevel").value +
        //"&fromTime=" + fromTime + "&toTime=" + toTime + "&fromDate=" + fromDate +  "&toDate=" + toDate + 
        "&userRID=" + userID + "&logDate=" + logDate;
        if(document.getElementById("processor") != null || document.getElementById("processor").value != ""){
            var processor = document.getElementById("processor").value;
            url += "&processor="+processor;
        }
       
        
        var displayDiv = document.getElementById("displayDiv");
        
        if(displayDiv != null) {
            xmlLoadElementValues(url, displayDiv);
        }
        document.getElementById("displayInfo").className = "borderLine";
    }
    self.resetDisplay = function(){
        
        document.getElementById("displayInfo").className = "hidden";
        var displayDiv = document.getElementById("displayDiv");
        displayDiv.innerHTML = " ";
        document.getElementById("fromDate").value = "";
        document.getElementById("toDate").value = "";
        
        document.getElementById("toTime").value = "";
        document.getElementById("fromTime").value= "";
        document.getElementById("processor").value = "";
        //  document.getElementById("setLogLevel").value = "Select";
    }
    self.setDate = function(dateName, dateBtn) {
        newPopupCalender(dateName,dateBtn);
        document.getElementById(dateName).focus();
    }
}
var logBrowser = new LogBrowser();