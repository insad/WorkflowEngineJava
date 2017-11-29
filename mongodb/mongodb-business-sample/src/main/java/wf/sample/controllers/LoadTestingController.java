package wf.sample.controllers;

import business.helpers.DocumentHelper;
import business.helpers.EmployeeHelper;
import business.models.Document;
import business.models.Employee;
import business.models.WorkflowInbox;
import business.persistence.PersistenceHelper;
import business.workflow.WorkflowInit;
import optimajet.workflow.core.fault.CommandNotValidForStateException;
import optimajet.workflow.core.fault.ImpossibleToSetStatusException;
import optimajet.workflow.core.runtime.WorkflowCommand;
import optimajet.workflow.core.util.CollectionUtil;
import optimajet.workflow.core.util.UUIDUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import wf.sample.models.LoadTestingOperationModel;
import wf.sample.models.LoadTestingStatisticItemModel;
import wf.sample.models.LoadTestingStatisticsModel;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class LoadTestingController {

    private static void documentCreateWorkflow(UUID id) {
        Date opStart = new Date();
        WorkflowInit.getRuntime().createInstance("SimpleWF", id);
        addOperation(opStart, new Date(), "CreatingWorkflow");
    }

    private static void addOperation(Date opStart, Date opEnd, String type) {
        long duration = opEnd.getTime() - opStart.getTime();

        LoadTestingOperationModel loadTestingOperationModel = new LoadTestingOperationModel();
        loadTestingOperationModel.setId(UUID.randomUUID());
        loadTestingOperationModel.setDate(opStart);
        loadTestingOperationModel.setType(type);
        loadTestingOperationModel.setDurationMilliseconds(duration);
        PersistenceHelper.save(loadTestingOperationModel);
    }

    @GetMapping("LoadTesting")
    public String index(@RequestParam(value = "GraphUnit", required = false, defaultValue = "60") int graphUnit,
                        Map<String, Object> model) {
        model.put("statistics", getStatistics(graphUnit));
        return "load-testing/index";
    }

    @PostMapping("LoadTesting/Run")
    @ResponseBody
    public String run(int doccount, int threadcount, int wfcommandcount, int wfthreadcount) throws InterruptedException {
        for (int i = 0; i < threadcount; i++) {
            Thread myThread = new Thread(new DocCreate(doccount));
            myThread.start();
        }

        Thread.sleep(1000);

        for (int i = 0; i < wfthreadcount; i++) {
            Thread myThread = new Thread(new WFCommandExecute(wfcommandcount));
            myThread.start();
        }

        return "Starting success!";
    }

    @PostMapping("LoadTesting/Clean")
    @ResponseBody
    public String clean() {
        PersistenceHelper.clean(LoadTestingOperationModel.class);
        return "";
    }

    private List<LoadTestingStatisticsModel> getStatistics(int spanInSeconds) {
        List<LoadTestingStatisticsModel> result = new ArrayList<>();

        List<LoadTestingOperationModel> loadTestingOperationModels = PersistenceHelper.asList(LoadTestingOperationModel.class);
        for (final LoadTestingOperationModel op : loadTestingOperationModels) {
            op.setDate(floor(op.getDate(), spanInSeconds * 1000));
            LoadTestingStatisticsModel r = CollectionUtil.firstOrDefault(result,
                    new CollectionUtil.ItemCondition<LoadTestingStatisticsModel>() {
                        @Override
                        public boolean check(LoadTestingStatisticsModel c) {
                            return c.getDate().equals(op.getDate());
                        }
                    });
            if (r == null) {
                r = new LoadTestingStatisticsModel(op.getDate());
                result.add(r);
            }

            LoadTestingStatisticItemModel item = CollectionUtil.firstOrDefault(r.getItems(),
                    new CollectionUtil.ItemCondition<LoadTestingStatisticItemModel>() {
                        @Override
                        public boolean check(LoadTestingStatisticItemModel c) {
                            return c.getType().equals(op.getType());
                        }
                    });
            if (item == null) {
                item = new LoadTestingStatisticItemModel(op.getType());
                r.getItems().add(item);
            }

            item.setDuration(item.getDuration() + op.getDurationMilliseconds());
            item.checkDurationMinMax(op.getDurationMilliseconds());
            item.setCount(item.getCount() + 1);
        }

        return result;
    }

    private Date floor(Date date, int millisSpan) {
        long ticks = date.getTime() / millisSpan;
        return new Date(ticks * millisSpan);
    }

    private static final class DocCreate implements Runnable {
        private final int docCount;

        private DocCreate(int docCount) {
            this.docCount = docCount;
        }

        @Override
        public void run() {
            List<Employee> employees = EmployeeHelper.getAll();
            Random r = new Random();

            for (int i = 0; i < docCount; i++) {
                UUID id = createDocument(employees, r);
                documentCreateWorkflow(id);
            }
        }

        private UUID createDocument(List<Employee> employees, Random r) {
            UUID id = UUID.randomUUID();

            Date opStart = new Date();

            Employee author = employees.get(r.nextInt(employees.size()));
            Employee controller = employees.get(r.nextInt(employees.size()));

            Document doc = new Document();
            doc.setId(id);
            doc.setAuthorId(author.getId());
            doc.setAuthorName(author.getName());
            doc.setStateName("Draft");
            doc.setNumber(DocumentHelper.getNextNumber());
            doc.setName("AG_Doc " + doc.getNumber());

            if (r.nextBoolean()) {
                doc.setEmployeeControllerId(controller.getId());
                doc.setEmployeeControllerName(controller.getName());
            }
            String shortTime = new SimpleDateFormat("HH:mm").format(new Date());
            doc.setComment(String.format("Auto-generated document. %s.", shortTime));
            doc.setSum("" + (r.nextInt(10000) + 1));

            PersistenceHelper.save(doc);
            addOperation(opStart, new Date(), "CreatingDocument");

            return id;
        }
    }

    private static final class WFCommandExecute implements Runnable {
        private final int wfCommandCount;

        private WFCommandExecute(int wfCommandCount) {
            this.wfCommandCount = wfCommandCount;
        }

        @Override
        public void run() {
            List<Employee> employees = EmployeeHelper.getAll();
            Random r = new Random();

            for (int i = 0; i < wfCommandCount; ) {
                int oldI = i;
                for (int k = 0; k < employees.size() - 1; k++) {
                    Employee employee = employees.get(k);
                    UUID docId = null;

                    int inboxCount = PersistenceHelper.getWorkflowInboxCountForEmployee(employee);
                    if (inboxCount > 0) {
                        WorkflowInbox tmp = PersistenceHelper.getRandomWorkflowInbox(employee, r.nextInt(inboxCount));

                        if (tmp != null) {
                            docId = tmp.getProcessId();
                        }

                        if (docId != null) {
                            Date opStart = new Date();
                            List<WorkflowCommand> commands = new ArrayList<>(WorkflowInit.getRuntime()
                                    .getAvailableCommands(docId, UUIDUtil.asString(employee.getId())));
                            addOperation(opStart, new Date(), "GetAvailableCommands");

                            if (!commands.isEmpty()) {
                                WorkflowCommand c = commands.get(r.nextInt(commands.size()));
                                c.setParameter("Comment", "Load testing. ExecuteCommand");

                                opStart = new Date();

                                try {
                                    String userId = UUIDUtil.asString(employee.getId());
                                    WorkflowInit.getRuntime().executeCommand(c, userId, userId);
                                } catch (CommandNotValidForStateException | ImpossibleToSetStatusException ignored) {
                                    // If process is changed state then ignore it's
                                    // If process is Running then ignore it's
                                    continue;
                                }

                                addOperation(opStart, new Date(), "ExecuteCommand");
                                i++;
                                break;
                            }
                        }
                    }
                }

                if (oldI == i) {
                    break;
                }
            }
        }
    }
}
