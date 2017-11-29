package wf.sample.controllers;

import optimajet.workflow.core.builder.IWorkflowBuilder;
import optimajet.workflow.core.builder.WorkflowBuilder;
import optimajet.workflow.core.bus.NullBus;
import optimajet.workflow.core.parser.XmlWorkflowParser;
import optimajet.workflow.core.runtime.JavaEETimerManager;
import optimajet.workflow.core.runtime.WorkflowRuntime;
import optimajet.workflow.core.runtime.WorkflowRuntimeBuilder;
import optimajet.workflow.mysql.MySqlProvider;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.util.UUID;

@ApplicationScoped
public class Workflow {

    @Resource(lookup = "java:jboss/datasources/MySqlDS")
    private DataSource dataSource;
    @EJB
    private JavaEETimerManager javaEETimerManager;
    private WorkflowRuntime workflowRuntime;

    @PostConstruct
    private void initWorkflowRuntime() {
        MySqlProvider provider = new MySqlProvider(dataSource);
        IWorkflowBuilder workflowBuilder = new WorkflowBuilder<>(provider, new XmlWorkflowParser(), provider)
                .withDefaultCache();

        this.workflowRuntime = WorkflowRuntimeBuilder
                .create(UUID.fromString("8D38DB8F-F3D5-4F26-A989-4FDD40F32D9D"))
                .withBuilder(workflowBuilder)
                .withPersistenceProvider(provider)
                .withTimerManager(javaEETimerManager.getTimerManager())
                .withBus(new NullBus())
                .switchAutoUpdateSchemeBeforeGetAvailableCommandsOn()
                .start()
                .build();
    }

    WorkflowRuntime getRuntime() {
        return workflowRuntime;
    }
}