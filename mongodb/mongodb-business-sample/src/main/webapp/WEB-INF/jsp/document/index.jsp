<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<t:genericpage pageTitle="Documents">
    <jsp:body>
        <a href="/Document/Edit">Create</a>
        <a onclick="DeleteSelected()" href="#">Delete</a>

        <table class="table">
            <tbody>
            <tr>
                <th style="width:20px">#</th>
                <th style="width:20px">Number</th>
                <th>State</th>
                <th>Name</th>
                <th>Comment</th>
                <th>Author</th>
                <th>Controller</th>
                <th>Sum</th>
            </tr>

            <c:forEach items="${docs}" var="item">
            <tr>
                <td><input type="checkbox" name="checkedbox" class="selectedValues" value="<c:out value="${item.id}"/>">
                </td>
                <td><a href="/Document/Edit/<c:out value="${item.id}"/>">${item.number}</a></td>
                <td>${item.stateName}</td>
                <td><a href="/Document/Edit/<c:out value="${item.id}"/>">${item.name}</a></td>
                <td>${item.comment}</td>
                <td>${item.authorName}</td>
                <td>${item.employeeControllerName}</td>
                <td>${item.sum}</td>
            </tr>
            </c:forEach>
        </table>
        Current Page: ${page + 1}<br/>
        Items count: ${count}<br/>

        <c:if test="${page != 0}">
            <a href="?page=0">first page</a>
        </c:if>

        <c:if test="${page gt 0}">
            <a href="?page=${page - 1}">prev page</a>
        </c:if>

        <c:if test="${(page + 1) * pageSize lt count}">
            <a href="?page=${page + 1}">next page</a>
        </c:if>

        <c:if test="${(page + 2) * pageSize lt count}">
            <a href="?page=${fn:substringBefore(count / pageSize - 1, '.')}">last page</a>
        </c:if>

        <script>
            function DeleteSelected() {
                var data = [];
                var selectedValues = $('.selectedValues:checked');

                if (selectedValues.length < 1) {
                    alert('Items not selected');
                    return;
                }

                for (var i = 0; i < selectedValues.length; i++) {
                    data.push({name: 'ids', value: selectedValues[i].value});
                }

                $.ajax({
                    type: "POST",
                    url: "/Document/DeleteRows",
                    data: data,
                    success: function (msg) {
                        alert(msg);
                        location.reload();
                    }
                });
            }
        </script>
    </jsp:body>
</t:genericpage>