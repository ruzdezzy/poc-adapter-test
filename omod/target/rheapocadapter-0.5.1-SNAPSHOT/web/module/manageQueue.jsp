<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%-- <%@ taglib prefix="form" uri="resources/spring-form.tld"%> --%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<openmrs:htmlInclude
	file="/moduleResources/rheapocadapter/jquery-1.4.4.min.js" />
	<openmrs:htmlInclude
	file="/moduleResources/rheapocadapter/paginator.js" />
<%@ include file="template/menu.jsp"%>
<br />
<style>
		
		.collapse_tareas input, .collapse_tareas textarea { font: 10pt arial, sans-serif; border: 1px solid #888; }
		.collapse_tareas textarea { height: 1.45em; }
		.collapse_tareas textarea.compact { height: 1.45em !important; }
		.collapse_tareas textarea.expanded { height: 25em !important; }
</style>
<script>
		
		function setupTextareas() {
			var pageForms = document.getElementsByTagName("form");

			for( var j=0; j<pageForms.length; j++) {
				var formArea = pageForms[j];

				if( formArea.className.indexOf("collapse_tareas") > -1 ) {
					var txtAreas = formArea.getElementsByTagName("textarea");
					for( var i=0; i<txtAreas.length; i++ ) {
						var thisTxtArea = txtAreas[i];

						if( thisTxtArea.addEventListener ) {
							thisTxtArea.addEventListener("focus", bigSmlTextarea, false);
							thisTxtArea.addEventListener("blur", bigSmlTextarea, false);
						} else { // IE
							thisTxtArea.attachEvent("onfocus", bigSmlTextarea);
							thisTxtArea.attachEvent("onblur", bigSmlTextarea);
						}
					}
				}
			}
		}

		
		function bigSmlTextarea(e)
		{
			var node = ( e.target ? e.target : e.srcElement );

			if( node.className.indexOf("expanded") == -1 )
				node.className += " expanded";
			else
				node.className = node.className.replace(/expanded/gi, "");
		}

		
		window.onload = setupTextareas;
</script>
<div id="archiveQueue" style="text-align: center;">
	<b><u>Archive Transactions</u></b>
	<div class="box">
	<form class="collapse_tareas">
	<c:if test="${fn:length(archiveTransactions) > 10}">
	<span style="float:left;" class="prev"><button type="button">&lt;&lt;
				Previous</button></span> <span class="next"><button type="button">Next
				&gt;&gt;</button></span>
	<span style="float:right;" class="prev" id="records"></span> 
	</c:if>
		<table class="paginated-table">
			<tbody>
				<tr>
					<th>#</th>
					<th>id</th>
					<th>timeRequestSent</th>
					<th>message</th>
					<th>url</th>
					<th>sender</th>
					<th>Time Response Received</th>
					<th>relatedEncounter</th>

				</tr>

				<c:forEach var="archiveTransactions" items="${archiveTransactions}"
					varStatus="num">
					<tr class="${num.count%2!=0?'evenRow':'oddRow'}">
						<td><b>${num.count}.</b></td>
						<td>${archiveTransactions.id}</td>
						<td>${archiveTransactions.timeRequestSent}</td>
						<td><textarea rows="10" cols="50" style="border:none;" class="" readonly="readonly">${archiveTransactions.message}</textarea></td>
						<td>${archiveTransactions.url}</td>
						<td>${archiveTransactions.sender}</td>
						<td>${archiveTransactions.timeResponseReceived}</td>
						<td>${archiveTransactions.relatedEncounter}</td>

					</tr>
				</c:forEach>
			</tbody>
		</table>
		</form>
<script type="text/javascript">
	var maxRows = 10;
	$('.paginated-table').each(
			function() {
				var cTable = $(this);
				var cRows = cTable.find('tr:gt(0)');
				var cRowCount = cRows.size();
				var number_of_pages = Math.ceil(cRowCount/maxRows);
				var whereAreWe = 1;
				
				var ret = "Showing Page "+ whereAreWe +" Of "+number_of_pages+" With "+cRowCount+" Messages";
				//var ret = "Total Messages = " + cRowCount +" Pages = "+number_of_pages;
				$(records).html("<i>&nbsp;&nbsp;" + ret + "</i>");
				if (cRowCount < maxRows) {
					return;
				}

				
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
					ret = "Showing Page "+ whereAreWe +" Of "+number_of_pages+" With "+cRowCount+" Messages";
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
					ret = "Showing Page "+ whereAreWe +" Of "+number_of_pages+" With "+cRowCount+" Messages";
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
	
</script>
		
	</div>
</div>

<br />


<%@ include file="/WEB-INF/template/footer.jsp"%>