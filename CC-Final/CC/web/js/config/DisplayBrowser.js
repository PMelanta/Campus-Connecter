DisplayBrowser = function(){
    var self = this;
    
    self.LoadDisplay = function() {
        var displayTxt = document.getElementById("queryTxt").value;
   
        alert(displayTxt);
        var url = PROJECT_CTXT_PATH + "/ConfigServlet?command=loadDisplayBrowser&textarea=" + displayTxt;
        var loadDisplay = document.getElementById("loadDisplayDiv");
         
        xmlLoadElementValues(url, loadDisplay);
    }
}
var displayBrowser = new DisplayBrowser();
