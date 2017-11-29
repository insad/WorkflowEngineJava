<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<table class="table">
    <tbody>
    <tr>
        <th style="width:20px">Number</th>
        <th>Name</th>
    </tr>
    <c:forEach items="${roles}" var="item" varStatus="loop">
    <tr>
        <td><c:out value="${loop.index + 1}"/></td>
        <td><c:out value="${item.name}"/></td>
    </tr>
    </c:forEach>
</table>