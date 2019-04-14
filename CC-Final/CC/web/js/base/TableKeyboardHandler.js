/*
 PURPOSE :
 The object defined in the file enables you to quickly add navigation of rows in tables using the keyboard
 UP, DOWN, LEFT, RIGHT, SPACE keys. It supports handling of DEL, ESC, and RET keys. All other keys are ignored. 
 In addition, single and double click can be used to highlight a row and to select.

 See TableKeyboardHandler.txt for documentation.
*/ 

// Levels:
//   1 -> Function call level
//   2 -> Statement level

var tkhDebugLevel = 0;

function _tkhTrace(debugLevel, traceMsg) {
  if(tkhDebugLevel >= debugLevel)
    alert(traceMsg);
}

function TableKeyboardHandler(tbl, params) {

	function param_default(pname, def) { if (typeof params[pname] == "undefined") { params[pname] = def; } };

	param_default("headerRows",   0);
	param_default("cbHighlight",  null);
	param_default("cbSelect",  null);
	param_default("cbEscape",  null);
	param_default("cbDelete",  null);
	param_default("cbSpace",  null);
	param_default("cbShowDetail",  null);
	param_default("cbHideDetail",  null);
	param_default("cbDefaultKeyPress",  null);

        var HIGHLIGHTED_ROW_CLASS = "highlightedRow";
        var SELECTED_ROW_CLASS = "selectedRow";

	var headerRows = params.headerRows;

	// The 'me' variable allow you to access the TableKeyboardHandler object
	// from the tbl's event handlers defined below.
	var me = this;

	//A reference to the element we're binding the list to.
	this.tbl = tbl;

        this.cbHighlight = params.cbHighlight;
	this.cbSelect = params.cbSelect;
	this.cbEscape = params.cbEscape;
	this.cbDelete = params.cbDelete;
	this.cbSpace = params.cbSpace;
	this.cbShowDetail = params.cbShowDetail;
	this.cbHideDetail = params.cbHideDetail;
	this.cbDefaultKeyPress = params.cbDefaultKeyPress;
        if(tbl.attributes['savedClassName'] == null)
            tbl.setAttribute('savedClassName', '');
        if(tbl.attributes['selectedRowIndex'] == null)
            tbl.setAttribute('selectedRowIndex', '');

	//Do you want to remember what keycode means what? Me neither.
        var BSPC = 8;
	var TAB = 9;
	var ESC = 27;
	var KEYLEFT = 37;
	var KEYUP = 38;
	var KEYRIGHT = 39;
	var KEYDN = 40;
	var RET = 13;
        var DEL = 46;
	var SPC = 32;

 	function _doNavigation(sel, key) {

	  if(sel == null)
            return;

	  var newSelection = null;

          if(key == KEYUP) {
            newSelection = baGetPreviousSibling(sel);
          } else if(key == KEYDN) {
            newSelection = baGetNextSibling(sel);
          }

	  if(newSelection == null)
            return sel;

	  if(me.cbHighlight != null) {
            if(me.cbHighlight(tbl.currHighlight, newSelection))
	      return newSelection;
	    else
              // Try navigating to the row before/after the new selection
	      return _doNavigation(newSelection, key);
	  } else 
	    return newSelection;
        }

	function _handleNavigation(ev, sel, key) {
          var newSelection = _doNavigation(sel, key);
   
          _highlightRow(newSelection);

          // @@ The following should work but shows up as a problem in Patient Search Screen. Revisit!
          // @@ Commenting out for the time being
          newSelection.firstChild.scrollIntoView(false);

           // Cancel bubbling so that the event is not taken by any scrollbar
	   baCancelEvent(ev);
        };

	function _placeFocus(row) {
          if(row != null && row.firstChild != null) {
 	    _tkhTrace(1, "_placeFocus: setting focus on first child of row");
  	    //row.firstChild.focus();
            row.focus();
          } else 
 	    _tkhTrace(1, "_placeFocus: Failed. Couldn't find first child.");
	};

	function _highlightRow(sel) {

	  _tkhTrace(2, "_highlightRow: stage 1, selection = " + sel +
                    ", sel.firstChild = " + (sel == null ? null : sel.firstChild));

          if(sel == null || sel.firstChild == null) // @@ Why are we checking for sel.firstChild??
            return;

	  _tkhTrace(2, "_highlightRow: stage 2");

          var tbl = me.tbl;
          var currSelection = tbl.currSelection;
          var currHighlight = tbl.currHighlight;

          if(currHighlight == sel) {
            // Already selected. Nothing to do
            return;
          }

	  _tkhTrace(2, "_highlightRow: stage 3");

          if(me.cbHighlight != null) {
            if(!me.cbHighlight(currHighlight, sel))
              return;
          }

      	  _tkhTrace(2, "_highlightRow: stage 4");

          if(currHighlight != null && currHighlight != currSelection)
            currHighlight.className = tbl.attributes['savedClassName'].value;

          tbl.currHighlight = sel;

          // Don't change highlight colour if SEL is the current selection
          if(sel != currSelection) {
            tbl.attributes['savedClassName'].value = sel.className;
            sel.className = HIGHLIGHTED_ROW_CLASS;
          }

	  _placeFocus(sel);
          // remember the selected row is some attribute
          tbl.attributes['selectedRowIndex'].value = sel.rowIndex;
          
	  _tkhTrace(2, "_highlightRow: stage 5");
	};

	function _highlightSelection(sel) {

	  _tkhTrace(2, "_highlightSelection: stage 1, selection = " + sel +
                    ", sel.firstChild = " + (sel == null ? null : sel.firstChild));

          if(sel == null || sel.firstChild == null) // @@ Why are we checking for sel.firstChild??
            return;

	  _tkhTrace(2, "_highlightSelection: stage 2");

          var tbl = me.tbl;
          var currSelection = tbl.currSelection;

          if(currSelection == sel) {
            // Already selected. Nothing to do
            return;
          }

/*
	  _tkhTrace(2, "_highlightSelection: stage 3");

          if(me.cbHighlight != null) {
            if(!me.cbHighlight(currSelection, sel))
              return;
          }
*/
      	  _tkhTrace(2, "_highlightSelection: stage 4");

          if(currSelection != null)
            currSelection.className = tbl.attributes['savedClassName'].value;

          tbl.currSelection = sel;
   
          // Don't write over the original saved class name if the row is 
          // highlighted already
          if(sel.className != HIGHLIGHTED_ROW_CLASS)
            tbl.attributes['savedClassName'].value = sel.className;

          sel.className = SELECTED_ROW_CLASS;

         //This call creates problem when the focus has to be given to an inner table
         //Commented by Akhil
	 // _placeFocus(sel);

	  _tkhTrace(2, "_highlightSelection: stage 5");
	};

	tbl.onclick = function(ev) {

	  var n = baGetEventSource(ev);

          if(n != null) {
            var row = dynTableRow(n);

            row.propagateEvent = true;

	    _highlightRow(row);
          }

	  if(!row.propagateEvent)
            baCancelEvent();

          row.propagateEvent = true;
        };

        tbl.selectRow = function(row) {

          if(me.cbSelect != null) {
	    if(me.cbSelect(row))
              _highlightSelection(row);
          }

        };

	/********************************************************
	onkeydown event handler for the input tbl.

	ESC key = Call the cbEscape
	Up/down arrows = Move the highlight up and down in the table and call the cbHighlight
	RET key = Call the cbSelect
	DEL key = Call the cbDelete
	********************************************************/
	tbl.onkeydown = function(ev) {
          var key = baGetKeyCode(ev);

          var t = me.tbl;

          var currSelection = t.currSelection;
          var currHighlight = t.currHighlight;

	  switch(key) {

	    case RET:
              if(currHighlight == null)
                return;

	      if(me.cbSelect != null) {
	        if(me.cbSelect(currHighlight))
                  _highlightSelection(currHighlight);
                /* try {
                     currHighlight.focus();
                 } catch(e) {
                 } */ 
	      }

	      break;

	    case ESC:
	      if(me.cbEscape != null) {
                  me.cbEscape(currSelection);

	        // It is possible that the ESC handler may delete the current 
	        // selection. So, we will put this in a try catch block
	        try {
                  currSelection.focus();
                } catch (e) {}
              }

	      break;

	    case DEL:
            case BSPC:
              if(currHighlight == null)
                return;

	      if(me.cbDelete != null) {
                me.cbDelete(currHighlight);

	        baCancelEvent(ev);
              }

	      break;

	    case SPC:
              if(currHighlight == null)
                return;

	      if(me.cbSpace != null) {
                me.cbSpace(currHighlight);

	        baCancelEvent(ev);
              } /* Should this be the default or should this be
                   specifiable by the caller?? Disable for now. -- Amitava 
		else {
	        // Treat this as a KEYDN
	        _handleNavigation(ev, currSelection, KEYDN);
	      } */
	      break;

	    case KEYUP:
	    case KEYDN:
               if(currHighlight == null)
                 return;

	        _handleNavigation(ev, currHighlight, key);
	        break;

	    case KEYRIGHT:
               if(currHighlight == null)
                 return;

  	        if(me.cbShowDetail != null) {
	          me.cbShowDetail(currHighlight);
                  /*try {
                    currSelection.focus();
                  } catch(e) {}  */
	        }
                break;

	    case KEYLEFT:
               if(currHighlight == null)
                 return;

  	        if(me.cbHideDetail != null) {
	          me.cbHideDetail(currHighlight);
                  /* try {
                    currSelection.focus();
                  } catch(e) {}  */
	        }
                break;

	    default:
               if(currHighlight == null)
                 return;

  	        if(me.cbDefaultKeyPress != null) {
	          me.cbDefaultKeyPress(currHighlight, key);
	        }
                break;
	  }
	};

	tbl.ondblclick = function(ev) {

      	  if(me.cbSelect != null) {
	    var n = baGetEventSource(ev);

            if(n != null) {
              var row = dynTableRow(n);

	      if(me.cbSelect(row)) {
                _highlightSelection(row);
              }
	    }
          }

	  baCancelEvent();
	};

	// Select the first row by default (one after the header)
	var rows = tbl.rows;

	// First child of tbl is a TBODY. Its first child is the first row
        var firstRow = baGetTblFirstRow(tbl);

        for(var i = 0; i < headerRows; i++) {
	  firstRow = baGetNextSibling(firstRow);                      
        }

	// We may not have any rows. So, handle exception
        try {
            
            if(tbl.attributes['selectedRowIndex'].value == '') {
                if(baIsIEBrowser()) {
                    firstRow.click();
                } else {
                    _highlightRow(firstRow);
                }
            } else {
                // if there is already selected row defined for that table
                tbl.rows[tbl.attributes['selectedRowIndex'].value].className = tbl.attributes['savedClassName'].value;
                if(baIsIEBrowser()) {
                    tbl.rows[tbl.attributes['selectedRowIndex'].value].click();
                } else {
                    tbl.rows[tbl.attributes['selectedRowIndex'].value].onclick();
                }   
            }
        } catch (e) {}

	// The following would make sense but in some cases, this makes the table scroll up
	// If required to get default focus on the table, it must be called outside by the caller
	// right after the creation of this object.
	//tbl.focus();
}