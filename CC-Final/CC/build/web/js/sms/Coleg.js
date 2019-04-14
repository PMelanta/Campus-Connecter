
var Coleg = function(){
   var self = this;
   
//   self.addNewBranch = function(){
//      var url = PROJ_CTXT_PATH+"/BranchManagementServlet?command=addNewBranch";
//      desktop.showPopup("Add New Branch", url, {});
  // }
   self.editColegDetails = function(colegRid){
      var url = PROJ_CTXT_PATH+"/AdminServlet?command=editColeg&colegRid="+colegRid;
      desktop.showPopup("Edit College Details", url, {});
   }
//   self.deleteBranchDetails = function(branchRid){
//      if(!confirm("Are you sure you want delete?")){
//         return false;
//      }
//      var url = PROJ_CTXT_PATH+"/AdminServlet?command=deleteBranch&branchRid="+branchRid;
//      var response = xmlGetResultString(url);
//      if(response.trim() == '1'){
//         alert("Deleted!")
//         self.reloadPage();
//      }else{
//         alert("Unable to delete");
//      }
//   }

   
   self.handleSuccess = function(mes){
      alert(mes);
      self.reloadPage();
      desktop.closePopup(0);
   }
   
   self.reloadPage = function(){
      var url = "/AdminServlet?command=loadCollegeView"
      desktop.loadPage(url, "College");
   }
   
   self.handleFailure = function(mes){
      alert(mes);
   }
   
};
var coleg = new Coleg();


