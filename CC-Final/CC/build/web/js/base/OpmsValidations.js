String.prototype.trim = function() {

 return this.replace(/^\s+|\s+|[\n]|[\r]|[\r\n]$/g,"");
  
}

function capitalizeWords(str) {

  str = str.toLowerCase();

  var tempArray = str.split(' ');

  // Make the first character of each word upper- or lowercase
  // depending on the value of theCase
  for (var i = 0; i < tempArray.length; i++) {
    tempArray[i] = tempArray[i].charAt(0).toUpperCase() + tempArray[i].substring(1);
  }

  return tempArray.join(' ');
}

function isValued(field, msg) {

  if(isEmpty(field.value)) {
    alert(msg);

    field.focus();

    return false;
  }

  return true;
}

function checkTextLength(elem, maxLength, msg) {

  if(elem.value.length > maxLength) {
    alert(msg + " cannot exceed " + maxLength + " characters");
    elem.focus();

    return false;
  }

  return true;
}

function isNumeric(sText)

{
   var ValidChars = "0123456789.";
   var IsNumber=true;
   var Char;
 
   for (i = 0; i < sText.length && IsNumber == true; i++) 
      { 
      Char = sText.charAt(i); 
      if (ValidChars.indexOf(Char) == -1) 
         {
         IsNumber = false;
         }
      }

   return IsNumber;
   
}

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

function getInt(str,i,minlength,maxlength){
  for(var x=maxlength;x>=minlength;x--) {
    var token=str.substring(i,i+x);

    if(token.length < minlength){
      return null;
    }

    if(uscmIsInteger(token)){
      return token;
    }
  }
  return null;
}

