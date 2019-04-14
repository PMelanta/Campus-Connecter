// amount: type should be float
// roundOffFactor
function getRoundedOffAmt(amount, roundOffFactor) {
    
    var halfRound = (parseFloat(roundOffFactor) / 2);
    var tempAmount = (parseFloat(amount) + parseFloat(halfRound));
    var rem = modValue(tempAmount, parseFloat(roundOffFactor));
    
    if ( isNaN(rem) || rem == null || rem == ''){
        rem = 0;
    }
    var amountAfterRoundOff = (parseFloat(tempAmount) - parseFloat(rem));
    var roundedOffAmount = (parseFloat(amountAfterRoundOff) - parseFloat(amount));
    
    return roundedOffAmount;
}

function modValue(x, y) {
    return x - Math.floor(x / y) * y;
}

var Utils = function() {
    
    var self = this;
    
    self.formatDate = function(formatDate, formatString) {
        if(formatDate instanceof Date) {
            var months = new Array("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");
            var yyyy = formatDate.getFullYear();
            var yy = yyyy.toString().substring(2);
            var m = formatDate.getMonth() + 1;
            var mm = m < 10 ? "0" + m : m;
            var mmm = months[m];
            var d = formatDate.getDate();
            var dd = d < 10 ? "0" + d : d;
            
            var h = formatDate.getHours();
            var hh = h < 10 ? "0" + h : h;
            var n = formatDate.getMinutes();
            var nn = n < 10 ? "0" + n : n;
            var s = formatDate.getSeconds();
            var ss = s < 10 ? "0" + s : s;
            
            formatString = formatString.replace(/yyyy/i, yyyy);
            formatString = formatString.replace(/yy/i, yy);
            formatString = formatString.replace(/mmm/i, mmm);
            formatString = formatString.replace(/mm/i, mm);
            formatString = formatString.replace(/m/i, m);
            formatString = formatString.replace(/dd/i, dd);
            formatString = formatString.replace(/d/i, d);
            formatString = formatString.replace(/hh/i, hh);
            formatString = formatString.replace(/h/i, h);
            formatString = formatString.replace(/nn/i, nn);
            formatString = formatString.replace(/n/i, n);
            formatString = formatString.replace(/ss/i, ss);
            formatString = formatString.replace(/s/i, s);
            
            return formatString;
        } else {
            return "";
        }
    }
    
    
    self.convertDBtoDisplayFormat = function (dbDate) {
        try {
            var yyyy = dbDate.split('-')[0];
            var mm = dbDate.split('-')[1];
            var dd   = dbDate.split('-')[2];
            
            return dd + "/" + mm + "/" + yyyy;
        } catch (e) {
            return "";
        }
    }
    
    self.convertTimeToDisplayFormat = function(timeString) {
        var HH = timeString.split(":")[0];
        var MI = timeString.split(":")[1];
        var SS = timeString.split(":")[2];
        
        return HH + ":" + MI;
        
    }
    
    
    self.convertDisplaytoDBFormat = function (displayDate) {
        try {
            var yyyy = displayDate.split('/')[2];
            var mm = displayDate.split('/')[1];
            var dd   = displayDate.split('/')[0];
            
            return yyyy + "-" + mm + "-" + dd;
        } catch (e) {
            return "";
        }
    };

    self.calculateAgeFromDOB = function(dob) {
        
    if(isDate(dob, "dd/MM/yyyy")) {
        var today = utils.formatDate(new Date(), 'dd/mm/yyyy');
        
        var array = dob.split('/');
        
        bday = array[0];
        bmo = array[1];
        byr = array[2];
        
        var thisarray = today.split('/');
        
        thisDay = thisarray[0];
        thisMon = thisarray[1];
        thisYr = thisarray[2];
        
        // Convert the dates to days since 1800. If anyone walks in whose DOB is prior to 1800
        // we are dead!!
        var BASE_YEAR = 1800;
        
        var todayInDays = ((parseInt(thisYr, 10) - BASE_YEAR) * 365 + parseInt(thisMon, 10) * 30 + parseInt(thisDay, 10));
        var bdInDays = ((parseInt(byr, 10) - BASE_YEAR) * 365 + parseInt(bmo, 10) * 30 + parseInt(bday, 10));
        var ageInDays = todayInDays - bdInDays;
        
        if(ageInDays < 0) {
            alert("Date of birth cannot be in the future");
            return null;
        }
        
        // If ageInDays comes to 0, that means the DOB is today's date. 
        // Can't have age as 0 days. As a special case, we will set this as 1 day.
        if(ageInDays == "0")
            ageInDays = 1;
        
        var age = new Array();
        
        // Till the age of 2 years, we will allow age in Months
        if(ageInDays > (365 * 2)) {
            age.value = ageInDays / 365;
            age.unit = AGE_IN_YEARS;
        } else if(ageInDays > 30) {
            age.value = ageInDays / 30;
            age.unit = AGE_IN_MONTHS;
        } else {
            age.value = ageInDays;
            age.unit = AGE_IN_DAYS;
        }
        
        age.value = parseInt(age.value, 10);        
        return age.value;
    }
    
    return null;
};
    

    
    
    self.calculateDOBFromAge = function(age, ageUnit) {
        //Age Unit 1 - Years, 2 - months, 3 - Days
        
        var today = new Date();
        
        var todayInMS = today.getTime();
        
        ageUnit = ageUnit ? ageUnit : 1;
        
        var oneMinute = 60 * 1000;  // milliseconds in a minute
        var oneHour = oneMinute * 60;
        var oneDay = oneHour * 24;
        var oneMonth = oneDay * 30;
        var oneYear = oneDay * 365;
        
        var date;
        var dateStr = '';
        
        var bYear;
        var bMonth;
        var bDay;
        
        if(ageUnit == 1) {
            var year = today.getFullYear();
            
            if(age > 150) {
                return 0;
            }
            
            bMonth = parseInt(today.getMonth(), 10) + 1;
            bDay = '01';
            
            var bYear = parseInt(year, 10) - parseInt(age, 10);
            
        } else if (ageUnit == 2) {
            
            if(age > 11) {
                return 0;
            }
            
            date = new Date(todayInMS - parseInt(age, 10) * oneMonth);
            
            bDay = '01';
            bMonth = parseInt(date.getMonth(), 10) + 1;
            bYear = date.getFullYear();
        } else if (ageUnit == 3) {
            if(age > 30) {
                return 0;
            }
            
            date = new Date();
            date.setDate(today.getDate() - parseInt(age, 10));
            
            bDay = date.getDate();
            bMonth = parseInt(date.getMonth(), 10) + 1;
            bYear = date.getFullYear();
        } 
        
        var ms = '';
        
        if(bMonth < 10)
            ms = '0' + bMonth;
        else
            ms = bMonth;
        
        dateStr = bDay + '/' + ms + '/' + bYear;
        
        return dateStr;
    }
    
    self.createCSVFile = function (detailsStr,FQFileNameVariable,lineSeperator) {
        var FQFileName = document.getElementById(FQFileNameVariable).value;            
        detailsStr = utils.replaceAll(detailsStr, lineSeperator, "\n", true);        
                
        var response = document.printApplet.createFile(FQFileName, detailsStr);
        if(response != 'success') {
                alert (response);
                return;
        }
    }

    self.replaceAll = function (oldString, searchTerm, replaceWith, ignoreCase) {
        var regex = "/"+searchTerm+"/g";
        if( ignoreCase ) regex += "i";
        return oldString.replace( eval(regex), replaceWith );        
    }   

    self.roundNumber = function(rnum, rlength) {
        if(rnum == '' || rnum == null) rnum = 0;
            return parseFloat(rnum).toFixed(rlength);
    }
    
    // Removes leading whitespaces
    function LTrim( value ) {
	
        var re = /\s*((\S+\s*)*)/;
        return value.replace(re, "$1");
	
    }

    // Removes ending whitespaces
    function RTrim( value ) {
	
        var re = /((\s*\S+)*)\s*/;
        return value.replace(re, "$1");
	
    }

    // Removes leading and ending whitespaces
    function trim( value ) {
	
        return LTrim(RTrim(value));
	
    }
    
}

var utils = new Utils();
