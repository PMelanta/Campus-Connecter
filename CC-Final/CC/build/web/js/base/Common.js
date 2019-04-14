var templateEditorCache = new Hashtable();
var IS_DIRECTPRINT;
 
function newPopupCalender(nameOfTxtBox,nameOfBtn, elem) {
    alert("TEST");
    
    Calendar.setup( {
        inputField : nameOfTxtBox, // ID of the input field
        ifFormat : "%d/%m/%Y", // the date format
        weekNumbers : false,
        button : nameOfBtn, // ID of the button
        row : dynTableRow(elem) // row element
    }
    );
}

function newPopupCalender(nameOfTxtBox,nameOfBtn, elem, callBackfn) {
    
    Calendar.setup( {
        inputField : nameOfTxtBox, // ID of the input field
        ifFormat : "%d/%m/%Y", // the date format
        weekNumbers : false,
        onSelect : callBackfn,
        button : nameOfBtn, // ID of the button
        row : dynTableRow(elem) // row element
    }
    );
}


function findPosX(obj) {
    var curleft = 0;
    if(obj.offsetParent)
        while(1) {
            curleft += obj.offsetLeft;
            if(!obj.offsetParent)
                break;
            obj = obj.offsetParent;
        }
    else if(obj.x)
        curleft += obj.x;
    return curleft;
}

function findPosY(obj) {
    var curtop = 0;
    if(obj.offsetParent)
        while(1) {
            curtop += obj.offsetTop;
            if(!obj.offsetParent)
                break;
            obj = obj.offsetParent;
        }
    else if(obj.y)
        curtop += obj.y;
    return curtop;
}

function closeEPR() {
    self.close();
}

function setDefaultFeature(featureRID) {
    var url = PROJECT_CTXT_PATH + "/UMasterServlet?command=setDefaultFeature&newFeatureRID=" +  featureRID;
    var resultStr = trimNewLineSpaces(xmlGetResultString(url),2);  
    
    if(resultStr != 'success')
        alert('Error in re-setting default page. Please try again later') ;
}

function portalHomeInit(cal) {
    desktop.setActivity('');
    //document.getElementById('currentActivity').value = '';

}

function hideDiv(){
    var targetDiv = document.getElementById('alertDiv') ;
    if(targetDiv != null) {
        targetDiv.style.visibility = 'hidden' ;
        targetDiv.style.display = 'none' ;
    }
    var targetFrame  = document.getElementById('newFrame');
    if(targetFrame != null) {
        targetFrame.style.visibility = 'hidden' ;
        targetFrame.style.display = 'none' ;
    }
}  

function setActivity(description)  {
    desktop.setActivity(description);
    /*if(document.getElementById('currentActivity') != null) {
        document.getElementById('currentActivity').value = description;
    }*/
}

function commonErrorMsgFun(errorMsg){
    document.getElementById('commonErrorMsg') != null ? document.getElementById('commonErrorMsg').innerHTML = errorMsg : "" ;
}

var Editor = function() {
    
    this.getEditor = function(instanceName, width, height, toolbarSet, targetDiv, cbFunOnComplete) {
        if(toolbarSet == null) {
            toolbarSet = 'UbqDocumentToolbar';
        }
        
        if(width == null) {
            width = 600;
        }
        
        if(height == null) {
            height = 778;
        }
        
        //var editorInstance = desktop.getFromCache(instanceName);
        var editorInstance = document.getElementById(instanceName + 'Div');
        
        if(editorInstance == null) {
            var head = document.getElementsByTagName("head")[0];
            var div = document.createElement('div');
            div.id = instanceName + 'Div';
            //div.style.display = "none";
            div.innerHTML = createDocumentTemplate(instanceName, width, height, toolbarSet );
            div.cbFunOnComplete = cbFunOnComplete;
            //head.appendChild(div);
            //desktop.putIntoCache(instanceName, div);
            editorInstance = div;  // document.getElementById(instanceName + 'Div');
            //editorInstance = div; 
        } 
        
        //targetDiv.replaceNode(editorInstance); 
        //targetDiv.innerHTML = editorInstance.innerHTML;
        targetDiv.appendChild(editorInstance); 
        if(document.getElementById(instanceName + '___Frame') != null ){
            document.getElementById(instanceName + '___Frame').src = document.getElementById(instanceName + '___Frame').src;
        }
    }
    
    function createDocumentTemplate(instanceName, width, height, toolbarSet) {
        try{
            var doc_tmpl_oFCKeditor = new FCKeditor(instanceName, width, height, toolbarSet);
            doc_tmpl_oFCKeditor.BasePath = PROJECT_CTXT_PATH + "/js/base/FCKeditor/";
            doc_tmpl_oFCKeditor.Config['ToolbarStartExpanded'] = true;
            return doc_tmpl_oFCKeditor.CreateHTML();
        } catch (e) {
            throw "Recall Function";
        }
    }
    
}  

