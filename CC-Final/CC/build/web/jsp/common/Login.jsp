<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html; charset=iso-8859-1" language="java" errorPage="" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
    String projPath = request.getContextPath();
%>

<script language="javascript"> 
    var PROJECT_CTXT_PATH = "<%= request.getContextPath()%>"  ;
</script>


<html>
    <head>
        <title>Student Management System</title>
        <meta http-equiv="content-type" content="text/html; charset=iso-8859-1">
        <meta name="keywords" content="keywords here">
        <meta name="description" content="description here">
        <meta name="robots" content="index, follow"> <!-- (robot commands: all, none, index, no index, follow, no follow) -->
        <meta name="revisit-after" content="30 days">
        <meta name="distribution" content="global"> 
        <meta name="rating" content="general">
        <meta name="content-language" content="english">

        <link href="<%= projPath%>/style/UbqApp.css" rel="stylesheet" type="text/css">
        <link id="<%= projPath%>/style/OpmsStyle.css" href="<%= projPath%>/style/OpmsStyle.css" rel="stylesheet" type="text/css">

        <SCRIPT id="<%= projPath%>/js/base/CalendarPopup.js" language="Javascript" src="<%= projPath%>/js/base/CalendarPopup.js"></script>
        <SCRIPT id="<%= projPath%>/js/calendar/calendar.js" language="Javascript" src="<%= projPath%>/js/calendar/calendar.js"></script>
        <SCRIPT id="<%= projPath%>/js/calendar/lang/calendar-en.js" language="Javascript" src="<%= projPath%>/js/calendar/lang/calendar-en.js"></script>
        <SCRIPT id="<%= projPath%>/js/calendar/calendar-setup.js" language="Javascript" src="<%= projPath%>/js/calendar/calendar-setup.js"></script>


        <SCRIPT id="<%= projPath%>/js/base/BrowserAbstraction.js" language="Javascript" src="<%=projPath%>/js/base/BrowserAbstraction.js"></script>
        <SCRIPT id="<%= projPath%>/js/base/DynamicTableTemplate.js" language="Javascript" src="<%= projPath%>/js/base/DynamicTableTemplate.js"></SCRIPT>
        <SCRIPT language="Javascript" src="<%= projPath%>/js/base/HashTable.js"></script>
        <SCRIPT LANGUAGE="JavaScript" SRC="<%= projPath%>/js/base/Desktop.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="<%= projPath%>/js/base/ValidateFunction.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="<%= projPath%>/js/base/DynamicTableTemplate.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="<%= projPath%>/js/base/xmlHelper.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="<%= projPath%>/js/project/ProjectHelper.js"></SCRIPT>
        <SCRIPT LANGUAGE="JavaScript" SRC="<%= projPath%>/js/fileBrowser/FileUpload.js"></SCRIPT>

        <script src="<%= projPath%>/js/jquery/jquery-1.7.1.js" type="text/javascript"></script>
        <script src="<%= projPath%>/js/jquery/jquery.easing.1.3.js" type="text/javascript"></script>

        <script src="<%= projPath%>/js/jquery/jquery-ui-1.8.17.custom.min.js" type="text/javascript"></script>

        <script src="<%= projPath%>/js/fileBrowser/jqueryFileTree.js" type="text/javascript"></script>

        <link href="<%= projPath%>/style/jquery/jquery-ui-1.8.17.custom.css" rel="stylesheet" type="text/css" media="screen" />


        <script language="Javascript" src="<%= projPath%>/js/common/userRegistration.js"></script>


        <script>
            var loading = 1;
            var ContextPath = "<%= projPath%>";
            
            function closePopUp(){
                try{
                    debugger;
                    var desktopPopupWell = document.getElementById("desktopPopupWell");
                    var popupDiv = document.getElementById('popup0');
                    desktopPopupWell.removeChild(popupDiv);
                }catch(ex){
                    alert(ex);
                }
            }
            
            function checkAvailability(){
                var elem = document.getElementById("userId");
                if((elem.value).trim().length == 0){
                    return;
                }
                
                if(loading == 1){
                    document.getElementById("success").className="hidden";
                    document.getElementById("error").className="hidden";
                    document.getElementById("loading").className="visible";
                    loading = 0;
                    //setTimeout(checkAvailability, 5000);
                }
                var url = ContextPath + "/UserRegistration?command=checkUserIdAvailability&userId="+(elem.value).trim();
                
                var response = xmlGetResultString(url);
                
                document.getElementById("loading").className="hidden";
                loading = 1;
                
                if(response.trim() == "1"){//available
                    document.getElementById("success").className="visible";
                    document.getElementById("error").className="hidden";
                }else{
                    document.getElementById("error").className="visible";
                    document.getElementById("success").className="hidden";
                }
            }
        </script>

        <script language="Javascript">

            function wclose() {
                window.opener=null;
                top.window.close();
            } 

            function setfocusUserName() {
                if (!document.login_form.userName.disabled) 
                    document.login_form.userName.focus();
                else
                    document.login_form.exitButton.focus();
            }

            //With inline error msg.
            function submitForm() {
  
                document.getElementById("errorMsg").innerHTML = "";
  

                var uname = document.getElementById("userName").value;            

                if(isEmpty(uname)) {
                    document.getElementById("errorMsg").innerHTML = "You must enter your user name";
                    setfocusUserName();
                    return false;
                }

                document.getElementById("command").value = "";
                document.login_form.target="_self";

                document.getElementById("infoMsg").innerHTML = "Signing you in...please wait...";
                return true;
            }
        
            function formReset() {
                document.login_form.reset();
            }

            // @@ Move to UbqValidations.js and replace the buggy trim function in that (which removes ALL white spaces,
            // @@ not just the leading and trailing whitespaces)

            String.prototype.trim = function () {
                var temp = this;
                var obj = /^(\s*)([\W\w]*)(\b\s*$)/;
                if (obj.test(temp)) { temp = temp.replace(obj, '$2'); }
                var obj = /  /g;
                while (temp.match(obj)) { temp = temp.replace(obj, " "); }
                return temp;
            }

            /*
          var a = rval.split("~");

          var optArray = new Array(a.length + 1); // One for the empty option

          var lcombo = document.getElementById('locationRID');

          lcombo.options[0] = new Option("", 0, false, false);

          for(var i = 0; i < a.length; i++) {
            var opt = a[i].split('@');

            lcombo.options[i+1] = new Option(opt[1].trim(), opt[0].trim(), false, false);
          }

          if(lcombo.length == 2)
            lcombo.selectedIndex = 1;
        }
             */

            function formInit() {
                setfocusUserName();
            }
            
            function userRegister(){
                debugger;
                document.getElementById("regUser").submit();
            }
            
            function validateLogin(){
                
                
                if((document.getElementById("userName").value).trim() == ""){
                    alert("Enter Login Id");
                    return false;
                }
                if((document.getElementById("password").value).trim() == ""){
                    alert("Enter password");
                    return false;
                }
                    
                if(document.getElementById("captchaDetail")){
                    if((document.getElementById("captchaDetail").value).trim() == ""){
                        alert("Enter Captcha");
                        return false;
                    }
                }
                
                return true;
            }
                            


        </script>
    </head>
    <body id="loginPage" onLoad="formInit()" style="margin:10"
          onkeydown="desktop.handleKeyDown(event);desktop.handleBackspace(event);"
          onmouseup="desktop.stopPopup(event)" onmousemove="desktop.movePopup(event)" 
          class="backgroundImg">
        <div>
            <div id="desktopPopupWell" >
            </div>
        </div>
        <div>
            <div id="mainPage">
                <FORM name="login_form" METHOD="POST" action="Login" ONSUBMIT="return validateLogin();" TARGET="_self" >
                    <input type="hidden" id="includesCaptcha" name="includesCaptcha" value ="<%= request.getAttribute("captureCaptcha") == null ? "0" : "1"%>" >
                    <div >
                        <div align="center" style="width:100%;">
                            <input type="hidden" id="command" name="command">

                            <table width="100%" border="0" cellpadding=0 cellspacing=0 align="center" >

                                <tr> <td> <br> </td></tr>

                                <!--                <tr>
                                                    <td valign="top" align="center">
                                                        <img src="images/medics_logo.png" width="100%">
                                                    </td>
                                                </tr>-->

                                <tr>
                                    <td valign="top" align="left">
                                        <table width="100%" >
                                            <tr>
                                                <td width="50%" align="left" style="color:#000000;">
                                                    &nbsp;<span class="productVersionBig"> Version: <%= (String) request.getAttribute("productVersion")%> </span>                                    
                                                </td>
                                                <td width="50%" align="right" >

                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>


                                <tr>
                                    <td colspan="100%" height="1px" class="heading" >
                                    </td> 
                                </tr>
                                <tr> <td>&nbsp;</td></tr>

                                <tr>
                                    <td valign="bottom" align="right" width="100%" style="padding-right:30px;" >
                                        <table border="0" cellpadding="0" cellspacing="0" width="35%" height="150" class="loginShadow" >

                                            <tr>
                                                <td class="heading" align="center" height="25">
                                                    User Login
                                                </td>
                                            </tr>

                                            <tr>
                                                <td class="loginBox"  align="left" >
                                                    <table width="100%" height="150" border="0" cellpadding="4" cellspacing="0" id="userTable" >
                                                        <tr>
                                                            <td align="right" class="label">
                                                                <label>Login Id </label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td> 
                                                            <td>
                                                                <input type="text" name="userName" id="userName" maxlength="20" autocomplete="off">
                                                            </td>
                                                        </tr>
                                                        <tr><td align="right" class="label">
                                                                <label>Password </label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td> 
                                                            <td><input type="password"  name="password" id="password" autocomplete="off"></td>
                                                        </tr>
                                                        <% if (request.getAttribute("captureCaptcha") != null) {%>
                                                        <tr>
                                                            <td colspan="2" valign="top" style="border-top-style: solid; border-top-width: thin; color: black;">
                                                                <label> Enter Captcha : </label>


                                                            </td>
                                                        </tr>
                                                        <tr style="border-top-style: solid; border-top-width: thin;color: black;background-color: #FF0000" >
                                                            <td colspan="2" style="color: black" >
                                                                <table>
                                                                    <tr>
                                                                        <td>
                                                                            <img id="captcha" src="<c:url value='simpleCaptcha.jpg'/>" height="30px"> 
                                                                        </td>
                                                                        <td valign="center">
                                                                            <input type="text" id="captchaDetail" name="captchaDetail" />
                                                                        </td>
                                                                    </tr>
                                                                </table>
                                                            </td>
                                                        </tr>
                                                        <% }%>

                                                        <tr>
                                                            <td>&nbsp;</td>
                                                            <td>
                                                                <input type="submit" name="login" id="login"  value="Sign In"  class="smallBtn" >
                                                                &nbsp;&nbsp;
                                                                <input type="button" name="exit" id="exit"  value="  Exit  " onclick="wclose();"  class="smallBtn">

