GroupLookupISearchHelper = function (className) {
    
    var searchHelperClassName = className;

    // I have to make a hashTable which contains the groupwise items
    var groupHashTable = new Hashtable();    
    
    this.getGroupISearchHelper = function (groupID, groupName, url) {
        
        var searchHelper = groupHashTable.get(groupID);
        
        if(searchHelper != null) {
            // Search Helper exists just return that and keep ur mouth shut.            
            return searchHelper;
        }
        
        // Control will come here only if it doesn't find the object. So u create the obj, set to the hash and then         
        searchHelper = eval("new " + searchHelperClassName + "(groupName + groupID, url)");
        
        // Now we got the new searchHelper. Add this to the hashMap.
        groupHashTable.put(groupID, searchHelper);

        // Now return the search Helper         
        return searchHelper;                                                
    }
}           
