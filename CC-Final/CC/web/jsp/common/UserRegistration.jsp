<%-- 
    Document   : UserRegistration
    
--%>

<%
    String projPath = request.getContextPath();
    String loadingImgSrc = projPath + "/images/loading/loading.gif";
    String errorImgSrc = projPath + "/images/alert/error.png";
    String successImgSrc = projPath + "/images/alert/success.png";
%>


<form enctype="multipart/form-data" id="userRegistrationFrm" method="POST" name="userRegistrationFrm"  
      target="Response"
      action="<%= projPath%>/UserRegistration" onsubmit="return userRegistration.formValidation()">
    <div id="errorDisplay" style="color: red;font-weight: bold;font-style: italic">
    </div>
    <table cellpadding="3" cellsapcing="5" style="color: black">
        <tr>
            <td colspan="100%" class="hidden">User Registration
                <input type="hidden" id="command" name="command" value="saveUserDetails" />
            </td>
        </tr>
        <tr>
            <td>
                Login Id: 
            </td>
            <td>
                <input type="text" name="userId" id="userId" size="30" onblur="checkAvailability()"/>
                <span class="userInfo">*</span>
            </td>
            <td colspan="2">
                <div id="loading" class="hidden">
                    <img src="<%= loadingImgSrc%>" height="20px" width="20px" style="float: left;padding-left: 5px;padding-right: 10px" />
                    <span style="font-style: italic;font-weight: bold;">
                        Checking Availability
                    </span>
                </div>
                <div id="success" class="hidden">
                    <img src="<%= successImgSrc%>" height="20px" width="20px" style="float: left;padding-left: 5px;padding-right: 10px" />
                    <span style="font-style: italic;font-weight: bold;color: green">
                        Available!
                    </span>
                </div>
                <div id="error" class="hidden">
                    <img src="<%= errorImgSrc%>" height="20px" width="20px" style="float: left;padding-left: 5px;padding-right: 10px" />
                    <span style="font-style: italic;font-weight: bold;color: red">Already in use!</span>
                </div>
            </td>
        </tr>

        <tr>
            <td>
                Name: 
            </td>
            <td>
                <input type="text" name="userName" id="userName" size="20" />
                <span class="userInfo">*</span>
            </td>
            <td>
                Company 
            </td>
            <td>
                <input type="text" name="comapany" id="comapany" size="25" />  
            </td>
        </tr>
        <tr>
            <td>
                Email: 
            </td>
            <td>
                <input type="text" name="email" id="email" size="25" />  
                <span class="userInfo">*</span>
            </td>
            <td>
                Website :
            </td>
            <td>
                <input type="text" name="url" id="url" size="25" /> 
            </td>
        </tr>
        <tr>
            <td>
                Gender:
            </td>
            <td>
                <input type="radio" name="mgender" value="Male" />
                <label for="mgender">Male</label>
                <input type="radio" name="fgender" value="Female" />
                <label for="fgender">Female</label>
            </td>
            <td>
                DOB :
            </td>
            <td>
                <input type="text" id="dob" name="dob" size="20" /> 
            </td>
        </tr>
        <tr>
            <td>
                Phone(O/R): 
            </td>
            <td>
                <input type="text" name="phone" id="phone" size="25" />  
            </td>
            <td>
                Mobile :
            </td>
            <td>
                <input type="text" name="mobile" id="mobile" size="25" /> 
            </td>
        </tr>
        <tr>
            <td>
                Avatar :
            </td>
            <td colspan="3">
                <input type="file" id="file" name="file" />
            </td>
        </tr>
        <tr>
            <td valign="top">
                About:
            </td>
            <td colspan="3">
                <textarea name="desc" id="desc"></textarea>    
            </td>
        </tr>
        <tr>
            <td colspan="100%" align="right">
                <span style="cursor: pointer;float: right">
                    <input type="submit" value="submit">
                </span>
            </td>
        </tr>
    </table>
</form>
<iframe id="Response" name="Response" class="hidden"></iframe>
