<%-- 
    Document   : AcademicSummary
    
--%>
<%@page import="java.sql.ResultSet"%>
<%@page import="cc.util.UDate"%>
<%
   ResultSet rsBranch = (ResultSet) request.getAttribute("rsBranch");
   
%>
<div>
   <input type="hidden" name="jsFile" id="jsFile" value="<%=request.getContextPath()%>/js/sms/Email.js">
   <input type="hidden" name="jsFile" id="jsFile" value="<%=request.getContextPath()%>/js/sms/StudPerformance.js">
   <input type="hidden" name="jsFile" id="jsFile" value="<%=request.getContextPath()%>/js/sms/Subject.js">
   <table width ="100%" cellpadding="5" cellspacing="5">
      <tr>
         <td>
            <fieldset>
               <legend>
                  Academic Summary [POST]
               </legend>
               <table width ="100%" cellpadding="5" cellspacing="5">
                  <tr>
                     <td width="10%">
                        Branch
                     </td>
                     <td width="30%">
                        <select id="branchSel" name="branchSel" onchange="email.loadSelections();">
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
                     <td width="10%">
                        Batch
                     </td>
                     <td width="15%">
                        <select id="batchSel" name="batchSel">
                           <option value="0">
                              -- Select Branch-- 
                           </option>
                        </select>
                     </td>
                     <td width="10%">
                        Sem
                     </td>
                     <td width="35%">
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
                        Month
                     </td>
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
                        Reg. No.
                     </td>
                     <td>
                         <select id="regNoSel" name="regNoSel">
                           <option value="0">
                              -Select Branch/Batch- 
                           </option>
                        </select>
                     </td>
                  </tr>
                  <tr>
                     <td colspan="6" >
                        <div class="buttonwrapper" style="float: right">
                           <a class="ovalbutton" href="#" onclick="email.loadStudSummary('post')"><span>  Search  </span></a> 
                        </div>
                     </td>
                  </tr>
               </table>
            </fieldset>
         </td>
      </tr>
      <tr>
         <td>
            <div id="studentSummaryDiv">
            </div>
         </td>
      </tr>

   </table>
   `
</div>

