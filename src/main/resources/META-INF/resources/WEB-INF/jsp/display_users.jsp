<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container my-4">
	<h2>All Users</h2>
	<hr>

	<c:if test="${empty users}">
		<p>No users found.</p>
	</c:if>

	<c:if test="${not empty users}">
		<table class="table table-striped">
			<thead>
			<tr>
				<th>User ID</th>
				<th>Name</th>
				<th>Email</th>
				<th>Phone</th>
				<th>City</th>
				<th>State</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="u" items="${users}">
				<tr>
					<td>${u.userId}</td>
					<td>${u.userName}</td>
					<td>${u.userEmail}</td>
					<td>${u.userPhone}</td>
					<td>${u.userCity}</td>
					<td>${u.userState}</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</c:if>
</div>
