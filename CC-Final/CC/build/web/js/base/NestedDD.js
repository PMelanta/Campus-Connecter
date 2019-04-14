function setFocusStore(focObj, refObj){

    if (focObj == null)
        focObj = refObj;

    return focObj;

}    
    
    
function validateNestedDD() {

    //document.getElementById('buttonDiv').style.visibility = 'hidden' ;
    document.getElementById("commonErrorMsg").innerHTML = "";
         clearErrorObjectNestDD(); 
        var objFocus = null;
        var valStatus = 1;
        var EArray = {1:"category_error", 2:"desc_error"} ;
        var fArray = {1:"diagnosis category",2:"description"} ;
        var fldArray = new Array ('diaType','descBox') ;
	var tempValue = null ;
	var tempHandle = null ;
       
	for(var i =0 ; i < fldArray.length; i++){
		tempHandle = document.getElementById(fldArray[i]) ; 
		tempValue = tempHandle.value ;
		if(uscmIsEmpty(tempValue) || tempValue == '0') {
                        valStatus = 0;
                        document.getElementById(EArray[i+1]).innerHTML = "Enter " +  fArray[i+1] ;
                         objFocus = setFocusStore(objFocus, tempHandle);
		}
          }
 if (valStatus == 0)
       {
           objFocus.focus();
          // document.getElementById('buttonDiv').style.visibility = 'visible' ;
           return false;
       }
    
   document.ddict_entry_form.submit();

}

function clearErrorObjectNestDD()
{
    var EArray = {1:"category_error", 2:"desc_error"} ;
    for ( i=1; i<3; i++)
    {
            document.getElementById(EArray[i]).innerHTML = '';
    }
}



function clearNestedDDForm(){
	document.getElementById('targetRid').value = "0";
	document.getElementById('saveBtn').value = "Save" ;
        document.getElementById('code').value = "";
        document.getElementById('descBox').value = "" ;
        document.getElementById('diaType').value = 0 ;
	document.getElementById("commonErrorMsg").innerHTML = "";
        clearErrorObjectNestDD();
        desktop.resetDirtyBit();
	document.getElementById('diaType').focus() ;
}

