/*******************************************************

AutoSuggest - a javascript automatic text input completion component
Copyright (C) 2005 Joe Kepley, The Sling & Rock Design Group, Inc.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

*******************************************************

Please send any useful modifications or improvements via 
email to joekepley at yahoo (dot) com

*******************************************************/

/********************************************************
 The AutoSuggest class binds to a text input field
 and creates an automatic suggestion dropdown in the style
 of the "IntelliSense" and "AutoComplete" features of some
 desktop apps. 
 Parameters: 
 elem: A DOM element for an INPUT TYPE="text" form field
 suggestions: an array of strings to be used as suggestions
              when someone's typing.

 Example usage: 
 
 Please enter the name of a fruit.
 <input type="text" id="fruit" name="fruit" />
 <script language="Javascript">
 var fruits=new Array("apple","orange","grape","kiwi","cumquat","banana");
 new AutoSuggest(document.getElementById("fruit",fruits));
 </script>

 Requirements: 

 Unfortunately the AutoSuggest class doesn't seem to work 
 well with dynamically-created DIVs. So, somewhere in your 
 HTML, you'll need to add this: 
 <div id="autoSearch"><ul></ul></div>

 Here's a default set of style rules that you'll also want to 
 add to your CSS: 

 .suggestion_list
 {
 background: white;
 border: 1px solid;
 padding: 4px;
 }

 .suggestion_list ul
 {
 padding: 0;
 margin: 0;
 list-style-type: none;
 }

 .suggestion_list a
 {
 text-decoration: none;
 color: navy;
 }

 .suggestion_list .selected
 {
 background: navy;
 color: white;
 }

 .suggestion_list .selected a
 {
 color: white;
 }

 #autoSearch
 {
 display: none;
 }
*********************************************************/
function AutoSearch(elem, searchCallbackFunc, selectFunc)
{

	//The 'me' variable allow you to access the AutoSearch object
	//from the elem's event handlers defined below.
	var me = this;

	//A reference to the element we're binding the list to.
	this.elem = elem;

	this.searchCallback = searchCallbackFunc;

	this.selectFunc = selectFunc;

	//Arrow to store a subset of eligible suggestions that match the user's input
	this.eligible = new Array();

	//The text input by the user.
	this.inputText = null;

	//A pointer to the index of the highlighted eligible item. -1 means nothing highlighted.
	this.highlighted = -1;

	//A div to use to create the dropdown.
	this.div = document.getElementById("autoSearch");


	//Do you want to remember what keycode means what? Me neither.
	var TAB = 9;
	var ESC = 27;
	var KEYUP = 38;
	var KEYDN = 40;
	var RET = 13;

	//The browsers' own autocomplete feature can be problematic, since it will 
	//be making suggestions from the users' past input.
	//Setting this attribute should turn it off.
	elem.setAttribute("autocomplete","off");

	//We need to be able to reference the elem by id. If it doesn't have an id, set one.
	if(!elem.id)
	{
		var id = "autoSearch" + idCounter;
		idCounter++;

		elem.id = id;
	}


	/********************************************************
	onkeydown event handler for the input elem.
	Tab key = use the highlighted suggestion, if there is one.
	Esc key = get rid of the autoSearch dropdown
	Up/down arrows = Move the highlight up and down in the suggestions.
	********************************************************/
	elem.onkeydown = function(ev)
	{
		var key = me.getKeyCode(ev);

		switch(key)
		{
			case TAB:
			me.useSuggestion(this);
                        break;

			case RET:
                        if(me.displayingSuggestions) {
  			  me.useSuggestion(this);
			  me.cancelEvent(ev);
                        }
			break;

			case ESC:

			// Check if we are currently showing the div. If not, do nothing. 
			// This is important, because we don't want to cancel
			// the handling of key when the div is not being shown.
			if(me.div.style.display == 'block') {
	 		    me.hideDiv();
			    me.cancelEvent(ev);
                        }

			break;

			case KEYUP:
			if (me.highlighted > 1)
			{
  				  me.highlighted--;
			}
			me.changeHighlight(key);
			break;

			case KEYDN:
			if (me.highlighted < (me.eligible.length - 1))
			{
				me.highlighted++;

                                if(me.highlighted == 0) 
                                  // Skip the header
                                  me.highlighted++;
			}
			me.changeHighlight(key);
			break;
		}
	};

        this.doAutoSearch = function(str) {
		if (str != me.inputText)
		{
			me.inputText = str;
			me.getEligible();

			if(this.eligible != null) {
  			  me.createDiv();
			  me.positionDiv();
			  me.showDiv();
                        }
		} else
		{
			me.hideDiv();
		}
        };

	/********************************************************
	onkeyup handler for the elem
	If the text is of sufficient length, and has been changed, 
	then display a list of eligible suggestions.
	********************************************************/
	elem.onkeyup = function(ev) 
	{
		var key = me.getKeyCode(ev);
		switch(key)
		{
		//The control keys were already handled by onkeydown, so do nothing.
		case TAB:
		case ESC:
		case KEYUP:
		case KEYDN:
		case RET:
			return;
		default:
			var str = this.value;
			var f = function () { me.doAutoSearch(str); };

                        if(me.timerActive)
                          clearTimeout(me.timer);

			me.timer = setTimeout(f, 1500);
                        me.timerActive = true;
		};
		  
	};


	/********************************************************
	Insert the highlighted suggestion into the input box, and 
	remove the suggestion dropdown.
	********************************************************/
	this.useSuggestion = function(elem)
	{
		if (this.highlighted > -1)
		{
			// Call the user's function to handle selection
			me.selectFunc(elem, this.eligible[this.highlighted]);

			this.hideDiv();
			//It's impossible to cancel the Tab key's default behavior. 
			//So this undoes it by moving the focus back to our field right after
			//the event completes.
			setTimeout("document.getElementById('" + this.elem.id + "').focus()",0);
		}
	};

	/********************************************************
	Display the dropdown. Pretty straightforward.
	********************************************************/
	this.showDiv = function()
	{
		this.div.style.display = 'block';
		this.displayingSuggestions = true;
	};

	/********************************************************
	Hide the dropdown and clear any highlight.
	********************************************************/
	this.hideDiv = function()
	{
		this.div.style.display = 'none';
		this.highlighted = -1;
		this.displayingSuggestions = false;
	};

	/********************************************************
	Modify the HTML in the dropdown to move the highlight.
	********************************************************/
	this.changeHighlight = function()
	{
		var lis = this.div.getElementsByTagName('TR');
		for (i in lis)
		{
			if(i == 0)
                          // Skip the header
                          continue;

			var li = lis[i];

			if (this.highlighted == i)
			{
				li.className = "selected";
			}
			else
			{
				li.className = "";
			}
		}
	};

	/********************************************************
	Position the dropdown div below the input text field.
	********************************************************/
	this.positionDiv = function()
	{
		var el = this.elem;
		var x = 0;
		var y = el.offsetHeight;

		//Walk up the DOM and add up all of the offset positions.
		while (el.offsetParent && el.tagName.toUpperCase() != 'BODY')
		{
			x += el.offsetLeft;
			y += el.offsetTop;
			el = el.offsetParent;
		}

		x += el.offsetLeft;
		y += el.offsetTop;

		this.div.style.left = x + 'px';
		this.div.style.top = y + 'px';

		// Adjust for scroll
	        // alert(this.elem.offsetParent.scrollTop);
                y -= this.elem.offsetParent.scrollTop;
		this.div.style.top = y + 'px';
	};

	/********************************************************
	Build the HTML for the dropdown div
	********************************************************/
	this.createDiv = function()
	{
		// We expect this.eligible to contain the list to be shown to the
		// user. Format for each row:
		// 
		// & : separates each field
		// > : as the first character of a field indicates a hidden value
		//     and will be ignored

		var tbl = document.createElement('table');
		tbl.className = "suggestion_list";

		var tbody = document.createElement('tbody');
	        tbl.appendChild(tbody);

		for(i in this.eligible) {

		  var row = document.createElement('tr');
		  var flds = this.eligible[i].split('&');

		  for(j in flds) {
		
		    // Ignore hidden fields (starting with '>')
	            if(flds[j].charAt(0) == '>')
                      continue;
 
		    var cell;

		    // If the number of fields is > 1, we expect the first
		    // row to be the header.
		    if(i == 0 && flds.length > 1) {
		      cell = document.createElement('th');
	            } else
		      cell = document.createElement('td');

		    var txt = document.createTextNode(flds[j]);

		    cell.appendChild(txt);
		    row.appendChild(cell);
                  }

		  if (me.highlighted == i)
		  {
			row.className = "selected";
		  }
	
		  tbody.appendChild(row);
                }

		this.div.replaceChild(tbl,this.div.childNodes[0]);

		/********************************************************
		click handler for the dropdown ul
		insert the clicked suggestion into the input
		********************************************************/
		tbl.onclick = function(ev)
		{
			me.useSuggestion(this);
			me.hideDiv();
			me.cancelEvent(ev);
			return false;
		};
	
		this.div.className="suggestion_list";
		this.div.style.position = 'absolute';
	};

	/********************************************************
	determine which of the suggestions matches the input
	********************************************************/
	this.getEligible = function()
	{
		this.eligible = this.searchCallback(this.inputText);
	};

	/********************************************************
	Helper function to determine the keycode pressed in a 
	browser-independent manner.
	********************************************************/
	this.getKeyCode = function(ev)
	{
		if(ev)			//Moz
		{
			return ev.keyCode;
		}
		if(window.event)	//IE
		{
			return window.event.keyCode;
		}
	};

	/********************************************************
	Helper function to determine the event source element in a 
	browser-independent manner.
	********************************************************/
	this.getEventSource = function(ev)
	{
		if(ev)			//Moz
		{
			return ev.target;
		}
	
		if(window.event)	//IE
		{
			return window.event.srcElement;
		}
	};

	/********************************************************
	Helper function to cancel an event in a 
	browser-independent manner.
	(Returning false helps too).
	********************************************************/
	this.cancelEvent = function(ev)
	{
		if(ev)			//Moz
		{
			ev.preventDefault();
			ev.stopPropagation();
		}
		if(window.event)	//IE
		{
			window.event.returnValue = false;
		}
	}
}

//counter to help create unique ID's
var idCounter = 0;