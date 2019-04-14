var Student = function(){
   var self = this;
   
   self.searchStudentDetails = function(){
      var branchRid = document.getElementById('branchSel').value;
      var sem = document.getElementById('semSel').value;
      var batchRid = document.getElementById('batchSel').value;
      var studName = (document.getElementById('studName').value).trim();
      var regNo = (document.getElementById('regNo').value).trim();
      var viewType = $("input[name='viewType']:checked").val();
      
      var url = PROJ_CTXT_PATH+"/StudentManagementServlet?command=viewStudentDetails&branchRid="+branchRid
      +"&sem="+sem+"&studName="+studName+"&regNo="+regNo+"&batchRid="+batchRid+"&viewType="+viewType;
      xmlLoadElementValues(url, document.getElementById("studentDetailsDiv"));
      
   }
   
   self.addNewStudent = function(){
      var url = PROJ_CTXT_PATH+"/StudentManagementServlet?command=addNewStudent";
      desktop.showPopup("Add Student", url, {
          onLoad : function(){
              $( "#dob" ).datepicker({dateFormat: "dd/mm/yy"});
          }
      });
   }
   self.editProfile = function(studentRid){
      var url = PROJ_CTXT_PATH+"/StudentManagementServlet?command=editStudent&studentRid="+studentRid;
      desktop.showPopup("Edit Student Details", url, {});
   }
   
   self.viewProfile = function(studentRid){
      var url = PROJ_CTXT_PATH+"/StudentManagementServlet?command=viewStudent&studentRid="+studentRid;
      desktop.showPopup("Student Details", url, {});
   }
   self.deleteProfile = function(studentRid){
      if(!confirm("Are you sure you want delete?")){
         return false;
      }
      var url = PROJ_CTXT_PATH+"/StudentManagementServlet?command=deleteStudent&studentRid="+studentRid;
      var response = xmlGetResultString(url);
      if(response.trim() == '1'){
         alert("Deleted!")
         self.searchStudentDetails();
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
      var url = "/StudentManagementServlet?command=loadStudentView";
      desktop.loadPage(url, "Student Management");
   }
   
   self.handleFailure = function(mes){
      alert(mes);
   }
}

var student= new Student();