<!--                                                                <span style="float: right">
                                                                    <a href="#"
                                                                       onclick="projectHelper.openRegistration()">
                                                                        New User?
                                                                    </a>
                                                                </span>
                                                                <br>    -->
                                                                <span style="float: right">
                                                                    <a href="#"
                                                                       onclick="projectHelper.changeClassName('forgotPasswordDiv','visible')">
                                                                        Forgot password?
                                                                    </a>
                                                                </span>

                                                            </td>
                                                        </tr>

                                                        <tr>
                                                            <td colspan="2" class="userInfo">&nbsp;&nbsp;

                                                                <% String errorDisplayMsg = null;
                                                                    if (request.getAttribute("error") != null) {
                                                                        if (request.getAttribute("captcha") != null) {
                                                                            errorDisplayMsg = "Incorrect Captcha ,Login failed";
                                                                        } else {
                                                                            errorDisplayMsg = "Login failed";
                                                                        }
                                                                    } else {
                                                                        errorDisplayMsg = "";
                                                                    }
                                                                %>
                                                                &nbsp;&nbsp;  
                                                                <span id="errorMsg" class="userInfo"><%= errorDisplayMsg%></span>   
                                                            </td>
                                                        </tr>

                                                    </table>

                                                </td>
                                            </tr>


                                        </table>
                                    </td>
                                    <!-- <td  valign="top" style="padding:25px;" align="left"> </td> -->
                                </tr>
                                <tr>
                                    <td align="right" style="padding-right:30px;" height="30px" valign="bottom" >
                                        <span id="infoMsg"></span>
                                    </td>
                                </tr>
                                </tbody>
                            </table>

                        </div>



                        <iframe style="visibility:hidden" name="myIframe" id="myIframe" height="0" width="0"> </iframe>
                    </div>
                </form>
            </div>
        </div>
        <div id="forgotPasswordDiv" class="hidden" style="z-index: 1;position: absolute;left:600px;top:300px;border: solid;">
            <table bgcolor="black">
                <tr>
                    <td style="color: white;font-weight: bold" nowrap>
                        Enter Login Id :
                    </td>
                    <td>
                        <input type="text" id="userLoginId" name="userLoginId">
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <input type="button" value="Email password" onclick="projectHelper.emailPassword()">
                        <input type="button" value="Cancel" onclick="projectHelper.changeClassName('forgotPasswordDiv','hidden')">
                    </td>
                </tr>
            </table>
        </div>
    </body>
</html>

