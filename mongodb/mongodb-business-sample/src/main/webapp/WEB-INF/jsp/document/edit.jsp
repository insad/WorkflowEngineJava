<%@ page import="business.helpers.DocumentHelper" %>
<%@ page import="business.helpers.EmployeeHelper" %>
<%@ page import="business.models.Document" %>
<%@ page import="business.models.DocumentCommandModel" %>
<%@ page import="business.models.DocumentTransitionHistory" %>
<%@ page import="business.models.Employee" %>
<%@ page import="optimajet.workflow.core.model.TransitionClassifier" %>
<%@ page import="optimajet.workflow.core.util.CollectionUtil" %>
<%@ page import="optimajet.workflow.core.util.UUIDUtil" %>
<%@ page import="wf.sample.helpers.CurrentUserSettings" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    List<Employee> employeeList = EmployeeHelper.getAll();
    Map<Employee, String> employeeRoles = new HashMap<>();

    for (Employee e : employeeList) {
        String roles = EmployeeHelper.getListRoles(e);
        employeeRoles.put(e, roles);
    }
    pageContext.setAttribute("employeeList", employeeList);
    pageContext.setAttribute("employeeRoles", employeeRoles);

    String designerUrl = "/Designer";
    DocumentCommandModel[] commands = new DocumentCommandModel[0];
    Map<String, String> states = new HashMap<>();
    Document model = (Document) request.getAttribute("model");

    if (model != null && model.getId() != null) {
        designerUrl = "/Designer?processid=" + model.getId().toString();
        commands = DocumentHelper.getCommands(model.getId(),
                UUIDUtil.asString(CurrentUserSettings.getCurrentUser(request, response)));
        states = DocumentHelper.getStates(model.getId());
    }

    DocumentTransitionHistory nextStep = CollectionUtil.firstOrDefault(model.getTransitionHistories(),
            new CollectionUtil.ItemCondition<DocumentTransitionHistory>() {
                @Override
                public boolean check(DocumentTransitionHistory c) {
                    return c.getTransitionTime() == null;
                }
            });

    pageContext.setAttribute("designerUrl", designerUrl);
    pageContext.setAttribute("commands", commands);
    pageContext.setAttribute("states", states);
    pageContext.setAttribute("nextStep", nextStep);

    pageContext.setAttribute("tcDirect", TransitionClassifier.Direct.name());
    pageContext.setAttribute("tcReverse", TransitionClassifier.Reverse.name());
%>

<t:genericpage pageTitle="Edit">
    <jsp:body>
        <c:if test="${error != null}"><h3 style="color: red;">${error}</h3></c:if>
        <form action="/Document/Edit" id="form0" method="post">
            <input id="Id" name="Id" type="hidden" value="${model.id}">
            <c:if test="${fn:length(commands) gt 0 || !states.isEmpty()}">
                <div>
                    <c:choose>
                        <c:when test="${fn:length(commands) == 0}">
                            <c:if test="${nextStep != null}">
                                <span style="color: #CC3300">
                                For the current user commands are not available. In the field <b>"Current employee"</b>,
                                    select one of the users: <b>${nextStep.allowedToEmployeeNames}</b>
                                </span>
                            </c:if>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${commands}" var="command">
                                <c:choose>
                                    <c:when test="${command.classifier == tcDirect}">
                                        <button name="button" value="${command.key}"
                                                class="button">${command.value}</button>
                                    </c:when>
                                    <c:when test="${command.classifier == tcReverse}">
                                        <button name="button" style="background: linear-gradient(#FF0033, #FF9999);"
                                                value="${command.key}" class="button">${command.value}</button>
                                    </c:when>
                                    <c:otherwise>
                                        <button name="button" style="background: linear-gradient(#666666, #CCCCCC);"
                                                value="${command.key}" class="button">${command.value}</button>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                        </c:otherwise>
                    </c:choose>
                    &nbsp;&nbsp;
                    <button name="button" value="SetState" class="button">Set This State</button>
                    <select id="StateNameToSet" name="StateNameToSet" style="width:15%">
                        <c:forEach items="${states}" var="state">
                            <option value="${state.key}">${state.value}</option>
                        </c:forEach>
                    </select>
                    <a class="button" target="_blank" style="text-align:right" href="${designerUrl}">Open in Workflow
                        Designer</a>
                </div>
            </c:if>
            <table class="table">
                <tbody>
                <tr>
                    <td><label for="Number">Number</label></td>
                    <td>
                        <input id="Number" name="Number" type="hidden" value="${model.number}">
                            ${model.number}
                    </td>
                </tr>
                <tr>
                    <td><b><label for="Name">Name</label></b></td>
                    <td>
                        <input class="text-box single-line" id="Name" name="Name" type="text" value="${model.name}">
                        <c:if test="${bindingResult != null}">
                            <span class="field-validation-error" id="Name_validationMessage">${bindingResult.getFieldError('name').getDefaultMessage()}</span>
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <td><label for="StateName">State</label></td>
                    <td>
                        <input id="StateName" name="StateName" type="hidden" value="${model.stateName}">
                            ${model.stateName}
                    </td>
                </tr>
                <tr>
                    <td><label for="AuthorName">Author</label></td>
                    <td>
                        <input id="AuthorId" name="AuthorId" type="hidden" value="${model.authorId}">
                        <input id="AuthorName" name="AuthorName" type="hidden" value="${model.authorName}">
                            ${model.authorName}
                    </td>
                </tr>
                <tr>
                    <td><label for="EmployeeControllerId">Controller</label></td>
                    <td>
                        <select id="EmployeeControllerId" name="EmployeeControllerId" style="width:98%">
                            <option <c:if test="${model.employeeControllerId == null}">selected
                            </c:if></option>
                            <c:forEach items="${employeeList}" var="item">
                                <option value="${item.id}"
                                        <c:if test="${model.employeeControllerId.equals(item.id)}">selected</c:if>>
                                    Name: ${item.name}; StructDivision: ${item.structDivisionName},
                                    Roles: <c:out value="${employeeRoles.get(item)}"/><c:if
                                        test="${item.head}">;Head</c:if>
                                </option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td><b><label for="Sum">Sum</label></b></td>
                    <td>
                        <input class="text-box single-line" id="Sum" name="Sum" type="text" value="${model.sum}">
                        <c:if test="${bindingResult != null}">
                            <span class="field-validation-valid" id="Sum_validationMessage">${bindingResult.getFieldError('sum').getDefaultMessage()}</span>
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <td><label for="Comment">Comment</label></td>
                    <td>
                    <textarea cols="100" id="Comment" name="Comment" rows="6"
                              style="width:98%">${model.comment}</textarea>
                    </td>
                </tr>
                </tbody>
            </table>

            <jsp:include page="document-history.jsp">
                <jsp:param name="model" value="${model}"/>
            </jsp:include>
            <div>
                <button name="button" value="Save" class="button">Save</button>
                <button name="button" value="SaveAndExit" class="button">Save & Exit</button>
            </div>
        </form>

    </jsp:body>
</t:genericpage>