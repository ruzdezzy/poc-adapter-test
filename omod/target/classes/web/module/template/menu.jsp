<br />
<ul id="menu">
	<li
		<c:if test='<%=request.getRequestURI().contains("manageQueue")%>'>class="active"</c:if>><a
		href="${pageContext.request.contextPath}/module/rheapocadapter/manageQueue.form"><spring:message
				code="rheapocadapter.label.archive" /></a></li>
	<li
		<c:if test='<%=request.getRequestURI().contains("processingQueue")%>'>class="active"</c:if>><a
		href="${pageContext.request.contextPath}/module/rheapocadapter/processingQueue.form"><spring:message
				code="rheapocadapter.label.processing" /></a></li>
	<li
		<c:if test='<%=request.getRequestURI().contains("errorQueue")%>'>class="active"</c:if>><a
		href="${pageContext.request.contextPath}/module/rheapocadapter/errorQueue.form"><spring:message
				code="rheapocadapter.label.error" /></a></li>
	<li
		<c:if test='<%=request.getRequestURI().contains("backEnteredQueue")%>'>class="active"</c:if>><a
		href="${pageContext.request.contextPath}/module/rheapocadapter/backEnteredQueue.form"><spring:message
				code="rheapocadapter.label.backEnteredQueue" /></a></li>
</ul>

<!-- ${pageContext.request.contextPath}/module/adaptermodule/registerPatientForm.form -->