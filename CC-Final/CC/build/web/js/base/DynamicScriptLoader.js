
var SEQ_loadScript = {

	scriptList:[],
	timeInterval:500,
	scriptCount:0,
	maxLoadCount:20,
	loadCount:0,
	loadDone:false,
	timeID:-1,
	debug: false,
	
	addScript: function (url, timeInt, sIdPrefix, sID) {
		if (timeInt == undefined || timeInt == null) timeInt = SEQ_loadScript.timeInterval;
		var scriptID = "";
		SEQ_loadScript.scriptCount++;
		if (sID == undefined || sID == null) {
			scriptID = sIdPrefix + scriptCount;
		} else
		{
			scriptID = sIdPrefix + sID;
		}
		
		SEQ_loadScript.scriptList.push( {"url":url, "sid":scriptID,  "tint":timeInt  });
	},
	
	begin: function(){
	
		SEQ_loadScript.loadDone = false;
		SEQ_loadScript.loadCount = 0;
		
		timeID = -1;
		SEQ_loadScript.loadingData();
	},
	
	clear: function(){
		// clear out the array
		SEQ_loadScript.scriptList = [];
		SEQ_loadScript.scriptCount = 0;
	},
	
	loadingData: function(){
	
		if (SEQ_loadScript.scriptList.length <= 0) return;
		var tempObj = SEQ_loadScript.scriptList.shift();
		var url = tempObj.url;
		var sid = tempObj.sid;
		var tint = tempObj.tint;
	
		SEQ_loadScript.loadDone = false;
		SEQ_loadScript.loadCount = 0;
		SEQ_loadScript.loadJSLibrary(url, null, sid);
	
		setTimeout(function(){
						SEQ_loadScript.timeID = setInterval("SEQ_loadScript.checkLoad('"+sid+"')", tint);
		
					}, 50);
	},
	
	checkLoad: function (param) {
		if (SEQ_loadScript.loadDone)
		{
			// now load new data
			clearInterval(SEQ_loadScript.timeID);

			SEQ_loadScript.callbackFunc(param);
			SEQ_loadScript.loadingData();
		} else
		{
			SEQ_loadScript.loadCount++;
			if (SEQ_loadScript.loadCount > SEQ_loadScript.maxLoadCount)
			{
				clearInterval(SEQ_loadScript.timeID);
				SEQ_loadScript.callbackFunc(param, "FAIL TO LOAD");
				// load next one.
				SEQ_loadScript.loadingData();
			}	
			// else continue
		}
	},
	
	setLoadDone: function(){
		SEQ_loadScript.loadDone = true;
	},
	
	loadJSLibrary: function (aUrl, aTag, aSId)
	{	
		var headObj = {};
		if (aTag == null || aTag == "")
		{
			headObj = document.getElementsByTagName("head")[0];
		} else
		{
			headObj = document.getElementById(aTag);
		}
	
		var node = document.createElement("script");
		if (node.addEventListener)
			node.addEventListener("load", function() { SEQ_loadScript.setLoadDone(); }, false);
		else
			node.onreadystatechange = function() {
			if (this.readyState == "complete" || this.readyState == "loaded") SEQ_loadScript.setLoadDone();
		}
		node.src = aUrl;
		node.id = aSId;
		headObj.appendChild(node);
	
	},
	
	callbackFunc:function(sID){
		if (debug)
		{
			alert("script ID: " + sID + " Src: " + document.getElementById(sID).src);
		}
	}
};