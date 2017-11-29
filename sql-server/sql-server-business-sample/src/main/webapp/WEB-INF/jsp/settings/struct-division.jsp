<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="htmlGenerator" class="wf.sample.helpers.HtmlGenerator" scope="page"/>

<script src="${pageContext.request.contextPath}/scripts/jquery.treeTable.min.js" type="text/javascript"></script>
<link href="${pageContext.request.contextPath}/content/themes/base/jquery.treeTable.css" rel="stylesheet"
      type="text/css"/>

<style>
    table.table td.columnTree {
        padding-left: 20px;
    }
</style>

<table id="SDTable" class="table">
    <tbody>
    <tr>
        <th>Name</th>
        <th>Roles</th>
    </tr>
    <c:out value="${htmlGenerator.generateStructDivisions(structDivision, employees)}" escapeXml="false"/>
</table>
<script>
    $(document).ready(function () {
        $('#SDTable').treeTable(
            {
                initialState: "expanded"
            }
        );
    });
</script>