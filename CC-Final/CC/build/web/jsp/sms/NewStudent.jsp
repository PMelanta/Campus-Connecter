<%-- 
    Document   : NewStudent
    
--%>
<%@page import="java.sql.ResultSet"%>
<%
   ResultSet rsBranch = (ResultSet) request.getAttribute("rsBranch");
%>

<div>
   <fieldset>
      <legend>
         Student Details
      </legend>
      <table with="100%" cellspacing="3" cellpadding="3">
         <tr>
            <td>
               Name
            </td>
            <td>
               <span class="helpText">(First Name)</span>
               <input type="text" name="firstName" id="firstName" value="">
               <span class="userInfo">*</span>
               &nbsp;&nbsp;&nbsp;
               <span class="helpText">(Last Name)</span>
               <input type="text" name="lastName" id="lastName" value="">
            </td>
         </tr>
         <tr>
            <td>
               Register No.
            </td>
            <td>
               <input type="text" name="regNo" id="regNo" value="">
               <span class="userInfo">*</span>
            </td>
         </tr>
         <tr>
            <td>
               Branch
            </td>
            <td>
               <select id="branchSel" name="branchSel" onchange="subject.loadSemesters(this.value)">
                  <% while (null != rsBranch && rsBranch.next()) {%>
                  <option value="<%=rsBranch.getInt("branch_rid")%>">
                     <%=rsBranch.getString("branch_name") + " (" + rsBranch.getString("branch_code") + ")"%>
                  </option>
                  <%}%>
               </select>
               <span class="userInfo">*</span>
            </td>
         </tr>
         <tr>
            <td>
               Sem
            </td>
            <td>
               <select id="semSel" name="semSel">
                  <option>
                     -- Select Branch-- 
                  </option>
               </select>
               <span class="userInfo">*</span>
            </td>
         </tr>
         <tr>
            <td>
               Gender
            </td>
            <td>
               <input type="radio" name="gender" id="genderM" value="Male" checked>
               <label for="genderM">Male</label>
               &nbsp;&nbsp;&nbsp;
               <input type="radio" name="gender" id="genderF" value="Female">
               <label for="genderF">Female</label>
            </td>
         </tr>
         <tr>
            <td>
               Date of Birth
            </td>
            <td>
               <input type="text" name="dob" id="dob" value="">
               <span class="userInfo">*</span>
            </td>
         </tr>
         <tr>
            <td>
               E-mail
            </td>
            <td>
               <input type="text" name="studEmail" id="studEmail" value="">
            </td>
         </tr>
         <tr>
            <td>
               Mobile No.
            </td>
            <td>
               <input type="text" name="studMobile" id="studMobile" value="">
            </td>
         </tr>
         <tr>
            <td>
               Father's Name
            </td>
            <td>
               <input type="text" name="fatherName" id="fatherName" value="">
            </td>
         </tr>
         <tr>
            <td>
               Mother's Name
            </td>
            <td>
               <input type="text" name="motherName" id="motherName" value="">
            </td>
         </tr>
         <tr>
            <td>
               Parent Contact No.
            </td>
            <td>
               <input type="text" name="parentContactNo" id="parentContactNo" value="">
               <span class="userInfo">*</span>
            </td>
         </tr>
         <tr>
            <td>
               Parent E-mail
            </td>
            <td>
               <input type="text" name="parentEmail" id="parentEmail" value="">
            </td>
         </tr>
         <tr>
            <td valign="top">
               Present Address
            </td>
            <td>
               <textarea id="presentAddress" name="presentAddress" rows="3" cols="40"></textarea>
               <span class="userInfo">*</span>
            </td>
         </tr>
         <tr>
            <td colspan="2">
               <input type="checkbox" name="isActive" id="isActive" checked>
               <label for="isActive">Is Active</label>
            </td>
         </tr>
      </table>
   </fieldset>
</div>
