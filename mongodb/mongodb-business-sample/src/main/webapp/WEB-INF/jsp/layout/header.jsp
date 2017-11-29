<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="business.helpers.EmployeeHelper" %>
<%@ page import="business.models.Employee" %>
<%@ page import="optimajet.workflow.core.util.UUIDUtil" %>
<%@ page import="wf.sample.helpers.CurrentUserSettings" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.UUID" %>

<%
    List<Employee> employeeList = EmployeeHelper.getAll();
    Map<Employee, String> employeeRoles = new HashMap<>();

    UUID currentEmployeeId = CurrentUserSettings.getCurrentUser(request, response);
    if (currentEmployeeId.equals(UUIDUtil.EMPTY)) {
        if (employeeList != null && !employeeList.isEmpty()) {
            currentEmployeeId = employeeList.get(0).getId();
            CurrentUserSettings.setUserInCookies(currentEmployeeId, response);
        }
    }
    for (Employee e : employeeList) {
        String roles = EmployeeHelper.getListRoles(e);
        employeeRoles.put(e, roles);
    }
    pageContext.setAttribute("employeeList", employeeList);
    pageContext.setAttribute("employeeRoles", employeeRoles);
    pageContext.setAttribute("currentEmployeeId", currentEmployeeId);
%>

<table width="100%">
    <tr>
        <td style="width:120px">
            <label for="CurrentEmployee"><b>Current employee:</b></label>
        </td>
        <td>
            <select id="CurrentEmployee" name="CurrentEmployee" onchange="CurrentEmployee_OnChange(this);"
                    style="width:100%">
                <c:forEach items="${employeeList}" var="item">
                    <option
                            <c:if test="${item.id.equals(currentEmployeeId)}">selected</c:if>
                            value="${item.id}">
                        Name: ${item.name}; StructDivision: ${item.structDivisionName},
                        Roles: <c:out value="${employeeRoles.get(item)}"/><c:if test="${item.head}">;Head</c:if>
                    </option>
                </c:forEach>
            </select>
        </td>
    </tr>
</table>

<script>
    function CurrentEmployee_OnChange(sender) {
        window.location.search = "CurrentEmployee=" + sender.value;
    }
</script>