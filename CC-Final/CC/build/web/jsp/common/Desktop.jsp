<%@ page contentType="text/html; charset=iso-8859-1" language="java" errorPage="" import="cc.base.UConfig, cc.base.URequestContext, cc.util.ULocale" %>
<%@ page pageEncoding="UTF-8" import="cc.base.URequestContext"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
   String projPath = request.getContextPath();
   String defaultFeature = request.getAttribute("defaultFeature") != null ? request.getAttribute("defaultFeature").toString() : "";
   int productRID = Integer.parseInt(session.getAttribute("productRID").toString());
   int userRID = Integer.parseInt(session.getAttribute("userRID").toString());
   String userID = session.getAttribute("userID").toString();
   String userName = session.getAttribute("userName").toString();
   //String dbProdName = (String) request.getSession().getAttribute("dbProductName");
   String dbProdName = (String) request.getAttribute("dbProductName");
%>
<html>
   <head >
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <link id="<%= projPath%>/style/UbqApp.css" href="<%= projPath%>/style/UbqApp.css" rel="stylesheet" type="text/css">
      <link id="<%= projPath%>/style/OpmsStyle.css" href="<%= projPath%>/style/OpmsStyle.css" rel="stylesheet" type="text/css">

      <link id="<%= projPath%>/js/calendar/calendar-uhealth.css" href="<%= projPath%>/js/calendar/calendar-uhealth.css" rel="stylesheet" type="text/css" media="all" title="blue" />
      <link id="<%= projPath%>/js/menu/menu.css" href="<%= projPath%>/js/menu/menu.css" rel="stylesheet" type="text/css" media="all" title="blue" />

      <SCRIPT id="<%= projPath%>/js/base/xmlHelper.js" language="Javascript" src="<%= projPath%>/js/base/xmlHelper.js"></script>
      <SCRIPT id="<%= projPath%>/js/base/HashTable.js" language="Javascript" src="<%= projPath%>/js/base/HashTable.js"></script>
      <SCRIPT id="<%= projPath%>/js/base/Desktop.js" language="Javascript" src="<%= projPath%>/js/base/Desktop.js"></script>

      <script>
         var PROJECT_CTXT_PATH = "<%= projPath%>";
         var PROJ_CTXT_PATH = PROJECT_CTXT_PATH;
         var projPath = PROJECT_CTXT_PATH;
      </script>

      <SCRIPT id="<%= projPath%>/js/base/ValidateFunction.js" language="Javascript" src="<%= projPath%>/js/base/ValidateFunction.js"></script>
      <SCRIPT id="<%= projPath%>/js/base/RestrictInputFunction.js" language="Javascript" src="<%= projPath%>/js/base/RestrictInputFunction.js"></script>
      <SCRIPT id="<%= projPath%>/js/base/BrowserAbstraction.js" language="Javascript" src="<%=projPath%>/js/base/BrowserAbstraction.js"></script>
      <SCRIPT id="<%= projPath%>/js/base/DynamicTableTemplate.js" language="Javascript" src="<%= projPath%>/js/base/DynamicTableTemplate.js"></SCRIPT>
          
      <SCRIPT id="<%= projPath%>/js/base/Util.js" language="Javascript" src="<%= projPath%>/js/base/Util.js"></SCRIPT>

      <script type="text/javascript" src="<%= projPath%>/js/base/HashTable.js"></script>

      <SCRIPT id="<%= projPath%>/js/base/CalendarPopup.js" language="Javascript" src="<%= projPath%>/js/base/CalendarPopup.js"></script>
      <SCRIPT id="<%= projPath%>/js/calendar/calendar.js" language="Javascript" src="<%= projPath%>/js/calendar/calendar.js"></script>
      <SCRIPT id="<%= projPath%>/js/calendar/lang/calendar-en.js" language="Javascript" src="<%= projPath%>/js/calendar/lang/calendar-en.js"></script>
      <SCRIPT id="<%= projPath%>/js/calendar/calendar-setup.js" language="Javascript" src="<%= projPath%>/js/calendar/calendar-setup.js"></script>

  <SCRIPT id="<%= projPath%>/js/base/Common.js" language="Javascript" src="<%= projPath%>/js/base/Common.js"></script>

      <script src="<%= projPath%>/js/jquery/jquery-1.7.1.js" type="text/javascript"></script>
      <script src="<%= projPath%>/js/jquery/jquery.easing.1.3.js" type="text/javascript"></script>

      

      <script src="<%= projPath%>/js/jquery/jquery-ui-1.8.17.custom.min.js" type="text/javascript"></script>

      <script src="<%= projPath%>/js/fileBrowser/jqueryFileTree.js" type="text/javascript"></script>

      <link href="<%= projPath%>/style/jquery/jquery-ui-1.8.17.custom.css" rel="stylesheet" type="text/css" media="screen" />

      <link href="<%= projPath%>/style/jqueryFileTree.css" rel="stylesheet" type="text/css" media="screen" />


      <title>Campus Connect</title>
      <script>
         
         function desktopInit() {
       
            try {
               desktop.loadPage('/StudentManagementServlet?command=loadStudentView', 'Student Management');
            } catch(e) {
               // if no connection is avaliable
            }
        }
             
      </script> 
   </head>
   <body id="desktopBody" leftmargin="3" onload="desktopInit()" onunload="desktop.handlePageUnload()" 
         autocomplete="off" 
         onkeydown="desktop.handleKeyDown(event);desktop.handleBackspace(event);"
         onmouseup="desktop.stopPopup(event)" onmousemove="desktop.movePopup(event)" >
      <!--          class="backGroundColor">-->


      <input type="hidden" id="generatedSessionID" name="generatedSessionID" value="<%= session.getAttribute("generatedSessionID")%>">
      <input type="hidden" id="dbProdName" name="dbProdName" value="<%=dbProdName%>" >
      <input type="hidden" id="userId" name="userId" value="<%=userID%>" >
      <input type="hidden" id="userFullName" name="userFullName" value="<%=userName%>" >

      <input type="hidden" id="usersListStatus" name="usersListStatus" value="1" >

      <div id="desktopPopupWell" style="z-index: 1;border-radius: 15px;">
      </div>

      <table border="0" cellpadding="0" cellspacing="0" width="100%" id="skeletonTable">
         <tr>
            <td class="desktopTitle" valign="top" align="center" colspan="100%" bgcolor="silver">
               &nbsp; 
               <span style="font-weight: bold;float: left;vertical-align: top;font-family:verdena;font-style: italic;font-weight: bolder;color: white;font-size: larger">
                  Hi <%=userName%>, 
               </span>
               <br>

               <span style="padding-left: 100px;color: white;font-family:verdena;font-size: large;font-weight: bold">
                  Welcome To Campus Connect
               </span>

               <%-- <%@include file="../common/Banner.jsp"%> --%>
               <div style="z-index: 10;float: right;">
                  <span style="float: right;vertical-align: bottom;cursor: pointer">
                     <img src ="<%= projPath%>/images/signout.png" style="height: 40px;width: 40px"
                          title="sign out" onclick="desktop.signout()"/>
                  </span>
               </div>
            </td>
         </tr>
         <tr>
            <td colspan="100%" >

 <%@include file="/jsp/common/Menu.jsp"%>
