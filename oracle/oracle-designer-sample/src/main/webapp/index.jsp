
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title></title>
</head>
<body>
    <script src="scripts/konva.min.js" type="text/javascript"></script>
    <script src="scripts/jquery.js" type="text/javascript"></script>
    <script src="scripts/jquery-ui.js" type="text/javascript"></script>
    <link href="content/themes/base/jquery-ui.min.css" rel="stylesheet" type="text/css" />
    <link href="content/workflowdesigner.css" rel="stylesheet" type="text/css" />
    <script src="scripts/workflowdesigner.min.js" type="text/javascript"></script>
    <script src="scripts/ace.js" type="text/javascript"></script>
    <script src="scripts/json5.js" type="text/javascript"></script>

    <style>
        a.button15 {
            display: inline-block;
            font-family: arial,sans-serif;
            font-size: 14px;
            font-weight: bold;
            color: rgb(68,68,68);
            text-decoration: none;
            user-select: none;
            padding: .2em 1.2em;
            outline: none;
            border: 1px solid rgba(0,0,0,.1);
            border-radius: 2px;
            background: rgb(245,245,245) linear-gradient(#f4f4f4, #f1f1f1);
            transition: all .218s ease 0s;
        }

            a.button15:hover {
                color: rgb(24,24,24);
                border: 1px solid rgb(198,198,198);
                background: #f7f7f7 linear-gradient(#f7f7f7, #f1f1f1);
                box-shadow: 0 1px 2px rgba(0,0,0,.1);
            }

            a.button15:active {
                color: rgb(51,51,51);
                border: 1px solid rgb(204,204,204);
                background: rgb(238,238,238) linear-gradient(rgb(238,238,238), rgb(224,224,224));
                box-shadow: 0 1px 2px rgba(0,0,0,.1) inset;
            }

        a.button15 > table {
            height: 38px;
        }
    </style>

    <form action="" id="uploadform" method="post" enctype="multipart/form-data" onsubmit="tmp()" style="padding-bottom: 8px;">
        <table>
            <tr>
                <td>
                    <a class="button15" href='javascript:OnNew()' style="text-decoration: none;">
                        <table>
                            <tr style="text-align:center">
                                <td></td>
                                <td>New scheme</td>
                            </tr>
                        </table>
                    </a>
                </td>
                <td>
                    <a class="button15" href='javascript:OnSave()' style="text-decoration: none;">
                        <table>
                            <tr style="text-align:center">
                                <td></td>
                                <td>Save scheme</td>
                            </tr>
                        </table>
                    </a>
                </td>
                <td>
                    <a class="button15" href='javascript:DownloadScheme()' style="text-decoration: none;">
                        <table>
                            <tr style="text-align:center">
                                <td>
                                    <img height="24px" src="images/download.png" title="Click on this for download the workflow scheme" />

                                </td>
                                <td>Download XML</td>
                            </tr>
                        </table>
                    </a>
                </td>
                <td>
                    <a class="button15" href='javascript:SelectScheme()' style="text-decoration: none;">
                        <table>
                            <tr style="text-align:center">
                                <td>
                                    <img height="24px" src="images/upload.png" title="Click on this for upload a workflow scheme" />
                                </td>
                                <td>Upload XML</td>
                            </tr>
                        </table>
                    </a>
                </td>

            </tr>
        </table>
        <input type="file" name="uploadFile" id="uploadFile" style="display:none" onchange="javascript: UploadScheme();">
    </form>
    <div id="wfdesigner"></div>

    <script>
    var QueryString = function () {
        // This function is anonymous, is executed immediately and
        // the return value is assigned to QueryString!
        var query_string = {};
        var query = window.location.search.substring(1);
        var vars = query.split("&");
        for (var i = 0; i < vars.length; i++) {
            var pair = vars[i].split("=");
            // If first entry with this name
            if (typeof query_string[pair[0]] === "undefined") {
                query_string[pair[0]] = pair[1];
                // If second entry with this name
            } else if (typeof query_string[pair[0]] === "string") {
                var arr = [query_string[pair[0]], pair[1]];
                query_string[pair[0]] = arr;
                // If third or later entry with this name
            } else {
                query_string[pair[0]].push(pair[1]);
            }
        }
        return query_string;
    }();

    var schemecode = 'SimpleWF';
    var processid = QueryString.processid;
    var graphwidth = 1200;
    var graphheight = 600;

    var wfdesigner = undefined;
    function wfdesignerRedraw() {
        var data;

        if (wfdesigner != undefined) {
            data = wfdesigner.data;
            wfdesigner.destroy();
        }

        WorkflowDesignerConstants.FormMaxHeight = 600;
        wfdesigner = new WorkflowDesigner({
            name: 'simpledesigner',
            apiurl: 'Designer/API',
            renderTo: 'wfdesigner',
            imagefolder: 'images/',
            graphwidth: graphwidth,
            graphheight: graphheight
        });

        if (data == undefined) {
            var isreadonly = false;
            if (processid != undefined && processid != '')
                isreadonly = true;

            var p = { schemecode: schemecode, processid: processid, readonly: isreadonly };
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

    $(window).resize(function () {
        var w = $(window).width();
        var h = $(window).height();

        if (w > 300)
            graphwidth = w - 20;

        if (h > 300)
            graphheight = h - 90;

        wfdesignerRedraw();
    })

    $(window).resize();

    function DownloadScheme(){
        wfdesigner.downloadscheme({ schemecode: schemecode });
    }
    function SelectScheme() {
        var file = $('#uploadFile');
        file.trigger('click');
    }

    function UploadScheme() {
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
    function OnNew() {
        wfdesigner.create();
    }
    </script>

</body>
</html>

