<%-- 
    Document   : AddUpdateStudent
    
--%>
<%@page import="cc.util.UDate"%>
<%@page import="java.sql.ResultSet"%>
<%
   ResultSet rsBranch = (ResultSet) request.getAttribute("rsBranch");
   ResultSet rsBatch = (ResultSet) request.getAttribute("rsBatch");
   ResultSet rsStudentDetails = (ResultSet) request.getAttribute("rsStudentDetails");
   /*
    stud_rid,stud_first_name,stud_last_name,stud_reg_no,stud_branch_rid,stud_cur_sem,
    stud_gender,stud_dob,stud_image_url,stud_fathers_name,stud_mothers_name,stud_address,
    stud_email,stud_parent_email,stud_parent_contact,is_active
    */
   String studFirstName = "", studLastName = "", studRegNo = "", 
           studGender = "", studDOB = "", studImageUrl = "", studFathersName = "", studMothersName = "", studAddress = "",
           studEmail = "", studParentEmail = "", studParentContact = "";
   int studentRid = 0, IsActive = 1, branchRid = 0,studBatchRid=0, maxNoOfSems = 0,studCurSem=0, canAddEvents=0;
   if (null != rsStudentDetails && rsStudentDetails.first()) {
      studFirstName = rsStudentDetails.getString("stud_first_name");
      studLastName = rsStudentDetails.getString("stud_last_name");
      studRegNo = rsStudentDetails.getString("stud_reg_no");
      studCurSem = rsStudentDetails.getInt("stud_cur_sem");
      studGender = rsStudentDetails.getString("stud_gender");
      studDOB = UDate.dbToDisplay(rsStudentDetails.getString("stud_dob"));
      studImageUrl = rsStudentDetails.getString("stud_image_url");
      studFathersName = rsStudentDetails.getString("stud_fathers_name");
      studMothersName = rsStudentDetails.getString("stud_mothers_name");
      studAddress = rsStudentDetails.getString("stud_address");
      studEmail = rsStudentDetails.getString("stud_email");
      studParentEmail = rsStudentDetails.getString("stud_parent_email");
      studParentContact = rsStudentDetails.getString("stud_parent_contact");
      IsActive = rsStudentDetails.getInt("is_active");
      branchRid = rsStudentDetails.getInt("stud_branch_rid");
      maxNoOfSems = rsStudentDetails.getInt("no_of_sems");
      studentRid = rsStudentDetails.getInt("stud_rid");
      studBatchRid = rsStudentDetails.getInt("stud_batch_rid");
      canAddEvents = rsStudentDetails.getInt("can_create_events");

   }
