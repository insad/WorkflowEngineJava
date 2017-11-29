package wf.sample.controllers;

import optimajet.workflow.Designer;
import optimajet.workflow.core.util.StringUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesignerController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
            // note: why text/html?
            resp.setContentType("text/html; charset=utf-8");
        }
        if (res != null) {
            resp.getOutputStream().write(StringUtil.toUtf8Bytes(res));
        }
    }

    private InputStream getFileStream(HttpServletRequest req) throws IOException {
        try {
            if (ServletFileUpload.isMultipartContent(req)) {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletContext servletContext = this.getServletConfig().getServletContext();
                File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
                factory.setRepository(repository);
                ServletFileUpload upload = new ServletFileUpload(factory);
                List<FileItem> items = upload.parseRequest(req);
                if (items != null && !items.isEmpty()) {
                    return items.get(0).getInputStream();
                }
            }

            return null;
        } catch (FileUploadException e) {
            throw new IOException(e);
        }
    }
}