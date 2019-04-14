var userCodeValue = null;
var formName = null;
function UserCode() {
    var self = this;
    var jsFunctionName = null;
    var openedURL = null;
    var isValueEntered = false;
    var isUsrCodePopUpExist = false;
    
    self.openUserCode = function(jsFunction, url) {
        if(!(isUsrCodePopUpExist)) {
            userCode.setAllGlobalValues(jsFunction, url);
            var url = PROJECT_CTXT_PATH + "/ConfigServlet?command=openUserCodeJsp"; 
            desktop.showPopup("Enter User Code", url, {width:180, height:50, onCloseFun:'userCode.resetAllGlobalValues()'}); 
        } else {
            userCode.evaluateTheFunction();     
        }
    }
    
    self.setUserCodeValue = function(event) {
        if(baGetKeyCode(event) == 13) {
            var userCodeTxtValue = document.getElementById("userCodeTxt").value;
            if(userCodeValue = "") {
                alert("Please enter user code");
                return;
            }
            userCodeValue = userCodeTxtValue;
            isValueEntered = true;
            userCode.evaluateTheFunction();
            userCode.resetAllGlobalValues();
            desktop.closePopup();
        }
    };
    
    self.resetAllGlobalValues = function(){
        jsFunctionName = null;
        openedURL = null;
        userCodeValue = null;
        isUsrCodePopUpExist = false;
    }
    
    self.setAllGlobalValues = function(jsFunction, url) {
        jsFunctionName = jsFunction;
        openedURL = url;
        isUsrCodePopUpExist = true;
    }
    
    self.evaluateTheFunction = function(){
        if(jsFunctionName != null) {
            openedURL += "&userCode="+ userCodeValue;
            var evalString  = jsFunctionName + "('" + openedURL + "')";
            eval(evalString);
        } 
    }
    
    self.isUserEnteredCode = function() {
        if(isUsrCodePopUpExist) {
            return true;
        } else {
            return false;
        }
    }
    
    self.returnUserCode = function(contextMsg) {
        var returnUserCode = -1;
        var commonUserInfoFlag  = 0;
        if(document.getElementById('commonUserInfoFlag')) {
            commonUserInfoFlag = document.getElementById('commonUserInfoFlag').value; 
            if(commonUserInfoFlag == 0 || commonUserInfoFlag == "0") {
                returnUserCode = -2;
                return  returnUserCode;
            }
        }
        
       
        var dialogWidth = 0;
        var dialogHeight = 0;
        
        var actionName = "";
        if (document.getElementById('actionName')) {
            actionName = document.getElementById('actionName').value;    
        }
        
        if (baIsIEBrowser()) {
            //For IE
            dialogWidth = '600px';
            dialogHeight = '100px';
        } else {
            //For Mozilla
            dialogWidth = '680px';
            dialogHeight = '120px';
        }
        if(typeof contextMsg == "undefined") {
            contextMsg = "";
        }
        var url = PROJECT_CTXT_PATH + "/ConfigServlet?command=openUserCodeJsp&contextLabel=authentication&contextMsg=" + contextMsg + "&actionName=" + actionName; 
        returnUserCode = window.showModalDialog(url,"titlebar=0, toolbar=no,location=no,directories=no, scroll=no, status=no,menubar=no,scrollbars=no,resizable=1,copyhistory=no", "dialogWidth:400px; dialogHeight:125px;dialogLeft:400px;dialogTop:300px;");
        returnUserCode = (baIsIEBrowser()) ? returnUserCode : userCodeValue ;
        if(returnUserCode == "" || returnUserCode == null) {
            returnUserCode = -1;
        }
        userCodeValue = null;
        return returnUserCode;
    }

     self.getAutherizationUserCode = function(contextMsg) {
        var returnUserCode = -1;
        var dialogWidth = 0;
        var dialogHeight = 0;
        
        if (baIsIEBrowser()) {
            //For IE
            dialogWidth = '600px';
            dialogHeight = '100px';
        } else {
            //For Mozilla
            dialogWidth = '600px';
            dialogHeight = '100px';
        }
        if(typeof contextMsg == "undefined") {
            contextMsg = "";
        }
        var url = PROJECT_CTXT_PATH + "/ConfigServlet?command=openUserCodeJsp&contextLabel=authorization&contextMsg="+contextMsg; 
        returnUserCode = window.showModalDialog(url,"titlebar=0, toolbar=no,location=no,directories=no, scroll=no, status=no,menubar=no,scrollbars=no,resizable=1,copyhistory=no", "dialogWidth:375px; dialogHeight:125px;dialogLeft:400px;dialogTop:300px;");
        returnUserCode = (baIsIEBrowser()) ? returnUserCode : userCodeValue ;
        if(returnUserCode == "" || returnUserCode == null) {
            returnUserCode = -1;
        }
        userCodeValue = null;
        return returnUserCode;
    }
     
    self.showValidation = function(url, methodType, handlerName, formName, targetName){
        var popup = PROJECT_CTXT_PATH + '/jsp/billing/ValidateUser.jsp';
        var index = 0;
        var targetUrl = "";
        desktop.showPopup("User Authenication", popup,"");

        document.getElementById('acceptBtn').onclick = function(){
            var user_Code = document.getElementById('userCode').value;
            if(user_Code == ""){
                alert("Please enter a valid User Code");
                return;
            }
            index = url.indexOf('?');
            if(index > 0){
                targetUrl = url + '&userCode='+ user_Code;
            }
            else{
                targetUrl = url + '?userCode='+ user_Code;
            }
            // Clear the password box once the user clicks the button.
            document.getElementById('userCode').value='';
            
            if(formName == null || formName == ""){
                
                if(methodType == "POST" || methodType == "post"){
                    // Handle POST requests.
                    var response = xmlPostSync(targetUrl);
                    if(response == 1){
                        desktop.closePopup();
                    }
                    handlerName(response);
                }
                else if(methodType == "GET" || methodType == "get"){
                     // Handle GET requests.
                    var response = xmlGetResultString(url);
                    if(response == 1){
                       desktop.closePopup();
                    }
                    handlerName(response);
                }
                else{
                    // Inappropriate data passed.
                    alert("Invalid data passed !!!");
                    return;
                }
            }
            else{
                // Handle authenication if the request is form submit.
                //var response = xmlGetResultString(targetUrl);
                //if(response == 1){
                //    desktop.closePopup();
                //}
                this.formName = formName;
                this.formName.method = methodType;
                this.formName.action = targetUrl;
                this.formName.target = targetName;
                this.formName.submit();
           }
        }
        return;
    }
}
var userCode = new UserCode();