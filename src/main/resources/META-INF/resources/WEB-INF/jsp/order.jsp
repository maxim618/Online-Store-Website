<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
	<title>My Orders</title>
	<%@ include file="common_css_js.jsp" %>
</head>
<body>
<%@ include file="navbar.jsp" %>

<div class="container my-4">
	<h2>My Orders</h2>

	<c:if test="${empty orders}">
		<p>You have no orders yet.</p>
	</c:if>

	<c:if test="${not empty orders}">
		<table class="table table-bordered">
			<thead>
			<tr>
				<th>Order ID</th>
				<th>Date</th>
				<th>Status</th>
				<th>Payment Type</th>   <!--  новое поле -->
			</tr>
			</thead>
			<tbody>
			<c:forEach var="o" items="${orders}">
				<tr>
					<td>${o.orderId}</td>
					<td>${o.date}</td>
					<td>${o.status}</td>
					<td>${o.paymentType}</td> <!--  выводим -->
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</c:if>
</div>
</body>
</html>
