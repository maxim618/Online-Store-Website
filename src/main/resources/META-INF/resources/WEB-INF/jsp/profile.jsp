<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container my-4">
	<h2>My Profile</h2>
	<hr>

	<c:if test="${empty user}">
		<p class="text-danger">User not found or not logged in.</p>
	</c:if>

	<c:if test="${not empty user}">
		<table class="table table-bordered">
			<tr>
				<th>User ID</th>
				<td>${user.userId}</td>
			</tr>
			<tr>
				<th>Name</th>
				<td>${user.userName}</td>
			</tr>
			<tr>
				<th>Email</th>
				<td>${user.userEmail}</td>
			</tr>
			<tr>
				<th>Phone</th>
				<td>${user.userPhone}</td>
			</tr>
			<tr>
				<th>Address</th>
				<td>${user.userAddress}, ${user.userCity} - ${user.userPincode}, ${user.userState}</td>
			</tr>
			<tr>
				<th>Registered</th>
				<td>${user.dateTime}</td>
			</tr>
		</table>

		<!-- ✅ кнопка перехода на редактирование -->
		<a href="${pageContext.request.contextPath}/user/edit" class="btn btn-primary">Edit Profile</a>
	</c:if>
</div>
