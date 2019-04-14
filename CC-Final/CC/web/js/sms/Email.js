var Email = function(){
   var self = this;
   
   self.loadSelections = function(){
      var branchElem = document.getElementById("branchSel");
      subject.loadSemesters(branchElem.value);
      studPerformance.loadExams(branchElem);
      subject.loadBatches(branchElem.value,0);
      email.loadRegNos();
      
      //attach events
      $('#batchSel').change(email.loadRegNos);
      //$('#semSel').change(.loadRegNos);
   }
   
   self.loadRegNos = function(){
      var branchRid = document.getElementById("branchSel").value;
      var batchRid = document.getElementById("batchSel").value;
      var sem = document.getElementById("semSel").value;
      
      if(branchRid>0){
         var url = PROJ_CTXT_PATH+"/StudentManagementServlet?command=loadStudSel&branchRid="+branchRid
            +"&batchRid="+batchRid+"&sem="+sem;
         xmlLoadElementValues(url, document.getElementById("regNoSel"));
      }else{
         var emptyOption = "<option value='0'>-- Select --</option>";
         document.getElementById("regNoSel").innerHTML = emptyOption;
      }
   }
   
   self.loadStudSummary = function(viewType){
      
      var branchRid = document.getElementById("branchSel").value;
       var batchRid = document.getElementById('batchSel').value;
      var sem = document.getElementById("semSel").value;
      var examRid = document.getElementById("examSel").value;
      var studRid = document.getElementById("regNoSel").value;
      var monthNo = document.getElementById("monthSel").value;
      if(branchRid>0 && sem>0 && examRid>0&&studRid>0){
         var url = PROJ_CTXT_PATH+"/StudentManagementServlet?command=loadStudSummary&branchRid="+branchRid
               +"&sem="+sem+"&examRid="+examRid+"&monthNo="+monthNo+"&batchRid="+batchRid+"&studRid="+studRid+"&viewType="+viewType;
         xmlLoadElementValues(url, document.getElementById("studentSummaryDiv"));
      }else{
         alert("Branch/Sem/Exam/Student should be selected");
      }
   }
   
   self.handleSuccess = function(mes){
      alert("E-mail has been sent.");
      document.getElementById("sendEmailGIF").style.visibility = 'hidden';
   }
   
   self.handleFailure = function(mes){
      document.getElementById("sendEmailGIF").style.visibility = 'hidden';
      alert(mes);
   }
   
   self.sendStudSummary = function(){
      document.getElementById("sendEmailGIF").style.visibility = 'visible';
      document.getElementById("frmStudentSummary").submit();
   }
};
var email = new Email();