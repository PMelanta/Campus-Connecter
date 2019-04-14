<%-- 
    Document   : ViewStudent
    
--%>
<%@page import="cc.util.UDate"%>
<%@page import="java.sql.ResultSet"%>
<%
   ResultSet rsStudentDetails = (ResultSet) request.getAttribute("rsStudentDetails");
   /*
    stud_rid,stud_first_name,stud_last_name,stud_reg_no,stud_branch_rid,stud_cur_sem,
    stud_gender,stud_dob,stud_image_url,stud_fathers_name,stud_mothers_name,stud_address,
    stud_email,stud_parent_email,stud_parent_contact,is_active
    */
   String studFirstName = "", studLastName = "", studRegNo = "", 
           studGender = "", studDOB = "", studImageUrl = "", studFathersName = "", studMothersName = "", studAddress = "",
           studEmail = "", studParentEmail = "", studParentContact = "",branchName="",batchName="",branchCode="";
   int studentRid = 0, IsActive = 1, studCurSem=0;
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
      studentRid = rsStudentDetails.getInt("stud_rid");
      branchName = rsStudentDetails.getString("branch_name");
      batchName = rsStudentDetails.getString("batch_name");
      branchCode = rsStudentDetails.getString("branch_code");
   }
%>
<div>
      <input type="hidden" name="command" id="command" value="saveStudentDetails">
      <input type="hidden" name="studentRid" id="studentRid" value="<%=studentRid%>">
      <table width="100%" style="min-width: 400px" cellspacing="6" cellpadding="6">
         <tr>
            <td class="details" colspan="2" width="100%">
               <img src="<%= studImageUrl == null || "".equals(studImageUrl.trim()) ? request.getContextPath() + "/images/common/user.jpg" : studImageUrl%>" 
                    width="60px" height="60px" style="padding-right: 4px;padding-bottom: 4px;float: left;"/>
               <span style="float: left;font-weight: bold;">
                  <%= studFirstName + " " + studLastName +" ("+studCurSem+ " sem / "+batchName+")"%>
               </span>
               <br />
               <span style="float: left">
                  <%=branchName + " (" + branchCode + ")"%>
               </span>
               <br />
               <span style="float: left">
                  <%= studRegNo%>
               </span>
               <br />
               <span>
                  <%= studEmail%>
               </span>
            </td>
         </tr>
        <%-- <tr>
            <td>
               Name
            </td>
            <td>
                  <%= studFirstName+" "+studLastName%>
            </td>
         </tr>
         <tr>
            <td>
               Reg. No.
            </td>
            <td>
               <%= studRegNo%>
            </td>
         </tr>
         <tr>
            <td>
               Branch
            </td>
            <td>
               <%=rsBranch.getString("branch_name") + " (" + rsBranch.getString("branch_code") + ")"%>
            </td>
         </tr>
         <tr>
            <td>
               Sem
            </td>
            <td>
               <%= studCurSem%>
            </td>
         </tr>
        <tr>
            <td>
               Student Email
            </td>
            <td>
               <%= studEmail%>
            </td>
         </tr>
        --%>
        <tr class="oddRow">
            <td>
               Gender
            </td>
            <td class="dataField">
               <%= studGender%>
            </td>
         </tr>
         <tr class="evenRow">
            <td>
               Date Of Birth
            </td>
            <td class="dataField">
               <%= studDOB%>
            </td>
         </tr>
         <tr class="oddRow">
            <td>
               Father's Name
            </td>
            <td class="dataField">
               <%= studFathersName%>
            </td>
         </tr>
         <tr class="evenRow">
            <td>
               Mother's Name
            </td>
            <td class="dataField">
               <%= studMothersName%>
            </td>
         </tr>
         <tr class="oddRow">
            <td>
               Parent Email
            </td>
            <td class="dataField">
               <%= studParentEmail%>
            </td>
         </tr>
         <tr class="evenRow">
            <td>
               Contact No.
            </td>
            <td class="dataField">
               <%= studParentContact%>
            </td>
         </tr>
         <tr class="oddRow">
            <td>
               Address
            </td>
            <td class="dataField">
               <%= studAddress%>
            </td>
         </tr>
         <tr class="evenRow">
            <td>
               Status
            </td>
            <td class="dataField">
               <%= IsActive == 1 ? "Active" : "In Active"%>
            </td>
         </tr>
      </table>
</div>
