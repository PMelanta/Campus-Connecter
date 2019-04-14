var Subject = function(){
   var self = this;
   
   self.addNewSubject = function(){
      var url = PROJ_CTXT_PATH+"/SubjectManagementServlet?command=addNewSubject";
      desktop.showPopup("Add New Subject", url, {});
   }
   self.editSubjectDetails = function(subjectRid){
      var url = PROJ_CTXT_PATH+"/SubjectManagementServlet?command=editSubject&subjectRid="+subjectRid;
      desktop.showPopup("Edit Subject Details", url, {});
   }
   self.deleteSubjectDetails = function(subjectRid){
      if(!confirm("Are you sure you want delete?")){
         return false;
      }
      var url = PROJ_CTXT_PATH+"/SubjectManagementServlet?command=deleteSubject&subjectRid="+subjectRid;
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
   
   self.loadSemesters = function(branchRid,includeZero){
      if(includeZero == undefined){
         includeZero = 0;
      }
      if(branchRid>0){
         var url = PROJ_CTXT_PATH+"/SubjectManagementServlet?command=loadSemesters&branchRid="+branchRid
         +"&selName=semSel&includeZero="+includeZero;
         xmlLoadElementValues(url, document.getElementById("semSel"));
      }else{
         var emptyOption = "<option value='0'>-- Select --</option>";
         document.getElementById("semSel").innerHTML = emptyOption;
      }
   }
   
   self.loadBatches = function(branchRid,includeZero){
      if(includeZero == undefined){
         includeZero = 0;
      }
      if(branchRid>0){
         var url = PROJ_CTXT_PATH+"/SubjectManagementServlet?command=loadBatches&branchRid="+branchRid
         +"&selName=batchSel&includeZero="+includeZero;
         xmlLoadElementValues(url, document.getElementById("batchSel"));
      }else{
         var emptyOption = "<option value='0'>-- Select Branch--</option>";
         document.getElementById("batchSel").innerHTML = emptyOption;
      }
   }
   
   self.reloadPage = function(){
      var url = "/SubjectManagementServlet?command=loadSubjectView"
      desktop.loadPage(url, "Subject Management");
   }
   
   self.handleFailure = function(mes){
      alert(mes);
   }
   
};
var subject = new Subject();

