

var UPopupDiv = function(){
    var self = this;
    var containerHandler = new Hashtable();
    var containerPointer = -1;
    var activePopup = -1;
    var uniquePopup = new Hashtable(); // if the popup should be opened only once
    
    self.getContainerHandler = function(){
        return containerHandler.get(containerPointer);
    }
    
    self.hasContainorHandler = function(){
        return (containerPointer > -1);
    }
    
    function inActivePopup(popupID){
        var tempObj = document.getElementById('popup' + popupID);
        var popupContent = containerHandler.get('CPopup' + popupID);
        if(tempObj != null && popupContent == null) {
            containerHandler.put('CPopup' + popupID, tempObj.innerHTML);
            document.getElementById('desktopPopUpWorkArea' + popupID).innerHTML = "<h5>Data cached</h5>";
        }
        
    }
    
    self.activatePopup = function(popupID){
        var tempObj = document.getElementById('popup' + popupID);
        var popupContent = containerHandler.get('CPopup' + popupID);
        if(tempObj != null && popupContent != null) {
            tempObj.innerHTML =  popupContent;
            tempObj.style.zIndex = '1';
            document.getElementById('desktopPopUpFrame' + popupID).style.zIndex = '2';
            document.getElementById('desktopPopUpDiv' + popupID).style.zIndex = '3';
            inActivePopup(activePopup)
            activePopup = popupID;
        }
    }
    
    self.showPopup = function(title, url, popupArg) {
        
        var width, height, top, left, scrollable, floatable, containerHandlerName, id;
        
        if(popupArg != null) {
            width = popupArg.width;
            height = popupArg.height;
            top = popupArg.top;
            left = popupArg.left;
            scrollable = popupArg.scrollable;
            floatable = popupArg.floatable;
            containerHandlerName = popupArg.containerName;
            //id = popupArg.id; // commented because right now we are allowing only one instance on popup. once the class structure is done we can go with this model
            id = "MedicsPopup";
            if(uniquePopup.get(id) != null) {
                //  if(title != null && title != '') {
                //      alert("Only one instance of "' + title + '" can be opend");
                //  } else {
                alert('Only one instance of the popup window can be opened');
                // }
                return;
            }
            
        }
        
        scrollable = (scrollable == null ? false : scrollable);
        floatable = (floatable == null ? false : floatable);
        
        if(title == null) {
            title = "";
        }
        if(url == null) {
            alert('Please specify the url for popup window');
            return false;
        }
        
        inActivePopup(containerPointer);
        
        containerPointer++;
        activePopup = containerPointer;
        if(id != null) {
            uniquePopup.put(id, containerPointer);
            uniquePopup.put(containerPointer, id);
        }
        createPopup(containerPointer); // create a node for popup
        
        xmlLoadElementValues(url, document.getElementById('desktopPopUpWorkArea' + containerPointer)); 
        
        if(containerHandlerName != null) {
            containerHandler.put(containerPointer, containerHandlerName);
        }
        
        var desktopPopUpDiv = document.getElementById('desktopPopUpDiv' + containerPointer);
        var desktopPopUpFrame = document.getElementById('desktopPopUpFrame' + containerPointer);
        var desktopPopUpScroll = document.getElementById('desktopPopUpScroll' + containerPointer);
        var desktopPopUpWorkArea = document.getElementById('desktopPopUpWorkArea' + containerPointer);
        
        document.getElementById('desktopPopUpTitle' + containerPointer ).innerHTML = "&nbsp;" + title;
        desktopPopUpDiv.style.visibility = 'visible';
        desktopPopUpDiv.style.display = 'inline';
        desktopPopUpDiv.className = 'whiteBG';
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
        
        desktopPopUpDiv.style.height = height ;
        desktopPopUpDiv.style.width = width;
        desktopPopUpFrame.style.height = height + 2;
        desktopPopUpFrame.style.width = width;
        
        if(scrollable) {
            desktopPopUpFrame.style.width = width ;
            desktopPopUpFrame.style.height  = height + 20;
            desktopPopUpDiv.style.width = width ;
            desktopPopUpScroll.style.height = height ;
            desktopPopUpScroll.style.width = '100%' ;
            desktopPopUpWorkArea.style.width = '97.5%' ;
            desktopPopUpScroll.style.overflow = 'scroll';
        } else {
            desktopPopUpScroll.style.height = '' ;
            desktopPopUpScroll.style.width = '' ;
            desktopPopUpWorkArea.style.width = '100%' ;
            desktopPopUpScroll.style.overflow = '';
        }
        
        
        
        if(floatable) {
            JSFX_FloatDiv("desktopPopUpDiv" + containerPointer, left, top).floatIt();
            JSFX_FloatDiv("desktopPopUpFrame" + containerPointer, left, top).floatIt();
        } else {
            desktopPopUpDiv.style.top = top ;
            desktopPopUpDiv.style.left = left;
            desktopPopUpFrame.style.top = top;
            desktopPopUpFrame.style.left = left;
        }
        
        return true;
    }
    
    
    self.closePopup = function(popupID){
        
        removePopup(popupID);
        uniquePopup.remove(uniquePopup.get(containerPointer));
        uniquePopup.remove(containerPointer);
        containerHandler.remove(containerPointer);
        containerPointer--;
        activePopup = containerPointer;
        self.activatePopup(containerPointer);
    }
    
    
    var ns = (navigator.appName.indexOf("Netscape") != -1);
    var d = document;
    function JSFX_FloatDiv(id, sx, sy) {       
        var el=d.getElementById?d.getElementById(id):d.all?d.all[id]:d.layers[id];
        var px = document.layers ? "" : "px";
        window[id + "_obj"] = el;
        if(d.layers)el.style=el;
        el.cx = el.sx = sx;el.cy = el.sy = sy;
        el.sP=function(x,y){this.style.left=x+px;this.style.top=y+px;};
        
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
            this.cx += (pX + this.sx - this.cx)/8;this.cy += (pY + this.sy - this.cy)/8;
            this.sP(this.cx, this.cy);
            setTimeout(this.id + "_obj.floatIt()", 1);
        }
        return el;
    }
    
    
    var popupMove = false;
    var fixLeft = false;
    var tempMouseX = 0;
    
    self.stopPopup = function() {
        popupMove = false;
        fixLeft = false;
    }
    
    self.startPopup = function(popupID) {
        popupMove = true;
    }
    
    self.movePopup = function(evt) {
        if (popupMove ) {
            var mouseX = evt.pageX ? evt.pageX : evt.clientX;
            mouseY = evt.pageY ? evt.pageY : document.documentElement.scrollTop + evt.clientY;
            var desktopPopUpDiv = document.getElementById('desktopPopUpDiv' + activePopup);
            var desktopPopUpFrame = document.getElementById('desktopPopUpFrame' + activePopup);
            
            var desktopPopUpDiv = document.getElementById('desktopPopUpDiv' + activePopup);
            var desktopPopUpFrame = document.getElementById('desktopPopUpFrame' + activePopup);
            if(desktopPopUpDiv != null && desktopPopUpFrame != null) {
                
                if(fixLeft == false) {
                    tempMouseX = (mouseX - desktopPopUpDiv.offsetLeft);
                    fixLeft = true;
                }
                mouseX = mouseX - 5;
                mouseY = mouseY - 7;
                desktopPopUpDiv.style.left = (mouseX - tempMouseX);
                desktopPopUpDiv.style.top = mouseY;
                desktopPopUpFrame.style.left = (mouseX - tempMouseX);
                desktopPopUpFrame.style.top = mouseY;
            }
        }
    }
    
    function createPopup(popupID) {
        
        var desktopPopupWell = document.getElementById("desktopPopupWell");
        var div = document.createElement('div');
        div.id = 'popup' + popupID;
        div.innerHTML = "<iframe id='desktopPopUpFrame" + popupID + "' style='position:absolute;width:400px;height:400px;display:none;' frameborder='0' scrolling='no' ></iframe>" +  
        "<div id='desktopPopUpDiv" + popupID + "' style='visibility:hidden;display:none;position:absolute;background-color:white;z-index:1;'>" +
        "<table width='100%' cellpadding='0' cellspacing='0' border='0'  style='border: 2px #C32020 solid;' >" +
        "<tr class='lhsMenuHeader'>" +
        "<td width='95%' id='desktopPopUpTitle" + popupID + "' height='20px' onmousedown='desktop.activatePopup(" + popupID + "),desktop.startPopup(" + popupID + ")'  >" +
        
        "</td>" +
        "<td width='5%' align='right'><span style='cursor:pointer;' title='Close window' " +
        " onclick='desktop.closePopup(" + popupID + ")'><u>Close</u>&nbsp;</span>" +
        "</td>" +
        "</tr>" +
        "<tr>" +
        "<td colspan='2' > " +
        "<div id='desktopPopUpScroll" + popupID + "' ><div id='desktopPopUpWorkArea" + popupID + "' ></div></div>" +
        "</td>" +
        "</tr>" +
        "</table>" +
        "</div>";
        
        desktopPopupWell.appendChild(div);
    }
    
    function removePopup(popupID) {
        if(popupID == null) {
            popupID = containerPointer;
        }
        var desktopPopupWell = document.getElementById("desktopPopupWell");
        var popupDiv = document.getElementById('popup' + popupID);
        desktopPopupWell.removeChild(popupDiv);
    }
    
}

