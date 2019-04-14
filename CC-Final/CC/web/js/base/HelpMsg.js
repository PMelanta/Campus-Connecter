// Include this in ur container

function showInfoDiv(strTxt, ev)  {

    var infoTop = 0, infoLeft = 0;
   // if(frameEvent != null) {
   //     infoTop = frameEvent.y - parseInt(100);
   //     infoLeft = frameEvent.x - parseInt(20);
   // } else {
        infoTop = baGetMouseY(ev);
        infoLeft = baGetMouseX(ev);
    //}
    var targetFrame = document.getElementById('infoFrame') ;     
    var targetDiv = document.getElementById('infoDiv') ; 

    document.getElementById('infoText').innerHTML = strTxt;

    //targetFrame.style.visibility = 'visible' ;
    //targetFrame.style.display = 'block' ;    
    //targetDiv.style.visibility = 'visible' ;
    //targetDiv.style.display = 'block' ;

    //targetFrame.style.width = targetDiv.offsetWidth-3;
    //targetFrame.style.height = targetDiv.offsetHeight-3;

    setCommonInfoDivVisible();
    
    targetFrame.style.top = parseInt(infoTop) + "px";

    var divWidth  = parseInt((infoLeft - targetDiv.offsetWidth - 3),0 ) ;
    targetFrame.style.left = divWidth + "px";
    
    targetDiv.style.top = parseInt(infoTop) + "px";
    targetDiv.style.left = parseInt((infoLeft - targetDiv.offsetWidth - 3),0 ) + "px";
    //setCommonInfoDivVisible();
}    

 function setCommonInfoDivVisible()   {
    var targetFrame = document.getElementById('infoFrame') ;     
    var targetDiv = document.getElementById('infoDiv') ; 
    targetFrame.style.visibility = 'visible' ;
    targetFrame.style.display = 'block' ;    
    targetDiv.style.visibility = 'visible' ;
    targetDiv.style.display = 'block' ;
    targetFrame.style.width = targetDiv.offsetWidth-3;
    targetFrame.style.height = targetDiv.offsetHeight-3;
}

function closeInfoDiv(){
    var targetFrame = document.getElementById('infoFrame') ; 
    var targetDiv = document.getElementById('infoDiv') ; 
    targetFrame.style.visibility = 'hidden' ;
    targetFrame.style.display = 'none' ;    
    targetDiv.style.visibility = 'hidden' ;
    targetDiv.style.display = 'none' ;
}

