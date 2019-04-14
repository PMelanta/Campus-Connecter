var Branch = function(){
   var self = this;
   
   self.addNewBranch = function(){
      var url = PROJ_CTXT_PATH+"/BranchManagementServlet?command=addNewBranch";
      desktop.showPopup("Add New Branch", url, {});
   }
   
   self.addNewBatch=function (){
        var url = PROJ_CTXT_PATH+"/BranchManagementServlet?command=addNewBatch";
      desktop.showPopup("Add New Batch", url, {});
   }
   self.editBranchDetails = function(branchRid){
      var url = PROJ_CTXT_PATH+"/BranchManagementServlet?command=editBranch&branchRid="+branchRid;
      desktop.showPopup("Edit Branch Details", url, {});
   }
   self.deleteBranchDetails = function(branchRid){
      if(!confirm("Are you sure you want delete?")){
         return false;
      }
      var url = PROJ_CTXT_PATH+"/BranchManagementServlet?command=deleteBranch&branchRid="+branchRid;
      var response = xmlGetResultString(url);
      if(response.trim() == '1'){
         alert("Deleted!")
         self.reloadPage();
      }else{
         alert("Unable to delete");
      }
   }

   
   self.handleSuccess = function(mes){
      alert(mes);
      self.reloadPage();
      desktop.closePopup(0);
   }
   
   self.reloadPage = function(){
      var url = "/BranchManagementServlet?command=loadBranchView"
      desktop.loadPage(url, "Branch Management");
   }
   
   self.handleFailure = function(mes){
      alert(mes);
   }
   
};
var branch = new Branch();

