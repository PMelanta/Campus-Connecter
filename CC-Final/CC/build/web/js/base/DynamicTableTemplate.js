// New Dynamic Table functions
// Added by : Amitava Datta

function dynTableGetLastRow(tableId) {
  // Get the table body
  var tbl = document.getElementById(tableId);
  var tby = tbl.getElementsByTagName("TBODY");

  if(tby.length == 0)
    return null;

  tby = tby[tby.length - 1];

  // Get the last row of the table. We expect this to be the row to use as template
  // for new rows
  rows = tby.getElementsByTagName("TR");
  var lastRow = rows[rows.length - 1];

  return lastRow;
}

function dynTableDeleteRow(row) {

  row.parentNode.removeChild(row);
}

function dynTableInit(tableId) {

  var tbl = document.getElementById(tableId);

  lastRow = dynTableGetLastRow(tableId);

  // Now we are ready to make a copy of the last row
  rowTmpl = lastRow.cloneNode(true);

  // Save the row template
  //tbl.setAttribute("rowTemplate", rowTmpl);
  // Since set and get attribute function return only string in Firefox
  tbl.rowTemplate = rowTmpl;
}

function dynTableCloneRow(tableId, row) {

  var tbl = document.getElementById(tableId); 

  // Clone the row passed
  rowTmpl = row.cloneNode(true);

  // Save the row template
  //tbl.setAttribute("rowTemplate", rowTmpl);
  // Since set and get attribute function return only string in Firefox
  tbl.rowTemplate = rowTmpl;
}


/*********************************************************************************************************************/

function _AppendRow(table, row) { 
  
  var newRow = row.cloneNode(true);

  var tby = table.getElementsByTagName("TBODY");

  tby = tby[tby.length - 1];

  tby.appendChild(newRow);

  return newRow;
}

function dynTableAppendRow(tableId) {

  var tbl = document.getElementById(tableId);

  var tmpl = tbl.rowTemplate; //tbl.getAttribute("rowTemplate");

  return _AppendRow(tbl, tmpl); 
}

function dynTableAppendGivenRow(tableId, row) {
  var tbl = document.getElementById(tableId); 
  return _AppendRow(tbl, row); 
}

/**********************************************************************************************************************/
function dynTableInsertAfterRow(tableId, row) {

  var tbl = document.getElementById(tableId);

  var tmpl = tbl.rowTemplate; //tbl.getAttribute("rowTemplate");

  var newRow = tmpl.cloneNode(true);

  row.parentNode.insertBefore(newRow, baGetNextSibling(row));

  return newRow;
}

// @@ for what tableId is used here
function dynTableInsertGivenCloneAfterRow(tableId, row, cloneRow) {

  var tbl = document.getElementById(tableId); 
  var newRow = cloneRow.cloneNode(true);

  row.parentNode.insertBefore(newRow, baGetNextSibling(row));

  return newRow;
}

function dynTableRow(elem) {

  if(elem == null)
    return null;

  var tag = elem.tagName;

  if(tag == "TR" || tag == "tr")
    return elem;
  else

    return dynTableRow(elem.parentNode);
}

function dynTableGetAncestorByTagName(elem, tagName) {

  if(elem == null)
    return null;

  var tName = elem.tagName;

  if(tName.toUpperCase() == tagName.toUpperCase())
    return elem;
  else
    return dynTableGetAncestorByTagName(elem.parentNode, tagName);
}

function dynTableGetTable(elem) {
  return dynTableGetAncestorByTagName(elem, 'TABLE');
}

function dynTableIsLastRow(elem) {

  if(elem.tagName == "TR")

    row = elem;

  else

    row = dynTableRow(elem);

  return baGetNextSibling(row) == null;
}

function _findElementByName(root, elementName) {

     var matchingNode = null;

     //Check to see if root is the desired element. If so return root.
     var nodeName = root.name;
     var nodeID = root.id ;

     if(nodeName == elementName || nodeID == elementName)
       return root;

     //Check to see if root has any children if not return null
     if(!root.hasChildNodes())
       return null;

     //Root has children, so continue searching for them
     var childNodes = root.childNodes;

     for(var i = 0; i < childNodes.length; i++){
       if(matchingNode == null) {
         var child = childNodes[i];

         matchingNode = _findElementByName(child, elementName);
       } else {
         break;
       }
     }

     return matchingNode;
}

function dynTableGetNodeInRow(elem, name) {

  var row = dynTableRow(elem);

  if(row == null)
    return null;

  foundNode = _findElementByName(row, name);
	
	return foundNode ;
/*  if(foundNode != null)
    return foundNode;
  else
    alert("Could not find element : " + name);*/
}

function dynTableDeleteSelectedRows(selectionCheckboxName) {
  
  var ckArray = document.getElementsByName(selectionCheckboxName);
 
  for(var i = ckArray.length - 1 ; i >= 0; i--) {
    
    if(ckArray[i].checked) {
      var r = dynTableRow(ckArray[i]);
      if(r != null) {
        r.parentNode.removeChild(r) ;
      } else 
        alert("Cannot find row for element index " + (i+1));
    }
  }
}

