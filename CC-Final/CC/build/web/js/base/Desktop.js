Desktop = function(){
    var self = this;
    
    var cache = new Hashtable();
    var loaded = false;
    var dirtyBit = '0' ;
    
    var RET = 13; 
    var ESC = 27;
    var CAP = 94; 
    
    
    
    
    self.dirtyBitSet = function() {
        return dirtyBit == '1';
    };
    
    
    self.setDirtyBit = function(eventCheck, ev){
        /*
         * Due to some explict handle of keycode which is done on body keydown, we don not require keyCode check.
         * If you do not want to perform keyCode check here send false as eventCheck i.e., setDirtyBit(false);
         */
        eventCheck = (eventCheck == null ? true : eventCheck);
        var keyCode = baGetKeyCode(ev);
        
        if(eventCheck == true && (keyCode == 126 || keyCode == CAP || 
            keyCode == ESC || (keyCode == RET && baGetEventSource(ev).tagName != "TEXTAREA" ) ||
            keyCode == 18 ) ){
            window.event.returnValue=false;           
            return;
        }     
        
        dirtyBit = '1' ;
        
    };
    
    
    self.isPrintAppletEnabled = function() {
        var response = false;
        try {
            if(document.printApplet.isAppletEnabled() == 1) {
                response = true;
            }else{
                alert("DOS Printer configuration is missing , Please contact system administrator. ");
            }
        } catch (e) {
            alert("DOS Printer configuration is missing , Please contact system administrator. " + e);
        }
                
        return response;
        
    }
    
    //call this function when the form is cleared
    self.resetDirtyBit = function(){    
        
        dirtyBit = '0' ;    
        
    };
    
    //this function will be called internally when any link is clicked. (you dont have to bother about it)
    self.checkTransition = function() {    
        var ans;
        if(dirtyBit == '1'){ 
            ans = confirm("Selected action will discard any entered data. Continue?");
            if(ans) {
                
                dirtyBit = '0' ;
                return true;
            } else {
                return false;
            }
        }
        
        return true ;
    };
    
    
    self.jsLoad = function(jsFile) {
        var f = document.getElementById(jsFile);
        
        if (f != null) { // Already exists
            return;
        }
        
        var head = document.getElementsByTagName("head")[0];
        var script = document.createElement('script');
        script.id = jsFile;
        script.type = 'text/javascript';
        script.src = jsFile + "?jsRequestTime=" + new Date().getTime();
        
        head.appendChild(script);
        
        loadedScriptNodes.push(script);
    };
    
    self.cssLoad = function (cssFile) {
        
        var f = document.getElementById(cssFile);
        
        if (f != null) { // Already exists
            return;
        }
        
        var head = document.getElementsByTagName("head")[0];
        var lnk = document.createElement('link');
        lnk.id = cssFile;
        lnk.type = 'text/css';
        lnk.rel = 'stylesheet';
        lnk.href = cssFile + "?jsRequestTime=" + new Date().getTime();
        head.appendChild(lnk);
    };
    
    var loadedScriptNodes;
    
    function loadJsCssFile() {
        var jsFiles = document.getElementsByName('jsFile');
        debugger;
        loadedScriptNodes = new Array();
        
        for(var i = 0; i < jsFiles.length; i++) {
            var jsFileName = jsFiles[i].value;
            
            self.jsLoad(jsFileName);
        }
        
        var cssFiles = document.getElementsByName('cssFile');
        
        for(i = 0; i < cssFiles.length; i++) {
            var cssFileName = cssFiles[i].value;
            self.cssLoad(cssFileName);
        }
      
    }; 
    
    
    self.checkAllJsLoaded = function(endFunc) {
        
        for(var i = 0; i < loadedScriptNodes.length; i++) {
            //alert(loadedScriptNodes[i].id + ":" + loadedScriptNodes[i].readyState);
            if(loadedScriptNodes[i].readyState == null)
                break;
            
            if(loadedScriptNodes[i].readyState != 'loaded' && loadedScriptNodes[i].readyState != 'complete') {
                setTimeout(function() {
                    self.checkAllJsLoaded(endFunc)
                    }, 200);
                return;
            }
        }
        //alert("Calling init Func");
        
        endFunc();
    };
    
    self.showJSFileCount = function (msg) {
        var scr = document.getElementsByTagName("script");
        var jsf = document.getElementsByName("jsFile");
        alert(msg + scr.length + ", " + jsf.length);
    };
    
    self.handleDocumentStateChange = function (cbFunc) {
        
        alert(document.readyState);
        
        if(document.readyState == 'complete')
            cbFunc();
    };
    
    self.initPopupPage = function() {
        
        loadJsCssFile();
        
        setTimeout(function() {
            self.checkAllJsLoaded(callOnLoadPopupFunctions)
            }, 500);
    };
    
    
    self.initPage = function() {
        
        loadJsCssFile();
        
        setTimeout(function() {
            self.checkAllJsLoaded(callOnLoadFunctions)
            }, 500);
    };
    
    self.initPageTemp = function() {
        
        loadJsCssFile();
        
    //setTimeout(function() {self.checkAllJsLoaded(callOnLoadFunctions)}, 500);
    };
    
    self.callFunction = function(functionType) {
        
        var initCmds = document.getElementsByName(functionType); 
        
        for(var i = 0; i < initCmds.length; i++) {
            evalFunctions(initCmds[i].value);
        }
        self.hideBusyMessage();
    };
    
    function callOnLoadFunctions() {
        /*
        var initCmds = document.getElementsByName('onLoadFunction'); 
         
        for(var i = 0; i < initCmds.length; i++) {
            evalFunctions(initCmds[i].value);
        }
        self.hideBusyMessage();
         */
        self.callFunction('onLoadFunction');
    };
    
    function callOnLoadPopupFunctions() {
        /*
        var initCmds = document.getElementsByName('onLoadPopupFunction');
        for(var i = 0; i < initCmds.length; i++) {
            evalFunctions(initCmds[i].value);
        }
        self.hideBusyMessage();
         */
        self.callFunction('onLoadPopupFunction');
    };
    
    function callOnUnloadPopupFunctions() {
        var initCmds = document.getElementsByName('onUnloadPopupFunction');
        for(var i = 0; i < initCmds.length; i++) {
            evalFunctions(initCmds[i].value);
        }
    };
    
    var callCount = 0;
    function evalFunctions(funName) {
        callCount++;
        var functionName = funName.substring(0, funName.indexOf('('));
        try {
            var functionExist = eval(functionName); // checking if function exists
            try {
                eval(funName); // function is loaded and is executing
            } catch (ex) {
                alert(ex + ", " + ex.message);
                if(ex == "Recall Function") { // Function is loaded but function has subsequent calls to other function which are not loaded. So its called after some time
                    throw "Recall Function";  // The onload function need to handle this 
                } else {
                    throw "Function error";
                }
            }
        } catch (e) {
            if(e == "Function error") {
                alert("Failed to execute onload function '" + funName + "'");
                callCount = 0;
            } else {
                if(callCount < 13) {
                    setTimeout(function() {
                        evalFunctions(funName)
                        }, 2000) ;
                } else {
                    
                    callCount = 0;
                }
            }
        }
    };
    
    self.loadPage = function(featureCommand, featureName) { 
        //alert("suhas testing "+"\nfeatureCommand=" + featureCommand + " \nfeatureName=" + featureName + " \nfeatureCode" + featureCode);
        // Added extra parameter featureCode which is unique and there is no reason we should change it. - Girish
        if(featureCommand != null && featureCommand != '' && document.getElementById('desktopWell') != null) {
            try {
                //featureCommand += '?fName=' + featureName + '&fCode=' + featureCode ;
                featureCommand = PROJECT_CTXT_PATH + featureCommand;
                xmlLoadElementValues(featureCommand, document.getElementById('desktopWell'));
                self.setActivity(featureName);
                self.initPage();  
                clearInlinePopupCache();
            //worklistFormInit();
                
            } catch(e) {
                throw(e);
            }
            
            var tempFeatureCommand = featureCommand.substring(featureCommand.lastIndexOf('/') + 1, featureCommand.length);
            var tempURL = "UDesktop?command=setSelectedFeature&selectedFeature=" + featureCommand;
        //xmlGetResultString(tempURL);
        }
    };
    
    var InlinePopupHandler = function(desktopWell, classHandler) {
        this.desktopWell = desktopWell; 
        this.classHandler = classHandler;
    };
    
    var inlinePopupStack = Array();
    var inlinePopupStackPointer = -1;
    
    self.pushInlinePopup = function(url, classHandler) {
        
        var desktopWell = document.getElementById('desktopContainer');
        if(desktopWell != null) {
            var inlinePopupHandler = new InlinePopupHandler(desktopWell.innerHTML, classHandler);
            cache.put(url, inlinePopupHandler);
            inlinePopupStack[++inlinePopupStackPointer] = url;
            xmlLoadElementValues(url, desktopWell);
            self.initPage();     
        }        
        
    };
    
    self.popInlinePopup = function() {
        var desktopWell = document.getElementById('desktopContainer');
        var key = inlinePopupStack[inlinePopupStackPointer--];
        var inlinePopupHandler = cache.get(key);
        if(desktopWell != null && key != null && inlinePopupHandler != null) {
            if(inlinePopupHandler.desktopWell != null) {
                desktopWell.innerHTML = inlinePopupHandler.desktopWell;
            }
            var classHandler = inlinePopupHandler.classHandler;
            if(classHandler != null) {
                if(typeof(classHandler.refreshHandler) == 'function') {
                    classHandler.refreshHandler();
                }
            }
            
            self.clearCahce(key);
            inlinePopupStack[inlinePopupStackPointer + 1] = null;
        }
        
        desktop.reInit();
    };    
    
    self.clearCahce = function(key){
        cache.remove(key) ;
    };
    
    
    
    
    
    
    var inlineWorkAreaPopupStack = Array();
    var inlineWorkAreaPopupStackPointer = -1;
    
    self.pushInlineWorkAreaPopup = function(url, classHandler) {        
        var desktopWell = document.getElementById('desktopWell');
        if(desktopWell != null) {
            var inlinePopupHandler = new InlinePopupHandler(desktopWell.innerHTML, classHandler);
            cache.put(url, inlinePopupHandler);
            inlineWorkAreaPopupStack[++inlineWorkAreaPopupStackPointer] = url;
            xmlLoadElementValues(url, desktopWell);
            self.initPage();     
        }        
    };
    
    self.popInlineWorkAreaPopup = function() {
        var desktopWell = document.getElementById('desktopWell');
        var key = inlineWorkAreaPopupStack[inlineWorkAreaPopupStackPointer--];
        var inlinePopupHandler = cache.get(key);
        if(desktopWell != null && key != null && inlinePopupHandler != null) {
            if(inlinePopupHandler.desktopWell != null) {
                desktopWell.innerHTML = inlinePopupHandler.desktopWell;
            }
            var classHandler = inlinePopupHandler.classHandler;
            if(classHandler != null) {
                if(typeof(classHandler.refreshHandler) == 'function') {
                    classHandler.refreshHandler();
                }
            }
            
            self.clearCahce(key);
            inlineWorkAreaPopupStack[inlineWorkAreaPopupStackPointer + 1] = null;
        }
        
        desktop.reInit();
    };
    
    
    
    
    
    
    
    
    
    function clearInlinePopupCache() {
        if(inlinePopupStackPointer > -1) {
            for(var i = 0; i <= inlinePopupStackPointer; i++) {
                var key = inlinePopupStack[i];
                self.clearCahce(key);
                inlinePopupStack[i] = null;
            }
        }
    };
    
    self.showBusyMessage = function(){
        desktop.setActivity('');
        var desktopWell = document.getElementById('desktopWell') ;  
        if(desktopWell != null) {
            desktopWell.innerHTML = "<div id='desktopLoadingMsgDiv' align='right' style='height:50px;' class='wellHeader' ><br><span class=loadingMessage >&nbsp;Loading... Please wait...&nbsp;&nbsp;</span></div>";
            
        }
    };    
    
    self.unHideBusyMessage = function(){
        var desktopLoadingMessageDiv = document.getElementById('desktopLoadingMessageDiv') ;  
        
        if(desktopLoadingMessageDiv != null) {
            
            desktopLoadingMessageDiv.style.display = "block";
        }
    };  
    
    self.hideBusyMessage = function() {
        var desktopLoadingMessageDiv = document.getElementById('desktopLoadingMessageDiv') ;  
        if(desktopLoadingMessageDiv != null) {
            desktopLoadingMessageDiv.style.display = "none";
        }
    };
    
    
    self.showLoading = function(ev){
        
        if(!ev) 
            ev = window.event;
        
        var loadingDiv = document.getElementById('loadingDiv');
        if(loadingDiv != null) {
            var screenWidth = document.documentElement.clientWidth;
            var left = (baGetMouseX(ev) > (screenWidth - 200)) ? (screenWidth - 200) : baGetMouseX(ev);
            loadingDiv.style.left = left + "px";
            loadingDiv.style.top = document.documentElement.scrollTop + baGetMouseY(ev) + "px";
            loadingDiv.style.display = "inline";
        }
        
    };
    
    var currentURL = null;
    var elemToBeReplaced = null;
    self.showReloginDiv = function(url, elem) {
        
        currentURL = null;
        elemToBeReplaced = null;
        
        if(url && elem) {
            currentURL = url;
            elemToBeReplaced = elem;    
        }
        
        var reloginDiv = document.getElementById('reloginDiv');
        var desktopReloginIFrame = document.getElementById('desktopReloginIFrame');
        
        if(reloginDiv != null) {
            reloginDiv.style.left =  "300px";
            reloginDiv.style.top = document.documentElement.scrollTop + 200 + "px";
            reloginDiv.style.display = "inline";
            document.getElementById('loginPassWord').value = '';
            document.getElementById('loginPassWord').focus()
            
            desktopReloginIFrame.style.left =  "300px"
            desktopReloginIFrame.style.top = document.documentElement.scrollTop + 200 + "px";
            desktopReloginIFrame.style.display = "inline";
            desktopReloginIFrame.style.width = reloginDiv.style.width;
            desktopReloginIFrame.style.height = reloginDiv.style.height;
            document.getElementById('btnRelogin').disabled = false;
            document.getElementById('btnCloseRelogin').disabled = false;
        // parentElem.appendChild(reloginDiv);
        // parentElem.appendChild(desktopReloginIFrame);
            
        }
        
        if(document.getElementById('desktopPopupWell'))
            document.getElementById('desktopPopupWell').className = 'hidden';
        
    }
    
    self.reloginOnEnter = function(ev) {
        var kc = baGetKeyCode(ev);
        
        if(kc != null && kc == 13) {
            self.relogin();
        }
    }
    
    self.relogin = function() {
        document.getElementById('btnRelogin').disabled = true;
        document.getElementById('btnCloseRelogin').disabled = true;
        
        var password = document.getElementById('loginPassWord').value;
        var userID = document.getElementById('userID').value;
        var generatedSessionID = document.getElementById('generatedSessionID').value;
        
        var url = PROJECT_CTXT_PATH + "/Login?command=relogin&userName=" + userID + 
        "&password=" + escape(password) +  
        "&generatedSessionID=" + generatedSessionID;
        
        if(document.getElementById("projectID")){
            url += "&projectID="+document.getElementById("projectID");
        }
        
        var response = xmlPostSync(url);
        document.getElementById('btnRelogin').disabled = false;
        document.getElementById('btnCloseRelogin').disabled = false; 
        if(response != 1) {
            
            
            alert('Login Failed. Please verify the password and try again')
            document.getElementById('loginPassWord').value = '';
            document.getElementById('loginPassWord').focus();
            return;
        }
        
        /*
        var cmb = document.getElementById('cmbUnit');
        try {
            var unitRID = cmb.options[cmb.selectedIndex].value;
            var unitName = cmb.options[cmb.selectedIndex].text;
            var url = PROJECT_CTXT_PATH + "/UUnitServlet?command=setUnit&unitRID=" + unitRID + "&unitName="+unitName; 
            xmlPostSync(url);
        } catch(e) {
            // do nothing
        }
        */
        self.closeReloginDiv(true);
        
    /*if(currentURL && elemToBeReplaced == document.getElementById('desktopWell')) {
            self.loadPage(currentURL, '', '');
        }*/
    }
    
    self.closeReloginDiv = function(success) {
        document.getElementById('reloginDiv').style.display = "none";
        document.getElementById('desktopReloginIFrame').style.display = "none";
        
        if(document.getElementById('desktopPopupWell'))
            document.getElementById('desktopPopupWell').className = 'visible';
    }
    
    
    self.hideLoading = function(){
        var loadingDiv = document.getElementById('loadingDiv');
        if(loadingDiv != null) {
            loadingDiv.style.display = "none";
        }
    };
    
    self.getGeneratedSessionID = function() {
        return document.getElementById('generatedSessionID').value;
    }
    
    self.hideBusyMessage = function() {
        var desktopLoadingMessageDiv = document.getElementById('desktopLoadingMessageDiv') ;  
        if(desktopLoadingMessageDiv != null) {
            desktopLoadingMessageDiv.style.display = "none";
        }
    };
    
    self.handlePageUnload = function(){
        var fn = document.getElementById('onUnloadFunction');
        
        if(fn != null) {
            eval(fn.value);
        }
    };
    
    self.signout = function(){
        if(confirm("Are you sure?")){
            
            document.getElementById("frmLogin").submit();
        }
        
    }
    
    self.handleKeyDown = function(ev) {  
        
        var kc = baGetKeyCode(ev);        
        
        var fn = document.getElementById('keyDownHandler');
        
        if(fn != null) {
            eval(fn.value);
        }
    };
    
    self.handleBackspace = function(ev) {
        var kc = baGetKeyCode(ev);
        
        if(kc != null && kc == 8) {
            baCancelEvent(ev);
        }
    };
    
    self.handleRefresh = function() {
        var fn = document.getElementById('refreshHandler');
        
        if(fn != null) {
            eval(fn.value);
        }
    };
    
    self.handleUnitChange = function(unitRID) {   
        var fn = document.getElementById('unitChangeHandler');
        
        if(fn != null) {
            eval(fn.value + "(" + unitRID + ");");
        }
    };
    
    self.putIntoCache = function(key, value) {
        try {
            cache.put(key, value);
        } catch(e) {
        //Nothing to do
        }
    };
    
    self.getFromCache = function(key, url) {
        
        var r = cache.get(key);
        
        if(r == null && url != null) {
            
            //alert("Loading cache from server (" + key + ")");
            
            self.putIntoCache(key, xmlGetResultString(url));
            
            r = cache.get(key);
        }
        
        return r;
    };
    
    self.setActivity = function(description) {
        if(document.getElementById('currentActivity') != null) {
            document.getElementById('currentActivity').value = description;
        }
    };
    
    
    var popupDiv = new UPopupDiv();
    
    self.showPopup = function(title, url, popupArg) {
        var tempPopupArg = popupArg;
        tempPopupArg.onPopupInit = "desktop.initPopupPage()";
        
        return popupDiv.showPopup(title, url, tempPopupArg);
        
    };
    
    self.closePopup = function(popupID) {
        debugger;
        popupDiv.closePopup(popupID);
        callOnUnloadPopupFunctions();
    };
    
    self.startPopup = function(ev){
        popupDiv.startPopup(ev);
    };
    
    self.stopPopup = function(){
        popupDiv.stopPopup();
    };
    
    self.movePopup = function(ev){
        popupDiv.movePopup(ev);
    };
    
    self.confirmMessage = function(successMsg){
        var targetIFrame = document.getElementById('statusMessageFrame') ;  
        var targetDiv = document.getElementById('statusMessageDiv') ;  
        targetIFrame.style.display = 'inline' ;
        targetDiv.style.display = 'inline' ;
        var tempSpan = targetDiv.getElementsByTagName('span');    
        tempSpan[0].innerHTML = successMsg ;
        setTimeout(hideDiv,2000) ;
    };     
    
    function hideDiv(){
        var targetDiv = document.getElementById('statusMessageDiv') ;
        targetDiv.style.display = 'none' ;
        var targetIFrame = document.getElementById('statusMessageFrame') ; 
        targetIFrame.style.display = 'none' ;            
    };
    
    self.getHandler = function(handlerID){
        
        // First check if we have an active popup. If so, we need to return the
        // the handler from the popup form's context
        var containerHandlerName = popupDiv.getContainerHandler();
        
        if(popupDiv.hasContainerHandler() && containerHandlerName != null) {
            // for now its hard coded, later we need to find different logic
            
            var handlerName = "";
            if(handlerID == 'successHandler') {
                handlerName = 'handleSuccess';
            } else  if(handlerID == 'failureHandler') {
                handlerName = 'handleFailure';
            } else  if(handlerID == 'reloadSuccessHandler') {
                handlerName = 'handleSuccessReload';
            }
            
            return (containerHandlerName + "." + handlerName);
        } 
        
        var handler = document.getElementById(handlerID);
        
        if(handler != null) {
            return handler.value;
        }
        
        return null;
    };
    
    self.getUserUnitRID = function() {
        return document.getElementById('cmbUnit').value;
    };
    
    self.checkConnection = function() {
        var url = PROJECT_CTXT_PATH  + "/UDesktop?command=checkConnection";
        try {
            var response = xmlGetResultString(url);
            return (response == 1);
        } catch(e) {
            return false;
        }
    };
    
    self.setOpacity = function(obj, value) {
        obj.style.opacity = value / 10;
        obj.style.filter = 'alpha(opacity=' + value * 10 + ')';
    };
    
    self.fadeInDesktop = function() {
        self.setDesktopBodyOpacity(2);
    };
    
    self.fadeOutDesktop = function() {
        self.setDesktopBodyOpacity(10);
    };
    
    self.setDesktopBodyOpacity = function(value) {
        var desktopBody = document.getElementById("desktopBody");
        self.setOpacity(desktopBody, value);
    };
    
    self.openInset = function(row, url, ev, multiLevel, disableBorder) {
        // @@ check if the row already exists
        if(baGetNextSibling(row) != null && baGetNextSibling(row).attributes['detailsRowExpanded'] != null) {
            self.closeInset(baGetNextSibling(row));
        //return;
        }
        
        desktop.showLoading(ev);
        
        var targetDiv = createInsetRow(row, null, multiLevel, disableBorder);
        xmlLoadElementValues(url, targetDiv);
        desktop.hideLoading();
    };
    
    var lastCreatedInsetRow = null; 
    
    function createInsetRow(row, handleClose, multiLevel, disableBorder) {
        // @@ creating a row for loading the page
        var newRow = document.createElement('tr');
        //newRow.className = "inset";
        newRow.setAttribute("detailsRowExpanded", "true");
        
        var tdElem = document.createElement('td');
        tdElem.colSpan = row.cells.length;
        tdElem.width = "100%";
        if(disableBorder == null){
            tdElem.className = "inset";
        }
        var divElem = document.createElement('div');
        divElem.className = "whiteBG";
        tdElem.appendChild(divElem);
        newRow.appendChild(tdElem);
        if(lastCreatedInsetRow && !multiLevel) {
            try {
                dynTableDeleteRow(lastCreatedInsetRow);
            } catch(e) {
            // do nothing
            }
        }
        lastCreatedInsetRow = row.parentNode.insertBefore(newRow, baGetNextSibling(row));
        /*
        if(handleClose) {
            // @@ creating a row for close button
            var newRow2 = document.createElement('tr');
            newRow2.className = "inset";
         
            var tdElem2 = document.createElement('td');
            tdElem2.colSpan = row.cells.length;
            tdElem2.width = "100%";
            tdElem2.align = "right";
            tdElem2.innerHTML = "<input type='button' value='Close' onclick='desktop.closeInset(this)' >";
         
            newRow2.appendChild(tdElem2);
            newRow.parentNode.insertBefore(newRow2, baGetNextSibling(newRow));
        }
         */
        return divElem;
    };
    
    self.closeInset = function(elem) {
        var row = dynTableRow(elem);
        //dynTableDeleteRow(baGetPreviousSibling(row));
        dynTableDeleteRow(row);
    };
    
    self.reInit = function() {
        userProfile.refresh();
        loadAccordion(); 
        boWorklist.refreshWorklistTable();
    };
    
    self.showErrorDetails = function() {
        if(document.getElementById('stackTraceTR').className == 'hidden') {
            document.getElementById('stackTraceTR').className = 'visible';
            document.getElementById('showErrDetailLink').innerHTML = 'Hide Details..';
        } else {
            document.getElementById('stackTraceTR').className = 'hidden';
            document.getElementById('showErrDetailLink').innerHTML = 'Show more details..';
        }
    }
    
    self.showErrorMessage = function(errString) {
        
        var errorDisplay = document.getElementById('errorDisplay');
        var errorDisplayTD = document.getElementById('errorDisplayTD');
        
        if(!errorDisplay)
            return;
        
        errorDisplayTD.innerHTML = errString;
        if(document.getElementById("errorPageString")) {
            var errorString = document.getElementById("errorPageString").innerHTML;
            if(errorString == ': This feature is not permitted from the selected unit' || errorString == 'This feature is not permitted from the selected unit'
                || errorString == ': ubq.base.UDBAccessException: This feature is not permitted from the selected unit') {
                errorDisplay.className = "hidden";
                alert("This feature is not permitted from the selected unit");
                errorDisplayTD.innerHTML = "";
                errorDisplay.className = "shadow whiteBG collapseBorder";
                var desktopLoadingMsgDiv = document.getElementById("desktopLoadingMsgDiv");
                if(desktopLoadingMsgDiv) {
                    dynTableDeleteRow(desktopLoadingMsgDiv);
                }
                return;
            } 
        }
        errorDisplay.style.left =  "300px";
        errorDisplay.style.top = document.documentElement.scrollTop + 200 + "px";
        errorDisplay.style.display = "inline";
    }
    
    self.closeErrorDisplay = function() {
        
        var errorDisplay = document.getElementById('errorDisplay');
        var errorDisplayTD = document.getElementById('errorDisplayTD');
        if(!errorDisplay)
            return;
        
        errorDisplayTD.innerHTML = '';
        errorDisplay.style.display = "none";   
    }

    function findElementPos(element) {
        var curleft = curtop = 0;

        if(element.offsetParent) {
            do {
                curleft += element.offsetLeft;
                curtop += element.offsetTop;
            } while (element = element.offsetParent); // '=' is intentionally used. It is not '=='!
        }

        return [curleft, curtop];
    }

    self.extendElement = function (element, extendDirection) {
        var height = 0;
        var width = 0;
        var body = window.document.body;

        if (window.innerHeight) {
            height = window.innerHeight;
            width = window.innerWidth;
        } else if (body.parentElement.clientHeight) {
            height = body.parentElement.clientHeight;
            width = body.parentElement.clientWidth;
        } else if (body && body.clientHeight) {
            height = body.clientHeight;
            width = body.clientWidth;
        }

        var elementLeft = findElementPos(element)[0];
        var elementTop = findElementPos(element)[1];

        if(extendDirection == "HEIGHT") {
            element.style.height = ((height - elementTop - 4) + "px");
        } else if(extendDirection == "WIDTH") {
            element.style.width = ((width - elementLeft - 20) + "px");
        }else {
            element.style.height = ((height - elementTop - 4) + "px");
            element.style.width = ((width - elementLeft - 20) + "px");
        }
    }
    
    self.extendElementHeight = function (element) {
        self.extendElement(element, "HEIGHT");
    }

    self.extendElementWidth = function (element) {
        self.extendElement(element, "WIDTH");
    }


}; // end of class



