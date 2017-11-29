package wf.sample.controllers;

import optimajet.workflow.core.builder.IWorkflowBuilder;
import optimajet.workflow.core.builder.WorkflowBuilder;
import optimajet.workflow.core.bus.NullBus;
import optimajet.workflow.core.parser.XmlWorkflowParser;
import optimajet.workflow.core.provider.WorkflowDocumentProvider;
import optimajet.workflow.core.runtime.TimerManager;
import optimajet.workflow.core.runtime.WorkflowRuntime;
import optimajet.workflow.core.runtime.WorkflowRuntimeBuilder;

import java.util.UUID;

class WorkflowInit {
    private static final Object SYNC = new Object();
    private static volatile WorkflowRuntime runtime;

    static WorkflowRuntime getRuntime() {
        if (runtime == null) {
            synchronized (SYNC) {
                if (runtime == null) {
                    WorkflowDocumentProvider provider = ProviderHelper.getProvider();
                    IWorkflowBuilder workflowBuilder = new WorkflowBuilder<>(provider, new XmlWorkflowParser(), provider)
                            .withDefaultCache();

                    runtime = WorkflowRuntimeBuilder
                            .create(UUID.fromString("8D38DB8F-F3D5-4F26-A989-4FDD40F32D9D"))
                            .withBuilder(workflowBuilder)
                            .withPersistenceProvider(provider)
                            .withTimerManager(new TimerManager())
                            .withBus(new NullBus())
                            .switchAutoUpdateSchemeBeforeGetAvailableCommandsOn()
                            .start()
                            .build();
                }
            }
        }
        return runtime;
    }
}