function getDateFromFormatX(val,format) {
  val=val+"";
  format=format+"";

  var i_val=0;
  var i_format=0;
  var c="";
  var token="";
  var token2="";
  var x,y;

  var now=new Date();

  var year=now.getYear();
  var month=now.getMonth()+1;
  var date=1;
  var hh=now.getHours();
  var mm=now.getMinutes();
  var ss=now.getSeconds();
  var ampm="";

  while(i_format < format.length){
    c=format.charAt(i_format);
    token="";

    while((format.charAt(i_format)==c) &&(i_format < format.length)){
      token += format.charAt(i_format++);
    }

    if(token=="yyyy" || token=="yy" || token=="y"){

      if(token=="yyyy"){
        x=4;
        y=4;
      }

      if(token=="yy"){
        x=2;
        y=2;
      }

      if(token=="y"){
        x=2;
        y=4;
      }

      year= getInt(val,i_val,x,y);

      if(year==null){
        return 0;
      }

      i_val += year.length;

      if(year.length==2){
        if(year > 70){
          year=1900+(year-0);
        }else{
          year=2000+(year-0);
        }
      }
    }else if(token=="MMM"||token=="NNN"){
      month=0;
      for(var i=0; i<MONTH_NAMES.length; i++){
        var month_name=MONTH_NAMES[i];
        if(val.substring(i_val,i_val+month_name.length).toLowerCase()==month_name.toLowerCase()){
          if(token=="MMM"||(token=="NNN"&&i>11)){
            month=i+1;

            if(month>12){
              month -= 12;
            }

            i_val += month_name.length;
            break;
          }
        }
      }

      if((month < 1)||(month>12)){
        return 0;
      }
    } else if(token=="EE"||token=="E"){
      for(var i=0; i<DAY_NAMES.length; i++){
        var day_name=DAY_NAMES[i];
        if(val.substring(i_val,i_val+day_name.length).toLowerCase()==day_name.toLowerCase()){
          i_val += day_name.length;
          break;
        }
      }
    } else if(token=="MM"||token=="M"){
      month= getInt(val,i_val,token.length,2);
      if(month==null||(month<1)||(month>12)){
        return 0;
      }

      i_val+=month.length;
    } else if(token=="dd"||token=="d"){
      date=getInt(val,i_val,token.length,2);
      if(date==null||(date<1)||(date>31)){
        return 0;
      }
      i_val+=date.length;
    } else if(token=="hh"||token=="h"){
      hh=getInt(val,i_val,token.length,2);
      if(hh==null||(hh<1)||(hh>12)){
        return 0;
      }

      i_val+=hh.length;
    } else if(token=="HH"||token=="H"){
      hh=getInt(val,i_val,token.length,2);
      if(hh==null||(hh<0)||(hh>23)){
        return 0;
      }

      i_val+=hh.length;
    } else if(token=="KK"||token=="K"){
      hh=getInt(val,i_val,token.length,2);
      if(hh==null||(hh<0)||(hh>11)){
        return 0;
      }

      i_val+=hh.length;
    } else if(token=="kk"||token=="k"){
      hh=getInt(val,i_val,token.length,2);
      if(hh==null||(hh<1)||(hh>24)){
        return 0;
      }

      i_val+=hh.length;
      hh--;
    } else if(token=="mm"||token=="m"){
      mm=getInt(val,i_val,token.length,2);
      if(mm==null||(mm<0)||(mm>59)){
        return 0;
      }

      i_val+=mm.length;
    } else if(token=="ss"||token=="s"){
      ss=getInt(val,i_val,token.length,2);
      if(ss==null||(ss<0)||(ss>59)){
        return 0;
      }

      i_val+=ss.length;
    } else if(token=="a"){
      if(val.substring(i_val,i_val+2).toLowerCase()=="am"){
        ampm="AM";
      } else if(val.substring(i_val,i_val+2).toLowerCase()=="pm"){
        ampm="PM";
      }else{
        return 0;
      }

      i_val+=2;
    } else{
      if(val.substring(i_val,i_val+token.length)!=token){
        return 0;
      }else{
        i_val+=token.length;
      }
    }
  }

  if(i_val != val.length){
    return 0;
  }

  if(month==2){
    if( ((year%4==0)&&(year%100 != 0) ) ||(year%400==0) ){
      if(date > 29){
        return 0;
      }
    }else{
      if(date > 28){
        return 0;
      }
    }
  }

  if((month==4)||(month==6)||(month==9)||(month==11)){
    if(date > 30){
      return 0;
    }
  }

  if(hh<12 && ampm=="PM"){
    hh=hh-0+12;
  }else if(hh>11 && ampm=="AM"){
    hh-=12;
  }

  var newdate=new Date(year,month-1,date,hh,mm,ss);

  return newdate.getTime();
  }

function isDate(val,format) {
  var date=getDateFromFormatX(val,format);

  if(date==0) {
    return false;
  } 
  
  return true;
}

function formatIntValue(value){
 
    if(value == NaN || value == "")
    return 0;
    else
    return parseInt(value);
}

function formatFloatValue(value){
 
    if(value == NaN || value == "")
    return 0;
    else
    return parseFloat(value);
}

function uTrim (sInput, iSide)
{

	var sTemp = "";
	var cChar = "";
	var iCount = "";
	var SINGLE_BLANK = " ";
	var iInputWidth = 0;

	iInputWidth = sInput.length;

	switch (iSide)
	{
	case 0:
		//left trim
		for (iCount = 0; iCount < iInputWidth; iCount++)
		{
			cChar = sInput.charAt (iCount);
			if (SINGLE_BLANK != cChar)
			{
				sTemp = sInput.substring (iCount, iInputWidth);
				break;
			}
		}
		break;
	case 1:
		//right trim
		for (iCount = iInputWidth - 1; iCount >= 0 ; iCount--)
		{
			cChar = sInput.charAt (iCount);
			if (cChar != SINGLE_BLANK)
			{
				sTemp = sInput.substring (0, iCount+1);
				break;
			}
		}
		break;
	case 2:
		//both trim
		for (iCount = 0; iCount < iInputWidth; iCount++)
		{
			cChar = sInput.charAt (iCount);
			if (SINGLE_BLANK != cChar)
			{
				sTemp = sInput.substring (iCount, iInputWidth);
				break;
			}
		}
		iInputWidth = sTemp.length;
		for (iCount = iInputWidth - 1; iCount >= 0 ; iCount--)
		{
			cChar = sTemp.charAt (iCount);
			if (cChar != SINGLE_BLANK)
			{
				sTemp = sTemp.substring (0, iCount+1);
				break;
			}
		}
		break;
	}


	return sTemp;
} // end of uTrim

