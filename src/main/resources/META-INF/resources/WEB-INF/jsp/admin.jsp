<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<title>Admin Management</title>
	<%@ include file="common_css_js.jsp" %>
</head>
<body>
<%@ include file="navbar.jsp" %>

<div class="container mt-4">
	<div class="d-flex justify-content-between align-items-center">
		<h2>Manage Admins</h2>
		<a href="${pageContext.request.contextPath}/logout" class="btn btn-danger">Logout</a>
	</div>
	<hr/>

	<c:if test="${not empty error}">
		<div class="alert alert-danger">${error}</div>
	</c:if>
	<c:if test="${not empty success}">
		<div class="alert alert-success">${success}</div>
	</c:if>

	<!-- форма добавления -->
	<form action="${pageContext.request.contextPath}/admin/manage/add" method="post" class="mb-4">
		<div class="row">
			<div class="col-md-3">
				<input type="text" name="name" class="form-control" placeholder="Name" required/>
			</div>
			<div class="col-md-3">
				<input type="email" name="email" class="form-control" placeholder="Email" required/>
			</div>
			<div class="col-md-3">
				<input type="password" name="password" class="form-control" placeholder="Password" required/>
			</div>
			<div class="col-md-3">
				<button type="submit" class="btn btn-primary">Add Admin</button>
			</div>
		</div>
	</form>

	<c:if test="${empty admins}">
		<div class="alert alert-info">No admins found.</div>
	</c:if>

	<c:if test="${not empty admins}">
		<table class="table table-striped table-hover">
			<thead>
			<tr>
				<th>ID</th>
				<th>Name</th>
				<th>Email</th>
				<th>Action</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="a" items="${admins}">
				<tr>
					<td>${a.id}</td>
					<td>${a.name}</td>
					<td>${a.email}</td>
					<td>
						<a href="${pageContext.request.contextPath}/admin/manage/delete/${a.id}"
						   class="btn btn-danger btn-sm">Delete</a>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</c:if>
</div>
</body>
</html>
