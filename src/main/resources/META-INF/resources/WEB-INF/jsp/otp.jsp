<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Verify OTP</title>
    <%@ include file="common_css_js.jsp" %>
    <style>
        label { font-weight: bold; }
        .otp-input { letter-spacing: 0.3em; text-align: center; font-size: 1.1rem; }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>

<div class="container mt-5">
    <div class="row">
        <div class="col-md-5 offset-md-3">
            <div class="card shadow-sm">
                <div class="card-body px-4 py-4">
                    <div class="text-center mb-3">
                        <img src="${pageContext.request.contextPath}/Images/otp.png" alt="OTP"
                             style="max-width: 100px;" class="img-fluid">
                    </div>
                    <h3 class="text-center mb-3">Enter OTP</h3>

                    <%@ include file="alert_message.jsp" %>

                    <form action="${pageContext.request.contextPath}/password/otp" method="post" class="mt-3">
                        <c:if test="${not empty email}">
                            <input type="hidden" name="email" value="${email}">
                        </c:if>

                        <div class="mb-3">
                            <label class="form-label">OTP Code</label>
                            <input type="text" name="otp" maxlength="6" minlength="4"
                                   pattern="\\d{4,6}" class="form-control otp-input"
                                   placeholder="Enter 6-digit code" required>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary">Verify</button>
                        </div>
                    </form>

                    <hr class="my-4"/>
                    <div class="d-flex justify-content-between">
                        <form action="${pageContext.request.contextPath}/admin/login/resend-otp" method="post">
                            <c:if test="${not empty email}">
                                <input type="hidden" name="email" value="${email}">
                            </c:if>
                            <button type="submit" class="btn btn-link">Resend Code</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
