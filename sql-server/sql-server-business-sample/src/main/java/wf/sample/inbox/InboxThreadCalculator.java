package wf.sample.inbox;

import business.models.Document;
import business.repository.DocumentRepository;
import business.workflow.WorkflowInit;
import lombok.extern.slf4j.Slf4j;
import optimajet.workflow.core.runtime.WorkflowRuntime;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Component
@Scope("prototype")
@Slf4j
public class InboxThreadCalculator extends Thread {
    private final PlatformTransactionManager platformTransactionManager;
    private final DocumentRepository documentRepository;
    private final InboxCalculator inboxCalculator;

    public InboxThreadCalculator(PlatformTransactionManager platformTransactionManager,
                                 DocumentRepository documentRepository, InboxCalculator inboxCalculator) {
        this.platformTransactionManager = platformTransactionManager;
        this.documentRepository = documentRepository;
        this.inboxCalculator = inboxCalculator;
    }

    @Override
    public void run() {
        recalcInbox();
    }

    public void recalcInbox() {
        Iterable<Document> documents = documentRepository.findAll();
        WorkflowRuntime runtime = WorkflowInit.getRuntime();

        for (Document document : documents) {
            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
            definition.setName("InboxCalculator");
            definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            TransactionStatus transaction = platformTransactionManager.getTransaction(definition);
            try {
                inboxCalculator.recalcDocument(runtime, document.getId());
                platformTransactionManager.commit(transaction);
            } catch (Exception e) {
                log.error("Unable to calculate the inbox for process Id = {}", document.getId(), e);
                platformTransactionManager.rollback(transaction);
            }
        }
    }
}
