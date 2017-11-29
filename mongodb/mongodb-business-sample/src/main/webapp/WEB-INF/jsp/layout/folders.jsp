<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<table>
    <tr>
        <td style="padding-right:30px"><a href="/">
            <c:choose>
                <c:when test="${folder == 'all'}"><b>All documents</b></c:when>
                <c:otherwise>All documents</c:otherwise>
            </c:choose>
        </a></td>
        <td style="padding-right: 30px"><a href="/Document/Inbox">
            <c:choose>
                <c:when test="${folder == 'inbox'}"><b>Inbox</b></c:when>
                <c:otherwise>Inbox</c:otherwise>
            </c:choose>
        </a></td>
        <td style="padding-right: 30px"><a href="/Document/Outbox">
            <c:choose>
                <c:when test="${folder == 'outbox'}"><b>Outbox</b></c:when>
                <c:otherwise>Outbox</c:otherwise>
            </c:choose>
        </a></td>
        <td style="padding-right: 30px">|</td>
        <td style="padding-right: 30px;"><a style="color: #FFCC66" href="javascript:ReCalcInbox()">Calculating inbox</a>
        </td>
        <td style="padding-right: 30px">|</td>
        <td style="padding-right: 30px;"><a href="/LoadTesting">Load testing</a></td>
    </tr>
</table>

<script>
    function ReCalcInbox() {
        $.ajax({
            type: "POST",
            url: "/Document/RecalcInbox",
            success: function (msg) {
                alert(msg);
            }
        });
    }
</script>