var GenericBrowser = function() {
    var self = this;
    
    var caller = null;
    var callBackFunctionName = null;
    var handlerClassName = null;
    
    self.init = function(handlerName, cbFunctionName, elem, title) {
        caller = elem;
        callBackFunctionName = cbFunctionName;
        handlerClassName = handlerName;
        
        var url = PROJECT_CTXT_PATH + "/GenericBrowserServlet?command=loadGenericBrowser&handlerClassName=" + handlerClassName + 
        "&callBackFunctionName=" + callBackFunctionName;
        desktop.showPopup(title, url, {width:600, height:400, id:'genericBrowser'});
    };
    
    self.gbCallBackFunction = function(elem) {
        
        if(callBackFunctionName == null) {
            return;
        }
        
        eval(callBackFunctionName)(elem, caller);
        
    };
    
    self.search = function(elem, ev) {
        if(baGetKeyCode(ev) == 13) {
            self.searchContents(elem);
        }
    };
    
    self.searchContents = function(elem) {
        var row = dynTableRow(elem);
        var contentsSearchStr = dynTableGetNodeInRow(row, "contentsSearchStr").value;
        contentsSearchStr = uscmTrim(contentsSearchStr, 2);
        
        var target = document.getElementById("contentsDiv");
        if(target != null && contentsSearchStr != null) {
            var url = PROJECT_CTXT_PATH + "/GenericBrowserServlet?command=searchContents&handlerClassName=" + handlerClassName + 
            "&contentsSearchStr=" + contentsSearchStr + "&callBackFunctionName=" + callBackFunctionName;
            
            xmlLoadElementValues(url, target);
        }
    };
    
    self.mouseOver = function(elem) {
        elem.className = 'highlightedRow';
    };
    
    self.mouseOut = function(elem) {
        elem.className = 'whiteBG';
    };
    
    self.expandCollapseSubContent = function(elem, contentParentRID) {
        
        var row = dynTableRow(elem);
        if(row.getAttribute("isExpanded") == "1") {
            row.className = "hidden";
            row.setAttribute("isExpanded", "0");
            elem.innerHTML = "&#9658;";
            baGetNextSibling(row).className = "hidden";
        } else {
            
            if(row.getAttribute("isExpanded") == null) {
                var childContentRow = document.getElementById("childContentRow");
                var newRow = childContentRow.cloneNode(true);
                newRow.id = "";
                
                var url = PROJECT_CTXT_PATH + "/GenericBrowserServlet?command=getContents&handlerClassName=" + handlerClassName + 
                "&contentParentRID=" + contentParentRID;
                
                var newInsertedRow = dynTableInsertGivenCloneAfterRow(null, row, newRow);
                var childContentsDiv = dynTableGetNodeInRow(newInsertedRow, "childContentsDiv");
                xmlLoadElementValues(url, childContentsDiv);
            } 
            
            row.className = "visible whiteBG";
            row.setAttribute("isExpanded", "1");
            elem.innerHTML = "&#9660;";
        }
    };
    
};

var genericBrowser = new GenericBrowser();