function dynTableDeleteAllRows(tableId) {

  var tbl = document.getElementById(tableId);
  var tby = tbl.getElementsByTagName("TBODY");

  if(tby.length == 0)
    return null;

  tby = tby[tby.length - 1];

  rows = tby.getElementsByTagName("TR");

  while(rows.length != 0) {
    var lastRow = rows[rows.length - 1];

    lastRow.parentNode.removeChild(lastRow);

    rows = tby.getElementsByTagName("TR");
  }
}

/* ------------------------------------------------------------------------

  Dynamic row edit feature - Amitava

  ------------------------------------------------------------------------
*/

function applyAllCells(selRow, func) {

  var inputNodes = selRow.getElementsByTagName("INPUT");

  for(var i = 0; i < inputNodes.length; i++) {

    var nd = inputNodes[i];

    if(nd.type == "TEXT" || nd.type == "text" || nd.type == 'checkbox')
      func(nd);
  }

  var selectNodes = selRow.getElementsByTagName("SELECT");

  for(var i = 0; i < selectNodes.length; i++) {

    var nd = selectNodes[i];

    func(nd);
  }

  var textAreaNodes = selRow.getElementsByTagName("TEXTAREA");

  for(var i = 0; i < textAreaNodes.length; i++) {

    var nd = textAreaNodes[i];

    func(nd);
  }
}

function _disableRowEdit(nd) {
 
  if(nd.tagName == 'INPUT' || nd.tagName == 'TEXTAREA'){
  nd.readOnly = true;
  if(nd.type == 'checkbox') 
    nd.disabled = true;
  }
  else if(nd.tagName == 'SELECT')
    nd.disabled = true;

  nd.style.fontWeight = 'normal';
  nd.style.backgroundColor = nd.savedBackgroundColor;
  // nd.detachEvent('onkeypress', checkKey);
  removeEvent(nd, 'onkeypress', checkKey);
  nd.blur();
}

function disableEdit(elem) {

  var selRow = dynTableRow(elem);

  applyAllCells(selRow, _disableRowEdit);

  var tbl = selRow.parentNode;

  tbl.rowEdit = false;
  selRow.editActive = false;
}

function _cancelRowEdit(nd) {
  nd.value = nd.savedData;
}

function cancelRowEdit(elem) {
  var selRow = dynTableRow(elem)

  applyAllCells(selRow, _cancelRowEdit);

  disableEdit(elem);
}

function checkKey(e) {

  var key = e.keyCode;
  var elem = e.srcElement;

  var row = dynTableRow(elem);

  if(key == 27) {               // Handle ESC
    row.cancelFunc();
  } else if(key == 13) {         // Handle RET
    row.confirmFunc();
  }
}

function _enableRowEdit(nd) {
  if(nd.tagName == 'INPUT'){
    nd.readOnly = false;
    if(nd.type == 'checkbox') 
        nd.disabled = false;
  }  
  else if(nd.tagName == 'SELECT')
    nd.disabled = false;
  else if(nd.tagName == 'TEXTAREA')
  nd.readOnly = false;

//  nd.style.fontWeight = 'bolder';
  nd.savedBackgroundColor = nd.style.backgroundColor;
  if(nd.type != 'button' && nd.type != 'checkbox')
    nd.style.backgroundColor = 'yellow'; // '#FFFFFF';
  nd.savedData = nd.value;
  // nd.attachEvent('onkeypress', checkKey);
  addEvent(nd, 'onkeypress', checkKey); 
}

function dynTableEnableRowEdit(elem, confirmFunc, cancleHandler) {

  var selRow = dynTableRow(elem);

  if(selRow.editActive == true)
    return;

  var tbl = selRow.parentNode;

  if(tbl.rowEdit == true) {
    alert("Please complete the currently edited row before attempting to edit another row");
    return false;
  }

  applyAllCells(selRow, _enableRowEdit);

  var _confirmFunc = 
    function () {
      var row = selRow;

      var r = confirmFunc(row);
      if(r == true)
        disableEdit(this);
    };

  var _cancelFunc = 
    function () {
      cancelRowEdit(this);
      if (cancleHandler)
            cancleHandler(this);
    };

  selRow.confirmFunc = _confirmFunc;
  selRow.cancelFunc = _cancelFunc;

  tbl.rowEdit = true;
  selRow.editActive = true;

  // Set focus on the first INPUT text element
  var inputElems = selRow.getElementsByTagName('INPUT');
  
  for(var i = 0; i < inputElems.length; i++) {

    nd = inputElems[i];

    if(nd.type == "TEXT" || nd.type == "text") {
      nd.focus();
      break;
    }
  }
return true;
}

// Added by Neelakanth
function dynTableIsFirstRow(elem) {

  if(elem.tagName == "TR")
    row = elem;
  else
    row = dynTableRow(elem);

  return row.previousSibling == null;
}

function dynTableGetFirstRow(tableId) {
  // Get the table body
  var tbl = document.getElementById(tableId);
  var tby = tbl.getElementsByTagName("TBODY");

  if(tby.length == 0)
    return null;

  tby = tby[0];

  // Get the first row of the table. 
  rows = tby.getElementsByTagName("TR");
  var firstRow = rows[0];

  return firstRow;
}

