<%@ include file="/common/taglibs.jsp"%>

<table class="table table-striped">
	<thead>
		<tr>
			<th class="first"></th>
			<th>File Name</th>
			<th>Type</th>
			<th>Download</th>
			<th>View</th>
			<c:if test="${ canModifyVulnerabilities }">
				<th class="last">Delete</th>
			</c:if>
		</tr>
	</thead>
	<tbody id="wafTableBody">
	<c:if test="${ empty vulnerability.documents }">
		<tr class="bodyRow">
			<td colspan="6" style="text-align:center;">No documents found.</td>
		</tr>
	</c:if>
	<c:forEach var="document" items="${ vulnerability.documents }" varStatus="status">
		<tr class="bodyRow">
			<td id="docNum${ status.count }"><c:out value="${ status.count }" /></td>
			<td id="name${ status.count }"><c:out value="${ document.name }"/></td>
			<td id="type${ status.count }"><c:out value="${ document.type }"/></td>
			<td>
				<spring:url value="/organizations/{orgId}/applications/{appId}/documents/{docId}/download" var="downloadUrl">
					<spring:param name="orgId" value="${ vulnerability.application.organization.id }"/>
					<spring:param name="appId" value="${ vulnerability.application.id }"/>
					<spring:param name="docId" value="${ document.id }"/>
				</spring:url>
				<a  href="<c:out value="${ downloadUrl }"/>">Download</a>
			</td>			
			<td>
				<spring:url value="/organizations/{orgId}/applications/{appId}/documents/{docId}/view" var="viewUrl">
					<spring:param name="orgId" value="${ vulnerability.application.organization.id }"/>
					<spring:param name="appId" value="${ vulnerability.application.id }"/>
					<spring:param name="docId" value="${ document.id }"/>
				</spring:url>
				<a href="<c:out value="${ viewUrl }"/>" target="_blank">View</a>
			</td>
			<c:if test="${ canModifyVulnerabilities }">
			<td>
				<spring:url value="/organizations/{orgId}/applications/{appId}/documents/{docId}/delete" var="deleteUrl">
					<spring:param name="orgId" value="${ vulnerability.application.organization.id }"/>
					<spring:param name="appId" value="${ vulnerability.application.id }"/>
					<spring:param name="docId" value="${ document.id }"/>
				</spring:url>
				<a onclick='return confirm("Do you really want to delete this document?")' href="<c:out value="${ deleteUrl }"/>">Delete</a>
			</td>
			</c:if>
		</tr>
	</c:forEach>
	</tbody>
</table>
<c:if test="${ canModifyVulnerabilities }">
		<div style="margin-top:10px;margin-bottom:7px;">
			<a id="uploadDocVulnModalLink${ vulnerability.id }" href="#uploadDocVuln${ vulnerability.id }" role="button" class="btn" data-toggle="modal">Upload</a>
			<%@ include file="/WEB-INF/views/applications/modals/uploadDocVulnModal.jsp" %>
		</div>	
</c:if>