/**
 * Created By Yashaswi Kumar yashx1@gmail.com
 */

function searchTable(inputVal) {
	var table = $('#tblData');
	table.find('tr').each(function(index, row) {
		var allCells = $(row).find('td');
		if (allCells.length > 0) {
			var found = false;
			allCells.each(function(index, td) {
				var regExp = new RegExp(inputVal, 'i');
				if (regExp.test($(td).text())) {
					found = true;
					return false;
				}
			});
			if (found == true)
				$(row).show();
			else
				$(row).hide();
		}
	});
}


function searchDiv(inputVal){
	
	var $div = $('#containerDiv');
	if(inputVal){
		$div.find('.grey-panel').hide();
		$div.find('.grey-panel:contains("'+inputVal+'")').show();		
	}else{
		$div.find('.grey-panel').show();
	}
}




//Code for overloading the :contains selector to be case insensitive:
//Without the overload on the :contains selector jquery would normaly only underline the second line

//New selector
jQuery.expr[':'].Contains = function(a, i, m) {
return jQuery(a).text().toUpperCase()
   .indexOf(m[3].toUpperCase()) >= 0;
};

//Overwrites old selecor
jQuery.expr[':'].contains = function(a, i, m) {
return jQuery(a).text().toUpperCase()
   .indexOf(m[3].toUpperCase()) >= 0;
};
