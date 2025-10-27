<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${not empty message}">
	<div class="alert ${message.type} alert-dismissible fade show" role="alert">
		<strong>${message.title}</strong> ${message.content}
		<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
	</div>
</c:if>
