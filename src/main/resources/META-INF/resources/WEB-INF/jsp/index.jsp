<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page errorPage="error_exception.jsp" %>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Home</title>
	<%@include file="common_css_js.jsp"%>
	<style type="text/css">
		.cus-card {
			border-radius: 50%;
			border-color: transparent;
			max-height: 200px;
			max-width: 200px;
		}
		.real-price { font-size: 20px !important; font-weight: 600; }
		.product-price { font-size: 17px !important; text-decoration: line-through; }
		.product-discount { font-size: 15px !important; color: #027a3e; }
	</style>
</head>
<body>
<!--navbar -->
<%@include file="navbar.jsp"%>

<!-- Category list -->
<div class="container-fluid px-3 py-3" style="background-color: #e3f7fc;">
	<div class="row">
		<div class="card-group">
			<c:forEach var="c" items="${categoryList}">
				<div class="col text-center">
					<a href="${pageContext.request.contextPath}/products?category=${c.categoryId}" style="text-decoration: none;">
						<div class="card cus-card h-100">
							<div class="container text-center">
								<img src="Product_imgs/${c.categoryImage}" class="mt-3"
									 style="max-width: 100%; max-height: 100px; width: auto; height: auto;">
							</div>
							<h6>${c.categoryName}</h6>
						</div>
					</a>
				</div>
			</c:forEach>
		</div>
	</div>
</div>

<!-- Carousel -->
<div id="carouselAutoplaying" class="carousel slide carousel-dark mt-3 mb-3" data-bs-ride="carousel">
	<div class="carousel-inner">
		<div class="carousel-item active">
			<img src="Images/scroll_img2.png" class="d-block w-100" alt="...">
		</div>
		<div class="carousel-item">
			<img src="Images/scroll_img1.png" class="d-block w-100" alt="...">
		</div>
		<div class="carousel-item">
			<img src="Images/scroll_img3.png" class="d-block w-100" alt="...">
		</div>
	</div>
	<button class="carousel-control-prev" type="button" data-bs-target="#carouselAutoplaying" data-bs-slide="prev">
		<span class="carousel-control-prev-icon" aria-hidden="true"></span>
		<span class="visually-hidden">Previous</span>
	</button>
	<button class="carousel-control-next" type="button" data-bs-target="#carouselAutoplaying" data-bs-slide="next">
		<span class="carousel-control-next-icon" aria-hidden="true"></span>
		<span class="visually-hidden">Next</span>
	</button>
</div>

<!-- latest products -->
<div class="container-fluid py-3 px-3" style="background: #f2f2f2;">
	<div class="row row-cols-1 row-cols-md-4 g-3">
		<div class="col">
			<div class="container text-center px-5 py-5">
				<h1>Latest Products</h1>
				<img src="Images/product.png" class="card-img-top" style="max-width: 100%; max-height: 200px;">
			</div>
		</div>

		<c:forEach var="p" items="${productList}" end="2">
			<div class="col">
				<a href="viewProduct.jsp?pid=${p.productId}" style="text-decoration: none;">
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

<!-- Hot deals -->
<div class="container-fluid py-3 px-3" style="background: #f0fffe;">
	<h3>Hot Deals</h3>
	<div class="row row-cols-1 row-cols-md-4 g-3">
		<c:forEach var="p" items="${topDeals}" end="3">
			<div class="col">
				<a href="viewProduct.jsp?pid=${p.productId}" style="text-decoration: none;">
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

<!-- order confirmation -->
<c:if test="${not empty order}">
	<script type="text/javascript">
		Swal.fire({
			icon : 'success',
			title: 'Order Placed, Thank you!',
			text: 'Confirmation will be sent to ${user.userEmail}',
			width: 600,
			padding: '3em',
			showConfirmButton : false,
			timer : 3500,
			backdrop: `rgba(0,0,123,0.4)`
		});
	</script>
</c:if>
</body>
</html>
