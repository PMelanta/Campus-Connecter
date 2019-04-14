<%-- 
    Document   : UserRegistration
    
--%>

<%@page import="java.sql.ResultSet"%>
<%
    String projPath = request.getContextPath();
    String loadingImgSrc = projPath + "/images/loading/loading.gif";
    String errorImgSrc = projPath + "/images/alert/error.png";
    String successImgSrc = projPath + "/images/alert/success.png";

    ResultSet rsUserDetails = (ResultSet) request.getAttribute("rsUserDetails");
    String enableDisable = "disabled";
%>

<% if (null != rsUserDetails && rsUserDetails.first()) {%>
<form enctype="multipart/form-data" id="userRegistrationFrm" method="POST" name="userRegistrationFrm"  
      target="Response"
      action="<%= projPath%>/UserRegistration" onsubmit="return userRegistration.formValidation()">
    <div id="errorDisplay" style="color: red;font-weight: bold;font-style: italic">
    </div>
    <table cellpadding="3" cellsapcing="5" style="color: black">
        <tr>
            <td colspan="100%">
                <img src="<%= (rsUserDetails.getString("USER_AVATAR") == null ? request.getContextPath() + "/images/common/user.jpg" : rsUserDetails.getString("USER_AVATAR"))%>" 
                     width="72px" height="72px" style="padding-right: 4px;padding-bottom: 4px;float: left;"/>
                <span style="float: left;font-weight: bold;">
                    <%= rsUserDetails.getString("user_full_name")%>
                </span>
                <br />
                <br />
                <span style="float: left">
                    Company : <%= rsUserDetails.getString("user_company")%>
                </span>

                
            </td>
        </tr>
        <tr>
            <td>
                Email: 
            </td>
            <td>
                <input type="text" name="email" id="email" value="<%= rsUserDetails.getString("user_email")%>" size="25" 
                       <%= enableDisable%>/>  
            </td>
            <td>
                Website :
            </td>
            <td>
                <input type="text" name="url" id="url" value="<%= rsUserDetails.getString("user_url")%>" size="25" 
                       <%= enableDisable%>/> 
            </td>
        </tr>
        <tr>
            <td>
                Gender:
            </td>
            <td>
                <input type="radio" name="mgender" value="Male" <%= "M".equals(rsUserDetails.getString("user_gender")) ? "checked" : ""%>
                       <%= enableDisable%>/>
                <label for="mgender">Male</label>
                <input type="radio" name="fgender" value="Female" <%= "F".equals(rsUserDetails.getString("user_gender")) ? "checked" : ""%>
                       <%= enableDisable%>/>
                <label for="fgender">Female</label>
            </td>
            <td>
                DOB :
            </td>
            <td>
                <input type="text" id="dob" name="dob" value="<%= rsUserDetails.getString("user_dob")%>" size="20" 
                       <%= enableDisable%>/> 
            </td>
        </tr>
        <tr>
            <td>
                Phone(O/R): 
            </td>
            <td>
                <input type="text" name="phone" id="phone" value="<%= rsUserDetails.getString("user_phone")%>" size="25" 
                       <%= enableDisable%>/>  
            </td>
            <td>
                Mobile :
            </td>
            <td>
                <input type="text" name="mobile" id="mobile" value="<%= rsUserDetails.getString("user_mobile")%>" size="25" 
                       <%= enableDisable%>/> 
            </td>
        </tr>
        <tr>
            <td valign="top">
                About:
            </td>
            <td colspan="3">
                <textarea name="desc" cols="60" id="desc" <%= enableDisable%>><%= rsUserDetails.getString("user_about")%></textarea>    
            </td>
        </tr>
        <!--        <tr>
                    <td colspan="100%" align="right">
                        <span style="cursor: pointer;float: right">
                            <input type="submit" value="submit">
                        </span>
                    </td>
                </tr>-->
    </table>
</form>
<iframe id="Response" name="Response" class="hidden"></iframe>
<% }%>
