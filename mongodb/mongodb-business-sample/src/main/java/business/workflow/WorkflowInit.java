package business.workflow;

import business.models.Document;
import business.models.WorkflowInbox;
import business.persistence.PersistenceHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import optimajet.workflow.core.builder.IWorkflowBuilder;
import optimajet.workflow.core.builder.WorkflowBuilder;
import optimajet.workflow.core.bus.NullBus;
import optimajet.workflow.core.event.EventHandler;
import optimajet.workflow.core.parser.XmlWorkflowParser;
import optimajet.workflow.core.persistence.ProcessStatus;
import optimajet.workflow.core.provider.WorkflowDocumentProvider;
import optimajet.workflow.core.runtime.ProcessStatusChangedEventArgs;
import optimajet.workflow.core.runtime.TimerManager;
import optimajet.workflow.core.runtime.WorkflowRuntime;
import optimajet.workflow.core.runtime.WorkflowRuntimeBuilder;
import optimajet.workflow.core.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorkflowInit {
    private static final Object SYNC = new Object();
    private static volatile WorkflowRuntime runtime;

    private static IWorkflowBuilder getDefaultBuilder(WorkflowDocumentProvider provider) {
        return new WorkflowBuilder<>(provider, new XmlWorkflowParser(), provider)
                .withDefaultCache();
    }

    public static WorkflowRuntime getRuntime() {
        if (runtime == null) {
            synchronized (SYNC) {
                if (runtime == null) {
                    WorkflowDocumentProvider provider = PersistenceHelper.createProvider();
                    IWorkflowBuilder builder = getDefaultBuilder(provider);

                    runtime = WorkflowRuntimeBuilder
                            .create(UUID.fromString("8D38DB8F-F3D5-4F26-A989-4FDD40F32D9D"))
                            .withBuilder(builder)
                            .withActionProvider(new WorkflowActions())
                            .withRuleProvider(new WorkflowRule())
                            .withPersistenceProvider(provider)
                            .withTimerManager(new TimerManager())
                            .withBus(new NullBus())
                            .switchAutoUpdateSchemeBeforeGetAvailableCommandsOn()
                            .start()
                            .build();

                    runtime.getProcessStatusChanged().subscribe(new ProcessStatusChangedListener());
                }
            }
        }
        return runtime;
    }

    private static void preExecuteAndFillInbox(ProcessStatusChangedEventArgs e) {
        UUID processId = e.getProcessId();
        fillInbox(processId);
    }

    private static void fillInbox(UUID processId) {
        Collection<String> newActors = getRuntime().getAllActorsForDirectCommandTransitions(processId);
        List<WorkflowInbox> items = new ArrayList<>();
        for (String newActor : newActors) {
            WorkflowInbox workflowInbox = new WorkflowInbox();
            workflowInbox.setId(UUID.randomUUID());
            workflowInbox.setIdentityId(newActor);
            workflowInbox.setProcessId(processId);
            items.add(workflowInbox);
        }

        if (!items.isEmpty()) {
            PersistenceHelper.save(items);
        }
    }

    private static class ProcessStatusChangedListener implements EventHandler<ProcessStatusChangedEventArgs> {

        @Override
        public void handle(Object sender, ProcessStatusChangedEventArgs e) {
            if (e.getNewStatus() != ProcessStatus.Idled && e.getNewStatus() != ProcessStatus.Finalized) {
                return;
            }

            if (StringUtil.isNullOrEmpty(e.getSchemeCode())) {
                return;
            }

            WorkflowActions.deleteEmptyPreHistory(e.getProcessId());
            runtime.preExecuteFromCurrentActivity(e.getProcessId());

            // Inbox
            PersistenceHelper.deleteInboxForProcess(e);

            if (e.getNewStatus() != ProcessStatus.Finalized) {
                preExecuteAndFillInbox(e);
            }

            // Change state name
            Document document = PersistenceHelper.get(Document.class, e.getProcessId());
            if (document != null) {
                String nextState = getRuntime().getLocalizedStateName(e.getProcessId(), e.getProcessInstance().getCurrentState());
                document.setStateName(nextState);
                PersistenceHelper.save(document);
            }
        }
    }
}