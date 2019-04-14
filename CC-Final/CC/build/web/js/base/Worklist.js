var WORK_LIST_CONTEXT = true;
var worklistTable = null;
  
    /*-----variables added by prileep--*/
    var currentDate = "";
    var worklistHelpMessage = 'Select the status and click on View to list.';

function refreshWorklistItem(row) {

  var dlt = document.getElementById('worklistTable');

  if(dlt.selectRow)
    dlt.selectRow(row);
}

function refreshSelectedWorklistItem() {

  if(selectedWorklistRow != null) {
    var dlt = document.getElementById('worklistTable');

  if(dlt.selectRow)
    dlt.selectRow(selectedWorklistRow);
  }
}

function selectFirstWorklistItem() {
  // Select the first worklist item
  var dlt = document.getElementById('worklistTable');

  if(dlt != null) {  
    var rows = dlt.getElementsByTagName("TR");

    if(rows.length > 0) {
      if(dlt.selectRow)
        dlt.selectRow(rows[0]);
    }
  }
}

function setActivityBarTitle(title) {
    desktop.setActivity(title);
  /*var e = document.getElementById('currentActivity');

  if(e != null)
    e.value = title;
   */
}

var selectedWorklistRow = null;
var savedWorklistRID = 0;

function loadWorklistItem(row) {
    if(row == null) {
        return true;
    }
  // initially we dont get 'isDateSpecific' value since the page is not loaded. so send the current date   
  // subsequently when you change the date, selected worklist will be loaded accordingly     
    var worklistDate = document.getElementById('worklistDate').value;
    /*var worklistDate = document.getElementById('worklistDate') != null && selectedWorklistRow == row ?
        document.getElementById('worklistDate').value : presentDate; 
*/
    if(dynTableGetNodeInRow(row, "worklistRID") == null) {
        // no worklist item to load
        return true;
    }
    
    var worklistRID = dynTableGetNodeInRow(row, "worklistRID").value;
    var isDateSpecific = dynTableGetNodeInRow(row, "isDateSpecific") != null;
    
    var url = dynTableGetNodeInRow(row, "worklistCommand").value;
    
    if(isDateSpecific) {
        url += "&worklistDate=" + worklistDate;
    }
    url += "&worklistRID=" + worklistRID;
  
    var wname = dynTableGetNodeInRow(row, "worklistName").value;
    xmlLoadElementValues(url, document.getElementById('desktopWell'));
  
    var isDateSpecific = document.getElementById('isDateSpecific') != null;
    var worklistDateSelection = document.getElementById('worklistDateSelection');

    setActivityBarTitle("Worklist: " + wname);

    selectedWorklistRow = row;
    savedWorklistRID = worklistRID;
    
    // Load all the JS files that this worklist item needs
    var jsFiles = document.getElementsByName('jsFile');

    for(var i = 0; i < jsFiles.length; i++) {
      var jsFileName = jsFiles[i].value;
      desktop.jsLoad(jsFileName);
    }

    // Load all the CSS files that this worklist item needs
    var cssFiles = document.getElementsByName('cssFile');

    for(var i = 0; i < cssFiles.length; i++) {
      var cssFileName = cssFiles[i].value;
      desktop.cssLoad(cssFileName);
    }

    var initCmds = document.getElementsByName('onWorklistLoad');

    for(var i = 0; i < initCmds.length; i++) {
      eval(initCmds[i].value);
    }

    return true;
}


function refreshWorklist() {
    var desktopWell = document.getElementById('desktopWell');
    if(desktopWell) {
        desktopWell.innerHTML = "<table width=100% ><tr><td></td></tr></table>";
    }
    
    // We need to refresh the selected row  
    if(refreshWorklistTable() && savedWorklistRID != 0) {
        var w = document.getElementsByName('worklistRID');
        
        for(var i = 0; i < w.length; i++) {
            if(w[i].value == savedWorklistRID)
                break;
        }
        
        if(i == w.length) {
            // Go to the first row
            selectFirstWorklistItem();
        } else {
            refreshWorklistItem(dynTableRow(w[i]));
        }
    }
    
};


function refreshWorklistTable() {
    var wListDiv = document.getElementById('worklistItemsDiv');
    if(wListDiv) {
        var url = PROJECT_CTXT_PATH + "/BOServlet?command=refreshWorklist";
        
        xmlLoadElementValues(url, wListDiv);
        
        setupWorklistTable();
    }
    return true;
};

function updateSelectedWorklistItemCount(count) {

  var row = selectedWorklistRow;

  if(count == 0) {
    dynTableGetNodeInRow(row, "itemCount").innerHTML = "";
    dynTableGetNodeInRow(row, "itemName").style.fontWeight = "";
  } else {
    dynTableGetNodeInRow(row, "itemCount").innerHTML = " (" + count + ")";
  }
}


function setupWorklistTable() {
  var tbl = document.getElementById('worklistTable');

  if(tbl != null) {
    worklistTable = new TableKeyboardHandler(tbl,
			                      {
			                        cbSelect       : loadWorklistItem
                                              });

//    selectFirstWorklistItem();
  
  }
  
}


function worklistFormInit() {
//  setupWorklistTable();
  currentDate = document.getElementsByName("currentDate");
}

function worklistHandleSuccess(successMsg) {

  var fn = document.getElementById('successHandler');

  if(fn != null) {
    var callStr = fn.value + "('" + successMsg + "');";

    eval(callStr);
  }
}

function worklistHandleFailure(errorMsg) {

  var fn = document.getElementById('failureHandler');

  if(fn != null)
    eval(fn.value + "('" + errorMsg + "');");
}

function addDaysToWorklistDate(days) {
    var date = null;
    if(days != null) {
        date = addDaysToGivenDate(document.getElementById('worklistDate').value, days);
    } else {
        date = document.getElementById('worklistDate').value;
    }
    document.getElementById('worklistDate').value = date;
    document.getElementById('worklistDateLink').innerHTML = date;
    boWorklist.refreshWorklist();
};


