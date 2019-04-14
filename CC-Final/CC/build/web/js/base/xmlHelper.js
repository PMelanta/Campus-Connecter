var xmlHttp;

var _elem;
 
var _g = 0;

function _cb() {
    if(_g == 0) { _g++; return;} // To avoid the xmlHttp.readyState undefined problem
    
    if (xmlHttp.readyState == 4) {
        if (xmlHttp.status == 200) {
            _elem.parentNode.innerHTML = xmlHttp.responseText
            
            xmlHttp = null;
            _g = 0;
            _elem = null;
        } else {
            alert("There was a problem retrieving the XML data:\n" +
            xmlHttp.statusText);
        }
    }
}

function _loadElementValuesSync(url, elem) {
    
    _g = 0; 
    
    xmlGetSync(url);
    
    if (xmlHttp.status == 200) {
        if (isSessionExpired()) { 
            doRelogin(url, elem);
            return;    
        }    
        _parent = elem.parentNode;
    
        elem.parentNode.innerHTML = xmlHttp.responseText;

        var tempResponseText = xmlHttp.responseText;

        var _new_elem = null;

        // @@ For some reason in Firefox _parent.childNodes[0] does not
        // @@ return the right node. So we ignore nodes that don't have .tagName defined
        // @@ HACK!!?? 
        for(var i = 0; i < _parent.childNodes.length; i++) {
          if(_parent.childNodes[i].tagName) {
            _new_elem = _parent.childNodes[i];
            break;
          }
        }
        
         baMergeAttributes(elem, _new_elem);
         
        xmlHttp = null;
        return tempResponseText;
    } else {
        showErrorDisplay(xmlHttp.responseText);
        //alert("XML data fetch failed:\n" + xmlHttp.statusText);
        return null;
    }
    
}

function xmlLoadElementValuesAsync(url, elem) {
    return _loadElementValuesAsync(url, elem);
}

function xmlLoadElementValuesAsync(url, elem) {
    return _loadElementValuesAsync(url, elem);
}

function _loadElementValuesAsync(url, elem) {
    
    _g = 0;
    
    var func = function () {
        if(_g == 0) {_g++; return;} // To avoid the xmlHttp.readyState undefined problem
        
        if (xmlHttp.readyState == 4) {
            if (xmlHttp.status == 200) {
                elem.parentNode.innerHTML = xmlHttp.responseText
                
                xmlHttp = null;
                _g = 0;
                _elem = null;
            } else {
                showErrorDisplay(xmlHttp.responseText);
                //alert("XML data fetch failed:\n" + xmlHttp.statusText);
            }
        }
    }
    
    _elem = elem;
    
    xmlGetAsync(url, _cb);
}

function xmlGetResultString(url){
    try {
        debugger;
        xmlGetSync(url);
        if (xmlHttp.status == 200) {
            if (isSessionExpired()) {
                doRelogin();
                return null;   
            }
            return xmlHttp.responseText ;
            
        } else {
            showErrorDisplay(xmlHttp.responseText);
            //alert("XML data fetch failed:\n" + xmlHttp.statusText);
            return null ;
        }
    } catch (ex) {
        try {
            // setting server status to offline if no connection
            if(ex.message != null && ex.message.indexOf("system cannot locate the resource specified") > 0) {
                uOfflineManager.setServerActiveState(false);
            }
        } catch (e) {
            // do nothing if uOfflineManager.setServerActiveState is not avaliable
        }
        throw(ex);
    }
}


function xmlLoadElementValues(url, elem) {
    try {
        return _loadElementValuesSync(url, elem);
    } catch (ex) {
        try {
            // setting server status to offline if no connection
            if(ex.message != null && ex.message.indexOf("system cannot locate the resource specified") > 0) {
                uOfflineManager.setServerActiveState(false);
            }
        } catch (e) {
            // do nothing if uOfflineManager.setServerActiveState is not avaliable
        }
        throw(ex);
    }
}

