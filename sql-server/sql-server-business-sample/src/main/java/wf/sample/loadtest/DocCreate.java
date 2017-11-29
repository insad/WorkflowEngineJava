package wf.sample.loadtest;

import business.helpers.EmployeeHelper;
import business.models.Employee;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.Random;

@Component
@Scope("prototype")
@Slf4j
public class DocCreate extends Thread {

    private final DocumentCreator documentCreator;
    private final PlatformTransactionManager platformTransactionManager;
    @Setter
    private int docCount;

    public DocCreate(DocumentCreator documentCreator, PlatformTransactionManager platformTransactionManager) {
        this.documentCreator = documentCreator;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Override
    public void run() {
        List<Employee> employees = EmployeeHelper.getAll();
        Random random = new Random();

        for (int i = 0; i < docCount; i++) {
            DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
            definition.setName("createDocument-" + i);
            definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            TransactionStatus transaction = platformTransactionManager.getTransaction(definition);
            try {
                documentCreator.createDocumentAndWorkflow(employees, random);
                platformTransactionManager.commit(transaction);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                platformTransactionManager.rollback(transaction);
            }
        }
    }
}