<!--               <div id="menu">
                  <ul class="menu">
                     <li><a href="#" class="parent"><span>Home</span></a>
                        <ul>
                           <li><a href="#"><span>Sub Item 2</span></a></li>
                           <li><a href="#"><span>Sub Item 3</span></a></li>
                        </ul>
                     </li>
                     <li><a href="#"><span>Help</span></a></li>
                     <li class="last"><a href="#"><span>Contacts</span></a></li>
                  </ul>
               </div>-->
               <div class="hidden"><a href="http://apycom.com/"></a></div>




            </td>
         </tr>
         <tr>
            <td>                    
               <div id="desktopContainer" >
                  <table width="98%" height="100%"  border="0" cellpadding="0" cellspacing="0" align="center" >
<!--                     <td width="15%" valign="top" height="20px" >
                        <table width="100%" height="100%"  border="0" cellpadding="0" cellspacing="0">

                            Insert Exceptions here when available 
                           <tr>
                              <td>
                                 <%--
                                 <%@include file="/jsp/common/Features.jsp"%>
                                 --%>
                              </td>
                           </tr>
                           <tr><td>&nbsp;</td></tr>
                           <tr>
                              <td>
                                 &nbsp;
                              </td>
                           </tr>


                        </table>
                     </td>-->
                     <td width="1%" valign="top">&nbsp;</td>

                     <td width="80%" valign="top" class="wellHeader" >
                        <div id="desktopWell" >
                           <div align='right' id="desktopLoadingMsgDiv" class="wellHeader" style='height:50px;' >
                              <!--                                        <br><span class='loadingMessage' ></span>-->
                           </div>
                        </div>
                     </td>
                     <td width="1%" valign="top">&nbsp;</td>
                     <td width="18%" valign="top" class="hidden"> 
                        <div id="rightPanel" >
                           <table width="100%">
                              <tr>
                                 <td>
                                    <div id="usersDiv">
                                       <div style="max-height: 200px;overflow: auto;background: #ffffff">
                                        
                                       </div>
                                    </div>
                                 </td>
                              </tr>
                              <tr>
                                 <td class="specialRow">
                                    Recent Tasks
                                 </td>
                              </tr>
                           </table>

                        </div>
                     </td>
                  </table>
               </div>    
            </td>
         </tr>
         <tr><td colspan="100%" align="center"> <%@include file="/jsp/common/Footer.jsp"%></td></tr>
      </table>

      <iframe id="statusMessageFrame" style="display:none; width:300px; height:50px;z-index:1;" class="centered">    
      </iframe>      
      <div id="statusMessageDiv" style="display:none; width:300px; height:50px;z-index:2;" class="centered">
         <table width="100%" height="100%" border="0" cellpadding="3" cellspacing="0">
            <tr> 
               <td valign="middle"> <img src="images/info.gif" width="33px" height="33px"></td>
               <td valign="middle" align="center"> <span> Success Message</span></td>
            </tr>
         </table>
      </div>

      <iframe id="desktopReloginIFrame" 
              style="position:absolute;width:400px;display:none;" class="hidden">    
      </iframe> 
      <div id="reloginDiv" style="position:absolute;width:400px;border:2px solid #360; border-color:#445677;;display:none" >
         <table width="100%" cellpadding="0" cellspacing="0" border="0" class="whiteBG" >
            <tr class="heading" >
               <td align="center" height="25px" >
                  Your session has expired. Please login again...
               </td>
            </tr>
            <tr>
               <td width="100%">
                  <table width="100%" cellpadding="2" border="0">
                     <tr>
                        <td height="25px" >
                           User ID
                        </td>
                        <td>
                           : <b><%= userID%></b>
                           <input type="hidden" name="userID" id="userID" value="<%= userID%>">
                        </td>
                     </tr>
                     <tr>
                        <td height="25px" >
                           Password
                        </td>
                        <td>
                           : <input type="password" id="loginPassWord"  name="loginPassWord" onkeydown="desktop.reloginOnEnter(event)">
                        </td>
                     </tr>
                     <tr>
                        <td height="35px" ></td>
                        <td align="right" >
                           <input type="button" id="btnRelogin" name="btnRelogin" onclick="desktop.relogin()" value="Login">
                           <input type="button" id="btnCloseRelogin" name="btnCloseRelogin" onclick="desktop.closeReloginDiv(false)" value="Close"> &nbsp;  &nbsp;
                        </td>
                     </tr>
                  </table>
               </td>
            </tr>
         </table>
      </div>

      <div id="errorDisplay" class="shadow whiteBG collapseBorder" style="position:absolute;width:500px;border:2px solid #360; border-color:#445677;;display:none;z-index:4"  >
         <table>
            <tr class="tableBodyColor" style="height:30px" >
               <td>
                  <label class="errorPageTitle"> System Error</label><br>
               </td>
               <td align="right">
                  <a href="javascript:desktop.closeErrorDisplay()">Close</a>
               </td>
            </tr>
            <tr>
               <td colspan="100%" id="errorDisplayTD">

               </td>
            </tr>
         </table>
      </div>

      <div id="desktopChatPopupDiv" style="bottom: 0;right: 0;">

      </div>

      <form name="frmLogin" id="frmLogin" action="Login"></form>

   </body>
</html>