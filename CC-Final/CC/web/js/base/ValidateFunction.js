/*****************************************************************************
 * File Name        :   ValidationFunction.js
 * Description      :   JS file for Validation functions
 *****************************************************************************/

var RADIX_INT = 10;
var DATE_FORMAT = "dd/MM/yyyy";
var dirtyBit = '0' ;

function dirtyBitSet() {
    return dirtyBit == '1'; 
}

//call this function on keypress event of every form

function setDirtyBit(){
    // dirty bit setting is duplicate. need to remove this function and use the desktop dirtybit function
    try {        
        if( event != null && (event.keyCode == 126 || event.keyCode == 94 || event.keyCode == 27 || (event.keyCode == 13 && event.srcElement.tagName != "TEXTAREA" )|| event.keyCode == 18 ) ){
            window.event.returnValue=false;           
            return;
        }   
    } catch(e) {}
    //parent.banner.document.getElementById('dirtyBit').value = '1' ;    
    dirtyBit = '1' ;
    
}

//call this function when the form is cleared
function resetDirtyBit(){    
    //parent.banner.document.getElementById('dirtyBit').value = '0' ;
    dirtyBit = '0' ;    
    
}

//this function will be called internally when any link is clicked. (you dont have to bother about it)
function checkTransition() {    
    var ans;
    //if(parent.banner.document.getElementById('dirtyBit').value == '1') {
    if(dirtyBit == '1'){ 
        ans = confirm("Selected action will discard any entered data. Continue?");
        if(ans) {
            //parent.banner.document.getElementById('dirtyBit').value = '0';
            dirtyBit = '0' ;
            return true;
        } else {
            return false;
        }
    }
    
    return true ;
}

function uscmCurrentDate(){
    // formateDate is a part of CalendarPopup.js
    return formatDate(new Date(),DATE_FORMAT);
}

function uscmFormatNumber(num,decimalPlaces){
    
    var numValue = new Number(num);
    return numValue.toFixed(decimalPlaces);
}

function commify(num, decimalPlaces) {
    var nStr = uscmFormatNumber(num,decimalPlaces);
    
    x = nStr.split('.');
    x1 = x[0];
    x2 = x.length > 1 ? '.' + x[1] : '';
    
    var rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
        x1 = x1.replace(rgx, '$1' + ',' + '$2');
    }
    
    return x1 + x2;
    
}

function uscmFormatCurrency(num){
    
    return uscmFormatNumber(num,2);
    
}

function trimNewLineSpaces(inputVal,side){
    
    if(inputVal == null)
        return null;
    
    var i = 0;
    var iLength = inputVal.length ;
    var sTemp = "" ;
    sTemp = inputVal.replace(/\n+|\r+|\f+/g,"") ;
    /*while(i < iLength ){
        if(inputVal.charAt(i) == '\\n' || inputVal.charAt(i) == ' ')
            i++ ;
        else{
            sTemp = inputVal.substring (i, iLength);
            break ;
        }
    }
        alert(inputVal) ;
    i = sTemp.length ;
    while(i > 0){
        if(sTemp.charAt(i) == '\\n' || inputVal.charAt(i) == ' ')
            i-- ;
        else{
            sTemp = sTemp.substring (0, i+1);
            break ;
        }    
    }*/
    return(uscmTrim(sTemp,side)) ;
}

function uscmTrim(sInput, iSide) {
    
    var sTemp = "";
    var cChar = "";
    var iCount = "";
    var SINGLE_BLANK = " ";
    var iInputWidth = 0;
    
    iInputWidth = sInput.length;
    
    switch (iSide) {
        case 0:
            //left trim
            for (iCount = 0; iCount < iInputWidth; iCount++) {
                cChar = sInput.charAt(iCount);
                if (SINGLE_BLANK != cChar) {
                    sTemp = sInput.substring(iCount, iInputWidth);
                    break;
                }
            }
            break;
        case 1:
            //right trim
            for (iCount = iInputWidth - 1; iCount >= 0 ; iCount--) {
                cChar = sInput.charAt(iCount);
                if (cChar != SINGLE_BLANK) {
                    sTemp = sInput.substring(0, iCount+1);
                    break;
                }
            }
            break;
        case 2:
            //both trim
            for (iCount = 0; iCount < iInputWidth; iCount++) {
                cChar = sInput.charAt(iCount);
                if (SINGLE_BLANK != cChar) {
                    sTemp = sInput.substring(iCount, iInputWidth);
                    break;
                }
            }
            iInputWidth = sTemp.length;
            for (iCount = iInputWidth - 1; iCount >= 0 ; iCount--) {
                cChar = sTemp.charAt(iCount);
                if (cChar != SINGLE_BLANK) {
                    sTemp = sTemp.substring(0, iCount+1);
                    break;
                }
            }
            break;
    }
    
    
    return sTemp;
} // end of uscmTrim

