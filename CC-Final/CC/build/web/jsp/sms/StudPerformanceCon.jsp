<%-- 
    Document   : StudPerformanceCon
    
--%>
<%@page import="java.sql.ResultSet"%>
<%
   ResultSet rsBranch = (ResultSet) request.getAttribute("rsBranch");
   
%>
<div>
   <input type="hidden" name="jsFile" id="jsFile" value="<%=request.getContextPath()%>/js/sms/StudPerformance.js">
   <input type="hidden" name="jsFile" id="jsFile" value="<%=request.getContextPath()%>/js/sms/Subject.js">
   <table width ="100%" cellpadding="5" cellspacing="5">
      <tr>
         <td>
            <fieldset>
               <legend>
                  Student Performance Consolidated
               </legend>
               <table width ="100%" cellpadding="5" cellspacing="5">
                  <tr>
                     <td>
                        Branch
                     </td>
                     <td>
                        <select id="branchSel" name="branchSel" onchange="subject.loadSemesters(this.value);studPerformance.loadExams(this);subject.loadBatches(this.value,0)">
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
                     <td>
                        Exam
                     </td>
                     <td>
                        <select id="examSel" name="examSel">
                           <option value="0">
                              -- Select Branch-- 
                           </option>
                        </select>
                     </td>
                     <td>
                        
                     </td>
                     <td>
                        
                     </td>
                     <td>
                       
                     </td>
                     <td>
                        
                     </td>
                  </tr>
                  <tr>
                     <td colspan="6" >
                        <div class="buttonwrapper" style="float: right">
                           <a class="ovalbutton" href="#" onclick="studPerformance.displayMarks('HTML')"><span>  View  </span></a> 
                           <a class="ovalbutton" href="#" onclick="studPerformance.displayMarks('EXCEL')"><span>  Excel Export  </span></a> 
                        </div>
                     </td>
                  </tr>
               </table>
            </fieldset>
         </td>
      </tr>
      <tr>
         <td>
            <div id="studentMarksSheetDiv">
            </div>
         </td>
      </tr>

   </table>
   `
</div>

