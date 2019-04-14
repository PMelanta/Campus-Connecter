<%-- 
    Document   : NewSubject
    
--%>

<%@page import="java.sql.ResultSet"%>
<%
   String subjectName = "", subjectCode = "";
   int maxInternal = 0, minInternal = 0, maxExternal = 0, minExternal = 0, isActive = 1, subjectRid = 0,
           branchRid=0, noOfSems = 0, subSem = 0,subSeq=0;
   ResultSet rsSubjectDet = (ResultSet) request.getAttribute("rsSubjectDet");
   ResultSet rsBranch = (ResultSet) request.getAttribute("rsBranch");
   if (null != rsSubjectDet && rsSubjectDet.first()) {
      subjectName = rsSubjectDet.getString("sub_name");
      subjectCode = rsSubjectDet.getString("sub_code");
      maxInternal = rsSubjectDet.getInt("sub_internal_max");
      minInternal = rsSubjectDet.getInt("sub_internal_min");
      maxExternal = rsSubjectDet.getInt("sub_external_max");
      minExternal = rsSubjectDet.getInt("sub_external_min");
      subSem = rsSubjectDet.getInt("sub_sem");
      noOfSems = rsSubjectDet.getInt("no_of_sems");
      isActive = rsSubjectDet.getInt("is_active");
      subjectRid = rsSubjectDet.getInt("sub_rid");
      branchRid = rsSubjectDet.getInt("branch_rid");
      subSeq = rsSubjectDet.getInt("sub_seq");
   }
%>
<div style="width: 100%">
   <input type="hidden" id="successHandler" name="successHandler" value="subject.handleSuccess">
   <input type="hidden" id="failureHandler" name="failureHandler" value="subject.handleFailure">
   <form method="POST" action="<%= request.getContextPath()%>/SubjectManagementServlet" 
         target="responseFrame">
      <input type="hidden" id="command" name="command" value="saveSubjectDetails">
      <input type="hidden" id="subjectRid" name="subjectRid" value="<%=subjectRid%>">
      <table width="500px" height="400px" style="margin: 10px">
         <tr class="specialRow">
            <td colspan="100%">
               SUBJECT MANAGEMENT
            </td>
         </tr>
         <tr>
            <td>
               Branch Name
            </td>
            <td>
               <select id="branchSel" name="branchSel" onchange="subject.loadSemesters(this.value)">
                  <option value="0">
                     -- Select -- 
                  </option>
                  <% while (null != rsBranch && rsBranch.next()) {%>
                  <option value="<%= rsBranch.getInt("branch_rid")%>" <%= rsBranch.getInt("branch_rid") == branchRid?"selected":""%> >
                     <%=rsBranch.getString("branch_name") + " (" + rsBranch.getString("branch_code") + ")"%>
                  </option>
                  <%}%>
               </select>
            </td>
         </tr>
         <tr>
            <td>
               Sem
            </td>
            <td>
               <select id="semSel" name="semSel">
                  <% if (subjectRid > 0) {%>
                  <% for (int sem = 1; sem <= noOfSems; sem++) {%>
                  <option value="<%= sem%>" <%= sem == subSem ? "selected" : ""%> >
                     <%= sem%>
                  </option>
                  <%}%>
                  <%} else {%>
                  <option>
                     -- Select Branch-- 
                  </option>
                  <%}%>
               </select>
            </td>
         </tr>
         <tr>
            <td>
               Subject Name
            </td>
            <td>
               <input type="text" name="subName" id="subName" value="<%= subjectName%>">
            </td>
         </tr>
         <tr>
            <td>
               Subject Code
            </td>
            <td>
               <input type="text" name="subCode" id="subCode" value="<%= subjectCode%>">
            </td>
         </tr>
         <tr>
            <td>
               Max. Internal
            </td>
            <td>
               <input type="text" name="maxInternal" id="maxInternal" value="<%= maxInternal == 0 ? "" : maxInternal%>" onkeypress="editKeyBoard(event, this,keybNumber);">
            </td>
         </tr>
         <tr>
            <td>
               Min. Internal
            </td>
            <td>
               <input type="text" name="minInternal" id="minInternal" value="<%= minInternal == 0 ? "" : minInternal%>" onkeypress="editKeyBoard(event, this,keybNumber);">
            </td>
         </tr>
         <tr>
            <td>
               Max. External
            </td>
            <td>
               <input type="text" name="maxExternal" id="maxExternal" value="<%= maxExternal == 0 ? "" : maxExternal%>" onkeypress="editKeyBoard(event, this,keybNumber);">
            </td>
         </tr>
         <tr>
            <td>
               Min. External
            </td>
            <td>
               <input type="text" name="minExternal" id="minExternal" value="<%= minExternal == 0 ? "" : minExternal%>" onkeypress="editKeyBoard(event, this,keybNumber);">
            </td>
         </tr>
         <tr>
            <td>
               Subject Sequence
            </td>
            <td>
               <input type="text" name="subSeq" id="subSeq" value="<%= subSeq == 0 ? "" : subSeq%>" onkeypress="editKeyBoard(event, this,keybNumber);">
            </td>
         </tr>
         <tr>
            <td colspan="2">
               <input type="checkbox" name="isActive" id="isActive" <%= isActive == 1 ? "checked" : ""%>>
               <label for="isActive">Is Active</label>
            </td>
         </tr>
         <tr>
            <td colspan="2" align="right">
               <input type="submit" value="Save" > &nbsp;&nbsp;
               <input type="reset" value="Clear" >
            </td>
         </tr>
      </table>
   </form>
   <iframe id="responseFrame" name="responseFrame" class="hidden"></iframe>
</div>

