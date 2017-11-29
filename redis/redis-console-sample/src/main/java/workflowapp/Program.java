package workflowapp;

import optimajet.workflow.core.runtime.WorkflowCommand;
import optimajet.workflow.core.runtime.WorkflowState;
import optimajet.workflow.core.util.CollectionUtil;
import optimajet.workflow.core.util.CultureInfo;
import optimajet.workflow.core.util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

class Program {
    private static final String SCHEME_CODE = "SimpleWF";
    private static final BufferedReader IN_READER = new BufferedReader(new InputStreamReader(System.in));
    private static UUID processId = null;

    public static void main(String[] args) throws IOException {
        System.out.println("Operation:");
        System.out.println("0 - CreateInstance");
        System.out.println("1 - GetAvailableCommands");
        System.out.println("2 - ExecuteCommand");
        System.out.println("3 - GetAvailableState");
        System.out.println("4 - SetState");
        System.out.println("5 - DeleteProcess");
        System.out.println("9 - Exit");

        System.out.println("The process isn't created.");
        createInstance();

        while (true) {
            try {
                if (run()) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    private static boolean run() throws IOException {
        if (processId != null) {
            System.out.println(String.format("ProcessId = '%s'. CurrentState: %s, CurrentActivity: %s",
                    processId,
                    WorkflowInit.getRuntime().getCurrentStateName(processId),
                    WorkflowInit.getRuntime().getCurrentActivityName(processId)));
        }

        System.out.print("Enter code of operation:");
        String input = IN_READER.readLine();
        char operation = 'x';
        if (!StringUtil.isNullOrEmpty(input)) {
            operation = input.charAt(0);
        }

        switch (operation) {
            case '0':
                createInstance();
                break;
            case '1':
                getAvailableCommands();
                break;
            case '2':
                executeCommand();
                break;
            case '3':
                getAvailableState();
                break;
            case '4':
                setState();
                break;
            case '5':
                deleteProcess();
                break;
            case '9':
                return true;
            default:
                System.out.println("Unknown code. Please, repeat.");
                break;
        }

        System.out.println();
        return false;
    }

    private static void createInstance() {
        processId = UUID.randomUUID();
        try {
            WorkflowInit.getRuntime().createInstance(SCHEME_CODE, processId);
            System.out.println("CreateInstance - OK. " + processId);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(String.format("CreateInstance - Exception: %s", ex.getMessage()));
            processId = null;
        }
    }

    private static void getAvailableCommands() {
        if (processId == null) {
            System.out.println("The process isn't created. Please, create process instance.");
            return;
        }

        Collection<WorkflowCommand> commands = WorkflowInit.getRuntime().getAvailableCommands(processId, StringUtil.EMPTY);

        System.out.println("Available commands:");
        if (commands.isEmpty()) {
            System.out.println("Not found!");
        } else {
            for (WorkflowCommand command : commands) {
                System.out.println(String.format("%s - %s (LocalizedName:%s, Classifier:%s)", command.getCommandName(),
                        command.getIdentities(), command.getLocalizedName(), command.getClassifier()));
            }
        }
    }

    private static void executeCommand() throws IOException {
        if (processId == null) {
            System.out.println("The process isn't created. Please, create process instance.");
            return;
        }

        WorkflowCommand command;

        do {
            getAvailableCommands();
            System.out.print("Enter command:");
            final String commandName = IN_READER.readLine().toLowerCase().trim();
            if (commandName.equals(StringUtil.EMPTY)) {
                return;
            }

            command = CollectionUtil.firstOrDefault(WorkflowInit.getRuntime().getAvailableCommands(processId, StringUtil.EMPTY),
                    new CollectionUtil.ItemCondition<WorkflowCommand>() {
                        @Override
                        public boolean check(WorkflowCommand c) {
                            return c.getCommandName().trim().toLowerCase().equals(commandName);
                        }
                    });
            if (command == null) {
                System.out.println("The command isn't found.");
            }
        } while (command == null);

        WorkflowInit.getRuntime().executeCommand(command, StringUtil.EMPTY, StringUtil.EMPTY);
        System.out.println("ExecuteCommand - OK.");
    }

    private static void getAvailableState() {
        if (processId == null) {
            System.out.println("The process isn't created. Please, create process instance.");
            return;
        }

        Collection<WorkflowState> states = WorkflowInit.getRuntime().getAvailableStateToSet(processId, CultureInfo.getCurrentCulture());
        System.out.println("Available state to set:");

        if (states.isEmpty()) {
            System.out.println("Not found!");
        } else {
            for (WorkflowState state : states) {
                System.out.println(String.format("- %s", state.getName()));
            }
        }
    }

    private static void setState() throws IOException {
        if (processId == null) {
            System.out.println("The process isn't created. Please, create process instance.");
            return;
        }

        WorkflowState state = null;
        do {
            getAvailableState();
            System.out.print("Enter state:");
            String stateName = IN_READER.readLine().toLowerCase().trim();
            if (stateName.equals(StringUtil.EMPTY)) {
                return;
            }

            Collection<WorkflowState> workflowStates = WorkflowInit.getRuntime().getAvailableStateToSet(processId, CultureInfo.getCurrentCulture());
            for (WorkflowState s : workflowStates) {
                if (s.getName().trim().toLowerCase().equals(stateName)) {
                    state = s;
                    break;
                }
            }

            if (state == null) {
                System.out.println("The state isn't found.");
            } else {
                break;
            }
        } while (true);

        WorkflowInit.getRuntime().setState(processId, StringUtil.EMPTY, StringUtil.EMPTY, state.getName(), new HashMap<String, Object>());
        System.out.println("SetState - OK. " + processId);
    }

    private static void deleteProcess() {
        if (processId == null) {
            System.out.println("The process isn't created. Please, create process instance.");
            return;
        }

        WorkflowInit.getRuntime().getPersistenceProvider().deleteProcess(processId);
        System.out.println("DeleteProcess - OK. " + processId);
        processId = null;
    }
}