%>
<div>
   <input type="hidden" id="successHandler" name="successHandler" value="student.handleSuccess">
   <input type="hidden" id="failureHandler" name="failureHandler" value="student.handleFailure">

   <form enctype="multipart/form-data" action="StudentManagementServlet" method="POST" target="responseFrame">
      <input type="hidden" name="command" id="command" value="saveStudentDetails">
      <input type="hidden" name="studentRid" id="studentRid" value="<%=studentRid%>">
      <table width="100%" cellspacing="3" cellpadding="3">
         <tr>
            <td>
               Name
            </td>
            <td>

               <input type="text" name="firstName" id="firstName" value ="<%= studFirstName%>"><span class="helpText">(First)</span>
               &nbsp;

               <input type="text" name="lastName" id="lastName" value ="<%= studLastName%>"><span class="helpText">(last)</span>
            </td>
         </tr>
         <tr>
            <td>
               Reg. No.
            </td>
            <td>
               <input type="text" name="regNo" id="regNo" value ="<%= studRegNo%>">
            </td>
         </tr>
         <tr>
            <td>
               Branch
            </td>
            <td>
               <select id="branchSel" name="branchSel" onchange="subject.loadSemesters(this.value);subject.loadBatches(this.value,0)">
                  <option value="0">
                     -- Select --
                  </option>
                  <% while (null != rsBranch && rsBranch.next()) {%>
                  <option value="<%=rsBranch.getInt("branch_rid")%>"
                          <%=branchRid>0 && rsBranch.getInt("branch_rid") == branchRid?"selected":""%>>
                     <%=rsBranch.getString("branch_name") + " (" + rsBranch.getString("branch_code") + ")"%>
                  </option>
                  <%}%>
               </select>
            </td>
         </tr>
         <tr>
            <td>
               Batch
            </td>
            <td>
               <select id="batchSel" name="batchSel">
                  <% if (studentRid > 0) {%>
                  <% while(null != rsBatch && rsBatch.next()) {%>
                  <option value="<%=rsBatch.getInt("batch_rid")%>" <%= rsBatch.getInt("batch_rid") ==studBatchRid?"selected":""%> >
                     <%=rsBatch.getString("batch_name")%>
                  </option>
                  <%}}else{%>
                  <option value="0">
                     -- Select Branch-- 
                  </option>
                 <% }%>
               </select>
            </td>
         </tr>
         <tr>
            <td>
               Sem
            </td>
            <td>
               <select id="semSel" name="semSel">
                  <% if (studentRid > 0) {%>
                  <% for (int i = 1; i <= maxNoOfSems; i++) {%>
                  <option value="<%=i%>" <%= i==studCurSem?"selected":""%> >
                     <%=i%>
                  </option>
                  <%}}else{%>

                  <option value="0">
                     -- Select Branch-- 
                  </option>
                 <% }%>
               </select>
            </td>
         </tr>
         <tr>
            <td>
               Gender
            </td>
            <td>
               <input type="radio" name="gender" id="male" value="male"  <%= "".equals(studGender) || "male".equals(studGender) ? "checked" : ""%>
                      >
               <label for="male">Male</label>
               &nbsp;&nbsp;
               <input type="radio" name="gender" id="female" value="female" <%= "female".equals(studGender) ? "checked" : ""%>>
               <label for="female">Female</label>
            </td>
         </tr>
         <tr>
            <td>
               Date Of Birth
            </td>
            <td>
               <input type="text" name="dob" id="dob" value ="<%= studDOB%>" size="14"
                      onkeypress="" maxlength="10"
                      >
               
            </td>
         </tr>
         <tr>
            <td>
               Profile Picture
            </td>
            <td>
               <input type="file" name="file" id="file">
            </td>
         </tr>
         <tr>
            <td>
               Student Email
            </td>
            <td>
               <input type="text" name="studEmail" id="studEmail" value="<%= studEmail%>">
            </td>
         </tr>
         <tr>
            <td>
               Father's Name
            </td>
            <td>
               <input type="text" name="father" id="father" value ="<%= studFathersName%>">
            </td>
         </tr>
         <tr>
            <td>
               Mother's Name
            </td>
            <td>
               <input type="text" name="mother" id="mother" value ="<%= studMothersName%>">
            </td>
         </tr>
         <tr>
            <td>
               Parent Email
            </td>
            <td>
               <input type="text" name="parentEmail" id="parentEmail" value ="<%= studParentEmail%>">
            </td>
         </tr>
         <tr>
            <td>
               Contact No.
            </td>
            <td>
               <input type="text" name="contactNo" id="contactNo" value ="<%= studParentContact%>">
            </td>
         </tr>
         <tr>
            <td>
               Address
            </td>
            <td>
               <textarea id="address" name="address" rows="3" cols="60"><%= studAddress%></textarea>
            </td>
         </tr>
         <tr>
            <td colspan="2">
               <input type="checkbox" name="canCreateEvent" id="canCreateEvent" <%= canAddEvents == 1 ? "checked" : ""%>>
               <label for="canCreateEvent">Can Create Events?</label>
            </td>
         </tr>
         <tr>
            <td colspan="2">
               <input type="checkbox" name="isActive" id="isActive" <%= IsActive == 1 ? "checked" : ""%>>
               <label for="isActive">Is Active</label>
            </td>
         </tr>
         <tr>
            <td colspan="2" align="right">
               <input type="submit" value="Save">
               &nbsp;&nbsp;
               <input type="reset" value="Reset">
            </td>
         </tr>
      </table>
   </form>
   <iframe name="responseFrame" id="responseFrame" style="display: none"/>
</div>
