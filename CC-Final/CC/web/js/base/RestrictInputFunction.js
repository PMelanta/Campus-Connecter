var keybDecimal = new keybEdit('0123456789.');
var keybNegDecimal = new keybEdit('-0123456789.');
var keybNegPosDecimal = new keybEdit('-0123456789.+'); // With + symbol 
var keybDate = new keybEdit('0123456789/');
var keybDateChar = new keybEdit('0123456789/'); //Dunno where all its used.So adding a duplicate item Netto
var keybNumber = new keybEdit('0123456789');
var keybPhoneNumber = new keybEdit('-0123456789/');
var keybBatchNumber = new keybEdit('-0123456789abcdefghijklmnopqrstuvwxyz');
var keybCode = new keybEdit('-abcdefghijklmnopqrstuvwxyz');
var keybCodeNumber =  new keybEdit('0123456789abcdefghijklmnopqrstuvwxyz-');
var keybString =  new keybEdit(' 0123456789abcdefghijklmnopqrstuvwxyz+-\'\"(){}|/%,.:*<>?=_[]^&#@!');
var keybCurrency = new keybEdit('0123456789.,');
var keybTime = new keybEdit('0123456789:');
var keybName = new keybEdit(' abcdefghijklmnopqrstuvwxyz.\'');


function keybEdit(strValid, strMsg) 
{
    var reWork = new RegExp('[a-z]','gi');		//	Regular expression\
    //	Properties
    if(reWork.test(strValid))
            this.valid = strValid.toLowerCase() + strValid.toUpperCase();
    else
            this.valid = strValid;
    if((strMsg == null) || (typeof(strMsg) == 'undefined'))
            this.message = '';
    else
            this.message = strMsg;
    //	Methods
    this.getValid = keybEditGetValid;
    this.getMessage = keybEditGetMessage;
    function keybEditGetValid() {
            return this.valid.toString();
    }
    function keybEditGetMessage() {
            return this.message;
    }
}
function editKeyBoard(ev, objForm, objKeyb) 
{
    
    

    strWork = objKeyb.getValid();
    strMsg = '';							// Error message
    blnValidChar = false;					// Valid character flag
    var BACKSPACE = 8;
    var DELETE = 46;
    var TAB = 9;
    var LEFT = 37 ;
    var UP = 38 ;
    var RIGHT = 39 ;
    var DOWN = 40 ;
    var END = 35 ;
    var HOME = 35 ;
    
    // Checking backspace and delete  
    if(baGetKeyCode(ev) == BACKSPACE || baGetKeyCode(ev) == DELETE || baGetKeyCode(ev) == TAB 
        || baGetKeyCode(ev) == LEFT || baGetKeyCode(ev) == UP || baGetKeyCode(ev) == RIGHT || baGetKeyCode(ev) == DOWN)  {
            
        blnValidChar = true;
        
    }
    
    if(!blnValidChar) // Part 1: Validate input
            for(i=0;i < strWork.length;i++)
                    if(baGetASCIICode(ev) == strWork.charCodeAt(i) ) {
                            blnValidChar = true;
                            break;
                    }
                            // Part 2: Build error message
    if(!blnValidChar) 
    {
                //if(objKeyb.getMessage().toString().length != 0)
                    //		alert('Error: ' + objKeyb.getMessage());
            ev.returnValue = false;		// Clear invalid character
            
            if(!baIsIEBrowser()) {
            //ev.cancel = true;
                ev.preventDefault();
            }
            objForm.focus();						// Set focus
    }
}