function uscmIsEmpty(sFieldValue) {
    var bRetValue = false;
    var EMPTY_STRING = "";
    replacement = / /gi;			// regular expression
    replacementForEnter = /\s/gi;			// regular expression
    sFieldValue = uscmTrim(sFieldValue,2);
    
    // remove all spaces
    sFieldValue = sFieldValue + EMPTY_STRING;
    sFieldValue = sFieldValue.replace(replacement, EMPTY_STRING);
    sFieldValue = sFieldValue.replace(replacementForEnter, EMPTY_STRING);
    
    if ((EMPTY_STRING == sFieldValue) || (null == sFieldValue)) {
        bRetValue = true;
    }
    
    return bRetValue;
} // end of uscmIsEmpty

function uscmIsInteger(iFieldValue) {
    var bRetValue = true;
    
    var objRe = new RegExp("[^0-9|\]");		// Regular expression
    
    // trim from both sides
    iFieldValue = uscmTrim(iFieldValue, 2);
    
    if(!uscmIsEmpty(iFieldValue) && iFieldValue.charAt(0) == "-" && iFieldValue.length > 1) {
        iFieldValue = iFieldValue.substring(1);
    }
    
    iFieldValue = uscmTrim(iFieldValue, 2);
    
    
    if (true == uscmIsEmpty(iFieldValue)) {
        bRetValue = false;
    }
    else {
        if (objRe.test(iFieldValue))	    	// If not an integer
        {
            bRetValue = false;
        }
    }
    
    return bRetValue;
} // end of uscmIsInteger


function uscmIsCurrency(sCurency) {
    var bRetValue = true;
    
    // trim from both sides
    sCurency = uscmTrim(sCurency, 2);
    
    if(true == CURRENCY_INTEGER) {
        if(uscmIsInteger(sCurency)) {
            if(sCurency.length > CURRENCY_NO_DIGITS_BEFORE_DECIMAL) {
                bRetValue = false;
            }
        }
        else {
            bRetValue = false;
        }
    }
    else {
        if(uscmIsNumericTemp(sCurency,CURRENCY_DECIMAL_SEPARATOR)) {
            var sarrCurr = sCurency.split(CURRENCY_DECIMAL_SEPARATOR);
            if(sarrCurr[0].length > CURRENCY_NO_DIGITS_BEFORE_DECIMAL) {
                bRetValue = false;
            }
            else if(sarrCurr.length > 1 && sarrCurr[1].length > CURRENCY_NO_DIGITS_AFTER_DECIMAL) {
                bRetValue = false;
            }
        }
        else {
            bRetValue = false;
        }
    }
    
    return bRetValue;
} // end of uscmIsCurrency

function uscmIsNumeric(sVal,NUMERIC_NO_DIGITS_AFTER_DECIMAL) {
    
    DECIMAL_SEPARATOR='.';
    sVal = uscmTrim(sVal,2);
    
    if(!uscmIsEmpty(sVal) && sVal.charAt(0) == "-" && sVal.length > 1) {
        sVal = sVal.substring(1);
    }
    
    return uscmIsNumericTemp(uscmTrim(sVal,2),DECIMAL_SEPARATOR,NUMERIC_NO_DIGITS_AFTER_DECIMAL)
}


//-------------------------------------------------------------------
// uscmIsNumericTemp(value)
//   Returns true if value contains a positive float value
//-------------------------------------------------------------------
function uscmIsNumericTemp(val,sDecimalSep,iNoOfDigitsAfterDecimal) {
    var dp = false;
    var iCounter = 0;
    for (var i=0; i < val.length; i++) {
        if (!uscmIsDigit(val.charAt(i))) {
            
            if (val.charAt(i) == sDecimalSep) {
                if (dp == true) {
                    return false;
                } // already saw a decimal point
                else {
                    dp = true;
                }
            }
            else {
                return false;
            }
        }
        
        if(dp == true) {
            iCounter = iCounter + 1
        }
        
        if(iCounter > iNoOfDigitsAfterDecimal + 1) {
            return false;
        }
        
    }
    
    
    if(dp == true) {
        if(iCounter != 1) {
            return true;
        }
        else {
            return false;
        }
    }
    else
        return true;
    
}
function uscmIsDigit(num) {
    var string="1234567890";
    if (string.indexOf(num) != -1) {
        return true;
    }
    return false;
}
function uscmIsValidDate(sDate) {
    if(uscmIsEmpty(sDate)) {
        return false;
    }
    else {
        return isDate(uscmTrim(sDate,2),DATE_FORMAT);
    }
    
}

function uscmIsMoreDate(sMoreDate,sLessDate) {
    if(uscmIsValidDate(sMoreDate) && uscmIsValidDate(sLessDate)) {
        if(compareDates(uscmTrim(sMoreDate,2),DATE_FORMAT,uscmTrim(sLessDate,2),DATE_FORMAT) == 1) {
            return true;
        }
        else {
            return false;
        }
    }
    else {
        return false;
    }
}

