/* ******************************************************************

  WARNING: DO NOT USE THIS FILE!

  The following functions have been moved to UbqValidations.js.

  THIS FILE WILL BE DELETED SHORTLY.
  
  *******************************************************************
*/

function isEmpty(str) {
  if(str == null || str == "")
    return true;

  return false;
}

function isPositiveInteger(str) {
  var pattern = "0123456789"
  var i = 0;
  do {
    var pos = 0;
    for (var j=0; j<pattern.length; j++)
      if (str.charAt(i)==pattern.charAt(j)) {
        pos = 1;
        break;
      }
    i++;
  } while (pos==1 && i<str.length)  
  if (pos==0) 
    return false;

  return true;
} 

