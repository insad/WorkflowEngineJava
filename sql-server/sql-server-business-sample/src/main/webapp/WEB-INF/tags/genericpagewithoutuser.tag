<%@tag description="Overall Page template" pageEncoding="UTF-8" %>
<%@ attribute name="pageTitle" type="java.lang.String" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <title>WorkfolowEngine NET 2.0 :: <%=pageTitle%>
    </title>
    <link href="${pageContext.request.contextPath}/content/style.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/content/themes/base/jquery-ui.min.css" rel="stylesheet"
          type="text/css"/>
    <script src="${pageContext.request.contextPath}/scripts/jquery.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/scripts/jquery-ui.js" type="text/javascript"></script>
    <meta name="viewport" content="width=device-width">
</head>
<body>
<div style="text-align: center; position:fixed;left:50%;margin-left: -200px; z-index:1000 ">
    <a class="button2" target="_blank" href="/Designer">Workflow Designer</a>
        <a class="button2" target="_blank" href="https://workflowengine.io/documentation">Documentation</a>
        <a class="button2" target="_blank" href="https://workflowengine.io/pricing">Purchase</a>
</div>
<div class="container">
    <header class="header clearfix">

        <a style="text-decoration:none" href="/">
            <div class="logo" style="color:red">WorkfolowEngine NET 2.0</div>
        </a>

        <jsp:include page="${pageContext.request.contextPath}/WEB-INF/jsp/layout/main-menu.jsp"/>

    </header>
    <div class="info">
        <article class="article clearfix">
            <div class="clearfix"></div>
            <jsp:doBody/>
        </article>
    </div>
    <jsp:include page="${pageContext.request.contextPath}/WEB-INF/jsp/layout/footer.jsp"/>
</div>
</body>
</html>