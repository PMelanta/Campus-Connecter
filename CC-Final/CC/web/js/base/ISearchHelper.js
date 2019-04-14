ISearchHelper = function (prefix, theURL) {

  var self = this;

  this.keyPrefix = prefix; 
  this.url = theURL;

  var keywordHashTable = null; // Will be created separately, as needed

  this.getKeywordDescriptor = function (keyword) {
    return keywordHashTable.get(keyword);
  }

  this.getItems = function (searchStr) {

    var key = self.keyPrefix + '_' + searchStr;

    var keyProcessed = key + "_P";

    var sht = desktop.getFromCache(keyProcessed);

    if(sht != null) {
      keywordHashTable = sht;

      return sht.keys();
    }

    // Didn't find processed hash list
    var rval = desktop.getFromCache(key);

    if(rval == null) {
      // Hash table not loaded. Get the list from desktop cache
      var sURL = self.url;

      if(searchStr != null) {
        sURL += '&searchStr=' + searchStr;
      }

      var rval = xmlGetResultString(sURL);
      if(!rval)
        return;
      desktop.putIntoCache(key, rval); 
    }

    // @@ What if the '^' character is part of a service name!!!! FIX IT!
    var iArray = rval.split('^');

    keywordHashTable = new Hashtable();

    for(i = 0; i < iArray.length; i++) {
        
      var itemDetails = iArray[i].split('~');
        
      if(itemDetails[0] != "") {
          try {
              keywordHashTable.put(itemDetails[0], iArray[i]);
          } catch (e) {
              alert("Error while loading " + iArray[i]);
          }
      }
    }
    
    desktop.putIntoCache(keyProcessed, keywordHashTable);

    return keywordHashTable.keys();
  }


}
