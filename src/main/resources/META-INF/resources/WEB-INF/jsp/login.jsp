<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<title>User Login</title>
	<%@ include file="common_css_js.jsp" %>
</head>
<body>
<%@ include file="navbar.jsp" %>
<%@ include file="alert_message.jsp" %>

<div class="container mt-5">
	<div class="row">
		<div class="col-md-4 offset-md-4">
			<div class="card">
				<div class="card-body">
					<h3 class="text-center">Sign-In</h3>
					<form action="${pageContext.request.contextPath}/login" method="post">
						<div class="mb-3">
							<label>Email</label>
							<input type="email" name="email" class="form-control" required>
						</div>
						<div class="mb-3">
							<label>Password</label>
							<input type="password" name="password" class="form-control" required>
						</div>
						<div class="text-center">
							<button type="submit" class="btn btn-primary">Login</button>
						</div>
					</form>
					<div class="mt-3 text-center">
						<a href="${pageContext.request.contextPath}/forgot_password">Forgot Password?</a><br>
						<a href="${pageContext.request.contextPath}/register">New User? Sign Up</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</body>
</html>
