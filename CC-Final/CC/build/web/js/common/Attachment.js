var Attachment = function() {
    var self = this;

 self.handleAttachmentDivDisplay = function(){

        var divElem = document.getElementById("attachmentDetailsDiv");

        if(divElem.className == 'hidden'){
            divElem.className = 'visible';
            document.getElementById("expandComponents").innerHTML= "&#9660;";
        }else{
            divElem.className = 'hidden';

            document.getElementById("expandComponents").innerHTML= "&#9658;";
        }
 }
 
     self.setAttachmentPath = function(elem) {
        var row = dynTableRow(elem);
        dynTableGetNodeInRow(row, "filePath").value = elem.value;
        dynTableGetNodeInRow(row, "attachmentFileName").innerHTML = elem.value;
    };
 
 self.addAttachment = function(elem) {
         var curRow = dynTableRow(elem);
        if(!validateAttachmentRow(curRow))
            return ;   
        
        var tmplRow = document.getElementById('attachmentCloneRow').cloneNode(true);
        clearAttachmentRow(tmplRow);
        curRow.id = "";
        dynTableGetNodeInRow(curRow, "attachmentAdd").className = "deleteSmallButton whiteBG hidden";
        dynTableGetNodeInRow(curRow, "attachmentDelete").className = "deleteSmallButton whiteBG visible";
        dynTableGetNodeInRow(curRow, "file").className = "hidden";
        dynTableGetNodeInRow(curRow, "attachmentFileName").className = "visible";
        
        curRow.parentNode.insertBefore(tmplRow, curRow);
        
        dynTableGetNodeInRow(tmplRow, 'attachmentDesc').focus();
      };
      
      function validateAttachmentRow(row) {
        var all_is_fine = true ;
        var setFocusObj = null;
        dynTableGetNodeInRow(row,"rowErrorMsg").innerHTML = '';
        dynTableGetNodeInRow(row,"rowErrorMsg").title = ""; 
        if(dynTableGetNodeInRow(row,"fileDescription").value == "" ) {        
            dynTableGetNodeInRow(row,"rowErrorMsg").innerHTML = '!  ';
            dynTableGetNodeInRow(row,"rowErrorMsg").title += "Enter description\n";       
            if(setFocusObj == null) setFocusObj = dynTableGetNodeInRow(row, "attachmentDesc");
            all_is_fine = false ;                
        }
        if(dynTableGetNodeInRow(row,"filePath").value == "") {
            dynTableGetNodeInRow(row,"rowErrorMsg").innerHTML = '!  ';
            dynTableGetNodeInRow(row,"rowErrorMsg").title += "Select attachment\n";       
            if(setFocusObj == null) setFocusObj = dynTableGetNodeInRow(row, "file");
            all_is_fine = false;
        }
        if(all_is_fine == false)
            return false;
        else
            return true;                    
    }
    
    function clearAttachmentRow(row) {
        dynTableGetNodeInRow(row, "fileDescription").value = "";
        dynTableGetNodeInRow(row, "filePath").value = "";
        dynTableGetNodeInRow(row, "file").value = "";   
    }
    
    self.deleteAddedAttachment = function(elem) {
        var row = dynTableRow(elem);
        var ans = confirm("Details will be lost.Do you want to continue?");
        if(ans) {
            dynTableDeleteRow(row); 
        }
    };
      
}
var attachment = new Attachment();