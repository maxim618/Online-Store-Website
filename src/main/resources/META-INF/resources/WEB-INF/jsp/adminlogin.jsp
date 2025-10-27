<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>Admin Login</title>
	<%@ include file="common_css_js.jsp" %>
</head>
<body>
<%@ include file="alert_message.jsp" %>

<div class="container mt-5">
	<div class="row">
		<div class="col-md-4 offset-md-4">
			<div class="card">
				<div class="card-body">
					<h3 class="text-center">Admin Login</h3>
					<form action="${pageContext.request.contextPath}/admin/login" method="post">
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

				</div>
			</div>
		</div>
	</div>
</div>
</body>
</html>
