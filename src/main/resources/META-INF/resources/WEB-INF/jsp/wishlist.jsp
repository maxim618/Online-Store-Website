<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
	<title>My Wishlist</title>
	<%@ include file="common_css_js.jsp" %>
</head>
<body>
<%@ include file="navbar.jsp" %>
<%@ include file="alert_message.jsp" %>

<div class="container px-3 py-3">
	<c:choose>
		<c:when test="${empty products}">
			<div class="container mt-5 mb-5 text-center">
				<img src="${pageContext.request.contextPath}/Images/wishlist.png"
					 style="max-width: 200px;" class="img-fluid">
				<h4 class="mt-3">Empty Wishlist</h4>
				You have no items in your wishlist. Start adding!
			</div>
		</c:when>
		<c:otherwise>
			<h4>My Wishlist (${fn:length(products)})</h4>
			<hr>
			<div class="container">
				<table class="table table-hover">
					<c:forEach var="p" items="${products}">
						<tr class="text-center">
							<td>
								<img src="${pageContext.request.contextPath}/Product_imgs/${p.productImages}"
									 style="width: 50px; height: 50px;">
							</td>
							<td class="text-start">${p.productName}</td>
							<td>&#8377;${p.productPriceAfterDiscount}</td>
							<td>
								<form action="${pageContext.request.contextPath}/wishlist/delete" method="post">
									<input type="hidden" name="pid" value="${p.productId}" />
									<button type="submit" class="btn btn-secondary">Remove</button>
								</form>
							</td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</c:otherwise>
	</c:choose>
</div>
</body>
</html>