function isAlphaNumeric (sFieldValue)
{
	var iCount = 0;
	var iCode = 0;
	var bRetValue = true;

	sFieldValue = uTrim (sFieldValue, 2);

	if ("" == sFieldValue)
	{
		bRetValue = false;
	}
	else
	{
		var iFieldWidth = sFieldValue.length;
		for (iCount = 0; iCount < iFieldWidth; iCount++)
		{
			iCode = sFieldValue.charCodeAt (iCount);
			if (48 > iCode || (57 < iCode && 65 > iCode) || (90 < iCode && 97 > iCode) || iCode > 122 )
			{
				bRetValue = false;
				break;
			}
		}
	}
	return bRetValue;

}

// Sampath
// Calculates the date difference in days
function dateDiff(fromDateStr, toDateStr){
    var fromDate = new Date(); 
    var toDate = new Date();
    
        if(fromDateStr.length >= 9){
 		fromDate = new Date (parseInt(fromDateStr.substr(6,4),10), parseInt(fromDateStr.substr(3,2),10)-1, parseInt(fromDateStr.substr(0,2),10));
        }   

        if(toDateStr.length >= 9){
		toDate = new Date (parseInt(toDateStr.substr(6,4),10), parseInt(toDateStr.substr(3,2),10)-1, parseInt(toDateStr.substr(0,2),10));
        }

        var one_day = 1000 * 60 * 60 * 24;
        return( (toDate.getTime() - fromDate.getTime()) / one_day);
}


// Sampath  
// Adds no of days to DateStr 
function addDaysToDate(DateStr, addDays){
    var oldDate = new Date(); 

        if(DateStr.length > 9){
        oldDate = new Date (parseInt(DateStr.substr(6,4),10), parseInt(DateStr.substr(3,2),10)-1, parseInt(DateStr.substr(0,2),10));
        }   

        var days = oldDate.getDate();
      
        days = days + parseInt(addDays);
        oldDate.setDate(days);
      
       
        var day = "" + oldDate.getDate();
             
         var month = "" + (oldDate.getMonth()+1);
    
        if(day.length == 1)
        day = "0" + day;
        if(month.length == 1)
        month = "0" + month;
      
        return (day +  "/" + month + "/" + (oldDate.getYear() + 1900));
}


function rollDaysToDate(DateStr, rollDays){
    var oldDate = new Date(); 

    
        if(DateStr.length >= 9){
        oldDate.setDate(DateStr.substr(0,2));
        oldDate.setMonth(DateStr.substr(3,2)-1);
        oldDate.setYear(DateStr.substr(6,4));
        }   
        var days = oldDate.getDate();
        days = days - parseInt(rollDays);
        oldDate.setDate(days);

        var day = "" + oldDate.getDate();
        var month = "" + (oldDate.getMonth()+1);
        if(day.length == 1)
        day = "0" + day;
        if(month.length == 1)
        month = "0" + month;

        return (day +  "/" + month + "/" + oldDate.getYear());
}

function dateString(date, month, year, dayOfWeek) {

  var weekDays = ["Sun", "Mon", "Tue", "Wed", "Thurs", "Fri", "Sat"];
  var months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

  return weekDays[dayOfWeek] + ", " + months[month] + " " + date + ", " + year;
}

