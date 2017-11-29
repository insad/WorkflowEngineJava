package wf.sample.controllers;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@WebServlet("/Workflow")
public class WorkflowController extends HttpServlet {

    private final Workflow workflow;

    @Inject
    public WorkflowController(Workflow workflow) {
        this.workflow = workflow;
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws ServletException, IOException {
        UUID processId = UUID.randomUUID();
        workflow.getRuntime().createInstance("SimpleWF", processId);

        httpServletResponse.getOutputStream().write("Ok!".getBytes(StandardCharsets.UTF_8));
    }
}
