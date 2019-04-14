var DDSync = function() {
    var self = this;
    
    self.syncDDItems = function(lastPortedDatetime) {
        
        var rs = uOfflineManager.executeQuery("select ddi_type_index  " +
        " from u_ddict_item_type ");
        while (rs.isValidRow()) {
            var url = PROJECT_CTXT_PATH + '/UOfflineServlet?command=getDataDictionaryItems' ;
            url = url + '&ddTypeIndex='+ rs.fieldByName("ddi_type_index") ;
            if (lastPortedDatetime){
                url = url + '&fromDate='+ lastPortedDatetime ;
            }
            var resultString = xmlGetResultString(url);
            
            var queryArray = resultString.split('^');        
            var portedDate = "";
            
            if(queryArray.length > 1) {
                // FIRST ITEM IN queryArray IS THE LIST OF MODIFIED/CREATED DD INDEXES
                // Each remaining item in queryArray represents a modified row of dd index 
                var ddIndexList = queryArray[0] 
                if(ddIndexList != ""){                    
                    sql = "Delete from u_ddict Where dd_index in (" + ddIndexList + ")";
                    uOfflineManager.executeQuery(sql);
                } 
                for(var i = 1; i < queryArray.length - 1; i++) {
                    var values = queryArray[i].split('~');
                    var sql = "";
                    portedDate = values[5];                    
                    
                    sql = "insert into u_ddict (dd_index, dd_ddi_type_index, dd_value, dd_parent_index, dd_valid ) " +
                    " values ( " + values[0] + ", " + values[1] + ", '" + values[2] + "', " + values[3] + ", " + values[4] +")";
                    
                    try {
                        
                        uOfflineManager.executeQuery(sql);
                        
                    } catch (e) {
                        break;
                    }
                }
            }
            rs.next();
        }
        
        
        if(portedDate != "" ) {
            uSyncEngine.setPortedDate('ddSync.syncDDItems',portedDate);
        }    
        uSyncEngine.setAsReady('ddSync.syncDDItems');     
    }
}

var ddSync = new DDSync();    