<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<HTML>
<% String projPath = request.getContextPath(); %>
<HEAD>
<TITLE>Login Page</TITLE>
<SCRIPT LANGUAGE="JavaScript" SRC="<%= projPath%>/js/base/UbqValidations.js"></SCRIPT>
    <SCRIPT LANGUAGE="JavaScript" SRC="<%= projPath%>/js/base/ValidateFunction.js"></SCRIPT>

     <title>Online Project Management</title>
        <meta http-equiv="content-type" content="text/html; charset=iso-8859-1">
        <meta name="keywords" content="keywords here">
        <meta name="description" content="description here">
        <meta name="author" content="myfreetemplates.com">
        <meta name="robots" content="index, follow"> <!-- (robot commands: all, none, index, no index, follow, no follow) -->
        <meta name="revisit-after" content="30 days">
        <meta name="distribution" content="global"> 
        <meta name="rating" content="general">
        <meta name="content-language" content="english">

        <link href="<%= projPath%>/style/UbqApp.css" rel="stylesheet" type="text/css">
</HEAD>
<script language="Javascript">

function wclose() {
  window.opener=null;
  self.close();
} 

function setfocusPassword() {
  document.setPasswordForm.password.focus();
}

function validate() {

  pw = document.setPasswordForm.password.value;
  cpw = document.setPasswordForm.confirmPassword.value;
  if(pw == "" && cpw == "") {
        var cnf = confirm("Set blank password?")
        if(!cnf) 
            return false;
  }
 /* if(isEmpty(pw)) {
    setPasswordForm.errorMsg.value=('Enter a password');
    setfocusPassword();
    return false;
  }

  

  if(isEmpty(cpw)) {
    setPasswordForm.errorMsg.value=('Confirm the password');
    document.setPasswordForm.confirmPassword.focus();
    return false;
  }*/

  if(pw != cpw) {
    document.setPasswordForm.errorMsg.value=('Passwords do not match');

    document.setPasswordForm.password.value = ""
    document.setPasswordForm.confirmPassword.value = ""

    setfocusPassword();

    return false;
  }
  return true;
}

</script>
<body style="margin:10" onLoad="setfocusPassword()" class="whiteBG" >  

<p>

    <FORM name="setPasswordForm" METHOD="POST" action="SetPassword" onSubmit="return validate();" TARGET="_self">
        
        <div align="center" style="width:100%">
            <input type="hidden" id="command" name="command">
            
            <table width="100%" border="0" cellpadding=0 cellspacing=0 >
                
                <tr> <td> <br> </td></tr>
                
                <tr>
                    <td valign="top" align="left" width="100%"  colspan="2">
                        <img src="images/medics_logo.png">
                    </td>
                </tr>
                
                <tr>
                    <td valign="top" align="left"  colspan="2">
                        &nbsp;<span class="productVersionBig"> Version: <%= (String) request.getAttribute("productVersion") %> </span>
                    </td>
                </tr>
                
                <!--tr>
                    <td background="images/navbg.gif" class="heading" height=15  colspan="2"></td>
                </tr-->
                <tr>
                    <td colspan="100%" height="1px" class="heading" >
                    </td> 
                </tr>
                <tr> <td colspan="2"> <br> <br><br><br></td></tr>
                
                <tr>
                    
                    <td align="right" width="55%">
                        <div style="width:320px" align="left" class="userInfo">
                            No Password is set for this user<br>
                            Please enter a new password to use the system. <br>
                            Enter the same password in "Confirm Password"
                        </div>
                        
                    </td>
                    
                    <td valign="bottom" align="right" width="45%" style="padding-right:20px" >
                        <br>
                        
                        <table border="0" cellpadding="0" cellspacing="0" width="85%" height="150" class="loginShadow" >
                            <tr class="heading" >
                                <td class="heading" height="25px">
                                    <input type="text" size="30" class="heading" readonly value="User Login" align="left">
                                </td>
                            </tr>
                            
                            <tr>
                            <td align="left" class="whiteBG">
                                <table width="100%" height="150" align="center" border="0" cellpadding="0" cellspacing="0" id="userTable" >
                                    <tr>
                                        <td><br> &nbsp;User Name&nbsp;  </td> 
                                        <td>
                                            <br><input autocomplete="off" type="text" name="userName" id="userName" maxlength="20" value="<%=request.getAttribute("userName")%>">
                                        </td>
                                    </tr>
                                    
                                    <tr>
                                        <td><br> &nbsp;New Password &nbsp; </td> 
                                        <td><br><input autocomplete="off" type="password"  name="password" id="password" ></td>
                                    </tr>
                                    
                                    <tr>
                                        <td><br> &nbsp;Confirm Password &nbsp; </td> 
                                        <td><br><input autocomplete="off" type="password"  name="confirmPassword" id="confirmPassword" ></td>
                                    </tr>
                                    
                                    <tr><td>&nbsp;</td></tr>	                
                                    
                                    <tr>
                                        <td class="userInfo" >&nbsp;&nbsp;                                                                         
                                            
                                            <% String errorDisplayMsg = null;
                                            if (request.getAttribute("error")!=null) {
                                                errorDisplayMsg = "Login failed.";
                                                
                                            } else {
                                                errorDisplayMsg = "";
                                            }
                                            %>
                                            <br>&nbsp;&nbsp;  
                                            <input type="text"  size="25" id="errorMsg" name="errorMsg" value="<%=errorDisplayMsg%>" class="userInfo readOnly loginBox" readonly  >   
                                            </td>
                                        <td>
                                            <input type="submit" name="login" id="login"  value="   OK   "  class="smallBtn">
                                            &nbsp;&nbsp;
                                            <input type="button" name="exit" id="exit"  value="  Exit  " onclick="wclose();"  class="smallBtn">
                                        </td>
                                    </tr>
                                    
                                    <tr><td>&nbsp;</td></tr>
                                    
                                    
                                </table>
                                
                            </td>
                            
                        </table>
                        
                    </td>
                    
                </tr>

                </tbody>
            </table>
        </div>
        
        
        
        <iframe style="visibility:hidden" name="myIframe" id="myIframe" height="0" width="0"> </iframe>
    </FORM> 
</body> 
</HTML>
