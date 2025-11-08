<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
		 pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<title>Registration</title>
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
						<img src="${pageContext.request.contextPath}/Images/signUp.png"
							 style="max-width: 80px;" class="img-fluid">
					</div>
					<h3 class="text-center">Create Account</h3>

					<!-- ✅ Сообщения -->
					<%@include file="alert_message.jsp"%>

					<!-- Registration form -->
					<form id="register-form" action="${pageContext.request.contextPath}/register" method="post">
						<div class="row">
							<div class="col-md-6 mt-2">
								<label class="form-label">Your name</label>
								<input type="text" name="user_name" class="form-control"
									   placeholder="First and last name" required>
							</div>
							<div class="col-md-6 mt-2">
								<label class="form-label">Email</label>
								<input type="email" name="user_email" placeholder="Email address"
									   class="form-control" required>
							</div>
						</div>
						<div class="row">
							<div class="col-md-6 mt-2">
								<label class="form-label">Mobile number</label>
								<input type="number" name="user_mobile_no"
									   placeholder="Mobile number" class="form-control">
							</div>
							<div class="col-md-6 mt-5">
								<label class="form-label pe-3">Gender</label>
								<input class="form-check-input" type="radio" name="gender" value="Male"> Male
								<input class="form-check-input ms-3" type="radio" name="gender" value="Female"> Female
							</div>
						</div>
						<div class="mt-2">
							<label class="form-label">Address</label>
							<input type="text" name="user_address"
								   placeholder="Enter Address (Area and Street)"
								   class="form-control" required>
						</div>
						<div class="row">
							<div class="col-md-6 mt-2">
								<label class="form-label">City</label>
								<input class="form-control" type="text" name="city"
									   placeholder="City/District/Town" required>
							</div>
							<div class="col-md-6 mt-2">
								<label class="form-label">Pincode</label>
								<input class="form-control" type="number" name="pincode"
									   placeholder="Pincode" maxlength="6" required>
							</div>
						</div>
						<div class="row">
							<div class="col-md-6 mt-2">
								<label class="form-label">State</label>
								<select name="state" class="form-select" required>
									<option selected disabled>--Select State--</option>
									<option value="Delhi">Delhi</option>
									<option value="Maharashtra">Maharashtra</option>
									<option value="Karnataka">Karnataka</option>
									<option value="Tamil Nadu">Tamil Nadu</option>
									<option value="Uttar Pradesh">Uttar Pradesh</option>
									<!-- остальные -->
								</select>
							</div>
							<div class="col-md-6 mt-2">
								<label class="form-label">Password</label>
								<input type="password" name="user_password"
									   placeholder="Enter Password" class="form-control" required>
							</div>
						</div>

						<div id="submit-btn" class="container text-center mt-4">
							<button type="submit" class="btn btn-outline-primary me-3">Submit</button>
							<button type="reset" class="btn btn-outline-primary">Reset</button>
						</div>
						<div class="mt-3 text-center">
							<h6>
								Already have an account? <a href="${pageContext.request.contextPath}/login"
															style="text-decoration: none"> Sign in</a>
							</h6>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>

</body>
</html>
