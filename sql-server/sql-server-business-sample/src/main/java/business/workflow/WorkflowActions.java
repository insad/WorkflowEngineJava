package business.workflow;

import business.helpers.EmployeeHelper;
import business.models.Document;
import business.models.DocumentTransitionHistory;
import business.models.Employee;
import business.repository.DocumentRepository;
import business.repository.DocumentTransitionHistoryRepository;
import optimajet.workflow.core.model.ProcessInstance;
import optimajet.workflow.core.runtime.IWorkflowActionProvider;
import optimajet.workflow.core.runtime.WorkflowRuntime;
import optimajet.workflow.core.util.CollectionUtil;
import optimajet.workflow.core.util.StringUtil;
import optimajet.workflow.core.util.UUIDUtil;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WorkflowActions implements IWorkflowActionProvider {
    private final DocumentRepository documentRepository;
    private final DocumentTransitionHistoryRepository documentTransitionHistoryRepository;
    private final Map<String, TransitionHistoryAction> actions;
    private final Map<String, WorkflowCondition> conditions = new HashMap<>();

    public WorkflowActions(DocumentRepository documentRepository,
                           DocumentTransitionHistoryRepository documentTransitionHistoryRepository) {
        this.documentRepository = documentRepository;
        this.documentTransitionHistoryRepository = documentTransitionHistoryRepository;
        Map<String, TransitionHistoryAction> map = new HashMap<>();
        map.put("WriteTransitionHistory", new TransitionHistoryAction() {
            @Override
            public void action(ProcessInstance processInstance, String parameter) {
                writeTransitionHistory(processInstance);
            }
        });
        map.put("UpdateTransitionHistory", new TransitionHistoryAction() {
            @Override
            public void action(ProcessInstance processInstance, String parameter) {
                updateTransitionHistory(processInstance);
            }
        });
        this.actions = Collections.unmodifiableMap(map);
    }

    private static String getEmployeesString(final Collection<String> identities) {
        Collection<Employee> employees = CollectionUtil.where(EmployeeHelper.getEmployeeCache(),
                new CollectionUtil.ItemCondition<Employee>() {
                    @Override
                    public boolean check(Employee c) {
                        return identities.contains(UUIDUtil.asString(c.getId()));
                    }
                });

        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (Employee employee : employees) {
            if (!isFirst) {
                sb.append(',');
            }
            isFirst = false;

            sb.append(employee.getName());
        }

        return sb.toString();
    }

    private void writeTransitionHistory(ProcessInstance processInstance) {
        if (processInstance.getIdentityIds() == null) {
            return;
        }

        String currentState = WorkflowInit.getRuntime().getLocalizedStateName(processInstance.getProcessId(), processInstance.getCurrentState());
        String nextState = WorkflowInit.getRuntime().getLocalizedStateName(processInstance.getProcessId(), processInstance.getExecutedActivityState());
        String command = WorkflowInit.getRuntime().getLocalizedCommandName(processInstance.getProcessId(), processInstance.getCurrentCommand());
        Document document = documentRepository.findOne(processInstance.getProcessId());

        if (document != null) {
            DocumentTransitionHistory documentTransitionHistory = new DocumentTransitionHistory();
            documentTransitionHistory.setId(UUID.randomUUID());
            documentTransitionHistory.setAllowedToEmployeeNames(getEmployeesString(processInstance.getIdentityIds()));
            documentTransitionHistory.setDestinationState(nextState);
            documentTransitionHistory.setInitialState(currentState);
            documentTransitionHistory.setCommand(command);
            documentTransitionHistory.setDocument(document);
            if (document.getTransitionHistories() == null) {
                document.setTransitionHistories(new ArrayList<DocumentTransitionHistory>());
            }
            document.getTransitionHistories().add(documentTransitionHistory);
            documentTransitionHistoryRepository.save(documentTransitionHistory);
        }
    }

    private void updateTransitionHistory(ProcessInstance processInstance) {
        final String currentState = WorkflowInit.getRuntime().getLocalizedStateName(processInstance.getProcessId(), processInstance.getCurrentState());
        final String nextState = WorkflowInit.getRuntime().getLocalizedStateName(processInstance.getProcessId(), processInstance.getExecutedActivityState());
        String command = WorkflowInit.getRuntime().getLocalizedCommandName(processInstance.getProcessId(), processInstance.getCurrentCommand());
        boolean isTimer = !StringUtil.isNullOrEmpty(processInstance.getExecutedTimer());

        Document document = documentRepository.findOne(processInstance.getProcessId());
        if (document == null) {
            return;
        }

        DocumentTransitionHistory historyItem = CollectionUtil.firstOrDefault(document.getTransitionHistories(),
                new CollectionUtil.ItemCondition<DocumentTransitionHistory>() {
                    @Override
                    public boolean check(DocumentTransitionHistory h) {
                        return h.getTransitionTime() == null
                                && h.getInitialState().equals(currentState)
                                && h.getDestinationState().equals(nextState);
                    }
                });

        if (historyItem == null) {
            historyItem = new DocumentTransitionHistory();
            historyItem.setId(UUID.randomUUID());
            historyItem.setAllowedToEmployeeNames(StringUtil.EMPTY);
            historyItem.setDestinationState(nextState);
            historyItem.setInitialState(currentState);
            historyItem.setDocument(document);
            if (document.getTransitionHistories() == null) {
                document.setTransitionHistories(new ArrayList<DocumentTransitionHistory>());
            }
            document.getTransitionHistories().add(historyItem);
        }

        historyItem.setCommand(!isTimer ? command : String.format("Timer: %s", processInstance.getExecutedTimer()));
        historyItem.setTransitionTime(new Date());

        if (StringUtil.isNullOrWhiteSpace(processInstance.getIdentityId())) {
            historyItem.setEmployee(null);
        } else {
            historyItem.setEmployee(EmployeeHelper.getEmployee(UUIDUtil.fromString(processInstance.getIdentityId())));
        }
        documentTransitionHistoryRepository.save(historyItem);
    }

    void deleteEmptyPreHistory(UUID processId) {
        documentTransitionHistoryRepository.deleteEmptyPreHistory(processId);
    }

    public void executeAction(String name, ProcessInstance processInstance, WorkflowRuntime runtime, String actionParameter) {
        TransitionHistoryAction transitionHistoryAction = actions.get(name);
        if (transitionHistoryAction != null) {
            transitionHistoryAction.action(processInstance, actionParameter);
            return;
        }

        throw new UnsupportedOperationException(String.format("Action with name %s not implemented", name));
    }

    public boolean executeCondition(String name, ProcessInstance processInstance, WorkflowRuntime runtime, String actionParameter) {
        WorkflowCondition workflowCondition = conditions.get(name);
        if (workflowCondition != null) {
            return workflowCondition.check(processInstance, actionParameter);
        }

        throw new UnsupportedOperationException(String.format("Action condition with name %s not implemented", name));
    }

    public List<String> getActions() {
        return new ArrayList<>(actions.keySet());
    }

    @Override
    public List<String> getConditions() {
        return new ArrayList<>(conditions.keySet());
    }

    private interface TransitionHistoryAction {
        void action(ProcessInstance processInstance, String parameter);
    }

    private interface WorkflowCondition {
        boolean check(ProcessInstance processInstance, String parameter);
    }
}
