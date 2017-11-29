<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${model != null && !model.transitionHistories.isEmpty()}">
    <h1>Document's Transition History</h1>
    <table class="table">
        <tbody>
        <tr>
            <th>From</th>
            <th>To</th>
            <th>Command</th>
            <th>Executor</th>
            <th>TransitionTime</th>
            <th>Available for</th>
        </tr>

        <c:forEach items="${model.transitionHistories}" var="item">
            <tr>
                <td>${item.initialState}</td>
                <td>${item.destinationState}</td>
                <td>${item.command}</td>
                <td>${item.employee.name}</td>
                <td>
                    <c:if test="${item.transitionTime != null}">
                        <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${item.transitionTime}"/>
                    </c:if>
                </td>
                <td>${item.allowedToEmployeeNames}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>
