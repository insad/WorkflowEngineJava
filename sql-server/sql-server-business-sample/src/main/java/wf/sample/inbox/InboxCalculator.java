package wf.sample.inbox;

import business.repository.WorkflowInboxRepository;
import business.workflow.WorkflowInit;
import optimajet.workflow.core.runtime.WorkflowRuntime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class InboxCalculator {

    private final WorkflowInboxRepository workflowInboxRepository;

    public InboxCalculator(WorkflowInboxRepository workflowInboxRepository) {
        this.workflowInboxRepository = workflowInboxRepository;
    }

    void recalcDocument(WorkflowRuntime runtime, UUID id) {
        if (runtime.isProcessExists(id)) {
            runtime.updateSchemeIfObsolete(id);
            workflowInboxRepository.deleteByProcessId(id);
            WorkflowInit.fillInbox(id, workflowInboxRepository);
        }
    }
}
