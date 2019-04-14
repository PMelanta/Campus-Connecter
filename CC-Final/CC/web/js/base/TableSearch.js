function TableSearch() {

  var lastSelectedRow = null;

  var self = this;

  self.searchRow = function(tbl, col, searchStr) {

      if(searchStr.trim() == "") {
        alert("Please enter what to search");
        return;
      }

      var rows = tbl.getElementsByTagName('TR');

      for(var i = 0; i < rows.length; i++) {
        var cells = rows[i].getElementsByTagName('TD');

        if(cells.length < col) {
          /* This row doesn't have the required number of columns. Skip. */
          continue;
        }

	if(cells[col] == null)
          continue;

        var cellStr = cells[col].innerHTML; // @@ Maybe we should look for Text Nodes inside the cell. 

	// Remove leading and trailing whitespaces from the cell string
	cellStr = cellStr.replace(/^\s+|\s+$/g, '');
        
        try {
           if(lastSelectedRow != null) {
             // Restore row class
             lastSelectedRow.className = lastSelectedRow.getAttribute("savedClassName");
           }
        } catch (e) {};
          
        if(searchStr.trim().toUpperCase() == cellStr.substring(0, searchStr.length).toUpperCase()) {
          rows[i].setAttribute("savedClassName", rows[i].className);
          rows[i].className = "selectedRow";
          
          lastSelectedRow = rows[i];

          rows[i].scrollIntoView(false);

          return;
        }
      }

      alert("There are no rows that match the specified search phrase");
    }
  
}

var tableSearch = new TableSearch();