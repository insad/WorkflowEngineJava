package wf.sample.controllers;

import business.helpers.EmployeeHelper;
import business.models.Employee;
import business.persistence.ApplicationContextProvider;
import business.repository.LoadTestingOperationModelRepository;
import lombok.extern.slf4j.Slf4j;
import optimajet.workflow.core.util.CollectionUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import wf.sample.loadtest.DocCreate;
import wf.sample.loadtest.WFCommandExecute;
import wf.sample.models.LoadTestingOperationModel;
import wf.sample.models.LoadTestingStatisticItemModel;
import wf.sample.models.LoadTestingStatisticsModel;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@Transactional
@Slf4j
public class LoadTestingController {

    private final LoadTestingOperationModelRepository loadTestingOperationModelRepository;

    public LoadTestingController(LoadTestingOperationModelRepository loadTestingOperationModelRepository) {
        this.loadTestingOperationModelRepository = loadTestingOperationModelRepository;
    }

    private static Date floor(Date date, int millisSpan) {
        long ticks = date.getTime() / millisSpan;
        return new Date(ticks * millisSpan);
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
            DocCreate myThread = ApplicationContextProvider.getBean(DocCreate.class);
            myThread.setDocCount(doccount);
            myThread.setName("LoadTestCreate-" + i);
            myThread.start();
        }

        Thread.sleep(1000);

        List<Employee> employees = EmployeeHelper.getEmployeeCache();
        for (int i = 0; i < wfthreadcount; i++) {
            WFCommandExecute myThread = ApplicationContextProvider.getBean(WFCommandExecute.class);
            myThread.setWfCommandCount(wfcommandcount);
            myThread.setEmployees(employees);
            myThread.setName("LoadTestCommand-" + i);
            myThread.start();
        }

        return "Starting success!";
    }

    @PostMapping("LoadTesting/Clean")
    @ResponseBody
    public String clean() {
        loadTestingOperationModelRepository.deleteAll();
        return "";
    }

    private List<LoadTestingStatisticsModel> getStatistics(int spanInSeconds) {
        List<LoadTestingStatisticsModel> result = new ArrayList<>();

        Iterable<LoadTestingOperationModel> loadTestingOperationModels = loadTestingOperationModelRepository.findAll();
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
}
