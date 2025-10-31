<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Manage Orders</title>
    <%@ include file="common_css_js.jsp" %>
</head>
<body>
<%@ include file="navbar.jsp" %>

<div class="container mt-4">
    <h2>All Orders</h2>
    <hr/>

    <c:if test="${empty orders}">
        <div class="alert alert-info">No orders found.</div>
    </c:if>

    <c:if test="${not empty orders}">
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <th>ID</th>
                <th>OrderId</th>
                <th>UserId</th>
                <th>Status</th>
                <th>Payment</th>
                <th>Date</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="order" items="${orders}">
                <tr>
                    <td>${order.id}</td>
                    <td>${order.orderId}</td>
                    <td>${order.userId}</td>
                    <td>${order.status}</td>
                    <td>${order.paymentType}</td>
                    <td>${order.date}</td>
                    <td>
                        <form action="${pageContext.request.contextPath}/admin/orders/update-status"
                              method="post" class="d-flex">
                            <input type="hidden" name="orderId" value="${order.id}"/>
                            <select name="status" class="form-select me-2">
                                <option value="Pending" ${order.status == 'Pending' ? 'selected' : ''}>Pending</option>
                                <option value="Processing" ${order.status == 'Processing' ? 'selected' : ''}>Processing</option>
                                <option value="Shipped" ${order.status == 'Shipped' ? 'selected' : ''}>Shipped</option>
                                <option value="Delivered" ${order.status == 'Delivered' ? 'selected' : ''}>Delivered</option>
                                <option value="Cancelled" ${order.status == 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                            </select>
                            <button type="submit" class="btn btn-primary btn-sm">Update</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>
</body>
</html>
