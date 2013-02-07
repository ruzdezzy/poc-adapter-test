
	var maxRows = 10;
	$('.paginated-table').each(
			function() {
				var cTable = $(this);
				var cRows = cTable.find('tr:gt(0)');
				var cRowCount = cRows.size();
				var number_of_pages = Math.ceil(cRowCount/maxRows);
				var whereAreWe = 1;
				
				var ret = "Showing Page "+ whereAreWe +" Of "+number_of_pages+" With "+cRowCount+" Mesages";
				//var ret = "Total Messages = " + cRowCount +" Pages = "+number_of_pages;
				$(records).html("<it>&nbsp;&nbsp;" + ret + "</it>");
				if (cRowCount < maxRows) {
					return;
				}

				/* add numbers to the rows for visuals on the demo 
				cRows.each(function(i) {
					$(this).find('td:first').text(function(j, val) {
						return (i + 1) + " - " + val;
					});
				});*/

				/* hide all rows above the max initially */
				cRows.filter(':gt(' + (maxRows - 1) + ')').hide();

				var cPrev = cTable.siblings('.prev');
				var cNext = cTable.siblings('.next');

				/* start with previous disabled */
				cPrev.addClass('disabled');

				cPrev.click(function() {
					var cFirstVisible = cRows.index(cRows.filter(':visible'));
					if(whereAreWe > 1){
					whereAreWe = whereAreWe - 1;
					}
					ret = "Showing Page "+ whereAreWe +" Of "+number_of_pages+" With "+cRowCount+" Mesages";
					$(records).html("<i>&nbsp;&nbsp;" + ret + "</i>");
					
					if (cPrev.hasClass('disabled')) {
						return false;
					}

					cRows.hide();
					if (cFirstVisible - maxRows - 1 > 0) {
						cRows.filter(
								':lt(' + cFirstVisible + '):gt('
										+ (cFirstVisible - maxRows - 1) + ')')
								.show();
					} else {
						cRows.filter(':lt(' + cFirstVisible + ')').show();
					}

					if (cFirstVisible - maxRows <= 0) {
						cPrev.addClass('disabled');
					}

					cNext.removeClass('disabled');

					return false;
				});

				cNext.click(function() {
					
					var cFirstVisible = cRows.index(cRows.filter(':visible'));
					if(whereAreWe < number_of_pages){
					whereAreWe = whereAreWe + 1;
					}
					ret = "Showing Page "+ whereAreWe +" Of "+number_of_pages+" With "+cRowCount+" Mesages";
					$(records).html("<i>&nbsp;&nbsp;" + ret + "</i>");
					
					if (cNext.hasClass('disabled')) {
						return false;
					}

					cRows.hide();
					cRows.filter(
							':lt(' + (cFirstVisible + 2 * maxRows) + '):gt('
									+ (cFirstVisible + maxRows - 1) + ')')
							.show();

					if (cFirstVisible + 2 * maxRows >= cRows.size()) {
						cNext.addClass('disabled');
					}

					cPrev.removeClass('disabled');

					return false;
				});

			});