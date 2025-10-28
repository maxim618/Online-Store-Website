<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<title>All Admins</title>
	<%@ include file="common_css_js.jsp" %>
</head>
<body>
<%@ include file="navbar.jsp" %>

<div class="container mt-4">
	<h2>List of Admins</h2>
	<hr/>

	<c:if test="${empty admins}">
		<div class="alert alert-info">No admins available.</div>
	</c:if>

	<c:if test="${not empty admins}">
		<table class="table table-striped table-hover">
			<thead>
			<tr>
				<th>ID</th>
				<th>Name</th>
				<th>Email</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="a" items="${admins}">
				<tr>
					<td>${a.id}</td>
					<td>${a.name}</td>
					<td>${a.email}</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</c:if>
</div>
</body>
</html>
