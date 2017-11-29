<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:genericpagewithoutuser pageTitle="Settings">
    <jsp:body>
        <h2>Settings</h2>

        <table class="table">
            <tbody>
            <tr>
                <th>Param</th>
                <th>Value</th>
            </tr>
            <tr>
                <td><code>Workflow Scheme</code></td>
                <td>
                    <div>
                        <a class="button" target="_blank" href="/Designer">
                            Open in Designer</a>
                    </div>
                </td>
            </tr>
            <tr>
                <td><code>Roles</code></td>
                <td>
                    <jsp:include page="roles.jsp">
                        <jsp:param name="roles" value="${roles}"/>
                    </jsp:include>
                </td>
            </tr>
            <tr>
                <td><code>StructDivisions</code></td>
                <td>
                    <jsp:include page="struct-division.jsp">
                        <jsp:param name="structDivision" value="${structDivision}"/>
                        <jsp:param name="employees" value="${employees}"/>
                    </jsp:include>
                </td>
            </tr>
            </tbody>
        </table>
    </jsp:body>
</t:genericpagewithoutuser>