/*
Description: Sets the maximum value to an input control
Parameters:
elem = the input object.
objKeys = The array includes all valid characters.
maxValue = the maximum value that can be allowed(Supports integer and Float values).
*/
function setMaxValue(elem, objKeys, maxValue){
    strWork = objKeys.getValid();
    var bValidChar = false;
    
    for(var i = 0; i < strWork.length; i++){
        if(window.event.keyCode == strWork.charCodeAt(i)) {
            bValidChar = true;
            break;
        }
    }
    if(bValidChar){
        var tempVal = ('' + elem.value);
        tempVal+=(window.event.keyCode - 48);
        window.event.returnValue = (parseFloat(tempVal) <= maxValue);
    }else{
        window.event.returnValue = false;
    }
}

/*  duplication function 
function editKeyBoard(objForm, objKeyb) 
{
    strWork = objKeyb.getValid();
    strMsg = '';							// Error message
    blnValidChar = false;					// Valid character flag

    if(!blnValidChar) // Part 1: Validate input
            for(i=0;i < strWork.length;i++)
                    if(window.event.keyCode == strWork.charCodeAt(i)) {
                            blnValidChar = true;
                            break;
                    }
                            // Part 2: Build error message
    if(!blnValidChar) 
    {
                //if(objKeyb.getMessage().toString().length != 0)
                    //		alert('Error: ' + objKeyb.getMessage());
            window.event.returnValue = false;		// Clear invalid character
            objForm.focus();						// Set focus
    }
}
*/
function currentTime() {
  var d = new Date();

  return d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds();;
}

/**
 * DHTML email validation script. Courtesy of SmartWebby.com (http://www.smartwebby.com/dhtml/)
 */

function emailCheck(str) {

  var at="@"
  var dot="."
  var lat = str.indexOf(at)
  var lstr = str.length
  var ldot = str.indexOf(dot)

  if (str.indexOf(at) == -1){
    return false;
  }

  if (str.indexOf(at) == -1 || str.indexOf(at) == 0 || str.indexOf(at) == lstr) {
    return false;
  }

if (str.indexOf(dot) == -1 || str.indexOf(dot) == 0 || str.indexOf(dot) == lstr) {
    return false;
  }

  if (str.indexOf(at,(lat+1)) != -1) {
    return false;
  }

  if (str.substring(lat-1,lat) == dot || str.substring(lat+1,lat+2) == dot) {
    return false;
  }

  if (str.indexOf(dot,(lat+2)) == -1) {
    return false;
  }
		
  if (str.indexOf(" ") != -1) {
    return false;
  }

  if (str.charAt(str.length - 1) == '.') {
    return false;
  }

  return true;
}

function validateEmailId(elem) {
  if(elem.value.trim() != "" && !emailCheck(elem.value)) {
    alert("Invalid email id");
    elem.focus();
  }
}

function displayTransientMessage(msg) {

    var targetDiv = document.getElementById('alertDiv') ; 
    var targetFrame  = document.getElementById('newFrame');
    targetFrame.style.visibility ="visible";
    targetFrame.style.display = 'block' ;
    targetDiv.style.visibility = 'visible' ;
    targetDiv.style.display = 'block' ;
    var tempSpan = targetDiv.getElementsByTagName('span');    
    tempSpan[0].innerHTML = msg ;
    setTimeout(hideDiv,2000) ;
}

function maximizeWindow() {
  var offset = (navigator.userAgent.indexOf("Mac") != -1 || 
                 navigator.userAgent.indexOf("Gecko") != -1 || 
                 navigator.appName.indexOf("Netscape") != -1) ? 0 : 4;
  window.moveTo(-offset, -offset);
  window.resizeTo(screen.availWidth + (2 * offset), screen.availHeight + (2 * offset));
}

function isValidPhoneNumber(user_phone){

    var phone_char = '' ;
    for(var i = 0; i < user_phone.length; i++){
        phone_char = user_phone.charAt(i) ;
        if(!isPositiveInteger(phone_char)){
            if(phone_char != "+" && phone_char != " ")
                return false ;
        }
    }

    return true ;
}



