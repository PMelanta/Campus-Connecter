var StudPerformance = function() {
    var self = this;

    self.loadExams = function(elem) {
        var branchRid = elem.value;
        if (branchRid > 0) {
            var url = PROJ_CTXT_PATH + "/StudPerformanceServlet?command=loadExams&branchRid=" + branchRid;
            xmlLoadElementValues(url, document.getElementById("examSel"));
        } else {

            var emptyOption = "<option value='0'>-- Select --</option>";
            document.getElementById("examSel").innerHTML = emptyOption;
        }
    };

    self.addUpdateMarks = function() {

        var branchRid = document.getElementById("branchSel").value;
        var batchRid = document.getElementById('batchSel').value;
        var sem = document.getElementById("semSel").value;
        var examRid = document.getElementById("examSel").value;
        var studName = document.getElementById("studName").value;
        var regNo = document.getElementById("regNo").value;
        if (branchRid > 0 && sem > 0 && examRid > 0) {
            var url = PROJ_CTXT_PATH + "/StudPerformanceServlet?command=loadMarkSheet&branchRid=" + branchRid
                    + "&sem=" + sem + "&examRid=" + examRid + "&studName=" + studName + "&regNo=" + regNo + "&batchRid=" + batchRid;
            xmlLoadElementValues(url, document.getElementById("studentMarksSheetDiv"));
        } else {
            alert("Branch/Sem/Exam should be selected");
        }
    };

    self.displayMarks = function(viewType) {
        var branchRid = document.getElementById("branchSel").value;
        var batchRid = document.getElementById('batchSel').value;
        var sem = document.getElementById("semSel").value;
        var examRid = document.getElementById("examSel").value;
        if (branchRid > 0 && sem > 0 && examRid > 0) {
            var url = PROJ_CTXT_PATH + "/StudPerformanceServlet?command=loadMarkSheetCon&branchRid=" + branchRid
                    + "&sem=" + sem + "&examRid=" + examRid + "&batchRid=" + batchRid + "&viewType=" + viewType;
            if (viewType == 'HTML') {
                xmlLoadElementValues(url, document.getElementById("studentMarksSheetDiv"));
            } else {
                window.open(url, "_blank", "toolbar=yes,location=no,directories=no, status=no,menubar=yes,scrollbars=yes,resizable=yes, copyhistory=no,fullscreen=no,titlebar=yes");
            }
        } else {
            alert("Branch/Sem/Exam should be selected");
        }
    };

    self.handleSuccess = function(mes) {
        alert(mes);
        self.reloadPage();
    };

    self.reloadPage = function() {
        self.addUpdateMarks();
    };

    self.handleFailure = function(mes) {
        alert(mes);
    };
};

var studPerformance = new StudPerformance();