/* ---------------------------------------------------------

   Old style dynamic table - Gopi

   ---------------------------------------------------------
*/

// JavaScript Document
//this structure is required to specify details associated with a column
function uColumn(name,id,type,size,maxLength,align,disabled,checked,className,fArray)
{
	//this function defines the structure required for each column
	this.name = name ;
	this.id = id ;
	this.type = type;
	this.size = size ;
	this.maxlength = maxLength ;
	this.align = align ;
	this.disabled = disabled ;
	this.checked = checked ;
        this.className=className ;
  	this.funcArray = new Array();
	this.funcArray = fArray ;
}

//this structure is needed to store the metadata abt a row
function rowMetaData(colArray){
	this.cols = new Array () ;
	this.cols = colArray ;
}

//these are the JS functions that manipulate the table
function getTableHandle(tableId){
	return document.getElementById(tableId) ;
}

function getSelectedRows(tableId){
	var vicTable = getTableHandle(tableId) ;
	var selRowsArray = new Array();
	var vCount = 0 ;
	for(var i=0;i<vicTable.rows.length;i++){
		if(vicTable.rows[i].mArray.cols[0].checked){
			selRowsArray[vCount] = vicTable.rows[i] ;
			vCount++ ;
		}
	}
	return selRowsArray ;
}

function insRow(tableId,colsArray,valuesArray,index)
{
	var dTable = getTableHandle(tableId) ;
	var rowNum = dTable.rows.length;
	var newRow = dTable.insertRow(-1) ;
        newRow.bgcolor="#FFFFFF";
	var metaDataArray = new Array() ;
	var i;
	for(i=0;i<colsArray.length;i++){
                if(colsArray[i].type=='select'){
                    if(colsArray[i].funcArray){
                            if(colsArray[i].funcArray["outerHTML"])
                                     newRow.insertCell(i).innerHTML = colsArray[i].funcArray["outerHTML"] ;
                          var newCol = newRow.cells[i].childNodes[0];
                          setComboSelection(newCol,valuesArray[i]);
                    }
                } else {
                
                    var newCol = document.createElement('input') ;
                    newCol.type = colsArray[i].type;
                    newCol.checked = colsArray[i].checked ;
                    newCol.value = valuesArray[i] ;
                    newCol.size = colsArray[i].size ;
                    newCol.maxLength = colsArray[i].maxlength ;
                    newRow.insertCell(i).appendChild(newCol) ;

                }
                    newCol.name = colsArray[i].name ;
                    newCol.id = rowNum + colsArray[i].id ;
                  
                    newCol.align = colsArray[i].align ;
                    if(colsArray[i].disabled){
                    newCol.disabled = colsArray[i].disabled ;
                    } else {
                        if( newCol.type != "hidden" )
                            newCol.tabindex = index;
                    }
                    if(colsArray[i].className){
                        newCol.className=colsArray[i].className;
                    }
                    if(colsArray[i].funcArray){
                            if(colsArray[i].funcArray["onblur"])
                                    newCol.onblur = colsArray[i].funcArray["onblur"] ;
                            if(colsArray[i].funcArray["onfocus"])
                                    newCol.onfocus = colsArray[i].funcArray["onfocus"] ;
                            if(colsArray[i].funcArray["onclick"])
                                    newCol.onclick = colsArray[i].funcArray["onclick"] ;
                            if(colsArray[i].funcArray["onchange"])
                                    newCol.onchange = colsArray[i].funcArray["onchange"] ;
                    }
                    
                
		metaDataArray[i] = newCol ;
        
	}
	newRow.mArray = new rowMetaData(metaDataArray) ;
}

function getSelectedRows(tableId){
	var vicTable = getTableHandle(tableId) ;
	var selRowsArray = new Array();
	var vCount = 0 ;
	for(var i=0;i<vicTable.rows.length;i++){
		if(vicTable.rows[i].mArray.cols[0].checked){
			selRowsArray[vCount] = vicTable.rows[i] ;
			vCount++ ;
		}
	}
	return selRowsArray ;
}

function delRow(victimRow){
	var selectedRow = victimRow? victimRow : this ;
	selectedRow.parentNode.removeChild(selectedRow) ;
}

function delSelectedRows(tableId){
	var victimTable = getTableHandle(tableId) ;
	var toBeDeleted = new Array() ;
	toBeDeleted = getSelectedRows(tableId)
	if(toBeDeleted.length > 0)
		for(var i=0;i<toBeDeleted.length;i++)
			delRow(toBeDeleted[i]) ;
}

function clearTable(tableId) {
	var vicTable = getTableHandle(tableId) ;
	var vicLength = vicTable.rows.length ;
	for(var i=0;i<vicLength;i++){
		delRow(vicTable.rows[0]) ;
	}
}
function setComboSelection(combo,selectId){
    var i = 0;
    if(selectId!=null){
        for(i=0;i< combo.options.length;i++){           
            if( combo.options[i].value==selectId ){
                combo.options[i].selected=true;
                return ;
            }
        }
    }
}
