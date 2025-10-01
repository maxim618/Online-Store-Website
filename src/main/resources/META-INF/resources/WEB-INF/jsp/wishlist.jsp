<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="container px-3 py-3">

	<c:if test="${empty products}">
		<div class="container mt-5 mb-5 text-center">
			<img src="Images/wishlist.png" style="max-width: 200px;" class="img-fluid">
			<h4 class="mt-3">Empty Wishlist</h4>
			You have no items in your wishlist. Start adding!
		</div>
	</c:if>

	<c:if test="${not empty products}">
		<h4>My Wishlist (${fn:length(products)})</h4>
		<hr>
		<div class="container">
			<table class="table table-hover">
				<c:forEach var="p" items="${products}">
					<tr class="text-center">
						<td><img src="Product_imgs/${p.productImages}" style="width:50px;height:50px;"></td>
						<td class="text-start">${p.productName}</td>
						<td>&#8377;${p.productPriceAfterDiscount}</td>
						<td>
							<form method="post" action="${pageContext.request.contextPath}/wishlist/delete">
								<input type="hidden" name="pid" value="${p.productId}" />
								<button type="submit" class="btn btn-secondary">Remove</button>
							</form>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</c:if>
</div>
