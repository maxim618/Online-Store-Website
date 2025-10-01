<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
	<title>${product.productName}</title>
	<%@ include file="common_css_js.jsp" %>
</head>
<body>
<%@ include file="navbar.jsp" %>

<div class="container my-4">
	<div class="row">
		<div class="col-md-6 text-center">
			<img src="Product_imgs/${product.productImages}"
				 alt="${product.productName}"
				 style="max-width:100%; max-height:400px;">
		</div>
		<div class="col-md-6">
			<h2>${product.productName}</h2>
			<p>${product.productDescription}</p>
			<h4>
				<span class="real-price">&#8377;${product.productPriceAfterDiscount}</span>
				&ensp;<span class="product-price">&#8377;${product.productPrice}</span>
				&ensp;<span class="product-discount">${product.productDiscount}% off</span>
			</h4>

			<form method="post" action="${pageContext.request.contextPath}/cart/add">
				<input type="hidden" name="pid" value="${product.productId}" />
				<button type="submit" class="btn btn-primary">Add to Cart</button>
			</form>
		</div>
	</div>
</div>
</body>
</html>
