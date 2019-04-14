var UserRegistration = function(){
    var self = this;
    
    self.formValidation = function(){
        var userId = document.userRegistrationFrm.userId;
        var userName = document.userRegistrationFrm.userName;
        //var comapany = document.userRegistrationFrm.comapany;
        var email = document.userRegistrationFrm.email;
        var dob = document.userRegistrationFrm.dob;
        var Error = document.getElementById("errorDisplay");
        Error.innerHTML = "";
        if(!self.maxMinCheck(userId)){
            Error.innerHTML = "Invalid User Id (Minimum - 3 /Maximum - 10)";
            return false;
        }else if(!self.allLetter(userName)){
            Error.innerHTML = "Invalid user name";
            return false;
        }else if(!self.ValidateEmail(email)){
            Error.innerHTML = "Invalid email";
            return false;
        }else if(!self.ValidateDOB(dob)){
            Error.innerHTML = "Invalid DOB";
            return false;
        }else{
            return true;
        }
        return false;
    }
    self.maxMinCheck = function(input,mx,my){
        var input = input.value.length;
        if (input == 0 || input >= my || input < mx){
            //input.focus();
            return false;
        }
        return true;
    }

    self.allLetter = function(input){ 
        var letters = /^[A-Za-z ]+$/;
        if(input.value.match(letters)){
            return true;
        }else{
            //input.focus();
            return false;
        }
    }
    self.alphanumeric = function(input){ 
        var letters = /^[0-9a-zA-Z]+$/;
        if(input.value.match(letters)){
            return true;
        }else{
           // input.focus();
            return false;
        }
    }

    self.allnumeric = function(input){ 
        var numbers = /^[0-9]+$/;
        if(input.value.match(numbers)){
            return true;
        }else{
           // input.focus();
            return false;
        }
    }
    self.ValidateEmail = function(uemail){
        var mailformat = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
        if(uemail.value.match(mailformat)){
            return true;
        }else{
            //uemail.focus();
            return false;
        }
    }
    
    self.ValidateDOB = function(dob){
         if(!uscmIsValidDate(dob.value)){
            alert("Invalid DOB");
            return false;
        }else if(uscmIsMoreDate(dob.value,uscmCurrentDate())){
            alert("DOB can't be a future date")
            return false;
        }
        return true;
    }
}
var userRegistration = new UserRegistration();