function uscmIsMoreOrEqualDate(sMoreDate,sLessDate) {
    sMoreDate=uscmTrim(sMoreDate,2);
    sLessDate=uscmTrim(sLessDate,2);
    if(uscmIsValidDate(sMoreDate) && uscmIsValidDate(sLessDate)) {
        if(getDateFromFormat(sMoreDate,DATE_FORMAT) >= getDateFromFormat(sLessDate,DATE_FORMAT)) {
            return true;
        }
        else {
            return false;
        }
    }
    else {
        return false;
    }
}




function uscmIsMoreDateByDays(sMoreDate,sLessDate,sDays) {
    sMoreDate=uscmTrim(sMoreDate,2);
    sLessDate=uscmTrim(sLessDate,2);
    if(uscmIsValidDate(sMoreDate) && uscmIsValidDate(sLessDate)) {
        if((getDateFromFormat(sMoreDate,DATE_FORMAT) - getDateFromFormat(sLessDate,DATE_FORMAT)) == sDays*24*60*60*1000) {
            return true;
        }
        else {
            return false;
        }
    }
    else {
        return false;
    }
}
//-------------------------------------------------------------------
// checkMaxLength(control,length)
// Implements the maxlength functionality in a textarea control.
// Returns true if more characters are allowed to be entered.
//-------------------------------------------------------------------
function setLengthOfTextarea(textarea, maxLength) {
    document.getElementById(textarea.name+"Count").innerHTML =document.getElementById(textarea.name).value.length;
    
}

function checkMaxLength(textarea, maxLength) {
    if (maxLength != -1) {
        var allowKey = false;
        var keyCode = document.layers ? event.which : event.keyCode;
        
        if ((keyCode < 47 && keyCode != 13 && keyCode != 32) || keyCode > 146)
            allowKey = true;
        else if (event.ctrlKey) {
            adjustChars(textarea, maxLength);
            if (textarea.value.length <= maxLength)
                allowKey = true;
        }
        else
            allowKey = textarea.value.length < maxLength;
        
        textarea.selected = false;
        event.returnValue = allowKey;
    }
    document.getElementById(textarea.name+"Count").innerHTML =
    document.getElementById(textarea.name).value.length;
    
    
}

//-------------------------------------------------------------------
// adjustChars(control,length)
// Trims the additional characters from a textarea control
//-------------------------------------------------------------------
function adjustChars(textarea, maxLength) {
    if (maxLength != -1) {
        if (textarea.value.length > maxLength)
            textarea.value = textarea.value.substring(0, maxLength);
    }
    document.getElementById(textarea.name+"Count").innerHTML =
    document.getElementById(textarea.name).value.length;
}
function checkMaxLengthById(textarea, maxLength) {
    if (maxLength != -1) {
        var allowKey = false;
        var keyCode = document.layers ? event.which : event.keyCode;
        
        if ((keyCode < 47 && keyCode != 13 && keyCode != 32) || keyCode > 146)
            allowKey = true;
        else if (event.ctrlKey) {
            adjustChars(textarea, maxLength);
            if (textarea.value.length <= maxLength)
                allowKey = true;
        }
        else
            allowKey = textarea.value.length < maxLength;
        
        textarea.selected = false;
        event.returnValue = allowKey;
    }
    document.getElementById(textarea.id+"Count").innerHTML =
    document.getElementById(textarea.id).value.length;
    
    
}

//added by gopi from ubqvalidations.js

function isEmpty(str) {
    if(str == null || str == "")
        return true;
    
    return false;
}

function isPositiveInteger(str) {
    var pattern = "0123456789"
    var i = 0;
    do {
        var pos = 0;
        for (var j=0; j<pattern.length; j++)
            if (str.charAt(i)==pattern.charAt(j)) {
                pos = 1;
                break;
            }
        i++;
    } while (pos==1 && i<str.length)  
        if (pos==0) 
            return false;
    
    return true;
}

function isValidTime(elem, quiet) {
    
    var p = elem.value.split(':');
    
    if(elem.value.length != 5) {
        if(!quiet)
            alert("Invalid time");
        
        this.focus();
        return false;
    }
    
    if(p.length != 2) {
        if(!quiet)
            alert("Invalid time");
        
        this.focus();
        return false;
    }
    
    if(!isPositiveInteger(p[0]) || Number(p[0]) > 23)  {
        if(!quiet)
            alert("Invalid time");
        
        this.focus();
        return false;
    }
    
    if(!isPositiveInteger(p[1]) || Number(p[1]) > 59)  {
        
        if(!quiet)
            alert("Invalid time");
        
        this.focus();
        return false;
    }
    
    return true;
}

