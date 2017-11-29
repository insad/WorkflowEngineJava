<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script src="${pageContext.request.contextPath}/static/scripts/jquery.js"></script>
<script src="${pageContext.request.contextPath}/static/scripts/jquery-ui.js"></script>
<link href="${pageContext.request.contextPath}/static/content/themes/base/all.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/static/scripts/kinetic-v5.1.0.min.js"></script>
<%-- todo javascript ace--%>
<script src="${pageContext.request.contextPath}/static/scripts/ace.js"></script>
<link href="${pageContext.request.contextPath}/static/content/workflowdesigner.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/static/scripts/workflowdesigner.min.js"></script>
<script src="${pageContext.request.contextPath}/static/scripts/json5.js"></script>

<form action="" id="uploadform" method="post" enctype="multipart/form-data" onsubmit="tmp()">
    <table>
        <tr>
            <td></td>
            <td>
                <a href='javascript:DownloadScheme()'>Download scheme workflow</a>
            </td>
        </tr>
        <tr>
            <td>
                <input type="file" name="uploadFile" id="uploadFile" style="width:350px">
            </td>
            <td>
                <a href='javascript:UploadScheme()'>Upload scheme workflow</a>
            </td>
        </tr>
    </table>
</form>
<table>
    <tr>
        <td>Size:</td>
        <td><input id="graphwidth" value="1200"/> x <input id="graphheight" value="600"/></td>
        <td>
            <button onclick="wfdesignerRedraw()">Resize</button>
        </td>
        <td>
            |
        </td>
        <td>
            <button onclick="OnNew()">New</button>
        </td>
        <td>
            <button onclick="OnSave()">Save</button>
        </td>
        <td>
            <button onclick="StartProcess()">Start process</button>
        </td>

    </tr>
</table>
<br/>
<div id="wfdesigner"></div>

<script>
    var schemecode = 'SimpleWF';
    var wfdesigner = undefined;
    function wfdesignerRedraw() {
        var data;

        if (wfdesigner != undefined) {
            data = wfdesigner.data;
            wfdesigner.destroy();
        }

        wfdesigner = new WorkflowDesigner({
            name: 'simpledesigner',
            apiurl: 'Designer/API',
            renderTo: 'wfdesigner',
            imagefolder: 'images/',
            graphwidth: $('#graphwidth').val(),
            graphheight: $('#graphheight').val()
        });

        if (data == undefined) {
            var isreadonly = false;
            var p = {schemecode: schemecode, readonly: isreadonly};
            if (wfdesigner.exists(p))
                wfdesigner.load(p);
            else
                wfdesigner.create();
        }
        else {
            wfdesigner.data = data;
            wfdesigner.render();
        }
    }

    wfdesignerRedraw();

    function DownloadScheme() {
        wfdesigner.downloadscheme({schemecode: schemecode});
    }
    function UploadScheme() {
        var file = $('#uploadFile');
        if (file == undefined || file.val().length == 0) {
            alert('You did not select a file to upload!');
            return false;
        }

        wfdesigner.uploadscheme($('#uploadform')[0], function () {
            alert('The file is uploaded!');
        });
    }

    function OnSave() {
        wfdesigner.schemecode = schemecode;

        var err = wfdesigner.validate();
        if (err != undefined && err.length > 0) {
            alert(err);
        }
        else {
            wfdesigner.save(function () {
                alert('The scheme is saved!');
            });
        }
    }
    function StartProcess() {
        $.ajax('Workflow').then(
            function () {
                alert('Process started');
            },
            function (e) {
                alert('Error: ' + e);
            });
    }
</script>