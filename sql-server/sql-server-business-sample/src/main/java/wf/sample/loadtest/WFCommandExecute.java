package wf.sample.loadtest;

import business.models.Employee;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import optimajet.workflow.core.fault.CommandNotValidForStateException;
import optimajet.workflow.core.fault.ImpossibleToSetStatusException;
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
public class WFCommandExecute extends Thread {
    private final CommandExecutor commandExecutor;
    private final PlatformTransactionManager platformTransactionManager;
    @Setter
    private int wfCommandCount;
    @Setter
    private List<Employee> employees;

    public WFCommandExecute(CommandExecutor commandExecutor, PlatformTransactionManager platformTransactionManager) {
        this.commandExecutor = commandExecutor;
        this.platformTransactionManager = platformTransactionManager;
    }

    @Override
    public void run() {
        Random r = new Random();

        for (int i = 0; i < wfCommandCount; ) {
            int oldI = i;
            for (int k = 0; k < employees.size() - 1; k++) {
                Employee employee = employees.get(k);
                if (executeOneCommand(r, employee, i)) {
                    i++;
                    break;
                }
            }

            if (oldI == i) {
                break;
            }
        }
    }

    private boolean executeOneCommand(Random r, Employee employee, int i) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setName("executeCommand-" + i);
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transaction = platformTransactionManager.getTransaction(definition);
        try {
            boolean result = commandExecutor.executeOneCommand(employee, r);
            platformTransactionManager.commit(transaction);
            return result;
        } catch (CommandNotValidForStateException | ImpossibleToSetStatusException ignored) {
            // If process is changed state then ignore it's
            // If process is Running then ignore it's
            platformTransactionManager.rollback(transaction);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            platformTransactionManager.rollback(transaction);
        }
        return false;
    }
}
