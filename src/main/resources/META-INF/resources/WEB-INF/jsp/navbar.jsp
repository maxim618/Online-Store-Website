<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="navbar navbar-expand-lg navbar-light bg-light">
	<div class="container-fluid">
		<a class="navbar-brand" href="${pageContext.request.contextPath}/">My Store</a>
		<div class="collapse navbar-collapse">
			<ul class="navbar-nav me-auto mb-2 mb-lg-0">
				<c:forEach var="category" items="${categoryList}">
					<li class="nav-item">
						<a class="nav-link" href="${pageContext.request.contextPath}/products?category=${category.categoryId}">
								${category.categoryName}
						</a>
					</li>
				</c:forEach>
			</ul>

			<ul class="navbar-nav ms-auto">
				<c:if test="${not empty activeUser}">
					<li class="nav-item">
						<a class="nav-link" href="${pageContext.request.contextPath}/cart">
							Cart (<c:out value="${cartCount}" />)
						</a>
					</li>
					<li class="nav-item">
						<a class="nav-link" href="${pageContext.request.contextPath}/logout">Logout</a>
					</li>
				</c:if>

				<c:if test="${empty activeUser}">
					<li class="nav-item">
						<a class="nav-link" href="${pageContext.request.contextPath}/login">Login</a>
					</li>
				</c:if>
			</ul>
		</div>
	</div>
</nav>
