<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>My Orders</title>
	<%@ include file="common_css_js.jsp" %>
</head>
<body>
<%@ include file="navbar.jsp" %>

<div class="container mt-5">
	<%@ include file="alert_message.jsp" %>

	<h3>My Orders</h3>
	<hr/>

	<c:if test="${empty orders}">
		<div class="text-center">
			<img src="${pageContext.request.contextPath}/Images/order.png" style="max-width:150px;">
			<h5 class="mt-3">You have no orders yet.</h5>
		</div>
	</c:if>

	<c:if test="${not empty orders}">
		<table class="table table-hover">
			<thead>
			<tr>
				<th>Order ID</th>
				<th>Status</th>
				<th>Payment</th>
				<th>Date</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="o" items="${orders}">
				<tr>
					<td>${o.orderId}</td>
					<td>${o.status}</td>
					<td>${o.paymentType}</td>
					<td>${o.date}</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</c:if>
</div>
</body>
</html>
