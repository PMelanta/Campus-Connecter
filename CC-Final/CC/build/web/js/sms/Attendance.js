var Attendance = function() {
    var self = this;

    self.updateAttendance = function() {
        var branchRid = document.getElementById("branchSel").value;
        var batchRid = document.getElementById("batchSel").value;
        var monthNo = document.getElementById("monthSel").value;
        var sem = document.getElementById("semSel").value;
        var studName = document.getElementById("studName").value;
        var regNo = document.getElementById("regNo").value;
        if (branchRid > 0 && sem > 0) {
            var url = PROJ_CTXT_PATH + "/AttendanceServlet?command=loadAttendanceSheet&branchRid=" + branchRid
                    + "&sem=" + sem + "&studName=" + studName + "&regNo=" + regNo + "&monthNo=" + monthNo + "&batchRid=" + batchRid;
            xmlLoadElementValues(url, document.getElementById("studentAttendanceSheetDiv"));
        } else {
            alert("Branch/Sem should be selected");
        }
    }

    self.viewAttendance = function(viewType) {
        var branchRid = document.getElementById("branchSel").value;
        var batchRid = document.getElementById("batchSel").value;
        var monthNo = document.getElementById("monthSel").value;
        var sem = document.getElementById("semSel").value;
        if (branchRid > 0 && sem > 0) {
            var url = PROJ_CTXT_PATH + "/AttendanceServlet?command=loadAttendanceSheetCon&branchRid=" + branchRid
                    + "&sem=" + sem + "&monthNo=" + monthNo + "&batchRid=" + batchRid + "&viewType=" + viewType;
            if (viewType == 'HTML') {
                xmlLoadElementValues(url, document.getElementById("studentAttendanceSheetDiv"));
            } else {
                window.open(url, "_blank", "toolbar=yes,location=no,directories=no, status=no,menubar=yes,scrollbars=yes,resizable=yes, copyhistory=no,fullscreen=no,titlebar=yes");
            }

        } else {
            alert("Branch/Sem should be selected");
        }
    }

    self.handleSuccess = function(mes) {
        alert(mes);
        self.reloadPage();
    }

    self.reloadPage = function() {
        self.updateAttendance();
    }

    self.handleFailure = function(mes) {
        alert(mes);
    }
};
var attendance = new Attendance();

