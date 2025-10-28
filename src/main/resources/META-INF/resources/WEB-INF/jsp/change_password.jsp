<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Change Password</title>
	<%@include file="common_css_js.jsp"%>
</head>
<body>
<%@include file="navbar.jsp"%>

<div class="container my-4">
	<h3 class="text-center">Set New Password</h3>

	<c:if test="${not empty error}">
		<div class="alert alert-danger">${error}</div>
	</c:if>

	<form action="${pageContext.request.contextPath}/password/change" method="post" class="mt-3">
		<div class="mb-3">
			<label class="form-label">New Password</label>
			<!-- исправлено: name="password" -->
			<input type="password" name="password" class="form-control" required minlength="6">
		</div>

		<div class="mb-3">
			<label class="form-label">Confirm Password</label>
			<!-- добавлено: подтверждение -->
			<input type="password" name="confirm" class="form-control" required minlength="6">
		</div>

		<button type="submit" class="btn btn-success">Change</button>
	</form>
</div>
</body>
</html>
