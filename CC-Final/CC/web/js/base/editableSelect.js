/************************************************************************************************************
	(C) www.dhtmlgoodies.com, September 2005
	
	This is a script from www.dhtmlgoodies.com. You will find this and a lot of other scripts at our website.	
	
	Terms of use:
	You are free to use this script as long as the copyright message is kept intact. However, you may not
	redistribute, sell or repost it without our permission.
	
	Thank you!
	
	www.dhtmlgoodies.com
	Alf Magne Kalleland
	
	************************************************************************************************************/	
	
// Path to arrow images
var arrowImage = './images/select_arrow.gif';	// Regular arrow
var arrowImageOver = './images/select_arrow_over.gif';	// Mouse over
var arrowImageDown = './images/select_arrow_down.gif';	// Mouse down
	
var selectBoxIds = 0;
var currentlyOpenedOptionBox = false;
var editableSelect_activeArrow = false;
	
function selectBox_switchImageUrl()
{
	if(this.src.indexOf(arrowImage)>=0){
		this.src = this.src.replace(arrowImage,arrowImageOver);	
	}else{
		this.src = this.src.replace(arrowImageOver,arrowImage);
	}
		
	
}
	
function selectBox_showOptions()
{
	if(editableSelect_activeArrow && editableSelect_activeArrow!=this){
		editableSelect_activeArrow.src = arrowImage;
		
	}
	editableSelect_activeArrow = this;
	var optionDiv = document.getElementById('selectBoxOptions' + this.id.replace(/[^\d]/g,''));

	// Add the options if not already loaded
	if(!optionDiv.loaded) {
	  addOptions(optionDiv, optionDiv.getOptionsFunc());
          optionDiv.loaded = true;
        }

	if(optionDiv.style.display=='block'){
	  optionDiv.style.display='none';
	} else {			
  	  optionDiv.style.display='block';
	  if(currentlyOpenedOptionBox && currentlyOpenedOptionBox!=optionDiv)
            currentlyOpenedOptionBox.style.display='none';	
	  currentlyOpenedOptionBox= optionDiv;			
	}
}
	
function selectOptionValue()
{
	var parentNode = this.parentNode.parentNode;
	var textInput = parentNode.getElementsByTagName('INPUT')[0];
	textInput.value = this.innerHTML;	
	this.parentNode.style.display='none';	
}

var activeOption;
function highlightSelectBoxOption()
{
	if(this.style.backgroundColor=='#316AC5'){
		this.style.backgroundColor='';
		this.style.color='';
	}else{
		this.style.backgroundColor='#316AC5';
		this.style.color='#FFF';			
	}	
		
	if(activeOption){
		activeOption.style.backgroundColor='';
		activeOption.style.color='';			
	}
	activeOption = this;
	
}
	
function addOptions(optionDiv, optionList) {

  var options = optionList.split(';');
  var optionsTotalHeight = 0;
  var optionArray = new Array();

  for(var no=0;no<options.length;no++) {
    var anOption = document.createElement('DIV');
    anOption.innerHTML = options[no];
    anOption.className='selectBoxAnOption';
    anOption.onclick = selectOptionValue;
    anOption.style.width = optionDiv.style.width.replace('px','') - 2 + 'px'; 
    anOption.onmouseover = highlightSelectBoxOption;
    optionDiv.appendChild(anOption);	
    optionsTotalHeight = optionsTotalHeight + anOption.offsetHeight;
    optionArray.push(anOption);
  }

  if(optionsTotalHeight > optionDiv.offsetHeight){				
    for(var no=0;no<optionArray.length;no++){
      optionArray[no].style.width = optionDiv.style.width.replace('px','') - 22 + 'px';
    }	
  }		

  optionDiv.style.visibility='visible';
}

function createEditableSelect(dest, getOptionsFunc)
{
	dest.className='selectBoxInput';		
	var div = document.createElement('DIV');
	div.style.styleFloat = 'left';
	div.style.width = dest.offsetWidth + 16 + 'px';
	div.style.position = 'relative';
	div.id = 'selectBox' + selectBoxIds;
	var parent = dest.parentNode;
	parent.insertBefore(div,dest);
	div.appendChild(dest);	
	div.className='selectBox';
	div.style.zIndex = 0 - selectBoxIds;

	var img = document.createElement('IMG');
	img.src = arrowImage;
	img.className = 'selectBoxArrow';
        img.style.position = 'relative';
        img.style.top = '3px';
        img.style.left = '-16px';
        img.onclick = selectBox_showOptions;
	img.id = 'arrowSelectBox' + selectBoxIds;

	div.appendChild(img);
		
	var optionDiv = document.createElement('DIV');

	optionDiv.getOptionsFunc = getOptionsFunc;
	optionDiv.loaded = false;

	optionDiv.id = 'selectBoxOptions' + selectBoxIds;
	optionDiv.className='selectBoxOptionContainer';
	optionDiv.style.width = div.offsetWidth-2 + 'px';
	div.appendChild(optionDiv);

	selectBoxIds = selectBoxIds + 1;
}	