function xmlGetAsync(url, callback) {
    
    xmlHttp = GetXmlHttpObject("GET", url, callback, true)
    
    if(xmlHttp != null) {
        xmlHttp.send(null);
    } else {
        alert("XML connection failed!");
    } 
}

function xmlGetSync(url) {
    
    xmlHttp = GetXmlHttpObject("GET", url, null, false)
    
    if(xmlHttp != null) {
        xmlHttp.send(null);
    } else {
        alert("XML connection failed!");
    }
}

function xmlPostSync(url, sBody) {
    
    xmlHttp = GetXmlHttpObject("POST", url, null, false)
    
    if(xmlHttp != null) {
        xmlHttp.send(sBody ? sBody : null);
    } else {
        alert("XML connection failed!");
    }
    
    if (xmlHttp.status == 200) {
        if (isSessionExpired()) { 
            doRelogin();
            return; 
        }
        
        return xmlHttp.responseText ;
        
    } else {
        showErrorDisplay(xmlHttp.responseText);
        //alert("XML data fetch failed:\n" + xmlHttp.statusText);
        return null ;
    }
}

function GetXmlHttpObject(method, action, callback, async) { 
    
    var index = action.indexOf("?");
    if (index > 0)
        action = action + "&isSourceXML=true&xmlRequestTime=" + new Date().getTime();
    else
        action = action + "?isSourceXML=true&xmlRequestTime=" + new Date().getTime();
    
    if(navigator.userAgent.indexOf("MSIE") >= 0) { 
        
        var strName = "Msxml2.XMLHTTP"
        
        if(navigator.appVersion.indexOf("MSIE 5.5") >= 0) {
            strName = "Microsoft.XMLHTTP"
        } 
        
        try { 
            objXmlHttp = new ActiveXObject(strName)
            
            if(async) {
                objXmlHttp.onreadystatechange = callback 
            }
            objXmlHttp.open(method, action, async)
            objXmlHttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded")
            
            return objXmlHttp;
            
        } catch(e) { 
            
            alert("An exception occurred while opening an XML connection. Error name: " + e.name + ". " + 
            e.message + "." + " Scripting might be disabled.");
            return null;
        } 
    }
    
    if (navigator.userAgent.indexOf("Mozilla") >= 0) {
        
        objXmlHttp = new XMLHttpRequest()
        
        if(async) {
            objXmlHttp.onload = callback
            objXmlHttp.onerror = callback
        }
        
        objXmlHttp.open(method, action, async)
        objXmlHttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded")
        
        return objXmlHttp;
        
    }   
}

var sessionExpDisplayMessage = true;
function isSessionExpired() {
    if (xmlHttp.responseText == "Session Expired") {
        if(sessionExpDisplayMessage) {
            alert("Your login session has expired. Please relogin");
            sessionExpDisplayMessage = false;
        }
        xmlHttp = null;
        try {
            //doRelogin(); // this is defined in \js\base\Common.js
        } catch(e) {
            // do nothing;
        }
        return true;
    } else 
        return false;
    
}

function xmlPostForm (oForm,actionStr,sBody){
    var oForm;
    if(oForm == null)
        this.oForm = document.forms[0];
    else
        this.oForm = oForm;

    sBody = sBody ? sBody : getRequestBody(oForm);
    var xmlHttp=GetXmlHttpObject("post",actionStr, null, false);
    
    if(xmlHttp != null) {
        xmlHttp.send(sBody)
    } else {
        alert("XML connection failed!");
    }
    if (xmlHttp.readyState==4 || xmlHttp.readyState=="complete") {
        if (xmlHttp.status == 200) {
            return xmlHttp.responseText;
        } else {
            return null;
        }
    }
    
}



function getRequestBody(oForm) {
    var aParams = new Array();
    
    for (var i=0 ; i < oForm.elements.length; i++) {
        var sParam = encodeURIComponent(oForm.elements[i].name);
        sParam += "=";
        sParam += encodeURIComponent(oForm.elements[i].value);
        aParams.push(sParam);
    } 
    
    return aParams.join("&");
}
