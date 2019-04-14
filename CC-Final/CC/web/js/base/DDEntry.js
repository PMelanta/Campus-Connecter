function setFocusStore(focObj, refObj){

    if (focObj == null)
        focObj = refObj;

    return focObj;

}    
    
    
function validateDDEntryForm() {

    //document.getElementById('buttonDiv').style.visibility = 'hidden' ;
      document.getElementById("commonErrorMsg").innerHTML = "";
         clearErrorObjectDD(); 
        var objFocus = null;
        var valStatus = 1;
        var EArray = {1:"desc_error"} ;
        var fArray = {1:"Description"} ;
        var fldArray = new Array ('descBox') ;
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

       if(document.getElementById("ddict_item_name").value == 176) {
            document.getElementById("deptComboErrorMsg").innerHTML = "";
            var deptCombo = document.getElementById("deptCombo");
            if(deptCombo.options[deptCombo.selectedIndex].value == 0) {
                document.getElementById("deptComboErrorMsg").innerHTML = "Select the department";
                valStatus = 0;
            }                        
       }
 if (valStatus == 0)
       {
           objFocus.focus();
          // document.getElementById('buttonDiv').style.visibility = 'visible' ;
           return false;
       }
    
   document.dataEntryForm.submit();

}

function clearErrorObjectDD()
{
    var EArray = {1:"desc_error"};
    for ( i=1; i<2; i++)
    {
            document.getElementById(EArray[i]).innerHTML = '';
    }
}



function clearDDEntryForm(){

        if(document.getElementById("ddict_item_name").value == 176) {
            var deptCombo = document.getElementById("deptCombo");
            deptCombo.disabled = false;
            for(var i = 0; i < deptCombo.options.length; i++) {
                if(deptCombo.options[i].value == 0)
                    deptCombo.options[i].selected = true;                
            }
            document.getElementById("parentList").value = 0;
        }
        
	document.getElementById('targetRid').value = "0";
	document.getElementById('saveBtn').value = "Save" ;
        document.getElementById('code').value = "";
        document.getElementById('descBox').value = "" ;
	document.getElementById("commonErrorMsg").innerHTML = "";
	document.getElementById('code').focus() ;
        clearErrorObjectDD();
        desktop.resetDirtyBit();
}

function setParentList(elem) {
    document.getElementById("parentList").value = elem.value;    
}


 