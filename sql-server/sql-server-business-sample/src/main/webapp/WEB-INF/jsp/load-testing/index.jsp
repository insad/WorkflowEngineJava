<%@ page import="wf.sample.models.LoadTestingStatisticItemModel" %>
<%@ page import="wf.sample.models.LoadTestingStatisticsModel" %>
<%@ page import="java.util.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    List<LoadTestingStatisticsModel> model =
            (List<LoadTestingStatisticsModel>) pageContext.getRequest().getAttribute("statistics");
    List<LoadTestingStatisticItemModel> statistics = LoadTestingStatisticsModel.getByType(model);
    Collections.sort(statistics, new Comparator<LoadTestingStatisticItemModel>() {
        @Override
        public int compare(LoadTestingStatisticItemModel o1, LoadTestingStatisticItemModel o2) {
            return o1.getType().compareTo(o2.getType());
        }
    });

    pageContext.setAttribute("statistics", statistics);

    Map<String, String> labels = new HashMap<>();
    labels.put("CreatingWorkflow", "Creating workflow of document");
    labels.put("CreatingDocument", "Creating document");
    labels.put("GetAvailableCommands", "Getting available commands of document for employee");
    labels.put("ExecuteCommand", "Executing command of workflow");

    pageContext.setAttribute("labels", labels);

    String valueType = request.getParameter("Type") != null ? request.getParameter("Type") : "";
    pageContext.setAttribute("valueType", valueType);

    List<String> types = new ArrayList<>();
    Collections.sort(model, new Comparator<LoadTestingStatisticsModel>() {
        @Override
        public int compare(LoadTestingStatisticsModel o1, LoadTestingStatisticsModel o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    });

    StringBuilder sb = new StringBuilder();
    for (LoadTestingStatisticsModel stat : model) {
        for (LoadTestingStatisticItemModel item : stat.getItems()) {
            if (!types.contains(item.getType())) {
                sb.append(String.format("data.%1$s = {}; data.%1$s.label = '%2$s'; data.%1$s.data = [];", item.getType(),
                        labels.containsKey(item.getType()) ? labels.get(item.getType()) : item.getType()));
                types.add(item.getType());
            }

            double value;
            switch (valueType) {
                case "1":
                    value = item.getMinDuration() != null ? item.getMinDuration() : 0;
                    break;
                case "2":
                    value = item.getMaxDuration() != null ? item.getMaxDuration() : 0;
                    break;
                default:
                    value = item.getAverageDuration();
                    break;
            }

            sb.append(String.format("data.%s.data.push([%d, %s]);", item.getType(), stat.getDate().getTime(),
                    String.format("%f", value).replace(',', '.')));
        }
    }

    pageContext.setAttribute("dataString", sb.toString());
%>

<t:genericpage pageTitle="Load testing">
    <jsp:body>
        <script src="${pageContext.request.contextPath}/scripts/jquery.flot.js" type="text/javascript"></script>
        <script src="${pageContext.request.contextPath}/scripts/jquery.flot.selection.js"
                type="text/javascript"></script>

        <div id="resultTop" style="color: tomato; font-size: 16px;"></div>

        <h2>Load testing</h2>

        <table>
            <tr>
                <td>Create document & process of workflow:</td>
                <td><input id="doccount" value="100"/></td>
            </tr>
            <tr>
                <td>Thread:</td>
                <td><input id="threadcount" value="1"/></td>
            </tr>
            <tr>
                <td>Execution of workflow command:</td>
                <td><input id="wfcommandcount" value="100"/></td>
            </tr>
            <tr>
                <td>Thread:</td>
                <td><input id="wfthreadcount" value="1"/></td>
            </tr>
        </table>

        <a class="button" href="javascript:LoadTestingRun()">Run!</a>
        <script>
            function LoadTestingRun() {
                var data = [];
                data.push({name: 'doccount', value: $('#doccount')[0].value});
                data.push({name: 'threadcount', value: $('#threadcount')[0].value});
                data.push({name: 'wfcommandcount', value: $('#wfcommandcount')[0].value});
                data.push({name: 'wfthreadcount', value: $('#wfthreadcount')[0].value});

                $('#resultTop').load('/LoadTesting/Run', data, function () {
                    setTimeout(function () {
                        $('#resultTop')[0].innerHTML = '';
                    }, 2000);
                });
            }

            function LoadTestingCleanStatistics() {
                var data = [];
                $('#resultTop').load('/LoadTesting/Clean', data, function () {
                    location.reload();
                });
            }
        </script>

        <h2>Statistics</h2>
        <a class="button" href="javascript:location.reload()">Refresh</a>
        <a class="button" href="javascript:LoadTestingCleanStatistics()">Clean</a>

        <table class="table" style="max-width:900px;">
            <tbody>
            <tr>
                <th></th>
                <th>Type of operation</th>
                <th>Average duration (milliseconds)</th>
                <th>Min duration (milliseconds)</th>
                <th>Max duration (milliseconds)</th>
                <th>Count of operation</th>
                <th>Sum Duration (milliseconds)</th>
            </tr>

            <c:forEach items="${statistics}" var="item">
            <tr>
                <td><input class="StatOperationType" type="checkbox" checked="checked" name="${item.type}"
                           onclick="GraphRedraw()"/></td>
                <td><c:choose>
                    <c:when test="${labels.containsKey(item.type)}">${labels.get(item.type)}</c:when>
                    <c:otherwise>${item.type}</c:otherwise>
                </c:choose></td>
                <td style="text-align:right"><fmt:formatNumber type="number" maxFractionDigits="0"
                                                               value="${item.averageDuration}"/></td>
                <td style="text-align:right"><c:if test="${item.minDuration != null}">
                    <fmt:formatNumber type="number" maxFractionDigits="0" value="${item.minDuration}"/> </c:if></td>
                <td style="text-align:right"><c:if test="${item.maxDuration != null}">
                    <fmt:formatNumber type="number" maxFractionDigits="0" value="${item.maxDuration}"/> </c:if></td>
                <td>${item.count}</td>
                <td style="text-align:right"><fmt:formatNumber type="number" maxFractionDigits="0"
                                                               value="${item.duration}"/></td>
            </tr>
            </c:forEach>
        </table>
        <br/><br/>

        Unit:
        <select id="selectionGraphUnit" onchange="GraphSettingChange()">
            <option value="1">1 seconds</option>
            <option value="5">5 seconds</option>
            <option value="10">10 seconds</option>
            <option value="30">30 seconds</option>
            <option value="60">1 minute</option>
            <option value="300">5 minutes</option>
            <option value="600">10 minutes</option>
            <option value="1800">30 minutes</option>
            <option value="3600">1 hour</option>
            <option value="43200">12 hours</option>
            <option value="86400">1 day</option>
        </select>

        Type:
        <select id="selectionGraphType" onchange="GraphSettingChange()">
            <option value="0">Average duration</option>
            <option value="1">Min duration</option>
            <option value="2">Max duration</option>
        </select>

        <script>
            function GraphSettingChange() {
                var unit = $('#selectionGraphUnit')[0].value;
                var type = $('#selectionGraphType')[0].value;
                location.href = '/LoadTesting?GraphUnit=' + unit + "&Type=" + type;
            }
        </script>

        <form>
            <fieldset>
                <label></label>
                <div>
                    <div id="placeholder" style="width:600px;height:400px; margin:10px;"></div>
                    <p id="choices" style="float:right; width:135px;"></p>
                    <div id="overview" style="margin-left:60px;margin-top:20px;width:500px;height:50px;"></div>
                </div>
            </fieldset>
        </form>

        <script id="source">
            var data = {};
            ${dataString}

            function GraphRedraw() {
                var types = $('input.StatOperationType:checked');
                var dataArray = [];
                for (var i = 0; i < types.length; i++)
                    dataArray.push(data[types[i].name]);

                function weekendAreas(axes) {
                    var markings = [];
                    var d = new Date(axes.xaxis.min);
                    d.setUTCDate(d.getUTCDate() - ((d.getUTCDay() + 1) % 7));
                    d.setUTCSeconds(0);
                    d.setUTCMinutes(0);
                    d.setUTCHours(0);

                    var unit = $('#selectionGraphUnit')[0].value;
                    var i = d.getTime();
                    do {
                        markings.push({xaxis: {from: i, to: i + 2 * 24 * 60 * 60 * 1000}});
                        i += 7 * 24 * 60 * 60 * 1000;
                    } while (i < axes.xaxis.max);

                    return markings;
                }

                var options = {
                    xaxis: {mode: "time", tickLength: 5},
                    selection: {mode: "x"},
                    grid: {
                        markings: weekendAreas,
                        hoverable: true,
                        clickable: true
                    }
                };

                var plot = $.plot($("#placeholder"), dataArray, options);

                var overview = $.plot($("#overview"), dataArray, {
                    series: {
                        lines: {show: true, lineWidth: 1},
                        shadowSize: 0
                    },
                    xaxis: {ticks: [], mode: "time"},
                    yaxis: {ticks: [], min: 0, autoscaleMargin: 0.1},
                    selection: {mode: "x"},
                    legend: {show: false}
                });

                $("#placeholder").bind("plotselected", function (event, ranges) {
                    plot = $.plot($("#placeholder"), dataArray,
                        $.extend(true, {}, options, {
                            xaxis: {min: ranges.xaxis.from, max: ranges.xaxis.to}
                        }));
                    overview.setSelection(ranges, true);
                });

                $("#overview").bind("plotselected", function (event, ranges) {
                    plot.setSelection(ranges);
                });


                $("#placeholder").bind("plothover", function (event, pos, item) {
                    if (item) {
                        var x = item.datapoint[0].toFixed(0),
                            y = item.datapoint[1].toFixed(0);

                        $("#tooltip").html("Duration: " + y + "ms")
                            .css({top: item.pageY + 5, left: item.pageX + 5})
                            .fadeIn(200);
                    } else {
                        $("#tooltip").hide();
                    }

                });
            }

            function getQueryParams(qs) {
                qs = qs.split("+").join(" ");

                var params = {}, tokens,
                    re = /[?&]?([^=]+)=([^&]*)/g;

                while (tokens = re.exec(qs)) {
                    params[decodeURIComponent(tokens[1])]
                        = decodeURIComponent(tokens[2]);
                }

                return params;
            }

            $(function () {
                var unit = getQueryParams(location.search).GraphUnit;
                if (unit == undefined)
                    unit = 60;

                var type = getQueryParams(location.search).Type;
                if (type == undefined)
                    type = 0;

                $("<div id='tooltip'></div>").css({
                    position: "absolute",
                    display: "none",
                    border: "1px solid #fdd",
                    padding: "2px",
                    "background-color": "#fee",
                    opacity: 0.80
                }).appendTo("body");

                $('#selectionGraphUnit')[0].value = unit;
                $('#selectionGraphType')[0].value = type;

                GraphRedraw();
            });
        </script>
    </jsp:body>
</t:genericpage>