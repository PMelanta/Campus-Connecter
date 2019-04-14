UserProfile = function () {

  var self = this;

  this.addToFrequentlyUsedCommands = function (elem, featureRID) {
    var url = "UUserProfileServlet?command=addToFrequentlyUsedCommands&featureRID=" + featureRID;

    try {
      xmlGetResultString(url);
    } catch (e) {
      alert("Failed to add to frequently used commands");
      return false;
    }

    self.refresh();

    // Now change the click handler to do remove
    elem.title = "Click to remove from frequently used commands list";
    
    elem.onclick = function () {
          self.removeFromFrequentlyUsedCommands(elem, featureRID);
        }
  }

  this.removeFromFrequentlyUsedCommands = function (elem, featureRID) {
    var url = "UUserProfileServlet?command=removeFromFrequentlyUsedCommands&featureRID=" + featureRID;

    xmlGetResultString(url);

    try {
      xmlGetResultString(url);
    } catch (e) {
      alert("Failed to remove from frequently used commands");
      return false;
    }

    self.refresh();

    // Now change the click handler to do add
    elem.title = "Click to add to frequently used commands list";
    
    elem.onclick = function () {
          self.addToFrequentlyUsedCommands(elem, featureRID);
        }
  }

  this.refresh = function () {
    var url = "UUserProfileServlet?command=loadFrequentlyUsedCommands";

    xmlLoadElementValues(url, document.getElementById('frequentlyUsedCommandsDiv'));
  }

  this.init = function () {
  }
}

var userProfile = new UserProfile();