function handleSuccess(successMsg) {
    var fn = desktop.getHandler('successHandler');
    if(fn != null) {
        var callStr = fn + "(\"" + successMsg + "\");";
        eval(callStr);
    }
};

function handleFailure(errorMsg) {
    
    var fn = desktop.getHandler('failureHandler');
    
    if(fn != null)
        eval(fn + "(\"" + errorMsg + "\");");
};

function handleSuccessReload(reloadPage, url) {
    var fn = desktop.getHandler('reloadSuccessHandler');
    
    if(fn != null)
        eval(fn + "(\"" + reloadPage + "\", \"" + url + "\");");
};


var UPopupDiv = function(){
    var self = this;
    var containerHandler = new Hashtable();
    var containerPointer = -1;
    var uniquePopup = new Hashtable(); // if the popup should be opened only once
    
    self.getContainerHandler = function(){
        return containerHandler.get(containerPointer);
    };
    
    self.hasContainerHandler = function(){
        return (containerPointer > -1);
    };
    
    function popupIsOpen(title, id) {
        
        if(uniquePopup.get(id) != null) {
            
            if(title != '') {
                alert('Only one instance of ' + title + ' can be opend')
            } else {
                alert('Only one instance of the popup window can be opened')
            }
            
            return true;
        }
        
        return false;
    }
    
    self.showPopup = function(title, url, popupArg) {
        
        if(url == null) {
            alert('Please specify the url for popup window');
            return false;
        }
        
        var id = null;
        
        title = (title == null) ? "" : title;
        
        // We do not allow multiple instances of the same window. First check 
        // if the requested popup is already open. If so, display message and do
        // nothing
        
        if(popupArg != null) {
            id = popupArg.id; 
            
            if(popupIsOpen(title, id))
                return false;
        }
        
        // First time request for popup. We can proceed
        
        var width, height, top, left, scrollable = false, floatable = false, containerHandlerName;
        var onCloseFun, onPopupInit, onLoad, offlineCbFunc, explicitOffline, hideTitleBar;
        
        // Setup popup arguements
        if(popupArg != null) {
            width = popupArg.width;
            height = popupArg.height;
            top = popupArg.top;
            left = popupArg.left;
            scrollable = popupArg.scrollable;
            floatable = popupArg.floatable;
            containerHandlerName = popupArg.containerName;
            onCloseFun = popupArg.onCloseFun;
            onPopupInit = popupArg.onPopupInit;
            onLoad = popupArg.onLoad;
            
            offlineCbFunc = popupArg.offlineCbFunc;
            explicitOffline = popupArg.explicitOffline;
            hideTitleBar = popupArg.hideTitleBar;
        }
        
        containerPointer++;
        
        if(id != null) {
            uniquePopup.put(id, containerPointer);
            uniquePopup.put(containerPointer, id);
        }
        
        createPopup(containerPointer, onCloseFun, hideTitleBar); // create a node for popup
        
        //        document.getElementById('desktopPopUpWorkArea' + containerPointer).innerHTML = "<h5>&nbsp;&nbsp;Loading... Please wait...<h5>";
        
        xmlLoadElementValues(url, document.getElementById('desktopPopUpWorkArea' + containerPointer)); 
        
        setTimeout(function() {
            
            if(onPopupInit != null) {
                eval(onPopupInit);
            }
            
            if(onLoad != null) {
                try{
                    
                    if(typeof(onLoad) == "function") {
                        onLoad();
                    } else if (typeof(onLoad) == "string"){
                        eval(onLoad);
                    }
                    
                } catch(ex) {
                    // Error while calling onLoad function. Request user to try again.
                    alert("Desktop error: Failed to execute " + onLoad + "...please try again");
                }
            }
            
            document.getElementById('desktopPopUpTitle' + containerPointer ).innerHTML = "&nbsp;" + title;
            
        }, 0); 
        
        if(containerHandlerName != null) {
            containerHandler.put(containerPointer, containerHandlerName);
        }
        
        var desktopPopUpDiv = document.getElementById('desktopPopUpDiv' + containerPointer);
        var desktopPopUpFrame = document.getElementById('desktopPopUpFrame' + containerPointer);
        var desktopPopUpScroll = document.getElementById('desktopPopUpScroll' + containerPointer);
        var desktopPopUpWorkArea = document.getElementById('desktopPopUpWorkArea' + containerPointer);
        
        document.getElementById('desktopPopUpTitle' + containerPointer ).innerHTML = "&nbsp;" + title + "...please wait...";
        desktopPopUpDiv.style.visibility = 'visible';
        desktopPopUpDiv.style.display = 'inline';
        desktopPopUpFrame.style.visibility = 'visible';
        desktopPopUpFrame.style.display = 'inline';
        
        if(scrollable) {
            if(width == null) {
                width = 700;
            }
            
            if(height == null) {
                height = 400;
            }
        } else {
            if(height == null) {
                height = desktopPopUpDiv.offsetHeight;    
            }
            
            if(width == null) {
                width = desktopPopUpDiv.offsetWidth;    
            }
        }
        
        
        if(top == null) {
            var screenHeight = document.documentElement.clientHeight;
            var scrollTop = document.documentElement.scrollTop;
            top = ((screenHeight - height) / 2 - 20) ;
            if(top < 0) {
                top = 5;
            }
            top += scrollTop;
        }
        
        if(left == null) {
            var screenWidth = document.documentElement.clientWidth;
            var scrollLeft = document.documentElement.scrollLeft;
            left = (screenWidth - width) / 2 + 7;
            if(left < 0) {
                left = 5;
            }
            left += scrollLeft;
        }
        
        desktopPopUpDiv.style.height = height + "px";
        desktopPopUpDiv.style.width = width + "px";
        desktopPopUpFrame.style.height = (height + 2) + "px";
        desktopPopUpFrame.style.width = width + "px";
        
        if(scrollable) {
            desktopPopUpFrame.style.height  = (height + 20) + "px";
            desktopPopUpScroll.style.height = height + "px";
            desktopPopUpScroll.style.width = '100%';
            if(baIsIEBrowser()) {
                //desktopPopUpWorkArea.style.width = '97.5%';
                desktopPopUpWorkArea.style.width = '100%';
            }
            desktopPopUpScroll.style.overflow = 'auto';
        } else {
            desktopPopUpScroll.style.height = '';
            desktopPopUpScroll.style.width = '';
            if(baIsIEBrowser()) {
                desktopPopUpWorkArea.style.width = '100%';
            }
            desktopPopUpScroll.style.overflow = '';
        }
        
        
        
        if(floatable) {
            JSFX_FloatDiv("desktopPopUpDiv" + containerPointer, left, top).floatIt();
            JSFX_FloatDiv("desktopPopUpFrame" + containerPointer, left, top).floatIt();
        } else {
            desktopPopUpDiv.style.top = top + "px";
            desktopPopUpDiv.style.left = left + "px";
            desktopPopUpFrame.style.top = top + "px";
            desktopPopUpFrame.style.left = left + "px";
        }
        
        //desktop.fadeInDesktop();
        
        return containerPointer;
    };
    
    
    self.closePopup = function(popupID){

        removePopup(popupID);
        uniquePopup.remove(uniquePopup.get(containerPointer));
        uniquePopup.remove(containerPointer);
        containerHandler.remove(containerPointer);
        containerPointer--;
    /*
        if(containerPointer < 0) {
            desktop.fadeOutDesktop();
        }
         */
    };
   
    
    var ns = (navigator.appName.indexOf("Netscape") != -1);
    var d = document;
    function JSFX_FloatDiv(id, sx, sy) {       
        var el=d.getElementById?d.getElementById(id):d.all?d.all[id]:d.layers[id];
        var px = document.layers ? "" : "px";
        window[id + "_obj"] = el;
        if(d.layers)el.style=el;
        el.cx = el.sx = sx;
        el.cy = el.sy = sy;
        el.sP=function(x,y){
            this.style.left=x+px;
            this.style.top=y+px;
        };
        
        el.floatIt=function() {
            var pX, pY;
            pX = (this.sx >= 0) ? 0 : ns ? innerWidth : 
            document.documentElement && document.documentElement.clientWidth ? 
            document.documentElement.clientWidth : document.body.clientWidth;
            pY = ns ? pageYOffset : document.documentElement && document.documentElement.scrollTop ? 
            document.documentElement.scrollTop : document.body.scrollTop;
            if(this.sy<0) 
                pY += ns ? innerHeight : document.documentElement && document.documentElement.clientHeight ? 
                document.documentElement.clientHeight : document.body.clientHeight;
            this.cx += (pX + this.sx - this.cx)/8;
            this.cy += (pY + this.sy - this.cy)/8;
            this.sP(this.cx, this.cy);
            setTimeout(this.id + "_obj.floatIt()", 1);
        }
        return el;
    };
    
    
    var popupMove = false;
    var fixLeft = false;
    var tempMouseX = 0;
    
    self.stopPopup = function() {
        popupMove = false;
        fixLeft = false;
    };
    
    self.startPopup = function(ev) {
        popupMove = true;
    };
    
    self.movePopup = function(evt) {
        if (popupMove ) {
            var mouseX = evt.pageX ? evt.pageX : evt.clientX;
            mouseY = evt.pageY ? evt.pageY : document.documentElement.scrollTop + evt.clientY;
            var desktopPopUpDiv = document.getElementById('desktopPopUpDiv' + containerPointer);
            var desktopPopUpFrame = document.getElementById('desktopPopUpFrame' + containerPointer);
            
            if(desktopPopUpDiv != null && desktopPopUpFrame != null) {
                
                if(fixLeft == false) {
                    tempMouseX = (mouseX - desktopPopUpDiv.offsetLeft);
                    fixLeft = true;
                }
                mouseX = mouseX - 5;
                mouseY = mouseY - 15;
                desktopPopUpDiv.style.left = (mouseX - tempMouseX) + "px";
                desktopPopUpDiv.style.top = mouseY + "px";
                desktopPopUpFrame.style.left = (mouseX - tempMouseX) + "px";
                desktopPopUpFrame.style.top = mouseY + "px";
            }
        }
    };
    
    
    
    function createPopup(popupID, onCloseFun, hideTitleBar) {
       
        var desktopPopupWell = document.getElementById("desktopPopupWell");
        var div = document.createElement('div');
        div.id = 'popup' + popupID;
        div.onCloseFun = onCloseFun;
        div.innerHTML = "<iframe id='desktopPopUpFrame" + popupID + "' style='position:absolute;width:400px;height:400px;display:none;z-index:2;' frameborder='0' scrolling='no' ></iframe>" +  
        "<div id='desktopPopUpDiv" + popupID + "' style='visibility:hidden;display:none;position:absolute;background-color:white;z-index:2;'>" +
        "<table width='100%' cellpadding='0' cellspacing='0' border='0' " + (hideTitleBar == true ? "" : "class='popupColor'") + " >" +
        "<tr " + (hideTitleBar == true ? "class='popupColor hidden'" : "class='popupColor visible'") + " >" +
        "<td  width='55%' id='desktopPopUpTitle" + popupID + "' height='25px' onmousedown='desktop.startPopup(event)' style='cursor:crosshair;'  >" +
        
        "</td>" +
        "<td width='5%' align='right'><span id='navigationSpan' style='cursor:pointer;' title='Close window' " +
        " onclick='desktop.closePopup(" + popupID + ")'></u>&nbsp;</span>" +
        "</td>" +
        
        "<td width='5%' align='right'><span style='cursor:pointer;' title='Close window' " +
        " onclick='desktop.closePopup(" + popupID + ")'><u>Close</u>&nbsp;</span>" +
        "</td>" +
        "</tr>" +
        "<tr>" +
        "<td colspan='3' style='padding-left:0px;' > " +
        "<div id='desktopPopUpScroll" + popupID + "' ><div id='desktopPopUpWorkArea" + popupID + "' style='width:100%' ></div></div>" +
        "</td>" +
        "</tr>" +
        "</table>" +
        "</div>";
        
        desktopPopupWell.appendChild(div);
    };
    
    function removePopup(popupID) {
        if(popupID == null) {
            popupID = containerPointer;
        }
        
        var desktopPopupWell = document.getElementById("desktopPopupWell");
        var popupDiv = document.getElementById('popup' + popupID);
        if(popupDiv.onCloseFun != null) {
            
            if(typeof(popupDiv.onCloseFun) == "function") {
                popupDiv.onCloseFun();
            } else if (typeof(popupDiv.onCloseFun) == "string"){
                eval(popupDiv.onCloseFun);
            }            
        //eval(popupDiv.onCloseFun);
        }
        
        desktopPopupWell.removeChild(popupDiv);
    };
    
}; // End of pop function


var desktop = new Desktop();
