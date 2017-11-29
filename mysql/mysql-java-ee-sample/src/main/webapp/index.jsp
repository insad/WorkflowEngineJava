<%@ page import="java.util.Calendar" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Workflow Designer example</title>

    <link href="${pageContext.request.contextPath}/static/content/bootstrap.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/static/content/site.css" rel="stylesheet"/>

    <script src="${pageContext.request.contextPath}/static/scripts/modernizr-2.6.2.js"></script>
</head>
<body>
<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Application name</a>
        </div>
    </div>
</div>
<div class="container body-content">
    <jsp:include page="views/designer.jsp"/>
    <hr/>
    <footer>
        <p>&copy; <c:out value="<%=Calendar.getInstance().get(Calendar.YEAR)%>"/> - OptimaJet</p>
    </footer>
</div>

<script src="${pageContext.request.contextPath}/static/scripts/bootstrap.js"></script>
<script src="${pageContext.request.contextPath}/static/scripts/respond.js"></script>
</body>
</html>
