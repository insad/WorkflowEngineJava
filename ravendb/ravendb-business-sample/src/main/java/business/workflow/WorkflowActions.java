package business.workflow;

import business.helpers.EmployeeHelper;
import business.models.Document;
import business.models.DocumentTransitionHistory;
import business.models.Employee;
import business.persistence.PersistenceHelper;
import optimajet.workflow.core.model.ProcessInstance;
import optimajet.workflow.core.runtime.IWorkflowActionProvider;
import optimajet.workflow.core.runtime.WorkflowRuntime;
import optimajet.workflow.core.util.CollectionUtil;
import optimajet.workflow.core.util.StringUtil;
import optimajet.workflow.core.util.UUIDUtil;

import java.util.*;

public class WorkflowActions implements IWorkflowActionProvider {
    private static final Map<String, TransitionHistoryAction> ACTIONS;
    private static final Map<String, WorkflowCondition> CONDITIONS = new HashMap<>();

    static {
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
        ACTIONS = Collections.unmodifiableMap(map);
    }

    private static void writeTransitionHistory(ProcessInstance processInstance) {
        if (processInstance.getIdentityIds() == null) {
            return;
        }

        String currentState = WorkflowInit.getRuntime().getLocalizedStateName(processInstance.getProcessId(), processInstance.getCurrentState());
        String nextState = WorkflowInit.getRuntime().getLocalizedStateName(processInstance.getProcessId(), processInstance.getExecutedActivityState());
        String command = WorkflowInit.getRuntime().getLocalizedCommandName(processInstance.getProcessId(), processInstance.getCurrentCommand());
        Document document = PersistenceHelper.get(Document.class, processInstance.getProcessId());

        if (document != null) {
            DocumentTransitionHistory documentTransitionHistory = new DocumentTransitionHistory();
            documentTransitionHistory.setId(UUID.randomUUID());
            documentTransitionHistory.setAllowedToEmployeeNames(getEmployeesString(processInstance.getIdentityIds()));
            documentTransitionHistory.setDestinationState(nextState);
            documentTransitionHistory.setInitialState(currentState);
            documentTransitionHistory.setCommand(command);
            document.getTransitionHistories().add(documentTransitionHistory);
            PersistenceHelper.save(document);
        }
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

    private static void updateTransitionHistory(ProcessInstance processInstance) {
        final String currentState = WorkflowInit.getRuntime().getLocalizedStateName(processInstance.getProcessId(), processInstance.getCurrentState());
        final String nextState = WorkflowInit.getRuntime().getLocalizedStateName(processInstance.getProcessId(), processInstance.getExecutedActivityState());
        String command = WorkflowInit.getRuntime().getLocalizedCommandName(processInstance.getProcessId(), processInstance.getCurrentCommand());
        boolean isTimer = !StringUtil.isNullOrEmpty(processInstance.getExecutedTimer());

        Document document = PersistenceHelper.get(Document.class, processInstance.getProcessId());
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
            document.getTransitionHistories().add(historyItem);
        }

        historyItem.setCommand(!isTimer ? command : String.format("Timer: %s", processInstance.getExecutedTimer()));
        historyItem.setTransitionTime(new Date());

        if (StringUtil.isNullOrWhiteSpace(processInstance.getIdentityId())) {
            historyItem.setEmployeeId(null);
            historyItem.setEmployeeName(StringUtil.EMPTY);
        } else {
            historyItem.setEmployeeId(UUIDUtil.fromString(processInstance.getIdentityId()));
            final UUID employeeId = historyItem.getEmployeeId();
            Employee employee = CollectionUtil.first(EmployeeHelper.getEmployeeCache(), new CollectionUtil.ItemCondition<Employee>() {
                @Override
                public boolean check(Employee c) {
                    return c.getId().equals(employeeId);
                }
            });
            historyItem.setEmployeeName(employee.getName());
        }

        PersistenceHelper.save(document);
    }

    static void deleteEmptyPreHistory(UUID processId) {
        Document document = PersistenceHelper.get(Document.class, processId);
        if (document != null) {
            Collection<DocumentTransitionHistory> items = CollectionUtil.where(document.getTransitionHistories(),
                    new CollectionUtil.ItemCondition<DocumentTransitionHistory>() {
                        @Override
                        public boolean check(DocumentTransitionHistory dth) {
                            return dth.getTransitionTime() == null;
                        }
                    });
            document.getTransitionHistories().removeAll(items);
            PersistenceHelper.save(document);
        }
    }

    public void executeAction(String name, ProcessInstance processInstance, WorkflowRuntime runtime, String actionParameter) {
        TransitionHistoryAction transitionHistoryAction = ACTIONS.get(name);
        if (transitionHistoryAction != null) {
            transitionHistoryAction.action(processInstance, actionParameter);
            return;
        }

        throw new UnsupportedOperationException(String.format("Action with name %s not implemented", name));
    }

    public boolean executeCondition(String name, ProcessInstance processInstance, WorkflowRuntime runtime, String actionParameter) {
        WorkflowCondition workflowCondition = CONDITIONS.get(name);
        if (workflowCondition != null) {
            return workflowCondition.check(processInstance, actionParameter);
        }

        throw new UnsupportedOperationException(String.format("Action condition with name %s not implemented", name));
    }

    public List<String> getActions() {
        return new ArrayList<>(ACTIONS.keySet());
    }

    @Override
    public List<String> getConditions() {
        return new ArrayList<>(CONDITIONS.keySet());
    }

    private interface TransitionHistoryAction {
        void action(ProcessInstance processInstance, String parameter);
    }

    private interface WorkflowCondition {
        boolean check(ProcessInstance processInstance, String parameter);
    }
}
