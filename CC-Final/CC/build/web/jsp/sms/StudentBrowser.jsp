
<%@page import="java.sql.ResultSet"%>
<%
   ResultSet rsBranch = (ResultSet) request.getAttribute("rsBranch");
%>
<div>
   <input type="hidden" name="jsFile" id="jsFile" value="<%=request.getContextPath()%>/js/sms/Student.js">
   <input type="hidden" name="jsFile" id="jsFile" value="<%=request.getContextPath()%>/js/sms/Subject.js">
   <table width ="100%" cellpadding="5" cellspacing="5">
      <tr>
         <td>
            <fieldset>
               <legend>
                  Search Student(s)
                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                  <a href="#" onclick="student.addNewStudent()" title="Add Student">
                     <img src="<%= request.getContextPath()%>/images/common/add.png" 
                          height="15px" width="15px" 
                          >
                     <b>Add New Student</b>
                  </a>
               </legend>
               <table width ="100%" cellpadding="5" cellspacing="5">
                  <tr>
                     <td>
                        Branch
                     </td>
                     <td>
                        <select id="branchSel" name="branchSel" onchange="subject.loadSemesters(this.value,1);subject.loadBatches(this.value,1)">
                           <option value="0">
                              -- Select --
                           </option>
                           <% while (null != rsBranch && rsBranch.next()) {%>
                           <option value="<%=rsBranch.getInt("branch_rid")%>">
                              <%=rsBranch.getString("branch_name") + " (" + rsBranch.getString("branch_code") + ")"%>
                           </option>
                           <%}%>
                        </select>
                     </td>
                     <td>
                        Batch
                     </td>
                     <td>
                        <select id="batchSel" name="batchSel">
                           <option value="0">
                              -- Select Branch-- 
                           </option>
                        </select>
                     </td>
                     <td>
                        Sem
                     </td>
                     <td>
                        <select id="semSel" name="semSel">
                           <option value="0">
                              -- Select Branch-- 
                           </option>
                        </select>
                     </td>
                  </tr>
                  <tr>
                     <td>
                        Name
                     </td>
                     <td>
                        <input type="text" name="studName" id="studName" value ="">
                     </td>
                     <td>
                        Reg. No.
                     </td>
                     <td>
                        <input type="text" name="regNo" id="regNo" value ="">
                     </td>
                     <td colspan="2">
                         View Type : &nbsp;
                         <input type="radio" name="viewType" id="regular" value="1">
                         <label for="regular">Regular</label>
                         &nbsp;&nbsp;
                         <input type="radio" name="viewType" id="tabular" value="2" checked="">
                         <label for="tabular">Tabular</label>
                     </td>
                  </tr>
                  <tr>
                     <td colspan="6" >
                        <span class="helpText">
                           Note : Filter selection is optional, selected filters will be applied while searching student details.
                        </span>
                        <div class="buttonwrapper" style="float: right">
                           <a class="ovalbutton" href="#" onclick="student.searchStudentDetails()"><span>  View  </span></a> 
                        </div>
                     </td>
                  </tr>
               </table>
            </fieldset>
         </td>
      </tr>
      <tr>
         <td>
            <div id="studentDetailsDiv">
            </div>
         </td>
      </tr>

   </table>
   `
</div>

