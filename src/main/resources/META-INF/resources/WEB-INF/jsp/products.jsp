<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page errorPage="error_exception.jsp" %>

<!DOCTYPE html>
<html>
<head>
	<title>Products</title>
	<%@include file="common_css_js.jsp"%>
</head>
<body>
<%@include file="navbar.jsp"%>

<div class="container my-4">
	<h2>Products in ${category.categoryName}</h2>

	<c:if test="${empty products}">
		<p>No products found in this category.</p>
	</c:if>

	<div class="row row-cols-1 row-cols-md-4 g-3">
		<c:forEach var="p" items="${products}">
			<div class="col">
				<a href="${pageContext.request.contextPath}/viewProduct?pid=${p.productId}" style="text-decoration: none;">
					<div class="card h-100">
						<div class="container text-center">
							<img src="Product_imgs/${p.productImages}" class="card-img-top m-2"
								 style="max-width: 100%; max-height: 200px;">
						</div>
						<div class="card-body">
							<h5 class="card-title text-center">${p.productName}</h5>
							<div class="container text-center">
								<span class="real-price">&#8377;${p.productPriceAfterDiscount}</span>
								&ensp;<span class="product-price">&#8377;${p.productPrice}</span>
								&ensp;<span class="product-discount">${p.productDiscount}% off</span>
							</div>
						</div>
					</div>
				</a>
			</div>
		</c:forEach>
	</div>
</div>
</body>
</html>
