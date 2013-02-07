<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%-- <%@ taglib prefix="form" uri="resources/spring-form.tld"%> --%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<openmrs:htmlInclude file="/moduleResources/rheapocadapter/jquery-1.4.4.min.js" />

<br />
<%@ include file="template/menu.jsp"%>
<br />

<div id="errorTransactions" style="text-align: center;">
	<b><u>Error Transactions</u></b>
	<div class="box">
	<c:if test="${fn:length(errorTransactions) > 10}">
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
					<th>Error</th>
					<th>url</th>
					<th>sender</th>
					<th>error</th>
					<th>Time Response Received</th>

				</tr>

				<c:forEach var="errorTransactions" items="${errorTransactions}"
					varStatus="num">
					<tr class="${num.count%2!=0?'evenRow':'oddRow'}">
						<td><b>${num.count}.</b></td>
						<td>${errorTransactions.id}</td>
						<td>${errorTransactions.timeRequestSent}</td>
						<td>${errorTransactions.message}</td>
						<td>${errorTransactions.url}</td>
						<td>${errorTransactions.sender}</td>
						<td>${errorTransactions.error}</td>
						<td>${errorTransactions.responseTimeReceived}</td>

					</tr>
				</c:forEach>
			</tbody>
		</table>
<script type="text/javascript">
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
	
</script>
	</div>
</div>


<%@ include file="/WEB-INF/template/footer.jsp"%>