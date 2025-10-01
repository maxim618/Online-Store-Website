<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container my-4">
    <h2>Edit Profile</h2>
    <hr>

    <!-- ✅ блок для ошибок -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/user/edit" method="post">
        <div class="mb-3">
            <label class="form-label">Name</label>
            <input type="text" name="userName" value="${user.userName}" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Email</label>
            <input type="email" name="userEmail" value="${user.userEmail}" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Phone</label>
            <input type="text" name="userPhone" value="${user.userPhone}" class="form-control" placeholder="10-15 digits">
        </div>

        <div class="mb-3">
            <label class="form-label">Gender</label>
            <input type="text" name="userGender" value="${user.userGender}" class="form-control">
        </div>

        <div class="mb-3">
            <label class="form-label">Address</label>
            <input type="text" name="userAddress" value="${user.userAddress}" class="form-control">
        </div>

        <div class="mb-3">
            <label class="form-label">City</label>
            <input type="text" name="userCity" value="${user.userCity}" class="form-control">
        </div>

        <div class="mb-3">
            <label class="form-label">Pincode</label>
            <input type="text" name="userPincode" value="${user.userPincode}" class="form-control">
        </div>

        <div class="mb-3">
            <label class="form-label">State</label>
            <input type="text" name="userState" value="${user.userState}" class="form-control">
        </div>

        <button type="submit" class="btn btn-primary">Save</button>
        <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-secondary">Cancel</a>
    </form>
</div>
