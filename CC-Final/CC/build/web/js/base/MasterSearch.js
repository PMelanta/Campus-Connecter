//Javascript functions for Master Search


function searchMasters(){
 //
  var searchStr = document.getElementById('searchTxt').value;
  var searchingFor = document.getElementById('searchingFor').value;
  var result_array ;
  var actionStr;
 
  if(searchingFor == 'ddItem'){        
          var ddict_item_name1 = document.getElementById('ddict_item_name').value ; 
          actionStr = projCtxtPath+"/SearchDdictItemsServlet?ddict_item_name=" + ddict_item_name1 + "&searchTxt=" + searchStr ;
  }else if(searchingFor == 'nestedDDItem'){
          var ddict_item_name1 = document.getElementById('ddict_item_name1').value ;  
          actionStr = projCtxtPath+"/SearchDdictItemsServlet?ddict_item_name=" + ddict_item_name1 + "&searchTxt=" + searchStr ;
  
    }else {
            alert('Search target not initialized.') ;
            return ;
        }
   result_array = window.showModalDialog(actionStr, 
			"toolbar=no,location=no,directories=no, status=no,menubar=no,scrollbars=no,resizable=1,copyhistory=no");
       if(result_array){
            populateFields(result_array) ;
        }
         else
            return 0 ;
}   

function populateFields(result_array){

        var searchingFor = document.getElementById('searchingFor').value ;
          
        if(searchingFor == 'ddItem')
            populateDDItemFields(result_array) ; 
        else if(searchingFor == 'nestedDDItem')
           populateNestedDDItemFields(result_array) ;      
        else{
          alert("target not initialised");
          return;
        }
}

function populateDDItemFields(result_array){
   
    document.getElementById('code').value = (result_array["dd_abbrv"] == "null"?"":result_array["dd_abbrv"]) ;
    document.getElementById('codeFromDB').value = result_array["dd_abbrv"] ;

    document.getElementById('descBox').value = result_array["dd_value"] ;
    document.getElementById('targetRid').value = result_array["dd_index"] ;
    
    if(document.getElementById("ddict_item_name").value == 176) {
        var parentCombo = document.getElementById("deptCombo");
        for(var i = 0 ; i < parentCombo.options.length ; i++) {
            if(parentCombo.options[i].value == result_array["dd_parent_index"]) {
                parentCombo.options[i].selected = true;
                document.getElementById("parentList").value = result_array["dd_parent_index"];
            }
        }
        parentCombo.disabled = true;
    }
    if(result_array["dd_valid"] == '1')
      document.getElementById('isActive').checked = true;
    else
      document.getElementById('isActive').checked = false;
      document.getElementById('saveBtn').value = "Modify" ;
}


function populateNestedDDItemFields(result_array){

	
	var parentsList  = null;
	var parentsArray = null;
   document.getElementById('code').value = (result_array["dd_abbrv"] == "null"?"":result_array["dd_abbrv"]) ;
   document.getElementById('codeFromDB').value = result_array["dd_abbrv"] ;

   document.getElementById('descBox').value = (result_array["dd_value"]== "null"?"":result_array["dd_value"]);
   document.getElementById('targetRid').value = (result_array["dd_index"]== "null"?"":result_array["dd_index"]);
   selectComboItem(document.getElementById('diaType'), parseInt(result_array["dd_parent_index"])) ;

  if(result_array["dd_valid"] == '1')
      document.getElementById('isActive').checked = true;
    else
     document.getElementById('isActive').checked = false;

    document.getElementById('saveBtn').value = "Modify" ;
}


function selectComboItem(targetCombo, targetItem){
	for(var i =0; i < targetCombo.options.length; i++){
		if(targetCombo.options[i].value == targetItem){
			targetCombo.options[i].selected = true ;
			return ;
		}
	}
}



