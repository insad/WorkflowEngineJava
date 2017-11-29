package wf.sample.loadtest;

import business.models.Employee;
import business.models.WorkflowInbox;
import business.repository.LoadTestingOperationModelRepository;
import business.repository.WorkflowInboxRepository;
import business.workflow.WorkflowInit;
import lombok.extern.slf4j.Slf4j;
import optimajet.workflow.core.runtime.WorkflowCommand;
import optimajet.workflow.core.util.CollectionUtil;
import optimajet.workflow.core.util.UUIDUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@Slf4j
public class CommandExecutor {
    private final LoadTestingOperationModelRepository loadTestingOperationModelRepository;
    private final WorkflowInboxRepository workflowInboxRepository;

    public CommandExecutor(LoadTestingOperationModelRepository loadTestingOperationModelRepository,
                           WorkflowInboxRepository workflowInboxRepository) {
        this.loadTestingOperationModelRepository = loadTestingOperationModelRepository;
        this.workflowInboxRepository = workflowInboxRepository;
    }

    boolean executeOneCommand(Employee employee, Random r) {
        UUID docId = null;
        int inboxCount = workflowInboxRepository.countByEmployee(employee);
        if (inboxCount > 0) {
            WorkflowInbox tmp = getRandomWorkflowInbox(r, employee, inboxCount);
            if (tmp != null) {
                docId = tmp.getProcessId();
            }

            if (docId != null) {
                Date opStart = new Date();
                List<WorkflowCommand> commands = getWorkflowCommands(employee, docId, opStart);

                if (!commands.isEmpty()) {
                    WorkflowCommand c = commands.get(r.nextInt(commands.size()));
                    c.setParameter("Comment", "Load testing. ExecuteCommand");

                    opStart = new Date();
                    String userId = UUIDUtil.asString(employee.getId());

                    WorkflowInit.getRuntime().executeCommand(c, userId, userId);
                    LoadTestUtil.addOperation(opStart, new Date(), "ExecuteCommand", loadTestingOperationModelRepository);
                    return true;
                }
            }
        }
        return false;
    }

    private List<WorkflowCommand> getWorkflowCommands(Employee employee, UUID docId, Date opStart) {
        List<WorkflowCommand> commands = new ArrayList<>(WorkflowInit.getRuntime()
                .getAvailableCommands(docId, UUIDUtil.asString(employee.getId())));
        LoadTestUtil.addOperation(opStart, new Date(), "GetAvailableCommands", loadTestingOperationModelRepository);
        return commands;
    }

    private WorkflowInbox getRandomWorkflowInbox(Random r, Employee employee, int inboxCount) {
        Page<WorkflowInbox> workflowInboxPage = workflowInboxRepository.findByEmployee(employee,
                new PageRequest(r.nextInt(inboxCount), 1));
        return CollectionUtil.firstOrDefault(workflowInboxPage);
    }

}
