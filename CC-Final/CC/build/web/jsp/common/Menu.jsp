<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*" 
import="java.util.*" import="cc.util.*" import="cc.base.*" errorPage="" %>
<%@page pageEncoding="UTF-8"%>
<%-- 
    Document   : Menu
    
    
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<%
  String proj = request.getContextPath();
%>
<script src="<%= proj%>/js/menu/menu.js" type="text/javascript"></script>

<script language="javascript">

    function checkDirtyBit(mode, featureName, featureCommand, featureCode){
    
    var state = true;
    try{
        state = desktop.checkTransition();
    }catch(e){
        // do nothing
    }
        if(state){
            if(mode == '1'){
                window.open(featureCommand, '', "toolbar=yes,location=no,directories=no, status=yes,menubar=yes,scrollbars=yes,resizable=yes, copyhistory=no,fullscreen=no,titlebar=yes");
            }if(mode == '2'){
                loadRemoteApp(featureCommand, featureCode) ;
            }else{
                   // location.href = featureCommand;
                   desktop.showBusyMessage();
		   
		   if(document.getElementById('myslidemenu')) {
                   document.getElementById('myslidemenu').style.display = "none";
		   }
                   // setTimeout(function(){ 
                          try {
                              desktop.loadPage(featureCommand, featureName, featureCode);
                          } catch(ex) {
                                // if server status is active and not updated but the network is not avaliable
                                if(ex.message != null && ex.message.indexOf("system cannot locate the resource specified") > 0) {
                                    alert('Failed to connect. Switching to offline mode');
                                }
                          }

			  if(document.getElementById('myslidemenu')) {
                          document.getElementById('myslidemenu').style.display = "inline";
			  }
			  
                  // }, 0);
            }
        }    
    }
    
    function loadRemoteApp(remoteURL, prodCode){
    window.open("<%= request.getContextPath()%>/controlServlet?command=uDashBoard&remoteProdCode=" + prodCode ,"_blank", "toolbar=no,location=yes,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,fullscreen=yes,titlebar=no");
    }
    
    
</script>




<table width=100% border="0" height="100%" cellpadding="0" cellspacing="0">
    <tr class="activityBar" >
<!--        <td valign="middle"  height="30px" width="auto" >
            <% Vector v = (Vector) request.getAttribute("accessibleCommands"); %>
            <input type="hidden" name="menuSize" value="<%= v.size()%>">
            <input type="hidden" id = "searchingFor" name ="searchingFor">
            <input type="hidden" id="rid" name="rid">
            <input type="hidden" id="name" name="name">
            <input type="hidden" id="command" name="command">
            <input type="hidden" id="defPage" name="defPage">
            &nbsp;<input type="text" name="currentActivity" id="currentActivity" size="45" readonly  tabindex=-1>&nbsp;
        </td>-->
        <td align="left" width="100%" >
            
            <div id="menu">
                
               <ul class="menu">
                    
                    <%
                    int tempRootParentRID = -2, tempFeatureParentGroupRID = -2;
                    
                    if(v != null){
                        for(int i = 0; i < v.size(); i++) {
                            
                            UFeature f = (UFeature) v.elementAt(i);
                            int rootParentRID = f.rootParentRID;
                            String rootParentDesc = f.rootParentDesc;
                            int featureParentGroupRID = f.featParentGroup;
                            int featureRID_L2 = f.featureRID_L2;
                            
                            String featureName_L2 = f.featureName_L2;
                            if(rootParentRID <= 0)
                                continue;
                            
                            if(!"".equals(UString.blankIfNull(featureName_L2))) {
                                rootParentDesc = featureName_L2;
                            }
                            
                            if(rootParentRID != tempRootParentRID ) {
                                tempRootParentRID = rootParentRID;
                    %>
                    
                    
                    <li><a href="#" class="parent" tabindex="-1" >
                          <span>
                          <%= rootParentDesc %>
                          </span>
                       </a>
                        <ul>
                             
                            <%
                            int currMode = 0 ;
                            while(true) {
                                    if(i < v.size()) {
                                        currMode = "L".equalsIgnoreCase(f.featGroup)? 1 : "RA".equalsIgnoreCase(f.featGroup)? 2 : 0 ;
                                        String onClickFun = "checkDirtyBit('" + currMode + "', '" + f.featName + "', '" + (f.featCommand + "&featureRID=" + f.featRID) + "', '" + f.featCode + "')";
                                        
                                        if(featureRID_L2 == rootParentRID) {
                                            tempFeatureParentGroupRID = f.featParentGroup;
                            %>
                            <li><a href="#" onclick="" tabindex="-1" >
                                  <span><%= f.rootParentDesc %></span>
                               </a>
                                <ul>
                                    
                                    <%
                                    
                                    while(true) {
                                                if(i < v.size()) {
                                            currMode = "L".equalsIgnoreCase(f.featGroup) ? 1 : "RA".equalsIgnoreCase(f.featGroup)? 2 : 0 ;
                                            onClickFun = "checkDirtyBit('" + currMode + "', '" + f.featName + "', '" + (f.featCommand + "&featureRID=" + f.featRID) + "', '" + f.featCode + "')";
                                    %>
                                    <li><a href="#" onclick="<%= onClickFun %>" tabindex="-1" >
                                          <span><%= f.featName %></span>
                                       </a></li>
                                    <%
                                                }
                                                
                                                if(i < v.size() -1) {
                                                    f = (UFeature) v.elementAt(++i);
                                                    rootParentRID = f.rootParentRID;
                                                    featureParentGroupRID = f.featParentGroup;
                                                    featureRID_L2 = f.featureRID_L2;
                                                    if(featureParentGroupRID != tempFeatureParentGroupRID) {
                                                        i--;
                                                        break;
                                                    }
                                                } else {
                                                    break;
                                                }
                                    } 
                                    %>  
                                </ul>
                            </li>
                            <%                                            
                                        } else {                                        
                            %>
                            <li><a href="#" onclick="<%= onClickFun %>" tabindex="-1" >
                                  <span><%= f.featName %></span>
                               </a>
                            </li>
                            <%
                                        }
                                    }
                                    
                                    if(i < v.size() -1) {
                                        f = (UFeature) v.elementAt(++i);
                                        rootParentRID = f.rootParentRID;
                                        featureParentGroupRID = f.featParentGroup;
                                        featureRID_L2 = f.featureRID_L2;
                                        if(rootParentRID != tempRootParentRID) {
                                            i--;
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                            } 
                            
                            %>
                        </ul>
                    </li>
                    <%
                            }
                            
                        }
                    }
                    %>    
                </ul>
                
                <br style="clear: left" />
            </div>
            
            
        </td> 
    </tr>
    <tr  >
        <td style="border-width:0px"></td>
        
        <td style="border-width:1px" align="right">
            <iframe name="menuIFrame"  id="menuIFrame" style="position:absolute;left:0px;display:none;width:0px;z-index:1;"  frameborder="0"></iframe>
            
        </td>
    </tr>
</table>

