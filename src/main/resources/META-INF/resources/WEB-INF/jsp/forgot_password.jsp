<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
		 pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Forgot Password</title>
	<%@include file="common_css_js.jsp"%>
	<style>
		label {
			font-weight: bold;
		}
	</style>
</head>
<body>
<!-- Navbar -->
<%@include file="navbar.jsp"%>

<div class="container-fluid mt-4">
	<div class="row g-0">
		<div class="col-md-6 offset-md-3">
			<div class="card">
				<div class="card-body px-5">

					<div class="container text-center">
						<img src="${pageContext.request.contextPath}/Images/forgot_password.png"
							 style="max-width: 100px;" class="img-fluid">
					</div>
					<h3 class="text-center">Forgot Password</h3>

					<!-- ✅ Сообщения -->
					<%@include file="alert_message.jsp"%>

					<!-- Forgot password form -->
					<form action="${pageContext.request.contextPath}/passwo 7м7 +rd/forgot" method="post">
						<div class="mb-3">
							<label class="form-label">Email</label>
							<input type="email" name="email" placeholder="Enter your registered email"
								   class="form-control" required>
						</div>
						<div class="container text-center">
							<button type="submit" class="btn btn-outline-primary">Send OTP</button>
						</div>
					</form>

					<div class="mt-3 text-center">
						<h6>
							Remembered your password? <a href="${pageContext.request.contextPath}/login"
														 style="text-decoration: none">Login</a>
						</h6>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</body>
</html>
