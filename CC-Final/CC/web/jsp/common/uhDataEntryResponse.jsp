<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*" 
         import="java.util.*" import="cc.base.*, cc.util.UString" errorPage="" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <script>
 
            function Window_onload() {
                try{

            <%
                if (request.getAttribute("errorMessage") != null) {
                    String errorMessage = (String) request.getAttribute("errorMessage");
                    errorMessage = errorMessage.replaceAll("\n", "");

            %>
                        debugger;
                        window.parent.handleFailure("Error: " + "<%= errorMessage%>");     
 
            <%
            } else if (request.getAttribute("reloadPage") != null) {
            %>
                        window.parent.handleSuccessReload("<%= request.getAttribute("reloadPage")%>","<%= request.getAttribute("url")%>");
            <%
            } else if (request.getAttribute("success") != null) {
            %>
                        debugger;
                        window.parent.handleSuccess("<%= request.getAttribute("success")%>");
            <%
            } else if (request.getAttribute("closePopUp") != null) {
            %>
                        alert("Successfully Registered");
                        window.parent.closePopUp();
            <%     }
            %>
                    } catch (e) {
                        alert(e);
                    }
                }

        </script>
    </head>

    <body onLoad="Window_onload()">
       
    </body>
</html>