// The following checks for dd/mm/yyyy ONLY
// Allowing years between 1900 and 2100 ONLY.

function isValidDate(elem, quiet) {
    
    var p = elem.value.split('/');
    
    if(p.length != 3) {
        if(!quiet)
            alert("Invalid date");
        
        elem.focus();
        return false;
    }
    
    if(!isPositiveInteger(p[0]) || Number(p[0]) > 31)  {
        if(!quiet)
            alert("Invalid date");
        
        elem.focus();
        return false;
    }
    
    if(!isPositiveInteger(p[1]) || Number(p[1]) > 12)  {
        
        if(!quiet)
            alert("Invalid date");
        
        elem.focus();
        return false;
    }
    
    if(!isPositiveInteger(p[2]) || Number(p[2]) < 1900 || Number(p[2]) > 2100)  {
        
        if(!quiet)
            alert("Invalid date");
        
        elem.focus();
        return false;
    }
    
    return true;
}

function validateDate(elem) {
    
    if(elem.value != "")
        isValidDate(elem, false);
}

function validateTime(elem) {
    if(elem.value != "")
        isValidTime(elem, false);
}

function getNumericAdjustmentDone(value) {
    return Math.ceil(value);
}

function removeAllOptions(selectbox,status)  //status : true-> will leave the blank option ,false->will remove the blank option also.  
{
    var i;
    var limit = 0;
    if(status)
        limit = 1;
    for(i=selectbox.options.length-1;i>=limit;i--) {
        selectbox.remove(i);
    }
}

function maximizeWindow() {
    var offset = (navigator.userAgent.indexOf("Mac") != -1 || 
    navigator.userAgent.indexOf("Gecko") != -1 || 
    navigator.appName.indexOf("Netscape") != -1) ? 0 : 4;
    window.moveTo(-offset, -offset);
    window.resizeTo(screen.availWidth + (2 * offset), screen.availHeight + (2 * offset));
}

function isValidGivenTime(time, quiet) {
    
    var p = time.split(':');
    
    if(time.length != 5 && time.length != 8) {
        if(!quiet)
            alert("Invalid time");
        
        this.focus();
        return false;
    }
    
    if(p.length != 2 && p.length != 3) {
        if(!quiet)
            alert("Invalid time");
        
        this.focus();
        return false;
    }
    
    if(!isPositiveInteger(p[0]) || Number(p[0]) > 23)  {
        if(!quiet)
            alert("Invalid time");
        
        this.focus();
        return false;
    }
    
    if(!isPositiveInteger(p[1]) || Number(p[1]) > 59)  {
        
        if(!quiet)
            alert("Invalid time");
        
        this.focus();
        return false;
    }
    
    if(p.length == 3) {
        if(!isPositiveInteger(p[2]) || Number(p[2]) > 59)  {
            if(!quiet)
                alert("Invalid time");
            this.focus();
            return false;
        }
        
    }
    
    return true;
}

function isMoreTime(moreTime,lessTime) {
    
    if(!isValidGivenTime(moreTime,true) || !isValidGivenTime(lessTime,true))
        return false;    
    var moreTimeHr = parseInt(moreTime.split(':')[0], 10);
    var moreTimeMnt = parseInt(moreTime.split(':')[1], 10);
    var lessTimeHr = parseInt(lessTime.split(':')[0], 10);
    var lessTimeMnt = parseInt(lessTime.split(':')[1], 10);
    
    if((moreTimeHr == lessTimeHr) && (moreTimeMnt < lessTimeMnt)){    
        return false;
    }
    else if(moreTimeHr < lessTimeHr){
        return false;
    }    
    return true;
}


function isMoreAndEqualTime(moreTime,lessTime) {
    
    if(!isValidGivenTime(moreTime,true) || !isValidGivenTime(lessTime,true))
        return false;    
    var moreTimeHr = parseInt(moreTime.split(':')[0], 10);
    var moreTimeMnt = parseInt(moreTime.split(':')[1], 10);
    var lessTimeHr = parseInt(lessTime.split(':')[0], 10);
    var lessTimeMnt = parseInt(lessTime.split(':')[1], 10);
    
    if((moreTimeHr == lessTimeHr) && (moreTimeMnt == lessTimeMnt)){    
        return false;
    }
    else if(moreTimeHr < lessTimeHr){
        return false;
    }else if(moreTimeMnt < lessTimeMnt) {
        return false;
    }
    return true;
}




function copySelect(sourceCombo,targetCombo) {
    removeAllOptions(targetCombo,false);    
    for(var i = 0 ; i < sourceCombo.options.length; i++) {
        var newOption = document.createElement("option");
        newOption.value = sourceCombo.options[i].value;
        newOption.innerHTML = sourceCombo.options[i].text;
        targetCombo.appendChild(newOption);
    }       
}

