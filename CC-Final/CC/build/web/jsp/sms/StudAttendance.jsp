<%-- 
    Document   : StudAttendance
    
--%>
<%@page import="java.sql.ResultSet"%>
<%@page import="cc.util.UDate"%>
<%
   ResultSet rsBranch = (ResultSet) request.getAttribute("rsBranch");
   
%>
<div>
   <input type="hidden" name="jsFile" id="jsFile" value="<%=request.getContextPath()%>/js/sms/Attendance.js">
   <input type="hidden" name="jsFile" id="jsFile" value="<%=request.getContextPath()%>/js/sms/Subject.js">
   <table width ="100%" cellpadding="5" cellspacing="5">
      <tr>
         <td>
            <fieldset>
               <legend>
                  Student Attendance
               </legend>
               <table width ="100%" cellpadding="5" cellspacing="5">
                  <tr>
                     <td>
                        Branch
                     </td>
                     <td>
                        <select id="branchSel" name="branchSel" onchange="subject.loadSemesters(this.value);subject.loadBatches(this.value,0)">
                           <option value="0" maxExams ="0">
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
                     <td>Month</td>
                     <td>
                        <select name="monthSel" id="monthSel">
                           <% for(int i=1;i<UDate.monthNames.length;i++){%>
                           <option value="<%=i%>">
                           <%= UDate.monthNames[i]%>
                           </option>
                           <%}%>
                        </select>
                     </td>
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
                     
                  </tr>
                  <tr>
                     <td colspan="6" >
                        <span class="helpText">
                           Note : Name and Reg. no. is optional, if not selected it will not be considered while searching student details.
                        </span>
                        <div class="buttonwrapper" style="float: right">
                           <a class="ovalbutton" href="#" onclick="attendance.updateAttendance()"><span>  Search  </span></a> 
                        </div>
                     </td>
                  </tr>
               </table>
            </fieldset>
         </td>
      </tr>
      <tr>
         <td>
            <div id="studentAttendanceSheetDiv">
            </div>
         </td>
      </tr>

   </table>
   `
</div>

