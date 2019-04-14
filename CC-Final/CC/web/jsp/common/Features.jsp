<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*" 
         import="java.util.*" import="cc.util.*" import="cc.base.*" errorPage="" %>
<%@page pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%
    String dateStr = cc.util.UDate.nowDisplayString();
%>
<input type="hidden" id = "searchingFor" name ="searchingFor">
<table class="lhsMenuTable shadow" width=100% border="0" height="100%"  cellpadding="5" cellspacing="5">
    
    <%
        Vector v = (Vector) request.getAttribute("accessibleCommands");


        if (v != null) {

            for (int i = 0; i < v.size(); i++) {

                UFeature f = (UFeature) v.elementAt(i);

                String link_url = f.featCommand;

                if (link_url != null && !link_url.equals("")) {
    %>
    <tr>
        <td  align="left" height="33" class ="specialRow"  onmouseover="this.className='mainLinkMouseOver'"
             onmouseout="this.className='specialRow'" title="<%= f.featHelp%>" 
             onclick="desktop.loadPage('<%= f.featCommand%>', '<%= f.featName%>')">
            <a href="#" onclick="desktop.loadPage('<%= f.featCommand%>', '<%= f.featName%>')" style="color: white"><%= f.featName%></a>
        </td>
    </tr>
    <% }
            }
        }
    %>

</table>                        
<!-- </form> -->
