<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
	<title>Checkout</title>
	<%@ include file="common_css_js.jsp" %>
</head>
<body>
<%@ include file="navbar.jsp" %>

<div class="container my-4">
	<h2>Checkout</h2>

	<c:if test="${empty cartList}">
		<p>Your cart is empty. <a href="${pageContext.request.contextPath}/products?category=1">Shop now</a></p>
	</c:if>

	<c:if test="${not empty cartList}">
		<table class="table table-bordered">
			<thead>
			<tr>
				<th>Product ID</th>
				<th>Quantity</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="c" items="${cartList}">
				<tr>
					<td>${c.productId}</td>
					<td>${c.quantity}</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>

		<h4>Total: &#8377;${totalPrice}</h4>

		<form method="post" action="${pageContext.request.contextPath}/order/place">
			<div class="form-group">
				<label>Payment Type:</label>
				<select name="paymentType" class="form-control">
					<option value="COD">Cash on Delivery</option>
					<option value="CARD">Credit/Debit Card</option>
				</select>
			</div>
			<button type="submit" class="btn btn-success">Place Order</button>
		</form>

	</c:if>
</div>
</body>
</html>
