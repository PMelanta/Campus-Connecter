<%@page import = "cc.util.UDate"%>
<%-- 
    Document   : PostNewTopic
    
    
--%>

<%
    int contextRID = 0;
    String contextType = request.getAttribute("fileContextType").toString(); 
    %>


<input type="hidden" id="jsFile" name="jsFile" value="<%=request.getContextPath()%>/js/common/Attachment.js">

<table width="100%" class="whiteBG">
    <tr class="specialRow"  id="rowRemarks" onclick='attachment.handleAttachmentDivDisplay()' >
        <th colspan="100%" align="left">
            <span name="expandComponents" id="expandComponents" class="arrow" title="Show Attachments" onclick="">
                &#9658;
            </span>
            <span class="arrow">Add Attachment</span>
        </th>
    </tr>

    <tr>
        <td colspan="100%" align="left">
            <div class = "hidden" id="attachmentDetailsDiv">

                <input type="hidden" name="contextRID" value="<%= contextRID%>"  >
                <input type="hidden" name="contextType" value="<%= contextType%>">
                <table border="0" cellpadding="3" cellspacing="0" width="100%" class="whiteBG" >
                    <tr>
                        <td>
                            <div id="attachmentDiv" style="width: 100%">
                                <table id="attachmentTable" border="0" cellpadding="0" cellspacing="0" width="100%" >
                                    <tr>
                                        <th width="3%" height="25px" >&nbsp; </th>
                                        <th align="left" width="40%" class="specialRow" style="padding-left:3px;" >Description <span class='userInfo'>*</span></th>
                                        <th align="left" width="46%" class="specialRow" style="padding-left:3px;" >Attachment</th>
                                        <th width="3%" class="specialRow">&nbsp; </th>
                                    </tr>

                                    <tr><td height="4px" ></td></tr>


                                    <tr id="attachmentCloneRow" align="left" >
                                        <td height="25px" ><span id="rowErrorMsg" class="errorInfo"></span></td>
                                        <td style="padding-left:3px;" >
                                            <input type="text" name="fileDescription" id="fileDescription" value="" size="35" >
                                        </td>
                                        
                                        <td style="padding-left:3px;" >
                                            <input type="hidden" id="deleteFlag" name="deleteFlag" value="0">
                                            <input type="hidden" name="filePath" id="filePath">
                                            <span id="attachmentFileName" name="attachmentFileName" class="hidden" ></span>
                                            <input type="file" size="30" maxlength="300" name="file" id="file" value="" class="file"
                                                   onchange="attachment.setAttachmentPath(this);">
                                        </td>
                                        <td >
                                            <input type="button" name="attachmentAdd" value=" + "
                                                   class="deleteSmallButton whiteBG" onclick="attachment.addAttachment(this);" >
                                            <input type="button" name="attachmentDelete" value="X"
                                                   class="deleteSmallButton whiteBG hidden" onclick="attachment.deleteAddedAttachment(this);">
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </td>
                    </tr>
                </table>
                <iframe name="entryResponseFrame" style="visibility:hidden;display:none;"></iframe>
            </div>
        </td>
    </tr>
</table>

<%--end of file attatchment--%>