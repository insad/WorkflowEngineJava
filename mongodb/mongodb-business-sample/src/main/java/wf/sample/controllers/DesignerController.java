package wf.sample.controllers;


import business.workflow.WorkflowInit;
import optimajet.workflow.Designer;
import optimajet.workflow.core.util.CollectionUtil;
import optimajet.workflow.core.util.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DesignerController {

    @GetMapping("Designer")
    public String index(@PathVariable(value = "schemeName", required = false) String schemeName,
                        Map<String, Object> model) {
        model.put("SchemeName", schemeName);
        return "designer/index";
    }

    @RequestMapping(value = "Designer/API", method = {RequestMethod.GET, RequestMethod.POST})
    public void api(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String> pars = new HashMap<>();
        for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
            String value = entry.getValue() == null || entry.getValue().length == 0 ? null : entry.getValue()[0];
            pars.put(entry.getKey(), value);
        }

        InputStream fileStream = getFileStream(req);
        String res = Designer.designerAPI(WorkflowInit.getRuntime(), pars, fileStream, true);
        if (pars.get("operation").equalsIgnoreCase("downloadscheme")) {
            resp.setHeader("Content-disposition", "attachment; filename=scheme.xml");
            resp.setContentType("text/xml");
        } else {
            resp.setContentType("text/html; charset=utf-8");
        }
        if (res != null) {
            resp.getOutputStream().write(StringUtil.toUtf8Bytes(res));
        }
    }

    private InputStream getFileStream(HttpServletRequest req) throws IOException {
        if (req instanceof StandardMultipartHttpServletRequest) {
            StandardMultipartHttpServletRequest request = (StandardMultipartHttpServletRequest) req;
            List<MultipartFile> multipartFiles = CollectionUtil.firstOrDefault(request.getMultiFileMap().values());
            if (multipartFiles == null) {
                return null;
            }

            MultipartFile multipartFile = CollectionUtil.firstOrDefault(multipartFiles);
            if (multipartFile == null) {
                return null;
            }
            return multipartFile.getInputStream();
        }
        return null;
    }
}