var myEditor = new Editor();          


function getSelectedRadioValue(radioObjName){
    var radioObjArr = document.getElementsByName(radioObjName);
    if(radioObjArr.length == 0)
        return null;
    
    for(var i = 0 ; i < radioObjArr.length; i++) {
        if(radioObjArr[i].checked == true) {
            return radioObjArr[i].value;
        }
    }
    
    return null;
};


function FCKeditor_OnComplete(editorInstance) {

    var cbFunOnComplete = document.getElementById(editorInstance.Name + 'Div').cbFunOnComplete;
    if(cbFunOnComplete != null) {
        eval(cbFunOnComplete); 
    }
    
}

function FCKeditor_OnAfterSetHTML(editorInstance) {
    
    editorInstance.ResetIsDirty();
}

function ubqGetFloatValue(value) {
    if(value == null || value == '') {
        return parseFloat(0);
    } else {
        return parseFloat(value);
    }
}

function doRelogin(url, elem){
    desktop.showReloginDiv(url, elem);
    
    //location.href = PROJECT_CTXT_PATH + "/Login";
}

function showErrorDisplay(errorMessage) {
    desktop.showErrorMessage(errorMessage);
} 

function findParentElementByTagName(node, elemTagName) {
    
    var parentElement = node.parentElement;
    
    if(parentElement != null) {
        if(parentElement.tagName == elemTagName) {
            return parentElement;
        } else {
            findParentElementByTagName(parentElement, elemTagName)
        }
    } 
};


function initSortingOnTable(tableName) {
    // for sorting of the table dynamically
    setTimeout(function(){
        try{
            var tbl = document.getElementById(tableName);
            if(typeof(sorttable) == 'object' && tbl != null) {
                sorttable.makeSortable(tbl);
            }
        } catch(ex) {
            // do nothing
        }
    }, 0);
};


function addDaysToGivenDate(givenDate, daysToAdd) {
    
    if(givenDate != null && uscmIsValidDate(givenDate)) {
        var dateArray = givenDate.split("/");
        var date = new Date(dateArray[2], parseInt(dateArray[1]/1) - 1, dateArray[0]);
        var d = date.getDate();
        d = d + daysToAdd;
        date.setDate(d);
    }
    var day = (date.getDate() > 9 ? date.getDate() : "0" + date.getDate());
    var month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0" + (date.getMonth() + 1);
    var year = (date.getYear() > 1900 ? date.getYear() : (1900 + date.getYear()) );
    return (day + "/" + month + "/" + year);
};

function toggleOrderFilter(elem) {
    var row = dynTableRow(elem);
    if(elem.getAttribute('expanded') == '1') {
        elem.setAttribute('expanded', 0);
        elem.innerHTML = '&#9660;';
        baGetPreviousSibling(row).className = "hidden";
    } else {
        elem.setAttribute('expanded', 1);
        elem.innerHTML = '&#9650;';
        baGetPreviousSibling(row).className = "visible";
    }
};

var FadeEffect  = function() {
    var self = this;
    var duration = 1000;  /* 1000 millisecond fade = 1 sec */
    var steps = 20;       /* number of opacity intervals   */
    var delay = 5000;     /* 5 sec delay before fading out */
    var element = document.getElementById("desktopBody");
    
    self.fadeIn = function() {
        for (i = 0; i <= 1; i += (1 / steps)) {
            setTimeout("fadeEffect.setOpacity(" + i + ")", i * duration);
        }
        setTimeout("fadeEffect.fadeOut()", delay);
    }
    
    self.fadeOut = function() {
        for (i = 0; i <= 1; i += (1 / steps)) {
            setTimeout("fadeEffect.setOpacity(" + (1 - i) + ")", i * duration);
        }
        setTimeout("fadeEffect.fadeIn()", duration);
    }
    
    self.setOpacity = function(level) {
        element.style.opacity = level;
        element.style.MozOpacity = level;
        element.style.KhtmlOpacity = level;
        element.style.filter = "alpha(opacity=" + (level * 100) + ");";
    }
    
};

var fadeEffect = new FadeEffect();